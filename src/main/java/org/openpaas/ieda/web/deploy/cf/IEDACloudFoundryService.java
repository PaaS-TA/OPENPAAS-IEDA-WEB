package org.openpaas.ieda.web.deploy.cf;

import java.util.List;

import org.openpaas.ieda.web.deploy.cf.CfParam.Cf;
import org.springframework.beans.factory.annotation.Autowired;

public class IEDACloudFoundryService {

	@Autowired
	private IEDACfAwsRepository cfAwsRepository;
	
	@Autowired
	private IEDACfOpenstackRepository cfOpenstackRepository;

	public IEDACfAwsConfig saveAwsCfInfo(Cf dto) {
		// TODO Auto-generated method stub
		return null;
	}

}
