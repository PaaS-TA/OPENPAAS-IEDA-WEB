<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" type="text/css" href="/css/progress-step.css"/>
<style type="text/css">
.w2ui-popup .w2ui-msg-body{background-color: #FFF; }

#browse {
	position: relative;
    display : inline-block;        
    width : 72px;
    height : 25px;
    background : #147ad0;
    color : #FFF;
    left: 5px;
    text-decoration : none;dto
    line-height : 25px;
    text-align : center;
    margin-top: 3px;
}

#keyPathFileName{
	background-color:#fff;
	text-rendering: auto;
    color: initial;
    letter-spacing: normal;
    word-spacing: normal;
    text-transform: none;
    text-indent: 0px;
    text-shadow: none;
    display: inline-block;
    -webkit-writing-mode: horizontal-tb;
    cursor: auto;
}
</style>
<script type="text/javascript" src="/js/sockjs-0.3.4.js"></script>
<script type="text/javascript" src="/js/stomp.js"></script>
<script type="text/javascript">

//private common variable
var structureType = "";
var bootstrapSeq= "";
var keyPathFileList = "";
var networkInfo = "";
var resourceInfo = "";
var deployInfo = "";
var deployFileName = "";
var installClient = "";
var deleteClient = "";

//private AWS variable
var awsInfo = "";
//private AWS variable
var openstackInfo = "";
var osBoshInfo = "";


$(function() {	
 	$('#config_bootstrapGrid').w2grid({
		name: 'config_bootstrapGrid',
		header: '<b>BOOTSTRAP 목록</b>',
		method: 'GET',
 		multiSelect: false,
		show: {	
				selectColumn: true,
				footer: true},
		style: 'text-align: center',
		columns:[
		      {field: 'recid', 	caption: 'recid', hidden: true}
			, {field: 'id', caption: 'ID', hidden: true}
			, {field: 'iaas', caption: 'IaaS', size: '20%'}
			, {field: 'directorPrivateIp', caption: 'PrivateIp', size: '30%'}
			, {field: 'directorPublicIp', caption: 'PublicIp', size: '30%'}
			, {field: 'createdDate', caption: 'created', size: '10%'}
			],
		onClick:function(event) {
			var grid = this;
			event.onComplete = function() {
				var sel = grid.getSelection();
				console.log("##### sel ::: " + sel);
				if ( sel == null || sel == "") {
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
 	
 	//BootStrap 설치
 	$("#installBtn").click(function(){
 		bootSetPopup();
 	});
 	
 	//BootStrap 수정
	$("#modifyBtn").click(function(){
		if($("#modifyBtn").attr('disabled') == "disabled") return;
		w2confirm({
			title 	: "BOOTSTRAP 수정",
			msg		: "BOOTSTRAP 정보를 수정하시겠습니까?",
			yes_text: "확인",
			yes_callBack : function(envent){
				//ajax data call
				var selected = w2ui['config_bootstrapGrid'].getSelection();
				console.log("modify Click!!!");
				if( selected.length == 0 ){
					w2alert("선택된 정보가 없습니다.", "BOOTSTRAP 수정");
					return;
				}
				else{
					var record = w2ui['config_bootstrapGrid'].get(selected);
					console.log(record.iaas);
					if(record.iaas == "AWS") {
						getBootstrapAwsData(record);
						return;
					}
					else{
						getBootstrapOpenstackData(record);
						return;
					}
				}
			},
			no_text : "취소"
		});
 	});
 	
 	//BootStrap 삭제
	$("#deleteBtn").click(function(){
		if($("#deleteBtn").attr('disabled') == "disabled") return;
		w2confirm({
			title 	: "BOOTSTRAP 삭제",
			msg		: "BOOTSTRAP 정보를 삭제하시겠습니까?",
			yes_text: "확인",
			yes_callBack : function(event){
				//event.onComplete= function(){
					//ajax data call
					var selected = w2ui['config_bootstrapGrid'].getSelection();
					console.log("Delete Click!!!");
					if( selected.length == 0 ){
						w2alert("선택된 정보가 없습니다.", "BOOTSTRAP 삭제");
						return;
					}
					else{
						var record = w2ui['config_bootstrapGrid'].get(selected);
						console.log(record.iaas);
						deletePop(record);
						
					}
			//	}
			},
			no_text : "취소"
		});
 	});
 	
 	
	doSearch();
});

//조회기능
function doSearch() {
	//doButtonStyle();
	w2ui['config_bootstrapGrid'].load("<c:url value='/bootstraps'/>",
			function (){
				doButtonStyle();
			});
}

//BOOTSTRAP 수정시 AWS 정보 조회
function getBootstrapAwsData(record){
	
	var url = "/bootstrap/aws/"+record.id;
	$.ajax({
		type : "GET",
		url : url,
		contentType : "application/json",
		success : function(data, status) {
			console.log("== /bootstrap/aws/ RESULT :: ");
			if ( data == null || data == "" ){
				//isOk = false;
			} 
			else {
				initSetting();
				//var content = JSON.parse(data.contents);
				console.log("=== Content ::: " + data.contents);
				settingAWSData(data.contents);
			}
		},
		error : function(request, status, error) {
			var errorResult = JSON.parse(request.responseText);
			console.log("console log ::: " +errorResult.message);
			w2alert(errorResult.message, "BOOTSTRAP 수정");
		}
	});	
}

function getBootstrapOpenstackData(record){
	var url = "/bootstrap/openstack/"+record.id;
	$.ajax({
		type : "GET",
		url : url,
		contentType : "application/json",
		success : function(data, status) {
			console.log("== /bootstrap/openstack/ RESULT :: ");
			if ( data == null || data == "" ){
				//isOk = false;
			} 
			else {
				initSetting();
				//var content = JSON.parse(data.contents);
				console.log("=== Content ::: " + data.contents);
				settingOpenstackData(data.contents);
			}
		},
		error : function(request, status, error) {
			var errorResult = JSON.parse(request.responseText);
			console.log("console log ::: " +errorResult.message);
			w2alert(errorResult.message, "BOOTSTRAP 수정");
		}
	});
}

function settingAWSData(contents){
	bootstrapSeq = contents.id;
	structureType = "AWS";
	awsInfo = {
			iaas		 	: "AWS",
			awsKey			: contents.accessKey,
			awsPw			: contents.secretAccessKey,
			defaultSecurityGroups	: contents.defaultSecurityGroups,
			privateKeyName	: contents.defaultKeyName,
			privateKeyPath	: contents.privateKeyPath
	}
	networkInfo = {
			id					: contents.id,
			subnetRange			: contents.subnetRange,
			gateway				: contents.gateway,
			dns					: contents.dns,
			subnetId			: contents.subnetId,
			directorPrivateIp	: contents.directorPrivateIp,
			directorPublicIp	: contents.directorPublicIp
	}
	resourceInfo = {
			id				: contents.id,
			targetStemcell	: contents.stemcellName,
			instanceType	: contents.instanceType,
			region			: contents.region,
			availabilityZone: contents.availabilityZone,
			microBoshPw		: contents.microBoshPw,
			ntp				: contents.ntp
	}
	awsPopup();	
}

function settingOpenstackData(contents){
	bootstrapSeq =  contents.id;
	structureType = "OPENSTACK";
	console.log(bootstrapSeq + "/" + structureType);
	osBoshInfo = {
			id				: bootstrapSeq,
			iaas 			: structureType,
			boshName		: contents.boshName,
			boshUrl			: contents.boshUrl,
			boshCpiUrl		: contents.boshCpiUrl,
			privateKeyPath	: contents.privateKeyPath			
	}
	
	openstackInfo = {
			id					: bootstrapSeq,
			privateStaticIp		: contents.privateStaticIp,
			publicStaticIp		: contents.publicStaticIp,
			directorName		: contents.directorName,
			authUrl				: contents.authUrl,
			tenant				: contents.tenant,
			userName			: contents.userName,
			apiKey				: contents.apiKey,
			defaultKeyName		: contents.defaultKeyName,
			defaultSecurityGroups: contents.defaultSecurityGroups,
			ntp					: contents.ntp
	}
	
	networkInfo = {
			id				: bootstrapSeq,
			subnetRange		: contents.subnetRange,
			subnetGateway	: contents.subnetGateway,
			subnetDns		: contents.subnetDns,
			cloudNetId		: contents.cloudNetId
	}
	
	resourceInfo = {
			id					: bootstrapSeq,
			stemcellUrl			: contents.stemcellUrl,
			envPassword			: contents.envPassword,
			cloudInstanceType	: contents.cloudInstanceType
	}
	
	osBoshInfoPop();
			
}

//BOOTSTRAP 삭제 실행
function deletePop(record){
	var body = '<div style="margin:10px 0;"><b>▶ 설치 로그</b></div>';
	//body += '<div>';
	body += '<textarea id="deleteLogs" style="width:95%;height:250px;overflow-y:visible;resize:none;background-color: #FFF;margin-left:2%" readonly="readonly"></textarea>';
	//body += '</div>';
	
	w2popup.open({
		width : 610,
		height : 400,
		title : "<b>BOOTSTRAP 삭제</b>",
		body  : body,
		buttons : '<button class="btn" style="float: right; padding-right: 15%" onclick="popupComplete();;">완료</button>',
		showMax : true,
		onOpen : function(event){
			event.onComplete = function(){
			console.log("Delete Pop");
				var socket = new SockJS('/bootstrapDelete');
				deleteClient = Stomp.over(socket); 
				deleteClient.connect({}, function(frame) {
					console.log('Connected Frame : ' + frame);
					deleteClient.subscribe('/bootstrap/bootstrapDelete', function(data){
				        console.log('Connected: Data : ' + data);
			        	var deleteLogs = $(".w2ui-msg-body #deleteLogs");
			        	deleteLogs.append(data.body + "\n").scrollTop( deleteLogs[0].scrollHeight );
			        	
			        	if( data == "complete"){
			        		deleteClient.res
			        		deleteClient.disconnect(function(){
			        			console.log("disconnect");
			        		});//callback
			        	}
			        });
					deleteClient.send('/send/bootstrapDelete', {}, JSON.stringify({iaas:record.iaas, id:record.id}));
			    });
			}
		},
		onClose : function (event){
			event.onComplete= function(){
				/* $("body ").remove("#deleteLogs");
				deleteLogs.text(""); */
				$(".w2ui-msg-body #deleteLogs").text("");
				w2ui['config_boshGrid'].reset();
				console.log("close");
				deleteClient.disconnect();
				deleteClient.close();
				deleteClient = "";
				doSearch();
			}
		}
	});
}

function gridReload(){
	console.log("delete complete!");
	w2ui['config_bootstrapGrid'].reset();
	doSearch();
}

function doButtonStyle(){
	//Button Style init
	$('#modifyBtn').attr('disabled', true);
	$('#deleteBtn').attr('disabled', true);
}

//다른페이지 이동시 호출
function clearMainPage() {
	$().w2destroy('config_bootstrapGrid');
}

//화면 리사이즈시 호출
$( window ).resize(function() {
	setLayoutContainerHeight();
});


//Bootstrap 
function bootSetPopup() {
	w2confirm({
		width 			: 500,
		height 			: 180,
		title 			: '<b>BOOTSTRAP 설치</b>',
		msg 			: $("#bootSelectBody").html(),
		modal			: true,
		yes_text 		: "확인",
		no_text 		: "취소",
		yes_callBack 	: function(){
			//alert($("input[name='structureType']").val());
			structureType = $(".w2ui-msg-body input:radio[name='structureType']:checked").val();
			if(structureType){
				console.log("iaas ::: " + structureType);
				if( structureType == "AWS") awsPopup();
				else osBoshInfoPop();				
			}
			else{
				w2alert("설치할 Infrastructure 을 선택하세요");
			}
		}
	});
}

//AWS Info Setting Popup
function awsPopup(){
	$("#awsSettingInfoDiv").w2popup({
		width : 610,
		height : 430,
		onClose : initSetting,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
				if(awsInfo != ""){
					$(".w2ui-msg-body input[name='awsKey']").val(awsInfo.awsKey);
					$(".w2ui-msg-body input[name='awsPw']").val(awsInfo.awsPw);
					$(".w2ui-msg-body input[name='defaultSecurityGroups']").val(awsInfo.defaultSecurityGroups);
					$(".w2ui-msg-body input[name='privateKeyName']").val(awsInfo.privateKeyName);
					$(".w2ui-msg-body input[name='privateKeyPath']").val(awsInfo.privateKeyPath);
				}
				//keyPathFile list 
				getKeyPathFileList();				
			}
		}
	});
}

function getKeyPathFileList(){
	$.ajax({
		type : "GET",
		url : "/bootstrap/getKeyPathFileList",
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

function changeKeyPathType(type){
	console.log(type);
	var keyPathDiv = $('.w2ui-msg-body #keyPathDiv');
	var fileUploadInput = '<span><input type="file" name="keyPathFile" onchange="setPrivateKeyPathFileName(this);" hidden="true"/>';
	fileUploadInput += '<input type="text" id="keyPathFileName" style="width:40%;" readonly  onClick="openBrowse();" placeholder="Key File을 선택해주세요."/>';
	fileUploadInput += '<a href="#" id="browse" onClick="openBrowse();">Browse </a></span>';
	var selectInput = '<input type="list" name="keyPathList" style="float: left;width:60%;" onchange="setPrivateKeyPath(this.value);"/>';
	
	if(type == "list") {
		keyPathDiv.html(selectInput);		
		$('#w2ui-popup #keyPathDiv input[type=list]').w2field('list', { items: keyPathFileList , maxDropHeight:200, width:250});
		if(awsInfo.privateKeyPath) $(".w2ui-msg-body input[name='keyPathList']").data('selected', {text:awsInfo.privateKeyPath});
	}else{		
		keyPathDiv.html(fileUploadInput);
		//FileInput Ui (bootstrap-fileInput)) 
		$(".w2ui-msg-body input[name='keyPathFile']").hide();		
	}
}

function openBrowse(){
	$(".w2ui-msg-body input[name='keyPathFile']").click();
}

//Save AWS Setting Info
function saveAwsSettingInfo(){
	awsInfo = {
			id				: bootstrapSeq,
			iaas			: structureType,
			awsKey			: $(".w2ui-msg-body input[name='awsKey']").val(),
			awsPw			: $(".w2ui-msg-body input[name='awsPw']").val(),
			defaultSecurityGroups	: $(".w2ui-msg-body input[name='defaultSecurityGroups']").val(),
			privateKeyName	: $(".w2ui-msg-body input[name='privateKeyName']").val(),
			privateKeyPath  : $(".w2ui-msg-body input[name='privateKeyPath']").val()
	}
	
	if( $(".w2ui-msg-body input[name='keyPathFile']").val() != null){
		var keyPathFile =  $(".w2ui-msg-body input[name='keyPathFile']").val().split('.').pop().toLowerCase();
		
		if($.inArray(keyPathFile, ['pem']) == -1) {
			w2alert("KeyPath File은 .pem 파일만 등록 가능합니다.", "BOOTSTRAP 설치");
			return;
		}
	}
	
	$.ajax({
		type : "PUT",
		url : "/bootstrap/bootstrapSetAws",
		contentType : "application/json",
		async : true,
		data : JSON.stringify(awsInfo), 
		success : function(data, status) {
			bootstrapSeq = data.id;
			console.log("keypath::"+ bootstrapSeq);
			keyPathFileUpload(structureType);
		},
		error : function( e, status ) {
			w2alert("AWS 설정 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
		}
	});
}

function keyPathFileUpload(iaas){
	var awsForm = $(".w2ui-msg-body #awsForm")[0];
	var awsFormData = new FormData(awsForm);
	$.ajax({
		type : "POST",
		url : "/bootstrap/keyPathFileUpload",
		enctype : 'multipart/form-data',
		dataType: "text",
		async : true,
		processData: false, 
		contentType:false,
		data : awsFormData,  
		success : function(data, status) {
			if(iaas=="AWS") awsNetworkPopup();
			else openstackInfoPopup();
		},
		error : function( e, status ) {
			w2alert("AWS 설정 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
		}
	});
}

//Network Info Setting Popup
function awsNetworkPopup(){
	$("#networkSettingInfoDiv").w2popup({
		width : 610,
		height : 420,
		modal	: true,
		onClose : initSetting,
		onOpen : function(event){
			event.onComplete = function(){
				setNetworkData();	
			}
		}
	});
}

function setNetworkData(){
	if( networkInfo != ""){
		$(".w2ui-msg-body input[name='subnetRange']").val(networkInfo.subnetRange);
		$(".w2ui-msg-body input[name='gateway']").val(networkInfo.gateway);
		$(".w2ui-msg-body input[name='dns']").val(networkInfo.dns);
		$(".w2ui-msg-body input[name='subnetId']").val(networkInfo.subnetId);
		$(".w2ui-msg-body input[name='directorPrivateIp']").val(networkInfo.directorPrivateIp);
		$(".w2ui-msg-body input[name='directorPublicIp']").val(networkInfo.directorPublicIp);
	}
}

//Save Network Setting Info
function saveNetworkInfo(param){
	if(bootstrapSeq == ""){ w2alert("BOOTSTRAP ID가 존재하지 않습니다."); return;}
	
	networkInfo = {
			id			: bootstrapSeq,
			subnetRange			: $(".w2ui-msg-body input[name='subnetRange']").val(),
			gateway				: $(".w2ui-msg-body input[name='gateway']").val(),
			dns					: $(".w2ui-msg-body input[name='dns']").val(),
			subnetId			: $(".w2ui-msg-body input[name='subnetId']").val(),
			directorPrivateIp	: $(".w2ui-msg-body input[name='directorPrivateIp']").val(),
			directorPublicIp	: $(".w2ui-msg-body input[name='directorPublicIp']").val()
	}
	
	if(param == 'before'){
		awsPopup();
	}
	else{
		$.ajax({
			type : "PUT",
			url : "/bootstrap/bootstrapSetAwsNetwork",
			contentType : "application/json",
			//dataType: "json",
			async : true,
			data : JSON.stringify(networkInfo), 
			success : function(data, status) {
				 resourcePopup();
			},
			error : function( e, status ) {
				w2alert("네트워크 설정 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
			}
		});
	}
}


function resourcePopup(){
	//getStemcellList();
	$("#resourceSettingInfoDiv").w2popup({
		width : 610,
		height : 430,
		modal	: true,
		onOpen : function(event){
			event.onComplete = function(){	
				getStemcellList();//스템셀 리스트 가져오기
			}
		},
		onClose : initSetting
	});
}

function setReourceData(){
	if(resourceInfo != ""){
		$(".w2ui-msg-body input[name='targetStemcell']").data('selected', {text:resourceInfo.targetStemcell});
		
		$(".w2ui-msg-body input[name='instanceType']").val(resourceInfo.instanceType);
		$(".w2ui-msg-body input[name='region']").val(resourceInfo.region);
		$(".w2ui-msg-body input[name='availabilityZone']").val(resourceInfo.availabilityZone);
		$(".w2ui-msg-body input[name='microBoshPw']").val(resourceInfo.microBoshPw);
		$(".w2ui-msg-body input[name='ntp']").val(resourceInfo.ntp);
	}
}

function getStemcellList(){
	$.ajax({
		type : "GET",
		url : "/bootstrap/getLocalStemcellList",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		success : function(data, status) {
			$('#w2ui-popup input[type=list]').w2field('list', { items: data , maxDropHeight:300, width:500});
			setReourceData();
		},
		error : function( e, status ) {
			w2alert("스템셀 목록을 가져오는데 실패하였습니다.", "BOOTSTRAP 설치");
		}
	});
}

function saveResourceInfo(param){
	if(bootstrapSeq == ""){ w2alert("BOOTSTRAP ID가 존재하지 않습니다."); return;}
	
	resourceInfo = {
			id				: bootstrapSeq,
			targetStemcell	: $(".w2ui-msg-body input[name='targetStemcell']").val(),
			instanceType	: $(".w2ui-msg-body input[name='instanceType']").val(),
			region			: $(".w2ui-msg-body input[name='region']").val(),
			availabilityZone: $(".w2ui-msg-body input[name='availabilityZone']").val(),
			microBoshPw		: $(".w2ui-msg-body input[name='microBoshPw']").val(),
			ntp				: $(".w2ui-msg-body input[name='ntp']").val()
	}
	
	if( param == 'before') {
		networkPopup();return;
	}else {
		$.ajax({
			type : "PUT",
			url : "/bootstrap/bootSetAwsResource",
			contentType : "application/json",
			//dataType: "json",
			async : true,
			data : JSON.stringify(resourceInfo), 
			success : function(data, status) {
				if( data){
					console.log("## DeployFileName :: " + data.deploymentFile)
					deployFileName = data.deploymentFile;	
				}
				deployPopup();				
			},
			error : function( e, status ) {
				w2alert("리소스 설정 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
			}
		});
	}
}

function deployPopup(){
	$("#deployManifestDiv").w2popup({
		width 	: 610,
		height 	: 470,
		modal	: true,
		showMax : true,
		onClose : initSetting,
		onOpen : function(event){
			event.onComplete = function(){
				getDeployInfo();
			}
		}
	});
}

function getDeployInfo(){
	console.log(deployFileName);
	$.ajax({
		type : "POST",
		url : "/bootstrap/getBootstrapDeployInfo",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		data : JSON.stringify({deploymentFile:deployFileName}),
		success : function(data, status) {
			if(status == "success"){
				//deployInfo = data;
				$(".w2ui-msg-body #deployInfo").text(data);
			}
			else if(status == "204"){
				w2alert("sampleFile이 존재하지 않습니다.", "BOOTSTRAP 설치");
			}
		},
		error : function( e, status ) {
			w2alert("Temp 파일을 가져오는 중 오류가 발생하였습니다. ", "BOOTSTRAP 설치");
		}
	});
}

function confirmDeploy(param){
	//Deploy 단에서 저장할 데이터가 있는지 확인 필요
	//Confirm 설치하시겠습니까?
	if(param == 'after'){		
		w2confirm({
			msg			: "설치하시겠습니까?",
			title		: w2utils.lang('BOOTSTRAP 설치'),
			yes_text	: "예",
			no_text		: "아니오",
			yes_callBack: installPopup
			
		});
	}
	else{
		resourcePopup();
	}
}

function installPopup(){
	$("#installDiv").w2popup({
		width : 610,
		height : 490,
		modal	: true,
		showMax : true,
		onOpen : function(event){
			event.onComplete = function(){
				//deployFileName
				var socket = new SockJS('/bootstrapInstall');
				installClient = Stomp.over(socket); 
				installClient.connect({}, function(frame) {
					console.log('Connected Frame : ' + frame);
			        installClient.subscribe('/bootstrap/bootstrapInstall', function(data){
				        console.log('Connected: Data : ' + data);
			        	var installLogs = $(".w2ui-msg-body #installLogs");
			        	installLogs.append(data.body + "\n").scrollTop( installLogs[0].scrollHeight );
			        	
			        	if( data == "complete"){
			        		installClient.close()
			        		installClient.disconnect();//callback
			        		installClient = "";
			        	}
			        });
			        installClient.send('/send/bootstrapInstall', {}, JSON.stringify({deployFileName:deployFileName}));
			    });
			}
		},
		onClose : initSetting
	});
}

//팝업창 닫을 경우
function initSetting(){
	structureType = "";
	bootstrapSeq= "";
	awsInfo = "";
	networkInfo = "";
	resourceInfo = "";
	deployInfo = "";
	deployFileName = "";
	installClient = "";
	deleteClient = "";
}

function uploadStemcell(){
	$("#targetStemcellUpload").click();
}

function popupComplete(){
	//params init
	initSetting();
	//popup.close
	w2popup.close();
	//grid Reload
	gridReload();	
}

function setPrivateKeyPath(value){
	$(".w2ui-msg-body input[name=privateKeyPath]").val(value);
}

function setPrivateKeyPathFileName(fileInput){
	var file = fileInput.files;
	$(".w2ui-msg-body input[name=privateKeyPath]").val(file[0].name);
	$(".w2ui-msg-body #keyPathFileName").val(file[0].name);
}


/**
 * OPENSTACK script
 */
function osBoshInfoPop(){
	$("#osBoshInfoDiv").w2popup({
		width : 670,
		height : 430,
		onClose : initSetting,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
				if(osBoshInfo != ""){
					$(".w2ui-msg-body input[name='boshName']").val(osBoshInfo.boshName);
					$(".w2ui-msg-body input[name='boshUrl']").val(osBoshInfo.boshUrl);
					$(".w2ui-msg-body input[name='boshCpiUrl']").val(osBoshInfo.boshCpiUrl);
					$(".w2ui-msg-body input[name='privateKeyPath']").val(osBoshInfo.privateKeyPath);
				}
				//keyPathFile list 
				getKeyPathFileList();				
			}
		}
	});
}

//Openstack Bosh Info Save
function saveOsBoshInfo(){
	osBoshInfo = {
			iaas 			: structureType,
			boshName		: $(".w2ui-msg-body input[name=boshName]").val(),
			boshUrl			: $(".w2ui-msg-body input[name=boshUrl]").val(),
			boshCpiUrl		: $(".w2ui-msg-body input[name=boshCpiUrl]").val(),
			privateKeyPath	: $(".w2ui-msg-body input[name=privateKeyPath]").val()			
	}	
	
	//osBOshInfo SAVE
	$.ajax({
		type : "PUT",
		url : "/bootstrap/setOsBoshInfo",
		contentType : "application/json",
		async : true,
		data : JSON.stringify(osBoshInfo),
		success : function(data, status) {
			bootstrapSeq = data.id;
			keyPathFileUpload(structureType);
		},
		error : function( e, status ) {
			w2alert("AWS 설정 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
		}
	});
}

// Openstack Info Popup
function openstackInfoPopup(){
	$("#openstackInfoDiv").w2popup({
		width : 670,
		height : 530,
		onClose : initSetting,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
				if(openstackInfo != ""){
					$(".w2ui-msg-body input[name='privateStaticIp']").val(openstackInfo.privateStaticIp);
					$(".w2ui-msg-body input[name='publicStaticIp']").val(openstackInfo.publicStaticIp);
					$(".w2ui-msg-body input[name='directorName']").val(openstackInfo.directorName);
					$(".w2ui-msg-body input[name='authUrl']").val(openstackInfo.authUrl);
					$(".w2ui-msg-body input[name='tenant']").val(openstackInfo.tenant);
					$(".w2ui-msg-body input[name='authUrl']").val(openstackInfo.authUrl);
					$(".w2ui-msg-body input[name='userName']").val(openstackInfo.userName);
					$(".w2ui-msg-body input[name='apiKey']").val(openstackInfo.apiKey);
					$(".w2ui-msg-body input[name='defaultKeyName']").val(openstackInfo.defaultKeyName);
					$(".w2ui-msg-body input[name='defaultSecurityGroups']").val(openstackInfo.defaultSecurityGroups);
					$(".w2ui-msg-body input[name='ntp']").val(openstackInfo.ntp);
				}
			}
		}
	});	
}

function saveOpenstackInfo(type){
	openstackInfo = {
			id					: bootstrapSeq,
			privateStaticIp		: $(".w2ui-msg-body input[name='privateStaticIp']").val(),
			publicStaticIp		: $(".w2ui-msg-body input[name='publicStaticIp']").val(),
			directorName		: $(".w2ui-msg-body input[name='directorName']").val(),
			authUrl				: $(".w2ui-msg-body input[name='authUrl']").val(),
			tenant				: $(".w2ui-msg-body input[name='tenant']").val(),
			userName			: $(".w2ui-msg-body input[name='userName']").val(),
			apiKey				: $(".w2ui-msg-body input[name='apiKey']").val(),
			defaultKeyName		: $(".w2ui-msg-body input[name='defaultKeyName']").val(),
			defaultSecurityGroups: $(".w2ui-msg-body input[name='defaultSecurityGroups']").val(),
			ntp					: $(".w2ui-msg-body input[name='ntp']").val()
	}
	
	if( type == 'before') {
		osBoshInfoPop();
		return;
	}
	
	//SAVE
	$.ajax({
		type : "PUT",
		url : "/bootstrap/setOpenstackInfo",
		contentType : "application/json",
		async : true,
		data : JSON.stringify(openstackInfo),
		success : function(data, status) {
			//bootstrapSeq = data;
			osNetworkInfoPopup();
		},
		error : function( e, status ) {
			console.log(e + "status ::: " + status);
			w2alert("OPENSTACK 설정 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
		}
	});
}

// Openstack Network Info Popup
function osNetworkInfoPopup(){
	$("#osNetworkInfoDiv").w2popup({
		width : 670,
		height : 350,
		onClose : initSetting,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
				if(networkInfo != "" && structureType == "OPENSTACK"){
					$(".w2ui-msg-body input[name='subnetRange']").val(networkInfo.subnetRange);
					$(".w2ui-msg-body input[name='subnetGateway']").val(networkInfo.subnetGateway);
					$(".w2ui-msg-body input[name='subnetDns']").val(networkInfo.subnetDns);
					$(".w2ui-msg-body input[name='cloudNetId']").val(networkInfo.cloudNetId);
				}
			}
		}
	});	
}

function saveOsNetworkInfo(type){
	networkInfo = {
			id				: bootstrapSeq,
			subnetRange		: $(".w2ui-msg-body input[name='subnetRange']").val(),
			subnetGateway	: $(".w2ui-msg-body input[name='subnetGateway']").val(),
			subnetDns		: $(".w2ui-msg-body input[name='subnetDns']").val(),
			cloudNetId		: $(".w2ui-msg-body input[name='cloudNetId']").val()
	}
	
	if( type == "before") {
		openstackInfoPopup();
		return;
	}
	
	//SAVE
	$.ajax({
		type : "PUT",
		url : "/bootstrap/setOsNetworkInfo",
		contentType : "application/json",
		async : true,
		data : JSON.stringify(networkInfo),
		success : function(data, status) {
			osResourceInfoPopup();
		},
		error : function( e, status ) {
			w2alert("OPENSTACK Network 설정 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
		}
	});
}

//Openstack Resource Info Popup
function osResourceInfoPopup(){
	$("#osResourceInfoDiv").w2popup({
		width : 670,
		height : 400,
		onClose : initSetting,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
				if(resourceInfo != "" && structureType == "OPENSTACK"){
					$(".w2ui-msg-body input[name='stemcellUrl']").val(resourceInfo.stemcellUrl);
					$(".w2ui-msg-body input[name='envPassword']").val(resourceInfo.envPassword);
					$(".w2ui-msg-body input[name='cloudInstanceType']").val(resourceInfo.cloudInstanceType);
				}
			}
		}
	});	
}

function saveOsResourceInfo(type){
	resourceInfo = {
			id					: bootstrapSeq,
			stemcellUrl			: $(".w2ui-msg-body input[name='stemcellUrl']").val(),
			envPassword			: $(".w2ui-msg-body input[name='envPassword']").val(),
			cloudInstanceType	: $(".w2ui-msg-body input[name='cloudInstanceType']").val()
	}
	
	if( type == "before") osNetworkInfoPopup();
	
	//SAVE
	$.ajax({
		type : "PUT",
		url : "/bootstrap/setOsResourceInfo",
		contentType : "application/json",
		async : true,
		data : JSON.stringify(resourceInfo),
		success : function(data, status) {
			deployFileName = data.deploymentFile;
			osDeployPopup();
		},
		error : function( e, status ) {
			w2alert("OPENSTACK Resource 설정 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
		}
	});
}

function osDeployPopup(){
	$("#osDeployManifestDiv").w2popup({
		width 	: 670,
		height 	: 470,
		modal	: true,
		showMax : true,
		onClose : initSetting,
		onOpen : function(event){
			event.onComplete = function(){
				getDeployInfo();
			}
		}
	});
}


</script>

<div id="main">
	<div class="page_site">설치관리자 환경설정 > <strong>BOOTSTRAP 설치</strong></div>
	
	<!-- BOOTSTRAP 목록-->
	<div class="pdt20"> 
		<div class="title fl">BOOTSTRAP 목록</div>
		<div class="fr"> 
			<!-- Btn -->
			<span id="installBtn" class="btn btn-primary"  style="width:120px">설&nbsp;&nbsp;치</span>
			&nbsp;
			<span id="modifyBtn" class="btn btn-info" style="width:120px">수&nbsp;&nbsp;정</span>
			&nbsp;
			<span id="deleteBtn" class="btn btn-danger" style="width:120px">삭&nbsp;&nbsp;제</span>
			<!-- //Btn -->
	    </div>
	</div>
	<div id="config_bootstrapGrid" style="width:100%; height:500px"></div>	
	
</div>

<!-- Start AWS Popup -->
	<!-- Infrastructure  설정 DIV -->
	<div id="bootSelectBody" style="width:100%; height: 80px;" hidden="true">
		<div class="w2ui-lefted" style="text-align: left;">
			설치할 Infrastructure 을 선택하세요<br />
			<br />
		</div>
		<div class="col-sm-9">
			<div class="btn-group" data-toggle="buttons" >
				<label style="width: 100px;margin-left:40px;">
					<input type="radio" name="structureType" id="type1" value="AWS" checked="checked" tabindex="1" />
					&nbsp;AWS
				</label>
				<label style="width: 130px;margin-left:50px;">
					<input type="radio" name="structureType" id="type2" value="OPENSTACK" tabindex="2" />
					&nbsp;OPENSTACK
				</label>
			</div>
		</div>
	</div>

	<!-- AWS  설정 DIV -->
	<div id="awsSettingInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%;padding:15px 5px 0 5px;">
			<div style="margin-left:3%;">
	            <ul class="progressStep_5" >
		            <li class="active">AWS 설정</li>
		            <li class="before">Network 설정</li>
		            <li class="before">리소스 설정</li>
		            <li class="before">배포 Manifest</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" class="cont_title" style="margin-left:1.5%;">▶ AWS 설정정보</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
		    	<form id="awsForm" method="POST" enctype="multipart/form-data" action="/bootstrap/keyPathFileUpload">
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">AWS 키(access-key)</label>
			            <div>
			                <input name="awsKey" type="text" maxlength="100" size="30" style="float:left;width:60%;" tabindex="1"/>
			            </div>
			        </div>
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">AWS 비밀번호(secret-access-key)</label>
			            <div>
			                <input name="awsPw" type="password" maxlength="100" size="30" style="float:left;width:60%;" tabindex="2"/>
			            </div>
			        </div>
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">시큐리티 그룹명</label>
			            <div>
			                <input name="defaultSecurityGroups" type="text" maxlength="100" size="30" style="float:left;width:60%;" tabindex="3"/>
			            </div>
			        </div>
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">Private Key 명</label>
			            <div>
			                <input name="privateKeyName" type="text" maxlength="100" size="30" style="float:left;width:60%;" tabindex="4"/>
			            </div>
			        </div>
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">Private Key Path</label>
		                <div >
							<span onclick="changeKeyPathType('list');" style="width:30%;"><label><input type="radio" name="keyPathType" value="list" tabindex="5"/>&nbsp;리스트</label></span>
							&nbsp;&nbsp;
	  						<span onclick="changeKeyPathType('file');" style="width:30%;"><label><input type="radio" name="keyPathType" value="file" tabindex="6"/>&nbsp;파일업로드</label></span>
						</div>
			        </div>
			        <div class="w2ui-field">			         	
		                <input name="privateKeyPath" type="text" style="width:200px;" hidden="true" onclick="openBrowse();"/>
			            <label style="text-align: left;width:40%;font-size:11px;" class="control-label"></label>
						<div id="keyPathDiv" ></div>
			        </div>
		        </form>
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
		        <button class="btn" style="float: left;" onclick="w2popup.close();" tabindex="7">취소</button>
		        <button class="btn" style="float: right;padding-right:15%" onclick="saveAwsSettingInfo();" tabindex="8">다음>></button>
		    </div>
		</div>
	</div>
	
	
	<!-- Network  설정 DIV -->
	<div id="networkSettingInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%;padding:15px 5px 0 5px;">
			<div style="margin-left:3%;">
	            <ul class="progressStep_5">
		            <li class="pass">AWS 설정</li>
		            <li class="active">Network 설정</li>
		            <li class="before">리소스 설정</li>
		            <li class="before">배포 Manifest</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" class="cont_title" style="margin-left:1.5%;">▶ Network 설정정보</div>
			<div class="w2ui-page page-0" style="padding-left: 5%;">
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">Subnet Range</label>
					<div>
						<input name="subnetRange" type="text" maxlength="100" size="30" style="float:left;width:330px;" tabindex="1"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">Gateway</label>
					<div>
						<input name="gateway" type="text" maxlength="100" size="30" style="float:left;width:330px;" tabindex="2"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">DNS</label>
					<div>
						<input name="dns" type="text" maxlength="100" size="30" style="float:left;width:330px;" tabindex="3"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">Subnet ID</label>
					<div>
						<input name="subnetId" type="text" maxlength="100" size="30" style="float:left;width:330px;" tabindex="4"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">Director Private IP</label>
					<div>
						<input name="directorPrivateIp" type="text" maxlength="100" size="30" style="float:left;width:330px;" tabindex="5"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">Director Public IP</label>
					<div>
						<input name="directorPublicIp" type="text" maxlength="100" size="30" style="float:left;width:330px;" tabindex="6"/>
					</div>
				</div>
			</div>
			<br />
			<div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: left;" onclick="saveNetworkInfo('before');" tabindex="7">이전</button>
				<button class="btn" onclick="popupComplete();" tabindex="8">취소</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveNetworkInfo('after');" tabindex="9">다음>></button>
			</div>
		</div>
	</div>
	
	<!-- Resource  설정 DIV -->
	<div id="resourceSettingInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%;padding:15px 5px 0 5px;">
			<div style="margin-left:3%;">
	            <ul class="progressStep_5">
		            <li class="pass">AWS 설정</li>
		            <li class="pass">Network 설정</li>
		            <li class="active">리소스 설정</li>
		            <li class="before">배포 Manifest</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" class="cont_title" style="margin-left:1.5%;">▶ 리소스 설정정보</div>
			<div class="w2ui-page page-0" style="padding-left: 5%;">
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">스템셀 지정</label>
					<div>
						<!-- <input name="targetStemcell" type="text" maxlength="100" style="float: left;width:330px;margin-top:1.5px;" /> -->
						<div><input type="list" name="targetStemcell" style="float: left;width:330px;margin-top:1.5px;" tabindex="1"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">인스턴스 유형</label>
					<div>
						<input name="instanceType" type="text" maxlength="100" size="30" style="float:left;width:330px;" tabindex="2"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">Region</label>
					<div>
						<input name="region" type="text" maxlength="100" size="30" style="float:left;width:330px;" tabindex="3"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">Availability Zone</label>
					<div>
						<input name="availabilityZone" type="text" maxlength="100" size="30" style="float:left;width:330px;" tabindex="4"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">NTP</label>
					<div>
						<input name="ntp" type="text" maxlength="100" size="30" style="float:left;width:330px;" tabindex="5"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">MicroBOSH Password</label>
					<div>
						<input name="microBoshPw" type="password" maxlength="100" size="30" style="float:left;width:330px;" tabindex="5"/>
					</div>
				</div>
			</div>
			<br />
			<div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: left;" onclick="saveResourceInfo('before');" tabindex="6">이전</button>
				<button class="btn" onclick="popupComplete();" tabindex="7">취소</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveResourceInfo('after');" tabindex="8">다음>></button>
			</div>
		</div>
	</div>
	
	<!-- AWS Deploy DIV -->
	<div id="deployManifestDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%;height:100%;padding:15px 5px 0 5px;margin:0 auto;">
			<div style="margin-left:3%;">
	            <ul class="progressStep_5">
		            <li class="pass">AWS 설정</li>
		            <li class="pass">Network 설정</li>
		            <li class="pass">리소스 설정</li>
		            <li class="active">배포 Manifest</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" class="cont_title" style="margin-left:1.5%;">▶ 배포 Manifest 정보</div>
			<div style="width:95%;height:72%;float: left;">
				<textarea id="deployInfo" style="width:100%;height:100%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:2%" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" style="float: left;" onclick="confirmDeploy('before');">이전</button>
			<button class="btn" onclick="popupComplete();">취소</button>
			<button class="btn" style="float: right; padding-right: 15%" onclick="confirmDeploy('after');">다음>></button>
		</div>
	</div>
	
	<!-- AWS Install DIV -->
	<div id="installDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%;height:100%;padding:15px 5px 0 5px;">
			<div style="margin-left:3%;">
	            <ul class="progressStep_5">
		            <li class="pass">AWS 설정</li>
		            <li class="pass">Network 설정</li>
		            <li class="pass">리소스 설정</li>
		            <li class="pass">배포 Manifest</li>
		            <li class="active">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" class="cont_title" style="margin-left:1.5%;">▶ 설치 로그</div>
			<div style="height:80%;">
				<textarea id="installLogs" style="width:97%;height:88%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:1%" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
				<!-- 설치 실패 시 -->
				<button class="btn" style="float: left;" onclick="confirmDeploy('before');">이전</button>
				<button class="btn" onclick="popupComplete();">취소</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="popupComplete();">완료</button>
		</div>		
	</div>	
<!-- End AWS Popup -->

<!-- Start OPENSTACK POPUP  -->
	<div id="osBoshInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%;padding:15px 5px 0 5px;">
			<div style="margin-left:2%;">
	            <ul class="progressStep_6" >
		            <li class="active">BOSH 설정</li>
		            <li class="before">OPENSTACK 설정</li>
		            <li class="before">Network 설정</li>
		            <li class="before">리소스 설정</li>		            
		            <li class="before">배포 Manifest</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" class="cont_title" style="margin-left:1.5%;">▶ BOSH 설정정보</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
		    	<form id="osBoshInfoForm" method="POST" enctype="multipart/form-data" action="/bootstrap/keyPathFileUpload">
			    	<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">Bosh Name</label>
						<div>
							<input name="boshName" type="text" maxlength="100" size="30" style="float:left;width:60%;" tabindex="1"/>
						</div>
					</div>
					<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">Bosh Url</label>
					<div>
						<input name="boshUrl" type="text" maxlength="100" size="30" style="float:left;width:60%;" tabindex="2"/>
					</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">Bosh Cpi Url</label>
						<div>
							<input name="boshCpiUrl" type="text" maxlength="100" size="30" style="float:left;width:60%;" tabindex="3"/>
						</div>
					</div>
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">Private Key Path</label>
		                <div >
							<span onclick="changeKeyPathType('list');" style="width:30%;"><label><input type="radio" name="keyPathType" value="list" tabindex="4"/>&nbsp;리스트</label></span>
							&nbsp;&nbsp;
	  						<span onclick="changeKeyPathType('file');" style="width:30%;"><label><input type="radio" name="keyPathType" value="file" tabindex="5"/>&nbsp;파일업로드</label></span>
						</div>
			        </div>
			        <div class="w2ui-field">			         	
		                <input name="privateKeyPath" type="text" style="width:50%;" hidden="true" onclick="openBrowse();"/>
			            <label style="text-align: left;width:40%;font-size:11px;" class="control-label" ></label>
						<div id="keyPathDiv" ></div>
			        </div>
		        </form>
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
		        <button class="btn" style="float: left;" onclick="popupComplete();" tabindex="8">취소</button>
		        <button class="btn" style="float: right;padding-right:15%" onclick="saveOsBoshInfo();" tabindex="9">다음>></button>
		    </div>
		</div>
	</div>
	
	<div id="openstackInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%;padding:15px 5px 0 5px;">
			<div style="margin-left:2%;">
	            <ul class="progressStep_6" >
		            <li class="pass">BOSH 설정</li>
		            <li class="active">OPENSTACK 설정</li>
		            <li class="before">Network 설정</li>
		            <li class="before">리소스 설정</li>		            
		            <li class="before">배포 Manifest</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" class="cont_title" style="margin-left:1.5%;">▶ OPENSTACK 설정정보</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
		    	<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">Private Static IP</label>
					<div>
						<input name="privateStaticIp" type="text" maxlength="100" size="30" style="float:left;width:60%;" tabindex="1"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">Public Static IP</label>
					<div>
						<input name="publicStaticIp" type="text" maxlength="100" size="30" style="float:left;width:60%;" tabindex="2"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">Director Name</label>
					<div>
						<input name="directorName" type="text" maxlength="100" size="30" style="float:left;width:60%;" tabindex="3"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">Auth Url</label>
					<div>
						<input name="authUrl" type="text" maxlength="100" size="30" style="float:left;width:60%;" tabindex="4"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">Tenant</label>
					<div>
						<input name="tenant" type="text" maxlength="100" size="30" style="float:left;width:60%;" tabindex="5"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">User Name</label>
					<div>
						<input name="userName" type="text" maxlength="100" size="30" style="float:left;width:60%;"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">API Key</label>
					<div>
						<input name="apiKey" type="text" maxlength="100" size="30" style="float:left;width:60%;"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">Default KeyName</label>
					<div>
						<input name="defaultKeyName" type="text" maxlength="100" size="30" style="float:left;width:60%;"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">Default Security Groups</label>
					<div>
						<input name="defaultSecurityGroups" type="text" maxlength="100" size="30" style="float:left;width:60%;"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">NTP</label>
					<div>
						<input name="ntp" type="text" maxlength="100" size="30" style="float:left;width:60%;"/>
					</div>
				</div>				
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: left;" onclick="saveOpenstackInfo('before');">이전</button>
				<button class="btn" onclick="popupComplete();">취소</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveOpenstackInfo('after');">다음>></button>
		    </div>
		</div>
	</div>
	
	<div id="osNetworkInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%;padding:15px 5px 0 5px;">
			<div style="margin-left:2%;">
	            <ul class="progressStep_6" >
		            <li class="pass">BOSH 설정</li>
		            <li class="pass">OPENSTACK 설정</li>
		            <li class="active">Network 설정</li>
		            <li class="before">리소스 설정</li>		            
		            <li class="before">배포 Manifest</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" class="cont_title" style="margin-left:1.5%;">▶ Network 설정정보</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
		    	<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">subnetRange</label>
					<div>
						<input name="subnetRange" type="text" maxlength="100" size="30" style="float:left;width:60%;" tabindex="1"/>
					</div>
				</div>
				<div class="w2ui-field">
				<label style="text-align: left; width: 40%; font-size: 11px;">subnetGateway</label>
				<div>
					<input name="subnetGateway" type="text" maxlength="100" size="30" style="float:left;width:60%;" tabindex="2"/>
				</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">subnetDns</label>
					<div>
						<input name="subnetDns" type="text" maxlength="100" size="30" style="float:left;width:60%;" tabindex="3"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">cloudNetId</label>
					<div>
						<input name="cloudNetId" type="text" maxlength="100" size="30" style="float:left;width:60%;" tabindex="4"/>
					</div>
				</div>
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
		        <button class="btn" style="float: left;" onclick="saveOsNetworkInfo('before');" tabindex="5">이전</button>
				<button class="btn" onclick="popupComplete();" tabindex="6">취소</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveOsNetworkInfo('after');" tabindex="7">다음>></button>
		    </div>
		</div>
	</div>
	
	<div id="osResourceInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%;padding:15px 5px 0 5px;">
			<div style="margin-left:2%;">
	            <ul class="progressStep_6" >
		            <li class="pass">BOSH 설정</li>
		            <li class="pass">OPENSTACK 설정</li>
		            <li class="pass">Network 설정</li>
		            <li class="active">리소스 설정</li>		            
		            <li class="before">배포 Manifest</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" class="cont_title" style="margin-left:1.5%;">▶ Resource 설정정보</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
		    	<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">stemcellUrl</label>
					<div>
						<input name="stemcellUrl" type="text" maxlength="100" size="30" style="float:left;width:60%;" tabindex="1"/>
					</div>
				</div>
				<div class="w2ui-field">
				<label style="text-align: left; width: 40%; font-size: 11px;">envPassword</label>
				<div>
					<input name="envPassword" type="password" maxlength="100" size="30" style="float:left;width:60%;" tabindex="2"/>
				</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">cloudInstanceType</label>
					<div>
						<input name="cloudInstanceType" type="text" maxlength="100" size="30" style="float:left;width:60%;" tabindex="3"/>
					</div>
				</div>
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
		        <button class="btn" style="float: left;" onclick="saveOsResourceInfo('before');" tabindex="5">이전</button>
				<button class="btn" onclick="popupComplete();" tabindex="6">취소</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveOsResourceInfo('after');" tabindex="7">다음>></button>
		    </div>
		</div>
	</div>
	
	<!-- OPENSTACK Deploy DIV -->
	<div id="osDeployManifestDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%;height:100%;padding:15px 5px 0 5px;margin:0 auto;">
			<div style="margin-left:2%;">
	            <ul class="progressStep_6" >
		            <li class="pass">BOSH 설정</li>
		            <li class="pass">OPENSTACK 설정</li>
		            <li class="pass">Network 설정</li>
		            <li class="pass">리소스 설정</li>		            
		            <li class="active">배포 Manifest</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" class="cont_title" style="margin-left:1.5%;">▶ 배포 Manifest 정보</div>
			<div style="width:95%;height:72%;float: left;">
				<textarea id="deployInfo" style="width:100%;height:100%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:2%" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" style="float: left;" onclick="confirmDeploy('before');">이전</button>
			<button class="btn" onclick="popupComplete();">취소</button>
			<button class="btn" style="float: right; padding-right: 15%" onclick="confirmDeploy('after');">다음>></button>
		</div>
	</div>
	
	<!-- OPENSTACK Install DIV -->
	<div id="osInstallDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%;height:100%;padding:15px 5px 0 5px;">
			<div style="margin-left:3%;">
	            <ul class="progressStep_5">
		            <li class="pass">AWS 설정</li>
		            <li class="pass">Network 설정</li>
		            <li class="pass">리소스 설정</li>
		            <li class="pass">배포 Manifest</li>
		            <li class="active">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" class="cont_title" style="margin-left:1.5%;">▶ 설치 로그</div>
			<div style="height:80%;">
				<textarea id="installLogs" style="width:97%;height:88%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:1%" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
				<!-- 설치 실패 시 -->
				<button class="btn" style="float: left;" onclick="confirmDeploy('before');">이전</button>
				<button class="btn" onclick="popupComplete();">취소</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="popupComplete();">완료</button>
		</div>		
	</div>	