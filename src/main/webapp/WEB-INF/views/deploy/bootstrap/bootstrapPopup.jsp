<%
/* =================================================================
 * 작성일 : 
 * 작성자 : 
 * 상세설명 : Bootstrap 설치
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016.07       지향은           화면 수정 및 vSphere 클라우드 기능 추가
 * 2016.12       지향은           Bootstrap 목록과 팝업 화면 .jsp 분리 및 설치 버그 수정 
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">

/******************************************************************
 * 설명 :	변수 설정
 ***************************************************************** */
var awsInfo = ""; //AWS variable
var openstackInfo = "";//OPENSTACK variable
var vSphereInfo = "";//VSPHERE variable
var keyPathFileList = "";
var bootstrapId= "";
var BoshInfo = ""; //기본 정보
var networkInfo = "";//네트워크 정보
var resourceInfo = "";//리소스 정보
var deployFileName = "";//배포 파일명
var installClient = "";//설치 client
var deleteClient = "";//삭제 client
var boshReleases;//bosh 릴리즈
var stemcells;//스템셀
var installStatus ="";//설치 상태

//private common variable
var defaultInfo = "";
var deployInfo = "";
var boshCpiReleases;
/******************************************************************
 * Function : getBootstrapData
 * 설명		 : Bootstrap 상세 조회
 ***************************************************************** */
function getBootstrapData(record){
	var url = "/deploy/bootstrap/install/detail/"+record.id;
	$.ajax({
		type : "GET",
		url : url,
		contentType : "application/json",
		success : function(data, status) {
			if ( data == null || data == "" ){
			} else {
				initSetting();
				setBootstrapData(data.contents);
			}
		},
		error : function(request, status, error) {
			var errorResult = JSON.parse(request.responseText);
			w2alert(errorResult.message, "BOOTSTRAP 수정");
		}
	});
}


/******************************************************************
 * Function : setBootstrapData
 * 설명		 : Bootstrap 데이터 셋팅
 ***************************************************************** */
function setBootstrapData(contents){
	bootstrapId =  contents.id;
	iaas = contents.iaasType;
	
	//aws 정보
	awsInfo = {
			id								: contents.id,
			iaas		 					: contents.iaasType,
			accessKeyId				: contents.awsAccessKeyId,
			secretAccessId			: contents.awsSecretAccessId,
			defaultSecurityGroups	: contents.defaultSecurityGroups,
			region						: contents.awsRegion,
			availabilityZone			: contents.awsAvailabilityZone,
			privateKeyName			: contents.privateKeyName,
			privateKeyPath			: contents.privateKeyPath
	}
	
	//openstack 정보
	openstackInfo = {
			id								: bootstrapId,
			iaas							: contents.iaasType,
			authUrl						: contents.openstackAuthUrl,
			tenant						: contents.openstackTenant,
			userName					: contents.openstackUserName,
			apiKey						: contents.openstackApiKey,
			defaultSecurityGroups	: contents.defaultSecurityGroups,
			privateKeyName			: contents.privateKeyName,
			privateKeyPath			: contents.privateKeyPath
	}
	
	//vSphere 정보
	vSphereInfo = {
			id										: bootstrapId,
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
	
	BoshInfo = {
			id								: bootstrapId,
			iaas 							: contents.iaasType,
			deploymentName		: contents.deploymentName,
			directorName				: contents.directorName,		
			ntp							: contents.ntp,
			boshRelease				: contents.boshRelease,
			boshCpiRelease			: contents.boshCpiRelease,
			enableSnapshots		: contents.enableSnapshots,
			snapshotSchedule		: contents.snapshotSchedule
	}
	
	networkInfo = {
			id								: bootstrapId,
			subnetId					: contents.subnetId,
			privateStaticIp			: contents.privateStaticIp,
			publicStaticIp				: contents.publicStaticIp,
			subnetRange				: contents.subnetRange,
			subnetGateway			: contents.subnetGateway,
			subnetDns					: contents.subnetDns,
			publicSubnetId			: contents.publicSubnetId,
			publicSubnetRange		: contents.publicSubnetRange,
			publicSubnetGateway	: contents.publicSubnetGateway,
			publicSubnetDns		: contents.publicSubnetDns,
	}
	
	resourceInfo = {
			id							: bootstrapId,
			stemcell				: contents.stemcell,
			boshPassword		: contents.boshPassword,
			cloudInstanceType	: contents.cloudInstanceType,
			resourcePoolCpu		: contents.resourcePoolCpu,
			resourcePoolRam	: contents.resourcePoolRam,
			resourcePoolDisk	: contents.resourcePoolDisk
	}
	
	if(iaas.toUpperCase() == "AWS"){ 
		awsPopup(); 
		return; 
	} else if(iaas.toUpperCase() == "OPENSTACK") { 
		openstackPopup(); 
		return; 
	} else if(iaas.toUpperCase() == "VSPHERE") {  
		vSpherePopup(); 
		return; 
	}
			
}


/******************************************************************
 * Function : awsPopup
 * 설명		 : AWS Info Setting Popup
 ***************************************************************** */
function awsPopup(){
	$("#awsDiv").w2popup({
		width : 720,
		height : 520,
		onClose : popupClose,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
				if(awsInfo != ""){
					$(".w2ui-msg-body input[name='accessKeyId']").val(awsInfo.accessKeyId);
					$(".w2ui-msg-body input[name='secretAccessId']").val(awsInfo.secretAccessId);
					$(".w2ui-msg-body input[name='defaultSecurityGroups']").val(awsInfo.defaultSecurityGroups);
					$(".w2ui-msg-body input[name='region']").val(awsInfo.region);
					$(".w2ui-msg-body input[name='availabilityZone']").val(awsInfo.availabilityZone);
					$(".w2ui-msg-body input[name='privateKeyName']").val(awsInfo.privateKeyName);
					$(".w2ui-msg-body input[name='privateKeyPath']").val(awsInfo.privateKeyPath);
				}
				//키파일정보 리스트
				getKeyPathFileList();
			}
		},onClose:function(event){
			gridReload();
		}
	});
}

/******************************************************************
 * Function : saveAwsInfo
 * 설명		 : Save AWS Setting Info
 ***************************************************************** */
function saveAwsInfo(){
	awsInfo = {
			id								: bootstrapId,
			iaas							: "AWS",
			accessKeyId				: $(".w2ui-msg-body input[name='accessKeyId']").val(),
			secretAccessId			: $(".w2ui-msg-body input[name='secretAccessId']").val(),
			defaultSecurityGroups	: $(".w2ui-msg-body input[name='defaultSecurityGroups']").val(),
			region  						: $(".w2ui-msg-body input[name='region']").val(),
			availabilityZone  			: $(".w2ui-msg-body input[name='availabilityZone']").val(),
			privateKeyName  		: $(".w2ui-msg-body input[name='privateKeyName']").val(),
			privateKeyPath  			: $(".w2ui-msg-body input[name='privateKeyPath']").val()
	}
	//유효성체크
	if(popupValidation()){ 
		$.ajax({
			type : "PUT",
			url : "/deploy/bootstrap/install/saveAwsInfo/N",
			contentType : "application/json",
			async : true,
			data : JSON.stringify(awsInfo), 
			success : function(data, status) {
				bootstrapId = data.id;
				if( $(".w2ui-msg-body :radio[name=keyPathType]:checked").val() == "file" ){
					keyPathFileUpload(iaas);
				}else{
					defaultInfoPop();
				}
			},
			error : function( e, status ) {
				w2alert("AWS 설정 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
			}
		});
	}else{
		awsInfo = "";
		return;
	}
}

/******************************************************************
 * Function : openstackPopup
 * 설명		 : Openstack Info Setting Popup
 ***************************************************************** */
function openstackPopup(){
	$("#openstackInfoDiv").w2popup({
		width : 720,
		height : 520,
		onClose : popupClose,
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
					//keyList
					$(".w2ui-msg-body input[name='privateKeyPath']").val(openstackInfo.privateKeyPath);
				}
				//키파일정보 리스트
				getKeyPathFileList();
			}
		},onClose:function(event){
			gridReload();
		}
	});	
}

/******************************************************************
 * Function : saveOpenstackInfo
 * 설명		 : Save Openstack Info 
 ***************************************************************** */
function saveOpenstackInfo(){
	openstackInfo = {
			id						: bootstrapId,
			iaas					: "OPENSTACK",
			authUrl					: $(".w2ui-msg-body input[name='authUrl']").val(),
			tenant					: $(".w2ui-msg-body input[name='tenant']").val(),
			userName				: $(".w2ui-msg-body input[name='userName']").val(),
			apiKey					: $(".w2ui-msg-body input[name='apiKey']").val(),
			defaultSecurityGroups	: $(".w2ui-msg-body input[name='defaultSecurityGroups']").val(),
			privateKeyName			: $(".w2ui-msg-body input[name='privateKeyName']").val(),
			privateKeyPath			: $(".w2ui-msg-body input[name='privateKeyPath']").val()
	}
	if(popupValidation()){		
		$.ajax({
			type : "PUT",
			url : "/deploy/bootstrap/install/setOpenstackInfo/N",
			contentType : "application/json",
			async : true,
			data : JSON.stringify(openstackInfo),
			success : function(data, status) {
				bootstrapId = data.id;
				if( $(".w2ui-msg-body :radio[name=keyPathType]:checked").val() == "file" ){
					keyPathFileUpload(iaas);
				}else{
					defaultInfoPop();
				}
			},
			error : function( e, status ) {
				w2alert("OPENSTACK 설정 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
			}
		});
	}else{
		openstackInfo = "";
		return;
	}
}

/******************************************************************
 * Function : keyPathFileUpload
 * 설명		 : keyPath 파일업로드
 ***************************************************************** */
function keyPathFileUpload(iaas){
	var form = $(".w2ui-msg-body #keyForm")[0];
	var formData = new FormData(form);
	
	var files = document.getElementsByName('keyPathFile')[0].files;
	formData.append("file", files[0]);
	
	$.ajax({
		type : "POST",
		url : "/common/deploy/key/upload",
		enctype : 'multipart/form-data',
		dataType: "text",
		async : true,
		processData: false, 
		contentType:false,
		data : formData,  
		success : function(data, status) {
			w2popup.clear();
			$('input:radio[name=enableSnapshots]:input[value=true]').attr("checked", true);
			defaultInfoPop();
		},
		error : function( e, status ) {
			w2alert( iaas + " 설정 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
		}
	});
}

/******************************************************************
 * Function : vSpherePopup
 * 설명		 : vSphere Info Setting Popup
 ***************************************************************** */
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
		},onClose:function(event){
			gridReload()
		}
	});	
}

/******************************************************************
 * Function : saveVsphereInfo
 * 설명		 : save Vsphere Info
 ***************************************************************** */
function saveVsphereInfo(){
	vSphereInfo = {
			id										: bootstrapId,
			iaas									: "VSPHERE",
			vCenterAddress					: $(".w2ui-msg-body input[name='vCenterAddress']").val(),
			vCenterUser						: $(".w2ui-msg-body input[name='vCenterUser']").val(),
			vCenterPassword				: $(".w2ui-msg-body input[name='vCenterPassword']").val(),
			vCenterName						: $(".w2ui-msg-body input[name='vCenterName']").val(),
			vCenterVMFolder					: $(".w2ui-msg-body input[name='vCenterVMFolder']").val(),
			vCenterTemplateFolder		: $(".w2ui-msg-body input[name='vCenterTemplateFolder']").val(),
			vCenterDatastore				: $(".w2ui-msg-body input[name='vCenterDatastore']").val(),
			vCenterPersistentDatastore	: $(".w2ui-msg-body input[name='vCenterPersistentDatastore']").val(),
			vCenterDiskPath					: $(".w2ui-msg-body input[name='vCenterDiskPath']").val(),
			vCenterCluster					: $(".w2ui-msg-body input[name='vCenterCluster']").val()
	}
	
	if(popupValidation()){		
		$.ajax({
			type : "PUT",
			url : "/deploy/bootstrap/install/saveVSphereInfo/N",
			contentType : "application/json",
			async : true,
			data : JSON.stringify(vSphereInfo),
			success : function(data, status) {
				bootstrapId = data.id;
				defaultInfoPop();
			},
			error : function( e, status ) {
				w2alert("vSphere 설정 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
			}
		});
	}else{
		openstackInfo = "";
		return;
	}
}


/******************************************************************
 * Function : getKeyPathFileList
 * 설명		 : 키 파일 정보 조회
 ***************************************************************** */
function getKeyPathFileList(){
	$.ajax({
		type : "GET",
		url : "/common/deploy/key/list",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		success : function(data, status) {
			keyPathFileList = data;
			$('.w2ui-msg-body input:radio[name=keyPathType]:input[value=list]').attr("checked", true);	
			changeKeyPathType("list");
		},
		error : function( e, status ) {
			w2alert("KeyPath File 목록을 가져오는데 실패하였습니다.", "BOOTSTRAP 설치");
		}
	});
}


/******************************************************************
 * Function : changeKeyPathType
 * 설명		 : Private key file 선택
 ***************************************************************** */
function changeKeyPathType(type){
	$(".w2ui-msg-body input[name=privateKeyPath]").val("");
	$(".w2ui-msg-body input[name=keyPathFileName]").val("");
	var keyPathDiv = $('.w2ui-msg-body #keyPathDiv');
	//파일 업로드
	var fileUploadInput = '<input type="text" id="keyPathFileName" name="keyPathFileName" style="width:40%;" readonly  onClick="openBrowse();" placeholder="업로드할 Key 파일을 선택하세요."/>';
	fileUploadInput += '<a href="#" id="browse" onClick="openBrowse();"><span id="BrowseBtn">Browse</span></a>';
	 fileUploadInput += '<input type="file" name="keyPathFile" onchange="setPrivateKeyPathFileName(this);" style="visibility: hidden"/>';
	//목록에서 선택
	var selectInput = '<input type="list" name="keyPathList" style="float: left;width:60%;" onchange="setPrivateKeyPath(this.value);" placeholder="Private Key 파일을 선택하세요."/>';
	
	//목록에서 선택
	if(type == "list") {
		keyPathDiv.html(selectInput);		
		$('#w2ui-popup #keyPathDiv input[type=list]').w2field('list', { items: keyPathFileList , maxDropHeight:200, width:250});
		if( awsInfo.privateKeyPath ){
			$(".w2ui-msg-body input[name='keyPathList']").data('selected', {text:awsInfo.privateKeyPath});
			$(".w2ui-msg-body input[name='privateKeyPath']").val(awsInfo.privateKeyPath);
		}else if(openstackInfo.privateKeyPath)
			$(".w2ui-msg-body input[name='keyPathList']").data('selected', {text:openstackInfo.privateKeyPath});		
		$(".w2ui-msg-body input[name='privateKeyPath']").val(openstackInfo.privateKeyPath);
	}else{
		//파일업로드
		keyPathDiv.html(fileUploadInput);
		//FileInput Ui (bootstrap-fileInput)) 
	}
}

/******************************************************************
 * Function : setPrivateKeyPath
 * 설명		 : Private key File List
 ***************************************************************** */
function setPrivateKeyPath(value){
	$(".w2ui-msg-body input[name=privateKeyPath]").val(value);
}

/******************************************************************
 * Function : openBrowse
 * 설명		 : File upload Browse Button
 ***************************************************************** */
function openBrowse(){
	$(".w2ui-msg-body input[name='keyPathFile']").click();
}

/******************************************************************
 * Function : setPrivateKeyPathFileName
 * 설명		 : File upload Input
 ***************************************************************** */
function setPrivateKeyPathFileName(fileInput){
	var file = fileInput.files;
	$(".w2ui-msg-body input[name=privateKeyPath]").val(file[0].name);
	$(".w2ui-msg-body #keyPathFileName").val(file[0].name);
	
}




/******************************************************************
 * Function : defaultInfoPop
 * 설명		 : Default Info popup
 ***************************************************************** */
function defaultInfoPop(){
	settingPopupTab("DefaultInfoDiv", iaas);
	$("#DefaultInfoDiv").w2popup({
		width : 720,
		height : 480,
		onClose : popupClose,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){			
				if( !checkEmpty(BoshInfo) && BoshInfo != "" ){
					$(".w2ui-msg-body input[name='deploymentName']").val(BoshInfo.deploymentName);
					$(".w2ui-msg-body input[name='directorName']").val(BoshInfo.directorName);
					$(".w2ui-msg-body input[name='ntp']").val(BoshInfo.ntp);
					$('.w2ui-msg-body input:radio[name=enableSnapshots]:input[value="' +BoshInfo.enableSnapshots + '"]').attr("checked", true);	
					if( !checkEmpty(BoshInfo.enableSnapshots) ){
						$(".w2ui-msg-body input[name='snapshotSchedule']").val(BoshInfo.snapshotSchedule);
						enableSnapshotsFn(BoshInfo.enableSnapshots);
					}else{
						$('input:radio[name=enableSnapshots]:input[value=false]').attr("checked", true);
						enableSnapshotsFn("false");
					}
				}else{
					$('input:radio[name=enableSnapshots]:input[value=false]').attr("checked", true);
					enableSnapshotsFn("false");
				}
				//BOSH 릴리즈 정보 가져오기
				getLocalBoshList('bosh');
				//BOSH CPI 릴리즈 정보 가져오기
				getLocalBoshCpiList('bosh_cpi', iaas);
				$('[data-toggle="popover"]').popover();
				getReleaseVersionList();
				
			}
		}
	});
}

/********************************************************
 * 설명 :  Bosh 릴리즈 버전 목록 정보 조회
 * Function : getReleaseVersionList
 *********************************************************/
function getReleaseVersionList(){
	 var contents = "";
	$.ajax({
		type :"GET",
		url :"/common/deploy/list/releaseInfo/bootstrap/"+iaas, 
		contentType :"application/json",
		success :function(data, status) {
			if (data != null && data != "") {
				contents = "<table id='popoverTable'><tr><th>IaaS 유형</th><th>릴리즈 최소 버전</th></tr>";
				data.map(function(obj) {
					contents += "<tr><td>" + obj.iaasType+ "</td><td>" +  obj.minReleaseVersion +"</td></tr>";
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

/******************************************************************
 * Function : getLocalBoshList
 * 설명		 : BOSH 릴리즈 정보
 ***************************************************************** */
function getLocalBoshList(type){
	$.ajax({
		type : "GET",
		url : "/common/deploy/systemRelease/list/"+type+"/''",
		contentType : "application/json",
		async : true,
		success : function(data, status) {
			boshReleases = data;
			
			var boshReleaseDiv = $('.w2ui-popup #boshReleaseDiv');
			var selectInput = '<input type="list" name="boshRelease" style="float:left;width:60%;" placeholder="BOSH 릴리즈를 선택하세요."/>';
			boshReleaseDiv.html(selectInput);
			
			$('.w2ui-popup #boshReleaseDiv input[type=list]').w2field('list', { items: boshReleases , maxDropHeight:200, width:250});
			
			if(BoshInfo.boshRelease){
				$(".w2ui-msg-body input[name='boshRelease']").data('selected', {text:BoshInfo.boshRelease});
			}
		},
		error : function( e, status ) {
			w2alert("Bosh Cpi 목록을 가져오는데 실패하였습니다.", "BOOTSTRAP 설치");
		}
	});
}

 /******************************************************************
  * Function : getLocalBoshCpiList
  * 설명		 : BOSH CPI 릴리즈 정보
  ***************************************************************** */
function getLocalBoshCpiList(type, iaas){
	$.ajax({
		type : "GET",
		url : "/common/deploy/systemRelease/list/"+type+"/"+iaas,
		contentType : "application/json",
		async : true,
		success : function(data, status) {

			var boshCpiReleaseDiv = $('.w2ui-popup #boshCpiReleaseDiv');
			var selectInput = '<input type="list" name="boshCpiRelease" style="float:left;width:60%;" placeholder="BOSH CPI 릴리즈를 선택하세요."/>';
			boshCpiReleaseDiv.html(selectInput);
			
			$('.w2ui-popup #boshCpiReleaseDiv input[type=list]').w2field('list', { items: data , maxDropHeight:200, width:250});
			if(BoshInfo.boshCpiRelease){
				$(".w2ui-msg-body input[name='boshCpiRelease']").data('selected', {text:BoshInfo.boshCpiRelease});
			}
			
		},
		error : function( e, status ) {
			w2alert("Bosh 목록을 가져오는데 실패하였습니다.", "BOOTSTRAP 설치");
		}
	});
}


 /******************************************************************
  * Function : enableSnapshotsFn
  * 설명		 : 스냅샵 가능 사용여부(사용일 경우)
  ***************************************************************** */
function enableSnapshotsFn(value){
	if(value == "true"){
		$(".w2ui-msg-body .snapshotScheduleDiv").show();
	}else if(value == "false"){
		$(".w2ui-msg-body  input[name=snapshotSchedule]").val("");
		$(".w2ui-msg-body .snapshotScheduleDiv").hide();
	}
}

/******************************************************************
 * Function : saveDefaultInfo
 * 설명		 : Default Info Save
 ***************************************************************** */
function saveDefaultInfo(type){
  	for(var i=0;i<bootStrapDeploymentName.length;i++){
		if($(".w2ui-msg-body input[name='deploymentName']").val()==bootStrapDeploymentName[i]
		&& defaultInfo.deploymentName != $(".w2ui-msg-body input[name='deploymentName']").val() ){
			w2alert("중복 된 BootStrap 배포 파일 명 입니다.", "BootStrap 설치");
			return;
		}
	}
	BoshInfo = {
			id							: bootstrapId,
			iaas 						: iaas,
			deploymentName	: $(".w2ui-msg-body input[name=deploymentName]").val(),
			directorName			: $(".w2ui-msg-body input[name=directorName]").val(),
			ntp						: $(".w2ui-msg-body input[name=ntp]").val(),
			boshRelease			: $(".w2ui-msg-body input[name=boshRelease]").val(),
			boshCpiRelease		: $(".w2ui-msg-body input[name=boshCpiRelease]").val(),
			enableSnapshots	: $(".w2ui-msg-body input:radio[name=enableSnapshots]:checked").val(),
			snapshotSchedule : $(".w2ui-msg-body input[name=snapshotSchedule]").val()
	}
	
	if(type == 'before'){
		w2popup.clear();
		if(iaas == 'OPENSTACK'){  openstackPopup(); return; }
		else if(iaas == "AWS"){ awsPopup(); return; }
		else {  vSpherePopup(); return; }
	}else{
		if(popupValidation()){
			//BoshInfo SAVE
			$.ajax({
				type : "PUT",
				url : "/deploy/bootstrap/install/setDefaultInfo",
				contentType : "application/json",
				async : true,
				data : JSON.stringify(BoshInfo),
				success : function(data, status) {
					w2popup.clear();
					networkInfoPopup();					
				},
				error : function( e, status ) {
					w2alert("BootStrap 기본정보 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
				}
			});
		}
	}
}


/******************************************************************
 * Function : networkInfoPopup
 * 설명		 : Network Info Popup
 ***************************************************************** */
function networkInfoPopup(){
	var div = "";
	var setHeight;
	if(iaas.toUpperCase() == "VSPHERE") {
		div = "VsphereNetworkInfoDiv";
		setHeight = 760;
	} else {
		div = "NetworkInfoDiv";
		setHeight = 650;
	}
	settingPopupTab(div, iaas);
	$("#"+div).w2popup({
		width : 720,
		height : setHeight,
		onClose : popupClose,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
				if(networkInfo != ""){
					//Internal
					$(".w2ui-msg-body input[name='privateStaticIp']").val(networkInfo.privateStaticIp);
					$(".w2ui-msg-body input[name='subnetId']").val(networkInfo.subnetId);
					$(".w2ui-msg-body input[name='subnetRange']").val(networkInfo.subnetRange);
					$(".w2ui-msg-body input[name='subnetGateway']").val(networkInfo.subnetGateway);
					$(".w2ui-msg-body input[name='subnetDns']").val(networkInfo.subnetDns);
					//External
					$(".w2ui-msg-body input[name='publicStaticIp']").val(networkInfo.publicStaticIp);
					$(".w2ui-msg-body input[name='publicSubnetId']").val(networkInfo.publicSubnetId);
					$(".w2ui-msg-body input[name='publicSubnetRange']").val(networkInfo.publicSubnetRange);
					$(".w2ui-msg-body input[name='publicSubnetGateway']").val(networkInfo.publicSubnetGateway);
					$(".w2ui-msg-body input[name='publicSubnetDns']").val(networkInfo.publicSubnetDns);
				}				
			}
		}
	});	
}

/******************************************************************
 * Function : saveNetworkInfo
 * 설명		 : 네트워크 정보 저장 
 ***************************************************************** */
function saveNetworkInfo(type){
	networkInfo = {
			id								: bootstrapId,
			//private
			subnetId					: $(".w2ui-msg-body input[name='subnetId']").val(),
			privateStaticIp			: $(".w2ui-msg-body input[name='privateStaticIp']").val(),
			publicStaticIp				: $(".w2ui-msg-body input[name='publicStaticIp']").val(),
			subnetRange				: $(".w2ui-msg-body input[name='subnetRange']").val(),
			subnetGateway			: $(".w2ui-msg-body input[name='subnetGateway']").val(),
			subnetDns					: $(".w2ui-msg-body input[name='subnetDns']").val(),
			
			//public
			publicStaticIp				: $(".w2ui-msg-body input[name='publicStaticIp']").val(),
			publicSubnetId			: $(".w2ui-msg-body input[name='publicSubnetId']").val(),
			publicSubnetRange		: $(".w2ui-msg-body input[name='publicSubnetRange']").val(),
			publicSubnetGateway	: $(".w2ui-msg-body input[name='publicSubnetGateway']").val(),
			publicSubnetDns		: $(".w2ui-msg-body input[name='publicSubnetDns']").val(),
	}
	if( type == "before") {
		defaultInfoPop();
		return;
	}else{
		var elements = $(".w2ui-box1 .w2ui-msg-body .w2ui-field input:visible");
		if( popupNetworkValidation()){		
			$.ajax({
				type : "PUT",
				url : "/deploy/bootstrap/install/setNetworkInfo",
				contentType : "application/json",
				async : true,
				data : JSON.stringify(networkInfo),
				success : function(data, status) {
					w2popup.clear();
					if(iaas.toUpperCase() == 'VSPHERE'){  
						$("#cloudInstanceTypeDiv").hide(); 
						$("#resourcePoolCpuDiv").show(); 
						$("#resourcePoolRamDiv").show(); 
						$("#resourcePoolDiskDiv").show(); 
					}else{
						$("#cloudInstanceTypeDiv").show();
						$("#resourcePoolCpuDiv").hide(); 
						$("#resourcePoolRamDiv").hide(); 
						$("#resourcePoolDiskDiv").hide(); 
					}
					resourceInfoPopup();
				},
				error : function( e, status ) {
					w2alert("OPENSTACK Network 정보 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
				}
			});
		}
	}
}

/******************************************************************
 * Function : resourceInfoPopup
 * 설명		  : Resource Info Popup
 ***************************************************************** */
function resourceInfoPopup(){
	settingPopupTab("ResourceInfoDiv", iaas);
	
	$("#ResourceInfoDiv").w2popup({
		width : 720,
		height : 430,
		onClose : popupClose,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){	
				getStemcellList(iaas);
			}
		}
	});	
}

/******************************************************************
 * Function : getStemcellList
 * 설명		  : 스템셀 목록 조회
 ***************************************************************** */
function getStemcellList(iaas){
	var url = "/common/deploy/stemcell/list/bootstrap/" + iaas;
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
					stemcells.push(obj.stemcellFileName);
				});
			}
			$('#w2ui-popup input[name=stemcell][type=list]').w2field('list', { items: stemcells , maxDropHeight:300, width:500});
			setReourceData();
		},
		error : function( e, status ) {
			w2alert("스템셀 목록을 가져오는데 실패하였습니다.", "BOOTSTRAP 설치");
		}
	});
}


/******************************************************************
 * Function : setReourceData
 * 설명		  : Resource Info Setting
 ***************************************************************** */
function setReourceData(){
	if(resourceInfo != ""){
		$(".w2ui-msg-body input[name='stemcell']").data('selected', {text:resourceInfo.stemcell});
		$(".w2ui-msg-body input[name='boshPassword']").val(resourceInfo.boshPassword);
		if(iaas.toUpperCase() != 'VSPHERE') { 
			$(".w2ui-msg-body input[name='cloudInstanceType']").val(resourceInfo.cloudInstanceType);
		}else{
			$(".w2ui-msg-body input[name='resourcePoolCpu']").val(resourceInfo.resourcePoolCpu);
			$(".w2ui-msg-body input[name='resourcePoolRam']").val(resourceInfo.resourcePoolRam);
			$(".w2ui-msg-body input[name='resourcePoolDisk']").val(resourceInfo.resourcePoolDisk);
		}
	}
}

/******************************************************************
 * Function : saveResourceInfo
 * 설명		  : Openstack/AWS Resource Info Save
 ***************************************************************** */
function saveResourceInfo(type){
	var cloudInstanceType = "";
	 if(iaas != 'VSPHERE' ) { 
		 cloudInstanceType =  $(".w2ui-msg-body input[name='cloudInstanceType']").val();  
	}
	resourceInfo = {
			id							: bootstrapId,
			stemcell				: $(".w2ui-msg-body input[name='stemcell']").val(),
			boshPassword		: $(".w2ui-msg-body input[name='boshPassword']").val(),
			cloudInstanceType 	: cloudInstanceType,
			resourcePoolCpu		:  $(".w2ui-msg-body input[name='resourcePoolCpu']").val(),
			resourcePoolRam	: $(".w2ui-msg-body input[name='resourcePoolRam']").val(),
			resourcePoolDisk	: $(".w2ui-msg-body input[name='resourcePoolDisk']").val()
	}
	
	if( type == "before"){
		w2popup.clear();
		networkInfoPopup();
		return;
	}else{
		if(popupValidation()){
			$.ajax({
				type : "PUT",
				url : "/deploy/bootstrap/install/setResourceInfo",
				contentType : "application/json",
				async : true,
				data : JSON.stringify(resourceInfo),
				success : function(data, status) {
					deployFileName = data.deploymentFile;
					w2popup.clear();
					createSettingFile(data);
				},
				error :function(request, status, error) {
					
					var errorResult = JSON.parse(request.responseText);
					w2alert(errorResult.message, "Bootstrap 리소스 정보 저장");
					
				}
			});
		}
	}
}


/******************************************************************
 * Function : createSettingFile
 * 설명		  : 배포 파일 생성
 ***************************************************************** */
function createSettingFile(data){
	bootstrapInfo = {
			iaasType		 					: data.iaasType,
			deploymentFile					: data.deploymentFile
	}
	
	$.ajax({
		type : "POST",
		url : "/deploy/bootstrap/install/createSettingFile/"+ data.id+"/N",
		contentType : "application/json",
		async : true,
		data : JSON.stringify(bootstrapInfo),
		success : function(status) {
			deployPopup();
		},
		error :function(request, status, error) {
			var errorResult = JSON.parse(request.responseText);
			w2alert(errorResult.message, "Bootstrap 배포 파일 생성");
			resourceInfoPopup();
		}
	});
}


/******************************************************************
 * Function : deployPopup
 * 설명		  : deploy File Info (AWS/Openstack)
 ***************************************************************** */
function deployPopup(){
	settingPopupTab("DeployDiv", iaas);
	
	$("#DeployDiv").w2popup({
		width 	: 720,
		height 	: 500,
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

/******************************************************************
 * Function : getDeployInfo
 * 설명		  : Manifest 파일 내용 출력
 ***************************************************************** */
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
			w2alert("Temp 파일을 가져오는 중 오류가 발생하였습니다. ", "BOOTSTRAP 설치");
		}
	});
}


/******************************************************************
 * Function : confirmDeploy
 * 설명		  : bootstrap Install Confirm
 ***************************************************************** */
function confirmDeploy(type){
	if(type == 'after'){		
		w2confirm({
			msg				: "BOOTSTRAP을 설치하시겠습니까?",
			title				: w2utils.lang('BOOTSTRAP 설치'),
			yes_text			: "예",
			no_text			: "아니오",
			yes_callBack	: installPopup
		});
	} else{
		w2popup.clear();
		resourceInfoPopup();
	}
}

/******************************************************************
 * Function : lockFileSet
 * 설명		  : Lock 파일 생성
 ***************************************************************** */
var lockFile = false;
function lockFileSet(deployFile){
	if(!checkEmpty(deployFile) ){
		var FileName = "bootstrap";
		var message = "현재 다른 설치 관리자가 해당 BootStrap을 사용 중 입니다.";
		lockFile = commonLockFile("<c:url value='/common/deploy/lockFile/"+FileName+"'/>",message);
	}
	return lockFile;
}

/******************************************************************
 * Function : popupComplete
 * 설명		  : 설치 화면 닫기
 ***************************************************************** */
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
			popupClose();
			w2popup.close();
		},
		no_text : "취소"
	});
}

 /******************************************************************
  * Function : bootstrapInstallSocket
  * 설명		  : Boostrap 설치
  ***************************************************************** */
var bootstrapInstallSocket = null;
function installPopup(){
	settingPopupTab("InstallDiv", iaas);
	
	if(!lockFileSet(deployFileName)) return;
	var message = "BOOTSTRAP ";
	var requestParameter = {
			id : bootstrapId,
			iaas: iaas
	};
	 $("#InstallDiv").w2popup({
		width : 800,
		height : 490,
		modal	: true,
		showMax : true,		
		onOpen : function(event){
			event.onComplete = function(){
				if(bootstrapInstallSocket != null) bootstrapInstallSocket = null;
				if(installClient != null) installClient = null;
				bootstrapInstallSocket = new SockJS('/deploy/bootstrap/install/bootstrapInstall');
				installClient = Stomp.over(bootstrapInstallSocket);
				
				installClient.connect({}, function(frame) {
			        installClient.subscribe('/user/deploy/bootstrap/install/logs', function(data){
			        	
			        	
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
								w2alert(message, "BOOTSTRAP 설치");
					       	}
			        	}
			        });
			        installClient.send('/send/deploy/bootstrap/install/bootstrapInstall', {}, JSON.stringify(requestParameter));
			    });
			}
		}, onClose : function(event){
			event.onComplete = function(){
				w2ui['config_bootstrapGrid'].reset();
				if( installClient != ""  ){
					installClient.disconnect();
				}
				popupClose();
			}
		}
	});
}

 /******************************************************************
  * Function : deletePop
  * 설명		  : BOOTSTRAP 삭제
  ***************************************************************** */
 function deletePop(record){
	
	var requestParameter = {
			id:record.id,
			iaas:record.iaas
	};
	if ( record.deployStatus == null || record.deployStatus == '' ) {
		// 단순 레코드 삭제
		var url = "/deploy/bootstrap/delete/data";
		$.ajax({
			type : "DELETE",
			url : url,
			data : JSON.stringify(requestParameter),
			contentType : "application/json",
			success : function(data, status) {
				bootStrapDeploymentName = [];
				gridReload();
			},
			error : function(request, status, error) {
				var errorResult = JSON.parse(request.responseText);
				w2alert(errorResult.message, "BOOTSTRAP 삭제");
			}
		});
	} else {
		if(!lockFileSet(record.deploymentFile)) return;
		
		var message = "BOOTSTRAP";
		var body = '<textarea id="deleteLogs" style="width:95%;height:90%;overflow-y:visible;resize:none;background-color: #FFF; margin:2%" readonly="readonly"></textarea>';
		
		w2popup.open({
			width   : 700,
			height  : 500,
			title   : "<b>BOOTSTRAP 삭제</b>",
			body    : body,
			buttons : '<button class="btn" style="float: right; padding-right: 15%;" onclick="popupComplete();">닫기</button>',
			modal   : true,
			showMax : true,
			onOpen  : function(event){
				event.onComplete = function(){
					var socket = new SockJS('/deploy/bootstrap/delete/instance');
					deleteClient = Stomp.over(socket);
 					deleteClient.connect({}, function(frame) {
						deleteClient.subscribe('/user/deploy/bootstrap/delete/logs', function(data){
							
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
									w2alert(message, "BOOTSTRAP 삭제");
						       	}
				        	}
				        });
						deleteClient.send('/send/deploy/bootstrap/delete/instance', {}, JSON.stringify(requestParameter));
				    });
				}
			}, onClose : function (event){
				event.onComplete= function(){
					bootStrapDeploymentName = [];
					w2ui['config_bootstrapGrid'].reset();
					if( deleteClient != ""  ){
						deleteClient.disconnect();
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

 /******************************************************************
  * Function : gridReload
  * 설명		  : 목록 재 조회
  ***************************************************************** */
 function gridReload(){
	w2ui['config_bootstrapGrid'].clear();
	doSearch();
}

 /******************************************************************
  * Function : doButtonStyle
  * 설명		  : Button 제어
  ***************************************************************** */
function doButtonStyle(){
	//Button Style init
	$('#modifyBtn').attr('disabled', true);
	$('#deleteBtn').attr('disabled', true);
}

 /******************************************************************
  * Function : clearMainPage
  * 설명		  : 다른페이지 이동시 Bootstrap Grid clear
  ***************************************************************** */
function clearMainPage() {
	$().w2destroy('config_bootstrapGrid');
}

 /******************************************************************
  * 설명		  : 화면 리사이즈시 호출 
  ***************************************************************** */
$( window ).resize(function() {
	setLayoutContainerHeight();
});


 /******************************************************************
  * Function : 전역변수 초기화
  * 설명		  : initSetting
  ***************************************************************** */
function initSetting(){
	iaas = "";
	bootstrapId= "";
	
	awsInfo = "";
	openstackInfo = "";
	BoshInfo = "";
	BoshInfo = "";
	networkInfo = "";
	resourceInfo = "";
	deployInfo = "";
	deployFileName = "";
	bootStrapDeploymentName = [];
	installClient = "";
	deleteClient = "";
	installStatus = "";
	vSphereInfo ="";
	
	lockFile ="";
}

 /******************************************************************
  * Function : 팝업창 닫을 경우
  * 설명		  : popupClose
  ***************************************************************** */
function popupClose() {
	//params init
	initSetting();
	//grid Reload
	gridReload();
	//button Control
	doButtonStyle();
	
}
</script>
<!-- AWS  설정 DIV -->
	<div id="awsDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:2%;display:inline-block;width:97%;">
	            <ul class="progressStep_6" >
		            <li class="active">AWS 정보</li>
		            <li class="before">기본 정보</li>
		            <li class="before">네트워크 정보</li>
		            <li class="before">리소스 정보</li>
		            <li class="before">배포 파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
	        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
				<div class="panel panel-info">	
					<div class="panel-heading"><b>AWS 정보</b></div>
					<div class="panel-body" style="padding:5px 5% 10px 5%;">
				    	<form id="keyForm" data-toggle="validator" >
					        <div class="w2ui-field">
					            <label style="text-align: left;width:40%;font-size:11px;">Access Key ID</label>
					            <div>
					                <input name="accessKeyId" type="text"  style="float:left;width:60%;"  required placeholder="AWS Access Key를 입력하세요."/>
					                <div class="isMessage"></div>			                
					            </div>
					        </div>
					        <div class="w2ui-field">
					            <label style="text-align: left;width:40%;font-size:11px;">Secret Access Key</label>
					            <div>
					                <input name="secretAccessId" type="password"  style="float:left;width:60%;"  required placeholder="AWS Secret Access Key를 입력하세요."/>
					                <div class="isMessage"></div>
					            </div>
					        </div>
					        <div class="w2ui-field">
					            <label style="text-align: left;width:40%;font-size:11px;">Security Group</label>
					            <div>
					                <input name="defaultSecurityGroups" type="text"  style="float:left;width:60%;"  required placeholder="시큐리티 그룹을 입력하세요."/>
					                <div class="isMessage"></div>
					            </div>
					        </div>
					        <div class="w2ui-field">
					            <label style="text-align: left;width:40%;font-size:11px;">Region</label>
					            <div>
					                <input name="region" type="text"  style="float:left;width:60%;"  required placeholder="설치할 Region을 입력하세요.(예: us-east-1)"/>
					                <div class="isMessage"></div>
					            </div>
					        </div>
					        <div class="w2ui-field">
					            <label style="text-align: left;width:40%;font-size:11px;">Availability Zone</label>
					            <div>
					                <input name="availabilityZone" type="text" style="display: inline-block;float:left;width:60%;" required placeholder="Availability Zone을 입력하세요."/>
					                <div class="isMessage"></div>
					            </div>
					        </div>
					        <div class="w2ui-field">
					            <label style="text-align: left;width:40%;font-size:11px;">Private Key Name</label>
					            <div>
					                <input name="privateKeyName" type="text"  style="display: inline-block;float:left;width:60%;"  required placeholder="Key Pair 이름을 입력하세요."/>
					                <div class="isMessage"></div>
					            </div>
					        </div>
					        
					        <div class="w2ui-field">
					            <label style="text-align: left;width:40%;font-size:11px;">Private Key File</label>
				                <div >
			  						<span onclick="changeKeyPathType('file');" style="width:30%;"><label><input type="radio" name="keyPathType" value="file" />&nbsp;파일업로드</label></span>
									&nbsp;&nbsp;
									<span onclick="changeKeyPathType('list');" style="width:30%;"><label><input type="radio" name="keyPathType" value="list" />&nbsp;목록에서 선택</label></span>
								</div>
					        </div>
					        <div class="w2ui-field">			         	
				                 <input name="privateKeyPath" type="text" style="width:200px;" hidden="true" />
					            <label style="text-align: left;width:40%;font-size:11px;" class="control-label"></label>
								<div id="keyPathDiv" ></div>
					        </div>
				        </form>
			        </div>
		        </div>
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
		        <button class="btn" style="float: right;padding-right:15%" onclick="saveAwsInfo();" >다음>></button>
		    </div>
		</div>
	</div>
	<!-- End AWS Popup -->
	
	<!-- Start OPENSTACK POPUP  -->
	<div id="openstackInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:2%;display:inline-block;width:97%;">
	            <ul class="progressStep_6" >
		            <li class="active">오픈스택 정보</li>
		            <li class="before">기본 정보</li>
		            <li class="before">네트워크 정보</li>
		            <li class="before">리소스 정보</li>		            
		            <li class="before">배포 파일 정보</li>
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
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">Tenant</label>
								<div>
									<input name="tenant" type="text"  style="float:left;width:60%;"  required placeholder="Tenant명을 입력하세요."/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">Username</label>
								<div>
									<input name="userName" type="text"  style="float:left;width:60%;" required placeholder="계정명을 입력하세요."/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">Password</label>
								<div>
									<input name="apiKey" type="password"  style="float:left;width:60%;"   required placeholder="계정 비밀번호를 입력하세요."/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">Security Group</label>
								<div>
									<input name="defaultSecurityGroups" type="text"  style="float:left;width:60%;"  required placeholder="시큐리티 그룹을 입력하세요."/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">Private Key Name</label>
								<div>
									<input name="privateKeyName" type="text"  style="float:left;width:60%;"  required placeholder="Key Pair명을 입력하세요."/>
									<div class="isMessage"></div>
								</div>
							</div>
							<!-- privateKeyPath -->
					        <div class="w2ui-field">
					        	<label style="text-align: left;width:40%;font-size:11px;">Private Key File</label>
				                <div >
				 					<span onclick="changeKeyPathType('file');" style="width:30%;"><label><input type="radio" name="keyPathType" value="file" />&nbsp;파일업로드</label></span>
									&nbsp;&nbsp;
									<span onclick="changeKeyPathType('list');" style="width:30%;"><label><input type="radio" name="keyPathType" value="list" />&nbsp;목록에서 선택</label></span>
								</div>
					        </div>
					        <div class="w2ui-field">			         	
					            <label style="text-align: left;width:40%;font-size:11px;" class="control-label"></label>
								<div id="keyPathDiv" ></div>
								<input name="privateKeyPath" type="text" style="width:200px;" hidden="true" />
					        </div>
				        </form>
			        </div>
		        </div>
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveOpenstackInfo();">다음>></button>
		    </div>
		</div>
	</div>
	<!-- End Openstack Popup -->
	
	<!-- Start Vsphere Popup  -->
	<div id="vSphereInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:2%;display:inline-block;width:97%;">
	            <ul class="progressStep_6" >
		            <li class="active">vSphere 정보</li>
		            <li class="before">기본 정보</li>
		            <li class="before">네트워크 정보</li>
		            <li class="before">리소스 정보</li>		            
		            <li class="before">배포 파일 정보</li>
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
								<label style="text-align: left;width:40%;font-size:11px;">vCenter DataCenter</label>
								<div>
									<input name="vCenterName" type="text"  style="float:left;width:60%;" required placeholder="vCenter DataCenter명을 입력하세요."/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">DataCenter VM Folder Name</label>
								<div>
									<input name="vCenterVMFolder" type="text"  style="float:left;width:60%;"  required placeholder="DataCenter VM 폴더명을 입력하세요."/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">DataCenter VM Stemcell Folder Name</label>
								<div>
									<input name="vCenterTemplateFolder" type="text"  style="float:left;width:60%;"  required placeholder="DataCenter VM 스템셀 폴더명을 입력하세요."/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">DataCenter DataStore</label>
								<div>
									<input name="vCenterDatastore" type="text"  style="float:left;width:60%;"  required placeholder="DataCenter 데이터 스토어를 입력하세요."/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">vCenter Persistent Datastore</label>
								<div>
									<input name="vCenterPersistentDatastore" type="text"  style="float:left;width:60%;"  required placeholder="DataCenter 영구 데이터 스토어를 입력하세요."/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">vCenter Disk Path</label>
								<div>
									<input name="vCenterDiskPath" type="text"  style="float:left;width:60%;"  required placeholder="DDataCenter 디스크 경로를 입력하세요."/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">vCenter Cluster</label>
								<div>
									<input name="vCenterCluster" type="text"  style="float:left;width:60%;"  required placeholder="DataCenter 클러스터명을 입력하세요."/>
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
	
	<div id="DefaultInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title" id="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:2%;display:inline-block;width:97%;">
	            <ul class="progressStep_6" >
		            <li class="pass"> 정보</li>
		            <li class="active">기본 정보</li>
		            <li class="before">네트워크 정보</li>
		            <li class="before">리소스 정보</li>		            
		            <li class="before">배포 파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
	        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
				<div class="panel panel-info">	
					<div class="panel-heading"><b>기본 정보</b></div>
					<div class="panel-body" style="padding:5px 5% 10px 5%;">
				    	<div class="w2ui-field">
							<label style="text-align: left;width:40%;font-size:11px;">배포명</label>
							<div>
								<input name="deploymentName" type="text"  style="float:left;width:60%;" required placeholder="배포명을 입력하세요."/>
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left;width:40%;font-size:11px;">디렉터 명</label>
							<div>
								<input name="directorName" type="text"  style="float:left;width:60%;" required placeholder="디렉터 명을 입력하세요."/>
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left;width:40%;font-size:11px;">NTP</label>
							<div>
								<input name="ntp" type="text"  style="float:left;width:60%;"  required placeholder="예) 10.0.0.2"/>
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left;width:40%;font-size:11px;">BOSH 릴리즈</label>
							<img alt="boshRelase-help-info" class="boshRelase-info" style="width:18px; position:absolute; left:19%; margin-top:5px" data-trigger="hover" data-toggle="popover" title="Bosh 릴리즈 버전 정보"  data-html="true" src="../images/help-Info-icon.png">	
							<div id="boshReleaseDiv"></div>
						</div>
						<div class="w2ui-field">
							<label style="text-align: left;width:40%;font-size:11px;">BOSH CPI 릴리즈</label>
							<div id="boshCpiReleaseDiv"></div>
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
					</div>
				</div>
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
		    	<button class="btn" style="float: left;" onclick="saveDefaultInfo('before');">이전</button>
		        <button class="btn" style="float: right;padding-right:15%" onclick="saveDefaultInfo('after');" >다음>></button>
		    </div>
		</div>
	</div>	
	
	<div id="NetworkInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:2%;display:inline-block;width:97%;">
	            <ul class="progressStep_6" >
		            <li class="pass"> 정보</li>
		            <li class="pass">기본 정보</li>
		            <li class="active">네트워크 정보</li>
		            <li class="before">리소스 정보</li>		            
		            <li class="before">배포 파일 정보</li>
		            <li class="before">설치</li>
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
						<div class="panel panel-info">	
							<div  class="panel-heading" style="padding:5px 5% 10px 5%;"><b>Internal</b></div>
							<div class="panel-body">
								<div class="w2ui-field" >
									<label style="text-align: left;width:40%;font-size:11px;">디렉터 내부 IP</label> 
									<div>
										<input name="privateStaticIp" type="text"  style="float:left;width:60%;" required placeholder="예) 10.0.0.20"/>
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
	
	<div id="VsphereNetworkInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:2%;display:inline-block;width:97%;">
	            <ul class="progressStep_6" >
		            <li class="pass"> 정보</li>
		            <li class="pass">기본 정보</li>
		            <li class="active">네트워크 정보</li>
		            <li class="before">리소스 정보</li>		            
		            <li class="before">배포 파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
	        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
				<div class="panel panel-info">	
					<div class="panel-heading"><b>네트워크 정보</b></div>
					<div class="panel-body">
						<!-- Internal -->
						<div class="panel panel-info">	
							<div  class="panel-heading" style="padding:5px 5% 10px 5%;"><b>Internal</b></div>
							<div class="panel-body">
								<div class="w2ui-field" >
									<label style="text-align: left;width:40%;font-size:11px;">디렉터 내부 IPs</label> 
									<div>
										<input name="privateStaticIp" type="text"  style="float:left;width:60%;" required placeholder="예) 10.0.0.20"/>
										<div class="isMessage"></div>
									</div>
								</div>
						    	<div class="w2ui-field">
									<label class="subnetId" style="text-align: left;width:40%;font-size:11px;">네트워크 명</label>
									<div>
										<input name="subnetId" type="text"  style="float:left;width:60%;" required placeholder="네트워크 명을 입력하세요."/>
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
							</div>
						</div>
						<!-- External -->
						<div class="panel panel-info">	
							<div  class="panel-heading" style="padding:5px 5% 10px 5%;"><b>External</b></div>
							<div class="panel-body">
								<div class="w2ui-field">
									<label style="text-align: left;width:40%;font-size:11px;">설치관리자 IPs</label> 
									<div>
										<input name="publicStaticIp" type="text"  style="float:left;width:60%;" required placeholder="예) 10.0.0.20"/>
										<div class="isMessage"></div>
									</div>
								</div>
								<div class="w2ui-field">
									<label style="text-align: left;width:40%;font-size:11px;">네트워크 명</label>
									<div>
										<input name="publicSubnetId" type="text"  style="float:left;width:60%;" required placeholder="네트워크 명을 입력하세요."/>
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
	
	<div id="ResourceInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
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
						<div class="w2ui-field" id="cloudInstanceTypeDiv">
							<label style="text-align: left;width:40%;font-size:11px;">인스턴스 유형</label>
							<div>
								<input name="cloudInstanceType" type="text"  style="float:left;width:65%;"  required placeholder="인스턴스 유형을 입력하세요."/>
								<div class="isMessage"></div>
							</div>
						</div>
						
						<div class="w2ui-field" id="resourcePoolCpuDiv">
							<label style="text-align: left;width:40%;font-size:11px;">리소스 풀 CPU</label>
							<div>
								<input name="resourcePoolCpu" type="text"  style="float:left;width:65%;"  required placeholder="리소스 풀 CPU 수를 입력하세요. 예) 2" onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  />
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field" id="resourcePoolRamDiv">
							<label style="text-align: left;width:40%;font-size:11px;">리소스 풀 RAM</label>
							<div>
								<input name="resourcePoolRam" type="text"  style="float:left;width:65%;"  required placeholder="리소스 풀 RAM(MB)을 입력하세요. 예) 4096" onkeydown="return onlyNumber(event);" onkeyup='removeChar(event)' style='ime-mode:disabled;'  />
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field" id="resourcePoolDiskDiv">
							<label style="text-align: left;width:40%;font-size:11px;">리소스 풀 DISK</label>
							<div>
								<input name="resourcePoolDisk" type="text"  style="float:left;width:65%;"  required placeholder="리소스 풀 DISK(MB)를 입력하세요. 예) 20000" onkeydown="return onlyNumber(event);"  onkeyup='removeChar(event)' style='ime-mode:disabled;'  />
								<div class="isMessage"></div>
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
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
		        <button class="btn" style="float: left;" onclick="saveResourceInfo('before');" >이전</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveResourceInfo('after');" >다음>></button>
		    </div>
		</div>
	</div>
	
	<!-- Deploy DIV -->
	<div id="DeployDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:2%;display:inline-block;width:97%;">
	            <ul class="progressStep_6" >
		            <li class="pass">오픈스택 정보</li>
		            <li class="pass">기본 정보</li>
		            <li class="pass">네트워크 정보</li>
		            <li class="pass">리소스 정보</li>		            
		            <li class="active">배포 파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div style="width:93%;height:84%;float: left;display: inline-block;margin:10px 0 0 1%;">
				<textarea id="deployInfo" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:3%;" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" style="float: left;" onclick="confirmDeploy('before');">이전</button>
			<button class="btn" style="float: right; padding-right: 15%" onclick="confirmDeploy('after');">다음>></button>
		</div>
	</div>
	
	<!-- Install DIV -->
	<div id="InstallDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:2%;display:inline-block;width:97%;">
	            <ul class="progressStep_6" >
		            <li class="pass">오픈스택 정보</li>
		            <li class="pass">기본 정보</li>
		            <li class="pass">네트워크 정보</li>
		            <li class="pass">리소스 정보</li>		            
		            <li class="pass">배포 파일 정보</li>
		            <li class="active">설치</li>
	            </ul>
	        </div>
	        <div style="width:93%;height:84%;float: left;display: inline-block;margin:10px 0 0 1%;">
				<textarea id="installLogs" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:3%;" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
				<!-- 설치 실패 시 -->
				<button class="btn" id="deployPopupBtn" style="float: left;" onclick="deployPopup();" disabled>이전</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="popupComplete();">닫기</button>
		</div>		
	</div>
