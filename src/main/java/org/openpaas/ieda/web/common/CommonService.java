package org.openpaas.ieda.web.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.IOUtils;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.deploy.bootstrap.IEDABootstrapAwsConfig;
import org.openpaas.ieda.web.deploy.bootstrap.IEDABootstrapAwsRepository;
import org.openpaas.ieda.web.deploy.bootstrap.IEDABootstrapOpenstackConfig;
import org.openpaas.ieda.web.deploy.bootstrap.IEDABootstrapOpenstackRepository;
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
				File isKeyFile = new File(keyFilePath);
				BufferedOutputStream stream =
						new BufferedOutputStream(new FileOutputStream(isKeyFile));
				stream.write(bytes);
				stream.close();
				
				isKeyFile.setWritable(false, false);
				isKeyFile.setExecutable(false, false);
				isKeyFile.setReadable(false, true);
				Set<PosixFilePermission> pfp = new HashSet<>();
				pfp.add(PosixFilePermission.OWNER_READ);
				Files.setPosixFilePermissions(Paths.get(keyFilePath), pfp);
				
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
		if("bootstrap".equals(param.getService().toLowerCase())) {
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
		return deployLog;
	}

}
