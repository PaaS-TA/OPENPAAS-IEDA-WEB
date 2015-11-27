package org.openpaas.ieda.web.config.release;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.openpaas.ieda.web.config.bootstrap.BootstrapItem;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IEDAReleaseManagementService {

	
	public void downloadSettingFile(String filePath){
		log.info("### filePath ::: " + filePath);
		//파일 가져오기
		File sample = new File(filePath); //src\main\resources\static\template\bosh-init-aws-micro-input-sample.yml
		
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
			
			//List<TargetReplace> targetReplaces = getReplaceList();
			List<BootstrapItem> bootstrapItems  = makeBootstrapItems();
			for(BootstrapItem item:bootstrapItems){
				content.replace(item.getTargetItem(), item.getSourceItem());
			}
			
			targetFilePath = "src/main/resources/static/template/";
			targetFileName = "bosh-init-aws-micro-input-sample.yml";
			
			IOUtils.write(content, new FileOutputStream(targetFilePath + "temp_"+targetFileName), "UTF-8");
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	public List<BootstrapItem> makeBootstrapItems() {
		List<BootstrapItem> items = new ArrayList<BootstrapItem>();
		items.add(new BootstrapItem("[boshName]", "%boshName%"));
		items.add(new BootstrapItem("[stemcell]", "%stemcell%"));
		items.add(new BootstrapItem("[microbosh]", "%microbosh%"));
		items.add(new BootstrapItem("[subnetRange]", "%subnetRange%"));
		items.add(new BootstrapItem("[dns]", "%dns%"));
		items.add(new BootstrapItem("[subnetId]", "%subnetId%"));
		items.add(new BootstrapItem("[directorPrivateIp]", "%directorPrivateIp%"));
		items.add(new BootstrapItem("[directorPublicIp]", "%directorPublicIp%"));
		items.add(new BootstrapItem("[awsKey]", "%awsKey%"));
		items.add(new BootstrapItem("[secretAccessKey]", "%secretAccessKey%"));
		items.add(new BootstrapItem("[securGroupName]", "%securGroupName%"));
		items.add(new BootstrapItem("[privateKey]", "%privateKey%"));
		return items; 
	}
}
