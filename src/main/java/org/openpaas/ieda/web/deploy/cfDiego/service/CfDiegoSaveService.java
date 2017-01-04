package org.openpaas.ieda.web.deploy.cfDiego.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.common.dto.SessionInfoDTO;
import org.openpaas.ieda.web.deploy.cf.dao.CfDAO;
import org.openpaas.ieda.web.deploy.cf.dao.CfVO;
import org.openpaas.ieda.web.deploy.cf.dto.CfParamDTO;
import org.openpaas.ieda.web.deploy.cf.service.CfSaveService;
import org.openpaas.ieda.web.deploy.cfDiego.dao.CfDiegoDAO;
import org.openpaas.ieda.web.deploy.cfDiego.dao.CfDiegoVO;
import org.openpaas.ieda.web.deploy.cfDiego.dto.CfDiegoParamDTO;
import org.openpaas.ieda.web.deploy.common.dto.network.NetworkDTO;
import org.openpaas.ieda.web.deploy.common.dto.resource.ResourceDTO;
import org.openpaas.ieda.web.deploy.diego.dao.DiegoVO;
import org.openpaas.ieda.web.deploy.diego.dto.DiegoParamDTO;
import org.openpaas.ieda.web.deploy.diego.service.DiegoSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Service
public class CfDiegoSaveService {
	
	@Autowired CfDiegoDAO cfDiegoDao;
	@Autowired CfSaveService cfSaveService;
	@Autowired DiegoSaveService diegoSaveService;
	@Autowired CfDAO cfDao;
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   :  CF 및 Diego 기본 정보 저장
	 * @title               : saveDefaultInfo
	 * @return            : CfDiegoVO
	***************************************************/
	@Transactional
	public CfDiegoVO saveDefaultInfo( CfDiegoParamDTO.Default dto, String test ) {
		CfDiegoVO vo = null;
		CfVO cfVo = null;
		DiegoVO diegoVo = null;
		CfParamDTO.Default cfDto = null;
		DiegoParamDTO.Default diegoDto = null;
		ObjectMapper mapper = new ObjectMapper();
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		try {
			String cfJson = new Gson().toJson(dto); 
			if( "cf".equals(dto.getPlatform()) ){ //cf update/insert
				cfDto = mapper.readValue(cfJson, CfParamDTO.Default.class);
				cfVo = cfSaveService.saveDefaultInfo(cfDto, test);
				vo = cfDiegoDao.selectCfDiegoInfoByPlaform( dto.getPlatform(), cfVo.getId());
			}else{ //diego update/insert
				diegoDto = mapper.readValue(cfJson, DiegoParamDTO.Default.class);
				String keyFileName = cfDao.selectCfInfoById(diegoDto.getCfId()).getKeyFile();
				diegoDto.setKeyFile(keyFileName);
				diegoVo = diegoSaveService.saveDefaultInfo(diegoDto, test);
				vo = cfDiegoDao.selectCfDiegoInfoByPlaform( "cf", diegoVo.getCfId());
				if ( vo != null ){
					dto.setId( String.valueOf( diegoVo.getId()) );	
				}
			}
			if( (dto.getId() == null || StringUtils.isEmpty(dto.getId()))
					|| ( "cf".equals(dto.getPlatform()) && "Y".equals(test)) ){ 
				//1.1 insert cf & diego
				if( cfVo != null || diegoVo != null ){
					//1.1.2 insert cfDiego
					vo = new CfDiegoVO();
					if( "Y".equals(test) ) vo.setId( Integer.parseInt(dto.getId()) );
					vo.setCreateUserId(sessionInfo.getUserId());
					vo.setUpdateUserId(sessionInfo.getUserId());
					vo.setIaasType(dto.getIaas());
					vo.getCfVo().setId(cfVo.getId() != null ? cfVo.getId() : 0);
					vo.getDiegoVo().setId( diegoVo != null ? diegoVo.getId() : 0 );
					cfDiegoDao.insertCfDiegoInfo(vo);
				}
			} else{
				//1.2 id != null => cfDiego update
				if( "diego".equals(dto.getPlatform()) ){
					if ( "Y".equals(test) ||  vo.getDiegoVo().getId() == null ) {
						vo.getDiegoVo().setId( Integer.parseInt(dto.getId()) );
					}
				}
				vo.setUpdateUserId(sessionInfo.getUserId());
				cfDiegoDao.updateCfDiegoInfo(vo);
			}
		} catch (IOException e) {
			throw new CommonException("notfound.cfDiego.exception",
					dto.getPlatform().toUpperCase() + " 정보를 읽어을 수 없습니다.", HttpStatus.NOT_FOUND);
		} catch (NullPointerException e){
			throw new CommonException("notfound.cfDiego.exception",
					"CF & Diego 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
		}
		return vo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 네트워크 정보 저장
	 * @title               : saveNetworkInfo
	 * @return            : void
	***************************************************/
	public void saveNetworkInfo(List<NetworkDTO> dto ) throws SQLException{
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		CfDiegoVO vo  = null;
		try{
			if( dto.size() > 0 ) {
				if( dto.get(0).getCfId() != null ){ //save cf network info 
					cfSaveService.saveNetworkInfo(dto);
					vo = cfDiegoDao.selectCfDiegoInfoByPlaform("cf", Integer.parseInt(dto.get(0).getCfId()));
				}else if( dto.get(0).getDiegoId() != null ){ //save diego network info
					diegoSaveService.saveNetworkInfo(dto);
					vo = cfDiegoDao.selectCfDiegoInfoByPlaform("diego", Integer.parseInt(dto.get(0).getDiegoId()));
				}else{
					throw new CommonException("notfound.cfDiego.exception",
							"CF 또는 Diego 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
				}
			}
			if( vo != null  ){
				vo.setUpdateUserId(sessionInfo.getUserId());
			}
			cfDiegoDao.updateCfDiegoInfo(vo);
		} catch(NullPointerException e){
			throw new CommonException("nullPoint.cfDiego.exception",
					"네트워크 정보를 저장할 수 없습니다. ", HttpStatus.INTERNAL_SERVER_ERROR);
		} 
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 리소스 정보 저장 및 배포 파일명 설정 
	 * @title               : saveResourceInfo
	 * @return            : Map<String,Object>
	***************************************************/
	public Map<String, Object> saveResourceInfo(ResourceDTO dto, String test){
		Map<String, Object> map  = null;
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		//1.1 cf resource
		if( "cf".equals(dto.getPlatform()) ){
			map = cfSaveService.saveResourceInfo(dto, test);
		} else {
			map = diegoSaveService.saveResourceInfo(dto, test);
		}
		CfDiegoVO result = cfDiegoDao.selectCfDiegoInfoByPlaform( dto.getPlatform(), Integer.parseInt(map.get("id").toString()) );
		result.setUpdateUserId(sessionInfo.getUserId());
		cfDiegoDao.updateCfDiegoInfo(result);
		
		return map;

	} 
}