package org.openpaas.ieda.web.deploy.cf;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IEDACfService {

	@Autowired
	private IEDACfAwsRepository awsRepository;
	@Autowired
	private IEDACfOpenstackRepository openstackRepository;
	
	public List<CfListDto> listCfs() {
		List<CfListDto> list;
		
		List<IEDACfAwsConfig> listrAws = awsRepository.findAll();
		List<IEDACfOpenstackConfig> listOpenstack  = openstackRepository.findAll();
		
		if( listrAws != null || listOpenstack != null ){
			list = new ArrayList<>();
			int recid = 0;
			if( listrAws != null ){
				for(IEDACfAwsConfig config : listrAws){
					CfListDto dto = new CfListDto();
					dto.setRecid(recid++);
					dto.setId(config.getId());
					dto.setCreateDate(config.getCreatedDate());
					dto.setUpdateDate(config.getUpdatedDate());
					
					dto.setDeployStatus(config.getDeployStatus());
					dto.setDeploymentName(config.getDeploymentName());
					
					dto.setReleaseVersion(config.getReleaseVersion());
					dto.setStemcellName(config.getStemcellName());
					dto.setStemcellVersion(config.getStemcellVersion());
					
					dto.setDirectorUuid(config.getDirectorUuid());
					dto.setSubnetRange(config.getSubnetRange());
					dto.setSubnetGateway(config.getSubnetGateway());
					dto.setSubnetDns(config.getSubnetDns());
					
				}
			}
			
			if ( listOpenstack != null ){
				for(IEDACfOpenstackConfig config : listOpenstack){
					CfListDto dto = new CfListDto();
					dto.setRecid(recid++);
					dto.setId(config.getId());
					dto.setCreateDate(config.getCreatedDate());
					dto.setUpdateDate(config.getUpdatedDate());
					
					dto.setDeployStatus(config.getDeployStatus());
					dto.setDeploymentName(config.getDeploymentName());
					
					dto.setReleaseVersion(config.getReleaseVersion());
					dto.setStemcellName(config.getStemcellName());
					dto.setStemcellVersion(config.getStemcellVersion());
					
					dto.setDirectorUuid(config.getDirectorUuid());
					dto.setSubnetRange(config.getSubnetRange());
					dto.setSubnetGateway(config.getSubnetGateway());
					dto.setSubnetDns(config.getSubnetDns());
				}
			}
		}
		else{
			
		}
		
		
		return null;
	}

}
