package org.openpaas.ieda.web.config.systemRelease.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Iterator;

import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.dto.SessionInfoDTO;
import org.openpaas.ieda.web.config.systemRelease.dao.ReleaseManagementDAO;
import org.openpaas.ieda.web.config.systemRelease.dto.ReleaseManagementDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Service
public class ReleaseManagementUploadService {
	
	@Autowired private ReleaseManagementDAO dao;
	
	private final static String SEPARATOR = System.getProperty("file.separator");
	private final static Logger LOGGER = LoggerFactory.getLogger(ReleaseManagementUploadService.class);

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 시스템 릴리즈 파일 업로드
	 * @title               : uploadReleaseFile
	 * @return            : void
	***************************************************/
	public void uploadReleaseFile(MultipartHttpServletRequest request){
		File isKeyFile = null;
		BufferedOutputStream stream = null;
		InputStream instream = null;
		Iterator<String> itr =  request.getFileNames();
		
		if(itr.hasNext()) {
			MultipartFile mpf = request.getFile(itr.next());
			try {
				byte[] tmp = new byte[8192];
				
				String releaseFilePath = LocalDirectoryConfiguration.getReleaseDir() + SEPARATOR + mpf.getOriginalFilename();
				isKeyFile = new File(releaseFilePath);
				if(isKeyFile.exists() && "true".equals(request.getParameter("overlay"))){
					//1. 파일이 존재하고 덮어쓰기가 체크 되어있을 때
					boolean delete = isKeyFile.delete();//삭제
					if(!delete){
						throw new CommonException("dbdeleteRelease.systemRelease.exception",
								"기존 파일 삭제 실패", HttpStatus.INTERNAL_SERVER_ERROR);

					}
				}else if(isKeyFile.exists() && "false".equals(request.getParameter("overlay"))){
					//2. 파일이 존재하지만 덮어쓰기가 체크 되어있지 않을 때 
					throw new CommonException("existReleaseFile.systemRelease.exception",
							"이미 동일한 릴리즈 파일이 존재합니다. 확인해주세요", HttpStatus.CONFLICT);
				}
				int i =0;
				stream = 	new BufferedOutputStream(new FileOutputStream(isKeyFile));
				instream = mpf.getInputStream();
				 while ((i = instream.read(tmp)) >= 0) {
					 stream.write(tmp, 0, i);
				 }
				if(isKeyFile.exists()){
					ReleaseManagementDTO.Regist dto = new ReleaseManagementDTO.Regist();
					dto.setId(Integer.parseInt(request.getParameter("id")));
					saveSystemRelese(dto);
				}
			}catch (SQLException e){
				throw new CommonException("sqlException.systemRelease.exception",
						"입력한 릴리즈 정보를 저장할 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
			} catch(FileNotFoundException e){
				throw new CommonException("notfoundReleaseFile.systemRelease.exception",
						"해당 릴리즈 파일을 찾을수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
			}catch (IOException e) {
				throw new CommonException("releaseUploadIoException.systemRelease.exception",
						"해당 릴리즈 파일 업로드 중 문제가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
			} finally {
				String originalFileName = mpf.getOriginalFilename();
				String originalFileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
				String FileResultName = originalFileName.replace(originalFileExtension, "");
				//lock 파일 삭제
				File releaseLockFile = new File(LocalDirectoryConfiguration.getLockDir()+System.getProperty("file.separator")+FileResultName+"-download.lock");
				if(releaseLockFile.exists()){
					Boolean check =releaseLockFile.delete();
					if( LOGGER.isDebugEnabled() ){
						LOGGER.debug("check delete ReleaseLock File  : "  + check);
					}
				}
		        if (stream != null) {
		            try {
		            	stream.close();
					} catch (IOException e) {
						if( LOGGER.isErrorEnabled() ){
							LOGGER.error( e.getMessage() );
						}
					}
		        }
		    }
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 릴리즈 다운로드 정보 저장
	 * @title               : saveSystemRelese
	 * @return            : void
	***************************************************/
	public void saveSystemRelese(ReleaseManagementDTO.Regist dto) throws SQLException{
		SessionInfoDTO userInfo = new SessionInfoDTO();
		if(  dto != null ){
			dto.setUpdateUserId(userInfo.getUserId());
			dto.setDownloadStatus("DOWNLOADED");
			dao.updateSystemReleaseById(dto);
		}
	}
}
