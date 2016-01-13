package org.openpaas.ieda.web.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.IOUtils;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.deploy.bootstrap.IEDABootstrapAwsConfig;
import org.openpaas.ieda.web.deploy.bootstrap.IEDABootstrapAwsService;
import org.openpaas.ieda.web.deploy.bootstrap.IEDABootstrapOpenstackConfig;
import org.openpaas.ieda.web.deploy.bootstrap.IEDABootstrapOpenstackService;
import org.openpaas.ieda.web.deploy.bosh.IEDABoshAwsConfig;
import org.openpaas.ieda.web.deploy.bosh.IEDABoshAwsService;
import org.openpaas.ieda.web.deploy.bosh.IEDABoshOpenstackConfig;
import org.openpaas.ieda.web.deploy.bosh.IEDABoshOpenstackService;
import org.openpaas.ieda.web.deploy.cf.IEDACfAwsConfig;
import org.openpaas.ieda.web.deploy.cf.IEDACfAwsService;
import org.openpaas.ieda.web.deploy.cf.IEDACfOpenstackConfig;
import org.openpaas.ieda.web.deploy.cf.IEDACfOpenstackService;
import org.openpaas.ieda.web.deploy.diego.IEDADiegoAwsConfig;
import org.openpaas.ieda.web.deploy.diego.IEDADiegoAwsService;
import org.openpaas.ieda.web.deploy.diego.IEDADiegoOpenstackConfig;
import org.openpaas.ieda.web.deploy.diego.IEDADiegoOpenstackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CommonService {

	@Autowired
	private IEDABootstrapAwsService bootstrapAwsService;
	@Autowired
	private IEDABootstrapOpenstackService bootstrapOpenstackService;
	@Autowired
	private IEDABoshAwsService boshAwsService;
	@Autowired
	private IEDABoshOpenstackService boshOpenstackService;
	@Autowired
	private IEDACfAwsService cfAwsService;
	@Autowired
	private IEDACfOpenstackService cfOpenstackService;
	@Autowired
	private IEDADiegoAwsService diegoAwsService;
	@Autowired
	private IEDADiegoOpenstackService diegoOpenstackService;
	
	public void uploadKeyFile(MultipartHttpServletRequest request) {
		Iterator<String> itr =  request.getFileNames();
		File keyPathFile = new File(LocalDirectoryConfiguration.getSshDir());
		if (!keyPathFile.isDirectory()){
			keyPathFile.mkdir();
		}
			
        if(itr.hasNext()) {
            MultipartFile mpf = request.getFile(itr.next());
            try {
            	String keyFilePath = LocalDirectoryConfiguration.getSshDir() + System.getProperty("file.separator") + mpf.getOriginalFilename();
                byte[] bytes = mpf.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(keyFilePath)));
                stream.write(bytes);
                stream.close();
            } catch (IOException e) {
                log.debug(e.getMessage());
                e.printStackTrace();
            }
        } 
	}

	public List<String> getKeyFileList() {

		FileNameExtensionFilter filter = new FileNameExtensionFilter("KeyFile only","pem");
		
		File keyPathFile = new File(LocalDirectoryConfiguration.getSshDir());
		if ( !keyPathFile.isDirectory() ) return null;
		
		List<String> localFiles = null;
		
		File[] listFiles = keyPathFile.listFiles();
		for (File file : listFiles) {
			
			if(!file.getName().toLowerCase().endsWith(".pem"))
				continue;
			
			if ( localFiles == null )
				localFiles = new ArrayList<String>();

			localFiles.add(file.getName());
		}
		
		return localFiles;
	}

	public String getDeploymentInfo(String deploymentFile) {
		String contents = "";
		File settingFile = null;
		try {
			settingFile = new File(LocalDirectoryConfiguration.getDeploymentDir() + System.getProperty("file.separator") + deploymentFile);
			contents = IOUtils.toString(new FileInputStream(settingFile), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contents;
	}

	public String getDeployMsg(CommonParam.DeployLog param) {
		String deployLog = "";
		switch (param.getService().trim().toLowerCase()) {
		case "boostrap":
			if( "AWS".equals(param.getIaas().trim().toUpperCase()) ){
				IEDABootstrapAwsConfig config = bootstrapAwsService.getAwsInfo(param.getId());
				deployLog = config.getDeployLog();
			}
			else if( "OPENSTACK".equals(param.getIaas().trim().toUpperCase()) ){
				IEDABootstrapOpenstackConfig config = bootstrapOpenstackService.getOpenstackInfo(param.getId());
				deployLog = config.getDeployLog();
			}
			break;
		case "bosh":
			if( "AWS".equals(param.getIaas().trim().toUpperCase()) ){
				IEDABoshAwsConfig config = boshAwsService.getAwsInfo(param.getId());
				deployLog = config.getDeployLog();
			}
			else if( "OPENSTACK".equals(param.getIaas().trim().toUpperCase()) ){
				IEDABoshOpenstackConfig config = boshOpenstackService.getOpenstackInfo(param.getId());
				deployLog = config.getDeployLog();
			}
			break;
		case "cf":
			if( "AWS".equals(param.getIaas().trim().toUpperCase()) ){
				IEDACfAwsConfig config = cfAwsService.getAwsInfo(param.getId());
				deployLog = config.getDeployLog();
			}
			else if( "OPENSTACK".equals(param.getIaas().trim().toUpperCase()) ){
				IEDACfOpenstackConfig config = cfOpenstackService.getOpenstackInfo(param.getId());
				deployLog = config.getDeployLog();
			}
			break;
		case "diego":
			if( "AWS".equals(param.getIaas().trim().toUpperCase()) ){
				IEDADiegoAwsConfig config = diegoAwsService.getAwsInfo(param.getId());
				deployLog = config.getDeployLog();
			}
			else if( "OPENSTACK".equals(param.getIaas().trim().toUpperCase()) ){
				IEDADiegoOpenstackConfig config = diegoOpenstackService.getOpenstackInfo(param.getId());
				deployLog = config.getDeployLog();
			}
			break;
		}
		
		return deployLog;
	}
}
