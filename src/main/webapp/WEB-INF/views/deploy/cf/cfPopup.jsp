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
<style>
	.popover{
		max-width:280px
	}
	.popover-content {
		width:275px;
 		max-height: 300px;
 		overflow-y: auto;
	}
</style>
<script type="text/javascript">

//setting variable
var cfId = "";
var networkId = "";
var defaultInfo = "";
var networkInfo = [];
var keyInfo="";
var publicStaticIp = "";
var internalCnt=1;
var resourceInfo = "";
var releases = "";
var stemcells = "";
var deploymentFile = "";
var installStatus ="";
var countryCodes = null;
var keyFile ="";

$(function() {
	
	$(document).delegate(".w2ui-popup","click",function(e){
	 $('[data-toggle="popover"]').each(function () {
	        //the 'is' for buttons that trigger popups
	        //the 'has' for icons within a button that triggers a popup
	        if (!$(this).is(e.target) && $(this).has(e.target).length === 0 && $('.popover').has(e.target).length === 0) {
	            $(this).popover('hide');
	        }
	    });
    });
	
});


/********************************************************
 * 설명 : CF 릴리즈 설치 지원 버전 목록 조회
 * Function : getReleaseVersionList
 *********************************************************/
function getReleaseVersionList(){
	 var contents = "";
	$.ajax({
		type :"GET",
		url :"/common/deploy/list/releaseInfo/cf/"+iaas, 
		contentType :"application/json",
		success :function(data, status) {
			if (data != null && data != "") {
				contents = "<table id='popoverTable'><tr><th>릴리즈 유형</th><th>릴리즈 버전</th></tr>";
				data.map(function(obj) {
					contents += "<tr><td>" + obj.releaseType+ "</td><td>" +  obj.minReleaseVersion +"</td></tr>";
				});
				contents += "</table>";
				$('.cf-info').attr('data-content', contents);
			}
		},
		error :function(request, status, error) {
			var errorResult = JSON.parse(request.responseText);
			w2alert(errorResult.message, "CF 릴리즈 설치 지원 버전");
		}
	});
}

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
		iaas                   : iaas,
		deploymentName         : contents.deploymentName,
		directorUuid           : contents.directorUuid,
		releaseName            : contents.releaseName,
		releaseVersion         : contents.releaseVersion,
		appSshFingerprint      : contents.appSshFingerprint,
		deaMemoryMB            : contents.deaMemoryMB,
		deaDiskMB              : contents.deaDiskMB,
		domain                 : contents.domain,
		description            : contents.description,
		domainOrganization     : contents.domainOrganization,		
		loginSecret            : contents.loginSecret,
		paastaMonitoringUse    : contents.paastaMonitoringUse,
		ingestorIp             : contents.ingestorIp,
		ingestorPort           : contents.ingestorPort
		
	}
	//네트워크 정보 
	for(var i=0; i<contents.networks.length; i++){
	 	var arr = {
	 		id                       : contents.id,
			deployType               : contents.networks[i].deployType,
			seq                      : i,
			net                      : contents.networks[i].net,
			publicStaticIp           : contents.networks[i].publicStaticIp,
			subnetRange              : contents.networks[i].subnetRange,
			subnetGateway            : contents.networks[i].subnetGateway,
			subnetDns                : contents.networks[i].subnetDns,
			subnetReservedFrom       : contents.networks[i].subnetReservedFrom,
			subnetReservedTo 		 : contents.networks[i].subnetReservedTo,
			subnetStaticFrom         : contents.networks[i].subnetStaticFrom,
			subnetStaticTo           : contents.networks[i].subnetStaticTo,
			subnetId                 : contents.networks[i].subnetId,
			cloudSecurityGroups      : contents.networks[i].cloudSecurityGroups,
			availabilityZone         : contents.networks[i].availabilityZone
		}
	 	networkInfo.push(arr);
	}
	
	keyFile = contents.keyFile;
	keyInfo = {
			countryCode					: contents.countryCode,
			stateName					: contents.stateName,
			localityName					: contents.localityName,
			organizationName			: contents.organizationName,
			unitName						: contents.unitName,
			email							: contents.email
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
		width : 750,
		height :820,
		modal : true,
		showMax : false,
		onOpen : function(event) {
			event.onComplete = function() {
				//릴리즈 정보 popup over
			 	$('[data-toggle="popover"]').popover();
			 	$(".paastaMonitoring-info").attr('data-content', "paasta-controller v2.0 이상에서 지원")
				
			 	getReleaseVersionList();
			 	//cf & diego 통합 설치일 경우 fingerprint readonly
			 	if( menu.toLowerCase() =="cfdiego" ){
			 		$(".w2ui-msg-body input[name='appSshFingerprint']").attr("readonly", true);
			 	}

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
					$(".w2ui-msg-body input[name='loginSecret']").val(defaultInfo.loginSecret);
					
					if( !checkEmpty(defaultInfo.ingestorIp) ){//PaaS-TA 모니터링 체크 
						$(".w2ui-msg-body input:checkbox[name='paastaMonitoring']").attr("checked", true);
						checkPaasTAMonitoringUseYn();
						$(".w2ui-msg-body input[name='ingestorIp']").val(defaultInfo.ingestorIp);
						$(".w2ui-msg-body input[name='ingestorPort']").val(defaultInfo.ingestorPort);
					}
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
	setDisabledMonitoring(defaultInfo.releaseName + "/"+ defaultInfo.releaseVersion);
	w2popup.unlock();
}


/********************************************************
 * 설명		: paasta-controller v2.0 이상에서 지원
 * Function	: setDisabledMonitoring
 *********************************************************/
function setDisabledMonitoring(val){
	 
	if( !checkEmpty(val) && val != "undefined/undefined"){
		var cfReleaseName = val.split("/")[0];
		var cfReleaseVersion = val.split("/")[1];
		
		//paasta-controller v2.0.0 이상 PaaS-TA 모니터링 지원 checkbox
		if( cfReleaseName.indexOf("paasta-controller") > -1 && compare(cfReleaseVersion, "2.0.0") > -1 ){
			$('.w2ui-msg-body #paastaMonitoring').attr('disabled',false);
		}else{
			if( $(".w2ui-msg-body input:checkbox[name='paastaMonitoring']").is(":checked")){
				$(".w2ui-msg-body input:checkbox[name='paastaMonitoring']").prop('checked',false);
				checkPaasTAMonitoringUseYn();
			}
			$('.w2ui-msg-body #paastaMonitoring').attr('disabled',true);
		}
	}
	
}

/********************************************************
 * 설명		: PaaS-TA 모니터링 사용 체크 검사
 * Function	: checkPaasTAMonitoringUseYn
 *********************************************************/
function checkPaasTAMonitoringUseYn(value){
	var cnt = $("input[name=paastaMonitoring]:checkbox:checked").length;
	
	if(cnt > 0 ){
		$(".w2ui-msg-body input[name='ingestorIp']").attr("disabled", false);
		$(".w2ui-msg-body input[name='ingestorPort']").attr("disabled", false);
		
	}else{
		$(".w2ui-msg-body input[name='ingestorIp']").css({"border-color" : "rgb(187, 187, 187)"}).parent().find(".isMessage").text("");
		$(".w2ui-msg-body input[name='ingestorPort']").css({"border-color" : "rgb(187, 187, 187)"}).parent().find(".isMessage").text("");
		//값 초기화
		$(".w2ui-msg-body input[name='ingestorIp']").val("");
		$(".w2ui-msg-body input[name='ingestorPort']").val("");
		//Read-only
		$(".w2ui-msg-body input[name='ingestorIp']").attr("disabled", true);
		$(".w2ui-msg-body input[name='ingestorPort']").attr("disabled", true);
	}
	 
}

/********************************************************
 * 설명		: 기본정보 등록
 * Function	: saveDefaultInfo
 *********************************************************/
function saveDefaultInfo() {
	var release = $(".w2ui-msg-body input[name='releases']").val();
	// 배포명 중복 검사
	if( !checkDeploymentNameDuplicate("cf", $(".w2ui-msg-body input[name='deploymentName']").val(), iaas ) 
			&& defaultInfo.deploymentName !=  $(".w2ui-msg-body input[name='deploymentName']").val() ){
		w2alert(  "입력한 배포명 (" + $(".w2ui-msg-body input[name='deploymentName']").val()  + ") 은 이미 존재합니다.","CF 설치");
		return;
	}
	
	defaultInfo = {
				id 						: (cfId) ? cfId : "",
				iaas 					: iaas.toUpperCase(),
				diegoYn					: diegoUse,
				platform				: "cf",
				deploymentName          : $(".w2ui-msg-body input[name='deploymentName']").val(),
				directorUuid            : $(".w2ui-msg-body input[name='directorUuid']").val(),
				releaseName 			: release.split("/")[0],
				releaseVersion 			: release.split("/")[1],
				appSshFingerprint   	: $(".w2ui-msg-body input[name='appSshFingerprint']").val(),
				deaMemoryMB			    : $(".w2ui-msg-body input[name='deaMemoryMB']").val(),
				deaDiskMB				: $(".w2ui-msg-body input[name='deaDiskMB']").val(),
				domain 					: $(".w2ui-msg-body input[name='domain']").val(),
				description 			: $(".w2ui-msg-body input[name='description']").val(),
				domainOrganization 	    : $(".w2ui-msg-body input[name='domainOrganization']").val(),
				loginSecret			    : $(".w2ui-msg-body input[name='loginSecret']").val(),
				paastaMonitoringUse     : $(".w2ui-msg-body input:checkbox[name='paastaMonitoring']").is(":checked") == true ? "true" : "false",
				ingestorIp        : $(".w2ui-msg-body input[name='ingestorIp']").val(),
				ingestorPort      : $(".w2ui-msg-body input[name='ingestorPort']").val()
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
function networkPopup(){
	settingDiegoUse(diegoUse, $("#networkInfoDiv ul"));
	if(iaas.toLowerCase() == "aws"){
		$('#availabilityZone').css('display','block');
	}else{
		$('#availabilityZone').css('display','none');
		$(".w2ui-msg-body input[name='availabilityZone']").val("");
	}
	$("#networkInfoDiv").w2popup({
		width : 750,
		height : 760,
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
							$(".w2ui-msg-body input[name='availabilityZone']").eq(cnt).val(networkInfo[i].availabilityZone);
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
		width : 750,
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
					cloudSecurityGroups		: $(".w2ui-msg-body input[name='cloudSecurityGroups']").eq(i).val(),
					availabilityZone		: $(".w2ui-msg-body input[name='availabilityZone']").eq(i).val()
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
					getCountryCodes();
					keyInfoPopup();
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
 * 설명		: 국가 코드 목록 조회
 * Function	: getCountryCodes
 *********************************************************/
function getCountryCodes() {
	var parentCode = 20000; //common_code -> country_code
	$.ajax({
		type : "GET",
		url : "/common/deploy/codes/countryCode/"+parentCode,
		contentType : "application/json",
		async : true,
		success : function(data, status) {
			countryCodes = new Array();
			if( data != null){
				data.map(function(obj) {
					countryCodes.push(obj.codeName);
				});
			}
			$(".w2ui-msg-body input[name='countryCode']").w2field('list', {items : countryCodes, maxDropHeight : 300,width : 150});
			if( !checkEmpty(keyInfo.countryCode) && !checkEmpty(keyInfo.countryCode) ){
				$(".w2ui-msg-body input[name='countryCode']").data('selected',{text : keyInfo.countryCode});
			}
		},
		error : function(e, status) {
			w2popup.unlock();
			w2alert("국가 코드를 가져오는데 실패하였습니다.", "CF 설치");
		}
	});
}

/********************************************************
 * 설명		: Key 생성  팝업
 * Function	: uaaInfoPopup
 *********************************************************/
function keyInfoPopup(){
	settingDiegoUse(diegoUse, $("#KeyInfoDiv ul"));
	$("#KeyInfoDiv").w2popup({
		width : 650,
		height : 520,
		modal : true,
		showMax : false,
		onOpen : function(event) {
			event.onComplete = function() {
				$(".w2ui-msg-body input[name='cfDomain']").val(defaultInfo.domain);//도메인
				if (keyInfo != "") {
					$(".w2ui-msg-body input[name='countryCode']").val(keyInfo.countryCode);//국가코드
					$(".w2ui-msg-body input[name='stateName']").val(keyInfo.stateName);//시/도
					$(".w2ui-msg-body input[name='localityName']").val(keyInfo.localityName);//시/구/군
					$(".w2ui-msg-body input[name='organizationName']").val(keyInfo.organizationName);//회사명
					$(".w2ui-msg-body input[name='unitName']").val(keyInfo.unitName);//부서명
					$(".w2ui-msg-body input[name='email']").val(keyInfo.email);//이메일
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
 * 설명		: Key 생성 확인
 * Function	: createKeyConfirm
 *********************************************************/
function createKeyConfirm(){
	 
	 var message = "";
	 if( !checkEmpty(keyFile) ){//이미 key가 생성됐으면,
		 message = "Key를 재 생성하시겠습니다.? \nDiego와 연동한 경우 Diego를 다시 설치해야 합니다.";
	 }else{
		message ="Key를 생성하시겠습니까?"; 
	 }
	 
	 w2confirm({
		width 			: 350,
		height 			: 180,
		title 				: '<b>Key 생성 여부</b>',
		msg 				: message,
		modal			: true,
		yes_text 		: "확인",
		no_text 		: "취소",
		yes_callBack 	: function(){
			createKeyInfo();
		},
		no_callBack : function(event){
		}
	});
}

/********************************************************
 * 설명		: Key 정보 생성
 * Function	: createKeyInfo
 *********************************************************/
function createKeyInfo(){
	 w2popup.lock("Key 생성 중입니다.", true);
	 keyInfo = {
				id                      		: cfId,
				iaas 							: iaas.toLowerCase(),
				platform					: menu, //cf -> 1, diego -> 2, cf&diego -> 3
				domain	 	        		: $(".w2ui-msg-body input[name='cfDomain']").val(), //도메인
				countryCode				: $(".w2ui-msg-body input[name='countryCode']").val(), //국가코드
				stateName      		 	: $(".w2ui-msg-body input[name='stateName']").val(), //시/도
				localityName 			: $(".w2ui-msg-body input[name='localityName']").val(), //시/구/군
				organizationName 	: $(".w2ui-msg-body input[name='organizationName']").val(), //회사명
				unitName 					: $(".w2ui-msg-body input[name='unitName']").val(), //부서명
				email 						: $(".w2ui-msg-body input[name='email']").val() //email
	}
	 
	 if (popupValidation()) {
		 $.ajax({
			type : "POST",
			url : "/common/deploy/key/createKey",
			contentType : "application/json",
			data : JSON.stringify(keyInfo),
			async : true,
			success : function(data, status) {
				w2popup.unlock();
				w2alert("Key 생성에 성공하였습니다.", "CF Key 생성");
				keyFile = data.keyFile;
			},
			error :function(request, status, error) {
				w2popup.unlock();
				var errorResult = JSON.parse(request.responseText);
				w2alert(errorResult.message, "CF Key 생성");
			}
		});
	 }
}

/********************************************************
 * 설명		: Key 정보 등록
 * Function	: saveUaaInfo
 *********************************************************/
function saveKeyInfo(type){
	 
	 if( type == "after"){
		 if( checkEmpty(keyFile) ){
			 w2alert("Key를 생성하지 않았습니다. 확인해주세요.", "CF 설치");
			 return;
		 }
		 
		keyInfo = {
				id                      		: cfId,
				iaas 							: iaas.toUpperCase(),
				platform					: "cf", //cf -> 1, diego -> 2, cf&diego -> 3
				domain	 	        		: $(".w2ui-msg-body input[name='cfDomain']").val(), //도메인
				countryCode				: $(".w2ui-msg-body input[name='countryCode']").val(), //국가코드
				stateName      		 	: $(".w2ui-msg-body input[name='stateName']").val(), //시/도
				localityName 			: $(".w2ui-msg-body input[name='localityName']").val(), //시/구/군
				organizationName 	: $(".w2ui-msg-body input[name='organizationName']").val(), //회사명
				unitName 					: $(".w2ui-msg-body input[name='unitName']").val(), //부서명
				email 						: $(".w2ui-msg-body input[name='email']").val(), //email
		}
		
		if (popupValidation()) {
			$.ajax({
				type : "PUT",
				url : "/deploy/"+menu+"/install/saveKeyInfo",
				contentType : "application/json",
				data : JSON.stringify(keyInfo),
				success : function(data, status) {
					w2popup.clear();
					if(iaas.toLowerCase() == "vsphere"){
						vSphereResourceInfoPopup();	
					}else{
						resourceInfoPopup();
					}
				},
				error : function(e, status) {
					w2alert("Key 생성 정보 등록에 실패 하였습니다.", "CF 설치");
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
 * 설명		: 리소스 정보 팝업(openstack/aws)
 * Function	: resourceInfoPopup
 *********************************************************/
function resourceInfoPopup() {
	settingDiegoUse(diegoUse, $("#resourceInfoDiv ul"));
	$("#resourceInfoDiv").w2popup({
		width : 750,
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
		width : 750,
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
		url : "/common/deploy/stemcell/list/cf/" + iaas,
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
		keyInfoPopup();
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
		width : 750,
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
					    		if(installClient!=""){
						    		installClient.disconnect();
					    			installClient = "";
					    		}
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
		$("#fingerprint").css("display","none");
	}else{
		$("#fingerprint").css("display","block");
		//progress style
		if( menu == "cfDiego" ){
			thisDiv.removeClass("progressStep_6");
	        thisDiv.addClass("progressStep_5");
            $(".progressStep_5 .install").hide();
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
			iaas		: (record.iaas) ? record.iaas : record.cType, 
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
						    		if(deleteClient!=""){
						    			deleteClient.disconnect();
						    			deleteClient = "";
						    		}
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
					doSearch();
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
	networkInfo = [];
	publicStaticIp = "";
	resourceInfo = "";
	releases = "";
	stemcells = "";
	installStatus ="";
	deploymentFile = "";
	countryCodes = null;
	keyFile ="";

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
		 doSearch();
	 }else if( menu =="cfDiego" ){
		 w2ui['config_cfDiegoGrid'].load("<c:url value='/deploy/cfDiego/list/"+iaas+"'/>",
					function() { doButtonStyle(); });
	 }else{
		 w2ui['config_cfGrid'].reset();
		 doSearch();
	 }
}
</script>

<!-- Default 정보 DIV -->
<div id="defaultInfoDiv" style="width: 100%; height: 100%;" hidden="true">
	<div rel="title"><b>CF 설치</b></div>
	<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
		<div style="margin-left: 2%;display:inline-block;width: 98%;">
			<ul class="progressStep_6">
				<li class="active">기본 정보</li>
				<li class="before">네트워크 정보</li>
				<li class="before">Key 생성</li>
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
						<img alt="diego-help-info"  src="../images/help-Info-icon.png" class="cf-info" style="width:18px; position:absolute; left:17%; margin-top:3px;"  data-toggle="popover"  data-trigger="click" data-html="true" title="설치 지원 버전 목록"/>
						<div>
							<input name="releases" type="list"  onchange='setDisabledMonitoring(this.value);' style="float: left; width: 60%;" required placeholder="CF 릴리즈를 선택하세요." />
						</div>
					</div>
					<div class="w2ui-field" id="fingerprint">
						<label style="text-align: left; width: 40%; font-size: 11px;">SSH 핑거프린트</label>
						<div>
							<input name="appSshFingerprint" type="text" style="float: left; width: 60%;" required placeholder="Diego 키 생성 후 SSH 핑거프린트를 입력하세요." />
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
							<input name="deaDiskMB" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="예) 32768" />
							<div class="isMessage"></div>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">DEA MEMORY 사이즈</label>
						<div>
							<input name="deaMemoryMB" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="예) 8192" />
							<div class="isMessage"></div>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align:left; width:40%; font-size:11px;">PaaS-TA 모니터링</label>
						<img alt="paasta-monitoring-help-info" class="paastaMonitoring-info" style="width:18px; position:absolute; left:24%; margin-top:3px" data-toggle="popover" data-html="true" src="../images/help-Info-icon.png" />
						<div>
							<input name="paastaMonitoring" type="checkbox" id="paastaMonitoring" onchange="checkPaasTAMonitoringUseYn(this);" disabled />사용
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
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">로그인 비밀번호</label>
						<div>
							<input name="loginSecret" type="text" style="float: left; width: 60%;" required placeholder="로그인 비밀번호룰 입력하세요." />
							<div class="isMessage"></div>
						</div>
					</div>
				</div>
			</div>	
			<div class="panel panel-info">	
				<div class="panel-heading"><b>PaaS-TA 모니터링 정보</b></div>
				<div class="panel-body" style="padding:5px 5% 10px 5%;">
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">PaaS-TA 모니터링 DB 서버 IP</label>
						<div>
							<input name="ingestorIp" type="text" style="float: left; width: 60%;" disabled placeholder="예)10.0.0.0" />
							<div class="isMessage ingestorIp"></div>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">PaaS-TA 모니터링 DB 서버 PORT</label>
						<div>
							<input name="ingestorPort" type="text" style="float: left; width: 60%;" disabled required placeholder="예)8063" />
							<div class="isMessage "ingestorPort""></div>
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

<!-- aws/openstack Network 설정 DIV -->
<div id="networkInfoDiv" style="width: 100%; height: 100%;" hidden="true">
	<div rel="title"><b>CF 설치</b></div>
	<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
		<div style="margin-left: 2%;display:inline-block;width: 98%;">
			<ul class="progressStep_6">
				<li class="pass">기본 정보</li>
				<li class="active">네트워크 정보</li>
				<li class="before">Key 생성</li>
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
								<label style="text-align: left;width:40%;font-size:11px;">CF API TARGET IP</label> 
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
							<div class="w2ui-field" id ="availabilityZone" style="display:none">
								<label style="text-align: left; width: 40%; font-size: 11px;">Availability Zone</label>
								<div>
									<input name="availabilityZone" type="text" style="float: left; width: 60%;" required placeholder="예) cf-AvaliailityZone" />
									<div class="isMessage"></div>
								</div>
							</div>
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
            <ul class="progressStep_6" >
	            <li class="pass">기본 정보</li>
				<li class="active">네트워크 정보</li>
				<li class="before">Key 생성</li>
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

<!--  Key 생성 Div -->
<div id="KeyInfoDiv" style="width: 100%; height: 100%;" hidden="true">
	<div rel="title"><b>CF 설치</b></div>
	<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
		<div style="margin-left: 2%;display:inline-block;width: 98%;">
			<ul class="progressStep_6">
				<li class="pass">기본 정보</li>
				<li class="pass">네트워크 정보</li>
				<li class="active">Key 생성</li>
				<li class="before">리소스 정보</li>
				<li class="before">배포파일 정보</li>
				<li class="before install">설치</li>
			</ul>
		</div>
		<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
			<div class="panel panel-info">	
				<div class="panel-heading"><b>Key 생성 정보</b></div>
				<div class="panel-body" style="padding:5px 5% 10px 5%;">
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">도메인</label>
						<div>
							<input name="cfDomain" type="text" style="float: left; width: 55%;" required readonly placeholder="도메인을 입력하세요." />
							<div class="isMessage"></div>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">국가 코드</label>
						<div>
							<input name="countryCode" type="list" style="float: left; width: 40%;" required placeholder="국가 코드를 선택하세요." />
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">시/도</label>
						<div>
							<input name="stateName" type="text" style="float: left; width: 55%;" required placeholder="시/도를 선택하세요." />
							<div class="isMessage"></div>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">시/구/군</label>
						<div>
							<input name="localityName" type="text" style="float: left; width: 55%;" required placeholder="시/구/군을 입력하세요." />
							<div class="isMessage"></div>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">회사명</label>
						<div>
							<input name="organizationName" type="text" style="float: left; width: 55%;" required placeholder="회사명을 입력하세요." />
							<div class="isMessage"></div>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">부서명</label>
						<div>
							<input name="unitName" type="text" style="float: left; width: 55%;" required placeholder="부서명을 입력하세요." />
							<div class="isMessage"></div>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">Email</label>
						<div>
							<input name="email" type="text" style="float: left; width: 55%;"  required placeholder="Email을 입력하세요." />
							<div class="isMessage"></div>
						</div>
					</div>
				</div>
					<button class="btn" style="float: right; margin-top:10px;" onclick="createKeyConfirm();" >Key 생성</button>
			</div>
		</div>
		<br/>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" style="float: left;" onclick="saveKeyInfo('before');" >이전</button>
			<button class="btn" style="float: right; padding-right: 15%" onclick="saveKeyInfo('after');">다음>></button>
		</div>
	</div>
</div>

<!-- Resource  설정 DIV -->
<div id="resourceInfoDiv" style="width: 100%; height: 100%;" hidden="true">
	<div rel="title"><b>CF 설치</b></div>
	<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
		<div style="margin-left: 2%;display:inline-block;width: 98%;">
			<ul class="progressStep_6">
				<li class="pass">기본 정보</li>
				<li class="pass">네트워크 정보</li>
				<li class="pass">Key 생성</li>
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
			<ul class="progressStep_6">
				<li class="pass">기본 정보</li>
				<li class="pass">네트워크 정보</li>
				<li class="pass">Key 생성</li>
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
			<ul class="progressStep_6">
				<li class="pass">기본 정보</li>
				<li class="pass">네트워크 정보</li>
				<li class="pass">Key 생성</li>
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
			<ul class="progressStep_6">
				<li class="pass">기본 정보</li>
				<li class="pass">네트워크 정보</li>
				<li class="pass">Key 생성</li>
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
