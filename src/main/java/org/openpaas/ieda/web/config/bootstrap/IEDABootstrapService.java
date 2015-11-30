package org.openpaas.ieda.web.config.bootstrap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IEDABootstrapService {
	
	private static String stemcell;
	private static String microboshPw;
	/*NETWORK*/
	private static String subnetRange;
	private static String dns;
	private static String subnetId;
	private static String directorPrivateIp;
	private static String directorPublicIp;
	/*AWS*/
	private static String awsKey;
	private static String secretAccessKey;
	private static String securGroupName;
	private static String privateKey;
	
	public void setAwsInfos(BootStrapSettingData.Aws dto){
		awsKey = dto.getAwsKey();
		secretAccessKey = dto.getAwsPw();
		securGroupName = dto.getSecurGroupName();
		privateKey = dto.getPrivateKeyName();
	}
	
	public void setNetworkInfos(BootStrapSettingData.Network dto){
		subnetRange = dto.getSubnetRange();
		dns = dto.getDns();
		subnetId = dto.getSubnetId();
		directorPrivateIp = dto.getDirectorPrivateIp();
		directorPublicIp = dto.getDirectorPublicIp();		
	}
	
	public void setReleaseInfos(BootStrapSettingData.Resources dto){
		stemcell = dto.getTargetStemcell();
		microboshPw = dto.getMicroBoshPw();
		downloadSettingFile();
	}
	
	public void downloadSettingFile(){
		
		//파일 가져오기
		File sample = new File("src\\main\\resources\\static\\deploy_template\\bosh-init-aws-template.yml"); //src\main\resources\static\deploy_template\bosh-init-aws-template.yml
		
	    //GET BootStrap Info(DB 정보)
		//file Set BootStrap Info
//		FileReader fr = null;
//		FileInputStream fis = null;
//		InputStreamReader isr = null;
//		Path path = Paths.get(filePath);
		Charset charset = StandardCharsets.UTF_8;
//
		String content = "";
		String value = "";
		String targetFilePath = "";
		String targetFileName = "";
		try {
			content = IOUtils.toString(new FileInputStream(sample), "UTF-8");
			/*log.info("######################################################");
			log.info(content);
			log.info("######################################################");*/
			//List<TargetReplace> targetReplaces = getReplaceList();
			List<BootstrapItem> bootstrapItems  = makeBootstrapItems();
			for(BootstrapItem item:bootstrapItems){
				content = content.replace(item.getTargetItem(), item.getSourceItem());
			}
			/*
			log.info("*******************************************************");
			log.info(content);
			log.info("*******************************************************");
			*/
			targetFilePath = "D:/ieda_workspace/temp/";
			targetFileName = "bosh-init-aws-micro-input-tample.yml";
			
			IOUtils.write(content, new FileOutputStream(targetFilePath + targetFileName), "UTF-8");
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public List<BootstrapItem> makeBootstrapItems() {
		List<BootstrapItem> items = new ArrayList<BootstrapItem>();
		/*log.info("## stemcell : " + stemcell);
		log.info("## microboshPw : " + microboshPw);
		log.info("## subnetRange : " + subnetRange);
		log.info("## dns : " + dns);
		log.info("## subnetId : " + subnetId);
		log.info("## directorPrivateIp : " + directorPrivateIp);
		log.info("## directorPublicIp : " + directorPublicIp);
		log.info("## awsKey : " + awsKey);
		log.info("## secretAccessKey : " + secretAccessKey);
		log.info("## securGroupName : " + securGroupName);
		log.info("## privateKey : " + privateKey);*/
		
		
		items.add(new BootstrapItem("[stemcell]", stemcell));
		items.add(new BootstrapItem("[microboshPw]", microboshPw));
		items.add(new BootstrapItem("[subnetRange]", subnetRange));
		items.add(new BootstrapItem("[dns]", dns));
		items.add(new BootstrapItem("[subnetId]", subnetId));
		items.add(new BootstrapItem("[directorPrivateIp]", directorPrivateIp));
		items.add(new BootstrapItem("[directorPublicIp]", directorPublicIp));
		items.add(new BootstrapItem("[awsKey]", awsKey));
		items.add(new BootstrapItem("[secretAccessKey]", secretAccessKey));
		items.add(new BootstrapItem("[securGroupName]", securGroupName));
		items.add(new BootstrapItem("[privateKey]", privateKey));
		/*log.info("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		log.info(items.toString());
		log.info("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");*/
		return items; 
	}
	
	public String getBootStrapSettingInfo(){
		String contents = "";
		File settingFile = null;
		String targetFilePath = "D:/ieda_workspace/temp/";
		String targetFileName = "bosh-init-aws-micro-input-tample.yml";
		try {
			settingFile = new File(targetFilePath+targetFileName);
			contents = IOUtils.toString(new FileInputStream(settingFile), "UTF-8");
		}catch (Exception e) {
			e.printStackTrace();
		}
		return contents; 
	}
}
