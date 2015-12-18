package org.openpaas.ieda.web.deploy.bosh;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.IEDAConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IEDABoshService {

	@Autowired
	private IEDABoshAwsRepository awsRepository;
	
	@Autowired
	private IEDABoshOpenstackRepository openstackRepository;
	@Autowired
	private IEDAConfiguration iedaConfiguration;
	
	@Autowired
	private IEDABoshAwsService awsService;
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	public List<BoshInfo> getBoshList(){
		
		List<BoshInfo> boshList = new ArrayList();
		List<IEDABoshAwsConfig> boshAwsList = awsRepository.findAll();
		List<IEDABoshOpenstackConfig> boshOpenstackList = openstackRepository.findAll();
		if ( boshAwsList == null ) {
			throw new IEDACommonException("notfound.bosh.exception",
					"Bosh 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		int recid = 0;
		if( boshAwsList.size() >0 ){
			for(IEDABoshAwsConfig awsConfig : boshAwsList){
				BoshInfo boshInfo = new BoshInfo();
				boshInfo.setRecid(recid++);
				boshInfo.setId(awsConfig.getId());
				boshInfo.setIaas("AWS");
				boshInfo.setSubnetRange(awsConfig.getSubnetRange());
				boshInfo.setCreatedDate(awsConfig.getCreatedDate());
				boshList.add(boshInfo);
			}
		}
		
		if( boshOpenstackList.size() >0 ){
			for(IEDABoshOpenstackConfig openstackConfig : boshOpenstackList){
				BoshInfo boshInfo = new BoshInfo();
				boshInfo.setRecid(recid++);
				boshInfo.setId(openstackConfig.getId());
				boshInfo.setIaas("OPENSTACK");
				boshInfo.setSubnetRange(openstackConfig.getSubnetRange());
				boshInfo.setCreatedDate(openstackConfig.getCreatedDate());
				boshList.add(boshInfo);
			}
		}
		boshList.stream().sorted((BoshInfo o1, BoshInfo o2) -> o1.getCreatedDate().compareTo(o2.getCreatedDate()));
		return boshList;
	}
	
	
	public Boolean setSpiffMerge(String tempFilePath, String stubFilePath, String deplyFilePath){
		File stubFile = null;
		File tempFile = null;
		String command = "";
		Runtime r = Runtime.getRuntime();

		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		Boolean status = Boolean.FALSE;
		try {
			tempFile = new File(tempFilePath);
			stubFile = new File(stubFilePath);
			
			if(stubFile.exists() && tempFile.exists()){
				command = iedaConfiguration.getScriptDir()+ System.getProperty("file.separator")  + "merge-deploy.sh ";
				command += stubFilePath + " ";
				command += tempFilePath + " ";
				command += deplyFilePath;
								
				Process process = r.exec(command);
				process.getInputStream();
				inputStream = process.getInputStream();
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				String info = null;
				String streamLogs = "";
				while ((info = bufferedReader.readLine()) != null){
					streamLogs += info;
					log.info("=== Deployment File Merge \n"+ info );
				}
				status = Boolean.TRUE;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
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
		return status;
	}

	public void deleteDeploy(String deploymentFile) {

		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		Runtime r = Runtime.getRuntime();
		try{
			String command = iedaConfiguration.getScriptDir()+ System.getProperty("file.separator")  + "bosh-delete.sh ";
			command += iedaConfiguration.getDeploymentDir()+ System.getProperty("file.separator") +deploymentFile + " ";
					
			Process process = r.exec(command);
			log.info("### PROCESS ::: " + process.toString());
			process.getInputStream();
			inputStream = process.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String info = null;
			String streamLogs = "";
			while ((info = bufferedReader.readLine()) != null){
				streamLogs += info;
				log.info("=== Delete Deploy File \n"+ info );
				messagingTemplate.convertAndSend("/bosh/boshDelete", info);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
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
			messagingTemplate.convertAndSend("/bosh/boshDelete", "complete");
		}
		
	}

	public void installBootstrap(String deployFileName) {
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		Runtime r = Runtime.getRuntime();
		String command = "";
		log.info("%%%% Deploy File Name : " + deployFileName);
		try{
			command += iedaConfiguration.getScriptDir()+ System.getProperty("file.separator")  + "bosh-deploy.sh ";
			command += iedaConfiguration.getDeploymentDir()+ System.getProperty("file.separator")  + deployFileName ;
					
			Process process = r.exec(command);
			log.info("### PROCESS ::: " + process.toString());
			process.getInputStream();
			inputStream = process.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String info = null;
			String streamLogs = "";
			while ((info = bufferedReader.readLine()) != null){
				streamLogs += info;
				log.info("=== InstallBootstrap Logs \n"+ info );
				messagingTemplate.convertAndSend("/bosh/boshInstall", info);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (Exception e) {
			}
			try {
				if (bufferedReader != null){
					bufferedReader.close();
					messagingTemplate.convertAndSend("/bosh/boshInstall", "complete");
				}
			} catch (Exception e) {
			}
		}
		
	}
}
