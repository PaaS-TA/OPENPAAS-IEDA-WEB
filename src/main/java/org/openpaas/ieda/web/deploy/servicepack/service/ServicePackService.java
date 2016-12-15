package org.openpaas.ieda.web.deploy.servicepack.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.dto.SessionInfoDTO;
import org.openpaas.ieda.web.deploy.servicepack.dao.ServicePackDAO;
import org.openpaas.ieda.web.deploy.servicepack.dao.ServicePackVO;
import org.openpaas.ieda.web.deploy.servicepack.dto.ServicePackParamDTO;
import org.openpaas.ieda.web.information.manifest.dao.ManifestDAO;
import org.openpaas.ieda.web.information.manifest.dao.ManifestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ServicePackService {
	@Autowired ServicePackDAO dao;
	@Autowired ManifestDAO manifestDao;
	
	final private static String SEPARATOR = System.getProperty("file.separator");
	final private static String MANIFEST_DIRECTORY 	= LocalDirectoryConfiguration.getManifastDir() + SEPARATOR;
	final private static Logger LOGGER = LoggerFactory.getLogger(ServicePackService.class);

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 전체 목록 조회
	 * @title               : getServicePackList
	 * @return            : List<ServicePackVO>
	***************************************************/
	public List<ServicePackVO> getServicePackList(String iaas) {
		String iassLower =  iaas.toUpperCase();
		List<ServicePackVO> vo = dao.selectServicePackInfo(iassLower);
		return vo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 정보 저장
	 * @title               : saveServicePackInfo
	 * @return            : ServicePackVO
	***************************************************/
	@Transactional
	public ServicePackVO saveServicePackInfo(ServicePackParamDTO dto, String test) {
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		ServicePackVO vo = null;
		vo = dao.selectServicePackDetailInfo(dto.getId());
		ManifestVO manifestVo =  manifestDao.selectManifestInfoByDeployName(dto.getDeploymentName());
		try{
			if(dto.getId()==0 ||  "Y".equals(test)) {
				vo = new ServicePackVO();
				if("Y".equals(test))vo.setId(dto.getId());
				vo.setCreateUserId(sessionInfo.getUserId());
				vo.setUpdateUserId(sessionInfo.getUserId());
				vo.setDeploymentFile(dto.getDeploymentFile());
				vo.setDeploymentName(dto.getDeploymentName());
				vo.setIaas(dto.getIaas());
				dao.insertServicePackInfo(vo);
				if(manifestVo != null){
					manifestVo.setDeployStatus("processing");
					manifestDao.updateManifestInfo(manifestVo);
				}
			}else{
				vo.setDeploymentFile(manifestVo.getFileName());
				vo.setDeploymentName(dto.getDeploymentName());
				dao.updateServicePackInfo(vo);
			}
		} catch(DuplicateKeyException e){
			throw new CommonException("duplicateKey.servicepack.exception",
					" 동일한 Manifest 파일을 사용하고 있습니다.", HttpStatus.CONFLICT);
		} catch (Exception e) {
			throw new CommonException("sql.servicepack.exception",
					" 서비스팩 정보 저장 중 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return vo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 입력 정보를 바탕으로 manifest 파일 생성 및 배포 파일명 응답  
	 * @title               : makeDeploymentFile
	 * @return            : void
	***************************************************/
	public void makeDeploymentFile(int id, String testFlag) {
		String settingDeploymentFileName = "";
		String settingDeploymentName = "";
		StringBuffer content = new StringBuffer();
		String manifestFilePath  = null;
		InputStream InputStream = null;
		BufferedReader bufferedReader = null;
		ServicePackVO vo  = dao.selectServicePackDetailInfo(id);
		
		if(vo == null){
			throw new CommonException("notFound.manifest.exception", "해당 서비스팩 정보가 조회되지 않습니다.", HttpStatus.NOT_FOUND);
		}
		ManifestVO manifestvo = manifestDao.selectManifestInfoByDeployName(vo.getDeploymentName());
		//서비스팩 배포 명 셋팅
		settingDeploymentName = vo.getDeploymentName();
		//서비스팩 배포 파일 명 셋팅
		settingDeploymentFileName = manifestvo.getFileName();
		//ManifestFile 경로 셋팅
		manifestFilePath =  MANIFEST_DIRECTORY+vo.getDeploymentFile();
		
		//셋팅 된 배포 명, 파일 명으로 테이블 업데이트
		vo.setDeploymentFile(settingDeploymentFileName);
		vo.setDeploymentName(settingDeploymentName);
		dao.updateServicePackInfo(vo);
		
		try {
			//Manifestl temp file 생성
			File settingFile = new File(manifestFilePath);
			InputStream = new FileInputStream(settingFile);
			bufferedReader = new BufferedReader(new InputStreamReader(InputStream,"UTF-8"));
			String info = null;
			while ((info = bufferedReader.readLine()) != null) {
				content.append(info + "\n");
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("content :" + "\n" + content.toString());
			}
			IOUtils.write(content.toString(), new FileOutputStream(LocalDirectoryConfiguration.getDeploymentDir() + SEPARATOR + vo.getDeploymentFile()), "UTF-8");
		} catch (IOException e) {
			if( LOGGER.isErrorEnabled() ){
				LOGGER.error( e.getMessage() );  
			}
			throw new CommonException("notFound.manifest.exception",
					"서비스팩 Manifest 파일을 읽어오는 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}  finally {
			if(bufferedReader!=null)
				try {
					bufferedReader.close();
				} catch (IOException e) {
					if( LOGGER.isErrorEnabled() ){
						LOGGER.error( e.getMessage() );  
					}
				}
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 단순 레코드 삭제
	 * @title               : deleteServicePackInfoRecord
	 * @return            : void
	***************************************************/
	@Transactional
	public void deleteServicePackInfoRecord(ServicePackParamDTO dto) throws SQLException{
			ServicePackVO vo  = dao.selectServicePackDetailInfo(dto.getId());	
			if(vo==null){
				throw new CommonException("notFound.manifest.exception",
						"해당 서비스 팩 정보가 존재하지 않습니다. 확인해주세요", HttpStatus.NOT_FOUND);
			}
			
			ManifestVO manifestvo = manifestDao.selectManifestInfoByDeployName(vo.getDeploymentName());
			dao.deleteServicePackInfoRecord(dto.getId());
			if(manifestvo != null){
				manifestvo.setDeployStatus(null);
				manifestDao.updateManifestInfo(manifestvo);
			}
	}
}
