<%
/* =================================================================
 * 작성일 : 
 * 작성자 : 
 * 상세설명 : CF 설치
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016.10       지향은           화면 수정 및 vSphere 클라우드 기능 추가
 * 2016.12       지향은           CF 목록과 팝업 화면 .jsp 분리 및 설치 버그 수정 
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript">

//setting variable
var cfId = "";
var networkId = "";
var defaultInfo = "";
var uaaInfo = "";
var consulInfo = "";
var blobstoreInfo="";
var hm9000Info = "";
var networkInfo = [];
var publicStaticIp = "";
var internalCnt=1;
var resourceInfo = "";
var releases = "";
var stemcells = "";
var deploymentFile = "";
var installStatus ="";

$(function() {

});
/********************************************************
 * 설명 :  CF 조회 시 데이터 조회
 * Function : getCfData
 *********************************************************/
function getCfData(record) {
	var url = "/deploy/"+menu+"/install/detail/" + record.id;
	$.ajax({
		type : "GET",
		url : url,
		contentType : "application/json",
		success : function(data, status) {
			if (data != null && data != "") {
				iaas = data.content.iaasType.toUpperCase();
				diegoUse = record.diegoYn;
				setCfData(data.content);
				defaultInfoPopup();
			}
		},
		error : function(request, status, error) {
			var errorResult = JSON.parse(request.responseText);
			w2alert(errorResult.message, "CF 수정");
		}
	});
}

/********************************************************
 * 설명 :  CF Data Setting
 * Function : setCfData
 *********************************************************/
function setCfData(contents) {
	if( menu == "cfDiego" ) {
		contents = contents.cfVo;
	}
	cfId = contents.id;
	iaas = contents.iaasType;
	diegoUse = contents.diegoYn;
	
	 //External을 제외한 Internal 수
	if( contents.networks.length > 1){
		internalCnt = contents.networks.length-1;
	}
	//기본정보
	defaultInfo = {
		iaas 							: iaas,
		deploymentName 		: contents.deploymentName,
		directorUuid 				: contents.directorUuid,
		releaseName 			: contents.releaseName,
		releaseVersion 			: contents.releaseVersion,
		appSshFingerprint		: contents.appSshFingerprint,
		deaMemoryMB			: contents.deaMemoryMB,
		deaDiskMB				: contents.deaDiskMB,
		
		domain 						: contents.domain,
		description 				: contents.description,
		domainOrganization 	: contents.domainOrganization,			
	}
	//네트워크 정보 
	for(var i=0; i<contents.networks.length; i++){
	 	var arr = {
	 		id								: contents.id,
			deployType					: contents.networks[i].deployType,
			seq							: i,
			net							: contents.networks[i].net,
			publicStaticIp				: contents.networks[i].publicStaticIp,
			subnetRange 				: contents.networks[i].subnetRange,
			subnetGateway 			: contents.networks[i].subnetGateway,
			subnetDns 				: contents.networks[i].subnetDns,
			subnetReservedFrom 	: contents.networks[i].subnetReservedFrom,
			subnetReservedTo 		: contents.networks[i].subnetReservedTo,
			subnetStaticFrom 		: contents.networks[i].subnetStaticFrom,
			subnetStaticTo 			: contents.networks[i].subnetStaticTo,
			subnetId 					: contents.networks[i].subnetId,
			cloudSecurityGroups 	: contents.networks[i].cloudSecurityGroups
		}
	 	networkInfo.push(arr);
	}
	
	
	//UAA
	for(var i=0; i<contents.keys.length; i++){
		if(Number(contents.keys[i].keyType) == 1310){
			uaaInfo = {
					id 							: contents.id,
					loginSecret				: contents.loginSecret,
					signingKey				: contents.keys[i].privateKey,
					verificationKey		: contents.keys[i].publicKey,
					
					proxyStaticIps 		: contents.proxyStaticIps,
					sslPemPub 			: contents.sslPemPub,
					sslPemRsa 			: contents.sslPemRsa
			}
		}
	
	
		//CONSUL
		else if(Number(contents.keys[i].keyType) == 1320){
			consulInfo = {
					id 							: contents.id,
					agentCert				: contents.keys[i].agentCert,
					agentKey				: contents.keys[i].agentKey,
					caCert					: contents.keys[i].caCert,
					encryptKeys			: contents.encryptKeys,
					serverCert				: contents.keys[i].serverCert,
					serverKey				: contents.keys[i].serverKey
			}
		}
		
		//Blobstore
		else if(Number(contents.keys[i].keyType) == 1330){
			blobstoreInfo = {
					id							: contents.id,
					blobstoreTlsCert		: contents.keys[i].tlsCert,
					blobstorePrivateKey	: contents.keys[i].privateKey,
					blobstoreCaCert		: contents.keys[i].caCert
			}
		}
		
		//hm9000
		else if(Number(contents.keys[i].keyType) == 1340){
			hm9000Info = {
					id							: contents.id,
					hm9000ServerKey				: contents.keys[i].serverKey,
					hm9000ServerCert				: contents.keys[i].serverCert,
					hm9000ClientKey				: contents.keys[i].clientKey,
					hm9000ClientCert				: contents.keys[i].clientCert,
					hm9000CaCert					: contents.keys[i].caCert
			}
		}
	}
	
	//resource
	if(contents.resource != null && contents.resource != undefined && contents.resource != ""){
		resourceInfo = {
				id 									: contents.id,
				stemcellName 				: contents.resource.stemcellName,
				stemcellVersion 			: contents.resource.stemcellVersion,
				boshPassword 				: contents.resource.boshPassword,
				smallFlavor					: contents.resource.smallFlavor,
				mediumFlavor				: contents.resource.mediumFlavor,
				largeFlavor					: contents.resource.largeFlavor,
				runnerFlavor					: contents.resource.runnerFlavor,
				smallRam						: contents.resource.smallRam,
				smallDisk						: contents.resource.smallDisk,
				smallCpu						: contents.resource.smallCpu,
				mediumRam					: contents.resource.mediumRam,
				mediumDisk					: contents.resource.mediumDisk,
				mediumCpu					: contents.resource.mediumCpu,
				largeRam						: contents.resource.largeRam						,
				largeDisk						: contents.resource.largeDisk,
				largeCpu						: contents.resource.largeCpu,
				runnerRam					: contents.resource.runnerRam,
				runnerDisk					: contents.resource.runnerDisk,
				runnerCpu						: contents.resource.runnerCpu
			}
	}
}

/********************************************************
 * 설명		:  기본 정보 팝업
 * Function	: defaultInfoPopup
 *********************************************************/
function defaultInfoPopup() {
	 settingDiegoUse(diegoUse, $("#defaultInfoDiv ul"));
	$("#defaultInfoDiv").w2popup({
		width : 950,
		height : 620,
		modal : true,
		showMax : false,
		onOpen : function(event) {
			event.onComplete = function() {
				if ( defaultInfo != "" && defaultInfo != null) {
					//설치관리자 UUID
					$(".w2ui-msg-body input[name='deploymentName']").val(defaultInfo.deploymentName);
					$(".w2ui-msg-body input[name='directorUuid']").val(defaultInfo.directorUuid);
					$(".w2ui-msg-body input[name='appSshFingerprint']").val(defaultInfo.appSshFingerprint);
					$(".w2ui-msg-body input[name='domainOrganization']").val(defaultInfo.domainOrganization);
					$(".w2ui-msg-body input[name='deaMemoryMB']").val(defaultInfo.deaMemoryMB);
					$(".w2ui-msg-body input[name='deaDiskMB']").val(defaultInfo.deaDiskMB);
					
					//CF 정보
					$(".w2ui-msg-body input[name='domain']").val(defaultInfo.domain);
					$(".w2ui-msg-body input[name='description']").val(defaultInfo.description);
				} else{
					if( !checkEmpty($("#directorUuid").text()) ){
						$(".w2ui-msg-body input[name='directorUuid']").val($("#directorUuid").text());
					}
				}
				w2popup.lock("릴리즈를 조회 중입니다.", true);
				getCfRelease();
			}
		}, onClose : function(event) {
			event.onComplete = function() {
				initSetting();
			}
		}
	});
}

/********************************************************
 * 설명		: CF 릴리즈 조회
 * Function	: getCfRelease
 *********************************************************/
function getCfRelease() {
	$.ajax({
		type : "GET",
		url : "/common/deploy/release/list/cf",
		contentType : "application/json",
		async : true,
		success : function(data, status) {
			releases = new Array();
			if( data.records != null){
				data.records.map(function(obj) {
					releases.push(obj.name + "/" + obj.version);
				});
			}
			$(".w2ui-msg-body input[name='releases']").w2field('list', {items : releases,maxDropHeight : 200,width : 250});
			setReleaseData();
		},
		error : function(e, status) {
			w2popup.unlock();
			w2alert("Cf Release List 를 가져오는데 실패하였습니다.", "CF 설치");
		}
	});
}

/********************************************************
 * 설명		: 조회한 CF 릴리즈 값 설정
 * Function	: setReleaseData
 *********************************************************/
function setReleaseData(){
	if( !checkEmpty(defaultInfo.releaseName) && !checkEmpty(defaultInfo.releaseVersion) ){
		$(".w2ui-msg-body input[name='releases']").data('selected',{text : defaultInfo.releaseName + "/"+ defaultInfo.releaseVersion});
	}
	w2popup.unlock();
}


/********************************************************
 * 설명		: 기본정보 등록
 * Function	: saveDefaultInfo
 *********************************************************/
function saveDefaultInfo() {
	var release = $(".w2ui-msg-body input[name='releases']").val();
	for(var i=0; i < deploymentName.length; i++){
		if ( $(".w2ui-msg-body input[name='deploymentName']").val() == deploymentName[i]  
		&& defaultInfo.deploymentName != $(".w2ui-msg-body input[name='deploymentName']").val() 	){
			w2alert("이미 존재하는 배포명 입니다.","CF 설치");
			return;
		}
	}
	defaultInfo = {
				id 								: (cfId) ? cfId : "",
				iaas 							: iaas.toUpperCase(),
				diegoYn						: diegoUse,
				platform						: "cf",
				deploymentName 		: $(".w2ui-msg-body input[name='deploymentName']").val(),
				directorUuid 				: $(".w2ui-msg-body input[name='directorUuid']").val(),
				releaseName 			: release.split("/")[0],
				releaseVersion 			: release.split("/")[1],
				appSshFingerprint   	: $(".w2ui-msg-body input[name='appSshFingerprint']").val(),
				deaMemoryMB			: $(".w2ui-msg-body input[name='deaMemoryMB']").val(),
				deaDiskMB				: $(".w2ui-msg-body input[name='deaDiskMB']").val(),
	
				domain 						: $(".w2ui-msg-body input[name='domain']").val(),
				description 				: $(".w2ui-msg-body input[name='description']").val(),
				domainOrganization 	: $(".w2ui-msg-body input[name='domainOrganization']").val(),
	}
	//유효성
	if (popupValidation()) {
			$.ajax({
			type : "PUT",
			url : "/deploy/"+menu+"/install/saveDefaultInfo/N",
			contentType : "application/json",
			data : JSON.stringify(defaultInfo),
			success : function(data, status) {
				var content = data.content
				if( menu == 'cfDiego' ){
					cfId = content.cfVo.id;
				} else{
					cfId = content.id;
				}
				
				w2popup.clear();
				//openstack/aws Or vSphere
				if( iaas.toUpperCase() == "VSPHERE" ){
					vSphereNetworkPopup();
				}else{
					networkPopup();	
				}
				
			},
			error : function(e, status) {
				w2alert("기본정보 설정 등록에 실패 하였습니다.", "CF 설치");
			}
		});
	}
}

/********************************************************
 * 설명		: 네트워크 정보 팝업(openstack/aws)
 * Function	: networkPopup
 *********************************************************/
function networkPopup(proxyStaticIps){
	 settingDiegoUse(diegoUse, $("#networkInfoDiv ul"));
	$("#networkInfoDiv").w2popup({
		width : 950,
		height : 700,
		modal : true,
		showMax : false,
		onOpen : function(event) {
			event.onComplete = function() {
				$(".addInternal").show();
				$(".delInternal").hide();
				if (networkInfo.length > 0) {
					networkId = networkInfo[0].id;
					for(var i=0; i <networkInfo.length; i++){
						if( (networkInfo[i].net).toLowerCase() == "external" ){
							$(".w2ui-msg-body input[name='publicStaticIp']").val(networkInfo[i].subnetStaticFrom); 
						}else{
							var cnt = i-1;
							if(cnt > 0)  settingNetwork(cnt);
							$(".w2ui-msg-body input[name='subnetRange']").eq(cnt).val(networkInfo[i].subnetRange); 
							$(".w2ui-msg-body input[name='subnetGateway']").eq(cnt).val(networkInfo[i].subnetGateway);
							$(".w2ui-msg-body input[name='subnetDns']").eq(cnt).val(networkInfo[i].subnetDns);
							$(".w2ui-msg-body input[name='subnetReservedFrom']").eq(cnt).val(networkInfo[i].subnetReservedFrom);
							$(".w2ui-msg-body input[name='subnetReservedTo']").eq(cnt).val(networkInfo[i].subnetReservedTo);
							$(".w2ui-msg-body input[name='subnetStaticFrom']").eq(cnt).val(networkInfo[i].subnetStaticFrom);
							$(".w2ui-msg-body input[name='subnetStaticTo']").eq(cnt).val(networkInfo[i].subnetStaticTo);
							$(".w2ui-msg-body input[name='subnetId']").eq(cnt).val(networkInfo[i].subnetId);
							$(".w2ui-msg-body input[name='cloudSecurityGroups']").eq(cnt).val(networkInfo[i].cloudSecurityGroups);
						}
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

/********************************************************
 * 설명		: 네트워크 정보 팝업(vSphere)
 * Function	: vSphereNetworkInfoDiv
 *********************************************************/
function vSphereNetworkPopup(){
	 settingDiegoUse(diegoUse, $("#VsphereNetworkInfoDiv ul"));
		$("#VsphereNetworkInfoDiv").w2popup({
			width : 950,
			height : 700,
			modal : true,
			showMax : false,
			onOpen : function(event) {
				event.onComplete = function() {
					$(".addInternal").show();
					$(".delInternal").hide();
					if (networkInfo.length > 0) {
						networkId = networkInfo[0].id;
						for(var i=0; i <networkInfo.length; i++){
							if( networkInfo[i].net == "External" ){
								$(".w2ui-msg-body input[name='publicSubnetId']").val(networkInfo[i].subnetId);
								$(".w2ui-msg-body input[name='publicSubnetRange']").val(networkInfo[i].subnetRange); 
								$(".w2ui-msg-body input[name='publicSubnetGateway']").val(networkInfo[i].subnetGateway);
								$(".w2ui-msg-body input[name='publicSubnetDns']").val(networkInfo[i].subnetDns);
								$(".w2ui-msg-body input[name='publicStaticIp']").val(networkInfo[i].subnetStaticFrom);
								//vsphere
								$(".w2ui-msg-body input[name='publicStaticFrom']").eq(i).val(networkInfo[i].subnetStaticFrom);
								$(".w2ui-msg-body input[name='publicStaticTo']").eq(i).val(networkInfo[i].subnetStaticTo);
							}else{
								var cnt = i-1;
								if(cnt > 0)  settingNetwork(cnt);
								$(".w2ui-msg-body input[name='subnetRange']").eq(cnt).val(networkInfo[i].subnetRange); 
								$(".w2ui-msg-body input[name='subnetGateway']").eq(cnt).val(networkInfo[i].subnetGateway);
								$(".w2ui-msg-body input[name='subnetDns']").eq(cnt).val(networkInfo[i].subnetDns);
								$(".w2ui-msg-body input[name='subnetReservedFrom']").eq(cnt).val(networkInfo[i].subnetReservedFrom);
								$(".w2ui-msg-body input[name='subnetReservedTo']").eq(cnt).val(networkInfo[i].subnetReservedTo);
								$(".w2ui-msg-body input[name='subnetStaticFrom']").eq(cnt).val(networkInfo[i].subnetStaticFrom);
								$(".w2ui-msg-body input[name='subnetStaticTo']").eq(cnt).val(networkInfo[i].subnetStaticTo);
								$(".w2ui-msg-body input[name='subnetId']").eq(cnt).val(networkInfo[i].subnetId);
							}
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

 /********************************************************
  * 설명		: 네트워크 화면 설정
  * Function	: settingNetwork
  *********************************************************/
  function settingNetwork( i ){
 	  var cnt =1;
 	  var networkDiv = "#networkInfoDiv_1";
 	  if( iaas.toLowerCase() == "vsphere" ){
 		  networkDiv = "#VsphereNetworkInfoDiv_1";
 		  cnt=2;
 	  }
 	  $(".w2ui-msg-body "+networkDiv).addClass("panel panel-info");
 	  $(".w2ui-msg-body "+networkDiv).html($("#panel-body"+cnt).html()).show();
 	  
 	  if( i == 1){
 		  $(".addInternal").hide(); 
 		  $(networkDiv +" .delInternal").show();
 	  } 
  }

/********************************************************
 * 설명		: 네트워크 입력 추가
 * Function	: addNetwork
 *********************************************************/
function addNetwork(){
	var cnt =1;
	var networkDiv = "#networkInfoDiv_1";
	  if( iaas.toLowerCase() == "vsphere" ){
		  networkDiv = "#VsphereNetworkInfoDiv_1";
		  cnt = 2;
	  }
	if (popupValidation()) {
		
		$(".w2ui-msg-body " + networkDiv).addClass("panel panel-info");
		$(".w2ui-msg-body " + networkDiv).html($("#panel-body"+cnt).html()).show();
		
		internalCnt ++;
		if( internalCnt == 1 ){
			$(".addInternal").show(); 
			$(".delInternal").hide();
		} else if( internalCnt > 1 ){
			$(".addInternal").hide(); 
			$(".delInternal").hide();
			$(networkDiv+" .delInternal").show();
		}
		
	}
}

/********************************************************
 * 설명		: 네트워크 입력 삭제
 * Function	: delNetwork
 *********************************************************/
function delNetwork(){
	 var networkDiv = "#networkInfoDiv_1";
	  if( iaas.toLowerCase() == "vsphere" ){
		  networkDiv = "#VsphereNetworkInfoDiv_1";
	  }
	 $(".w2ui-msg-body " + networkDiv).removeClass("panel");
	 $(".w2ui-msg-body " + networkDiv).removeClass("panel-info");
	 $(".w2ui-msg-body " + networkDiv).html("");
	
	$(".addInternal").show();
	$(networkDiv + " .delInternal").hide();
	internalCnt --;
}

/********************************************************
 * 설명		: 네트워크 정보 등록
 * Function	: saveNetworkInfo
 *********************************************************/
function saveNetworkInfo(type) {
	 
	 networkInfo = [];
	
	 var subnetStaticFrom = "";
	 if( iaas.toUpperCase() == "VSPHERE" ){
		 subnetStaticFrom = $(".w2ui-msg-body input[name='publicStaticFrom']").val();
	 }else{
		 subnetStaticFrom = $(".w2ui-msg-body input[name='publicStaticIp']").val();
	 }
	 publicStaticIp = subnetStaticFrom;
	 //External 
	 var ExternalArr = {
			 	cfId 									: cfId,
				id									: networkId,
				iaas								: iaas.toUpperCase(),
				deployType					: 1300,
				net								: "External",
				subnetStaticFrom			: subnetStaticFrom,
				subnetStaticTo				: $(".w2ui-msg-body input[name='publicStaticTo']").val(),
				subnetRange					: $(".w2ui-msg-body input[name='publicSubnetRange']").val(),
				subnetGateway				: $(".w2ui-msg-body input[name='publicSubnetGateway']").val(),
				subnetDns					: $(".w2ui-msg-body input[name='publicSubnetDns']").val(),
				subnetId						: $(".w2ui-msg-body input[name='publicSubnetId']").val(),
	 }
	 networkInfo.push(ExternalArr);
	 //Internal
	 for(var i=0; i < $(".w2ui-msg-body input[name='subnetReservedFrom']").length; i++){
		var  InternalArr = {
					cfId 								: cfId,
					id									: networkId,
					iaas								: iaas.toUpperCase(),
					deployType					: 1300,
					net								: "Internal",
					seq								: i,
					subnetRange					: $(".w2ui-msg-body input[name='subnetRange']").eq(i).val(),
					subnetGateway				: $(".w2ui-msg-body input[name='subnetGateway']").eq(i).val(),
					subnetDns					: $(".w2ui-msg-body input[name='subnetDns']").eq(i).val(),
					subnetReservedFrom		: $(".w2ui-msg-body input[name='subnetReservedFrom']").eq(i).val(),
					subnetReservedTo		: $(".w2ui-msg-body input[name='subnetReservedTo']").eq(i).val(),
					subnetStaticFrom			: $(".w2ui-msg-body input[name='subnetStaticFrom']").eq(i).val(),
					subnetStaticTo				: $(".w2ui-msg-body input[name='subnetStaticTo']").eq(i).val(),
					subnetId						: $(".w2ui-msg-body input[name='subnetId']").eq(i).val(),
					cloudSecurityGroups		: $(".w2ui-msg-body input[name='cloudSecurityGroups']").eq(i).val()
			}
		 networkInfo.push(InternalArr);
	 }
	 
	if (type == 'after') {
		if (popupValidation()) {
			//Server send Cf Info
			$.ajax({
				type : "PUT",
				url : "/deploy/"+menu+"/install/saveNetworkInfo",
				contentType : "application/json",
				async : true,
				data : JSON.stringify(networkInfo),
				success : function(data, status) {
					w2popup.clear();
					uaaInfoPopup();
				},
				error : function(e, status) {
					networkInfo = [];
					w2alert("Cf Network 등록에 실패 하였습니다.", "Cf 설치");
				}
			});
		}
	} else if (type == 'before') {
		w2popup.clear();
		defaultInfoPopup();
	}
}

/********************************************************
 * 설명		: UAA 정보 팝업
 * Function	: uaaInfoPopup
 *********************************************************/
function uaaInfoPopup(){
	settingDiegoUse(diegoUse, $("#uaaInfoDiv ul"));
	$("#uaaInfoDiv").w2popup({
		width : 950,
		height : 750,
		modal : true,
		showMax : false,
		onOpen : function(event) {
			event.onComplete = function() {
				if( $(".w2ui-msg-body input[name='proxyStaticIps']").val() == "" ){
					$(".w2ui-msg-body input[name='proxyStaticIps']").val(publicStaticIp);
				}
				
				if (uaaInfo != "") {
					$(".w2ui-msg-body input[name='loginSecret']").val(uaaInfo.loginSecret);
					$(".w2ui-msg-body textarea[name='signingKey']").val(uaaInfo.signingKey);
					$(".w2ui-msg-body textarea[name='verificationKey']").val(uaaInfo.verificationKey);
					
					//HAProxy 정보
					if( uaaInfo.proxyStaticIps != "" ){
						$(".w2ui-msg-body input[name='proxyStaticIps']").val(uaaInfo.proxyStaticIps);
					}else{
						$(".w2ui-msg-body input[name='proxyStaticIps']").val(publicStaticIp);
					}
					
					$(".w2ui-msg-body textarea[name='sslPemPub']").val(uaaInfo.sslPemPub);
					$(".w2ui-msg-body textarea[name='sslPemRsa']").val(uaaInfo.sslPemRsa);
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

/********************************************************
 * 설명		: UAA 정보 등록
 * Function	: saveUaaInfo
 *********************************************************/
function saveUaaInfo(type){
	uaaInfo = {
			id 						: cfId,
			iaas 					: iaas.toUpperCase(),
			loginSecret	 		: $(".w2ui-msg-body input[name='loginSecret']").val(), //로그인 비밀번호
			signingKey			: $(".w2ui-msg-body textarea[name='signingKey']").val(), //개인키
			verificationKey 	: $(".w2ui-msg-body textarea[name='verificationKey']").val(), //공개키
			proxyStaticIps 	: $(".w2ui-msg-body input[name='proxyStaticIps']").val(), //ha-proxy-staticips
			sslPemPub 		: $(".w2ui-msg-body textarea[name='sslPemPub']").val(), //ha-proxy-공개키
			sslPemRsa 		: $(".w2ui-msg-body textarea[name='sslPemRsa']").val() //ha-proxy 개인키
	}
	
	if( type == 'after'){
		if (popupValidation()) {
			//ajax AwsInfo Save
			$.ajax({
				type : "PUT",
				url : "/deploy/"+menu+"/install/saveUaaInfo",
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
	} else{
		w2popup.clear();
		if(iaas.toUpperCase() == "VSPHERE"){
			vSphereNetworkPopup();
		}else{
			networkPopup();
		}
	}
}

/********************************************************
 * 설명		: Consul 정보 팝업
 * Function	: consulInfoPopup
 *********************************************************/
function consulInfoPopup(){
	settingDiegoUse(diegoUse, $("#consulInfoDiv ul"));
	$("#consulInfoDiv").w2popup({
		width : 950,
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

/********************************************************
 * 설명		: Consul 정보 등록
 * Function	: saveConsulInfo
 *********************************************************/
function saveConsulInfo(type){
	consulInfo = {
			id 						: cfId,
			iaas					: iaas.toUpperCase(),
			agentCert			: $(".w2ui-msg-body textarea[name='agentCert']").val(),
			agentKey			: $(".w2ui-msg-body textarea[name='agentKey']").val(),
			caCert				: $(".w2ui-msg-body textarea[name='caCert']").val(),
			encryptKeys		: $(".w2ui-msg-body input[name='encryptKeys']").val(),
			serverCert			: $(".w2ui-msg-body textarea[name='serverCert']").val(),
			serverKey			: $(".w2ui-msg-body textarea[name='serverKey']").val()
	}
	
	if( type == 'after'){
		if (popupValidation()) {
			//ajax AwsInfo Save
			$.ajax({
				type : "PUT",
				url : "/deploy/"+menu+"/install/saveConsulInfo",
				contentType : "application/json",
				data : JSON.stringify(consulInfo),
				success : function(data, status) {
					w2popup.clear();
					blobstoreInfoPopup();
				},
				error : function(e, status) {
					w2alert("CONSUL 등록에 실패 하였습니다.", "CF 설치");
				}
			});
		}
	} else{
		w2popup.clear();
		uaaInfoPopup();
	}
}

/********************************************************
 * 설명		: BLOBSTORE 정보 팝업
 * Function	: blobstorePopup
 *********************************************************/
function blobstoreInfoPopup(){
	settingDiegoUse(diegoUse, $("#blobstoreInfoDiv ul"));
	$("#blobstoreInfoDiv").w2popup({
		width : 950,
		height : 550,
		modal : true,
		showMax : false,
		onOpen : function(event) {
			event.onComplete = function() {
				if (blobstoreInfo != "") {
					$(".w2ui-msg-body textarea[name='blobstoreTlsCert']").val(blobstoreInfo.blobstoreTlsCert);
					$(".w2ui-msg-body textarea[name='blobstorePrivateKey']").val(blobstoreInfo.blobstorePrivateKey);
					$(".w2ui-msg-body textarea[name='blobstoreCaCert']").val(blobstoreInfo.blobstoreCaCert);
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

/********************************************************
 * 설명		: BLOBSTORE 정보 등록
 * Function	: saveBlobstoreInfo
 *********************************************************/
function saveBlobstoreInfo(type){
	 blobstoreInfo = {
				id 								: cfId,
				blobstoreTlsCert			: $(".w2ui-msg-body textarea[name='blobstoreTlsCert']").val(),
				blobstorePrivateKey		: $(".w2ui-msg-body textarea[name='blobstorePrivateKey']").val(),
				blobstoreCaCert			: $(".w2ui-msg-body textarea[name='blobstoreCaCert']").val(),
		}
	 
	if (type == 'after') {
 		if (popupValidation()) {
			//Server send Cf Info
			$.ajax({
				type : "PUT",
				url : "/deploy/"+menu+"/install/saveBlobstoreInfo",
				contentType : "application/json",
				async : true,
				data : JSON.stringify(blobstoreInfo),
				success : function(data, status) {
					w2popup.clear();
					if(diegoUse=="true") {
						if(iaas.toLowerCase() == "vsphere"){
							vSphereResourceInfoPopup();	
						}else{
							resourceInfoPopup();
						}
					}else {
						hm9000InfoPopup();
					}
				},
				error : function(e, status) {
					w2alert("Cf BlobStore 등록에 실패 하였습니다.", "Cf 설치");
				}
			});
		} 
	} else {
		w2popup.clear();
		consulInfoPopup();
	}
	
}

/********************************************************
 * 설명		: hm9000 정보 팝업
 * Function	: hm9000Popup
 *********************************************************/
function hm9000InfoPopup(){
	$("#hm9000InfoDiv").w2popup({
		width : 950,
		height : 750,
		modal : true,
		showMax : false,
		onOpen : function(event) {
			event.onComplete = function() {
				if(hm9000Info != ""){
					$(".w2ui-msg-body textarea[name='hm9000ServerKey']").val(hm9000Info.hm9000ServerKey);
					$(".w2ui-msg-body textarea[name='hm9000ServerCert']").val(hm9000Info.hm9000ServerCert);
					$(".w2ui-msg-body textarea[name='hm9000ClientKey']").val(hm9000Info.hm9000ClientKey);
					$(".w2ui-msg-body textarea[name='hm9000ClientCert']").val(hm9000Info.hm9000ClientCert);
					$(".w2ui-msg-body textarea[name='hm9000CaCert']").val(hm9000Info.hm9000CaCert);
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

/*******************************************************
 * 설명		: hm9000 정보 등록
 * Function	: saveHm9000Info
 *********************************************************/
function saveHm9000Info(type){
	 hm9000Info = {
			id					: cfId,
			hm9000ServerKey		: $(".w2ui-msg-body textarea[name='hm9000ServerKey']").val(),
			hm9000ServerCert		: $(".w2ui-msg-body textarea[name='hm9000ServerCert']").val(),
			hm9000ClientKey		: $(".w2ui-msg-body textarea[name='hm9000ClientKey']").val(),
			hm9000ClientCert		: $(".w2ui-msg-body textarea[name='hm9000ClientCert']").val(),
			hm9000CaCert			: $(".w2ui-msg-body textarea[name='hm9000CaCert']").val()
	 }	
	 
	if (type == 'after') {
 		if (popupValidation()) {
			$.ajax({
				type : "PUT",
				url : "/deploy/cf/install/saveHm9000Info",
				contentType : "application/json",
				async : true,
				data : JSON.stringify(hm9000Info),
				success : function(data, status) {
					w2popup.clear();
					if(iaas.toLowerCase() == "vsphere"){
						vSphereResourceInfoPopup();	
					}else{
						resourceInfoPopup();
					}
				},
				error : function(e, status) {
					w2alert("Cf HM9000 등록에 실패 하였습니다.", "Cf 설치");
				}
			});
		} 
	} else {
		w2popup.clear();
		blobstoreInfoPopup();
	}
}


/********************************************************
 * 설명		: 리소스 정보 팝업(openstack/aws)
 * Function	: resourceInfoPopup
 *********************************************************/
function resourceInfoPopup() {
	 settingDiegoUse(diegoUse, $("#resourceInfoDiv ul"));
	$("#resourceInfoDiv").w2popup({
		width : 950,
		height : 800,
		modal : true,
		showMax : false,
		onOpen : function(event) {
			event.onComplete = function() {
				if (resourceInfo != "") {
					$(".w2ui-msg-body input[name='boshPassword']").val(resourceInfo.boshPassword);
					$(".w2ui-msg-body input[name='smallFlavor']").val(resourceInfo.smallFlavor);
					$(".w2ui-msg-body input[name='mediumFlavor']").val(resourceInfo.mediumFlavor);
					$(".w2ui-msg-body input[name='largeFlavor']").val(resourceInfo.largeFlavor);
					$(".w2ui-msg-body input[name='runnerFlavor']").val(resourceInfo.runnerFlavor);
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

/********************************************************
 * 설명		: 리소스 정보 팝업(vSphere)
 * Function	: vSphereResourceInfoPopup
 *********************************************************/
function vSphereResourceInfoPopup() {
	settingDiegoUse(diegoUse, $("#vSphereResourceInfoDiv ul"));
	$("#vSphereResourceInfoDiv").w2popup({
		width : 950,
		height : 800,
		modal : true,
		showMax : false,
		onOpen : function(event) {
			event.onComplete = function() {
				if (resourceInfo != "") {
					$(".w2ui-msg-body input[name='boshPassword']").val(resourceInfo.boshPassword);
					$(".w2ui-msg-body input[name='smallFlavorRam']").val(resourceInfo.smallRam);
					$(".w2ui-msg-body input[name='smallFlavorDisk']").val(resourceInfo.smallDisk);
					$(".w2ui-msg-body input[name='smallFlavorCpu']").val(resourceInfo.smallCpu);
					$(".w2ui-msg-body input[name='mediumFlavorRam']").val(resourceInfo.mediumRam);
					$(".w2ui-msg-body input[name='mediumFlavorDisk']").val(resourceInfo.mediumDisk);
					$(".w2ui-msg-body input[name='mediumFlavorCpu']").val(resourceInfo.mediumCpu);
					$(".w2ui-msg-body input[name='largeFlavorRam']").val(resourceInfo.largeRam);
					$(".w2ui-msg-body input[name='largeFlavorDisk']").val(resourceInfo.largeDisk);
					$(".w2ui-msg-body input[name='largeFlavorCpu']").val(resourceInfo.largeCpu);
					$(".w2ui-msg-body input[name='runnerFlavorRam']").val(resourceInfo.runnerRam);
					$(".w2ui-msg-body input[name='runnerFlavorDisk']").val(resourceInfo.runnerDisk);
					$(".w2ui-msg-body input[name='runnerFlavorCpu']").val(resourceInfo.runnerCpu);
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

/********************************************************
 * 설명		: 스템셀 조회
 * Function	: getStamcellList
 *********************************************************/
function getStamcellList() {
	$.ajax({
		type : "GET",
		url : "/common/deploy/stemcell/list/cf/openstack",
		contentType : "application/json",
		success : function(data, status) {
			stemcells = new Array();
			if(data.records != null ){
				data.records.map(function(obj) {
					stemcells.push(obj.stemcellFileName+"/"+obj.stemcellVersion);
				});
			}
			$(".w2ui-msg-body input[name='stemcells']").w2field('list', {items : stemcells,maxDropHeight : 200,width : 250});
			setStemcellData();
		},
		error : function(e, status) {
			w2popup.unlock();
			w2alert("Stemcell List 를 가져오는데 실패하였습니다.", "CF 설치");
		}
	});
}

/********************************************************
 * 설명		:  조회한 스템셀 정보 설정
 * Function	: getStamcellList
 *********************************************************/
function setStemcellData(){
	if( !checkEmpty(resourceInfo.stemcellName) && !checkEmpty(resourceInfo.stemcellVersion) ){
		$(".w2ui-msg-body input[name='stemcells']").data('selected',{text : resourceInfo.stemcellName + "/"+ resourceInfo.stemcellVersion});
	}
	w2popup.unlock();
}

/********************************************************
 * 설명		:  리소스 정보 등록
 * Function	: saveResourceInfo
 *********************************************************/
function saveResourceInfo(type) {
	var stemcellInfos = $(".w2ui-msg-body input[name='stemcells']").val().split("/");
	
	resourceInfo = {
			id 								: cfId,
			iaas							: iaas.toUpperCase(),
			platform						: "cf",
			stemcellName 			: stemcellInfos[0],
			stemcellVersion 			: stemcellInfos[1],
			boshPassword 			: $(".w2ui-msg-body input[name='boshPassword']").val(),
			smallFlavor				: $(".w2ui-msg-body input[name='smallFlavor']").val(),
			smallCpu					: $(".w2ui-msg-body input[name='smallFlavorCpu']").val(),
			smallRam					: $(".w2ui-msg-body input[name='smallFlavorRam']").val(),
			smallDisk					: $(".w2ui-msg-body input[name='smallFlavorDisk']").val(),
			mediumFlavor				: $(".w2ui-msg-body input[name='mediumFlavor']").val(),
			mediumCpu				: $(".w2ui-msg-body input[name='mediumFlavorCpu']").val(),
			mediumRam				: $(".w2ui-msg-body input[name='mediumFlavorRam']").val(),
			mediumDisk				: $(".w2ui-msg-body input[name='mediumFlavorDisk']").val(),
			largeFlavor					: $(".w2ui-msg-body input[name='largeFlavor']").val(),
			largeCpu					: $(".w2ui-msg-body input[name='largeFlavorCpu']").val(),
			largeRam					: $(".w2ui-msg-body input[name='largeFlavorRam']").val(),
			largeDisk					: $(".w2ui-msg-body input[name='largeFlavorDisk']").val(),
			runnerFlavor				: $(".w2ui-msg-body input[name='runnerFlavor']").val(),
			runnerCpu					: $(".w2ui-msg-body input[name='runnerFlavorCpu']").val(),
			runnerRam					: $(".w2ui-msg-body input[name='runnerFlavorRam']").val(),
			runnerDisk					: $(".w2ui-msg-body input[name='runnerFlavorDisk']").val()
	}

	if (type == 'after') {
		if(popupValidation()){		
			//Server send Cf Info
			$.ajax({
				type : "PUT",
				url : "/deploy/"+menu+"/install/saveResourceInfo/N",
				contentType : "application/json",
				async : true,
				data : JSON.stringify(resourceInfo),
				success : function(data, status) {
					w2popup.clear();
					deploymentFile = data.deploymentFile;
					createSettingFile(data.id);
				},
				error : function(e, status) {
					w2alert("Cf Resource 등록에 실패 하였습니다.", "Cf 설치");
				}
			});
		}
	} else if (type == 'before') {
		w2popup.clear();
		if(diegoUse=="true") {
			blobstoreInfoPopup();
		} else {
			hm9000InfoPopup();
		}
	}
}

/********************************************************
 * 설명			:  Manifest 파일 생성
 * Function	: createSettingFile
 *********************************************************/
function createSettingFile(id){
	var settingFile = {
			id				: id,
			platform		: "cf"	
	}
	 
	$.ajax({
		type : "POST",
		url : "/deploy/"+menu+"/install/createSettingFile/N",
		contentType : "application/json",
		data : JSON.stringify(settingFile),
		async : true,
		success : function(status) {
			deployPopup();
		},
		error :function(request, status, error) {
			var errorResult = JSON.parse(request.responseText);
			w2alert(errorResult.message, "CF  배포 파일 생성");
			if(iaas.toLowerCase() == "vsphere"){
				vSphereResourceInfoPopup();	
			}else{
				resourceInfoPopup();
			}
		}
	});
	
}

/********************************************************
 * 설명		:  배포정보 팝업
 * Function	: deployPopup
 *********************************************************/
function deployPopup() {
	 settingDiegoUse(diegoUse, $("#deployDiv ul"));
	$("#deployDiv").w2popup({
		width : 950,
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

/********************************************************
 * 설명		:  배포정보 조회
 * Function	: getDeployInfo
 *********************************************************/
function getDeployInfo() {
	$.ajax({
		type : "GET",
		url : "/common/use/deployment/" + deploymentFile,
		contentType : "application/json",
		async : true,
		success : function(data, status) {
			if (status == "success") {
				$(".w2ui-msg-body #deployInfo").text(data);
			} else if (status == "204") {
				w2alert("배포파일이 존재하지 않습니다.", "CF 설치");
			}
		},
		error : function(e, status) {
			w2alert(JSON.parse(e.responseText).message, "CF 설치");
		}
	});
}

/********************************************************
 * 설명		:  배포 확인창
 * Function	: cfDeploy
 *********************************************************/
function cfDeploy(type) {
	 if( type == "before"  ){
		 if(iaas.toLowerCase() == "vsphere"){
				vSphereResourceInfoPopup();	
			}else{
				resourceInfoPopup();
			}
	 }else{
		if ( menu =="cf" ){
			w2confirm({
				msg : "설치하시겠습니까?",
				title : w2utils.lang('CF 설치'),
				yes_text : "예",
				no_text : "아니오",
				yes_callBack : installPopup
			});
		}else if( menu == "cfDiego" ){
			popupInit();
			setDiegoPopup('after'); //cfDiego.jsp
		}
	 }
 }
	

/********************************************************
 * 설명		:  설치 팝업
 * Function	: installPopup
 *********************************************************/
var installClient = "";
function installPopup(){
	settingDiegoUse(diegoUse, $("#installDiv ul"));
	var deploymentName =  defaultInfo.deploymentName;
	var message = "CF(배포명:" + deploymentName +  ") ";
	
	var requestParameter = {
			id 			: cfId,
			iaas		: iaas,
			platform	: "cf"
	};
	
	$("#installDiv").w2popup({
		width : 850,
		height : 550,
		modal	: true,
		showMax : true,
		onOpen : function(event){
			event.onComplete = function(){
				//deployFileName
				var socket = new SockJS('/deploy/cf/install/cfInstall');
				installClient = Stomp.over(socket); 
				installClient.connect({}, function(frame) {
			        installClient.subscribe('/user/deploy/cf/install/logs', function(data){
			        	
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
					    		
					    		installStatus = response.state.toLowerCase();
					    		$('.w2ui-msg-buttons #deployPopupBtn').prop("disabled", false);
					    		
					    		installClient.disconnect();
								w2alert(message, "CF 설치");
					       	}
			        	}
			        });
			        installClient.send('/send/deploy/cf/install/cfInstall', {}, JSON.stringify(requestParameter));
			    });
			}
		}, onClose : function(event){
			if( installClient != "" ){
				installClient.disconnect();
			}
			initSetting();
		}
	});
}
	
/********************************************************
 * 설명		:  diego 사용 여부에 따른 화면 display
 * Function	: settingDiegoUse
 *********************************************************/
function settingDiegoUse(flag, thisDiv){
	if(flag=="false"){
		//ham_9000 display
		$(".progressStep .hm9000").show();
		$("#fingerprint").css("display","none");
	}else{
		$(".progressStep .hm9000").hide();
		$("#fingerprint").css("display","block");
		//progress style
		thisDiv.removeClass("progressStep");
		thisDiv.addClass("progressStep_"+installStep);
		if( installStep == 7 ) { 
			$(".progressStep_"+installStep +" .install").hide();
		}
	}
	return;
}

/********************************************************
 * 설명		:  CF 삭제
 * Function	: deletePopup
 *********************************************************/
function deletePopup(record){
	var requestParameter = {
			iaas		: (record.iaas) ? record.iaas : record.iaasType, 
			id			: record.id,
			platform	: "cf"
	};
	if ( record.deployStatus == null || record.deployStatus == '' ) {
		// 단순 레코드 삭제
		var url = "/deploy/cf/delete/data";
		$.ajax({
			type : "DELETE",
			url : url,
			data : JSON.stringify(requestParameter),
			contentType : "application/json",
			success : function(data, status) {
				deploymentName = [];
				doSearch();
			},
			error : function(request, status, error) {
				w2alert( JSON.parse(request.responseText).message, "CF 삭제");
			}
		});
	} else{
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
					var socket = new SockJS('/deploy/cf/delete/instance');
					deleteClient = Stomp.over(socket); 
					deleteClient.connect({}, function(frame) {
						deleteClient.subscribe('/user/deploy/cf/delete/logs', function(data){
							
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
						    			
						    		installStatus = response.state.toLowerCase();
						    		deleteClient.disconnect();
									w2alert(message, "CF 삭제");
									if( menu == "cfDiego" ){
										$("#cfDiegoPopupDiv").load("/deploy/diego/install/diegoPopup",function(event){
											record = record.diegoVo;
											diegoDeletePopup(record);
										});
									}
						       	}
				        	}					        	
				        });
						deleteClient.send('/send/deploy/cf/delete/instance', {}, JSON.stringify(requestParameter));
				    });
				}
			}, onClose : function (event){
				event.onComplete= function(){
					$("textarea").text("");
					w2ui['config_cfGrid'].reset();
					if( deleteClient != "" ){
						deleteClient.disconnect();
					}
					initSetting();
				}
			}
		});
	}		
}

/********************************************************
 * 설명		: 전체전역변수 초기화
 * Function	: initSetting
 *********************************************************/
function initSetting() {
	cfId = "";
	deploymentName = [];
	installClient ="";
	installStep = 0;
	internalCnt = 1;
	popupInit();
	gridReload();
}

/********************************************************
 * 설명		: 팝업전역변수 초기화
 * Function	: popupInit
 *********************************************************/
function popupInit(){
	diegoUse = "";
	networkId="";
	defaultInfo = "";
	uaaInfo = "";
	consulInfo = "";
	blobstoreInfo ="";
	hm9000Info ="";
	networkInfo = [];
	publicStaticIp = "";
	resourceInfo = "";
	releases = "";
	stemcells = "";
	installStatus ="";
	deploymentFile = "";
	blobstoreInfo="";
}

/********************************************************
 * 설명		: Install/Delete 팝업 종료시 이벤트
 * Function	: popupComplete
 *********************************************************/
function popupComplete(){
	var msg;
	if(installStatus == "done" || installStatus == "error"){
		msg = $(".w2ui-msg-title b").text() + " 화면을 닫으시겠습니까?";
	}else{
		msg = $(".w2ui-msg-title b").text() + " 화면을 닫으시겠습니까?<BR>(닫은 후에도 완료되지 않는 설치 또는 삭제 작업은 계속 진행됩니다.)";
	}
	w2confirm({
		title 	: $(".w2ui-msg-title b").text(),
		msg		: msg,
		yes_text: "확인",
		yes_callBack : function(envent){
			w2popup.close();
			//params init
			initSetting();
		},
		no_text : "취소"
	});
}

/********************************************************
 * 설명		: 팝업창 닫을 경우
 * Function	: popupClose
 *********************************************************/
function popupClose() {
	//params init
	initSetting();
	//grid Reload
	gridReload();
}

/********************************************************
 * 설명		: 그리드 재조회
 * Function	: gridReload
 *********************************************************/
function gridReload() {
	 if( menu == "cf" ){
		 w2ui['config_cfGrid'].reset();
	 }else if( menu =="cfDiego" ){
		 w2ui['config_cfDiegoGrid'].reset();
	 }else{
		 w2ui['config_cfGrid'].reset();
	 }
}

</script>

<!-- Default 정보 DIV -->
<div id="defaultInfoDiv" style="width: 100%; height: 100%;" hidden="true">
	<div rel="title"><b>CF 설치</b></div>
	<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
		<div style="margin-left: 2%;display:inline-block;width: 98%;">
			<ul class="progressStep">
				<li class="active">기본 정보</li>
				<li class="before">네트워크 정보</li>
				<li class="before">UAA 정보</li>
				<li class="before">CONSUL 정보</li>
				<li class="before">BLOBSTORE 정보</li>
				<li class="before hm9000">HM9000 정보</li>
				<li class="before">리소스 정보</li>
				<li class="before">배포파일 정보</li>
				<li class="before install">설치</li>
			</ul>
		</div>
		<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
			<div class="panel panel-info">	
				<div class="panel-heading"><b>기본정보</b></div>
				<div class="panel-body" style="padding:5px 5% 10px 5%;">
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">설치관리자 UUID</label>
						<div>
							<input name="directorUuid" type="text" style="float: left; width: 60%;" readonly required placeholder="설치관리자 UUID를 입력하세요." />
							<div class="isMessage"></div>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">배포 명</label>
						<div>
							<input name="deploymentName" type="text" style="float: left; width: 60%;" onkeydown="return fn_press_han(event, this);" onblur="return fn_press_han(event, this);"  style='ime-mode:inactive;' required placeholder="배포 명을 입력하세요." />
							<div class="isMessage"></div>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">CF 릴리즈</label>
						<div>
							<input name="releases" type="list" style="float: left; width: 60%;" required placeholder="CF 릴리즈를 선택하세요." />
						</div>
					</div>
					<div class="w2ui-field" id="fingerprint">
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
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">DEA DISK 사이즈</label>
						<div>
							<input name="deaMemoryMB" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="DEA disk 사이즈를 입력하세요." />
							<div class="isMessage"></div>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">DEA MEMORY 사이즈</label>
						<div>
							<input name="deaDiskMB" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="DEA memory 사이즈를 입력하세요." />
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
		</div>
		<br/>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" style="float: right; padding-right: 15%" onclick="saveDefaultInfo();">다음>></button>
		</div>
	</div>
</div>

<!-- Network 설정 DIV -->
<div id="networkInfoDiv" style="width: 100%; height: 100%;" hidden="true">
	<div rel="title"><b>CF 설치</b></div>
	<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
		<div style="margin-left: 2%;display:inline-block;width: 98%;">
			<ul class="progressStep">
				<li class="pass">기본 정보</li>
				<li class="active">네트워크 정보</li>
				<li class="before">UAA 정보</li>
				<li class="before">CONSUL 정보</li>
				<li class="before">BLOBSTORE 정보</li>
				<li class="before hm9000">HM9000 정보</li>
				<li class="before">리소스 정보</li>
				<li class="before">배포파일 정보</li>
				<li class="before install">설치</li>
			</ul>
		</div>
		<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
			<div class="panel panel-info">	
				<div class="panel-heading"><b>네트워크 정보</b></div>
				<div class="panel-body">
					<div class="panel panel-info">	
						<div  class="panel-heading" style="padding:5px 5% 10px 5%;"><b>External</b></div>
						<div class="panel-body">
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">디렉터 공인 IP</label> 
								<div>
									<input name="publicStaticIp" type="text"  style="float:left;width:60%;"  required placeholder="예) 10.0.0.20"/>
									<div class="isMessage"></div>
								</div>
							</div>
						</div>
					</div>
					<div class="panel panel-info" id="panel-body1">	
						<div  class="panel-heading" style="padding:5px 5% 17px 5%;">
							<b>Internal</b>
							<div style="float:right">
								<button class="btn addInternal" onclick="addNetwork();">추가</button>
								<button class="btn delInternal" onclick="delNetwork(this);">삭제</button>
							</div>
						</div>
						<div class="panel-body">
							<div class="w2ui-field">
								<label style="text-align: left; width: 40%; font-size: 11px;">시큐리티 그룹</label>
								<div>
									<input name="cloudSecurityGroups" type="text" style="float: left; width: 60%;" required placeholder="예) cf-security" />
									<div class="isMessage"></div>
								</div>
							</div>
					    	<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">네트워크 ID</label>
								<div>
									<input name="subnetId" type="text"  style="float:left;width:60%;" required placeholder="네트워크 ID를 입력하세요."/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">서브넷 범위</label>
								<div>
									<input name="subnetRange" type="text"  style="float:left;width:60%;"  required placeholder="예) 10.0.0.0/24"/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">게이트웨이</label>
								<div>
									<input name="subnetGateway" type="text"  style="float:left;width:60%;"  required placeholder="예) 10.0.0.1"/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">DNS</label>
								<div>
									<input name="subnetDns" type="text"  style="float:left;width:60%;" required placeholder="예) 8.8.8.8"/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left; width: 40%; font-size: 11px;">IP할당 제외 대역</label>
								<div>
									<input name="subnetReservedFrom" id="subnetStaticFrom" type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.100" />
									<span style="float: left; width: 6%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
									<input name="subnetReservedTo" id="subnetStaticTo" type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.106" />
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left; width: 40%; font-size: 11px;">IP할당 대역(최소 15개)</label>
								<div>
									<input name="subnetStaticFrom" type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.100" />
									<span style="float: left; width: 6%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
									<input name="subnetStaticTo" type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.106" />
									<div class="isMessage"></div>
								</div>
							</div>
						</div>
					</div>
					<!-- 추가 네트워크 div_1 -->
					<div  id="networkInfoDiv_1" ></div>
				</div>
			</div>
	    </div>
		<br/>
	    <div class="w2ui-buttons" rel="buttons" hidden="true">
	        <button class="btn" style="float: left;" onclick="saveNetworkInfo('before');" >이전</button>
			<button class="btn" style="float: right; padding-right: 15%" onclick="saveNetworkInfo('after');" >다음>></button>
	    </div>
	</div>
</div>
<!-- vSphere Network -->
<div id="VsphereNetworkInfoDiv" style="width:100%;height:100%;" hidden="true">
	<div rel="title"><b>CF 설치</b></div>
	<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
		<div style="margin-left: 2%;display:inline-block;width: 98%;">
            <ul class="progressStep" >
	            <li class="pass">기본 정보</li>
				<li class="active">네트워크 정보</li>
				<li class="before">UAA 정보</li>
				<li class="before">CONSUL 정보</li>
				<li class="before">BLOBSTORE 정보</li>
				<li class="before hm9000">HM9000 정보</li>
				<li class="before">리소스 정보</li>
				<li class="before">배포파일 정보</li>
				<li class="before install">설치</li>
            </ul>
        </div>
        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
			<div class="panel panel-info">	
				<div class="panel-heading"><b>네트워크 정보</b></div>
				<div class="panel-body">
					<!-- External -->
					<div class="panel panel-info">	
						<div  class="panel-heading" style="padding:5px 5% 10px 5%;"><b>External</b></div>
						<div class="panel-body">
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">포트 그룹명</label>
								<div>
									<input name="publicSubnetId" type="text"  style="float:left;width:60%;" required placeholder="포트 그룹명을 입력하세요."/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">서브넷 범위</label>
								<div>
									<input name="publicSubnetRange" type="text"  style="float:left;width:60%;" required placeholder="예) 10.0.0.0/24"/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">게이트웨이</label>
								<div>
									<input name="publicSubnetGateway" type="text"  style="float:left;width:60%;"  required placeholder="예) 10.0.0.1"/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">DNS</label>
								<div>
									<input name="publicSubnetDns" type="text"  style="float:left;width:60%;"  required placeholder="예) 8.8.8.8"/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">IP할당 대역</label> 
								<div>
									<input name="publicStaticFrom" type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.100" />
									<span style="float: left; width: 6%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
									<input name="publicStaticTo" type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.106" />
									<div class="isMessage"></div>
								</div>
							</div>
						</div>
					</div>
					<!-- Internal -->
					<div class="panel panel-info"  id="panel-body2">
						<div  class="panel-heading" style="padding:5px 5% 17px 5%;">
							<b>Internal</b>
							<div style="float:right">
								<button class="btn addInternal" onclick="addNetwork();">추가</button>
								<button class="btn delInternal" onclick="delNetwork(this);">삭제</button>
							</div>
						</div>
						<div class="panel-body">
					    	<div class="w2ui-field">
								<label class="subnetId" style="text-align: left;width:40%;font-size:11px;">포트 그룹명</label>
								<div>
									<input name="subnetId" type="text"  style="float:left;width:60%;" required placeholder="포트 그룹명을 입력하세요."/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">서브넷 범위</label>
								<div>
									<input name="subnetRange" type="text"  style="float:left;width:60%;"  required placeholder="예) 10.0.0.0/24"/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">게이트웨이</label>
								<div>
									<input name="subnetGateway" type="text"  style="float:left;width:60%;"  required placeholder="예) 10.0.0.1"/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">DNS</label>
								<div>
									<input name="subnetDns" type="text"  style="float:left;width:60%;" required placeholder="예) 8.8.8.8"/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left; width: 40%; font-size: 11px;">IP할당 제외 대역</label>
								<div>
									<input name="subnetReservedFrom" id="subnetStaticFrom" type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.100" />
									<span style="float: left; width: 6%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
									<input name="subnetReservedTo" id="subnetStaticTo" type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.106" />
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left; width: 40%; font-size: 11px;">IP할당 대역(최소 15개)</label>
								<div>
									<input name="subnetStaticFrom" type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.100" />
									<span style="float: left; width: 6%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
									<input name="subnetStaticTo" type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.106" />
									<div class="isMessage"></div>
								</div>
							</div>
						</div>
					</div>
					<!-- 추가 네트워크 div_1 -->
					<div  id="VsphereNetworkInfoDiv_1" ></div>
				</div>
			</div>
	    </div>
		<br/>
	    <div class="w2ui-buttons" rel="buttons" hidden="true">
	        <button class="btn" style="float: left;" onclick="saveNetworkInfo('before');" >이전</button>
			<button class="btn" style="float: right; padding-right: 15%" onclick="saveNetworkInfo('after');" >다음>></button>
	    </div>
	</div>
</div>

<!--  UAA 설정 DIV -->
<div id="uaaInfoDiv" style="width: 100%; height: 100%;" hidden="true">
	<div rel="title"><b>CF 설치</b></div>
	<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
		<div style="margin-left: 2%;display:inline-block;width: 98%;">
			<ul class="progressStep">
				<li class="pass">기본 정보</li>
				<li class="pass">네트워크 정보</li>
				<li class="active">UAA 정보</li>
				<li class="before">CONSUL 정보</li>
				<li class="before">BLOBSTORE 정보</li>
				<li class="before hm9000">HM9000 정보</li>
				<li class="before">리소스 정보</li>
				<li class="before">배포파일 정보</li>
				<li class="before install">설치</li>
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
								required placeholder="jwt-signing.key를 입력하세요." ></textarea>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">공개키</label>
						<div>
							<textarea name="verificationKey" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
								required placeholder="jwt-verification.key를 입력하세요." ></textarea>
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
								required placeholder="ha_proxy_ssl을 인증서를 입력하세요." ></textarea>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">HAProxy 개인키</label>
						<div>
							<textarea name="sslPemRsa" style="float: left; width: 60%; height: 60px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
								required placeholder="ha_proxy_ssl을 개인키 입력하세요." ></textarea>
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

<!-- CONSUL 설정 DIV -->
<div id="consulInfoDiv" style="width: 100%; height: 100%;" hidden="true">
	<div rel="title"><b>CF 설치</b></div>
	<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
		<div style="margin-left: 2%;display:inline-block;width: 98%;">
			<ul class="progressStep">
				<li class="pass">기본 정보</li>
				<li class="pass">네트워크 정보</li>
				<li class="pass">UAA 정보</li>
				<li class="active">CONSUL 정보</li>
				<li class="before">BLOBSTORE 정보</li>
				<li class="before hm9000">HM9000 정보</li>
				<li class="before">리소스 정보</li>
				<li class="before">배포파일 정보</li>
				<li class="before install">설치</li>
			</ul>
		</div>
		<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
			<div class="panel panel-info">	
				<div class="panel-heading"><b>CONSUL 정보</b></div>
				<div class="panel-body" style="padding:5px 5% 10px 5%;">
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">암호화 키</label>
						<div>
							<input name="encryptKeys" type="text" style="float: left; width: 60%;" required placeholder="encrypt_key를 입력하세요." />
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">에이전트 인증서</label>
						<div>
							<textarea name="agentCert" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
								required placeholder="agent.crt를 입력하세요." ></textarea>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">에이전트 개인키</label>
						<div>
							<textarea name="agentKey" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
								required placeholder="agent.key를 입력하세요." ></textarea>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">서버 CA 인증서</label>
						<div>
							<textarea name="caCert" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
								required placeholder="server-ca.crt를 입력하세요." ></textarea>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">서버 인증서</label>
						<div>
							<textarea name="serverCert" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
								required placeholder="server.crt를 입력하세요." ></textarea>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">서버 개인키</label>
						<div>
							<textarea name="serverKey" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
								required placeholder="server.key를 입력하세요." ></textarea>
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

<!-- BLOBSTORE 설정 DIV -->
<div id="blobstoreInfoDiv" style="width: 100%; height: 100%;" hidden="true">
	<div rel="title"><b>CF 설치</b></div>
	<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
		<div style="margin-left: 2%;display:inline-block;width: 98%;">
			<ul class="progressStep">
				<li class="pass">기본 정보</li>
				<li class="pass">네트워크 정보</li>
				<li class="pass">UAA 정보</li>
				<li class="pass">CONSUL 정보</li>
				<li class="active">BLOBSTORE 정보</li>
				<li class="before hm9000">HM9000 정보</li>
				<li class="before">리소스 정보</li>
				<li class="before">배포파일 정보</li>
				<li class="before install">설치</li>
			</ul>
		</div>
		<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
			<div class="panel panel-info">	
				<div class="panel-heading"><b>BLOBSTORE 정보</b></div>
				<div class="panel-body" style="padding:5px 5% 10px 5%;">
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">BlobStroe CA 인증서</label>
						<div>
							<textarea name="blobstoreCaCert" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
								required placeholder="server-ca.crt를 입력하세요." ></textarea>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">BlobStroe 서버키</label>
						<div>
							<textarea name="blobstorePrivateKey" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
								required placeholder="server.key를 입력하세요." ></textarea>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">BlobStroe 서버 인증서</label>
						<div>
							<textarea name="blobstoreTlsCert" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
								required placeholder="server.crt를 입력하세요." ></textarea>
						</div>
					</div>
				</div>
			</div>
		</div>
		<br/>
		<div class="w2ui-buttons" rel="buttons" hidden="true"> 
			<button class="btn" style="float: left;" onclick="saveBlobstoreInfo('before');">이전</button>
			<button class="btn" style="float: right; padding-right: 15%" onclick="saveBlobstoreInfo('after');">다음>></button>
		</div>
	</div>
</div>

<!-- hm9000 설정 DIV -->
<div id="hm9000InfoDiv" style="width: 100%; height: 100%;" hidden="true">
	<div rel="title"><b>CF 설치</b></div>
	<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
		<div style="margin-left: 2%;display:inline-block;width: 98%;">
			<ul class="progressStep">
				<li class="pass">기본 정보</li>
				<li class="pass">네트워크 정보</li>
				<li class="pass">UAA 정보</li>
				<li class="pass">CONSUL 정보</li>
				<li class="pass">BLOBSTORE 정보</li>
				<li class="active hm9000">HM9000 정보</li>
				<li class="before">리소스 정보</li>
				<li class="before">배포파일 정보</li>
				<li class="before install">설치</li>
			</ul>
		</div>
		<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
			<div class="panel panel-info">	
				<div class="panel-heading"><b>HM9000 정보</b></div>
				<div class="panel-body" style="padding:5px 5% 10px 5%;">
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">HM9000 서버키</label>
						<div>
							<textarea name="hm9000ServerKey" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
								required placeholder="hm9000_server.key 입력하세요." ></textarea>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">HM9000 서버 인증서</label>
						<div>
							<textarea name="hm9000ServerCert" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
								required placeholder="hm9000_server.crt 입력하세요. " ></textarea>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">HM9000 클라이언트키</label>
						<div>
							<textarea name="hm9000ClientKey" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
								required placeholder="hm9000_client.key 입력하세요." ></textarea>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">HM9000 클라이언트 인증서</label>
						<div>
							<textarea name="hm9000ClientCert" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
								required placeholder="hm9000_client.crt 입력하세요." ></textarea>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">HM9000 CA 인증서</label>
						<div>
							<textarea name="hm9000CaCert" style="float: left; width: 60%; height: 80px;margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;"
								required placeholder="hm9000_ca.crt 입력하세요." ></textarea>
						</div>
					</div>
				</div>
			</div>
		</div>
		<br/>
		<div class="w2ui-buttons" rel="buttons" hidden="true"> 
			<button class="btn" style="float: left;" onclick="saveHm9000Info('before');">이전</button>
			<button class="btn" style="float: right; padding-right: 15%" onclick="saveHm9000Info('after');">다음>></button>
		</div>
	</div>
</div>

<!-- Resource  설정 DIV -->
<div id="resourceInfoDiv" style="width: 100%; height: 100%;" hidden="true">
	<div rel="title"><b>CF 설치</b></div>
	<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
		<div style="margin-left: 2%;display:inline-block;width: 98%;">
			<ul class="progressStep">
				<li class="pass">기본 정보</li>
				<li class="pass">네트워크 정보</li>
				<li class="pass">UAA 정보</li>
				<li class="pass">CONSUL 정보</li>
				<li class="pass">BLOBSTORE 정보</li>
				<li class="pass hm9000">HM9000 정보</li>
				<li class="active">리소스 정보</li>
				<li class="before">배포파일 정보</li>
				<li class="before install">설치</li>
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
			<div class="panel panel-info">	
				<div class="panel-heading"><b>Small Resource Type</b></div>
				<div class="panel-body"  style="padding:5px 5% 10px 5%;">
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">Flavor</label>
						<div>
							<input name="smallFlavor" type="text" style="float: left; width: 60%;" required placeholder="Small Flavor Type을 입력하세요."  />
							<div class="isMessage"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="panel panel-info">	
				<div class="panel-heading"><b>Medium Resource Type</b></div>
				<div class="panel-body"  style="padding:5px 5% 10px 5%;" >
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">Flavor</label>
						<div>
							<input name="mediumFlavor" type="text" style="float: left; width: 60%;" required placeholder="Medium Flavor Type을 입력하세요."  />
							<div class="isMessage"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="panel panel-info">	
				<div class="panel-heading"><b>Large Resource Type</b></div>
				<div class="panel-body"  style="padding:5px 5% 10px 5%;">
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">Flavor</label>
						<div>
							<input name="largeFlavor" type="text" style="float: left; width: 60%;" required placeholder="Large Flavor Type을 입력하세요."  />
							<div class="isMessage"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="panel panel-info">	
				<div class="panel-heading"><b>Runner Resource Type</b></div>
				<div class="panel-body"  style="padding:5px 5% 10px 5%;">
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">Flavor</label>
						<div>
							<input name="runnerFlavor" type="text" style="float: left; width: 60%;" required placeholder="Runner Flavor Type을 입력하세요."  />
							<div class="isMessage"></div>
						</div>
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
<!--  vSphere Resource -->
<div id="vSphereResourceInfoDiv" style="width: 100%; height: 100%;" hidden="true">
	<div rel="title"><b>CF 설치</b></div>
	<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
		<div style="margin-left: 2%;display:inline-block;width: 98%;">
			<ul class="progressStep">
				<li class="pass">기본 정보</li>
				<li class="pass">네트워크 정보</li>
				<li class="pass">UAA 정보</li>
				<li class="pass">CONSUL 정보</li>
				<li class="pass">BLOBSTORE 정보</li>
				<li class="pass hm9000">HM9000 정보</li>
				<li class="active">리소스 정보</li>
				<li class="before">배포파일 정보</li>
				<li class="before install">설치</li>
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
							<input name="boshPassword" type="text" style="float: left; width: 60%;"  required placeholder="VM 비밀번호를 입력하세요." />
							<div class="isMessage"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="panel panel-info">	
				<div class="panel-heading"><b>Small Resource Type</b></div>
				<div class="panel-body"  style="padding:5px 5% 10px 5%;">
					<div class="w2ui-field">
						<label style="text-align: left;  width: 40%;">Small Type Ram</label>
						<div>
							<input name="smallFlavorRam" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Ram을 입력하세요. 예) 1024"  />
							<div class="isMessage message"></div>
						</div>
						<label style="text-align: left;  width: 40%; ">Small Type Disk</label>
						<div>
							<input name="smallFlavorDisk" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Disk를 입력하세요. 예) 4096"  />
							<div class="isMessage message"></div>
						</div>
						<label style="text-align: left;  width: 40%;">Small Type Cpu</label>
						<div>
							<input name="smallFlavorCpu" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Cpu를 입력하세요. 예) 1"  />
							<div class="isMessage message"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="panel panel-info">	
				<div class="panel-heading"><b>Medium Resource Type</b></div>
				<div class="panel-body"  style="padding:5px 5% 10px 5%;" >
					<div class="w2ui-field">
						<label style="text-align: left;  width: 40%;">Medium Ram</label>
						<div>
							<input name="mediumFlavorRam" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Ram을 입력하세요. 예) 1024"  />
							<br/><div class="isMessage message"></div>
						</div>
						<label style="text-align: left;  width: 40%;">Medium Disk</label>
						<div>
							<input name="mediumFlavorDisk" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Disk를 입력하세요. 예) 4096"  />
							<br/><div class="isMessage message"></div>
						</div>
						<label style="text-align: left;  width: 40%;">Medium Cpu</label>
						<div>
							<input name="mediumFlavorCpu" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Cpu를 입력하세요. 예) 1"  />
							<div class="isMessage message"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="panel panel-info">	
				<div class="panel-heading"><b>Large Resource Type</b></div>
				<div class="panel-body"  style="padding:5px 5% 10px 5%;">
					<div class="w2ui-field">
						<label style="text-align: left;  width: 40%;">Large Ram</label>
						<div>
							<input name="largeFlavorRam" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Ram을 입력하세요. 예) 1024"  />
							<div class="isMessage message"></div>
						</div>
						<label style="text-align: left;  width: 40%;">Large Disk</label>
						<div>
							<input name="largeFlavorDisk" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Disk를 입력하세요. 예) 10240 "  />
							<div class="isMessage message"></div>
						</div>
						<label style="text-align: left;  width: 40%;">Large Cpu</label>
						<div>
							<input name="largeFlavorCpu" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Cpu를 입력하세요. 예) 1"  />
							<div class="isMessage message"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="panel panel-info">	
				<div class="panel-heading"><b>Runner Resource Type</b></div>
				<div class="panel-body"  style="padding:5px 5% 10px 5%;">
					<div class="w2ui-field">
						<label style="text-align: left;  width: 40%;">Runner Ram</label>
						<div>
							<input name="runnerFlavorRam" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Ram을 입력하세요. 예) 16384"  />
							<div class="isMessage message"></div>
						</div>
						<label style="text-align: left;  width: 40%;">Runner Disk</label>
						<div>
							<input name="runnerFlavorDisk" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Disk를 입력하세요. 예) 32768"  />
							<div class="isMessage message"></div>
						</div>
						<label style="text-align: left;  width: 40%;">Runner Cpu</label>
						<div>
							<input name="runnerFlavorCpu" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Cpu를 입력하세요. 예) 2"  />
							<div class="isMessage message"></div>
						</div>
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

<!-- 배포파일 정보 -->
<div id="deployDiv" style="width: 100%; height: 100%;" hidden="true">
	<div rel="title"><b>CF 설치</b></div>
	<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
		<div style="margin-left: 2%;display:inline-block;width: 98%;">
			<ul class="progressStep">
				<li class="pass">기본 정보</li>
				<li class="pass">네트워크 정보</li>
				<li class="pass">UAA 정보</li>
				<li class="pass">CONSUL 정보</li>
				<li class="pass">BLOBSTORE 정보</li>
				<li class="pass hm9000">HM9000 정보</li>
				<li class="pass">리소스 정보</li>
				<li class="active">배포파일 정보</li>
				<li class="before install">설치</li>
			</ul>
		</div>
		<div style="width:95%;height:82%;float:left;display: inline-block;margin-top:1%;">
			<textarea id="deployInfo" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:3%;" readonly="readonly"></textarea>
		</div>
	</div>
	<div class="w2ui-buttons" rel="buttons" hidden="true">
		<button class="btn" style="float: left;" onclick="cfDeploy('before');">이전</button>
		<button class="btn" style="float: right; padding-right: 15%" onclick="cfDeploy('after');">다음>></button>
	</div>
</div>

<!-- 설치화면 -->
<div id="installDiv" style="width: 100%; height: 100%;" hidden="true">
	<div rel="title"><b>CF 설치</b></div>
	<div rel="body" style="width:100%;height:100%;padding:15px 5px 0 5px;margin:0 auto;">
		<div style="margin-left: 2%;display:inline-block;width: 98%;">
			<ul class="progressStep">
				<li class="pass">기본 정보</li>
				<li class="pass">네트워크 정보</li>
				<li class="pass">UAA 정보</li>
				<li class="pass">CONSUL 정보</li>
				<li class="pass">BLOBSTORE 정보</li>
				<li class="pass hm9000">HM9000 정보</li>
				<li class="pass">리소스 정보</li>
				<li class="pass">배포파일 정보</li>
				<li class="active install">설치</li>
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
<!-- End Popup -->

