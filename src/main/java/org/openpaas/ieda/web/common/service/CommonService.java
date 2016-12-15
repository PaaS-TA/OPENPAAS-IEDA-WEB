package org.openpaas.ieda.web.common.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Service
public class CommonService{
	
	final private static Logger LOGGER = LoggerFactory.getLogger(CommonService.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Private Key 파일을 업로드하고 권한을 설정
	 * @title               : uploadKeyFile
	 * @return            : void
	***************************************************/
	public void uploadKeyFile(MultipartHttpServletRequest request) {
		Iterator<String> itr =  request.getFileNames();
		File keyPathFile = new File(LocalDirectoryConfiguration.getSshDir());
		if (!keyPathFile.isDirectory()){
			boolean result = keyPathFile.mkdir();
			LOGGER.debug("keyPathFile.mkdir : " + result);
		}
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("request.getFileName : " + request.getFileNames().toString());
		}
		if(itr.hasNext()) {
			BufferedOutputStream stream = null;
			MultipartFile mpf = request.getFile(itr.next());
			try {
				String keyFilePath = LocalDirectoryConfiguration.getSshDir() + System.getProperty("file.separator") + mpf.getOriginalFilename();
				byte[] bytes = mpf.getBytes();
				File isKeyFile = new File(keyFilePath);
				stream = new BufferedOutputStream(new FileOutputStream(isKeyFile));
				stream.write(bytes);
				
				boolean result = isKeyFile.setWritable(false, false);
				LOGGER.debug("isKeyFile.setWritable : " + result);
				isKeyFile.setExecutable(false, false);
				isKeyFile.setReadable(false, true);
				Set<PosixFilePermission> pfp = new HashSet<>();
				pfp.add(PosixFilePermission.OWNER_READ);
				Files.setPosixFilePermissions(Paths.get(keyFilePath), pfp);
				
			} catch (IOException e) {
				if(LOGGER.isErrorEnabled()){
					LOGGER.error(e.getMessage());
				}
			} finally{
				try {
					if( stream != null ) stream.close();
				} catch (IOException e) {
					if( LOGGER.isErrorEnabled() ){
						LOGGER.error( e.getMessage() );
					}
				}
			}
		} 
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 로컬에서 Private Key 파일  정보 목록을 조회하고 파일명 목록을 응답
	 * @title               : getKeyFileList
	 * @return            : List<String>
	***************************************************/
	public List<String> getKeyFileList() {

		File keyPathFile = new File(LocalDirectoryConfiguration.getSshDir());
		if ( !keyPathFile.isDirectory() ) return null;

		List<String> localFiles = null;

		File[] listFiles = keyPathFile.listFiles();
		if(listFiles != null){
			for (File file : listFiles) {

				if(!file.getName().toLowerCase().endsWith(".pem"))
					continue;

				if ( localFiles == null )
					localFiles = new ArrayList<String>();

				localFiles.add(file.getName());
			}
		}
		return localFiles;
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포파일 정보 조회
	 * @title               : getDeploymentInfo
	 * @return            : String
	***************************************************/
	public String getDeploymentInfo(String deploymentFile) {
		String contents = "";
		File settingFile = null;
		try {
			settingFile = new File(LocalDirectoryConfiguration.getDeploymentDir() + System.getProperty("file.separator") + deploymentFile);
			contents = IOUtils.toString(new FileInputStream(settingFile));
		} catch (FileNotFoundException e) {
			throw new CommonException("notfound.manifest.exception", "배포 파일 정보 조회 중 문제가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IOException e) {
			throw new CommonException("ioFileRead.manifest.exception", "배포 파일 정보 조회 중 문제가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return contents;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Lock 파일 설정
	 * @title               : lockFileSet
	 * @return            : Boolean
	***************************************************/
	public Boolean lockFileSet(String lockFileName){
		File lockFile = new File(LocalDirectoryConfiguration.getLockDir()+System.getProperty("file.separator")+lockFileName+".lock");
		Writer createLockFile =null;
		Boolean flag = null;
		try{
			if(!lockFile.exists()){
				flag= true;
				createLockFile = new OutputStreamWriter(new FileOutputStream(lockFile), "UTF-8");
			}else flag= false;
		}catch(IOException e){
			throw new CommonException("ioFIleRead.lockFile.exception", "lock 파일 생성에 문제가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			try {
				if(createLockFile != null)
					createLockFile.close();
			} catch (IOException e) {
				if( LOGGER.isErrorEnabled() ){
					LOGGER.error( e.getMessage() );
				}
			}
		}
		return flag;
	}
}
