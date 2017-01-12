package org.openpaas.ieda.web.config.systemRelease.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.dto.SessionInfoDTO;
import org.openpaas.ieda.web.common.service.CommonService;
import org.openpaas.ieda.web.common.service.CommonUtils;
import org.openpaas.ieda.web.config.systemRelease.dao.ReleaseManagementDAO;
import org.openpaas.ieda.web.config.systemRelease.dao.ReleaseManagementVO;
import org.openpaas.ieda.web.config.systemRelease.dto.ReleaseManagementDTO;
import org.openpaas.ieda.web.management.code.dao.CommonCodeDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ReleaseManagementService {
	
	@Autowired private ReleaseManagementDAO dao;
	@Autowired private CommonCodeDAO commonCodeDao;
	@Autowired private CommonService commonService;
	
	final private static String TMPDIRECTORY = LocalDirectoryConfiguration.getTmpDir();
	final private static String RELEASEDIRECTORY = LocalDirectoryConfiguration.getReleaseDir() + System.getProperty("file.separator");
	private final static Logger LOGGER = LoggerFactory.getLogger(ReleaseManagementService.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   :  시스템 릴리즈 정보 목록 조회
	 * @title               : getSystemReleaseList
	 * @return            : List<ReleaseManagementVO>
	***************************************************/
	public List<ReleaseManagementVO> getSystemReleaseList(){
		//db release datas
		List<ReleaseManagementVO> releaseList = dao.selectSystemReleaseList();
		if( releaseList != null ){
			for( ReleaseManagementVO release : releaseList ){
				if( release.getDownloadStatus() != null ){
					if( release.getDownloadStatus().equals("DOWNLOADED") ){
						File releaseFile = new File(LocalDirectoryConfiguration.getReleaseDir()+System.getProperty("file.separator")+release.getReleaseFileName());
						if(!releaseFile.exists() || releaseFile.length() == 0){
							ReleaseManagementDTO.Delete dto = new ReleaseManagementDTO.Delete();
							dto.setId( String.valueOf(release.getId()) );
							dto.setReleaseFileName(release.getReleaseFileName());
							dao.deleteSystemRelase(dto);
						}
					}
				}
			}
		}
		return dao.selectSystemReleaseList();
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 로컬에 저장된 릴리즈 목록 조회 
	 * @title               : getLocalReleaseList
	 * @return            : List<String>
	***************************************************/
	public List<String> getLocalReleaseList() {
		
		//1.파일객체 생성
		File dir = new File(LocalDirectoryConfiguration.getReleaseDir());
		//2.폴더가 가진 파일객체를 리스트로 받는다.
		File[] localFiles = dir.listFiles();
		List<String> localReleases = new ArrayList<>();
		if( localFiles != null ){
			for (File file : localFiles) {
				localReleases.add(file.getName());
			}
		}
		return localReleases;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 공통 릴리즈 유형 콤보 조회 
	 * @title               : getSystemReleaseTypeList
	 * @return            : List<String>
	***************************************************/
	public List<String> getSystemReleaseTypeList(){
		return commonCodeDao.selectReleaseTypeList("RELEASE_TYPE");
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 공통 릴리즈 콤보
	 * @title               : getLocalReleaseList
	 * @return            : List<String>
	***************************************************/
	public List<String> getLocalReleaseList(String type, String iaas){
		return dao.selectLocalReleaseList(type, iaas);
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 로컬 파일 업로드에 대한 시스템 릴리즈 정보 저장
	 * @title               : registSystemReleaseUploadInfo
	 * @return            : ReleaseManagementVO
	***************************************************/
	public ReleaseManagementVO registSystemReleaseUploadInfo(ReleaseManagementDTO.Regist dto,  String testFlag){
		String fileName = dto.getReleaseFileName();
		String status = "";
		ReleaseManagementVO vo = null;
		if( fileName.indexOf(".tgz") < 0 && fileName.indexOf(".zip") < 0 ){
			status = "error";
			deleteLockFile(status, fileName);
			throw new CommonException("preconditionFailed.systemRelease.exception",
					"잘못된 확장자를 갖은 릴리즈 파일 입니다.", HttpStatus.PRECONDITION_FAILED);
		}
		if(StringUtils.isEmpty(dto.getReleaseFileName())){
			status = "error";
			deleteLockFile(status, fileName);
			throw new CommonException("notfound.systemRelease.exception",
					"해당하는 릴리즈 파일이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		if(Long.parseLong(dto.getReleaseSize()) < 1 ){
			status = "error";
			deleteLockFile(status, fileName);
			throw new CommonException("notfound.systemRelease.exception",
					"해당하는 릴리즈 파일이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		//release path
		File releseFile = new File(RELEASEDIRECTORY + dto.getReleaseFileName());
		
		//릴리즈 파일이 존재하고 덮어쓰기 체크가 안되어 있을 경우
		if(releseFile.exists() && "false".equals(dto.getOverlayCheck())) {
			status = "conflict";
			deleteLockFile(status, fileName);
			throw new CommonException("existReleaseFile.systemRelease.exception",
					dto.getReleaseFileName() + "의 릴리즈 파일은 이미 존재합니다.", HttpStatus.CONFLICT);
		}else{
			status = "done";
			// 릴리즈 사이즈 format
			long releaseSize = Long.parseLong(dto.getReleaseSize());
			dto.setReleaseSize(CommonUtils.formatSizeUnit(releaseSize));
			dto.setDownloadStatus("DOWNLOADING");
			vo = saveSystemRelease(dto, testFlag);
		}
		return vo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Exception 발생 시 lock 파일 삭제 
	 * @title               : deleteLockFile
	 * @return            : Boolean
	***************************************************/
	public Boolean deleteLockFile(String status,String  fileName){
		Boolean flag = false;
		if( status.equals("error")){
			//lock file delete
			if( !StringUtils.isEmpty(fileName) ){
				int index = fileName.lastIndexOf(".");
				String lockFileName = fileName.substring(0, index);
				File lockFile = new File(LocalDirectoryConfiguration.getLockDir()+ System.getProperty("file.separator")+lockFileName+"-download.lock");
				if(lockFile.exists()){
					flag = lockFile.delete();
				}
			}
		}
		return flag;
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : URL을 통한 릴리즈 다운로드 정보 저장
	 * @title               : registSystemReleaseDownloadInfo
	 * @return            : ReleaseManagementVO
	***************************************************/
	public ReleaseManagementVO registSystemReleaseDownloadInfo(ReleaseManagementDTO.Regist dto,  String testFlag){
		
		if(StringUtils.isEmpty(dto.getReleaseFileName())&& dto.getReleaseFileName() == null){
			throw new CommonException("notfound.systemRelease.exception",
					"릴리즈 파일 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		ReleaseManagementVO vo = null;
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		String releaseFileName = null;
		String[] search = null;
		String info = null;
		Process process = null;
		String status = "";
		String releaseSize = "";
		StringBuffer accumulatedBuffer = new StringBuffer();
		boolean flag = false;
		
		try{
			//wget 실행
			ProcessBuilder builder = new ProcessBuilder("wget", "--spider", "-d", "-P", TMPDIRECTORY, "--content-disposition", dto.getReleaseFileName());
			builder.redirectErrorStream(true);
			process = builder.start();
			inputStream = process.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			while ((info = bufferedReader.readLine()) != null){ 
				accumulatedBuffer.append(info + "\n");
				//파일명
				if(info.contains("Content-Disposition:") && !flag){
					search = info.split("filename=");
					releaseFileName = search[search.length-1];
					String fileType = releaseFileName.substring(releaseFileName.lastIndexOf(".") + 1, releaseFileName.length());
					if(!fileType.toLowerCase().equals("tgz") || fileType.toLowerCase().equals("zip")){
						throw new CommonException("preconditionFailed.systemRelease.exception", "잘 못된 확장자를 갖은 릴리즈 파일 입니다.", HttpStatus.PRECONDITION_FAILED);
					}
					flag = true;
				}
				if(info.contains("Content-Length:")){
					search = info.split(" ");
					releaseSize = search[search.length-1];
				}
			}
		}catch(UnsupportedEncodingException e){
			throw new CommonException("unsupportedEncoding.systemRelease.exception", releaseFileName + "의 릴리즈 파일의 인코딩을 지원할 수 없습니다.", HttpStatus.CONFLICT);
		}catch( IOException e){
			throw new CommonException("existReleaseFile.systemRelease.exception", releaseFileName + "의 릴리즈 파일은 이미 존재합니다.", HttpStatus.CONFLICT);
		}catch( NullPointerException e){
			throw new CommonException("nullpoint.systemRelease.exception", releaseFileName + "의 릴리즈 파일 다운로드 중 오류가 발생하였습니다..", HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			if(bufferedReader != null)
				try {
					bufferedReader.close();
				} catch (IOException e) {
					if( LOGGER.isErrorEnabled() ){
						LOGGER.error( e.getMessage() );
					}
				}
		}
		
		String accumulatedLog = accumulatedBuffer.toString();
		if(accumulatedLog.contains("Internal Server Error")){
			status ="badRequestURL";
			throw new CommonException("BadRequestURL.systemRelease.exception", "릴리즈 URL이 잘못되었습니다. 확인해주세요", HttpStatus.BAD_REQUEST);
		}
		
		if(releaseFileName == null || releaseFileName.isEmpty()){
			status ="notfound";
			throw new CommonException("notfoundReleaseFile.systemRelease.exception", "해당하는 릴리즈 파일이 존재하지 않습니다. 확인해주세요.", HttpStatus.NOT_FOUND);
		}
		
		//create lock file 
		int index = releaseFileName.lastIndexOf(".");
		String lockFile = releaseFileName.substring(0, index);
		Boolean checkLock = commonService.lockFileSet(lockFile +"-download");
		if( !checkLock ){
			throw new CommonException("conflict.systemRelease.exception", 
					"현재 다른 플랫폼 설치 관리자가 동일한 릴리즈를 등록 중 입니다.", HttpStatus.CONFLICT);
		}
		
		//release path
		File releseFile = new File(RELEASEDIRECTORY + releaseFileName);
		
		//릴리즈 파일이 존재하고 덮어쓰기 체크가 안되어 있을 경우
		if(releseFile.exists() && "false".equals(dto.getOverlayCheck())) {
			status ="error";
			deleteLockFile(status, releaseFileName); //lock 파일 삭제
			throw new CommonException("existReleaseFile.systemRelease.exception", 
					releaseFileName + "의 릴리즈 파일은 이미 존재합니다.", HttpStatus.CONFLICT);
		}else{
			// 릴리즈 사이즈 format
			status ="done";
			dto.setReleaseSize(CommonUtils.formatSizeUnit(Long.parseLong(releaseSize)));
			dto.setReleaseFileName(releaseFileName);
			dto.setDownloadStatus("DOWNLOADING");
			vo = saveSystemRelease(dto, testFlag);
		}
		return vo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 릴리즈 업로드 공통 정보 저장
	 * @title               : saveSystemRelease
	 * @return            : ReleaseManagementVO
	***************************************************/
	public ReleaseManagementVO saveSystemRelease(ReleaseManagementDTO.Regist dto,  String testFlag){
		
		ReleaseManagementVO vo = null;
		ReleaseManagementVO fileCheckResult = null;
		try{
			SessionInfoDTO sessionInfo = new SessionInfoDTO();
			//중복
			fileCheckResult = dao.selectSystemRelease(dto.getReleaseFileName());
			if( fileCheckResult == null || "Y".equals(testFlag)){
				dto.setCreateUserId(sessionInfo.getUserId());
				dto.setUpdateUserId(sessionInfo.getUserId());
				dao.insertSystemRelease(dto);
			} else{
				dto.setUpdateUserId(sessionInfo.getUserId());
				dto.setId(fileCheckResult.getId());
				dao.updateSystemRelease(dto);
			}
			vo = dao.selectSystemReleaseById(dto.getId());
			
		} catch(NullPointerException e){
			throw new CommonException("NullPointerException.systemRelease.exception",
					"시스템 릴리즈 정보 저장에 실패하였습니다. 확인해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch(Exception e){
			throw new CommonException("Exception.systemRelease.exception",
					"시스템 릴리즈 정보 저장에 실패하였습니다. 확인해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return vo;
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 시스템 릴리즈 삭제
	 * @title               : deleteSystemRelease
	 * @return            : Boolean
	***************************************************/
	public Boolean deleteSystemRelease(ReleaseManagementDTO.Delete dto) throws SQLException{
		Boolean check = false;
		File file = new File(RELEASEDIRECTORY +  dto.getReleaseFileName());
		dao.deleteSystemRelase(dto);
		//delete lock file
		int index = dto.getReleaseFileName().indexOf(".tgz");
		String lockFileName = dto.getReleaseFileName().substring(0, index) + "-download.lock";
		File lcokFile = new File(LocalDirectoryConfiguration.getLockDir() + lockFileName );
		if(  lcokFile.exists() ) check = lcokFile.delete();
		//delete release File
		if(file.exists()){ 
			boolean delete = file.delete(); 
			if(!delete){
				throw new CommonException("delete.systemRelease.exception", "시스템 릴리즈 삭제에 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}else{
			throw new CommonException("notfound.systemRelease.exception", "해당하는 릴리즈 파일이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		} 
		
		return check;
	}
	
}
