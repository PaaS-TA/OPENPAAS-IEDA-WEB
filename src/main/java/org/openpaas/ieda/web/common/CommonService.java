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
import org.openpaas.ieda.web.deploy.bootstrap.IEDABootstrapAwsRepository;
import org.openpaas.ieda.web.deploy.bootstrap.IEDABootstrapOpenstackConfig;
import org.openpaas.ieda.web.deploy.bootstrap.IEDABootstrapOpenstackRepository;
import org.openpaas.ieda.web.deploy.bosh.IEDABoshAwsConfig;
import org.openpaas.ieda.web.deploy.bosh.IEDABoshAwsRepository;
import org.openpaas.ieda.web.deploy.bosh.IEDABoshOpenstackConfig;
import org.openpaas.ieda.web.deploy.bosh.IEDABoshOpenstackRepository;
import org.openpaas.ieda.web.deploy.cf.IEDACfAwsConfig;
import org.openpaas.ieda.web.deploy.cf.IEDACfAwsRepository;
import org.openpaas.ieda.web.deploy.cf.IEDACfOpenstackConfig;
import org.openpaas.ieda.web.deploy.cf.IEDACfOpenstackRepository;
import org.openpaas.ieda.web.deploy.diego.IEDADiegoAwsConfig;
import org.openpaas.ieda.web.deploy.diego.IEDADiegoAwsRepository;
import org.openpaas.ieda.web.deploy.diego.IEDADiegoOpenstackConfig;
import org.openpaas.ieda.web.deploy.diego.IEDADiegoOpenstackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CommonService {

	@Autowired
	private IEDABootstrapAwsRepository bootstrapAwsRepository;
	@Autowired
	private IEDABootstrapOpenstackRepository bootstrapOpenstackRepository;
	@Autowired
	private IEDABoshAwsRepository boshAwsRepository;
	@Autowired
	private IEDABoshOpenstackRepository boshOpenstackRepository;
	@Autowired
	private IEDACfAwsRepository cfAwsRepository;
	@Autowired
	private IEDACfOpenstackRepository cfOpenstackRepository;
	@Autowired
	private IEDADiegoAwsRepository diegoAwsRepository;
	@Autowired
	private IEDADiegoOpenstackRepository diegoOpenstackRepository;

	public void uploadKeyFile(MultipartHttpServletRequest request) {
		Iterator<String> itr =  request.getFileNames();
		File keyPathFile = new File(LocalDirectoryConfiguration.getSshDir());
		if (!keyPathFile.isDirectory()){
			keyPathFile.mkdir();
		}

		log.debug("request.getFileName : " + request.getFileNames().toString());

		if(itr.hasNext()) {
			MultipartFile mpf = request.getFile(itr.next());
			try {
				String keyFilePath = LocalDirectoryConfiguration.getSshDir() + System.getProperty("file.separator") + mpf.getOriginalFilename();
				byte[] bytes = mpf.getBytes();
				BufferedOutputStream stream =
						new BufferedOutputStream(new FileOutputStream(new File(keyFilePath)));
				stream.write(bytes);
				stream.close();

				log.debug("keyFilePath : " + keyFilePath);

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
		log.info("SERVICE : "+param.getService().toLowerCase());
		log.info("IAAS : "+param.getIaas().toUpperCase());
		if("bootstrap".equals(param.getService().toLowerCase())) {
			log.info("BOOTSTRAP");
			if( "AWS".equals(param.getIaas().toUpperCase()) ){
				IEDABootstrapAwsConfig config = bootstrapAwsRepository.findOne(param.getId());
				log.debug("RESULT [\n"+config+ "\n]" );
				deployLog = config.getDeployLog();
			}
			else if( "OPENSTACK".equals(param.getIaas().toUpperCase()) ){
				IEDABootstrapOpenstackConfig config = bootstrapOpenstackRepository.findOne(param.getId());
				log.debug("RESULT [\n"+config+ "\n]" );
				deployLog = config.getDeployLog();
			}
		}
		else if("bosh".equals(param.getService().toLowerCase())) {
			if( "AWS".equals(param.getIaas().toUpperCase()) ){
				IEDABoshAwsConfig config = boshAwsRepository.findOne(param.getId());
				log.debug("RESULT [\n"+config+ "\n]" );
				deployLog = config.getDeployLog();
			}
			else if( "OPENSTACK".equals(param.getIaas().toUpperCase()) ){
				IEDABoshOpenstackConfig config = boshOpenstackRepository.findOne(param.getId());
				log.debug("RESULT [\n"+config+ "\n]" );
				deployLog = config.getDeployLog();
			}
		}
		else if("cf".equals(param.getService().toLowerCase())) {
			if( "AWS".equals(param.getIaas().toUpperCase()) ){
				IEDACfAwsConfig config = cfAwsRepository.findOne(param.getId());
				log.debug("RESULT [\n"+config+ "\n]" );
				deployLog = config.getDeployLog();
			}
			else if( "OPENSTACK".equals(param.getIaas().toUpperCase()) ){
				IEDACfOpenstackConfig config = cfOpenstackRepository.findOne(param.getId());
				log.debug("RESULT [\n"+config+ "\n]" );
				deployLog = config.getDeployLog();
			}
		}
		else if("diego".equals(param.getService().toLowerCase())) {
			if( "AWS".equals(param.getIaas().toUpperCase()) ){
				IEDADiegoAwsConfig config = diegoAwsRepository.findOne(param.getId());
				log.debug("RESULT [\n"+config+ "\n]" );
				deployLog = config.getDeployLog();
			}
			else if( "OPENSTACK".equals(param.getIaas().toUpperCase()) ){
				IEDADiegoOpenstackConfig config = diegoOpenstackRepository.findOne(param.getId());
				log.debug("RESULT [\n"+config+ "\n]" );
				deployLog = config.getDeployLog();
			}
		}
		return deployLog;
	}
}
