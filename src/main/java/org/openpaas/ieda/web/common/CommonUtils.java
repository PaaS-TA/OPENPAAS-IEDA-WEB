package org.openpaas.ieda.web.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtils {

	public static String lineAddSpace(String exc, int cnt ){
		String returnString = "";
		String[] lines = exc.split(System.getProperty("line.separator"));
		String empty = "";
		for(int i=0;i < cnt;i++){
			empty += " ";
		}
		
		if(lines.length >0 ){
			for(int i =0; i < lines.length;i++){
				String keyValue = lines[i].replace("/\r\n/g", "");
				if(!StringUtils.isEmpty(keyValue)){
					if(i == 0){
						returnString += empty + keyValue;
					}
					else{
						returnString += "\n" + empty + keyValue;
					}
				}
			}
		}
		return returnString;
	}
	
	public static String setSpiffMerge(String iaas, Integer id, String prefix ,  String stubFileName, String settingFileName) {
	
		String deploymentFileName = iaas.toLowerCase() +"-" + prefix + "-"+id+".yml";		
		String templateFile = LocalDirectoryConfiguration.getTempDir() + System.getProperty("file.separator") + stubFileName;
		String parameterFile = LocalDirectoryConfiguration.getTempDir() + System.getProperty("file.separator") + settingFileName;
		String deploymentPath= LocalDirectoryConfiguration.getDeploymentDir() + System.getProperty("file.separator") + deploymentFileName;

		File stubFile = null;
		File settingFile = null;
		String param = "";
		
		Runtime r = Runtime.getRuntime();

		InputStream inputStream = null;

		BufferedReader bufferedReader = null;
		try {
			stubFile = new File(templateFile);
			settingFile = new File(parameterFile);

			if(stubFile.exists() && settingFile.exists()){
				param = templateFile + " " + parameterFile ;
				
				
				ProcessBuilder builder = new ProcessBuilder("spiff", "merge", templateFile, parameterFile );
				builder.redirectErrorStream(true);
				Process process = builder.start();
				
				inputStream = process.getInputStream();
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				String info = null;
				String deloymentContent = "";
				while ((info = bufferedReader.readLine()) != null){
					deloymentContent += info + "\n";
				}

				IOUtils.write(deloymentContent, new FileOutputStream(deploymentPath), "UTF-8");
			}
			else{
				throw new IEDACommonException("illigalArgument.bosh.exception",
						"Merge할 File이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (Exception e) {
			}
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (Exception e) {
			}
		}
		return deploymentFileName;
	}
	
}
