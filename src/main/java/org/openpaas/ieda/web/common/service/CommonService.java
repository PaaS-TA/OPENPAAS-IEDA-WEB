package org.openpaas.ieda.web.common.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.dao.CommonDAO;
import org.openpaas.ieda.web.common.dao.ManifestTemplateVO;
import org.openpaas.ieda.web.common.dto.KeyInfoDTO;
import org.openpaas.ieda.web.common.dto.SessionInfoDTO;
import org.openpaas.ieda.web.deploy.cf.dao.CfDAO;
import org.openpaas.ieda.web.deploy.cf.dao.CfVO;
import org.openpaas.ieda.web.deploy.diego.dao.DiegoDAO;
import org.openpaas.ieda.web.deploy.diego.dao.DiegoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.yaml.snakeyaml.Yaml;

@Service
public class CommonService{
	
	@Autowired CfDAO cfDao;
	@Autowired DiegoDAO diegoDao;
	@Autowired CommonDAO commonDao;
	
	final private static String SEPARATOR = System.getProperty("file.separator");
	final private static String GENERATE_CERTS_DIR = LocalDirectoryConfiguration.getGenerateCertsDir();
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
			if( settingFile.exists() ){
				contents = IOUtils.toString(new FileInputStream(settingFile));
			}
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
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Key 생성
	 * @title               : createKeyInfo
	 * @return            : void
	***************************************************/
	public String  createKeyInfo( KeyInfoDTO dto){
		File generateCertsFile = null;
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		String keyFileName = "";
		
		String generateCerts = GENERATE_CERTS_DIR + SEPARATOR + "generate-certs";
		generateCertsFile = new File( generateCerts );
		try {
			if( generateCertsFile.exists() ){
				String code = "";
				if( dto.getPlatform().toLowerCase().equals("cf") ) code ="1";
				else if (dto.getPlatform().toLowerCase().equals("diego")) code ="2";
				else if( dto.getPlatform().toLowerCase().equals("cfdiego") ){
					dto.setPlatform("cf-diego");
					code = "3";
				}
				keyFileName = dto.getIaas().toLowerCase() + "-" + dto.getPlatform()+"-key-" + dto.getId()+".yml";//key 파일명
				
				ProcessBuilder builder = new ProcessBuilder();
				List<String> cmd = new ArrayList<String>();
				cmd.add(generateCerts);
				cmd.add(GENERATE_CERTS_DIR );
				cmd.add(code); //1:cf, 2: diego, 3: cf&diego
				cmd.add( keyFileName.split(".yml")[0] ); // make key name(<iaas>-cf-key-<id>);
				if( dto.getPlatform().toLowerCase().equals("cf") || dto.getPlatform().toLowerCase().equals("cf-diego") ){
					cmd.add(dto.getDomain());//domain
					cmd.add(dto.getCountryCode());//국가 코드
					cmd.add(dto.getStateName());//시//도
					cmd.add(dto.getLocalityName());//시/구/군
					cmd.add(dto.getOrganizationName());//회사명
					cmd.add(dto.getUnitName());//부서명
					cmd.add(dto.getEmail());//email
				}
				
				builder.command(cmd);
				builder.redirectErrorStream(true);
				Process process = builder.start();//start script
				
				inputStream = process.getInputStream();//get script log
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
				String info = null;
				StringBuffer scriptBuffer = new StringBuffer();
				while ((info = bufferedReader.readLine()) != null) {
					scriptBuffer.append(info + "\n");
					if( info.indexOf("ERROR") > -1 ){
						throw new CommonException("ioFileRead.createKey.exception", 
								"해당 " + keyFileName +" 파일을 생성하는데 오류가 발생했습니다." , HttpStatus.INTERNAL_SERVER_ERROR);
					}
				}
				File keyFile = new File( LocalDirectoryConfiguration.getKeyDir() + SEPARATOR + keyFileName );
				if( !keyFile.exists() ){
					throw new CommonException("notfound.createKey.exception", keyFileName +" 파일을 찾을 수 없습니다." + cmd, HttpStatus.NOT_FOUND);
				}
				saveKeyFileName(dto, keyFileName);
			}else{
				throw new CommonException("notfound.createKey.exception", "generateCerts 스크립트 파일을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
			}
		} catch (IOException e) {
			if(LOGGER.isErrorEnabled()) LOGGER.error(e.getMessage());
			throw new CommonException("ioFileRead.createKey.exception",
					"generateCerts 스크립트 실행  중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}finally{
			try {
				if( bufferedReader != null ){
					bufferedReader.close();
				}
				if( inputStream != null ){
					inputStream.close();
				}
			} catch (IOException e) {
				if(LOGGER.isErrorEnabled()){
					LOGGER.error(e.getMessage());
				}
			}
		}
		return keyFileName;
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 키 파일명 저장
	 * @title               : saveKeyFileName
	 * @return            : void
	***************************************************/
	public void saveKeyFileName( KeyInfoDTO dto, String keyFileName ){
		SessionInfoDTO session = new SessionInfoDTO();
		if( !("diego".equals(dto.getPlatform().toLowerCase())) ){
			CfVO cfVo = cfDao.selectCfInfoById( Integer.parseInt(dto.getId()) );
			cfVo.setKeyFile(keyFileName);
			cfVo.setUpdateUserId(session.getUserId());
			cfDao.updateCfInfo(cfVo);
		}else{
			DiegoVO diegoVo = diegoDao.selectDiegoInfo( Integer.parseInt(dto.getId()) );
			diegoVo.setKeyFile(keyFileName);
			diegoVo.setUpdateUserId(session.getUserId());
			diegoDao.updateDiegoDefaultInfo(diegoVo);
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 해당 key 파일에서 SSH 핑거프린트 조회
	 * @title               : getFingerprint
	 * @return            : String
	***************************************************/
	@SuppressWarnings("unchecked")
	public String getFingerprint(String keyFileName){
		FileInputStream fis = null;
		BufferedReader rd = null;
		String fingerprint = "";
		Yaml yaml = new Yaml();
		try{
			String keyFile = LocalDirectoryConfiguration.getKeyDir() + SEPARATOR + keyFileName;
			fis = new FileInputStream(new File(keyFile));
			rd = new BufferedReader(new InputStreamReader(fis,"UTF-8"));
			String line = null;
		    StringBuffer contents = new StringBuffer();
		    while((line = rd.readLine()) != null) {
		    	contents.append(line + "\n");
		    }
		    Map<String, Object> object = (Map<String, Object>)yaml.load(contents.toString());
			Map<String, String> certMap = (Map<String,String>)object.get("diego-certs");
			fingerprint = certMap.get("ssh-key-fingerprint");
			if( StringUtils.isEmpty(fingerprint)  || fingerprint == null){
				throw new CommonException("notfound.createKey.exception", "해당 " +  keyFileName  +" 파일에 SSH 핑거프린트가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
			}
		}catch(IOException e){
			throw new CommonException("ioFileRead.createKey.exception", "해당 " +  keyFileName  +" 파일을 읽어올 수 없습니다. ", HttpStatus.INTERNAL_SERVER_ERROR);
		}catch(Exception e){
			throw new CommonException("notfound.createKey.exception", e.getMessage(), HttpStatus.NOT_FOUND);
		} finally {
			try {
				 if (rd != null) {
					 rd.close();
					 rd = null;
				 }
			}catch (IOException e) {
				if( LOGGER.isErrorEnabled() ){
					LOGGER.error( e.getMessage() );  
				}
			}
			try{
				if (fis != null) {
					fis.close();
					fis = null;
		        }
			}catch (IOException e) {
				if( LOGGER.isErrorEnabled() ){
					LOGGER.error( e.getMessage() );  
				}
			}
	    }
		return fingerprint;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 플랫폼별 배포명 전체 조회
	 * @title               : listDeployment
	 * @return            : List<String>
	***************************************************/
	public List<String> listDeployment(String platform, String iaas){
		return commonDao.selectDeploymentNameByPlatform(platform, iaas);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 플랫폼 별 릴리즈 설치 지원 버전 목록 조회
	 * @title               : getReleaseInfoByPlatform
	 * @return            : ManifestTemplateVO
	***************************************************/
	public List<ManifestTemplateVO> getReleaseInfoByPlatform(String deployType, String iaas){
		return  commonDao.selectReleaseInfoByPlatform(deployType, iaas);
	}
	
}
