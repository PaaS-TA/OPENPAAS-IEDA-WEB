package org.openpaas.ieda.common;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class LocalDirectoryConfiguration {
	final private static String SEPARATOR      						= System.getProperty("file.separator");
	final private static String BASE_DIR       							= System.getProperty("user.home") + SEPARATOR + ".bosh_plugin";
	final private static String SSH_DIR       							= System.getProperty("user.home") + SEPARATOR + ".ssh";
	final private static String TMP_DIR        							= System.getProperty("user.home") + SEPARATOR + "tmp";
	final private static String STEMCELL_DIR   						= BASE_DIR + SEPARATOR + "stemcell";
	final private static String RELEASE_DIR    						= BASE_DIR + SEPARATOR + "release";
	final private static String DEPLOYMENT_DIR 					= BASE_DIR + SEPARATOR + "deployment";
	final private static String TEMP_DIR       							= BASE_DIR + SEPARATOR + "temp";
	final private static String LOCK_DIR								= BASE_DIR + SEPARATOR + "lock";
	final private static String KEY_DIR									= BASE_DIR + SEPARATOR + "key";
	final private static String DEPLOYMENT_MANIFEST_DIR = DEPLOYMENT_DIR + SEPARATOR + "manifest";
	
	final private static String PROJECT_STATIC_DIR				= System.getProperty("user.dir") + SEPARATOR + "src/main/resources/static";
	final private static String MANIFEST_TEMPLATE_DIR	 	= PROJECT_STATIC_DIR + SEPARATOR + "deploy_template";
	final private static String GENERATE_CERTS_DIR			= PROJECT_STATIC_DIR + SEPARATOR + "generate-certs";
	
	private final static Logger LOGGER = LoggerFactory.getLogger(LocalDirectoryConfiguration.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 어플케이션 실행 후 초기 실행 
	 * @title               : initialize
	 * @return            : boolean
	***************************************************/
	public static boolean initialize() {
		try {
			getSshDir();
			getStemcellDir();
			getReleaseDir();
			getDeploymentDir();
			getTempDir();
			getManifastDir();
			getLockDir();
			getKeyDir();
			getGenerateCertsDir();
		} catch ( Exception e ) {
			if( LOGGER.isErrorEnabled() ){
				LOGGER.error(e.getMessage());
			}
		}
		
		return true;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 디렉터리가 존재하는지 검사 후 디렉터 생성
	 * @title               : checkAndMakeDirectory
	 * @return            : boolean
	***************************************************/
	private static boolean checkAndMakeDirectory(String dirToCheck) {
		boolean isOk = true;
		try {
			File dir = new File(dirToCheck);
			if ( !dir.isDirectory() ) 
				isOk = dir.mkdirs();
		} catch ( Exception e ) {
			isOk = false;
		}
				
		return isOk;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : ssh 저장 위치를 검사하고 없으면 생성하여 응답
	 * @title               : getSshDir
	 * @return            : String
	***************************************************/
	public static String getSshDir() {
		if ( !checkAndMakeDirectory(SSH_DIR) )
			throw new CommonException("notfound.ssh.local.exception", "Key 파일 저장 위치가 존재 하지 않습니다.", HttpStatus.NOT_FOUND);

		return SSH_DIR;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : tmp 저장 위치를 검사하고 없으면 생성하여 응답
	 * @title               : getTmpDir
	 * @return            : String
	***************************************************/
	public static String getTmpDir() {
		if ( !checkAndMakeDirectory(TMP_DIR) )
			throw new CommonException("notfound.tmp.local.exception", "tmp 저장 위치가 존재 하지 않습니다.", HttpStatus.NOT_FOUND);
		return TMP_DIR;
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 저장 위치를 검사하고 없으면 생성하여 응답
	 * @title               : getStemcellDir
	 * @return            : String
	***************************************************/
	public static String getStemcellDir() {
		if ( !checkAndMakeDirectory(STEMCELL_DIR) )
			throw new CommonException("notfound.stemcell.local.exception", "스템셀 저장 위치가 존재 하지 않습니다.", HttpStatus.NOT_FOUND);

		return STEMCELL_DIR;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 릴리즈 저장 위치를 검사하고 없으면 생성하여 응답 
	 * @title               : getReleaseDir
	 * @return            : String
	***************************************************/
	public static String getReleaseDir() {
		if ( !checkAndMakeDirectory(RELEASE_DIR) )
			throw new CommonException("notfound.release.local.exception", "릴리즈 저장 위치가 존재 하지 않습니다.", HttpStatus.NOT_FOUND);
		return RELEASE_DIR;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포 관련 저장 위치를 검사하고 없으면 생성하여 응답 
	 * @title               : getDeploymentDir
	 * @return            : String
	***************************************************/
	public static String getDeploymentDir() {
		if ( !checkAndMakeDirectory(DEPLOYMENT_DIR) )
			throw new CommonException("notfound.deployment.local.exception", "배포관련 저장 위치가 존재 하지 않습니다.", HttpStatus.NOT_FOUND);
		return DEPLOYMENT_DIR;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 임시 저장 위치를 검사하고 없으면 생성하여 응답 
	 * @title               : getTempDir
	 * @return            : String
	***************************************************/
	public static String getTempDir() {
		if ( !checkAndMakeDirectory(TEMP_DIR) )
			throw new CommonException("notfound.temp.local.exception", "임시 저장 위치가 존재 하지 않습니다.", HttpStatus.NOT_FOUND);
		return TEMP_DIR;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Manifest 템플릿 위치를 검사하고 없으면 생성하여 응답 
	 * @title               : getManifastTemplateDir
	 * @return            : String
	***************************************************/
	public static String getManifastTemplateDir(){
		if(!checkAndMakeDirectory(MANIFEST_TEMPLATE_DIR))
			throw new CommonException("notfound.ManifestTemp.local.exception", "Manifest 템플릿 저장 위치가 존재 하지 않습니다.", HttpStatus.NOT_FOUND);
		return MANIFEST_TEMPLATE_DIR;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Manifest 위치를 검사하고 없으면 생성하여 응답
	 * @title               : getManifastDir
	 * @return            : String
	***************************************************/
	public static String getManifastDir(){
		if(!checkAndMakeDirectory(DEPLOYMENT_MANIFEST_DIR))
			throw new CommonException("notfound.manifest.local.exception", "Manifest 저장 위치가 존재 하지 않습니다.", HttpStatus.NOT_FOUND);
		return DEPLOYMENT_MANIFEST_DIR;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Lock 파일 위치를 검사하고 없으면 생성하여 응답
	 * @title               : getLockDir
	 * @return            : String
	***************************************************/
	public static String getLockDir(){
		if(!checkAndMakeDirectory(LOCK_DIR))
			throw new CommonException("notfound.lock.local.exception", "lock 파일 저장 위치가 존재 하지 않습니다.", HttpStatus.NOT_FOUND);
		return LOCK_DIR;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Key 파일 위치를 검사하고 없으면 생성하여 응답
	 * @title               : getKeyDir
	 * @return            : String
	***************************************************/
	public static String getKeyDir(){
		if(!checkAndMakeDirectory(KEY_DIR))
			throw new CommonException("notfound.key.local.exception", "key 파일 저장 위치가 존재 하지 않습니다.", HttpStatus.NOT_FOUND);
		return KEY_DIR;
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Generate Certs 파일 위치를 검사하고 없으면 생성하여 응답
	 * @title               : getGenerateCertsDir
	 * @return            : String
	***************************************************/
	public static String getGenerateCertsDir(){
		if(!checkAndMakeDirectory(GENERATE_CERTS_DIR))
			throw new CommonException("notfound.generateCerts.local.exception", "Generate Certs 파일 저장 위치가 존재 하지 않습니다.", HttpStatus.NOT_FOUND);
		return GENERATE_CERTS_DIR;
	}
	
	
}
