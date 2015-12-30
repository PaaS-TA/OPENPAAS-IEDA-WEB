<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<link rel="stylesheet" type="text/css" href="/css/progress-step.css" />
<style type="text/css">
.w2ui-popup .w2ui-msg-body {
	background-color: #FFF;
}
</style>
<script type="text/javascript" src="/js/sockjs-0.3.4.js"></script>
<script type="text/javascript" src="/js/stomp.js"></script>
<script type="text/javascript">
	//private var
	var iaas = "";
	var cfId = "";
	var awsInfo = "";
	var openstackInfo = "";
	var uaaInfo = "";
	var consulInfo = "";
	var networkInfo = "";
	var resourceInfo = "";
	var releases = "";
	var stemcells = "";
	var deploymentFile = "";

	//Main View Event
	$(function() {

		// 기본 설치 관리자 정보 조회
		var bDefaultDirector = getDefaultDirector("<c:url value='/directors/default'/>");

		$('#config_cfGrid').w2grid({
			name: 'config_cfGrid',
			header: '<b>CF 목록</b>',
			method: 'GET',
	 		multiSelect: false,
			show: {	
					selectColumn: true,
					footer: true},
			style: 'text-align: center',
			columns:[
			      {field: 'recid', 	caption: 'recid', hidden: true}
				, {field: 'id', caption: 'ID', hidden: true}
				, {field: 'deployStatus', caption: '배포상태', size: '80px', 
					render: function(record) {
			    			if ( record.deployStatus == 'done' )
			    				return '<span class="btn btn-primary" style="width:60px">성공</span>';
			    			else	if ( record.deployStatus == 'error' )
			    				return '<span class="btn btn-primary" style="width:60px">오류</span>';
			    			else	if ( record.deployStatus == 'cancelled' )
			    				return '<span class="btn btn-primary" style="width:60px">취소</span>';
			    			else	if ( record.deployStatus == 'deploying' )
			    				return '<span class="btn btn-primary" style="width:60px">배포중</span>';
							else
			    				return 'N/A';
			    	   }
					}
				, {field: 'deploymentName', caption: '배포명', size: '100px'}
				, {field: 'iaas', caption: 'IaaS', size: '100px'}
				, {field: 'releaseVersion', caption: '릴리즈', size: '100px'}
				, {field: 'stemcell', caption: '스템셀', size: '240px'
					, render:function(record){
							if( record.stemcellName && record.stemcellVersion ){
								return record.stemcellName +"/"+ record.stemcellVersion;
							}
						}
					}
				, {field: 'directorUuid', caption: '설치관리자 UUID', size: '220px'}
				, {field: 'subnetRange', caption: '서브넷 범위', size: '100px'}
				, {field: 'subnetGateway', caption: '게이트웨이', size: '100px'}
				, {field: 'subnetDns', caption: 'DNS', size: '100px'}
				],
			onClick:function(event) {
				var grid = this;
				event.onComplete = function() {
					var sel = grid.getSelection();

					if ( sel == null || sel == "" || checkEmpty(bDefaultDirector)) {
						$('#modifyBtn').attr('disabled', true);
						$('#deleteBtn').attr('disabled', true);
						return;
					}
					else{
						$('#modifyBtn').attr('disabled', false);
						$('#deleteBtn').attr('disabled', false);
					}
				}
			}
		});

		$("#installBtn").click(function() {
			var directorName = $("#directorName").text().toUpperCase();
			
			if (directorName.indexOf("AWS") > 0) {
				iaas = "AWS";
				awsPopup();
			} else if (directorName.indexOf("OPENSTACK") > 0) {
				iaas = "OPENSTACk";
				openstackPopup();
			}
			else{
				selectIaas();
			}
		});

		//Cf 수정
		$("#modifyBtn").click(function() {
			if ($("#modifyBtn").attr('disabled') == "disabled")
				return;
			
			getCfRelease();
			getStamcellList();
			
			w2confirm({
				title : "CF 설치",
				msg : "CF설치 정보를 수정하시겠습니까?",
				yes_text : "확인",
				yes_callBack : function(event) {
					var selected = w2ui['config_cfGrid'].getSelection();

					if (selected.length == 0) {
						w2alert("선택된 정보가 없습니다.", "CF 설치");
						return;
					} else {
						var record = w2ui['config_cfGrid'].get(selected);
						getCfData(record);
						//getCfRelease();
					}
				},
				no_text : "취소"
			});
		});

		//Cf 삭제
		$("#deleteBtn").click(
				function() {
					if ($("#deleteBtn").attr('disabled') == "disabled")
						return;

					var selected = w2ui['config_cfGrid'].getSelection();
					var record = w2ui['config_cfGrid'].get(selected);

					var message = "";

					if (record.deploymentName)
						message = "CF (배포명 : " + record.deploymentName + ")를 삭제하시겠습니까?";
					else
						message = "선택된 CF를 삭제하시겠습니까?";

					w2confirm({
						title : "CF 삭제",
						msg : message,
						yes_text : "확인",
						yes_callBack : function(event) {
							deleteCfPop(record);
						},
						no_text : "취소"
					});
		});
		
		getCfRelease();
		getStamcellList();
		
		doSearch();
	});

	//***** Start Main View Event Function

	//조회기능
	function doSearch() {
		w2ui['config_cfGrid'].load("<c:url value='/deploy/cfList'/>",
				function() {
					doButtonStyle();
				});
	}

	//Iaas Select Confirm
	function selectIaas() {
		//Bootstrap 
		w2confirm({
			width : 500,
			height : 180,
			title : '<b>CF 설치</b>',
			msg : $("#bootSelectBody").html(),
			modal : true,
			yes_text : "확인",
			no_text : "취소",
			yes_callBack : function() {
				structureType = $(
						".w2ui-msg-body input:radio[name='structureType']:checked")
						.val();
				if (structureType) {
					iaas = structureType;

					if (structureType == "AWS")
						awsPopup();
					else
						osCfInfoPopup();
				} else {
					w2alert("설치할 Infrastructure 을 선택하세요");
				}
			}
		});
	}

	//수정 기능 - 데이터 조회
	function getCfData(record) {
		var url = "/cf/" + record.iaas.toLowerCase() + "/" + record.id;
		$.ajax({
			type : "GET",
			url : url,
			contentType : "application/json",
			success : function(data, status) {
				if (data != null && data != "") {
					initSetting();
					if (record.iaas = "AWS"){
						setAwsData(data.contents);
					}
					else
						setOpenstackData(data.contents);
				}
			},
			error : function(request, status, error) {
				var errorResult = JSON.parse(request.responseText);
				w2alert(errorResult.message, "CF 수정");
			}
		});
	}

	//삭제 팝
	function deleteCfPop(record){
	
		var requestParameter = {iaas:record.iaas, id:record.id};업
		
		if ( record.deployStatus == null || record.deployStatus == '' ) {
			// 단순 레코드 삭제
			var url = "/cf/delete";
			$.ajax({
				type : "PUT",
				url : url,
				data : JSON.stringify(requestParameter),
				contentType : "application/json",
				success : function(data, status) {
					doSearch();
				},
				error : function(request, status, error) {
					var errorResult = JSON.parse(request.responseText);
					w2alert(errorResult.message, "CF 삭제");
				}
			});
			
		} else {
			var message = "";
			var body = '<textarea id="deleteLogs" style="width:95%;height:90%;overflow-y:visible;resize:none;background-color: #FFF; margin:2%" readonly="readonly"></textarea>';
			
			w2popup.open({
				width : 610,
				height : 500,
				title : "<b>CF 삭제</b>",
				body  : body,
				buttons : '<button class="btn" style="float: right; padding-right: 15%;" onclick="popupComplete();;">닫기</button>',
				showMax : true,
				onOpen : function(event){
					event.onComplete = function(){
						var socket = new SockJS('/cfDelete');
						deleteClient = Stomp.over(socket); 
						deleteClient.connect({}, function(frame) {
							deleteClient.subscribe('/cf/cfDelete', function(data){
								
					        	var deleteLogs = $(".w2ui-msg-body #deleteLogs");
					        	
					        	var response = JSON.parse(data.body);
					        	
					        	if ( response.messages != null ) {
							       	for ( var i=0; i < response.messages.length; i++) {
							       		deleteLogs.append(response.messages[i] + "\n").scrollTop( deleteLogs[0].scrollHeight );
							       	}
							       	
							       	if ( response.state.toLowerCase() != "started" ) {
							            if ( response.state.toLowerCase() == "done" )	message = message + " 삭제가 완료되었습니다."; 
							    		if ( response.state.toLowerCase() == "error" ) message = message + " 삭제 중 오류가 발생하였습니다.";
							    		if ( response.state.toLowerCase() == "cancelled" ) message = message + " 삭제 중 취소되었습니다.";
							    			
							    		deleteClient.disconnect();
										w2alert(message, "CF 삭제");
							       	}
					        	}
					        	
					        });
							deleteClient.send('/send/cfDelete', {}, JSON.stringify(requestParameter));
					    });
					}
				},
				onClose : function (event){
					event.onComplete= function(){
						$("textarea").text("");
						w2ui['config_cfGrid'].reset();
						deleteClient.disconnect();
						deleteClient = "";
						doSearch();
					}
				}
			});
		}		
	}

	/***************************************
	 **********     Aws Process    **********
	 ***************************************/
	// Aws 팝업
	function awsPopup() {
		$("#awsInfoDiv").w2popup({
			width : 800,
			height : 800,
			title : "CF 설치 (AWS)",
			modal : true,
			onOpen : function(event) {
				event.onComplete = function() {
					$(".w2ui-msg-body input[name='releases']").w2field('list', {items : releases,maxDropHeight : 200,width : 250});
					if (awsInfo != null) {
						$(".w2ui-msg-body input[name='deploymentName']").val(awsInfo.deploymentName);
						$(".w2ui-msg-body input[name='directorUuid']").val(awsInfo.directorUuid);
						
						$(".w2ui-msg-body input[name='domain']").val(awsInfo.domain);
						$(".w2ui-msg-body input[name='description']").val(awsInfo.description);
						$(".w2ui-msg-body input[name='domainOrganization']").val(awsInfo.domainOrganization);

						$(".w2ui-msg-body input[name='proxyStaticIps']").val(awsInfo.proxyStaticIps);
						$(".w2ui-msg-body textarea[name='sslPemPub']").val(awsInfo.sslPemPub);
						$(".w2ui-msg-body textarea[name='sslPemRsa']").val(awsInfo.sslPemRsa);
						if(releases.length > 0 && !checkEmpty(awsInfo.releaseName) && !checkEmpty(awsInfo.releaseVersion) ){
							$(".w2ui-msg-body input[name='releases']").data('selected',{text : awsInfo.releaseName + "/"+ awsInfo.releaseVersion});
						}
					}
					
				}
			},
			onClose : function(event) {
				event.onComplete = function() {
					initSetting();
				}
			}
		});
	}

	// AWS POPUP NEXT BUTTON EVENT
	function saveAwsInfo() {
		// AWSInfo Save
		var releases = $(".w2ui-msg-body input[name='releases']").val();
		
		awsInfo = {
					id 					: (cfId) ? cfId : "",
					iaas 				: "AWS",
					deploymentName 		: $(".w2ui-msg-body input[name='deploymentName']").val(),
					directorUuid 		: $(".w2ui-msg-body input[name='directorUuid']").val(),
					releaseName 		: releases.split("/")[0],
					releaseVersion 		: releases.split("/")[1],
		
					domain 				: $(".w2ui-msg-body input[name='domain']").val(),
					description 		: $(".w2ui-msg-body input[name='description']").val(),
					domainOrganization 	: $(".w2ui-msg-body input[name='domainOrganization']").val(),
		
					proxyStaticIps 		: $(".w2ui-msg-body input[name='proxyStaticIps']").val(),
					sslPemPub 			: $(".w2ui-msg-body textarea[name='sslPemPub']").val(),
					sslPemRsa 			: $(".w2ui-msg-body textarea[name='sslPemRsa']").val()
		}
		
		if (popupValidation()) {
			//ajax AwsInfo Save
			$.ajax({
				type : "PUT",
				url : "/cf/saveAws",
				contentType : "application/json",
				data : JSON.stringify(awsInfo),
				success : function(data, status) {
					cfId = data.id;
					awsUaaPopup();
				},
				error : function(e, status) {
					w2alert("AWS 설정 등록에 실패 하였습니다.", "CF 설치");
				}
			});
		}
	}

	// AWS AwsPopup Data Setting
	function setAwsData(contents) {
		cfId = contents.id;
		iaas = "AWS";
		awsInfo = {
			iaas : "AWS",
			deploymentName 		: contents.deploymentName,
			directorUuid 		: contents.directorUuid,
			releaseName 		: contents.releaseName,
			releaseVersion 		: contents.releaseVersion,
			
			domain 				: contents.domain,
			description 		: contents.description,
			domainOrganization 	: contents.domainOrganization,			
			proxyStaticIps 		: contents.proxyStaticIps,
			sslPemPub 			: contents.sslPemPub,
			sslPemRsa 			: contents.sslPemRsa
		}
		
		uaaInfo = {
				id 					: contents.id,
				loginSecret			: contents.loginSecret,
				signingKey			: contents.signingKey,
				verificationKey		: contents.verificationKey
		}
		
		consulInfo = {
				id 					: contents.id,
				agentCert			: contents.agentCert,
				agentKey			: contents.agentKey,
				caCert				: contents.caCert,
				encryptKeys			: contents.encryptKeys,
				serverCert			: contents.serverCert,
				serverKey			: contents.serverKey
		}

		networkInfo = {
			id : cfId,
			subnetRange 		: contents.subnetRange,
			subnetGateway 		: contents.subnetGateway,
			subnetDns 			: contents.subnetDns,
			subnetReservedFrom 	: contents.subnetReservedFrom,
			subnetReservedTo 	: contents.subnetReservedTo,
			subnetStaticFrom 	: contents.subnetStaticFrom,
			subnetStaticTo 		: contents.subnetStaticTo,
			subnetId 			: contents.subnetId,
			cloudSecurityGroups : contents.cloudSecurityGroups,
		}

		resourceInfo = {
			id 					: contents.id,
			stemcellName 		: contents.stemcellName,
			stemcellVersion 	: contents.stemcellVersion,
			boshPassword 		: contents.boshPassword,
			deploymentFile 		: contents.deploymentFile,
			deployStatus 		: contents.deployStatus,
			deployLog 			: contents.deployLog,
		}
		
		awsPopup();
	}
	
	// AWS UAA POPUP
	function awsUaaPopup(){
		$("#awsUaaInfoDiv").w2popup({
			width : 800,
			height : 500,
			title : "CF 설치 (AWS)",
			modal : true,
			onOpen : function(event) {
				event.onComplete = function() {
					if (uaaInfo != null) {
						$(".w2ui-msg-body input[name='loginSecret']").val(uaaInfo.loginSecret);
						$(".w2ui-msg-body textarea[name='signingKey']").val(uaaInfo.signingKey);
						$(".w2ui-msg-body textarea[name='verificationKey']").val(uaaInfo.verificationKey);
					}					
				}
			},
			onClose : function(event) {
				event.onComplete = function() {
					initSetting();
				}
			}
		});
	}
	
	// AWS UAA save AwsUaa Info
	function saveAwsUaaInfo(type){
		uaaInfo = {
				id : cfId,
				loginSecret : $(".w2ui-msg-body input[name='loginSecret']").val(),
				signingKey	: $(".w2ui-msg-body textarea[name='signingKey']").val(),
				verificationKey : $(".w2ui-msg-body textarea[name='verificationKey']").val()
		}
		
		if( type == 'after'){
			if (popupValidation()) {
				//ajax AwsInfo Save
				$.ajax({
					type : "PUT",
					url : "/cf/saveAwsUaa",
					contentType : "application/json",
					data : JSON.stringify(uaaInfo),
					success : function(data, status) {
						awsConsulPopup();
					},
					error : function(e, status) {
						w2alert("AWS UAA 등록에 실패 하였습니다.", "CF 설치");
					}
				});
			}
		}
		else{
			awsPopup();
		}
	}
	
	// AWS CONSUL Popup
	function awsConsulPopup(){
		$("#awsConsulInfoDiv").w2popup({
			width : 800,
			height : 700,
			title : "CF 설치 (AWS)",
			modal : true,
			showmax : true,
			onOpen : function(event) {
				event.onComplete = function() {
					if (consulInfo != null) {
						$(".w2ui-msg-body textarea[name='agentCert']").val(consulInfo.agentCert);
						$(".w2ui-msg-body textarea[name='agentKey']").val(consulInfo.agentKey);
						$(".w2ui-msg-body textarea[name='caCert']").val(consulInfo.caCert);
						$(".w2ui-msg-body input[name='encryptKeys']").val(consulInfo.encryptKeys);
						$(".w2ui-msg-body textarea[name='serverCert']").val(consulInfo.serverCert);
						$(".w2ui-msg-body textarea[name='serverKey']").val(consulInfo.serverKey);
					}					
				}
			},
			onClose : function(event) {
				event.onComplete = function() {
					initSetting();
				}
			}
		});
	}
	
	// AWS CONSUL save AwsConsul
	function saveAwsConsulInfo(type){
		consulInfo = {
				id 					: cfId,
				agentCert			: $(".w2ui-msg-body textarea[name='agentCert']").val(),
				agentKey			: $(".w2ui-msg-body textarea[name='agentKey']").val(),
				caCert				: $(".w2ui-msg-body textarea[name='caCert']").val(),
				encryptKeys			: $(".w2ui-msg-body input[name='encryptKeys']").val(),
				serverCert			: $(".w2ui-msg-body textarea[name='serverCert']").val(),
				serverKey			: $(".w2ui-msg-body textarea[name='serverKey']").val()
		}
		
		if( type == 'after'){
			if (popupValidation()) {
				//ajax AwsInfo Save
				$.ajax({
					type : "PUT",
					url : "/cf/saveAwsConsul",
					contentType : "application/json",
					data : JSON.stringify(consulInfo),
					success : function(data, status) {
						awsNetworkPopup();
					},
					error : function(e, status) {
						w2alert("AWS CONSUL 등록에 실패 하였습니다.", "CF 설치");
					}
				});
			}
		}
		else{
			awsUaaPopup();
		}
		
	}
	
	// AWS NETWORK POPUP
	function awsNetworkPopup(){
		$("#awsNetworkInfoDiv").w2popup({
			width : 800,
			height : 700,
			title : "CF 설치 (AWS)",
			modal : true,
			showmax : true,
			onOpen : function(event) {
				event.onComplete = function() {
					if (networkInfo != null) {
						$(".w2ui-msg-body input[name='subnetRange']").val(networkInfo.subnetRange);
						$(".w2ui-msg-body input[name='subnetGateway']").val(networkInfo.subnetGateway);
						$(".w2ui-msg-body input[name='subnetDns']").val(networkInfo.subnetDns);
						$(".w2ui-msg-body input[name='subnetReservedFrom']").val(networkInfo.subnetReservedFrom);
						$(".w2ui-msg-body input[name='subnetReservedTo']").val(networkInfo.subnetReservedTo);
						$(".w2ui-msg-body input[name='subnetStaticFrom']").val(networkInfo.subnetStaticFrom);
						$(".w2ui-msg-body input[name='subnetStaticTo']").val(networkInfo.subnetStaticTo);
						$(".w2ui-msg-body input[name='subnetId']").val(networkInfo.subnetId);
						$(".w2ui-msg-body input[name='cloudSecurityGroups']").val(networkInfo.cloudSecurityGroups);
					}					
				}
			},
			onClose : function(event) {
				event.onComplete = function() {
					initSetting();
				}
			}
		});
	}
	// AWS NETWORK save Aws Network
	function saveAwsNetworkInfo(type) {
		networkInfo = {
				id 					: cfId,
				subnetRange			: $(".w2ui-msg-body input[name='subnetRange']").val(),
				subnetGateway		: $(".w2ui-msg-body input[name='subnetGateway']").val(),
				subnetDns			: $(".w2ui-msg-body input[name='subnetDns']").val(),
				subnetReservedFrom	: $(".w2ui-msg-body input[name='subnetReservedFrom']").val(),
				subnetReservedTo	: $(".w2ui-msg-body input[name='subnetReservedTo']").val(),
				subnetStaticFrom	: $(".w2ui-msg-body input[name='subnetStaticFrom']").val(),
				subnetStaticTo		: $(".w2ui-msg-body input[name='subnetStaticTo']").val(),
				subnetId			: $(".w2ui-msg-body input[name='subnetId']").val(),
				cloudSecurityGroups	: $(".w2ui-msg-body input[name='cloudSecurityGroups']").val()
		}

		if (type == 'after') {
			if (popupValidation()) {
				//Server send Cf Info
				$.ajax({
					type : "PUT",
					url : "/cf/saveAwsNetwork",
					contentType : "application/json",
					async : true,
					data : JSON.stringify(networkInfo),
					success : function(data, status) {
						awsResourcePopup();
					},
					error : function(e, status) {
						w2alert("Cf Network 등록에 실패 하였습니다.", "Cf 설치");
					}
				});
			}
		} else if (type == 'before') {
			awsConsulPopup();
		}
	}

	// AWS RESOURCE POPUP
	function awsResourcePopup() {
		$("#awsResourceInfoDiv").w2popup({
			width : 800,
			height : 350,
			title : "CF 설치 (AWS)",
			modal : true,
			showmax : true,
			onOpen : function(event) {
				event.onComplete = function() {
					//if(stemcells.length > 0 ){
					$(".w2ui-msg-body input[name='stemcells']").w2field('list', {items:stemcells, maxDropHeight : 200,width : 250});
					/* }
 					else{
 						getStamcellList();
 					} */
					if (resourceInfo != null) {
						$(".w2ui-msg-body input[name='boshPassword']").val(resourceInfo.boshPassword);
						if(stemcells.length > 0 && !checkEmpty(resourceInfo.stemcellName) &&  !checkEmpty(resourceInfo.stemcellVersion) ){
							$(".w2ui-msg-body input[name='stemcells']").data('selected',{text : resourceInfo.stemcellName + "/"+ resourceInfo.stemcellVersion});
						}
					} 
				}
			},
			onClose : function(event) {
				event.onComplete = function() {
					initSetting();
				}
			}
		});
	}

	// AWS RESOURCE save Resource Info
	function saveAwsResourceInfo(type) {

		var stemcells = $(".w2ui-msg-body input[name='stemcells']").val().split("/");
		resourceInfo = {
				id 					: cfId,
				stemcellName 		: stemcells[0],
				stemcellVersion 	: stemcells[1],
				boshPassword 		: $(".w2ui-msg-body input[name='boshPassword']").val()
		}

		if (type == 'after') {
			if(popupValidation()){		
				//Server send Cf Info
				$.ajax({
					type : "PUT",
					url : "/cf/saveAwsResource",
					contentType : "application/json",
					async : true,
					data : JSON.stringify(resourceInfo),
					success : function(data, status) {
						deploymentFile = data.content.deploymentFile;
						deployPopup();
					},
					error : function(e, status) {
						w2alert("Cf Resource 등록에 실패 하였습니다.", "Cf 설치");
					}
				});
			}
		} else if (type == 'before') {
			awsNetworkPopup();
		}
	}
	/********************************* AWS END ********************************************/
	
	//OPENSTACK modify Openstack Data Setting
	function setOpenstackData(contents) {
		cfId = contents.id;
		iaas = "OPENSTACK";
		
		awsInfo = {
			iaas : "OPENSTACK",
			deploymentName 		: contents.deploymentName,
			directorUuid 		: contents.directorUuid,
			releaseName 		: contents.releaseName,
			releaseVersion 		: contents.releaseVersion,
			
			domain 				: contents.domain,
			description 		: contents.description,
			domainOrganization 	: contents.domainOrganization,			
			proxyStaticIps 		: contents.proxyStaticIps,
			sslPemPub 			: contents.sslPemPub,
			sslPemRsa 			: contents.sslPemRsa
		}
		
		uaaInfo = {
				id 					: contents.id,
				loginSecret			: contents.loginSecret,
				signingKey			: contents.signingKey,
				verificationKey		: contents.verificationKey
		}
		
		consulInfo = {
				id 					: contents.id,
				agentCert			: contents.agentCert,
				agentKey			: contents.agentKey,
				caCert				: contents.caCert,
				encryptKeys			: contents.encryptKeys,
				serverCert			: contents.serverCert,
				serverKey			: contents.serverKey
		}

		networkInfo = {
			id : cfId,
			subnetRange 		: contents.subnetRange,
			subnetGateway 		: contents.subnetGateway,
			subnetDns 			: contents.subnetDns,
			subnetReservedFrom 	: contents.subnetReservedFrom,
			subnetReservedTo 	: contents.subnetReservedTo,
			subnetStaticFrom 	: contents.subnetStaticFrom,
			subnetStaticTo 		: contents.subnetStaticTo,
			cloudNetId 			: contents.cloudNetId,
			cloudSecurityGroups : contents.cloudSecurityGroups,
		}

		resourceInfo = {
			id 					: contents.id,
			stemcellName 		: contents.stemcellName,
			stemcellVersion 	: contents.stemcellVersion,
			boshPassword 		: contents.boshPassword,
			deploymentFile 		: contents.deploymentFile,
			deployStatus 		: contents.deployStatus,
			deployLog 			: contents.deployLog,
		}
		
		openstackPopup();
	}
	
	//OPENSTACK OpenstackPopup Data Setting
	function openstackPopup(){
		$("#openstackInfoDiv").w2popup({
			width 	: 670,
			height	: 550,
			title   : "CF설치 (오픈스택)",
			modal	: true,
			onOpen:function(event){
				event.onComplete = function(){				
					if(openstackInfo != ""){
						$(".w2ui-msg-body input[name='authUrl']").val(openstackInfo.authUrl);
						$(".w2ui-msg-body input[name='tenant']").val(openstackInfo.tenant);
						$(".w2ui-msg-body input[name='userName']").val(openstackInfo.userName);
						$(".w2ui-msg-body input[name='apiKey']").val(openstackInfo.apiKey);
						$(".w2ui-msg-body input[name='defaultSecurityGroups']").val(openstackInfo.defaultSecurityGroups);
						$(".w2ui-msg-body input[name='privateKeyName']").val(openstackInfo.privateKeyName);
						$(".w2ui-msg-body input[name='privateKeyPath']").val(openstackInfo.privateKeyPath);
					}
					//$(".w2ui-msg-body input[name='releases']").w2field('list', {items : releases,maxDropHeight : 200,width : 250});
					if (openstackInfo != null) {
						$(".w2ui-msg-body input[name='deploymentName']").val(openstackInfo.deploymentName);
						$(".w2ui-msg-body input[name='directorUuid']").val(openstackInfo.directorUuid);
						
						$(".w2ui-msg-body input[name='domain']").val(openstackInfo.domain);
						$(".w2ui-msg-body input[name='description']").val(openstackInfo.description);
						$(".w2ui-msg-body input[name='domainOrganization']").val(openstackInfo.domainOrganization);

						$(".w2ui-msg-body input[name='proxyStaticIps']").val(openstackInfo.proxyStaticIps);
						$(".w2ui-msg-body textarea[name='sslPemPub']").val(openstackInfo.sslPemPub);
						$(".w2ui-msg-body textarea[name='sslPemRsa']").val(openstackInfo.sslPemRsa);
					}
					getCfRelease();
				}
			},
			onClose : initSetting
		});
	}

	function saveOpenstackInfo() {
		openstackInfo = {
			id : cfId,
			iaas : "OPENSTACK",
			authUrl : $(".w2ui-msg-body input[name='authUrl']").val(),
			tenant : $(".w2ui-msg-body input[name='tenant']").val(),
			userName : $(".w2ui-msg-body input[name='userName']").val(),
			apiKey : $(".w2ui-msg-body input[name='apiKey']").val(),
			defaultSecurityGroups : $(
					".w2ui-msg-body input[name='defaultSecurityGroups']").val(),
			privateKeyName : $(".w2ui-msg-body input[name='privateKeyName']")
					.val(),
			privateKeyPath : $(".w2ui-msg-body input[name='privateKeyPath']")
					.val()

		}

		if (checkEmpty($(".w2ui-msg-body input[name='authUrl']").val())) {
			w2alert("Identity API인증 링크를 입력하세요.", "", function() {
				$(".w2ui-msg-body input[name='authUrl']").focus();
			});
			return;
		}

		if (checkEmpty($(".w2ui-msg-body input[name='tenant']").val())) {
			w2alert("Tenant을 입력하세요.", "", function() {
				$(".w2ui-msg-body input[name='tenant']").focus();
			});
			return;
		}

		if (checkEmpty($(".w2ui-msg-body input[name='userName']").val())) {
			w2alert("계정명을 입력하세요.", "", function() {
				$(".w2ui-msg-body input[name='userName']").focus();
			});
			return;
		}

		if (checkEmpty($(".w2ui-msg-body input[name='apiKey']").val())) {
			w2alert("계정 비밀번호를 입력하세요.", "", function() {
				$(".w2ui-msg-body input[name='apiKey']").focus();
			});
			return;
		}

		if (checkEmpty($(".w2ui-msg-body input[name='defaultSecurityGroups']")
				.val())) {
			w2alert("Security Group를 입력하세요.", "",
					function() {
						$(".w2ui-msg-body input[name='defaultSecurityGroups']")
								.focus();
					});
			return;
		}

		if (checkEmpty($(".w2ui-msg-body input[name='privateKeyName']").val())) {
			w2alert("Key Pair명을 입력하세요.", "", function() {
				$(".w2ui-msg-body input[name='privateKeyName']").focus();
			});
			return;
		}

		if ($(".w2ui-msg-body input[name='keyPathFile']").val() != null) {
			var keyPathFile = $(".w2ui-msg-body input[name='keyPathFile']")
					.val().split('.').pop().toLowerCase();

			if ($.inArray(keyPathFile, [ 'pem' ]) == -1) {
				w2alert("Private Key 파일을 업로드 또는 목록에서 선택하세요.", "CF 설치");
				return;
			}
		}

		$.ajax({
			type : "PUT",
			url : "/cf/saveOpenstackInfo",
			contentType : "application/json",
			async : true,
			data : JSON.stringify(openstackInfo),
			success : function(data, status) {
				keyPathFileUpload();
			},
			error : function(e, status) {
				w2alert("오픈스택 정보 등록 실패하였습니다.", "CF 설치");
			}
		});
	}

	function saveOsCfInfo(type) {
		cfInfo = {
			id : cfId,
			deploymentName : $(".w2ui-msg-body input[name='deploymentName']")
					.val(),
			directorUuid : $(".w2ui-msg-body input[name='directorUuid']").val(),
			releaseVersion : $(".w2ui-msg-body input[name='releaseVersion']")
					.val()
		}

		if (type == 'after') {
			if (checkEmpty(cfInfo.deploymentName)) {
				w2alert("배포명을 입력하세요.", "", function() {
					$(".w2ui-msg-body input[name='deploymentName']").focus()
				});
				return;
			}

			if (checkEmpty(cfInfo.directorUuid)) {
				w2alert("설치관리자 UUID를 입력하세요.", "", function() {
					$(".w2ui-msg-body input[name='directorUuid']").focus();
				});
				return;
			}

			if (checkEmpty(cfInfo.releaseVersion)) {
				w2alert("CF 릴리즈를 선택하세요.", "", function() {
					$(".w2ui-msg-body input[name='releaseVersion']").focus();
				});
				return;
			}

			$.ajax({
				type : "PUT",
				url : "/cf/saveOsCfInfo",
				contentType : "application/json",
				async : true,
				data : JSON.stringify(cfInfo),
				success : function(data, status) {
					cfId = data.id;
					osNetworkInfoPopup();
				},
				error : function(e, status) {
					w2alert("기본정보 등록 실패하였습니다.", "CF 설치");
				}
			});
		} else {
			openstackPopup();
		}
	}

	function osNetworkInfoPopup() {
		$("#osNetworkInfoDiv")
				.w2popup(
						{
							width : 670,
							height : 550,
							modal : true,
							onOpen : function(event) {
								event.onComplete = function() {
									if (networkInfo != "") {
										$(
												".w2ui-msg-body input[name='publicStaticIp']")
												.val(networkInfo.publicStaticIp);
										$(
												".w2ui-msg-body input[name='subnetId']")
												.val(networkInfo.subnetId);
										$(
												".w2ui-msg-body input[name='subnetStaticFrom']")
												.val(
														networkInfo.subnetStaticFrom);
										$(
												".w2ui-msg-body input[name='subnetStaticTo']")
												.val(networkInfo.subnetStaticTo);
										$(
												".w2ui-msg-body input[name='subnetRange']")
												.val(networkInfo.subnetRange);
										$(
												".w2ui-msg-body input[name='subnetGateway']")
												.val(networkInfo.subnetGateway);
										$(
												".w2ui-msg-body input[name='subnetDns']")
												.val(networkInfo.subnetDns);
									}
								}
							},
							onClose : initSetting
						});
	}

	function saveOsNetworkInfo(type) {
		networkInfo = {
			id : cfId,
			publicStaticIp : $(".w2ui-msg-body input[name='publicStaticIp']")
					.val(),
			subnetId : $(".w2ui-msg-body input[name='subnetId']").val(),
			subnetStaticFrom : $(
					".w2ui-msg-body input[name='subnetStaticFrom']").val(),
			subnetStaticTo : $(".w2ui-msg-body input[name='subnetStaticTo']")
					.val(),
			subnetRange : $(".w2ui-msg-body input[name='subnetRange']").val(),
			subnetGateway : $(".w2ui-msg-body input[name='subnetGateway']")
					.val(),
			subnetDns : $(".w2ui-msg-body input[name='subnetDns']").val()
		}

		if (type == 'after') {
			if (checkEmpty(networkInfo.publicStaticIp)) {
				w2alert("Floating IP를 입력하세요", "", function() {
					$(".w2ui-msg-body input[name='publicStaticIp']").focus();
				});
				return;
			}

			if (checkEmpty(networkInfo.subnetId)) {
				w2alert("Subnet ID를 입력하세요.", "", function() {
					$(".w2ui-msg-body input[name='subnetId']").focus();
				});
				return;
			}

			if (checkEmpty(networkInfo.subnetStaticFrom)) {
				w2alert("Static IP 구간을 입력하세요.(From)", "", function() {
					$(".w2ui-msg-body input[name='subnetStaticFrom']").focus();
				});
				return;
			}

			if (checkEmpty(networkInfo.subnetStaticTo)) {
				w2alert("Static IP 구간을 입력하세요.(To)", "", function() {
					$(".w2ui-msg-body input[name='subnetStaticTo']").focus();
				});
				return;
			}

			if (checkEmpty(networkInfo.subnetRange)) {
				w2alert("Subnet Range를 입력하세요.", "", function() {
					$(".w2ui-msg-body input[name='subnetRange']").focus();
				});
				return;
			}
			if (checkEmpty(networkInfo.subnetGateway)) {
				w2alert("Gateway IP를 입력하세요.", "", function() {
					$(".w2ui-msg-body input[name='subnetGateway']").focus();
				});
				return;
			}

			if (checkEmpty(networkInfo.subnetDns)) {
				w2alert("DNS정보를 입력하세요.", "", function() {
					$(".w2ui-msg-body input[name='subnetDns']").focus();
				});
				return;
			}

			$.ajax({
				type : "PUT",
				url : "/cf/saveOsNetworkInfo",
				contentType : "application/json",
				async : true,
				data : JSON.stringify(networkInfo),
				success : function(data, status) {
					osResourceInfoPopup();
				},
				error : function(e, status) {
					w2alert("네트워크 정보 등록 실패하였습니다.", "CF 설치");
				}
			});
		} else {
			osCfInfoPopup();
		}
	}

	function osResourceInfoPopup() {
		$("#osResourceInfoDiv")
				.w2popup(
						{
							width : 670,
							height : 450,
							modal : true,
							onOpen : function(event) {
								event.onComplete = function() {
									$(".w2ui-msg-body input[name='stemcells']")
											.w2field('list', {
												items : stemcells,
												maxDropHeight : 200,
												width : 250
											});
									if (resourceInfo != "") {
										if (!checkEmpty(resourceInfo.stemcellName)) {
											$(
													".w2ui-msg-body input[name='stemcells']")
													.data(
															'selected',
															{
																text : resourceInfo.stemcellName
																		+ "/"
																		+ resourceInfo.stemcellVersion
															});
										}
										$(
												".w2ui-msg-body input[name='cloudInstanceType']")
												.val(
														resourceInfo.cloudInstanceType);
										$(
												".w2ui-msg-body input[name='cfPassword']")
												.val(resourceInfo.cfPassword);
									}
								}
							},
							onClose : initSetting
						});
	}

	function saveOsResourceInfo(type) {
		var stemcellInfo = $(".w2ui-msg-body input[name='stemcells']").val()
				.split("/");
		resourceInfo = {
			id : cfId,
			stemcellName : stemcellInfo[0],
			stemcellVersion : stemcellInfo[1],
			cloudInstanceType : $(
					".w2ui-msg-body input[name='cloudInstanceType']").val(),
			cfPassword : $(".w2ui-msg-body input[name='cfPassword']").val()
		}

		if (type == 'after') {

			if (checkEmpty(stemcellInfo)) {
				w2alert("Stemcell을 선택하세요.");
				return;
			}

			if (checkEmpty(resourceInfo.cloudInstanceType)) {
				w2alert("인스턴스 유형을 입력하세요.", "",
						function() {
							$(".w2ui-msg-body input[name='cloudInstanceType']")
									.focus();
						});
				return;
			}

			if (checkEmpty(resourceInfo.cfPassword)) {
				w2alert("VM 인스턴스의 비밀번호를 입력하세요.", "", function() {
					$(".w2ui-msg-body input[name='cfPassword']").focus();
				});
				return;
			}

			$.ajax({
				type : "PUT",
				url : "/cf/saveOsResourceInfo",
				contentType : "application/json",
				async : true,
				data : JSON.stringify(resourceInfo),
				success : function(data, status) {
					deploymentFile = data.content.deploymentFile;
					deployPopup();
				},
				error : function(e, status) {
					w2alert("리소스 정보 등록 실패하였습니다.", "CF 설치");
				}
			});
		} else {
			osNetworkInfoPopup();
		}
	}

	/*******************************  OPENSTACK END  ****************************************/
	
	// DEPLOY Confirm
	function cfDeploy(type) {
		//Deploy 단에서 저장할 데이터가 있는지 확인 필요
		//Confirm 설치하시겠습니까?
		if (type == 'before' && iaas == "AWS") {
			resourcePopup();
			return;
		} else if (type == 'before' && iaas == "OPENSTACK") {
			osResourceInfoPopup();
			return;
		}

		w2confirm({
			msg : "설치하시겠습니까?",
			title : w2utils.lang('CF 설치'),
			yes_text : "예",
			no_text : "아니오",
			yes_callBack : installPopup
		});
	}
	
	// DEPLOY POPUP
	function deployPopup() {
		var deployDiv = (iaas == "AWS") ? $("#deployDiv"):$("#osdeployDiv");
		deployDiv.w2popup({
			width : 800,
			height : 470,
			modal : true,
			showMax : true,
			onClose : initSetting,
			onOpen : function(event) {
				event.onComplete = function() {
					getDeployInfo();
				}
			}
		});
	}

	// DEPLOY Get Deployment File
	function getDeployInfo() {
		$.ajax({
			type : "POST",
			url : "/cf/getDeployInfo",
			contentType : "application/json",
			async : true,
			data : JSON.stringify({
				deploymentFile : deploymentFile
			}),
			success : function(data, status) {
				if (status == "success") {
					$(".w2ui-msg-body #deployInfo").text(data);
				} else if (status == "204") {
					w2alert("배포파일이 존재하지 않습니다.", "CF 설치");
				}
			},
			error : function(e, status) {
				w2alert("Temp 파일을 가져오는 중 오류가 발생하였습니다. ", "CF 설치");
			}
		});
	}

	// INSTALL POPUP
	function installPopup(){
		
		var installDiv = (iaas == 'AWS') ? $("#installDiv") : $("#osInstallDiv");
		var deploymentName = (iaas == 'AWS') ? awsInfo.deploymentName : openstackInfo.deploymentName;
		var message = "CF(배포명:" + deploymentName +  ") ";
		
		var requestParameter = {
				id : cfId,
				iaas: iaas
		};
		
		installDiv.w2popup({
			width 	: 800,
			height 	: 520,
			modal	: true,
			showMax : true,
			onOpen : function(event){
				event.onComplete = function(){
					//deployFileName
					var socket = new SockJS('/cfInstall');
					installClient = Stomp.over(socket); 
					installClient.connect({}, function(frame) {
						console.log('Connected Frame : ' + frame);
				        installClient.subscribe('/cf/cfInstall', function(data){
				        	
				        	var installLogs = $(".w2ui-msg-body #installLogs");
				        	
				        	var response = JSON.parse(data.body);
				        	
				        	if ( response.messages != null ) {
						       	for ( var i=0; i < response.messages.length; i++) {
						        	installLogs.append(response.messages[i] + "\n").scrollTop( installLogs[0].scrollHeight );
						       	}
						       	
						       	if ( response.state.toLowerCase() != "started" ) {
						            if ( response.state.toLowerCase() == "done" )	message = message + " 설치가 완료되었습니다."; 
						    		if ( response.state.toLowerCase() == "error" ) message = message + " 설치 중 오류가 발생하였습니다.";
						    		if ( response.state.toLowerCase() == "cancelled" ) message = message + " 설치 중 취소되었습니다.";
						    			
						    		installClient.disconnect();
									w2alert(message, "CF 설치");
						       	}
				        	}

				        });
				        installClient.send('/send/cfInstall', {}, JSON.stringify(requestParameter));
				    });
				}
			},
			onClose : initSetting
		});
	}

	//CF 릴리즈 조회
	function getCfRelease() {
		$.ajax({
			type : "GET",
			url : "/release/getReleaseList/cf",
			contentType : "application/json",
			async : true,
			success : function(data, status) {
				console.log("CF Releases List");
				releases = new Array();
				data.records.map(function(obj) {
					releases.push(obj.name + "/" + obj.version);
				});
			},
			error : function(e, status) {
				w2alert("Cf Release List 를 가져오는데 실패하였습니다.", "CF 설치");
			}
		});
	}
	
	// RELEASE release value setgting
	function setReleaseData(){
		if( iaas.toUpperCase() == "AWS" && !checkEmpty(awsInfo.releaseName) && !checkEmpty(awsInfo.releaseVersion) ){
			$(".w2ui-msg-body input[name='releases']").data('selected',{text : awsInfo.releaseName + "/"+ awsInfo.releaseVersion});
		}
		else if( iaas.toUpperCase() == "OPENSTACK" &&  !checkEmpty(openstackInfo.releaseName) &&  !checkEmpty(openstackInfo.releaseVersion) ){
			$(".w2ui-msg-body input[name='releases']").data('selected',{text : openstackInfo.releaseName + "/"+ openstackInfo.releaseVersion});
		}
	}
	
	// 스템셀 조회
	function getStamcellList() {
		$.ajax({
			type : "GET",
			url : "/stemcells",
			contentType : "application/json",
			success : function(data, status) {
				console.log("Stemcell List");
				stemcells = new Array();
				data.records.map(function(obj) {
					stemcells.push(obj.name + "/" + obj.version);
				});
			},
			error : function(e, status) {
				w2alert("Stemcell List 를 가져오는데 실패하였습니다.", "CF 설치");
			}
		});
	}
	
	// 스템셀 stemcell value setgting
	function setStemcellData(){
		if( !checkEmpty(resourceInfo.stemcellName) && !checkEmpty(resourceInfo.stemcellVersion) ){
			$(".w2ui-msg-body input[name='stemcells']").data('selected',{text : resourceInfo.stemcellName + "/"+ resourceInfo.stemcellVersion});
		}
	}
	
	
	//전역변수 초기화
	function initSetting() {
		cfId = "";
		awsInfo = "";
		networkInfo = "";
		resourceInfo = "";
	}

	//팝업 종료시 이벤트
	function popupComplete() {
		initSetting();
		w2popup.close();
		gridReload();
	}

	//버튼 스타일 변경
	function doButtonStyle() {
		//Button Style init
		$('#modifyBtn').attr('disabled', true);
		$('#deleteBtn').attr('disabled', true);
	}

	// 그리드 재조회
	function gridReload() {
		//console.log("delete complete!");
		w2ui['config_cfGrid'].clear();
		doSearch();
	}

	//다른페이지 이동시 호출
	function clearMainPage() {
		$().w2destroy('config_cfGrid');
	}

	//화면 리사이즈시 호출
	$(window).resize(function() {
		setLayoutContainerHeight();
	});

</script>

<div id="main">
	<div class="page_site">
		설치관리자 환경설정 > <strong>Cf 설치</strong>
	</div>

	<!-- 설치 관리자 -->
	<div class="title">설치 관리자</div>

	<table class="tbl1" border="1" cellspacing="0">
		<tr>
			<th width="18%" class="th_fb">관리자 이름</th>
			<td class="td_fb"><b id="directorName"></b></td>
			<th width="18%" class="th_fb">관리자 계정</th>
			<td class="td_fb"><b id="userId"></b></td>
		</tr>
		<tr>
			<th width="18%">관리자 URL</th>
			<td><b id="directorUrl"></b></td>
			<th width="18%">관리자 UUID</th>
			<td><b id="directorUuid"></b></td>
		</tr>
	</table>

	<!-- Cf 목록-->
	<div class="pdt20">
		<div class="title fl">CF 목록</div>
		<div class="fr">
			<!-- Btn -->
			<span id="installBtn" class="btn btn-primary" style="width: 120px">설&nbsp;&nbsp;치</span>
			&nbsp; <span id="modifyBtn" class="btn btn-info" style="width: 120px">수&nbsp;&nbsp;정</span>
			&nbsp; <span id="deleteBtn" class="btn btn-danger"
				style="width: 120px">삭&nbsp;&nbsp;제</span>
			<!-- //Btn -->
		</div>
	</div>
	<div id="config_cfGrid" style="width: 100%; height: 500px"></div>
</div>

<!-- IaaS 설정 DIV -->
<div id="bootSelectBody" style="width: 100%; height: 80px;"
	hidden="true">
	<div class="w2ui-lefted" style="text-align: left;">IaaS를 선택하세요</div>
	<div class="col-sm-9">
		<div class="btn-group" data-toggle="buttons">
			<label style="width: 100px; margin-left: 40px;"> <input
				type="radio" name="structureType" id="type1" value="AWS"
				checked="checked" tabindex="1" /> &nbsp;AWS
			</label> <label style="width: 130px; margin-left: 50px;"> <input
				type="radio" name="structureType" id="type2" value="OPENSTACK"
				tabindex="2" /> &nbsp;OPENSTACK
			</label>
		</div>
	</div>
</div>

<!-- Start AWS Popup  -->

	<!-- AWS  설정 DIV -->
	<div id="awsInfoDiv" style="width: 100%; height: 100%;" hidden="true">
		<div rel="title">
			<b>CF 설치</b>
		</div>
		<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 3%;display:inline-block;width: 97%;">
				<ul class="progressStep_7">
					<li class="active">AWS 정보</li>
					<li class="before">UAA 정보</li>
					<li class="before">CONSUL 정보</li>
					<li class="before">네트워크 정보</li>
					<li class="before">리소스 정보</li>
					<li class="before">배포파일 정보</li>
					<li class="before">설치</li>
				</ul>
			</div>
			<div style="margin:15px 1.5%;">▶ AWS정보 설정</div>
			<div class="w2ui-page page-0" style="padding-left: 5%;">
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">배포 명</label>
					<div>
						<input name="deploymentName" type="text" style="float: left; width: 60%;" required placeholder="배포 명을 입력하세요." />
						<div class="isMessage"></div>
					</div>
				</div>
	
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">설치관리자 UUID</label>
					<div>
						<input name="directorUuid" type="text" style="float: left; width: 60%;" required placeholder="설치관리자 UUID<를 입력하세요." />
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">CF 릴리즈</label>
					<div>
						<input name="releases" type="list" style="float: left; width: 60%;" required placeholder="CF 릴리즈를 선택하세요." />
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">도메인</label>
					<div>
						<input name="domain" type="text" style="float: left; width: 60%;" required placeholder="도메인을 입력하세요. 예)cfdoamin.com" />
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">도메인 설명</label>
					<div>
						<input name="description" type="text" style="float: left; width: 60%;" required placeholder="도메인에 대한 설명을 입력하세요." />
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">도메인 그룹</label>
					<div>
						<input name="domainOrganization" type="text" style="float: left; width: 60%;" required placeholder="도메인 그룹명을 입력하세요." />
						<div class="isMessage"></div>
					</div>
				</div>
				
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">CF 프록시 서버 공인 IP</label>
					<div>
						<input name="proxyStaticIps" type="text" style="float: left; width: 60%;" required placeholder="프록시 서버 공인 IP를 입력하세요." />
						<div class="isMessage"></div>
					</div>
				</div>
	
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">프록시 서버 공개키</label>
					<div>
						<textarea name="sslPemPub" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
							required placeholder="프록시 서버 공개키를 입력하세요." onfocus="$(this).w2overlay($(this).val());"></textarea>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">프록시 서버 개인키</label>
					<div>
						<textarea name="sslPemRsa" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
							required placeholder="프록시 서버 개인키를 입력하세요." onfocus="$(this).w2overlay($(this).val());"></textarea>
					</div>
				</div>
			</div>
			<br />
			<div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveAwsInfo();">다음>></button>
			</div>
		</div>
	</div>
	
	<!-- AWS UAA 설정 DIV -->
	<div id="awsUaaInfoDiv" style="width: 100%; height: 100%;" hidden="true">
		<div rel="title"><b>CF 설치</b></div>
		<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 3%;display:inline-block;width: 97%;">
				<ul class="progressStep_7">
					<li class="pass">AWS 정보</li>
					<li class="active">UAA 정보</li>
					<li class="before">CONSUL 정보</li>
					<li class="before">네트워크 정보</li>
					<li class="before">리소스 정보</li>
					<li class="before">배포파일 정보</li>
					<li class="before">설치</li>
				</ul>
			</div>
			<div style="margin:15px 1.5%;">▶ UAA 정보 설정</div>
			<div class="w2ui-page page-0" style="padding-left: 5%;">
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">로그인 비밀번호</label>
					<div>
						<input name="loginSecret" type="text" style="float: left; width: 60%;" required placeholder="로그인 비밀번호를 입력하세요." />
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">개인키</label>
					<div>
						<div>
						<textarea name="signingKey" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
							required placeholder="개인키를 입력하세요." onblur="$(this).w2overlay($(this).val());"></textarea>
					</div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">공개키</label>
					<div>
						<div>
						<textarea name="verificationKey" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
							required placeholder="공개키를 입력하세요." onblur="$(this).w2overlay($(this).val());"></textarea>
					</div>
					</div>
				</div>
			</div>
			<br />
			<div class="w2ui-buttons" rel="buttons" hidden="true"> 
				<button class="btn" style="float: left;" onclick="saveAwsUaaInfo('before');">이전</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveAwsUaaInfo('after');">다음>></button>
			</div>
		</div>
	</div>
	
	<!-- AWS CONSUL 설정 DIV -->
	<div id="awsConsulInfoDiv" style="width: 100%; height: 100%;" hidden="true">
		<div rel="title"><b>CF 설치</b></div>
		<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 3%;display:inline-block;width: 97%;">
				<ul class="progressStep_7">
					<li class="pass">AWS 정보</li>
					<li class="pass">UAA 정보</li>
					<li class="active">CONSUL 정보</li>
					<li class="before">네트워크 정보</li>
					<li class="before">리소스 정보</li>
					<li class="before">배포파일 정보</li>
					<li class="before">설치</li>
				</ul>
			</div>
			<div style="margin:15px 1.5%;">▶ CONSUL 정보 설정</div>
			<div class="w2ui-page page-0" style="padding-left: 5%;">
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">암호화 키</label>
					<div>
						<input name="encryptKeys" type="text" style="float: left; width: 60%;" required placeholder="암호화 키를 입력하세요." />
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">에이전트 인증키</label>
					<div>
						<textarea name="agentCert" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
							required placeholder="에이전트 인증키를 입력하세요." onblur="$(this).w2overlay($(this).val());"></textarea>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">에이전트 개인키</label>
					<div>
						<textarea name="agentKey" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
							required placeholder="에이전트 개인키를 입력하세요." onblur="$(this).w2overlay($(this).val());"></textarea>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">CA 인증키</label>
					<div>
						<textarea name="caCert" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
							required placeholder="CA 인증키를 입력하세요." onblur="$(this).w2overlay($(this).val());"></textarea>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">서버 인증키</label>
					<div>
						<textarea name="serverCert" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
							required placeholder="서버 인증키를 입력하세요." onblur="$(this).w2overlay($(this).val());"></textarea>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">서버 공개키</label>
					<div>
						<textarea name="serverKey" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
							required placeholder="서버 공개키를 입력하세요." onblur="$(this).w2overlay($(this).val());"></textarea>
					</div>
				</div>
			</div>
			<br />
			<div class="w2ui-buttons" rel="buttons" hidden="true"> 
				<button class="btn" style="float: left;" onclick="saveAwsConsulInfo('before');">이전</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveAwsConsulInfo('after');">다음>></button>
			</div>
		</div>
	</div>
	<!-- Network 설정 DIV -->
	<div id="awsNetworkInfoDiv" style="width: 100%; height: 100%;" hidden="true">
		<div rel="title">
			<b>CF 설치</b>
		</div>
		<div rel="body"
			style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 3%;display:inline-block;width: 97%;">
				<ul class="progressStep_7">
					<li class="pass">AWS 정보</li>
					<li class="pass">UAA 정보</li>
					<li class="pass">CONSUL 정보</li>
					<li class="active">네트워크 정보</li>
					<li class="before">리소스 정보</li>
					<li class="before">배포파일 정보</li>
					<li class="before">설치</li>
				</ul>
			</div>
			<div style="margin:15px 1.5%;">▶ 네트워크정보 설정</div>
			<div class="w2ui-page page-0" style="padding-left: 5%;">
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">Subnet Range(CIDR)</label>
					<div>
						<input name="subnetRange" type="text" style="float: left; width: 60%;" required placeholder="예) 10.0.0.0/24" />
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">Gateway IP</label>
					<div>
						<input name="subnetGateway" type="url" style="float: left; width: 60%;" required placeholder="예) 10.0.0.1" />
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">DNS</label>
					<div>
						<input name="subnetDns" type="text" style="float: left; width: 60%;" required placeholder="예) 8.8.8.8" />
						<div class="isMessage"></div>
					</div>
				</div>			
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">Subnet Id</label>
					<div>
						<input name="subnetId" type="text" style="float: left; width: 60%;" required placeholder="예) subnet-XXXXXX" />
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">시큐리티 그룹명</label>
					<div>
						<input name="cloudSecurityGroups" type="text" style="float: left; width: 60%;" required placeholder="예) cf-security" />
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">VM 할당 IP대역(최소 14개)</label>
					<div>
						<div style="display: inline-block; width: 60%;">
							<span style="float: left; width: 45%;">
								<input name="subnetStaticFrom" type="url" style="float:left;width:100%;" placeholder="예) 10.0.0.100" />
							</span> 
							<span style="float: left; width: 10%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
							<span style="float: left; width: 45%;">
								<input name="subnetStaticTo" type="url" style="float:left;width:100%;" placeholder="예) 10.0.0.106" />
							</span>
						</div>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">할당된 IP대역</label>
					<div>
						<div style="display: inline-block; width: 60%;">
							<span style="float: left; width: 45%;">
								<input name="subnetReservedFrom" id="subnetStaticFrom" type="url" style="float:left;width:100%;" placeholder="예) 10.0.0.100" />
							</span> 
							<span style="float: left; width: 10%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
							<span style="float: left; width: 45%;">
								<input name="subnetReservedTo" id="subnetStaticTo" type="url" style="float:left;width:100%;" placeholder="예) 10.0.0.106" />
							</span>
						</div>
						<div class="isMessage"></div>
					</div>
				</div>
				
			</div>
			<br />
			<div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: left;" onclick="saveAwsNetworkInfo('before');">이전</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveAwsNetworkInfo('after');">다음>></button>
			</div>
		</div>
	</div>
	
	<!-- Aws Resource  설정 DIV -->
	<div id="awsResourceInfoDiv" style="width: 100%; height: 100%;" hidden="true">
		<div rel="title"><b>CF 설치</b></div>
		<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 3%;display:inline-block;width: 97%;">
				<ul class="progressStep_7">
					<li class="pass">AWS 정보</li>
					<li class="pass">UAA 정보</li>
					<li class="pass">CONSUL 정보</li>
					<li class="pass">네트워크 정보</li>
					<li class="active">리소스 정보</li>
					<li class="before">배포파일 정보</li>
					<li class="before">설치</li>
				</ul>
			</div>
			<div style="margin:15px 1.5%;">▶ 리소스정보 설정</div>
			<div class="w2ui-page page-0" style="padding-left: 5%;">
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">Stemcell</label>
					<div>
						<div>
							<input type="list" name="stemcells" style="float: left; width:60%; margin-top: 1.5px;" placeholder="스템셀을 선택하세요.">
						</div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">VM 비밀번호</label>
					<div>
						<input name="boshPassword" type="text" style="float: left; width: 60%;" required placeholder="VM 비밀번호를 입력하세요." />
						<div class="isMessage"></div>
					</div>
				</div>
			</div>
			<div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: left;" onclick="saveAwsResourceInfo('before');">이전</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveAwsResourceInfo('after');">다음>></button>
			</div>
		</div>
	</div>
	
	<!-- Aws Deploy  DIV -->
	<div id="deployDiv" hidden="true">
		<div rel="title">
			<b>CF 설치</b>
		</div>
		<div rel="body"
			style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 3%;display:inline-block;width: 97%;">
				<ul class="progressStep_7">
					<li class="pass">AWS 정보</li>
					<li class="pass">UAA 정보</li>
					<li class="pass">CONSUL 정보</li>
					<li class="pass">네트워크 정보</li>
					<li class="pass">리소스 정보</li>
					<li class="active">배포파일 정보</li>
					<li class="before">설치</li>
				</ul>
			</div>
			<div style="width: 95%; height: 84%; float: left; display: inline-block;">
				<textarea id="deployInfo" style="width: 100%; height: 99%; overflow-y: visible; resize: none; background-color: #FFF; margin-left: 2%" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" style="float: left;" onclick="awsResourcePopup();">이전</button>
			<button class="btn" style="float: right; padding-right: 15%" onclick="cfDeploy('after');">다음>></button>
		</div>
	</div>

	<!-- AWS 설치화면 -->
	<div id="installDiv" style="width: 100%; height: 100%;" hidden="true">
		<div rel="title">
			<b>CF 설치</b>
		</div>
		<div rel="body"
			style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="height: 60px; margin: 0 15px;">
				<ul class="progressStep_5">
					<li class="pass">AWS 정보</li>
					<li class="pass">네트워크 정보</li>
					<li class="pass">리소스 정보</li>
					<li class="pass">배포파일 정보</li>
					<li class="active">설치</li>
				</ul>
			</div>
			<div style="width: 95%; height: 84%; float: left; display: inline-block;">
				<textarea id="installLogs" style="width: 100%; height: 99%; overflow-y: visible; resize: none; background-color: #FFF; margin-left: 1%" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" style="float: left;" onclick="deployPopup();" disabled>이전</button>
			<!-- <button class="btn" onclick="popupComplete();">취소</button> -->
			<button class="btn" style="float: right; padding-right: 15%" onclick="popupComplete();">닫기</button>
		</div>
	</div>
	<!-- End AWS Popup -->

<!-- Start Cf OPENSTACK POP -->
<!-- 오픈스택 정보 -->
<div id="openstackInfoDiv" style="width: 100%; height: 100%;"
	hidden="true">
	<div rel="title">
		<b>CF 설치</b>
	</div>
	<div rel="body"
		style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
		<div style="margin-left: 3%;display:inline-block;width: 97%;">
			<ul class="progressStep_7">
				<li class="active">오픈스택 정보</li>
				<li class="active">UAA 정보</li>
				<li class="active">CONSUL 정보</li>
				<li class="before">네트워크 정보</li>
				<li class="before">리소스 설정</li>
				<li class="before">배포파일 정보</li>
				<li class="before">설치</li>
			</ul>
		</div>
		<div style="margin:15px 1.5%;">▶ 오픈스택정보 설정</div>
		<div class="w2ui-page page-0" style="padding-left: 5%;">
			<div class="w2ui-field">
				<label style="text-align: left; width: 40%; font-size: 11px;">AUTH
					URL</label>
				<div>
					<input name="authUrl" type="text" style="float: left; width: 60%;"
						required placeholder="Identify API 인증 링크를 입력하세요." />
				</div>
			</div>

			<div class="w2ui-field">
				<label style="text-align: left; width: 40%; font-size: 11px;">Tenant</label>
				<div>
					<input name="tenant" type="text" style="float: left; width: 60%;"
						required placeholder="Tenant명을 입력하세요." />
				</div>
			</div>

			<div class="w2ui-field">
				<label style="text-align: left; width: 40%; font-size: 11px;">User
					Name</label>
				<div>
					<input name="userName" type="text" style="float: left; width: 60%;"
						required placeholder="계정명을 입력하세요." />
				</div>
			</div>

			<div class="w2ui-field">
				<label style="text-align: left; width: 40%; font-size: 11px;">API
					Key</label>
				<div>
					<input name="apiKey" type="text" style="float: left; width: 60%;"
						required placeholder="계정 비밀번호를 입력하세요." />
				</div>
			</div>

			<div class="w2ui-field">
				<label style="text-align: left; width: 40%; font-size: 11px;">Security
					Group</label>
				<div>
					<input name="defaultSecurityGroups" type="text"
						style="float: left; width: 60%;" required
						placeholder="시큐리티 그룹을 입력하세요." />
				</div>
			</div>

			<div class="w2ui-field">
				<label style="text-align: left; width: 40%; font-size: 11px;">Private
					Key Name</label>
				<div>
					<input name="privateKeyName" type="text"
						style="float: left; width: 60%;" required
						placeholder="Key Pair명을 입력하세요." />
				</div>
			</div>

			<div class="w2ui-field">
				<label style="text-align: left; width: 40%; font-size: 11px;">Private
					Key Path</label>
				<div>
					<span onclick="changeKeyPathType('file');" style="width: 30%;"><label><input
							type="radio" name="keyPathType" value="file" tabindex="6" />&nbsp;파일업로드</label></span>
					&nbsp;&nbsp; <span onclick="changeKeyPathType('list');"
						style="width: 30%;"><label><input type="radio"
							name="keyPathType" value="list" tabindex="5" />&nbsp;리스트</label></span>
				</div>
			</div>

			<div class="w2ui-field">
				<input name="privateKeyPath" type="text" style="width: 200px;"
					hidden="true" onclick="openBrowse();" /> <label
					style="text-align: left; width: 40%; font-size: 11px;"
					class="control-label"></label>
				<div id="keyPathDiv"></div>
			</div>

		</div>
		<br />
		<div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" style="float: left;" onclick="popupComplete();">취소</button>
			<button class="btn" style="float: right; padding-right: 15%"
				onclick="saveOpenstackInfo();">다음>></button>
		</div>
	</div>
</div>

<!-- 기본 정보 -->
<div id="osCfInfoDiv" style="width: 100%; height: 100%;" hidden="true">
	<div rel="title">
		<b>CF 설치</b>
	</div>
	<div rel="body"	style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
		<div style="margin-left: 3%;display:inline-block;width: 97%;">
			<ul class="progressStep_7">
				<li class="active">오픈스택 정보</li>
				<li class="active">UAA 정보</li>
				<li class="active">CONSUL 정보</li>
				<li class="before">네트워크 정보</li>
				<li class="before">리소스 설정</li>
				<li class="before">배포파일 정보</li>
				<li class="before">설치</li>
			</ul>
		</div>
		<div style="margin:15px 1.5%;">▶ 기본정보 설정</div>
		<div class="w2ui-page page-0" style="padding-left: 5%;">
			<form id="osCfForm">
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">배포명</label>
					<div>
						<input name="deploymentName" type="text"
							style="float: left; width: 60%;" required
							placeholder="배포명을 입력하세요." />
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">설치관리자
						UUID</label>
					<div>
						<input name="directorUuid" type="text"
							style="float: left; width: 60%;" required
							placeholder="설치관리자 UUID입력하세요." disabled />
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">CF
						릴리즈</label>
					<div>
						<input name="releaseVersion" type="text"
							style="float: left; width: 60%;" />
					</div>
				</div>
			</form>
		</div>
		<br />
		<div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" style="float: left;"
				onclick="saveOsCfInfo('before');">이전</button>
			<button class="btn" onclick="popupComplete();">취소</button>
			<button class="btn" style="float: right; padding-right: 15%"
				onclick="saveOsCfInfo('after');">다음>></button>
		</div>
	</div>
</div>

<!-- 네트워크 정보 -->
<div id="osNetworkInfoDiv" style="width: 100%; height: 100%;"
	hidden="true">
	<div rel="title">
		<b>CF 설치</b>
	</div>
	<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
		<div style="margin-left: 3%;display:inline-block;width: 97%;">
			<ul class="progressStep_7">
				<li class="active">오픈스택 정보</li>
				<li class="active">UAA 정보</li>
				<li class="active">CONSUL 정보</li>
				<li class="before">네트워크 정보</li>
				<li class="before">리소스 설정</li>
				<li class="before">배포파일 정보</li>
				<li class="before">설치</li>
			</ul>
		</div>
		<div style="margin:15px 1.5%;">▶ 네트워크정보 설정</div>
		<div class="w2ui-page page-0" style="padding-left: 5%;">

			<div class="w2ui-field">
				<label style="text-align: left; width: 40%; font-size: 11px;">Floating
					IP</label>
				<div>
					<input name="publicStaticIp" type="text"
						style="float: left; width: 60%;" required
						placeholder="설치관리자에 할당할 Floating IP를 입력하세요." />
				</div>
			</div>

			<div class="w2ui-field">
				<label style="text-align: left; width: 40%; font-size: 11px;">Subnet
					ID</label>
				<div>
					<input name="subnetId" type="text" style="float: left; width: 60%;"
						placeholder="Subnet ID를 입력하세요." />
				</div>
			</div>

			<div class="w2ui-field">
				<label style="text-align: left; width: 40%; font-size: 11px;">Static
					IP</label>
				<div>
					<div>
						<div style="display: inline-block;">
							<span style="float: left;"><input name="subnetStaticFrom"
								id="subnetStaticFrom" type="text"
								style="float: left; width: 152px;" tabindex="4"
								placeholder="예) 10.0.0.100" /></span> <span style="float: left;">&nbsp;
								&ndash; &nbsp;</span> <span style="float: left;"><input
								name="subnetStaticTo" id="subnetStaticTo" type="text"
								style="float: left; width: 152px;" tabindex="5"
								placeholder="예) 10.0.0.106" /></span>
						</div>
					</div>
				</div>
			</div>

			<div class="w2ui-field">
				<label style="text-align: left; width: 40%; font-size: 11px;">Subnet
					Range(CIDR)</label>
				<div>
					<input name="subnetRange" type="text"
						style="float: left; width: 60%;" required
						placeholder="예) 10.0.0.0/24" />
				</div>
			</div>

			<div class="w2ui-field">
				<label style="text-align: left; width: 40%; font-size: 11px;">Gateway
					IP</label>
				<div>
					<input name="subnetGateway" type="text"
						style="float: left; width: 60%;" required
						placeholder="예) 10.0.0.1" />
				</div>
			</div>

			<div class="w2ui-field">
				<label style="text-align: left; width: 40%; font-size: 11px;">DNS</label>
				<div>
					<input name="subnetDns" type="text"
						style="float: left; width: 60%;" required placeholder="예) 8.8.8.8" />
				</div>
			</div>

		</div>
		<br />
		<div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" style="float: left;"
				onclick="saveOsNetworkInfo('before');">이전</button>
			<button class="btn" onclick="popupComplete();">취소</button>
			<button class="btn" style="float: right; padding-right: 15%"
				onclick="saveOsNetworkInfo('after');">다음>></button>
		</div>
	</div>
</div>

<!-- 리소스 정보 -->
<div id="osResourceInfoDiv" style="width: 100%; height: 100%;"
	hidden="true">
	<div rel="title">
		<b>CF 설치</b>
	</div>
	<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
		<div style="margin-left: 3%;display:inline-block;width: 97%;">
			<ul class="progressStep_7">
				<li class="active">오픈스택 정보</li>
				<li class="active">UAA 정보</li>
				<li class="active">CONSUL 정보</li>
				<li class="before">네트워크 정보</li>
				<li class="before">리소스 설정</li>
				<li class="before">배포파일 정보</li>
				<li class="before">설치</li>
			</ul>
		</div>
		<div style="margin:15px 1.5%;">▶ 리소스정보 설정</div>
		<div class="w2ui-page page-0" style="padding-left: 5%;">
			<div class="w2ui-field">
				<label style="text-align: left; width: 40%; font-size: 11px;">Stemcell</label>
				<div>
					<div>
						<input type="list" name="stemcells"
							style="float: left; width: 330px; margin-top: 1.5px;"
							placeholder="스템셀을 선택하세요.">
					</div>
				</div>
			</div>
			<div class="w2ui-field">
				<label style="text-align: left; width: 40%; font-size: 11px;">Instance
					Type</label>
				<div>
					<input name="cloudInstanceType" type="text"
						style="float: left; width: 60%;" placeholder="인스턴스 유형을 입력하세요." />
				</div>
			</div>
			<div class="w2ui-field">
				<label style="text-align: left; width: 40%; font-size: 11px;">VM
					Password</label>
				<div>
					<input name="cfPassword" type="text"
						style="float: left; width: 60%;"
						placeholder="VM인스턴스의 비밀번호를 입력하세요." />
				</div>
			</div>
		</div>
		<br />
		<div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" style="float: left;"
				onclick="saveOsResourceInfo('before');">이전</button>
			<button class="btn" onclick="popupComplete();">취소</button>
			<button class="btn" style="float: right; padding-right: 15%"
				onclick="saveOsResourceInfo('after');">다음>></button>
		</div>
	</div>
</div>

<!-- 배포파일 정보 -->
<div id="osdeployDiv" style="width: 100%; height: 100%;"
	hidden="true">
	<div rel="title">
		<b>CF 설치</b>
	</div>
	<div rel="body"
		style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
		<div style="height: 60px; margin: 0 15px;">
			<ul class="progressStep_5">
				<li class="pass">오픈스택 정보</li>
				<li class="pass">네트워크 정보</li>
				<li class="pass">리소스 정보</li>
				<li class="active">배포파일 정보</li>
				<li class="before">설치</li>
			</ul>
		</div>
		<div
			style="width: 95%; height: 84%; float: left; display: inline-block;">
			<textarea id="deployInfo"
				style="width: 100%; height: 99%; overflow-y: visible; resize: none; background-color: #FFF; margin-left: 2%"
				readonly="readonly"></textarea>
		</div>
	</div>
	<div class="w2ui-buttons" rel="buttons" hidden="true">
		<button class="btn" style="float: left;"
			onclick="osResourceInfoPopup();">이전</button>
		<button class="btn" onclick="popupComplete();">취소</button>
		<button class="btn" style="float: right; padding-right: 15%"
			onclick="cfDeploy('after');">다음>></button>
	</div>
</div>

<!-- 오픈스택 설치화면 -->
<div id="osInstallDiv" style="width: 100%; height: 100%;" hidden="true">
	<div rel="title">
		<b>CF 설치</b>
	</div>
	<div rel="body"
		style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
		<div style="height: 60px; margin: 0 15px;">
			<ul class="progressStep_5">
				<li class="pass">오픈스택 정보</li>
				<li class="pass">네트워크 정보</li>
				<li class="pass">리소스 정보</li>
				<li class="pass">배포파일 정보</li>
				<li class="active">설치</li>
			</ul>
		</div>
		<div style="margin:15px 1.5%;">▶ 설치 로그</div>
		<div
			style="width: 95%; height: 84%; float: left; display: inline-block;">
			<textarea id="installLogs"
				style="width: 100%; height: 99%; overflow-y: visible; resize: none; background-color: #FFF; margin-left: 1%"
				readonly="readonly"></textarea>
		</div>
	</div>
	<div class="w2ui-buttons" rel="buttons" hidden="true">
		<button class="btn" style="float: left;" onclick="deployPopup()">이전</button>
		<button class="btn" style="float: right; padding-right: 15%"
			onclick="popupComplete();">완료</button>
	</div>
</div>