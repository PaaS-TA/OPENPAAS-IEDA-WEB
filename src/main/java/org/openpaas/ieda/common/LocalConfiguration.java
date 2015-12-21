package org.openpaas.ieda.common;

import java.io.File;

import org.springframework.http.HttpStatus;

public class LocalConfiguration {
	final private static String BASE_DIR       = System.getProperty("user.home") + System.getProperty("file.seperator") + ".bosh_plugin";
	final private static String STEMCELL_DIR   = BASE_DIR + System.getProperty("file.seperator") + "stemcell";
	final private static String RELEASE_DIR    = BASE_DIR + System.getProperty("file.seperator") + "release";
	final private static String DEPLOYMENT_DIR = BASE_DIR + System.getProperty("file.seperator") + "deployment";
	final private static String SCRIPT_DIR     = BASE_DIR + System.getProperty("file.seperator") + "script";
	final private static String TEMP_DIR       = BASE_DIR + System.getProperty("file.seperator") + "temp";
	
	private static boolean checkAndMakeDirectory(String dirToCheck) {
		boolean isOk = true;
		try {
			File dir = new File(dirToCheck);
			
			if ( !dir.isDirectory() ) 
				dir.mkdirs();
		} catch ( Exception e ) {
			e.printStackTrace();
			isOk = false;
		}
				
		return isOk;
	}
	
	public static String getStemcellDir() {
		if ( !checkAndMakeDirectory(STEMCELL_DIR) )
			throw new IEDACommonException("notfound.local.exception", "스템셀 저장 위치 조회 중 오류가 발생했습니다.", HttpStatus.NOT_FOUND);

		return STEMCELL_DIR;
	}
	
	public static String getReleaseDir() {
		if ( !checkAndMakeDirectory(RELEASE_DIR) )
			throw new IEDACommonException("notfound.local.exception", "릴리즈 저장 위치 조회 중 오류가 발생했습니다.", HttpStatus.NOT_FOUND);
		return RELEASE_DIR;
	}
	
	public static String getDeploymentDir() {
		if ( !checkAndMakeDirectory(DEPLOYMENT_DIR) )
			throw new IEDACommonException("notfound.local.exception", "배포관련 저장 위치 조회 중 오류가 발생했습니다.", HttpStatus.NOT_FOUND);
		return DEPLOYMENT_DIR;
	}
	
	public static String getBootstrapScriptDir() {
		if ( !checkAndMakeDirectory(SCRIPT_DIR) )
			throw new IEDACommonException("notfound.local.exception", "스크립트저장 위치 조회 중 오류가 발생했습니다.", HttpStatus.NOT_FOUND);
		
		return SCRIPT_DIR;
	}
	
	public static String getTempDir() {
		if ( !checkAndMakeDirectory(TEMP_DIR) )
			throw new IEDACommonException("notfound.local.exception", "임시 저장 위치 조회 중 오류가 발생했습니다.", HttpStatus.NOT_FOUND);
		return TEMP_DIR;
	}
}
