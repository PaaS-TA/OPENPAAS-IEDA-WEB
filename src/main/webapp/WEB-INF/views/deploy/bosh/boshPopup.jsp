<%
/* =================================================================
 * 작성일 : 
 * 작성자 : 
 * 상세설명 : Bosh 설치
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016.10       이동현           화면 수정 및 vSphere 클라우드 기능 추가
 * 2016.12       이동현           Bosh 목록과 팝업 화면 .jsp 분리 및 설치 버그 수정 
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">
var awsInfo = "";
var openstackInfo = "";
var vSphereInfo = "";
var boshInfo = "";
var networkInfo = new Array();
var publicStaticIp = "";
var internalCnt=0;
var installStatus ="";
var releases;
var stemcells;
var deploymentFile ;
var keyPathFileList = "";
var iaasInfo ="";
var boshId = "";
var networkId = "";
var deployInfo="";
var deployFileName = "";
var resourceInfo = "";

/********************************************************
 * 설명 :  Bosh 조회 시 데이터 조회
 * Function : getBoshData
 *********************************************************/
function getBoshData(record){
	var url = "/deploy/bosh/install/detail/"+record.id;
	$.ajax({
		type : "GET",
		url : url,
		contentType : "application/json",
		success : function(data, status) {
			if ( data == null || data == "" ){
			}else {
				initSetting();
				settingBoshData(data.contents);
			}
		},
		error : function(request, status, error) {
			var errorResult = JSON.parse(request.responseText);
			w2alert(errorResult.message, "BOSH 설치");
		}
	});	
}

/********************************************************
 * 설명 :  상세 조회를 통한 해당 Bosh 정보 셋팅
 * Function : settingBoshData
 *********************************************************/
function settingBoshData(contents){
	boshId = contents.id;
	 iaas = contents.iaasType; 
	openstackInfo = {
			id						: boshId,
			authUrl					: contents.openstackAuthUrl,
			tenant					: contents.openstackTenant,
			userName				: contents.openstackUserName,
			apiKey					: contents.openstackApiKey,
			defaultSecurityGroups	: contents.defaultSecurityGroups,
			privateKeyName			: contents.privateKeyName,
	}
	
	awsInfo = {
			iaas		 			: iaas,
			accessKeyId				: contents.awsAccessKeyId,
			secretAccessKey			: contents.awsSecretAccessId,
			region					: contents.awsRegion,
			availabilityZone 		: contents.awsAvailabilityZone,
			defaultSecurityGroups	: contents.defaultSecurityGroups,
			privateKeyName			: contents.privateKeyName,			
	}
	vSphereInfo = {
			id										: boshId,
			iaas									: contents.iaasType,
			vCenterAddress					: contents.vCenterAddress,
			vCenterUser						: contents.vCenterUser,
			vCenterPassword				: contents.vCenterPassword,
			vCenterName						: contents.vCenterDatacenterName,
			vCenterVMFolder					: contents.vCenterVMFolder,
			vCenterTemplateFolder		: contents.vCenterTemplateFolder,
			vCenterDatastore				: contents.vCenterDatastore,
			vCenterPersistentDatastore	: contents.vCenterPersistentDatastore,
			vCenterDiskPath					: contents.vCenterDiskPath,
			vCenterCluster					: contents.vCenterCluster
	}
	
	boshInfo = {
			id								: boshId,
			iaas 							: contents.iaasType,
			deploymentName 		: contents.deploymentName,
			directorUuid				: contents.directorUuid,
			releaseVersion			: contents.releaseVersion,
			directorName				: contents.directorName,		
			ntp							: contents.ntp,
			boshRelease				: contents.boshRelease,
			enableSnapshots		: contents.enableSnapshots,
			snapshotSchedule		: contents.snapshotSchedule
	}
	
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
		}
	 	networkInfo.push(arr);
	}
	
	if(contents.resource != null){
		resourceInfo = {
				id 									: contents.id,
				stemcellName 				: contents.resource.stemcellName,
				stemcellVersion 				: contents.resource.stemcellVersion,
				boshPassword 				: contents.resource.boshPassword,
				smallFlavor					: contents.resource.smallFlavor,
				mediumFlavor					: contents.resource.mediumFlavor,
				smallRam						: contents.resource.smallRam,
				smallDisk						: contents.resource.smallDisk,
				smallCpu						: contents.resource.smallCpu,
				mediumRam					: contents.resource.mediumRam,
				mediumDisk					: contents.resource.mediumDisk,
				mediumCpu					: contents.resource.mediumCpu,
		}
	}
	
	if(iaas.toUpperCase() == "AWS"){ awsPopup(); return; }
	else if(iaas.toUpperCase() == "OPENSTACK"){ openstackPopup(); return; }
	else {
		vSpherePopup(); return;
	}
}

/********************************************************
 * 설명			: AWS BOSH 정보 Popup 
 * Function	: awsPopup
 *********************************************************/
function awsPopup(){
	$("#awsInfoDiv").w2popup({
		width 	: 700,
		height	: 520,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
				if(awsInfo != ""){					
					$(".w2ui-msg-body input[name='accessKeyId']").val(awsInfo.accessKeyId);
					$(".w2ui-msg-body input[name='secretAccessKey']").val(awsInfo.secretAccessKey);
					$(".w2ui-msg-body input[name='defaultSecurityGroups']").val(awsInfo.defaultSecurityGroups);
					$(".w2ui-msg-body input[name='region']").val(awsInfo.region);
					$(".w2ui-msg-body input[name='privateKeyName']").val(awsInfo.privateKeyName);
					$(".w2ui-msg-body input[name='availabilityZone']").val(awsInfo.availabilityZone);
				}
			}
		},
		onClose : popupClose
	});
}

 /********************************************************
  * 설명			: AWS BOSH 정보 저장
  * Function	: saveAwsInfo
  *********************************************************/
function saveAwsInfo(){
	//AwsInfo Save
	awsInfo = {
			id									: boshId,
			iaas								: "AWS",
			accessKeyId 				: $(".w2ui-msg-body input[name='accessKeyId']").val(),
			secretAccessKey			: $(".w2ui-msg-body input[name='secretAccessKey']").val(),
			defaultSecurityGroups	: $(".w2ui-msg-body input[name='defaultSecurityGroups']").val(),
			region							: $(".w2ui-msg-body input[name='region']").val(),
			privateKeyName			: $(".w2ui-msg-body input[name='privateKeyName']").val(),
			availabilityZone 			: $(".w2ui-msg-body input[name='availabilityZone']").val()
	}
	
	if(popupValidation()){
		//ajax AwsInfo Save
		$.ajax({
			type : "PUT",
			url : "/deploy/bosh/install/saveAwsInfo/N",
			contentType : "application/json",
			async : true,
			data : JSON.stringify(awsInfo), 
			success : function(data, status) {
				boshId = data;
				defaultInfoPopup();
			},
			error : function( e, status ) {
				w2alert("AWS 설정 등록에 실패 하였습니다.", "BOSH 설치");
			}
		});
	}
}

/********************************************************
 * 설명			: OPENSTACK BOSH 정보 POPUP 
 * Function	: openstackPopup
 *********************************************************/
function openstackPopup(){
	$("#openstackInfoDiv").w2popup({
		width 	: 700,
		height	: 520,
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
				}
			}
		},
		onClose : function(event) {
			popupClose();
		}
	});
}

 /********************************************************
  * 설명			: OPENSTACK BOSH 정보 저장 
  * Function	: saveOpenstackInfo
  *********************************************************/
function saveOpenstackInfo(){
	openstackInfo = {
			id									: boshId,
			iaas								: "OPENSTACK",
			authUrl							: $(".w2ui-msg-body input[name='authUrl']").val(),
			tenant							: $(".w2ui-msg-body input[name='tenant']").val(),
			userName						: $(".w2ui-msg-body input[name='userName']").val(),
			apiKey							: $(".w2ui-msg-body input[name='apiKey']").val(),
			defaultSecurityGroups	: $(".w2ui-msg-body input[name='defaultSecurityGroups']").val(),
			privateKeyName			: $(".w2ui-msg-body input[name='privateKeyName']").val(),
			
	}
	if(popupValidation()){
		$.ajax({
			type : "PUT",
			url : "/deploy/bosh/install/saveOpenstackInfo/N",
			contentType : "application/json",
			async : true,
			data : JSON.stringify(openstackInfo), 
			success : function(data, status) {
				boshId = data.id;
				defaultInfoPopup();
				
			},
			error : function( e, status ) {
				w2alert("오픈스택 정보 등록 실패하였습니다.", "BOSH 설치");
			}
		});
	}
}	

 
/********************************************************
 * 설명			: VSPHERE BOSH 정보 Popup
 * Function	: vSpherePopup
 *********************************************************/
function vSpherePopup(){
	$("#vSphereInfoDiv").w2popup({
		width : 720,
		height : 580,
		onClose : popupClose,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
				if(vSphereInfo != ""){
					$(".w2ui-msg-body input[name='vCenterAddress']").val(vSphereInfo.vCenterAddress);
					$(".w2ui-msg-body input[name='vCenterUser']").val(vSphereInfo.vCenterUser);
					$(".w2ui-msg-body input[name='vCenterPassword']").val(vSphereInfo.vCenterPassword);
					$(".w2ui-msg-body input[name='vCenterName']").val(vSphereInfo.vCenterName);
					$(".w2ui-msg-body input[name='vCenterVMFolder']").val(vSphereInfo.vCenterVMFolder);
					$(".w2ui-msg-body input[name='vCenterTemplateFolder']").val(vSphereInfo.vCenterTemplateFolder);
					$(".w2ui-msg-body input[name='vCenterDatastore']").val(vSphereInfo.vCenterDatastore);
					$(".w2ui-msg-body input[name='vCenterPersistentDatastore']").val(vSphereInfo.vCenterPersistentDatastore);
					$(".w2ui-msg-body input[name='vCenterDiskPath']").val(vSphereInfo.vCenterDiskPath);
					$(".w2ui-msg-body input[name='vCenterCluster']").val(vSphereInfo.vCenterCluster);
				}
			}
		},
		onClose : function(event) {
			popupClose();
		}
	});	
}

/********************************************************
 * 설명			: vSphere BOSH 정보 저장
 * Function	: saveVsphereInfo
 *********************************************************/
function saveVsphereInfo(){
	vSphereInfo = {
			id											: boshId,
			iaas										: "VSPHERE",
			vCenterAddress					: $(".w2ui-msg-body input[name='vCenterAddress']").val(),
			vCenterUser							: $(".w2ui-msg-body input[name='vCenterUser']").val(),
			vCenterPassword					: $(".w2ui-msg-body input[name='vCenterPassword']").val(),
			vCenterName						: $(".w2ui-msg-body input[name='vCenterName']").val(),
			vCenterVMFolder					: $(".w2ui-msg-body input[name='vCenterVMFolder']").val(),
			vCenterTemplateFolder			: $(".w2ui-msg-body input[name='vCenterTemplateFolder']").val(),
			vCenterDatastore					: $(".w2ui-msg-body input[name='vCenterDatastore']").val(),
			vCenterPersistentDatastore	: $(".w2ui-msg-body input[name='vCenterPersistentDatastore']").val(),
			vCenterDiskPath					: $(".w2ui-msg-body input[name='vCenterDiskPath']").val(),
			vCenterCluster						: $(".w2ui-msg-body input[name='vCenterCluster']").val()
	}
	
	if(popupValidation()){		
		$.ajax({
			type : "PUT",
			url : "/deploy/bosh/install/saveVSphereInfo/N",
			contentType : "application/json",
			async : true,
			data : JSON.stringify(vSphereInfo),
			success : function(data, status) {
				boshId = data.id;
				defaultInfoPopup();
			},
			error : function( e, status ) {
				w2alert("vSphere 설정 등록에 실패 하였습니다.", "BOSH 설치");
			}
		});
	}
}

/********************************************************
 * 설명			: LIST 형식의 BOSH 릴리즈 설정
 * Function	: setReleaseVersionList
 *********************************************************/
function setReleaseVersionList(){
	$(".w2ui-msg-body input[name='releaseVersion']").w2field('list', { items: releases , maxDropHeight:200, width:200});
	setReleaseData();
}

/********************************************************
 * 설명			: LIST 형식의 BOSH 릴리즈에 데이터를 설정
 * Function	: setReleaseData
 *********************************************************/
function setReleaseData(){
	if(!checkEmpty(boshInfo.releaseVersion)){
		$(".w2ui-msg-body input[name='releaseVersion']").data('selected', {text:boshInfo.releaseVersion});
	}
	w2popup.unlock();
}
	
/********************************************************
 * 설명			: 기본 정보 Popup
 * Function	: defaultInfoPopup
 *********************************************************/
function defaultInfoPopup(){
	if(iaas.toUpperCase() != "OPENSTACK"){
		$('#directorNameDiv').hide();
	}else if(iaas.toUpperCase() == "OPENSTACK"){
		$('#directorNameDiv').show();
	}
	settingPopupTab("defaultInfoDiv", iaas);
	
	$("#defaultInfoDiv").w2popup({
		width 	: 700,
		height	: 464,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
				if(boshInfo != ""){
					$(".w2ui-msg-body input[name='deploymentName']").val(boshInfo.deploymentName);
					$(".w2ui-msg-body input[name='directorUuid']").val($("#directorUuid").text());
					$(".w2ui-msg-body input[name='ntp']").val(boshInfo.ntp);
					$(".w2ui-msg-body input[name='snapshotSchedule']").val(boshInfo.snapshotSchedule);
					$(".w2ui-msg-body input[name='directorName']").val(boshInfo.directorName);
					if( !checkEmpty(boshInfo.enableSnapshots) ){
						$('.w2ui-msg-body input:radio[name=enableSnapshots]:input[value='+boshInfo.enableSnapshots+']').attr("checked",true);
						enableSnapshotsFn(boshInfo.enableSnapshots);
					}else{
						$('input:radio[name=enableSnapshots]:input[value=false]').attr("checked", true);
						enableSnapshotsFn("false");
					}
					
				} else {
					if( !checkEmpty($("#directorUuid").text()) ){
						$(".w2ui-msg-body input[name='directorUuid']").val($("#directorUuid").text());
						$('input:radio[name=enableSnapshots]:input[value=false]').attr("checked", true);
						enableSnapshotsFn("false");
					}
				}
				
				w2popup.lock("릴리즈를 조회 중입니다.", true);
				getReleaseVersionList();
				$('[data-toggle="popover"]').popover();
				getBoshReleaseVersionList();
			}
		},
		onClose : popupClose
	});
}

/********************************************************
 * 설명 :  Bosh 릴리즈 버전 목록 정보 조회
 * Function : getReleaseVersionList
 *********************************************************/
function getBoshReleaseVersionList(){
	var contents = "";
	$.ajax({
		type :"GET",
		url :"/common/deploy/list/releaseInfo/bosh/"+iaas, 
		contentType :"application/json",
		success :function(data, status) {
			if (data != null && data != "") {
				contents = "<table id='popoverTable'><tr><th>릴리즈 유형</th><th>릴리즈 버전</th></tr>";
				data.map(function(obj) {
					contents += "<tr><td>" + obj.releaseType+ "</td><td>" +  obj.minReleaseVersion +"</td></tr>";
				});
				contents += "</table>";
				$('.boshRelase-info').attr('data-content', contents);
			}
		},
		error :function(request, status, error) {
			var errorResult = JSON.parse(request.responseText);
			w2alert(errorResult.message, "bosh 릴리즈 정보 목록 조회");
		}
	});
	
}

/********************************************************
 * 설명			: 스냅샵 가능 사용여부(사용일 경우)
 * Function	: enableSnapshotsFn
 *********************************************************/
function enableSnapshotsFn(value){
	if(value == "true"){
		$(".w2ui-msg-body .snapshotScheduleDiv").show();
	}else if(value == "false"){
		$(".w2ui-msg-body  input[name=snapshotSchedule]").val("");
		$(".w2ui-msg-body .snapshotScheduleDiv").hide();
	}
}

/********************************************************
 * 설명			: Get Releases List Info
 * Function	: getReleaseVersionList
 *********************************************************/
function getReleaseVersionList(){
	$.ajax({
		type : "GET",
		url : "/common/deploy/release/list/bosh",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		data : JSON.stringify(boshInfo), 
		success : function(data, status) {
			releases = new Array();
			if(data.records != null){
				data.records.map(function (obj){
					releases.push(obj.name+"/"+obj.version);
				});
			}
			setReleaseVersionList();
		},
		error : function( e, status ) {
			w2popup.unlock();
			w2alert("Release Version List 를 가져오는데 실패하였습니다.", "BOSH 설치");
		}
	});
}

/********************************************************
 * 설명			: 기본정보 저장
 * Function	: saveDefaultInfo
 *********************************************************/
function saveDefaultInfo(type){	
  	for(var i=0;i<boshDeploymentName.length;i++){
		if($(".w2ui-msg-body input[name='deploymentName']").val()==boshDeploymentName[i]
		&& boshInfo.deploymentName != $(".w2ui-msg-body input[name='deploymentName']").val() ){
			w2alert("중복 된  배포 파일 명 입니다. ", "BOSH 설치");
			return;
		}
	}
	boshInfo = {
			id				: boshId,
			iaas 			: iaas,
			deploymentName	: $(".w2ui-msg-body input[name='deploymentName']").val(),
			directorUuid 	: $(".w2ui-msg-body input[name='directorUuid']").val(),
			releaseVersion	: $(".w2ui-msg-body input[name='releaseVersion']").val(),
			ntp						: $(".w2ui-msg-body input[name=ntp]").val(),
			directorName			: $(".w2ui-msg-body input[name=directorName]").val(),
			enableSnapshots	: $(".w2ui-msg-body input:radio[name=enableSnapshots]:checked").val(),
			snapshotSchedule : $(".w2ui-msg-body input[name=snapshotSchedule]").val()
	}
	if ( type == 'after') {
		if(popupValidation()){
			$.ajax({
				type : "PUT",
				url : "/deploy/bosh/install/saveDefaultInfo",
				contentType : "application/json",
				async : true,
				data : JSON.stringify(boshInfo), 
				success : function(data, status) {
					boshId = data.id;
					w2popup.clear();
 					if(data.iaasType == "VSPHERE"){
						vSpherenetworkInfoPopup();
					}else{
						networkInfoPopup();
					} 
				},
				error : function( e, status ) {
					w2alert("기본정보 등록 실패하였습니다.", "BOSH 설치");
				}
			});
		}
	} else {
		w2popup.clear();
		if(iaas == "AWS"){
			awsPopup(); return;
		}else if(iaas == "OPENSTACK"){
			openstackPopup(); return;	
		}else if(iaas == "VSPHERE"){
			vSpherePopup(); return;
		}
	}
} 

/********************************************************
 * 설명			: 네트워크 Popup
 * Function	: networkInfoPopup
 *********************************************************/
function networkInfoPopup(){
	settingPopupTab("networkInfoDiv", iaas);
	
	$("#networkInfoDiv").w2popup({
		width 	: 700,
		height	: 695,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function() {
				if(iaas.toUpperCase() == "AWS"){
					$(".w2ui-msg-body input[name='publicStaticIp']").attr("placeholder", "Elastic IP를 입력하세요."); 
				}
				
				if (networkInfo.length > 0) {
					networkId = networkInfo[0].id;
					for(var i=0; i <networkInfo.length; i++){
						if( (networkInfo[i].net).toLowerCase() == "external" ){
							$(".w2ui-msg-body input[name='publicStaticIp']").val(networkInfo[i].subnetStaticFrom); 
						}else{
							var cnt = i-1;
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
		onClose : popupClose
	});
}

/********************************************************
 * 설명			: vSphere네트워크 정보 저장
 * Function	: vSpherenetworkInfoPopup
 *********************************************************/
function vSpherenetworkInfoPopup(){
	settingPopupTab("vSpherenetworkInfoDiv", iaas);
	
	$("#vSpherenetworkInfoDiv").w2popup({
		width 	: 700,
		height	: 580,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function() {
				if (networkInfo.length > 0) {
					networkId = networkInfo[0].id;
					for(var i=0; i <networkInfo.length; i++){
						if( (networkInfo[i].net).toLowerCase() == "external" ){
							$(".w2ui-msg-body input[name='publicStaticIp']").val(networkInfo[i].subnetStaticTo); 
							$(".w2ui-msg-body input[name='publicSubnetRange']").val(networkInfo[i].subnetRange);
							$(".w2ui-msg-body input[name='publicSubnetGateway']").val(networkInfo[i].subnetGateway);
							$(".w2ui-msg-body input[name='publicSubnetDns']").val(networkInfo[i].subnetDns);
							$(".w2ui-msg-body input[name='publicSubnetId']").val(networkInfo[i].subnetId);
						}else{
							var cnt = i-1;
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
		onClose : popupClose
	});
}


/********************************************************
 * 설명			: 네트워크 정보 저장
 * Function	: saveNetworkInfo
 *********************************************************/
function saveNetworkInfo(type){
	networkInfo = [];
	 var subnetStaticFrom = "";
	 if( iaas.toUpperCase() == "VSPHERE" ){
		 subnetStaticFrom = $(".w2ui-msg-body input[name='publicStaticFrom']").val();
	 }else{
		 subnetStaticFrom = $(".w2ui-msg-body input[name='publicStaticIp']").val();
	 }
	 publicStaticIp = subnetStaticFrom;
	 //External 
	 if(iaas!="VSPHERE"){
		 var ExternalArr = {
					boshId 					: boshId,
					id						: networkId,
					iaas					: iaas.toUpperCase(),
					deployType				: 1500,
					net						: "External",
					subnetStaticFrom		: subnetStaticFrom,
		 }
		 networkInfo.push(ExternalArr);
	 }else if(iaas == "VSPHERE"){
		 var ExternalArr = {
				 	boshId 					: boshId,
					id						: networkId,
					iaas					: iaas.toUpperCase(),
					deployType				: 1500,
					net						: "External",
					subnetStaticTo			: $(".w2ui-msg-body input[name='publicStaticIp']").val(),
					subnetRange				: $(".w2ui-msg-body input[name='publicSubnetRange']").val(),
					subnetGateway			: $(".w2ui-msg-body input[name='publicSubnetGateway']").val(),
					subnetDns				: $(".w2ui-msg-body input[name='publicSubnetDns']").val(),
					subnetId				: $(".w2ui-msg-body input[name='publicSubnetId']").val(),
		 }
		 networkInfo.push(ExternalArr);
	 }
	 //Internal
	 for(var i=0; i < $(".w2ui-msg-body input[name='subnetReservedFrom']").length; i++){
		var  InternalArr = {
					boshId 					: boshId,
					id						: networkId,
					iaas					: iaas.toUpperCase(),
					deployType				: 1500,
					net						: "Internal",
					seq						: i,
					subnetRange				: $(".w2ui-msg-body input[name='subnetRange']").eq(i).val(),
					subnetGateway			: $(".w2ui-msg-body input[name='subnetGateway']").eq(i).val(),
					subnetDns				: $(".w2ui-msg-body input[name='subnetDns']").eq(i).val(),
					subnetReservedFrom		: $(".w2ui-msg-body input[name='subnetReservedFrom']").eq(i).val(),
					subnetReservedTo		: $(".w2ui-msg-body input[name='subnetReservedTo']").eq(i).val(),
					subnetStaticFrom		: $(".w2ui-msg-body input[name='subnetStaticFrom']").eq(i).val(),
					subnetStaticTo			: $(".w2ui-msg-body input[name='subnetStaticTo']").eq(i).val(),
					subnetId				: $(".w2ui-msg-body input[name='subnetId']").eq(i).val(),
			}
		 networkInfo.push(InternalArr);
	 }
	
	if ( type == 'after' ) {
		if(popupValidation()){
			$.ajax({
				type : "PUT",
				url : "/deploy/bosh/install/saveNetworkInfo",
				contentType : "application/json",
				async : true,
				data : JSON.stringify(networkInfo), 
				success : function(data, status) {
					w2popup.clear();
					if(iaas == "VSPHERE"){
						vSphereresourceInfoPopup();
					}else{
						resourceInfoPopup();
					}
				},
				error : function( e, status ) {
					w2alert("네트워크 정보 등록 실패하였습니다.", "BOSH 설치");
				}
			});
		}
	} else {
		w2popup.clear();
		defaultInfoPopup();
	}
}

/********************************************************
  * 설명			: VSphere리소스정보 Popup
  * Function	:  saveResourceInfo
  *********************************************************/
function vSphereresourceInfoPopup(){
	settingPopupTab("vSphereResourceInfoDiv", iaas);
	
	$("#vSphereResourceInfoDiv").w2popup({
		width 	: 700,
		height	: 645,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
 				if(resourceInfo != ""){
 					$(".w2ui-msg-body input[name='mediumFlavor']").val(resourceInfo.mediumFlavor);
 					$(".w2ui-msg-body input[name='boshPassword']").val(resourceInfo.boshPassword);
 					$(".w2ui-msg-body input[name='smallFlavor']").val(resourceInfo.smallFlavor);
					$(".w2ui-msg-body input[name='smallFlavorRam']").val(resourceInfo.smallRam);
					$(".w2ui-msg-body input[name='smallFlavorDisk']").val(resourceInfo.smallDisk);
					$(".w2ui-msg-body input[name='smallFlavorCpu']").val(resourceInfo.smallCpu);
					$(".w2ui-msg-body input[name='mediumFlavorRam']").val(resourceInfo.mediumRam);
					$(".w2ui-msg-body input[name='mediumFlavorDisk']").val(resourceInfo.mediumDisk);
					$(".w2ui-msg-body input[name='mediumFlavorCpu']").val(resourceInfo.mediumCpu);
 				}
 				/* w2popup.lock("스템셀을 조회 중입니다.", true); */
				getStemcellList(iaas);
			}
		},
		onClose : popupClose
	});
}

/********************************************************
  * 설명			: 리소스정보 Popup
  * Function	:  resourceInfoPopup
  *********************************************************/
function resourceInfoPopup(){
	settingPopupTab("ResourceInfoDiv", iaas);
	
	$("#ResourceInfoDiv").w2popup({
		width 	: 700,
		height	: 537,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
 				if(resourceInfo != ""){
 					$(".w2ui-msg-body input[name='mediumFlavor']").val(resourceInfo.mediumFlavor);
 					$(".w2ui-msg-body input[name='boshPassword']").val(resourceInfo.boshPassword);
 					$(".w2ui-msg-body input[name='smallFlavor']").val(resourceInfo.smallFlavor);
 				}
 				/* w2popup.lock("스템셀을 조회 중입니다.", true); */
				getStemcellList(iaas);
			}
		},
		onClose : popupClose
	});
}

/********************************************************
 * 설명			: 스템셀 목록 정보 설정
 * Function	: getStemcellList
 *********************************************************/
function getStemcellList(iaas){
	var url = "/common/deploy/stemcell/list/bosh/" + iaas;
	$.ajax({
		type : "GET",
		url : url,
		contentType : "application/json",
		//dataType: "json",
		async : true,
		success : function(data, status) {
			stemcells = new Array();
			if(data.records != null){
				data.records.map(function (obj){
					var stemcell = obj.stemcellFileName + "/" +obj.stemcellVersion;
					stemcells.push(stemcell);
				});
			}
			$('#w2ui-popup input[name=stemcell][type=list]').w2field('list', { items: stemcells , maxDropHeight:200, width:200});
			setReourceData();
		},
		error : function( e, status ) {
			w2alert("스템셀 목록을 가져오는데 실패하였습니다.", "BOSH 설치");
		}
	});
}

/********************************************************
  * 설명			: Resource Info Setting
  * Function	:  setReourceData
  *********************************************************/
function setReourceData(){
	if(resourceInfo != ""){
		$(".w2ui-msg-body input[name='stemcell']").data('selected', {text:resourceInfo.stemcellName+"/"+resourceInfo.stemcellVersion});
		$(".w2ui-msg-body input[name='boshPassword']").val(resourceInfo.boshPassword);
	}
}

/********************************************************
  * 설명			: 리소스 저장
  * Function	:  saveResourceInfo
  *********************************************************/
function saveResourceInfo(type){
	var stemcellInfo = $(".w2ui-msg-body input[name='stemcell']").val().split("/");
	resourceInfo = {
			id								: boshId,
			iaas							: iaas.toUpperCase(),
			platform						: "bosh",
			stemcellName			: stemcellInfo[0],
			stemcellVersion			: stemcellInfo[1],
			boshPassword			: $(".w2ui-msg-body input[name='boshPassword']").val(),
			smallFlavor				: $(".w2ui-msg-body input[name='smallFlavor']").val(),
			smallCpu					: $(".w2ui-msg-body input[name='smallFlavorCpu']").val(),
			smallRam					: $(".w2ui-msg-body input[name='smallFlavorRam']").val(),
			smallDisk					: $(".w2ui-msg-body input[name='smallFlavorDisk']").val(),
			mediumFlavor				: $(".w2ui-msg-body input[name='mediumFlavor']").val(),
			mediumCpu				: $(".w2ui-msg-body input[name='mediumFlavorCpu']").val(),
			mediumRam				: $(".w2ui-msg-body input[name='mediumFlavorRam']").val(),
			mediumDisk				: $(".w2ui-msg-body input[name='mediumFlavorDisk']").val()
	}
	
	if ( type == 'after' ) {
		if( popupValidation()){
			$.ajax({
				type : "PUT",
				url : "/deploy/bosh/install/saveResourceInfo",
				contentType : "application/json",
				async : true,
				data : JSON.stringify(resourceInfo), 
				success : function(data, status) {
					deployFileName = data.content.deploymentFile;
					w2popup.clear();
					createSettingFile(data.content);
				},
				error : function( e, status ) {
					w2alert("리소스 정보 등록 실패하였습니다.", "BOSH 설치");
				}
			});
		}
	} else {
		w2popup.clear();
		if(iaas!="VSPHERE"){
			networkInfoPopup();	
		}else{
			vSpherenetworkInfoPopup();
		}
	}
}

/********************************************************
  * 설명		: Manifest 파일 생성
  * Function	:  createSettingFile
  *********************************************************/
function createSettingFile(data){
	deployInfo = {
			iaasType		 					: data.iaasType,
			deploymentFile					: data.deploymentFile
	}
	
	$.ajax({
		type : "POST",
		url : "/deploy/bosh/install/createSettingFile/"+ data.id+"/N",
		contentType : "application/json",
		async : true,
		data : JSON.stringify(deployInfo),
		success : function(status) {
			deployPopup();
		},
		error :function(request, status, error) {
			var errorResult = JSON.parse(request.responseText);
			w2alert(errorResult.message, "Bosh 배포 파일 생성");
			if(iaas!="VSPHERE"){
				resourceInfoPopup();
			}else{
				vSphereresourceInfoPopup();
			}
		}
	});
}

/********************************************************
  * 설명		: 배포파일 정보 POPUP
  * Function	:  deployPopup
  *********************************************************/
function deployPopup(){
	settingPopupTab("DeployDiv", iaas);
	
	$("#DeployDiv").w2popup({
		width 	: 700,
		height 	: 520,
		modal	: true,
		showMax : true,
		onClose : popupClose,
		onOpen : function(event){
			event.onComplete = function(){
				getDeployInfo();
			}
		}
	});
}

/********************************************************
  * 설명		: 입력된 정보를 바탕으로 Manifest 내용 조회 
  * Function	:  getDeployInfo
 *********************************************************/
function getDeployInfo(){
	$.ajax({
		type : "GET",
		url :"/common/use/deployment/"+deployFileName+"",
		contentType : "application/json",
		async : true,
		success : function(data, status) {
			if(status == "success"){
				$(".w2ui-msg-body #deployInfo").text(data);
			}
		},
		error : function( e, status ) {
			w2alert("Temp 파일을 가져오는 중 오류가 발생하였습니다. ", "BOSH 설치");
		}
	});
}

/********************************************************
  * 설명		: BOSH 설치 확인
  * Function	:  boshDeploy
  *********************************************************/
function boshDeploy(type){
	if(type == 'before'){
		w2popup.clear();
		if(iaas.toUpperCase() == "VSPHERE"){
			vSphereresourceInfoPopup();
		}else{
			resourceInfoPopup();
		}
		return;
	}else{
		w2confirm({
			msg			: "설치하시겠습니까?",
			title			: w2utils.lang('BOSH 설치'),
			yes_text		: "예",
			no_text		: "아니오",
			yes_callBack: installPopup
		});
	}
}

/********************************************************
  * 설명		: BOSH 설치 팝업
  * Function	:  installPopup
  *********************************************************/
var installClient = "";
  function installPopup(){
	settingPopupTab("installDiv", iaas);
	
	var message = "BOSH(배포명:" + boshInfo.deploymentName +  ") ";
	var requestParameter = {
			id : boshId,
			iaas: iaas
	};
	
	$("#installDiv").w2popup({
		width 	: 700,
		height 	: 520,
		modal	: true,
		showMax : true,
		onOpen : function(event){
			event.onComplete = function(){
				//deployFileName
				var socket = new SockJS('/deploy/bosh/install/boshInstall');
				installClient = Stomp.over(socket); 
				installClient.connect({}, function(frame) {
			        installClient.subscribe('/user/deploy/bosh/install/logs', function(data){
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
					    		if(installClient!=""){
					    			installClient.disconnect();
						    		installClient = "";
					    		}
								w2alert(message, "BOSH 설치");
					       	}
			        	}
			        });
			        installClient.send('/send/deploy/bosh/install/boshInstall', {}, JSON.stringify(requestParameter));
			    });
			}
		}, onClose : function(event){
			event.onComplete= function(){
				$("textarea").text("");
				w2ui['config_boshGrid'].reset();
				if( installClient != ""){
					installClient.disconnect();
					installClient = "";
				}
				popupClose();
			}
		}
	});
}

/********************************************************
  * 설명		: BOSH 삭제
  * Function	:  deletePopup
  *********************************************************/
var deleteClient = "";
function deletePopup(record, force){
	
	var requestParameter = {
			iaas:record.iaas, 
			id:record.id
	};
	
	if ( record.deployStatus == null || record.deployStatus == '' ) {
		// 단순 레코드 삭제
		var url = "/deploy/bosh/delete/data";
		$.ajax({
			type : "DELETE",
			url : url,
			data : JSON.stringify(requestParameter),
			contentType : "application/json",
			success : function(data, status) {
				boshDeploymentName = [];
				doSearch();
			},
			error : function(request, status, error) {
				var errorResult = JSON.parse(request.responseText);
				w2alert(errorResult.message, "BOSH 삭제");
			}
		});
	} else {
		var message = "";
		var body = '<textarea id="deleteLogs" style="width:95%;height:90%;overflow-y:visible;resize:none;background-color: #FFF; margin:2%" readonly="readonly"></textarea>';
		w2popup.open({
			width : 700,
			height : 500,
			title : "<b>BOSH 삭제</b>",
			body  : body,
			buttons : '<button class="btn" style="float: right; padding-right: 15%;" onclick="popupComplete();">닫기</button>',
			showMax : true,
			onOpen : function(event){
				event.onComplete = function(){
					var socket = new SockJS('/deploy/bosh/delete/instance');
					deleteClient = Stomp.over(socket); 
					deleteClient.connect({}, function(frame) {
						deleteClient.subscribe('/user/deploy/bosh/delete/logs', function(data){
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
						    		if( deleteClient != ""){
						    			deleteClient.disconnect();
						    			deleteClient = "";
						    		}
									w2alert(message, "BOSH 삭제");
						       	}
				        	}
				        });
						deleteClient.send('/send/deploy/bosh/delete/instance', {}, JSON.stringify(requestParameter));
				    });
				}
			}, onClose : function (event){
				event.onComplete= function(){
					boshDeploymentName = [];
					$("textarea").text("");
					w2ui['config_boshGrid'].reset();
					if( deleteClient != ""){
						deleteClient.disconnect();
						deleteClient = "";
					}
					popupClose();
				}
			}
		});
	}		
}

/******************************************************************
 * Function : settingPopupTab
 * 설명		  : 설치 팝업 화면에서 IaaS Tab명 설정 
 ***************************************************************** */
function settingPopupTab(div, iaas){
	 var tab= "";
	 if(iaas.toUpperCase() == "AWS") {
		 tab = "AWS";
	} else if (iaas.toUpperCase() == "OPENSTACK"){
		tab = "오픈스택";
	} else if(iaas.toUpperCase() == "VSPHERE"){
		tab = "vSphere";
	}
	$("#"+div+" .progressStep_6 li").first().html( tab + " 정보" );
}


/********************************************************
 * 설명 :  popupComplete
 * Function : 설치 닫기
 *********************************************************/
function popupComplete(){
	var msg;
	if(installStatus == "done" || installStatus == 'error'){
		msg = $(".w2ui-msg-title b").text() + " 화면을 닫으시겠습니까?";
	}else{
		msg = $(".w2ui-msg-title b").text() + " 화면을 닫으시겠습니까?<BR>(닫은 후에도 완료되지 않는 설치 또는 삭제 작업은 계속 진행됩니다.)";
	}
	w2confirm({
		title 	: $(".w2ui-msg-title b").text(),
		msg		: msg,
		yes_text: "확인",
		yes_callBack : function(envent){
			//params init
			initSetting();
			//popup.close
			w2popup.close();
			//grid Reload
			gridReload();
		},
		no_text : "취소"
	});
}

/********************************************************
 * Function : popupClose
 * 설명 		 :  팝업 화면 닫기
 *********************************************************/
function popupClose() {
	//params init
	initSetting();
	//grid Reload
	gridReload();
}

/********************************************************
 * Function : initSetting  
 * 설명 		 :전역 변수 초기 설정
 *********************************************************/
function initSetting(){
	iaas = "";
	boshId = "";
	awsInfo = "";
	openstackInfo = "";
	vSphereInfo = "";
	boshInfo = "";
	networkInfo = [];
	publicStaticIp = "";
	internalCnt=0;
	resourceInfo = "";
	keyPathFileList = "";
	releasest = "";
	stemcellst = "";
	deploymentFilet = "";
	installStatus = "";
	deployInfo ="";
	deployFileName = "";
	iaasInfo = "";
	boshDeployName = [];
	installClient ="";
	deleteClient="";
}

</script>

	<!-- AWS 정보 -->
	<div id="awsInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 2%;display:inline-block;width: 97%;">
	            <ul class="progressStep_6" >
		            <li class="active">AWS 정보</li>
		            <li class="before">기본 정보</li>
		            <li class="before">네트워크 정보</li>
		            <li class="before">리소스 정보</li>
		            <li class="before">배포파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
	        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
		    	<form id="keyForm" data-toggle="validator" >
					<div class="panel panel-info">	
						<div class="panel-heading"><b>AWS 정보</b></div>
						<div class="panel-body" style="padding:5px 5% 10px 5%;">
					        <div class="w2ui-field">
					            <label style="text-align: left;width:40%;font-size:11px;">Access Key ID</label>
					            <div>
					                <input name="accessKeyId" type="text"  style="float:left;width:60%;" required placeholder="AWS Access Key를 입력하세요."/>
					            </div>
							</div>
					        <div class="w2ui-field">
					            <label style="text-align: left;width:40%;font-size:11px;">Secret Access Key</label>
					            <div>
					                <input name="secretAccessKey" type="password"  style="float:left;width:60%;" required placeholder="AWS Secret Access Key를 입력하세요."/>
					            </div>
					        </div>
					        <div class="w2ui-field">
					            <label style="text-align: left;width:40%;font-size:11px;">Security Group</label>
					            <div>
					                <input name="defaultSecurityGroups" type="text"  style="float:left;width:60%;" required placeholder="시큐리티 그룹을 입력하세요."/>
					            </div>
					        </div>
					        <div class="w2ui-field">
					            <label style="text-align: left;width:40%;font-size:11px;">Region</label>
					            <div>
					                <input name="region" type="text"  style="float:left;width:60%;"  required placeholder="설치할 Region을 입력하세요.(예: us-east-1)"/>
					            </div>
					        </div>
					        <div class="w2ui-field">
					            <label style="text-align: left;width:40%;font-size:11px;">AvailabilityZone</label>
					            <div>
					                <input name="availabilityZone" type="text"  style="float:left;width:60%;" required placeholder="AvailabilityZone를 입력하세요. (예: us-east-1a)"/>
					            </div>
					        </div>
					        <div class="w2ui-field">
					            <label style="text-align: left;width:40%;font-size:11px;">Private Key Name</label>
					            <div>
					                <input name="privateKeyName" type="text"  style="float:left;width:60%;" required placeholder="Key Pair 이름을 입력하세요."/>
					            </div>
					        </div>
				        </div>
			        </div>
		        </form>
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
		        <button class="btn" style="float: right;padding-right:15%" onclick="saveAwsInfo();" >다음>></button>
		    </div>
		</div>
	</div>
	<!-- End AWS 정보 -->

	<!-- 오픈스택 정보 -->
	<div id="openstackInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 2%;display:inline-block;width: 97%;">
	            <ul class="progressStep_6" >
		            <li class="active">오픈스택 정보</li>
		            <li class="before">기본 정보</li>
		            <li class="before">네트워크 정보</li>
		            <li class="before">리소스 정보</li>
		            <li class="before">배포파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
	        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
				<div class="panel panel-info">	
					<div class="panel-heading"><b>오픈스택 정보</b></div>
					<div class="panel-body" style="padding:5px 5% 10px 5%;">
				    	<form id="keyForm" data-toggle="validator" >
					        <div class="w2ui-field">
					            <label style="text-align: left;width:40%;font-size:11px;">Identify API Tokens URL</label>
					            <div>
					                <input name="authUrl" type="text"  style="float:left;width:60%;"  required placeholder="Identify API Tokens URL을 입력하세요."/>
					            </div>
					        </div>
					        
					        <div class="w2ui-field">
					            <label style="text-align: left;width:40%;font-size:11px;">Tenant</label>
					            <div>
					                <input name="tenant" type="text"  style="float:left;width:60%;"  required placeholder="Tenant명을 입력하세요."/>
					            </div>
					        </div>
					        
				        <div class="w2ui-field">
				            <label style="text-align: left;width:40%;font-size:11px;">Username</label>
				            <div>
				                <input name="userName" type="text"  style="float:left;width:60%;"  required placeholder="계정명을 입력하세요."/>
				            </div>
				        </div>
				        
				        <div class="w2ui-field">
				            <label style="text-align: left;width:40%;font-size:11px;">Password</label>
				            <div>
				                <input name="apiKey" type="password"  style="float:left;width:60%;"  required placeholder="계정 비밀번호를 입력하세요."/>
				            </div>
				        </div>
				        
				        <div class="w2ui-field">
				            <label style="text-align: left;width:40%;font-size:11px;">Security Group</label>
				            <div>
				                <input name="defaultSecurityGroups" type="text"  style="float:left;width:60%;"  required placeholder="시큐리티 그룹을 입력하세요."/>
				            </div>
				        </div>
				        
				        <div class="w2ui-field">
				            <label style="text-align: left;width:40%;font-size:11px;">Private Key Name</label>
				            <div>
				                <input name="privateKeyName" type="text"  style="float:left;width:60%;"  required placeholder="Key Pair명을 입력하세요."/>
				            </div>
				        </div>
			        </form>
		        </div>
	        </div>
	    </div>
		<br/>
	    <div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" style="float: right; padding-right: 15%" onclick="saveOpenstackInfo();"  >다음>></button>
	    </div>
	</div>
</div>
<!-- End Bosh OPENSTACK POP -->

<!-- Start Vsphere Popup  -->
<div id="vSphereInfoDiv" style="width:100%;height:100%;" hidden="true">
	<div rel="title"><b>BOSH 설치</b></div>
	<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
		<div style="margin-left: 2%;display:inline-block;width: 97%;">
            <ul class="progressStep_6" >
	            <li class="active">vSphere 정보</li>
	            <li class="before">기본 정보</li>
	            <li class="before">네트워크 정보</li>
	            <li class="before">리소스 정보</li>
	            <li class="before">배포파일 정보</li>
	            <li class="before">설치</li>
            </ul>
        </div>
        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
			<div class="panel panel-info">	
				<div class="panel-heading"><b>VSPHERE 정보</b></div>
				<div class="panel-body" style="padding:5px 5% 10px 5%;">
			    	<form id="keyForm" data-toggle="validator" >
						<div class="w2ui-field">
							<label style="text-align: left;width:40%;font-size:11px;">vCenter IP</label>
							<div>
								<input name="vCenterAddress" type="text"  style="float:left;width:60%;"  required placeholder="vCenter IP를 입력하세요."/>
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left;width:40%;font-size:11px;">vCenter ID</label>
							<div>
								<input name="vCenterUser" type="text"  style="float:left;width:60%;"  required placeholder="vCenter 로그인 ID를 입력하세요."/>
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left;width:40%;font-size:11px;">vCenter Password</label>
							<div>
								<input name="vCenterPassword" type="password" style="float:left;width:60%;"   required placeholder="vCenter 로그인 비밀번호를 입력하세요."/>
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left;width:40%;font-size:11px;">vCenter DataCenter Name</label>
							<div>
								<input name="vCenterName" type="text"  style="float:left;width:60%;" required placeholder="vCenter DataCenter명을 입력하세요."/>
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left;width:40%;font-size:11px;">DataCenter VM Folder Name</label>
							<div>
								<input name="vCenterVMFolder" type="text"  style="float:left;width:60%;"  required placeholder="vCenter VM 폴더명을 입력하세요."/>
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left;width:40%;font-size:11px;">DataCenter VM Stemcell Folder Name</label>
							<div>
								<input name="vCenterTemplateFolder" type="text"  style="float:left;width:60%;"  required placeholder="vCenter 스템셀 폴더명을 입력하세요."/>
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left;width:40%;font-size:11px;">DataCenter DataStore</label>
							<div>
								<input name="vCenterDatastore" type="text"  style="float:left;width:60%;"  required placeholder="vCenter 데이터 스토어를 입력하세요."/>
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left;width:40%;font-size:11px;">vCenter Persistent Datastore</label>
							<div>
								<input name="vCenterPersistentDatastore" type="text"  style="float:left;width:60%;"  required placeholder="vCenter 영구 데이터 스토어를 입력하세요."/>
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left;width:40%;font-size:11px;">vCenter Disk Path</label>
							<div>
								<input name="vCenterDiskPath" type="text"  style="float:left;width:60%;"  required placeholder="vCenter 디스크 경로를 입력하세요."/>
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left;width:40%;font-size:11px;">vCenter Cluster</label>
							<div>
								<input name="vCenterCluster" type="text"  style="float:left;width:60%;"  required placeholder="vCenter 클러스터명을 입력하세요."/>
								<div class="isMessage"></div>
							</div>
						</div>
			        </form>
		        </div>
	        </div>
	    </div>
		<br/>
	   		 <div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" style="float: right; padding-right: 15%" onclick="saveVsphereInfo();">다음>></button>
	    </div>
	</div>
</div>
<!-- End Vsphere Popup -->

<!-- 기본 정보 -->
<div id="defaultInfoDiv" style="width:100%;height:100%;" hidden="true">
	<div rel="title"><b>BOSH 설치</b></div>
	<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
		<div style="margin-left: 2%;display:inline-block;width: 97%;">
            <ul class="progressStep_6" >
	            <li class="pass"></li>
	            <li class="active">기본 정보</li>
	            <li class="before">네트워크 정보</li>
	            <li class="before">리소스 정보</li>
	            <li class="before">배포파일 정보</li>
	            <li class="before">설치</li>
            </ul>
        </div>
         <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
			<div class="panel panel-info">	
				<div class="panel-heading"><b>기본 정보</b></div>
				<div class="panel-body" style="padding:5px 5% 10px 5%;">
			    	<form id="osBoshForm">
				        <div class="w2ui-field">
				            <label style="text-align: left;width:40%;font-size:11px;">설치관리자 UUID</label>
				            <div>
				                <input name="directorUuid" type="text"  style="float:left;width:60%;"  required placeholder="설치관리자 UUID입력하세요." readonly="readonly"/>
				                <div class="isMessage"></div>
				            </div>
				        </div>
				        <div class="w2ui-field">
				            <label style="text-align: left;width:40%;font-size:11px;">배포명</label>
				            <div>
				                <input name="deploymentName" type="text"  style="float:left;width:60%;"  required placeholder="배포명을 입력하세요."/>
				                <div class="isMessage"></div>
				            </div>
				        </div>
				        <div class="w2ui-field" id="directorNameDiv">
						<label style="text-align: left;width:40%;font-size:11px;">디렉터 명</label>
						<div>
							<input name="directorName" type="text"  style="float:left;width:60%;" required placeholder="디렉터 명을 입력하세요."/>
							<div class="isMessage"></div>
						</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left;width:40%;font-size:11px;">NTP</label>
							<div>
								<input name="ntp" type="text"  style="float:left;width:60%;"  required placeholder="NTP를 입력하세요. "/>
								<div class="isMessage"></div>
							</div>
						</div>
				        <div class="w2ui-field">
				            <label style="text-align: left;width:40%;font-size:11px;">BOSH 릴리즈</label>
				            <img alt="boshRelase-help-info" class="boshRelase-info" style="width:18px; position:absolute; left:19%; margin-top:5px" data-trigger="hover" data-toggle="popover" title="설치 지원 버전 목록"  data-html="true" src="../images/help-Info-icon.png">	
				            <div>
				                <input name="releaseVersion" type="list"  style="float:left;width:60%;" required placeholder="BOSH 릴리즈를 선택하세요." />
				            </div>
				        </div>
				        <div class="w2ui-field">
						<label style="text-align: left;width:40%;font-size:11px;">스냅샷기능 사용여부</label>
		                <div>
	  						<span onclick="enableSnapshotsFn('true');" style="width:30%;"><label><input type="radio" name="enableSnapshots" value="true" />&nbsp;사용</label></span>
							&nbsp;&nbsp;
							<span onclick="enableSnapshotsFn('false');" style="width:30%;"><label><input type="radio" name="enableSnapshots" value="false" />&nbsp;미사용</label></span>
						</div>
				        </div>
				        <div class="w2ui-field snapshotScheduleDiv" id="snapshotScheduleDiv">			         	
				           <label style="text-align: left;width:40%;font-size:11px;">스냅샷 스케쥴</label>
							<div>
								<input name="snapshotSchedule" id="snapshotSchedule" type="text"  style="float:left;width:60%;" required placeholder="예) 0 0 7 * * * UTC"/>
								<div class="isMessage"></div>
							</div>
				        </div>
			        </form>
		        </div>
	        </div>
	    </div>
		<br/>
	    <div class="w2ui-buttons" rel="buttons" hidden="true">
	    	<button class="btn" style="float: left;" onclick="saveDefaultInfo('before');">이전</button>
	        <button class="btn" style="float: right;padding-right:15%" onclick="saveDefaultInfo('after');">다음>></button>
	    </div>
	</div>
</div>	

<!-- aws/openstack 네트워크 정보 -->
<div id="networkInfoDiv" style="width:100%;height:100%;" hidden="true">
	<div rel="title"><b>BOSH 설치</b></div>
	<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
		<div style="margin-left: 2%;display:inline-block;width: 97%;">
            <ul class="progressStep_6" >
	            <li class="pass"></li>
	            <li class="pass">기본 정보</li>
	            <li class="active">네트워크 정보</li>
	            <li class="before">리소스 정보</li>
	            <li class="before">배포파일 정보</li>
	            <li class="before">설치</li>
            </ul>
        </div>
		<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
					<div class="panel panel-info">	
						<div  class="panel-heading" style="padding:5px 5% 10px 5%;"><b>External</b></div>
						<div class="panel-body">
							<div class="w2ui-field">
								<label id="ExternalLabel" style="text-align: left;width:40%;font-size:11px;">설치관리자 IPs</label> 
								<div>
									<input name="publicStaticIp" type="text"  style="float:left;width:60%;"  required placeholder="예) 10.0.0.20"/>
									<div class="isMessage"></div>
								</div>
							</div>
						</div>
					</div>
					<div class="panel panel-info" id="panel-body">	
						<div  class="panel-heading" style="padding:5px 5% 10px 5%;">
							<b>Internal</b>
						</div>
						<div class="panel-body">
					    	<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">서브넷 아이디</label>
								<div>
									<input name="subnetId" type="text"  style="float:left;width:60%;" required placeholder="서브넷 아이디를 입력하세요."/>
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
									<input name="subnetReservedFrom"  type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.100" />
									<span style="float: left; width: 6%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
									<input name="subnetReservedTo"  type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.106" />
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left; width: 40%; font-size: 11px;">IP할당 대역</label>
								<div>
									<input name="subnetStaticFrom" type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.100" />
									<span style="float: left; width: 6%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
									<input name="subnetStaticTo" type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.106" />
									<div class="isMessage"></div>
								</div>
							</div>
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

<!--VSPHERE 네트워크 정보 -->
<div id="vSpherenetworkInfoDiv" style="width:100%;height:100%;" hidden="true">
	<div rel="title"><b>BOSH 설치</b></div>
	<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
		<div style="margin-left: 2%;display:inline-block;width: 97%;">
            <ul class="progressStep_6" >
	            <li class="pass"></li>
	            <li class="pass">기본 정보</li>
	            <li class="active">네트워크 정보</li>
	            <li class="before">리소스 정보</li>
	            <li class="before">배포파일 정보</li>
	            <li class="before">설치</li>
            </ul>
        </div>
		<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
					<div class="panel panel-info">	
						<div  class="panel-heading" style="padding:5px 5% 10px 5%;"><b>External</b></div>
						<div class="panel-body">
							<div class="w2ui-field">
								<label id="ExternalLabel" style="text-align: left;width:40%;font-size:11px;">설치관리자 IPs</label> 
								<div>
									<input name="publicStaticIp" type="text"  style="float:left;width:60%;"  required placeholder="예) 10.0.0.20"/>
									<div class="isMessage"></div>
								</div>
							</div>
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
									<input name="publicSubnetRange" type="text"  style="float:left;width:60%;"  required placeholder="예) 10.0.0.0/24"/>
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
									<input name="publicSubnetDns" type="text"  style="float:left;width:60%;" required placeholder="예) 8.8.8.8"/>
									<div class="isMessage"></div>
								</div>
							</div>
							
						</div>
					</div>
					<div class="panel panel-info" id="panel-body">	
						<div  class="panel-heading" style="padding:5px 5% 10px 5%;">
							<b>Internal</b>
						</div>
						<div class="panel-body">
					    	<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">포트 그룹명</label>
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
									<input name="subnetReservedFrom"  type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.100" />
									<span style="float: left; width: 6%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
									<input name="subnetReservedTo"  type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.106" />
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left; width: 40%; font-size: 11px;">IP할당 대역</label>
								<div>
									<input name="subnetStaticFrom" type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.100" />
									<span style="float: left; width: 6%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
									<input name="subnetStaticTo" type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.106" />
									<div class="isMessage"></div>
								</div>
							</div>
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


<!-- 리소스 정보 -->
<div id="ResourceInfoDiv" style="width:100%;height:100%;" hidden="true">
	<div rel="title"><b>BOSH 설치</b></div>
	<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
		<div style="margin-left:2%;display:inline-block;width:97%;">
            <ul class="progressStep_6" >
	            <li class="pass">오픈스택 정보</li>
	            <li class="pass">기본 정보</li>
	            <li class="pass">네트워크 정보</li>
	            <li class="active">리소스 정보</li>		            
	            <li class="before">배포 파일 정보</li>
	            <li class="before">설치</li>
            </ul>
        </div>
        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
			<div class="panel panel-info">	
				<div class="panel-heading"><b>리소스 정보</b></div>
				<div class="panel-body" style="padding:5px 5% 10px 5%;">
			    	<div class="w2ui-field">
						<label style="text-align: left;width:40%;font-size:11px;">스템셀</label>
						<div>
							<div>
								<input type="list" name="stemcell" style="float:left; width:65%;margin-top:1.5px;"  required placeholder="스템셀을 선택하세요."/>
							</div>
						</div>
					</div>
											
					<div class="w2ui-field">
						<label style="text-align: left;width:40%;font-size:11px;">VM 비밀번호</label>
						<div>
							<input name="boshPassword" type="text"  style="float:left;width:65%;"  required placeholder="VM 비밀번호를 입력하세요."/>
							<div class="isMessage"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="panel panel-info">	
				<div class="panel-heading"><b>Small Instance Type</b></div>
				<div class="panel-body"  style="padding:5px 5% 10px 5%;">
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">Flavor</label>
						<div>
							<input name="smallFlavor" type="text" style="float: left; width: 60%;" required placeholder="Small Instance Type을 입력하세요."  />
							<div class="isMessage"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="panel panel-info">	
				<div class="panel-heading"><b>director Instance Type</b></div>
					<div class="panel-body"  style="padding:5px 5% 10px 5%;" >
						<div class="w2ui-field">
							<label style="text-align: left; width: 40%; font-size: 11px;">Flavor</label>
							<div>
								<input name="mediumFlavor" type="text" style="float: left; width: 60%;" required placeholder="director Instance Type을 입력하세요."  />
								<div class="isMessage"></div>
							</div>
						</div>
					</div>
			</div>
	    </div>
		<br/>
	    <div class="w2ui-buttons" rel="buttons" hidden="true">
	        <button class="btn" style="float: left;" onclick="saveResourceInfo('before');" >이전</button>
			<button class="btn" style="float: right; padding-right: 15%" onclick="saveResourceInfo('after');" >다음>></button>
	    </div>
	</div>
</div>

<!--  vSphere Resource -->
<div id="vSphereResourceInfoDiv" style="width: 100%; height: 100%;" hidden="true">
	<div rel="title"><b>BOSH 설치</b></div>
	<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
		<div style="margin-left:2%;display:inline-block;width:97%;">
            <ul class="progressStep_6" >
	            <li class="pass">오픈스택 정보</li>
	            <li class="pass">기본 정보</li>
	            <li class="pass">네트워크 정보</li>
	            <li class="active">리소스 정보</li>		            
	            <li class="before">배포 파일 정보</li>
	            <li class="before">설치</li>
            </ul>
        </div>
		<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
			<div class="panel panel-info">	
				<div class="panel-heading"><b>리소스 정보</b></div>
				<div class="panel-body" style="padding:5px 5% 10px 5%;">
					<div class="w2ui-field">
						<label style="text-align: left;width:40%;font-size:11px;">스템셀</label>
						<div>
							<div>
								<input type="list" name="stemcell" style="float:left; width:65%;margin-top:1.5px;"  required placeholder="스템셀을 선택하세요."/>
							</div>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">VM 비밀번호</label>
						<div>
							<input name="boshPassword" type="text" style="float: left; width: 65%;"  required placeholder="VM 비밀번호를 입력하세요." />
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
							<input name="smallFlavorRam" type="text" style="float: left; width: 65%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Ram을 입력하세요. 예) 1024"  />
							<div class="isMessage"></div>
						</div>
					</div>
					<div class="w2ui-field">	
						<label style="text-align: left;  width: 40%; ">Small Type Disk</label>
						<div>
							<input name="smallFlavorDisk" type="text" style="float: left; width: 65%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Disk를 입력하세요. 예) 4096"  />
							<div class="isMessage"></div>
						</div>
					</div>
					<div class="w2ui-field">	
						<label style="text-align: left;  width: 40%;">Small Type Cpu</label>
						<div>
							<input name="smallFlavorCpu" type="text" style="float: left; width: 65%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Cpu를 입력하세요. 예) 1"  />
							<div class="isMessage"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="panel panel-info">	
				<div class="panel-heading"><b>Director Resource Type</b></div>
				<div class="panel-body"  style="padding:5px 5% 10px 5%;" >
					<div class="w2ui-field">
						<label style="text-align: left;  width: 40%;">Director Ram</label>
						<div>
							<input name="mediumFlavorRam" type="text" style="float: left; width: 65%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Ram을 입력하세요. 예) 4096"  />
							<div class="isMessage"></div>
						</div>
					</div>	
					<div class="w2ui-field">		
						<label style="text-align: left;  width: 40%;">Director Disk</label>
						<div>
							<input name="mediumFlavorDisk" type="text" style="float: left; width: 65%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Disk를 입력하세요. 예) 4096"  />
							<div class="isMessage"></div>
						</div>
					</div>
					<div class="w2ui-field">		
						<label style="text-align: left;  width: 40%;">Director Cpu</label>
						<div>
							<input name="mediumFlavorCpu" type="text" style="float: left; width: 65%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Cpu를 입력하세요. 예) 1"  />
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

<!-- 배포파일 정보 -->
<div id="DeployDiv" style="width:100%;height:100%;" hidden="true">
	<div rel="title"><b>BOSH 설치</b></div>
	<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
		<div style="margin-left: 2%;display:inline-block;width: 97%;">
            <ul class="progressStep_6" >
	            <li class="pass"></li>
	            <li class="pass">기본 정보</li>
	            <li class="pass">네트워크 정보</li>
	            <li class="pass">리소스 정보</li>
	            <li class="active">배포파일 정보</li>
	            <li class="before">설치</li>
            </ul>
        </div>
		<div style="width:95%;height:84%;float: left;display: inline-block;margin-top:1%;">
			<textarea id="deployInfo" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:3%" readonly="readonly"></textarea>
		</div>
	</div>
	<div class="w2ui-buttons" rel="buttons" hidden="true">
		<button class="btn" style="float: left;" onclick="boshDeploy('before');">이전</button>
		<button class="btn" style="float: right; padding-right: 15%" onclick="boshDeploy('after');">다음>></button>
	</div>
</div>

<!-- 설치화면 -->
<div id="installDiv" style="width:100%;height:100%;" hidden="true">
	<div rel="title"><b>BOSH 설치</b></div>
	<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
		<div style="margin-left: 2%;display:inline-block;width: 97%;">
            <ul class="progressStep_6">
	            <li class="pass"></li>
	            <li class="pass">기본 정보</li>
	            <li class="pass">네트워크 정보</li>
	            <li class="pass">리소스 정보</li>
	            <li class="pass">배포파일 정보</li>
	            <li class="active">설치</li>
            </ul>
        </div>
		<div style="width:95%;height:84%;float: left;display: inline-block;margin-top:1%;">
			<textarea id="installLogs" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:3%" readonly="readonly"></textarea>
		</div>
	</div>
	<div class="w2ui-buttons" rel="buttons" hidden="true">
		<button class="btn" id="deployPopupBtn" style="float: left;" onclick="deployPopup()" disabled>이전</button>
		<button class="btn" style="float: right; padding-right: 15%" onclick="popupComplete();">닫기</button>
	</div>
</div>	
