<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<link rel="stylesheet" type="text/css" href="<c:url value='/css/progress-step.css'/>"/>
<style type="text/css">
.w2ui-popup .w2ui-msg-body {
	background-color: #FFF;
}
</style>

<script type="text/javascript" src="<c:url value='/js/sockjs-0.3.4.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/stomp.js'/>"></script>

<script type="text/javascript">
	//private var
	var iaas = "";
	var cfId = "";
	var defaultInfo = "";
	var uaaInfo = "";
	var consulInfo = "";
	var networkInfo = "";
	var resourceInfo = "";
	var releases = "";
	var stemcells = "";
	var deploymentFile = "";
	var bDefaultDirector = "";
	//Main View Event
	$(function() {

		// 기본 설치 관리자 정보 조회
		bDefaultDirector = getDefaultDirector("<c:url value='/directors/default'/>");

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
		    				else	if ( record.deployStatus == 'deleteing' )
			    				return '<span class="btn btn-primary" style="width:60px">삭제중</span>';
							else
			    				return '&ndash;';
			    	   }
					}
				, {field: 'taskId', caption: 'TASK ID', size: '100px', hidden: true}
				//1.1 Deployment 정보
				, {field: 'deploymentName', caption: '배포명', size: '100px'}
				, {field: 'iaas', caption: 'IaaS', size: '100px'}
				, {field: 'directorUuid', caption: '설치관리자 UUID', size: '280px'}
				, {field: 'release', caption: 'CF 릴리즈', size: '100px'
					, render:function(record){
							if( record.releaseName && record.releaseVersion ){
								return record.releaseName +"/"+ record.releaseVersion;
							}
						}
					}
				, {field: 'appSshFingerprint', caption: 'SSH 핑거프린트', size: '240px'}
				// 1.2 기본정보
				, {field: 'domain', caption: '도메인', size: '180px'}
				, {field: 'description', caption: '도메인 설명', size: '220px'}
				, {field: 'domainOrganization', caption: '도메인 그룹', size: '120px'}
				// 1.3 HA프록시 정보
				, {field: 'proxyStaticIps', caption: 'HAProxy 공인 IP', size: '120px'}
				// 4. 네트워크 정보
				, {field: 'subnetRange', caption: '서브넷 범위', size: '180px'}
				, {field: 'subnetGateway', caption: '게이트웨이', size: '100px'}
				, {field: 'subnetDns', caption: 'DNS', size: '100px'}
				, {field: 'subnetReserved', caption: '할당된 IP 대역', size: '240px'
					, render:function(record){
							if( record.subnetReservedFrom && record.subnetReservedTo ){
								return record.subnetReservedFrom +" - "+ record.subnetReservedTo;
							}
						}
					}
				, {field: 'subnetStatic', caption: 'VM 할당 IP대역', size: '240px'
					, render:function(record){
							if( record.subnetStaticFrom && record.subnetStaticTo ){
								return record.subnetStaticFrom +" - "+ record.subnetStaticTo;
							}
						}
					}
				, {field: 'subnetId', caption: '서브넷 ID(NET ID)', size: '140px'}
				, {field: 'cloudSecurityGroups', caption: '시큐리티 그룹명', size: '100px'}
				
				, {field: 'stemcell', caption: '스템셀', size: '240px'
					, render:function(record){
							if( record.stemcellName && record.stemcellVersion ){
								return record.stemcellName +"/"+ record.stemcellVersion;
							}
						}
					}
				, {field: 'createdDate', caption: '생성일자', size: '100px', hidden: true}
				, {field: 'updatedDate', caption: '수정일자', size: '100px', hidden: true}
				, {field: 'deploymentFile', caption: '배포파일명', size: '180px',
						render: function(record) {
							if ( record.deploymentFile != null ) {
			       				var deplymentParam = {
			       						  service	: "bootstrap"
			       						, iaas		: record.iaas
			       						, id		: record.id
			       				} 
			       				var fileName = record.deploymentFile;
			       				return '<a style="color:#333;" href="/common/downloadDeploymentFile/' + fileName +'" onclick="window.open(this.href); return false;">' + record.deploymentFile + '</a>';
							}
			    			else {
			    				return '&ndash;';
							}
						}
					}
				],
			onSelect : function(event) {
				event.onComplete = function() {
					if ( bDefaultDirector  != null) {
						$('#modifyBtn').attr('disabled', false);
						$('#deleteBtn').attr('disabled', false);
						return;
					}
				}
			},
			onUnselect : function(event) {
				event.onComplete = function() {
					$('#modifyBtn').attr('disabled', true);
					$('#deleteBtn').attr('disabled', true);
					return;
				}
			}
		});

		$("#installBtn").click(function() {
			var directorName = $("#directorName").text().toUpperCase();
			
			if (directorName.indexOf("AWS") > 0) {
				iaas = "AWS";
				defaultInfoPopup();
			} else if (directorName.indexOf("OPENSTACK") > 0) {
				iaas = "OPENSTACk";
				defaultInfoPopup();
			}
			else{
				selectIaas();
			}
		});

		//Cf 수정
		$("#modifyBtn").click(function() {
			if ($("#modifyBtn").attr('disabled') == "disabled")
				return;
			
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
							deletePopup(record);
						},
						no_text : "취소"
					});
		});
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
				iaas = $(".w2ui-msg-body input:radio[name='structureType']:checked").val();
				if (iaas) {
					if (iaas == "AWS")
						defaultInfoPopup();
					else
						openstackPopup();
				} else {
					w2alert("CF를 설치할 클라우드 환경을 선택하세요");
				}
			}
		});
	}

	//수정 기능 - 데이터 조회
	function getCfData(record) {
		console.log("@@@" + record.iaas.toLowerCase());
		var url = "/cf/" + record.iaas.toLowerCase() + "/" + record.id;
		$.ajax({
			type : "GET",
			url : url,
			contentType : "application/json",
			success : function(data, status) {
				if (data != null && data != "") {
					initSetting();
					iaas = record.iaas.toUpperCase();
					if (iaas == "AWS"){
						setAwsData(data.contents);
					}
					else if (iaas == "OPENSTACK"){
						setOpenstackData(data.contents);
					}
				}
			},
			error : function(request, status, error) {
				var errorResult = JSON.parse(request.responseText);
				w2alert(errorResult.message, "CF 수정");
			}
		});
	}

	//삭제 팝
	function deletePopup(record){
	
		var requestParameter = {iaas:record.iaas, id:record.id};
		
		if ( record.deployStatus == null || record.deployStatus == '' ) {
			// 단순 레코드 삭제
			var url = "/cf/delete";
			$.ajax({
				type : "DELETE",
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
				width : 700,
				height : 500,
				title : "<b>CF 삭제</b>",
				body  : body,
				buttons : '<button class="btn" style="float: right; padding-right: 15%;" onclick="popupComplete();">닫기</button>',
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
						initSetting();
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
	 
	// AWS AwsPopup Data Setting
	function setAwsData(contents) {
		cfId = contents.id;
		iaas = "AWS";
		defaultInfo = {
			iaas : "AWS",
			deploymentName 		: contents.deploymentName,
			directorUuid 		: contents.directorUuid,
			releaseName 		: contents.releaseName,
			releaseVersion 		: contents.releaseVersion,
			appSshFingerprint	: contents.appSshFingerprint,
			
			domain 				: contents.domain,
			description 		: contents.description,
			domainOrganization 	: contents.domainOrganization,			
			proxyStaticIps 		: contents.proxyStaticIps,
			
			sslPemPub 			: contents.sslPemPub,
			sslPemRsa 			: contents.sslPemRsa,
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
		}
		
		defaultInfoPopup();
	}
	 
	// Aws 팝업
	function defaultInfoPopup() {
		$("#defaultInfoDiv").w2popup({
			width : 850,
			height : 760,
			modal : true,
			showMax : false,
			onOpen : function(event) {
				event.onComplete = function() {
					if ( defaultInfo != "" && defaultInfo != null) {
						$(".w2ui-msg-body input[name='deploymentName']").val(defaultInfo.deploymentName);
						$(".w2ui-msg-body input[name='directorUuid']").val(defaultInfo.directorUuid);
						$(".w2ui-msg-body input[name='appSshFingerprint']").val(defaultInfo.appSshFingerprint);
						
						$(".w2ui-msg-body input[name='domain']").val(defaultInfo.domain);
						$(".w2ui-msg-body input[name='description']").val(defaultInfo.description);
						$(".w2ui-msg-body input[name='domainOrganization']").val(defaultInfo.domainOrganization);

						$(".w2ui-msg-body input[name='proxyStaticIps']").val(defaultInfo.proxyStaticIps);
						$(".w2ui-msg-body textarea[name='sslPemPub']").val(defaultInfo.sslPemPub);
						$(".w2ui-msg-body textarea[name='sslPemRsa']").val(defaultInfo.sslPemRsa);
					}
					else{
						if( !checkEmpty($("#directorUuid").text()) ){
							$(".w2ui-msg-body input[name='directorUuid']").val($("#directorUuid").text());
						}
					}
					w2popup.lock("릴리즈를 조회 중입니다.", true);
					getCfRelease();
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
	function saveDefaultInfo() {
		// AWSInfo Save
		var release = $(".w2ui-msg-body input[name='releases']").val();
		
		defaultInfo = {
					id 					: (cfId) ? cfId : "",
					iaas 				: iaas.toUpperCase(),
					deploymentName 		: $(".w2ui-msg-body input[name='deploymentName']").val(),
					directorUuid 		: $(".w2ui-msg-body input[name='directorUuid']").val(),
					releaseName 		: release.split("/")[0],
					releaseVersion 		: release.split("/")[1],
					appSshFingerprint   : $(".w2ui-msg-body input[name='appSshFingerprint']").val(),
		
					domain 				: $(".w2ui-msg-body input[name='domain']").val(),
					description 		: $(".w2ui-msg-body input[name='description']").val(),
					domainOrganization 	: $(".w2ui-msg-body input[name='domainOrganization']").val(),
		
					proxyStaticIps 		: $(".w2ui-msg-body input[name='proxyStaticIps']").val(),
					sslPemPub 			: $(".w2ui-msg-body textarea[name='sslPemPub']").val(),
					sslPemRsa 			: $(".w2ui-msg-body textarea[name='sslPemRsa']").val()
		}
		
		if (popupValidation()) {
 			$.ajax({
				type : "PUT",
				url : "/cf/saveDefaultInfo",
				contentType : "application/json",
				data : JSON.stringify(defaultInfo),
				success : function(data, status) {
					var content = data.content
					cfId = content.id;
					w2popup.clear();
					uaaInfoPopup();
				},
				error : function(e, status) {
					w2alert("AWS 설정 등록에 실패 하였습니다.", "CF 설치");
				}
			});
		}
	}

	// UAA POPUP
	function uaaInfoPopup(){
		$("#uaaInfoDiv").w2popup({
			width : 850,
			height : 480,
			modal : true,
			showMax : false,
			onOpen : function(event) {
				event.onComplete = function() {
					if (uaaInfo != "") {
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
	function saveUaaInfo(type){
		uaaInfo = {
				id : cfId,
				iaas : iaas.toUpperCase(),
				loginSecret : $(".w2ui-msg-body input[name='loginSecret']").val(),
				signingKey	: $(".w2ui-msg-body textarea[name='signingKey']").val(),
				verificationKey : $(".w2ui-msg-body textarea[name='verificationKey']").val()
		}
		
		if( type == 'after'){
			if (popupValidation()) {
				//ajax AwsInfo Save
				$.ajax({
					type : "PUT",
					url : "/cf/saveUaaInfo",
					contentType : "application/json",
					data : JSON.stringify(uaaInfo),
					success : function(data, status) {
						w2popup.clear();
						consulInfoPopup();
					},
					error : function(e, status) {
						w2alert("UAA 정보 등록에 실패 하였습니다.", "CF 설치");
					}
				});
			}
		}
		else{
			w2popup.clear();
			defaultInfoPopup();
		}
	}
	
	// CONSUL Info Popup
	function consulInfoPopup(){
		$("#consulInfoDiv").w2popup({
			width : 850,
			height : 750,
			modal : false,
			showMax : false,
			onOpen : function(event) {
				event.onComplete = function() {
					if (consulInfo != "") {
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
	function saveConsulInfo(type){
		consulInfo = {
				id 					: cfId,
				iaas				: iaas.toUpperCase(),
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
					url : "/cf/saveConsulInfo",
					contentType : "application/json",
					data : JSON.stringify(consulInfo),
					success : function(data, status) {
						if( iaas.toUpperCase() == "AWS" ){
							w2popup.clear();
							awsNetworkPopup();
						}
						else if( iaas.toUpperCase() == "OPENSTACK" ) {
							w2popup.clear();
							openstackNetworkPopup();
						}
					},
					error : function(e, status) {
						w2alert("AWS CONSUL 등록에 실패 하였습니다.", "CF 설치");
					}
				});
			}
		}
		else{
			w2popup.clear();
			uaaInfoPopup();
		}
		
	}
	
	// AWS NETWORK POPUP
	function awsNetworkPopup(){
		$("#awsNetworkInfoDiv").w2popup({
			width : 850,
			height : 490,
			modal : true,
			showMax : false,
			onOpen : function(event) {
				event.onComplete = function() {
					if (networkInfo != "") {
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
				iaas				: iaas.toUpperCase(),
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
					url : "/cf/saveAwsNetworkInfo",
					contentType : "application/json",
					async : true,
					data : JSON.stringify(networkInfo),
					success : function(data, status) {
						w2popup.clear();
						resourceInfoPopup();
					},
					error : function(e, status) {
						w2alert("Cf Network 등록에 실패 하였습니다.", "Cf 설치");
					}
				});
			}
		}
		else if (type == 'before') {
			w2popup.clear();
			consulInfoPopup();
		}
	}

	// RESOURCE POPUP
	function resourceInfoPopup() {
		$("#resourceInfoDiv").w2popup({
			width : 850,
			height : 350,
			modal : true,
			showMax : false,
			onOpen : function(event) {
				event.onComplete = function() {
					if (resourceInfo != "") {
						$(".w2ui-msg-body input[name='boshPassword']").val(resourceInfo.boshPassword);
					}
					w2popup.lock("스템셀을 조회 중입니다.", true);
					getStamcellList();
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
	function saveResourceInfo(type) {

		var stemcellInfos = $(".w2ui-msg-body input[name='stemcells']").val().split("/");
		resourceInfo = {
				id 					: cfId,
				iaas				: iaas.toUpperCase(),
				stemcellName 		: stemcellInfos[0],
				stemcellVersion 	: stemcellInfos[1],
				boshPassword 		: $(".w2ui-msg-body input[name='boshPassword']").val()
		}

		if (type == 'after') {
			if(popupValidation()){		
				//Server send Cf Info
				$.ajax({
					type : "PUT",
					url : "/cf/saveResourceInfo",
					contentType : "application/json",
					async : true,
					data : JSON.stringify(resourceInfo),
					success : function(data, status) {
						deploymentFile = data.content.deploymentFile;
						w2popup.clear();
						deployPopup();
					},
					error : function(e, status) {
						w2alert("Cf Resource 등록에 실패 하였습니다.", "Cf 설치");
					}
				});
			}
		} else if (type == 'before') {
			if( iaas.toUpperCase() == "AWS" ){
				w2popup.clear();
				awsNetworkPopup();
			}
			else if( iaas.toUpperCase() == "OPENSTACK" ){
				w2popup.clear();
				openstackNetworkPopup();
			}
		}
	}
	/********************************* AWS END ********************************************/
	
	
	/***************************************
	*******     Openstack Process    *******
	***************************************/
	//OPENSTACK OpenstackPopup Data Setting
	function setOpenstackData(contents) {
		cfId = contents.id;
		iaas = "OPENSTACK";
		defaultInfo = {
			iaas : "OPENSTACK",
			deploymentName 		: contents.deploymentName,
			directorUuid 		: contents.directorUuid,
			releaseName 		: contents.releaseName,
			releaseVersion 		: contents.releaseVersion,
			appSshFingerprint	: contents.appSshFingerprint,
			
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
		}
		
		defaultInfoPopup();
	}
	 
	// OPENSTACK NETWORK POPUP
	function openstackNetworkPopup(){
		$("#openstackNetworkInfoDiv").w2popup({
			width : 850,
			height : 490,
			modal : true,
			showMax : false,
			onOpen : function(event) {
				event.onComplete = function() {
					if (networkInfo != "") {
						$(".w2ui-msg-body input[name='subnetRange']").val(networkInfo.subnetRange);
						$(".w2ui-msg-body input[name='subnetGateway']").val(networkInfo.subnetGateway);
						$(".w2ui-msg-body input[name='subnetDns']").val(networkInfo.subnetDns);
						$(".w2ui-msg-body input[name='subnetReservedFrom']").val(networkInfo.subnetReservedFrom);
						$(".w2ui-msg-body input[name='subnetReservedTo']").val(networkInfo.subnetReservedTo);
						$(".w2ui-msg-body input[name='subnetStaticFrom']").val(networkInfo.subnetStaticFrom);
						$(".w2ui-msg-body input[name='subnetStaticTo']").val(networkInfo.subnetStaticTo);
						$(".w2ui-msg-body input[name='cloudNetId']").val(networkInfo.cloudNetId);
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
	// OPENSTACK NETWORK save Openstack Network
	function saveOpenstackNetworkInfo(type) {
		networkInfo = {
				id 					: cfId,
				iaas				: iaas.toUpperCase(),
				subnetRange			: $(".w2ui-msg-body input[name='subnetRange']").val(),
				subnetGateway		: $(".w2ui-msg-body input[name='subnetGateway']").val(),
				subnetDns			: $(".w2ui-msg-body input[name='subnetDns']").val(),
				subnetReservedFrom	: $(".w2ui-msg-body input[name='subnetReservedFrom']").val(),
				subnetReservedTo	: $(".w2ui-msg-body input[name='subnetReservedTo']").val(),
				subnetStaticFrom	: $(".w2ui-msg-body input[name='subnetStaticFrom']").val(),
				subnetStaticTo		: $(".w2ui-msg-body input[name='subnetStaticTo']").val(),
				cloudNetId			: $(".w2ui-msg-body input[name='cloudNetId']").val(),
				cloudSecurityGroups	: $(".w2ui-msg-body input[name='cloudSecurityGroups']").val()
		}
	
		if (type == 'after') {
			if (popupValidation()) {
				//Server send Cf Info
				$.ajax({
					type : "PUT",
					url : "/cf/saveOpenstackNetworkInfo",
					contentType : "application/json",
					async : true,
					data : JSON.stringify(networkInfo),
					success : function(data, status) {
						w2popup.clear();
						resourceInfoPopup();
					},
					error : function(e, status) {
						w2alert("Cf Network 등록에 실패 하였습니다.", "Cf 설치");
					}
				});
			}
		} else if (type == 'before') {
			w2popup.clear();
			consulInfoPopup();
		}
	}
	
	// OPENSTACK RESOURCE POPUP
	function openstackResourcePopup() {
		$("#openstackResourceInfoDiv").w2popup({
			width : 850,
			height : 350,
			modal : true,
			showMax : false,
			onOpen : function(event) {
				event.onComplete = function() {
					if (resourceInfo != "") {
						$(".w2ui-msg-body input[name='boshPassword']").val(resourceInfo.boshPassword);
					}
					w2popup.lock("스템셀을 조회 중입니다.", true);
					getStamcellList();
				}
			},
			onClose : function(event) {
				event.onComplete = function() {
					initSetting();
				}
			}
		});
	}
	
	/*******************************  OPENSTACK END  ****************************************/
	
	// DEPLOY Confirm
	function cfDeploy(type) {
		//Deploy 단에서 저장할 데이터가 있는지 확인 필요
		//Confirm 설치하시겠습니까?
		if (type == 'before' && iaas == "AWS") {
			w2popup.clear();
			resourceInfoPopup();
			return;
		} else if (type == 'before' && iaas == "OPENSTACK") {
			w2popup.clear();
			openstackResourcePopup();
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
		$("#deployDiv").w2popup({
			width : 850,
			height : 550,
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
			url : "/common/getDeployInfo",
			contentType : "application/json",
			async : true,
			data : deploymentFile,
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
		
		var deploymentName =  defaultInfo.deploymentName;
		var message = "CF(배포명:" + deploymentName +  ") ";
		
		var requestParameter = {
				id : cfId,
				iaas: iaas
		};
		
		$("#installDiv").w2popup({
			width : 850,
			height : 550,
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
						    		
						    		$('.w2ui-msg-buttons #deployPopupBtn').prop("disabled", false);
						    		
						    		installClient.disconnect();
									w2alert(message, "CF 설치");
						       	}
				        	}

				        });
				        installClient.send('/send/cfInstall', {}, JSON.stringify(requestParameter));
				    });
				}
			}
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
				if( data.records != null){
					data.records.map(function(obj) {
						releases.push(obj.name + "/" + obj.version);
					});
				}
				setReleaseList();
			},
			error : function(e, status) {
				w2popup.unlock();
				w2alert("Cf Release List 를 가져오는데 실패하였습니다.", "CF 설치");
			}
		});
	}
	
	//Release List W2Field 적용
	function setReleaseList(){
		$(".w2ui-msg-body input[name='releases']").w2field('list', {items : releases,maxDropHeight : 200,width : 250});
		setReleaseData();
	}
	
	// RELEASE release value setgting
	function setReleaseData(){
		if( !checkEmpty(defaultInfo.releaseName) && !checkEmpty(defaultInfo.releaseVersion) ){
			$(".w2ui-msg-body input[name='releases']").data('selected',{text : defaultInfo.releaseName + "/"+ defaultInfo.releaseVersion});
		}
		w2popup.unlock();
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
				if(data.records != null ){
					data.records.map(function(obj) {
						stemcells.push(obj.name + "/" + obj.version);
					});
				}
				setStemcellList();
			},
			error : function(e, status) {
				w2popup.unlock();
				w2alert("Stemcell List 를 가져오는데 실패하였습니다.", "CF 설치");
			}
		});
	}
	
	// 스템셀 List W2Field 적용
	function setStemcellList(){
		$(".w2ui-msg-body input[name='stemcells']").w2field('list', {items : stemcells,maxDropHeight : 200,width : 250});
		setStemcellData();
	}
	
	// 스템셀 value setgting
	function setStemcellData(){
		if( !checkEmpty(resourceInfo.stemcellName) && !checkEmpty(resourceInfo.stemcellVersion) ){
			$(".w2ui-msg-body input[name='stemcells']").data('selected',{text : resourceInfo.stemcellName + "/"+ resourceInfo.stemcellVersion});
		}
		w2popup.unlock();
	}
	
	//전역변수 초기화
	function initSetting() {
		iaas = "";
		cfId = "";
		defaultInfo = "";
		uaaInfo = "";
		consulInfo = "";
		networkInfo = "";
		resourceInfo = "";
		releases = "";
		stemcells = "";
		deploymentFile = "";
		//bDefaultDirector = "";
		//grid Reload
		gridReload();
	}

	//Install/Delete 팝업 종료시 이벤트
	function popupComplete(){
		w2confirm({
			title 	: $(".w2ui-msg-title b").text(),
			msg		: $(".w2ui-msg-title b").text() + " 화면을 닫으시겠습니까?<BR>(닫은 후에도 완료되지 않는 설치 또는 삭제 작업은 계속 진행됩니다.)",
			yes_text: "확인",
			yes_callBack : function(envent){
				w2popup.close();
				//params init
				initSetting();
			},
			no_text : "취소"
		});
	}

	//버튼 스타일 변경
	function doButtonStyle() {
		if ( !bDefaultDirector ) {
			$('#installBtn').attr('disabled', true);
			$('#modifyBtn').attr('disabled', true);
			$('#deleteBtn').attr('disabled', true);
		} 
		else {
			$('#installBtn').removeAttr("disabled"); 
			$('#modifyBtn').attr('disabled', true);
			$('#deleteBtn').attr('disabled', true);
		}
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
	<div class="page_site">플랫폼 설치 > <strong>CF 설치</strong></div>

	<!-- 설치 관리자 -->
	<div id="isDefaultDirector"></div>
	
	<!-- Cf 목록-->
	<div class="pdt20">
		<div class="title fl">CF 목록</div>
		<div class="fr">
			<!-- Btn -->
			<span id="installBtn" class="btn btn-primary" style="width: 120px">설&nbsp;&nbsp;치</span>
			&nbsp; <span id="modifyBtn" class="btn btn-info" style="width: 120px">수&nbsp;&nbsp;정</span>
			&nbsp; <span id="deleteBtn" class="btn btn-danger" style="width: 120px">삭&nbsp;&nbsp;제</span>
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
			<label style="width: 100px; margin-left: 40px;">
				<input type="radio" name="structureType" id="type1" value="AWS" checked="checked" tabindex="1" />
				&nbsp;AWS
			</label>
			<label style="width: 130px; margin-left: 50px;">
				<input type="radio" name="structureType" id="type2" value="OPENSTACK" tabindex="2" />
				 &nbsp;OPENSTACK
			</label>
		</div>
	</div>
</div>

	<!-- Default 정보 DIV -->
	<div id="defaultInfoDiv" style="width: 100%; height: 100%;" hidden="true">
		<div rel="title"><b>CF 설치</b></div>
		<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 2%;display:inline-block;width: 98%;">
				<ul class="progressStep_7">
					<li class="active">기본 정보</li>
					<li class="before">UAA 정보</li>
					<li class="before">CONSUL 정보</li>
					<li class="before">네트워크 정보</li>
					<li class="before">리소스 정보</li>
					<li class="before">배포파일 정보</li>
					<li class="before">설치</li>
				</ul>
			</div>
			<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
				<div class="panel panel-info">	
					<div class="panel-heading"><b>기본정보</b></div>
					<div class="panel-body" style="padding:5px 5% 10px 5%;">
						<div class="w2ui-field">
							<label style="text-align: left; width: 40%; font-size: 11px;">설치관리자 UUID</label>
							<div>
								<input name="directorUuid" type="text" style="float: left; width: 60%;" required placeholder="설치관리자 UUID를 입력하세요." />
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left; width: 40%; font-size: 11px;">배포 명</label>
							<div>
								<input name="deploymentName" type="text" style="float: left; width: 60%;" required placeholder="배포 명을 입력하세요." />
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
							<label style="text-align: left; width: 40%; font-size: 11px;">SSH 핑거프린트</label>
							<div>
								<input name="appSshFingerprint" type="text" style="float: left; width: 60%;" required placeholder="Diego SSH 핑거프린트를 입력하세요." />
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left; width: 40%; font-size: 11px;">기본 조직명</label>
							<div>
								<input name="domainOrganization" type="text" style="float: left; width: 60%;" required placeholder="기본 조직명을 입력하세요." />
								<div class="isMessage"></div>
							</div>
						</div>
					</div>
				</div>
				<div class="panel panel-info">	
					<div class="panel-heading"><b>CF 정보</b></div>
					<div class="panel-body" style="padding:5px 5% 10px 5%;">
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
					</div>
				</div>	
				<div class="panel panel-info">	
					<div class="panel-heading"><b>HAProxy 정보</b></div>
					<div class="panel-body" style="padding:5px 5% 10px 5%;">
						<div class="w2ui-field">
							<label style="text-align: left; width: 40%; font-size: 11px;">HAProxy 공인 IP</label>
							<div>
								<input name="proxyStaticIps" type="text" style="float: left; width: 60%;" required placeholder="프록시 서버 공인 IP를 입력하세요." />
								<div class="isMessage"></div>
							</div>
						</div>	
						<div class="w2ui-field">
							<label style="text-align: left; width: 40%; font-size: 11px;">HAProxy 인증서</label>
							<div>
								<textarea name="sslPemPub" style="float: left; width: 60%; height: 60px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
									required placeholder="프록시 서버 인증서를 입력하세요." ></textarea>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left; width: 40%; font-size: 11px;">HAProxy 개인키</label>
							<div>
								<textarea name="sslPemRsa" style="float: left; width: 60%; height: 60px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
									required placeholder="프록시 서버 개인키를 입력하세요." ></textarea>
							</div>
						</div>
					</div>
				</div>
			</div>
			<br/>
			<div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveDefaultInfo();">다음>></button>
			</div>
		</div>
	</div>
	
	<!-- AWS UAA 설정 DIV -->
	<div id="uaaInfoDiv" style="width: 100%; height: 100%;" hidden="true">
		<div rel="title"><b>CF 설치</b></div>
		<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 2%;display:inline-block;width: 98%;">
				<ul class="progressStep_7">
					<li class="pass">기본 정보</li>
					<li class="active">UAA 정보</li>
					<li class="before">CONSUL 정보</li>
					<li class="before">네트워크 정보</li>
					<li class="before">리소스 정보</li>
					<li class="before">배포파일 정보</li>
					<li class="before">설치</li>
				</ul>
			</div>
			<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
				<div class="panel panel-info">	
					<div class="panel-heading"><b>UAA 정보</b></div>
					<div class="panel-body" style="padding:5px 5% 10px 5%;">
						<div class="w2ui-field">
							<label style="text-align: left; width: 40%; font-size: 11px;">로그인 비밀번호</label>
							<div>
								<input name="loginSecret" type="text" style="float: left; width: 60%;" required placeholder="로그인 비밀번호를 입력하세요." />
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left; width: 40%; font-size: 11px;">개인키</label>
							<div>
								<textarea name="signingKey" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
									required placeholder="개인키를 입력하세요." ></textarea>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left; width: 40%; font-size: 11px;">공개키</label>
							<div>
								<textarea name="verificationKey" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
									required placeholder="공개키를 입력하세요." ></textarea>
							</div>
						</div>
					</div>
				</div>
			</div>
			<br/>
			<div class="w2ui-buttons" rel="buttons" hidden="true"> 
				<button class="btn" style="float: left;" onclick="saveUaaInfo('before');">이전</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveUaaInfo('after');">다음>></button>
			</div>
		</div>
	</div>
	
	<!-- AWS CONSUL 설정 DIV -->
	<div id="consulInfoDiv" style="width: 100%; height: 100%;" hidden="true">
		<div rel="title"><b>CF 설치</b></div>
		<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 2%;display:inline-block;width: 98%;">
				<ul class="progressStep_7">
					<li class="pass">기본 정보</li>
					<li class="pass">UAA 정보</li>
					<li class="active">CONSUL 정보</li>
					<li class="before">네트워크 정보</li>
					<li class="before">리소스 정보</li>
					<li class="before">배포파일 정보</li>
					<li class="before">설치</li>
				</ul>
			</div>
			<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
				<div class="panel panel-info">	
					<div class="panel-heading"><b>CONSUL 정보</b></div>
					<div class="panel-body" style="padding:5px 5% 10px 5%;">
						<div class="w2ui-field">
							<label style="text-align: left; width: 40%; font-size: 11px;">암호화 키</label>
							<div>
								<input name="encryptKeys" type="text" style="float: left; width: 60%;" required placeholder="암호화 키를 입력하세요." />
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left; width: 40%; font-size: 11px;">에이전트 인증서</label>
							<div>
								<textarea name="agentCert" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
									required placeholder="에이전트 인증서를 입력하세요." ></textarea>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left; width: 40%; font-size: 11px;">에이전트 개인키</label>
							<div>
								<textarea name="agentKey" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
									required placeholder="에이전트 개인키를 입력하세요." ></textarea>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left; width: 40%; font-size: 11px;">서버 CA 인증서</label>
							<div>
								<textarea name="caCert" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
									required placeholder="서버 CA 인증서를 입력하세요." ></textarea>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left; width: 40%; font-size: 11px;">서버 인증서</label>
							<div>
								<textarea name="serverCert" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
									required placeholder="서버 인증서를 입력하세요." ></textarea>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left; width: 40%; font-size: 11px;">서버 개인키</label>
							<div>
								<textarea name="serverKey" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
									required placeholder="서버 개인키를 입력하세요." ></textarea>
							</div>
						</div>
					</div>
				</div>
			</div>
			<br/>
			<div class="w2ui-buttons" rel="buttons" hidden="true"> 
				<button class="btn" style="float: left;" onclick="saveConsulInfo('before');">이전</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveConsulInfo('after');">다음>></button>
			</div>
		</div>
	</div>
	<!-- AWS Network 설정 DIV -->
	<div id="awsNetworkInfoDiv" style="width: 100%; height: 100%;" hidden="true">
		<div rel="title"><b>CF 설치</b></div>
		<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 2%;display:inline-block;width: 98%;">
				<ul class="progressStep_7">
					<li class="pass">기본 정보</li>
					<li class="pass">UAA 정보</li>
					<li class="pass">CONSUL 정보</li>
					<li class="active">네트워크 정보</li>
					<li class="before">리소스 정보</li>
					<li class="before">배포파일 정보</li>
					<li class="before">설치</li>
				</ul>
			</div>
			<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
				<div class="panel panel-info">	
					<div class="panel-heading"><b>네트워크 정보</b></div>
					<div class="panel-body" style="padding:5px 5% 10px 5%;">
						<div class="w2ui-field">
							<label style="text-align: left; width: 40%; font-size: 11px;">시큐리티 그룹</label>
							<div>
								<input name="cloudSecurityGroups" type="text" style="float: left; width: 60%;" required placeholder="예) cf-security" />
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left; width: 40%; font-size: 11px;">서브넷 ID</label>
							<div>
								<input name="subnetId" type="text" style="float: left; width: 60%;" required placeholder="예) subnet-XXXXXX" />
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left; width: 40%; font-size: 11px;">서브넷 범위</label>
							<div>
								<input name="subnetRange" type="text" style="float: left; width: 60%;" required placeholder="예) 10.0.0.0/24" />
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left; width: 40%; font-size: 11px;">게이트웨이</label>
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
							<label style="text-align: left; width: 40%; font-size: 11px;">IP할당 제외 대역</label>
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
						<div class="w2ui-field">
							<label style="text-align: left; width: 40%; font-size: 11px;">IP할당 대역(최소 6개)</label>
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
					</div>
				</div>
			</div>
			<br/>
			<div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: left;" onclick="saveAwsNetworkInfo('before');">이전</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveAwsNetworkInfo('after');">다음>></button>
			</div>
		</div>
	</div>
	
	<!-- Resource  설정 DIV -->
	<div id="resourceInfoDiv" style="width: 100%; height: 100%;" hidden="true">
		<div rel="title"><b>CF 설치</b></div>
		<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 2%;display:inline-block;width: 98%;">
				<ul class="progressStep_7">
					<li class="pass">기본 정보</li>
					<li class="pass">UAA 정보</li>
					<li class="pass">CONSUL 정보</li>
					<li class="pass">네트워크 정보</li>
					<li class="active">리소스 정보</li>
					<li class="before">배포파일 정보</li>
					<li class="before">설치</li>
				</ul>
			</div>
			<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
				<div class="panel panel-info">	
					<div class="panel-heading"><b>리소스 정보</b></div>
					<div class="panel-body" style="padding:5px 5% 10px 5%;">
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
				</div>
			</div>
			<div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: left;" onclick="saveResourceInfo('before');">이전</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveResourceInfo('after');">다음>></button>
			</div>
		</div>
	</div>
	
	<!-- 배포파일 정보 -->
	<div id="deployDiv" style="width: 100%; height: 100%;" hidden="true">
		<div rel="title"><b>CF 설치</b></div>
		<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 2%;display:inline-block;width: 98%;">
				<ul class="progressStep_7">
					<li class="pass">기본 정보</li>
					<li class="pass">UAA 정보</li>
					<li class="pass">CONSUL 정보</li>
					<li class="pass">네트워크 정보</li>
					<li class="pass">리소스 정보</li>
					<li class="active">배포파일 정보</li>
					<li class="before">설치</li>
				</ul>
			</div>
			<div style="width:95%;height:82%;float:left;display: inline-block;margin-top:1%;">
				<textarea id="deployInfo" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:3%;" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" style="float: left;" onclick="resourceInfoPopup();">이전</button>
			<button class="btn" style="float: right; padding-right: 15%" onclick="cfDeploy('after');">다음>></button>
		</div>
	</div>
	
	<!-- 설치화면 -->
	<div id="installDiv" style="width: 100%; height: 100%;" hidden="true">
		<div rel="title"><b>CF 설치</b></div>
		<div rel="body" style="width:100%;height:100%;padding:15px 5px 0 5px;margin:0 auto;">
			<div style="margin-left: 2%;display:inline-block;width: 98%;">
				<ul class="progressStep_7">
					<li class="pass">기본 정보</li>
					<li class="pass">UAA 정보</li>
					<li class="pass">CONSUL 정보</li>
					<li class="pass">네트워크 정보</li>
					<li class="pass">리소스 정보</li>
					<li class="pass">배포파일 정보</li>
					<li class="active">설치</li>
				</ul>
			</div>
			<div style="width:95%;height:84%;float: left;display: inline-block;margin-top:1%;">
				<textarea id="installLogs" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:3%;" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" id="deployPopupBtn" style="float: left;" onclick="deployPopup()" disabled>이전</button>
			<button class="btn" style="float: right; padding-right: 15%" onclick="popupComplete();">닫기</button>
		</div>
	</div>
	<!-- End AWS Popup -->

	<!-- Start Cf OPENSTACK POP -->
	
	<!-- 네트워크 정보 -->
	<div id="openstackNetworkInfoDiv" style="width: 100%; height: 100%;" hidden="true">
		<div rel="title"><b>CF 설치</b></div>
		<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 2%;display:inline-block;width: 98%;">
				<ul class="progressStep_7">
					<li class="pass">기본 정보</li>
					<li class="pass">UAA 정보</li>
					<li class="pass">CONSUL 정보</li>
					<li class="active">네트워크 정보</li>
					<li class="before">리소스 정보</li>
					<li class="before">배포파일 정보</li>
					<li class="before">설치</li>
				</ul>
			</div>
			<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
				<div class="panel panel-info">	
					<div class="panel-heading"><b>네트워크 정보</b></div>
					<div class="panel-body" style="padding:5px 5% 10px 5%;">
						<div class="w2ui-field">
							<label style="text-align: left; width: 40%; font-size: 11px;">시큐리티 그룹</label>
							<div>
								<input name="cloudSecurityGroups" type="text" style="float: left; width: 60%;" required placeholder="예) cf-security" />
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left; width: 40%; font-size: 11px;">네트워크 ID</label>
							<div>
								<input name="cloudNetId" type="text" style="float: left; width: 60%;" required placeholder="예) subnet-XXXXXX" />
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left; width: 40%; font-size: 11px;">서브넷 범위</label>
							<div>
								<input name="subnetRange" type="text" style="float: left; width: 60%;" required placeholder="예) 10.0.0.0/24" />
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left; width: 40%; font-size: 11px;">게이트웨이</label>
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
				</div>
			</div>
			<br/>
			<div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: left;" onclick="saveOpenstackNetworkInfo('before');">이전</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveOpenstackNetworkInfo('after');">다음>></button>
			</div>
		</div>
	</div>
	<!-- END -->