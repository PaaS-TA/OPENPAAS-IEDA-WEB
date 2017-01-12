package org.openpaas.ieda.web.common.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.dao.ManifestTemplateVO;
import org.openpaas.ieda.web.deploy.diego.dao.DiegoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.parser.ParserException;

final public class CommonUtils {

	final private static Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);
	public static final double SPACE_KB = 1024;
	public static final double SPACE_MB = 1024 * SPACE_KB;
	public static final double SPACE_GB = 1024 * SPACE_MB;
	public static final double SPACE_TB = 1024 * SPACE_GB;

	final private static String SEPARATOR = System.getProperty("file.separator");
	final private static String TEMP_FILE =  LocalDirectoryConfiguration.getTempDir() + SEPARATOR;
	final private static String DEPLOYMENT_FILE = LocalDirectoryConfiguration.getDeploymentDir() + SEPARATOR;

	private CommonUtils() {

	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 정규표현식 특수문자 검증 및 문자열 치환하여 문자열을 바꾼다.
	 * @title               : lineAddSpace
	 * @return            : String
	***************************************************/
	public static String lineAddSpace(String exc, int cnt) {
		String[] lines = exc.split(System.getProperty("line.separator"));
		StringBuffer emptyBuffer = new StringBuffer();
		for (int i = 0; i < cnt; i++) {
			emptyBuffer.append(" ");
		}
		String empty = emptyBuffer.toString();
		
		StringBuffer resultBuffer = new StringBuffer(); 
		if (lines.length > 0) {
			for (int i = 0; i < lines.length; i++) {
				String keyValue = lines[i].replace("/\r\n/g", "");
				if (!StringUtils.isEmpty(keyValue)) {
					if (i == 0) {
						resultBuffer.append(empty + keyValue);
					} else {
						resultBuffer.append("\n" + empty + keyValue);
					}
				}
			}
		}
		String returnString = resultBuffer.toString();
		return returnString;
	}

	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : Manifest 템플릿 파일과 merge하여 deployment 경로에 최종 Manifest 파일 생성
	 * @title         : setSpiffMerge
	 * @return        : void
	***************************************************/
	public static void setSpiffMerge(String iaas, Integer id, String keyFile, String settingFileName,
			ManifestTemplateVO manifestTemplate) {

		// temp
		String inputFile = TEMP_FILE + settingFileName;
		String deploymentPath = DEPLOYMENT_FILE + settingFileName;
		String keyPath = LocalDirectoryConfiguration.getKeyDir() + SEPARATOR + keyFile;

		File settingFile = null;
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		ProcessBuilder builder = new ProcessBuilder();
		try {
			settingFile = new File(inputFile);
			if (settingFile.exists()) {
				List<String> cmd = new ArrayList<String>();
				cmd.add("spiff");
				cmd.add("merge");
				if (!StringUtils.isEmpty(manifestTemplate.getCommonBaseTemplate())) {
					cmd.add(manifestTemplate.getCommonBaseTemplate());
				}
				if (!StringUtils.isEmpty(manifestTemplate.getCommonJobTemplate())) {
					cmd.add(manifestTemplate.getCommonJobTemplate());
				}
				if (!StringUtils.isEmpty(manifestTemplate.getIaasPropertyTemplate())) {
					cmd.add(manifestTemplate.getIaasPropertyTemplate());
				}
				if (!StringUtils.isEmpty(manifestTemplate.getMetaTemplate())) {
					cmd.add(manifestTemplate.getMetaTemplate());
				}
				if (!StringUtils.isEmpty(manifestTemplate.getOptionNetworkTemplate())) {
					cmd.add(manifestTemplate.getOptionNetworkTemplate());
				}
				if (!StringUtils.isEmpty(manifestTemplate.getOptionResourceTemplate())) {
					cmd.add(manifestTemplate.getOptionResourceTemplate());
				}
				if (!StringUtils.isEmpty(manifestTemplate.getCommonOptionTemplate())) {
					cmd.add(manifestTemplate.getCommonOptionTemplate());
				}
				if (!StringUtils.isEmpty(manifestTemplate.getOptionEtc())) {
					cmd.add(manifestTemplate.getOptionEtc());
				}
				if (!StringUtils.isEmpty(manifestTemplate.getOptionEtc())) {
					cmd.add(manifestTemplate.getOptionEtc());
				}
				if( !(keyFile.equals("microbosh") || keyFile.equals("bosh")) && !StringUtils.isEmpty(keyPath) ){
					cmd.add(keyPath);//생성한 key.yml파일 추가
				}
				cmd.add(inputFile);
				builder.command(cmd);
				builder.redirectErrorStream(true);
				
				Process process = builder.start();
				inputStream = process.getInputStream();
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
				String info = null;
				StringBuffer deployBuffer = new StringBuffer();
				while ((info = bufferedReader.readLine()) != null) {
					deployBuffer.append(info + "\n");
				}
				String deloymentContent = deployBuffer.toString();
				if( !deloymentContent.equals("") ){
					IOUtils.write(deloymentContent, new FileOutputStream(deploymentPath), "UTF-8");
				}
			} else {
				throw new CommonException("notfound.manifest.exception", "Merge할 File이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
			}
		} catch (FileNotFoundException e){
			e.printStackTrace();
				throw new CommonException("fileNotFound.manifest.exception", "Merge할 File이 존재하지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IOException e) {
			e.printStackTrace();
			throw new CommonException("ioFileRead.manifest.exception", "Manifest 생성 중 문제가 발생하였습니다.  "  +  builder.command() , HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if( bufferedReader != null) bufferedReader.close();
			} catch (IOException e) {
				if( LOGGER.isErrorEnabled() ){
					LOGGER.error( e.getMessage() );
				}
			}
		}
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego Manifest 템플릿 파일과 병합하여 읽어와서 deployment 경로에 최종 Manifest 파일 생성
	 * @title               : setSpiffScript
	 * @return            : void
	***************************************************/
	public static void setSpiffScript(String prefix, String inputFile, ManifestTemplateVO manifestTemplate,
			DiegoVO vo, String separator) {
		
		File settingFile = null;
		File shellScriptFile = null;
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;

		try {
			settingFile = new File( TEMP_FILE + inputFile);
			shellScriptFile = new File( manifestTemplate.getShellScript() );
			ProcessBuilder builder = new ProcessBuilder();

			if ( settingFile.exists() && shellScriptFile.exists() && vo.getDiegoReleaseVersion() != null ) {
				List<String> cmd = new ArrayList<String>();
				//1. shellScript
				cmd.add(manifestTemplate.getShellScript());
				//1.1 home
				cmd.add(System.getProperty("user.home"));
				//1.2 Deployment Version
                cmd.add(manifestTemplate.getMinReleaseVersion());

				//1.3 Path to BOSH manifest stub file.
				if (!StringUtils.isEmpty(manifestTemplate.getCommonBaseTemplate())) {
					cmd.add(manifestTemplate.getCommonBaseTemplate());
				} else{
					cmd.add("");
				}
				//1.4 Path to DIEGO stub file.
				if (!StringUtils.isEmpty(manifestTemplate.getCommonJobTemplate())) {
					cmd.add(manifestTemplate.getCommonJobTemplate());
				} else{
					cmd.add("");
				}
				//1.5 Path to DIEGO-version-overrides stub file.
				if (!StringUtils.isEmpty(manifestTemplate.getMetaTemplate())) {
					cmd.add(manifestTemplate.getMetaTemplate());
				} else{
					cmd.add("");
				}
				//1.6 Path to IaaS-Settings-overrides stub file.
				if (!StringUtils.isEmpty(manifestTemplate.getIaasPropertyTemplate())) {
					cmd.add(manifestTemplate.getIaasPropertyTemplate());
				} else{
					cmd.add("");
				}
				//1.7 Path to CF manifest file.
				if (!StringUtils.isEmpty(manifestTemplate.getCfTempleate())) {
					cmd.add(manifestTemplate.getCfTempleate());
				} else{
					cmd.add("");
				}
				//1.8 Path to DIEGO input file.
				cmd.add(inputFile);
				//1.9 Path to DIEGO manifest file.
				cmd.add(inputFile);
				if (!StringUtils.isEmpty( vo.getKeyFile() )) {
					cmd.add( vo.getKeyFile() );
				}
				
				//2. OPTIONAL TEMPLATES
				//2.1 Path to DIEGO Network 1 stub file.
				if (!StringUtils.isEmpty(manifestTemplate.getOptionNetworkTemplate())) {
					cmd.add(manifestTemplate.getOptionNetworkTemplate());
				} else {
					cmd.add("");
				}
				//2.2 Path to DIEGO Network 2 stub file.
				if (!StringUtils.isEmpty(manifestTemplate.getOptionEtc())) {
					cmd.add(manifestTemplate.getOptionEtc());
				} else {
					cmd.add("");
				}
				//2.3 Path to Resource-overrides stub file.
				if (!StringUtils.isEmpty(manifestTemplate.getOptionResourceTemplate())) {
					cmd.add(manifestTemplate.getOptionResourceTemplate());
				} else {
					cmd.add("");
				}
				//2.4 Path to Path to PaaSTA-overrides stub file.
				if( !StringUtils.isEmpty(manifestTemplate.getCommonOptionTemplate()) && "true".equals(vo.getPaastaMonitoringUse().toLowerCase()) ){
					cmd.add(manifestTemplate.getCommonOptionTemplate());
				}else{
					cmd.add("");
				}
				
				
				builder.command(cmd);
				builder.redirectErrorStream(true);
				Process process = builder.start();
				
				inputStream = process.getInputStream();
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
				
				String info = null;
				StringBuffer deployBuffer = new StringBuffer();
				while ((info = bufferedReader.readLine()) != null) {
					deployBuffer.append(info + "\n");
				}
				
				String deloymentContent = deployBuffer.toString();
				if (!deloymentContent.equals("")) {
					IOUtils.write(deloymentContent, new FileOutputStream( DEPLOYMENT_FILE + vo.getDeploymentFile()), "UTF-8");
				}
			} else {
				throw new CommonException("notfound.diegoManifest.exception",  "Merge할 File이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
			}
		} catch( UnsupportedEncodingException e){
			throw new CommonException("unsupportedEncoding.diegoManifest.exception", 
					"DIEGO Manifest 파일 정보를 읽어올 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch ( FileNotFoundException e ){
			throw new CommonException("notfound.diegoManifest.exception", 
					"DIEGO Manifest 파일을 생성할 수 없습니다. ", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IOException e) {
			e.printStackTrace();
			throw new CommonException("ioFileRead.diegoManifest.exception", 
					"DIEGO Manifest 파일 정보를 읽어올 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
			 
		} finally {
			try {
				if (bufferedReader != null)
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
	 * @description   : 파일 사이즈 반올림하여 MegaByte로 형식화함
	 * @title               : formatSizeUnit
	 * @return            : String
	***************************************************/
	public static String formatSizeUnit(long size) {
		if (size <= 0)
			return "0";
		final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
		// 밑을 10으로 사용하여 지정된 숫자의 로그를 반환
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		// format 메소드를 사용하여 특정 패턴으로 값을 포맷할 수 있다. (반환 값 String)
		// 소수점 원하는 자릿수까지만 출력,단위 반환
		return new DecimalFormat("#,##0.0").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : File Size 변경
	 * @title               : bytes2String
	 * @return            : String
	***************************************************/
	public static String bytes2String(long sizeInBytes) {

		NumberFormat nf = new DecimalFormat();
		nf.setMaximumFractionDigits(1);

		try {
			if (sizeInBytes < SPACE_KB) {
				return nf.format(sizeInBytes) + " Byte(s)";
			} else if (sizeInBytes < SPACE_MB) {
				return nf.format(sizeInBytes / SPACE_KB) + " KB";
			} else if (sizeInBytes < SPACE_GB) {
				return nf.format(sizeInBytes / SPACE_MB) + " MB";
			} else if (sizeInBytes < SPACE_TB) {
				return nf.format(sizeInBytes / SPACE_GB) + " GB";
			} else {
				return nf.format(sizeInBytes / SPACE_TB) + " TB";
			}
		} catch (NumberFormatException e) {
			return sizeInBytes + " Byte(s)";
		}

	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Yaml Parser
	 * @title               : yamlParser
	 * @return            : Map<String,Object>
	***************************************************/
	@SuppressWarnings("unchecked")
	public static Map<String, Object> yamlParser(String contents){
		Map<String, Object> object  = null;
		try{
			Yaml yaml = new Yaml();
			object = (Map<String, Object>)yaml.load(contents);
			
		} catch(ParserException e ){
			String errorMessage = getPrintStackTrace(e);
			throw new CommonException("parser.yaml.exception", errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch(Exception e){
			String errorMessage = getPrintStackTrace(e);
			throw new CommonException("server.yaml.exception", errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return object;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : printStackTrace 정보 String 변환
	 * @title               : getPrintStackTrace
	 * @return            : String
	***************************************************/
	public static String getPrintStackTrace(Exception e){
		StringWriter errors = new StringWriter();
		String[]  split = e.getMessage().split("\n");
		for(  int i=0; i<split.length; i++){
			errors.append(split[i] + "<br/>");
		}
		
		return errors.toString();
	}
}
