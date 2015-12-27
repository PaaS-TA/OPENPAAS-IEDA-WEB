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
.w2ui-field div.isMessage{
	display: inline-block;
	padding-left:10px;
	height:10px;
	font-size: 10px;
}
</style>
<script type="text/javascript" src="/js/sockjs-0.3.4.js"></script>
<script type="text/javascript" src="/js/stomp.js"></script>
<script type="text/javascript">

//private common variable
var iaas = "";
var bootstrapId= "";
var keyPathFileList = "";
var defaultInfo = "";
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
var fadeOutTime = 3000;

var boshReleases;
var boshCpiReleases;
var stemcells;

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
			, {field: 'deploymentName', caption: '배포명', size: '100px'}
			, {field: 'directorName', caption: '디렉터 명', size: '100px'}
			, {field: 'iaas', caption: 'IaaS', size: '100px'}
			, {field: 'boshRelease', caption: 'BOSH 릴리즈', size: '100px'}
			, {field: 'boshCpiRelease', caption: 'BOSH CPI 릴리즈', size: '150px'}
			, {field: 'subnetId', caption: '서브넷 ID', size: '100px'}
			, {field: 'privateStaticIp', caption: 'PRIVATE STATIC IP', size: '100px'}
			, {field: 'publicStaticIp', caption: 'PUBLIC STATIC IP', size: '100px'}
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
 		iaasSelectPopup();
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
				var selected = w2ui['config_bootstrapGrid'].getSelection();
				console.log("Delete Click!!!");
				if( selected.length == 0 ){
					w2alert("선택된 정보가 없습니다.", "BOOTSTRAP 삭제");
					return;
				}
				else{
					var record = w2ui['config_bootstrapGrid'].get(selected);
					deletePop(record);						
				}
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
				setOpenstackData(data.contents);
			}
		},
		error : function(request, status, error) {
			var errorResult = JSON.parse(request.responseText);
			w2alert(errorResult.message, "BOOTSTRAP 수정");
		}
	});
}

function settingAWSData(contents){
	bootstrapId = contents.id;
	iaas = "AWS";
	awsInfo = {
			id						: contents.id,
			iaas		 			: "AWS",
			accessKeyId				: contents.accessKeyId,
			secretAccessId			: contents.secretAccessId,
			defaultSecurityGroups	: contents.defaultSecurityGroups,
			region					: contents.region,
			availabilityZone		: contents.availabilityZone,
			privateKeyName			: contents.privateKeyName,
			privateKeyPath			: contents.privateKeyPath
	}
	
	defaultInfo = {
			id				: contents.id,
			deploymentName	: contents.deploymentName,
			directorName	: contents.directorName,
			boshRelease		: contents.boshRelease,
			boshCpiRelease	: contents.boshCpiRelease			
	}
	
	networkInfo = {
			id				: contents.id,
			privateStaticIp	: contents.privateStaticIp,
			publicStaticIp	: contents.publicStaticIp,
			subnetId		: contents.subnetId,
			subnetRange		: contents.subnetRange,
			subnetGateway	: contents.subnetGateway,
			subnetDns		: contents.subnetDns,
			ntp				: contents.ntp
	}
	
	resourceInfo = {
			id					: contents.id,
			stemcell			: contents.stemcell,
			cloudInstanceType	: contents.cloudInstanceType,
			boshPassword		: contents.boshPassword,
	}
	
	awsPopup();	
}

function setOpenstackData(contents){
	bootstrapId =  contents.id;
	iaas = "OPENSTACK";
	console.log(bootstrapId + "/" + iaas);
	
	openstackInfo = {
			id						: bootstrapId,
			authUrl					: contents.authUrl,
			tenant					: contents.tenant,
			userName				: contents.userName,
			apiKey					: contents.apiKey,
			defaultSecurityGroups	: contents.defaultSecurityGroups,
			privateKeyName			: contents.privateKeyName,
			privateKeyPath			: contents.privateKeyPath
	}
	
	osBoshInfo = {
			id					: bootstrapId,
			iaas 				: iaas,
			deploymentName		: contents.deploymentName,
			directorName		: contents.directorName,			
			boshRelease			: contents.boshRelease,
			boshCpiRelease		: contents.boshCpiRelease
	}
	networkInfo = {
			id				: bootstrapId,
			subnetId		: contents.subnetId,
			privateStaticIp	: contents.privateStaticIp,
			publicStaticIp	: contents.publicStaticIp,
			subnetRange		: contents.subnetRange,
			subnetGateway	: contents.subnetGateway,
			subnetDns		: contents.subnetDns,
			ntp				: contents.ntp
	}
	
	resourceInfo = {
			id					: bootstrapId,
			stemcellUrl			: contents.stemcellUrl,
			envPassword			: contents.envPassword,
			cloudInstanceType	: contents.cloudInstanceType
	}
	
	openstackPopup();
			
}

//BOOTSTRAP 삭제 실행
function deletePop(record){
	var body = '<div style="margin:10px 0;"><b>▶ 설치 로그</b></div>';
	//body += '<div>';
	body += '<textarea id="deleteLogs" style="width:95%;height:90%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:2%" readonly="readonly"></textarea>';
	//body += '</div>';
	deleteClient = "";
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
			        		deleteClient.disconnect(function(){
			        			console.log("disconnect");
			        			deleteClient = "";
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
				w2ui['config_bootstrapGrid'].reset();
				deleteClient.disconnect(function(){
			        			console.log("disconnect");
			        			deleteClient = "";
				});
				deleteClient = "";
				doSearch();
			}
		}
	});
}

function gridReload(){
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
function iaasSelectPopup() {
	w2confirm({
		width 			: 500,
		height 			: 180,
		title 			: '<b>BOOTSTRAP 설치</b>',
		msg 			: $("#bootSelectBody").html(),
		modal			: true,
		yes_text 		: "확인",
		no_text 		: "취소",
		yes_callBack 	: function(){
			//alert($("input[name='iaas']").val());
			iaas = $(".w2ui-msg-body input:radio[name='iaas']:checked").val();
			if(iaas){
				console.log("iaas ::: " + iaas);
				if( iaas == "AWS") awsPopup();
				else openstackPopup();				
			}
			else{
				w2alert("설치할 Infrastructure 을 선택하세요");
			}
		}
	});
}

//AWS Info Setting Popup
function awsPopup(){
	$("#awsDiv").w2popup({
		width : 670,
		height : 580,
		onClose : initSetting,
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
				getKeyPathFileList();
				getLocalBoshReleaseList();
				getLocalAwsBoshCpiReleaseList();
			}
		}
	});
}

function getKeyPathFileList(){
	$.ajax({
		type : "GET",
		url : "/common/getKeyPathFileList",
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

function getLocalBoshCpiList(){
	$.ajax({
		type : "GET",
		url : "/localBoshOpenstackCpiReleases",
		contentType : "application/json",
		async : true,
		success : function(data, status) {
			$('#w2ui-popup input[name=boshCpiRelease][type=list]').w2field('list', { items: data.records , maxDropHeight:200, width:250});
			if(osBoshInfo.boshCpiRelease) $(".w2ui-msg-body input[name='boshCpiRelease']").data('selected', {text:osBoshInfo.boshCpiRelease});			
		},
		error : function( e, status ) {
			w2alert("Bosh 목록을 가져오는데 실패하였습니다.", "BOOTSTRAP 설치");
		}
	});
}

function getLocalBoshList(){
	$.ajax({
		type : "GET",
		url : "/localBoshReleases",
		contentType : "application/json",
		async : true,
		success : function(data, status) {
			$('#w2ui-popup input[name=boshRelease][type=list]').w2field('list', { items: data.records , maxDropHeight:200, width:250});
			if(osBoshInfo.boshRelease) $(".w2ui-msg-body input[name='boshRelease']").data('selected', {text:osBoshInfo.boshRelease});
		},
		error : function( e, status ) {
			w2alert("Bosh Cpi 목록을 가져오는데 실패하였습니다.", "BOOTSTRAP 설치");
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
		if( awsInfo.privateKeyPath )
			$(".w2ui-msg-body input[name='keyPathList']").data('selected', {text:awsInfo.privateKeyPath});
		else if(openstackInfo.privateKeyPath)
			$(".w2ui-msg-body input[name='keyPathList']").data('selected', {text:openstackInfo.privateKeyPath});		
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
function saveAwsInfo(){
	
	awsInfo = {
			id						: bootstrapId,
			accessKeyId				: $(".w2ui-msg-body input[name='accessKeyId']").val(),
			secretAccessId			: $(".w2ui-msg-body input[name='secretAccessId']").val(),
			defaultSecurityGroups	: $(".w2ui-msg-body input[name='defaultSecurityGroups']").val(),
			region  				: $(".w2ui-msg-body input[name='region']").val(),
			availabilityZone  		: $(".w2ui-msg-body input[name='availabilityZone']").val(),
			privateKeyName  		: $(".w2ui-msg-body input[name='privateKeyName']").val(),
			privateKeyPath  		: $(".w2ui-msg-body input[name='privateKeyPath']").val()
	}
	
	if(validationAwsInfo()){
		$.ajax({
			type : "PUT",
			url : "/bootstrap/aws",
			contentType : "application/json",
			async : true,
			data : JSON.stringify(awsInfo), 
			success : function(data, status) {
				bootstrapId = data.id;
				console.log("keypath::"+ bootstrapId);
				keyPathFileUpload(iaas);
			},
			error : function( e, status ) {
				w2alert("AWS 설정 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
			}
		});
	}
	else{
		w2alert("필드값을 확인해 주세요.");
	}
}

function validationAwsInfo(){
	var checkValidation = true;
	var emptyFields = new Array();
		
	if( checkEmpty( $(".w2ui-msg-body input[name='accessKeyId']").val() ) ){
		emptyFields.push({name:"accessKeyId", label:"Access Key Id"});
		checkValidation = (checkValidation) ? false:false; 
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='secretAccessId']").val() ) ){
		emptyFields.push({name:"secretAccessId", label:"Secret Access Id"});
		checkValidation = (checkValidation) ? false:false;
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='defaultSecurityGroups']").val() ) ){
		emptyFields.push({name:"defaultSecurityGroups", label:"DEFAULT SECURITY GROUPS"});
		checkValidation = (checkValidation) ? false:false;
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='region']").val() ) ){
		emptyFields.push({name:"region", label:"REGION"});
		checkValidation = (checkValidation) ? false:false;
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='availabilityZone']").val() ) ){
		emptyFields.push({name:"availabilityZone", label:"Availability-Zone"});
		checkValidation = (checkValidation) ? false:false;
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='privateKeyName']").val() ) ){
		emptyFields.push({name:"privateKeyName", label:"Private Key Name"});
		checkValidation = (checkValidation) ? false:false;
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='privateKeyPath']").val() ) ){
		emptyFields.push({name:"privateKeyPath", label:"Private Key Path"});
		checkValidation = (checkValidation) ? false:false;
	}
	
	errFieldMessage(emptyFields);
	
	return checkValidation;
}

function errFieldMessage(emptyFields){
	if( emptyFields.length > 0 ){
		$(".w2ui-msg-body input[type=text]").parent().find(".isMessage").text("");
		
		emptyFields.map(function(obj){
			console.log("### Name/LABEL  " + obj.name+"/"+ obj.label);
			$(".w2ui-msg-body input[name='"+obj.name+"']").parent().find(".isMessage")
			.text(obj.label + "를(을) 입력하세요").css({"color":"red"});	
		});		
	}
}

function keyPathFileUpload(iaas){
	var awsForm = $(".w2ui-msg-body #awsForm")[0];
	var awsFormData = new FormData(awsForm);
	$.ajax({
		type : "POST",
		url : "/common/keyPathFileUpload",
		enctype : 'multipart/form-data',
		dataType: "text",
		async : true,
		processData: false, 
		contentType:false,
		data : awsFormData,  
		success : function(data, status) {
			if(iaas=="AWS") awsDefaultPopup();
			else osNetworkInfoPopup();
		},
		error : function( e, status ) {
			w2alert( iaas + " 설정 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
		}
	});
}

function awsDefaultPopup(){
	$("#awsDefaultDiv").w2popup({
		width : 670,
		height : 420,
		modal	: true,
		onClose : initSetting,
		onOpen : function(event){
			event.onComplete = function(){
				$('#w2ui-popup input[type=list][name=boshRelease]').w2field('list', { items: boshReleases , maxDropHeight:300, width:400});
				$('#w2ui-popup input[type=list][name=boshCpiRelease]').w2field('list', { items: boshCpiReleases , maxDropHeight:300, width:400});
				if(defaultInfo != "" ){
					$(".w2ui-msg-body input[name='deploymentName']").val(defaultInfo.deploymentName);
					$(".w2ui-msg-body input[name='directorName']").val(defaultInfo.directorName);
					$(".w2ui-msg-body input[name='boshRelease']").data('selected', {text : defaultInfo.boshRelease});
					$(".w2ui-msg-body input[name='boshCpiRelease']").data('selected', {text : defaultInfo.boshCpiRelease});
				}
			}
		}
	});
}

function getReleaseVersionList(){
	$.ajax({
		type : "GET",
		url : "/bosh/releases",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		data : JSON.stringify(boshInfo), 
		success : function(data, status) {
			console.log("Release List");
			releases = new Array();
			data.records.map(function (obj){
				console.log(obj.name+"/"+obj.version);
				releases.push(obj.name+"/"+obj.version);
			});			
		},
		error : function( e, status ) {
			w2alert("Release Version List 를 가져오는데 실패하였습니다.", "Bosh 설치");
		}
	});
}

function getLocalBoshReleaseList(){
	$.ajax({
		type : "GET",
		url : "/release/localBoshList",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		success : function(data, status) {
			boshReleases = data;			
		},
		error : function( e, status ) {
			w2alert("BOSH Release List 를 가져오는데 실패하였습니다.", "BOOTSTRAP 설치");
		}
	});
}

function getLocalAwsBoshCpiReleaseList(){
	$.ajax({
		type : "GET",
		url : "/release/localBoshAwsCpiList",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		success : function(data, status) {
			boshCpiReleases = data;			
		},
		error : function( e, status ) {
			w2alert("BOSH AWS CPI Release List 를 가져오는데 실패하였습니다.", "BOOTSTRAP 설치");
		}
	});
}

function getLocalOpenstackBoshCpiReleaseList(){
	$.ajax({
		type : "GET",
		url : "/release/localBoshOpenstackCpiList",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		success : function(data, status) {
			boshCpiReleases = data;
			//$('#w2ui-popup input[type=list][name=boshCpiRelease]').w2field('list', { items: data , maxDropHeight:300, width:400});
		},
		error : function( e, status ) {
			w2alert("OPENSTACK BOSH CPI Release List 를 가져오는데 실패하였습니다.", "BOOTSTRAP 설치");
		}
	});
}

function getStamcellList(){
	console.log("::::: getStamcellList");

	$.ajax({
		type : "GET",
		url : "/deploy/localStemcells",
		contentType : "application/json",
		async : true,
		data : JSON.stringify(boshInfo), 
		success : function(data, status) {
			console.log("Stemcell List");
			stemcells = data.records;
			/* data.records.map(function (obj){
			 	stemcells.push(obj.name+"/"+obj.version);
			}); */
		},
		error : function( e, status ) {
			w2alert("Stemcell List 를 가져오는데 실패하였습니다.", "Bosh 설치");
		}
	});
}


function saveAwsDefaultInfo(type){
	defaultInfo = {
			id				: bootstrapId,
			deploymentName 	: $(".w2ui-msg-body input[name='deploymentName']").val(),
			directorName 	:$(".w2ui-msg-body input[name='directorName']").val(),
			boshRelease 	:$(".w2ui-msg-body input[name='boshRelease']").val(),
			boshCpiRelease 	:$(".w2ui-msg-body input[name='boshCpiRelease']").val()
	}
	
	if( type == 'after'){
		if(validationAwsDefaultInfo()){
			$.ajax({
				type : "PUT",
				url : "/bootstrap/awsDefault",
				contentType : "application/json",
				async : true,
				data : JSON.stringify(defaultInfo), 
				success : function(data, status) {
					awsNetworkPopup();
				},
				error : function( e, status ) {
					w2alert("AWS 설정 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
				}
			});
		}
	}
	else if(type == 'before'){
		awsPopup();
	}
}

function validationAwsDefaultInfo(){
	var checkValidation = true;
	var emptyFields = new Array();
		
	if( checkEmpty( $(".w2ui-msg-body input[name='deploymentName']").val() ) ){
		emptyFields.push({name:"deploymentName", label:"배포명"});
		checkValidation = (checkValidation) ? false:false; 
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='directorName']").val() ) ){
		emptyFields.push({name:"directorName", label:"디렉터 명"});
		checkValidation = (checkValidation) ? false:false;
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='boshRelease']").val() ) ){
		emptyFields.push({name:"boshRelease", label:"BOSH RELEASE"});
		checkValidation = (checkValidation) ? false:false;
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='boshCpiRelease']").val() ) ){
		emptyFields.push({name:"boshCpiRelease", label:"BOSH CPI RELEASE"});
		checkValidation = (checkValidation) ? false:false;
	}
	
	errFieldMessage(emptyFields);
	
	return checkValidation;
}

//Network Info Setting Popup
function awsNetworkPopup(){
	$("#awsNetworkInfoDiv").w2popup({
		width : 670,
		height : 520,
		modal	: true,
		onClose : initSetting,
		onOpen : function(event){
			event.onComplete = function(){
				if( networkInfo != ""){
					$(".w2ui-msg-body input[name='subnetId']").val(networkInfo.subnetId);
					$(".w2ui-msg-body input[name='privateStaticIp']").val(networkInfo.privateStaticIp);
					$(".w2ui-msg-body input[name='publicStaticIp']").val(networkInfo.publicStaticIp);
					$(".w2ui-msg-body input[name='subnetRange']").val(networkInfo.subnetRange);
					$(".w2ui-msg-body input[name='subnetGateway']").val(networkInfo.subnetGateway);
					$(".w2ui-msg-body input[name='subnetDns']").val(networkInfo.subnetDns);
					$(".w2ui-msg-body input[name='ntp']").val(networkInfo.ntp);
				}
			}
		}
	});
}

//Save Network Setting Info
function saveAwsNetworkInfo(type){
	if(bootstrapId == ""){ w2alert("BOOTSTRAP ID가 존재하지 않습니다."); return;}
	
	networkInfo = {
			id					: bootstrapId,
			subnetId			: $(".w2ui-msg-body input[name='subnetId']").val(),
			privateStaticIp		: $(".w2ui-msg-body input[name='privateStaticIp']").val(),
			publicStaticIp		: $(".w2ui-msg-body input[name='publicStaticIp']").val(),
			subnetRange			: $(".w2ui-msg-body input[name='subnetRange']").val(),
			subnetGateway		: $(".w2ui-msg-body input[name='subnetGateway']").val(),
			subnetDns			: $(".w2ui-msg-body input[name='subnetDns']").val(),
			ntp					: $(".w2ui-msg-body input[name='ntp']").val()
	}
	
	if(type == 'before'){
		awsDefaultPopup();
		return;
	}
	else{
		if(validationAwsNetworkInfo()){
			$.ajax({
				type : "PUT",
				url : "/bootstrap/awsNetwork",
				contentType : "application/json",
				async : true,
				data : JSON.stringify(networkInfo), 
				success : function(data, status) {
					 awsResourcePopup();
				},
				error : function( e, status ) {
					w2alert("네트워크 설정 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
				}
			});
		}
		else{
			w2alert("필드값을 확인하세요.");
		}
	}
}

function validationAwsNetworkInfo(){
	var checkValidation = true;
	var emptyFields = new Array();
		
	if( checkEmpty( $(".w2ui-msg-body input[name='subnetId']").val() ) ){
		emptyFields.push({name:"subnetId", label:"SUBNET ID"});
		checkValidation = (checkValidation) ? false:false; 
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='privateStaticIp']").val() ) ){
		emptyFields.push({name:"privateStaticIp", label:"PRIVATE STATIC IP"});
		checkValidation = (checkValidation) ? false:false;
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='publicStaticIp']").val() ) ){
		emptyFields.push({name:"publicStaticIp", label:"PUBLIC STATIC IP"});
		checkValidation = (checkValidation) ? false:false;
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='subnetRange']").val() ) ){
		emptyFields.push({name:"subnetRange", label:"SUBNET RANGE"});
		checkValidation = (checkValidation) ? false:false;
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='subnetGateway']").val() ) ){
		emptyFields.push({name:"subnetGateway", label:"SUBNET GATEWAY"});
		checkValidation = (checkValidation) ? false:false;
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='subnetDns']").val() ) ){
		emptyFields.push({name:"subnetDns", label:"SUBNET DNS"});
		checkValidation = (checkValidation) ? false:false;
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='ntp']").val() ) ){
		emptyFields.push({name:"ntp", label:"NTP"});
		checkValidation = (checkValidation) ? false:false;
	}
	
	errFieldMessage(emptyFields);
	
	return checkValidation;
}

function awsResourcePopup(){
	console.log("resorce POPUP!!");
	$("#resourceSettingInfoDiv").w2popup({
		width : 670,
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

function getStemcellList(){
	$.ajax({
		type : "GET",
		url : "/deploy/localStemcells",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		success : function(data, status) {
			$('#w2ui-popup input[type=list]').w2field('list', { items: data.records , maxDropHeight:300, width:500});
			setReourceData();
		},
		error : function( e, status ) {
			w2alert("스템셀 목록을 가져오는데 실패하였습니다.", "BOOTSTRAP 설치");
		}
	});
}

function setReourceData(){
	if(resourceInfo != ""){
		$(".w2ui-msg-body input[name='stemcell']").data('selected', {text:resourceInfo.stemcell});
		
		$(".w2ui-msg-body input[name='cloudInstanceType']").val(resourceInfo.cloudInstanceType);
		$(".w2ui-msg-body input[name='boshPassword']").val(resourceInfo.boshPassword);
	}
}

function saveResourceInfo(type){
	if(bootstrapId == ""){ w2alert("BOOTSTRAP ID가 존재하지 않습니다."); return;}
	
	resourceInfo = {
			id				: bootstrapId,
			stemcell	: $(".w2ui-msg-body input[name='stemcell']").val(),
			cloudInstanceType	: $(".w2ui-msg-body input[name='cloudInstanceType']").val(),
			boshPassword			: $(".w2ui-msg-body input[name='boshPassword']").val()
	}
	
	if( type == 'before') {
		awsNetworkPopup();
		return;
	}else {
		if(validationAwsResourceInfo()){
			$.ajax({
				type : "PUT",
				url : "/bootstrap/awsResource",
				contentType : "application/json",
				//dataType: "json",
				async : true,
				data : JSON.stringify(resourceInfo), 
				success : function(data, status) {
					if( data){
						deployFileName = data.deploymentFile;	
					}
					deployPopup();				
				},
				error : function( e, status ) {
					w2alert("리소스 설정 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
				}
			});
		}
		else{
			w2alert("필드값을 확인해 주세요.");
		}
	}
}

function validationAwsResourceInfo(){
	var checkValidation = true;
	var emptyFields = new Array();
		
	if( checkEmpty( $(".w2ui-msg-body input[name='stemcell']").val() ) ){
		emptyFields.push({name:"stemcell", label:"STEMCELL"});
		checkValidation = (checkValidation) ? false:false; 
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='cloudInstanceType']").val() ) ){
		emptyFields.push({name:"cloudInstanceType", label:"CLOUD INSTANCE TYPE"});
		checkValidation = (checkValidation) ? false:false;
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='boshPassword']").val() ) ){
		emptyFields.push({name:"boshPassword", label:"BOSH PASSWORD"});
		checkValidation = (checkValidation) ? false:false;
	}
	
	errFieldMessage(emptyFields);
	
	return checkValidation;
}

function deployPopup(){
	var deployDiv = (iaas == "AWS") ? $("#deployManifestDiv") : $("#osDeployManifestDiv");
	deployDiv.w2popup({
		width 	: 670,
		height 	: 500,
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
	var url = (iaas == "AWS") ? "/bootstrap/getBootstrapDeployInfo": "/bootstrap/getBootstrapDeployInfo";
	$.ajax({
		type : "POST",
		url : "/bootstrap/getBootstrapDeployInfo",
		contentType : "application/json",
		async : true,
		data : JSON.stringify({deploymentFile:deployFileName}),
		success : function(data, status) {
			if(status == "success"){
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

function confirmDeploy(type){
	//Deploy 단에서 저장할 데이터가 있는지 확인 필요
	//Confirm 설치하시겠습니까?
	if(type == 'after'){		
		w2confirm({
			msg			: "설치하시겠습니까?",
			title		: w2utils.lang('BOOTSTRAP 설치'),
			yes_text	: "예",
			no_text		: "아니오",
			yes_callBack: installPopup
			
		});
	}
	else{
		 if( iaas=="AWS" ) awsResourcePopup();
		 else osResourceInfoPopup();
	}
}

var bootstrapInstallSocket = null;
function installPopup(){
	var installDiv = (iaas == 'AWS') ? $("#installDiv") : $("#osInstallDiv");
	var message = "BOOTSTRAP(배포명:" + deployFileName +  ") ";
	
	var requestParameter = {
			id : bootstrapId,
			iaas: iaas
	};
	
	installDiv.w2popup({
		width : 670,
		height : 490,
		modal	: true,
		showMax : true,
		onOpen : function(event){
			event.onComplete = function(){
				if(bootstrapInstallSocket != null) bootstrapInstallSocket = null;
				if(installClient != null) installClient = null;
				bootstrapInstallSocket = new SockJS('/bootstrapInstall');
				installClient = Stomp.over(bootstrapInstallSocket);
				
				installClient.connect({}, function(frame) {
					console.log('Connected Frame : ' + frame);
			        installClient.subscribe('/bootstrap/bootstrapInstall', function(data){
						var installLogs = $(".w2ui-msg-body #installLogs");
			        	
						installLogs.append(data.body+ "\n").scrollTop( installLogs[0].scrollHeight );
			        	/* var response = JSON.parse(data.body);
			        	
			        	if ( response.messages != null ) {
					       	for ( var i=0; i < response.messages.length; i++) {
					        	installLogs.append(response.messages[i] + "\n").scrollTop( installLogs[0].scrollHeight );
					       	}
					       	
					       	if ( response.state.toLowerCase() != "started" ) {
					            if ( response.state.toLowerCase() == "done" )	message = message + " 설치가 완료되었습니다."; 
					    		if ( response.state.toLowerCase() == "error" ) message = message + " 설치 중 오류가 발생하였습니다.";
					    		if ( response.state.toLowerCase() == "cancelled" ) message = message + " 설치 중 취소되었습니다.";
					    			
					    		installClient.disconnect();
								w2alert(message, "BOOTSRAP 설치");
					       	}
			        	} */
			        });
			        installClient.send('/send/bootstrapInstall', {}, JSON.stringify(requestParameter));
			    });
			}
		},
		onClose : initSetting
	});
}

//팝업창 닫을 경우
function initSetting(){
	iaas = "";
	bootstrapId= "";
	awsInfo = "";
	networkInfo = "";
	resourceInfo = "";
	deployInfo = "";
	deployFileName = "";
	installClient = "";
	deleteClient = "";
}

function uploadStemcell(){
	$("#stemcellUpload").click();
}

function popupComplete(){
	//params init
	initSetting();
	//popup.close
	w2popup.close();
	//grid Reload
	gridReload();
	if(installClient != "" || installClient != null ){
		installClient.disconnect();
		/* function(){
			console.log("InstallClient Disconnection!");
			installClient = "";
		}) */
	}
	if(deleteClient != "" || deleteClient != null){
		deleteClient.disconnect();
	}
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
					$(".w2ui-msg-body input[name='deploymentName']").val(osBoshInfo.deploymentName);
					$(".w2ui-msg-body input[name='directorName']").val(osBoshInfo.directorName);
					//$(".w2ui-msg-body input[name='boshRelease']").val(osBoshInfo.boshRelease);
					//$(".w2ui-msg-body input[name='boshCpiRelease']").val(osBoshInfo.boshCpiRelease);
				}
				getLocalBoshList();
				getLocalBoshCpiList();
			}
		}
	});
}

//Openstack Bosh Info Save
function saveOsBoshInfo(type){
	osBoshInfo = {
			id				: bootstrapId,
			iaas 			: iaas,
			deploymentName	: $(".w2ui-msg-body input[name=deploymentName]").val(),
			directorName	: $(".w2ui-msg-body input[name=directorName]").val(),		
			boshRelease		: $(".w2ui-msg-body input[name=boshRelease]").val(),
			boshCpiRelease	: $(".w2ui-msg-body input[name=boshCpiRelease]").val(),
	}
	
	if(type == 'before'){
		openstackPopup();
		return;
	}
	else{
		if(validationOsBoshInfo()){
			//osBOshInfo SAVE
			$.ajax({
				type : "PUT",
				url : "/bootstrap/setOsBoshInfo",
				contentType : "application/json",
				async : true,
				data : JSON.stringify(osBoshInfo),
				success : function(data, status) {
					osNetworkInfoPopup();					
				},
				error : function( e, status ) {
					w2alert("오프느택 기본정보 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
				}
			});
		}
	}
}

function validationOsBoshInfo(){
	var checkValidation = true;
	var emptyFields = new Array();
		
	if( checkEmpty( $(".w2ui-msg-body input[name='deploymentName']").val() ) ){
		emptyFields.push({name:"authUrl", label:"배포명"});
		checkValidation = (checkValidation) ? false:false; 
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='directorName']").val() ) ){
		emptyFields.push({name:"directorName", label:"디렉터 명"});
		checkValidation = (checkValidation) ? false:false;
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='boshRelease']").val() ) ){
		emptyFields.push({name:"boshRelease", label:"BOSH 릴리즈"});
		checkValidation = (checkValidation) ? false:false;
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='boshCpiRelease']").val() ) ){
		emptyFields.push({name:"boshCpiRelease", label:"BOSH CPI 릴리즈"});
		checkValidation = (checkValidation) ? false:false;
	}
	
	errFieldMessage(emptyFields);
	
	return checkValidation;
}

// Openstack Info Popup
function openstackPopup(){
	$("#openstackInfoDiv").w2popup({
		width : 670,
		height : 530,
		onClose : initSetting,
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
				getKeyPathFileList();
			}
		}
	});	
}

function saveOpenstackInfo(){
	openstackInfo = {
			id						: bootstrapId,
			authUrl					: $(".w2ui-msg-body input[name='authUrl']").val(),
			tenant					: $(".w2ui-msg-body input[name='tenant']").val(),
			userName				: $(".w2ui-msg-body input[name='userName']").val(),
			apiKey					: $(".w2ui-msg-body input[name='apiKey']").val(),
			defaultSecurityGroups	: $(".w2ui-msg-body input[name='defaultSecurityGroups']").val(),
			privateKeyName			: $(".w2ui-msg-body input[name='privateKeyName']").val(),
			privateKeyPath			: $(".w2ui-msg-body input[name='privateKeyPath']").val(),
	}
	
	if(validationOpenstackInfo()){		
		//SAVE
		$.ajax({
			type : "PUT",
			url : "/bootstrap/setOpenstackInfo",
			contentType : "application/json",
			async : true,
			data : JSON.stringify(openstackInfo),
			success : function(data, status) {
				//bootstrapId = data;
				osBoshInfoPop();
			},
			error : function( e, status ) {
				console.log(e + "status ::: " + status);
				w2alert("OPENSTACK 설정 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
			}
		});
	}
	else{
		w2alert("필드값을 확인하세요.");
	}
}

function validationOpenstackInfo(){
	var checkValidation = true;
	var emptyFields = new Array();
		
	if( checkEmpty( $(".w2ui-msg-body input[name='authUrl']").val() ) ){
		emptyFields.push({name:"authUrl", label:"AUTH URL"});
		checkValidation = (checkValidation) ? false:false; 
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='tenant']").val() ) ){
		emptyFields.push({name:"tenant", label:"TENANT"});
		checkValidation = (checkValidation) ? false:false;
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='userName']").val() ) ){
		emptyFields.push({name:"userName", label:"USER NAME"});
		checkValidation = (checkValidation) ? false:false;
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='apiKey']").val() ) ){
		emptyFields.push({name:"apiKey", label:"API KEY"});
		checkValidation = (checkValidation) ? false:false;
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='defaultSecurityGroups']").val() ) ){
		emptyFields.push({name:"defaultSecurityGroups", label:"DEFAULT SECURITY GROUPS"});
		checkValidation = (checkValidation) ? false:false;
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='privateKeyName']").val() ) ){
		emptyFields.push({name:"privateKeyName", label:"Private Key Name"});
		checkValidation = (checkValidation) ? false:false;
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='privateKeyPath']").val() ) ){
		emptyFields.push({name:"privateKeyPath", label:"Private Key Path"});
		checkValidation = (checkValidation) ? false:false;
	}
	
	errFieldMessage(emptyFields);
	
	return checkValidation;
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
				if(networkInfo != "" && iaas == "OPENSTACK"){
					$(".w2ui-msg-body input[name='subnetId']").val(networkInfo.subnetId);
					$(".w2ui-msg-body input[name='privateStaticIp']").val(networkInfo.privateStaticIp);
					$(".w2ui-msg-body input[name='publicStaticIp']").val(networkInfo.publicStaticIp);
					
					$(".w2ui-msg-body input[name='subnetRange']").val(networkInfo.subnetRange);
					$(".w2ui-msg-body input[name='subnetGateway']").val(networkInfo.subnetGateway);
					$(".w2ui-msg-body input[name='subnetDns']").val(networkInfo.subnetDns);
					$(".w2ui-msg-body input[name='ntp']").val(networkInfo.ntp);
				}				
			}
		}
	});	
}

function saveOsNetworkInfo(type){
	networkInfo = {
			id				: bootstrapId,
			subnetId		: $(".w2ui-msg-body input[name='subnetId']").val(),
			privateStaticIp	: $(".w2ui-msg-body input[name='privateStaticIp']").val(),
			publicStaticIp	: $(".w2ui-msg-body input[name='publicStaticIp']").val(),
			subnetRange		: $(".w2ui-msg-body input[name='subnetRange']").val(),
			subnetGateway	: $(".w2ui-msg-body input[name='subnetGateway']").val(),
			subnetDns		: $(".w2ui-msg-body input[name='subnetDns']").val(),
			ntp				: $(".w2ui-msg-body input[name='ntp']").val()
	}
	if( type == "before") {
		osBoshInfoPop();
		return;
	}
	else{
				
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
				w2alert("OPENSTACK Network 정보 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
			}
		});
	}
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
				getStemcellList();
			}
		}
	});	
}

function saveOsResourceInfo(type){
	resourceInfo = {
			id					: bootstrapId,
			stemcellUrl			: $(".w2ui-msg-body input[name='stemcellUrl']").val(),
			envPassword			: $(".w2ui-msg-body input[name='envPassword']").val(),
			cloudInstanceType	: $(".w2ui-msg-body input[name='cloudInstanceType']").val()
	}
	
	if( type == "before"){
		osNetworkInfoPopup();
		return;
	}
	else{
		if(validationOsResourceInfo()){
			$.ajax({
				type : "PUT",
				url : "/bootstrap/setOsResourceInfo",
				contentType : "application/json",
				async : true,
				data : JSON.stringify(resourceInfo),
				success : function(data, status) {
					deployFileName = data.content.deploymentFile;
					osDeployPopup();
				},
				error : function( e, status ) {
					w2alert("OPENSTACK Resource 설정 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
				}
			});
		}
	}
}

function validationOsResourceInfo(){
	var checkValidation = true;
	var emptyFields = new Array();
		
	if( checkEmpty( $(".w2ui-msg-body input[name='stemcell']").val() ) ){
		emptyFields.push({name:"stemcell", label:"STEMCELL"});
		checkValidation = (checkValidation) ? false:false; 
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='cloudInstanceType']").val() ) ){
		emptyFields.push({name:"cloudInstanceType", label:"CLOUD INSTANCE TYPE"});
		checkValidation = (checkValidation) ? false:false;
	}
	if( checkEmpty( $(".w2ui-msg-body input[name='boshPassword']").val() ) ){
		emptyFields.push({name:"boshPassword", label:"BOSH PASSWORD"});
		checkValidation = (checkValidation) ? false:false;
	}
	
	errFieldMessage(emptyFields);
	
	return checkValidation;
}

function osDeployPopup(){
	$("#osDeployManifestDiv").w2popup({
		width 	: 670,
		height 	: 470,
		modal	: true,
		showMax : true,
		onClose : function(event){
			event.onComplete = function(){
				initSetting();
			}
		},
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
					<input type="radio" name="iaas" id="type1" value="AWS" checked="checked" tabindex="1" />
					&nbsp;AWS
				</label>
				<label style="width: 130px;margin-left:50px;">
					<input type="radio" name="iaas" id="type2" value="OPENSTACK" tabindex="2" />
					&nbsp;OPENSTACK
				</label>
			</div>
		</div>
	</div>

	<!-- AWS  설정 DIV -->
	<div id="awsDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%;padding:15px 5px 0 5px;">
			<div style="width:100%;height:60px;paddinf:0 3%;display: inline-block;">
	            <ul class="progressStep_6" >
		            <li class="active">AWS 정보</li>
		            <li class="before">기본 정보</li>
		            <li class="before">네트워크 정보</li>
		            <li class="before">리소스 정보</li>
		            <li class="before">배포 파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div style="width:100%;height:15px;paddinf-left:1.5%;display: inline-block;">▶ AWS 정보</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
		    	<form id="awsForm" data-toggle="validator" >
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">AWS 키(access-key-id)</label>
			            <div>
			                <input name="accessKeyId" type="text"  style="float:left;width:60%;" tabindex="1" placeholder="AKIAIGLIMLV5...."/>
			                <div class="isMessage"></div>			                
			            </div>
			        </div>
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">AWS 비밀번호(secret-access-key)</label>
			            <div>
			                <input name="secretAccessId" type="text"  style="float:left;width:60%;" tabindex="2" placeholder="******"/>
			                <div class="isMessage"></div>
			            </div>
			        </div>
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">시큐리티 그룹명</label>
			            <div>
			                <input name="defaultSecurityGroups" type="text"  style="float:left;width:60%;" tabindex="3" placeholder="bosh"/>
			                <div class="isMessage"></div>
			            </div>
			        </div>
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">REGION</label>
			            <div>
			                <input name="region" type="text"  style="float:left;width:60%;" tabindex="3" placeholder="bosh"/>
			                <div class="isMessage"></div>
			            </div>
			        </div>
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">AVAILABILITY ZONE</label>
			            <div>
			                <input name="availabilityZone" type="text" style="display: inline-block;float:left;width:60%;" tabindex="3" placeholder="bosh"/>
			                <div class="isMessage"></div>
			            </div>
			        </div>
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">PRIVATE-KEY-NAME</label>
			            <div>
			                <input name="privateKeyName" type="text"  style="display: inline-block;float:left;width:60%;" tabindex="3" placeholder="bosh"/>
			                <div class="isMessage"></div>
			            </div>
			        </div>
			        
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">Private Key File</label>
		                <div >
	  						<span onclick="changeKeyPathType('file');" style="width:30%;"><label><input type="radio" name="keyPathType" value="file" tabindex="6"/>&nbsp;파일업로드</label></span>
							&nbsp;&nbsp;
							<span onclick="changeKeyPathType('list');" style="width:30%;"><label><input type="radio" name="keyPathType" value="list" tabindex="5"/>&nbsp;목록에서 선택</label></span>
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
		        <button class="btn" style="float: right;padding-right:15%" onclick="saveAwsInfo();" tabindex="8">다음>></button>
		    </div>
		</div>
	</div>
	
	<div id="awsDefaultDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%;padding:15px 5px 0 5px;">
			<div style="width:100%;height:60px;paddinf:0 3%;display: inline-block;">
	            <ul class="progressStep_6" >
		            <li class="pass">AWS 정보</li>
		            <li class="active">기본 정보</li>
		            <li class="before">네트워크 정보</li>
		            <li class="before">리소스 정보</li>
		            <li class="before">배포 파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div style="width:100%;height:15px;paddinf-left:1.5%;display: inline-block;">▶ 기본 정보</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
		    	<form id="awsDefaultForm" data-toggle="validator" >
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">배포명</label>
			            <div>
			                <input name="deploymentName" type="text"  style="float:left;width:60%;" tabindex="1" placeholder="AKIAIGLIMLV5...."/>
			                <div class="isMessage"></div>
			            </div>
			        </div>
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">디렉터 명</label>
			            <div>
			                <input name="directorName" type="text"  style="float:left;width:60%;" tabindex="2" placeholder="******"/>
			                <div class="isMessage"></div>
			            </div>
			        </div>
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">BOSH RELEASE</label>
			            <div>
			                <input name="boshRelease" type="list"  style="float:left;width:60%;" tabindex="3" placeholder="bosh"/>
			                <!-- <div class="isMessage"></div> -->
			            </div>
			        </div>
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">BOSH CPI RELEASE</label>
			            <div>
			                <input name="boshCpiRelease" type="list"  style="float:left;width:60%;" tabindex="4" placeholder="bosh"/>
			                <!-- <div class="isMessage"></div> -->
			            </div>
			        </div>			        
		        </form>
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
		    	<button class="btn" style="float: left;" onclick="saveAwsDefaultInfo('before');" tabindex="5">이전</button>
		        <button class="btn" onclick="w2popup.close();" tabindex="6">취소</button>
		        <button class="btn" style="float: right;padding-right:15%" onclick="saveAwsDefaultInfo('after');" tabindex="7">다음>></button>
		    </div>
		</div>
	</div>
	
	<!-- 네트워크  설정 DIV -->
	<div id="awsNetworkInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%;padding:15px 5px 0 5px;">
			<div style="margin-left:3%;">
	            <ul class="progressStep_6">
		            <li class="pass">AWS 정보</li>
		            <li class="pass">기본 정보</li>
		            <li class="active">네트워크 정보</li>
		            <li class="before">리소스 정보</li>
		            <li class="before">배포 파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" class="cont_title" style="margin-left:1.5%;">▶ 네트워크 정보</div>
			<div class="w2ui-page page-0" style="padding-left: 5%;">
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">SUBNET ID</label>
					<div>
						<input name="subnetId" type="text"  style="float:left;width:330px;" tabindex="1" placeholder="subnet-e8d03a9e"/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">PRIVATE STATIC IP</label>
					<div>
						<input name="privateStaticIp" type="text"  style="float:left;width:330px;" tabindex="2" placeholder="10.0.0.110"/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">PUBLIC STATIC IP</label>
					<div>
						<input name="publicStaticIp" type="text"  style="float:left;width:330px;" tabindex="3" placeholder="52.23.2.85"/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">SUBNET RANGE</label>
					<div>
						<input name="subnetRange" type="text"  style="float:left;width:330px;" tabindex="3" placeholder="예) 52.23.2.85/24"/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">SUBNET GATEWAY</label>
					<div>
						<input name="subnetGateway" type="text"  style="float:left;width:330px;" tabindex="6" placeholder="10.0.0.1"/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">SUBNET DNS</label>
					<div>
						<input name="subnetDns" type="text"  style="float:left;width:330px;" tabindex="7" placeholder="10.0.0.2"/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">NTP</label>
					<div>
						<input name="ntp" type="text"  style="float:left;width:330px;" tabindex="7" placeholder="10.0.0.2"/>
						<div class="isMessage"></div>
					</div>
				</div>
			</div>
			<br />
			<div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: left;" onclick="saveAwsNetworkInfo('before');" tabindex="7">이전</button>
				<button class="btn" onclick="popupComplete();" tabindex="8">취소</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveAwsNetworkInfo('after');" tabindex="9">다음>></button>
			</div>
		</div>
	</div>
	
	<!-- Resource  설정 DIV -->
	<div id="awsResourceInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%;padding:15px 5px 0 5px;">
			<div style="margin-left:3%;">
	            <ul class="progressStep_6">
		            <li class="pass">AWS 정보</li>
		            <li class="pass">기본 정보</li>
		            <li class="pass">네트워크 정보</li>
		            <li class="active">리소스 정보</li>
		            <li class="before">배포 파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" class="cont_title" style="margin-left:1.5%;">▶ 리소스 정보</div>
			<div class="w2ui-page page-0" style="padding-left: 5%;">
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">스템셀</label>
					<div>
						<div>
							<input type="list" name="stemcell" style="float: left;width:330px;margin-top:1.5px;" tabindex="1">
						</div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">인스턴스 유형</label>
					<div>
						<input name="cloudInstanceType" type="text"  style="float:left;width:330px;" tabindex="2" placeholder="m3.large"/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">BOSH PASSWORD</label>
					<div>
						<input name="boshPassword" type="text"  style="float:left;width:330px;" tabindex="3" placeholder="us-east-1"/>
						<div class="isMessage"></div>
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
			<div style="height:60px;margin:0 15px;">
	            <ul class="progressStep_6">
		            <li class="pass">AWS 정보</li>
		            <li class="pass">기본 정보</li>
		            <li class="pass">네트워크 정보</li>
		            <li class="pass">리소스 정보</li>
		            <li class="active">배포 파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div style="width:95%;height:84%;float: left;display: inline-block;">
				<textarea id="deployInfo" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:2%" readonly="readonly"></textarea>
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
			<div style="height:60px;margin:0 15px;">
	            <ul class="progressStep_6">
		            <li class="pass">AWS 정보</li>
		            <li class="pass">기본 정보</li>
		            <li class="pass">네트워크 정보</li>
		            <li class="pass">리소스 정보</li>
		            <li class="pass">배포 파일 정보</li>
		            <li class="active">설치</li>
	            </ul>
	        </div>
			<div style="height:84%;">
				<textarea id="installLogs" style="width:97%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:1%" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
				<!-- 설치 실패 시 -->
				<button class="btn" style="float: left;" onclick="deployPopup();">이전</button>
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
		            <li class="pass">오픈스텍 설정</li>
		            <li class="active">기본 설정</li>
		            <li class="before">네트워크 정보</li>
		            <li class="before">리소스 정보</li>		            
		            <li class="before">배포 파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" class="cont_title" style="margin-left:1.5%;">▶ 기본 정보</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
		    	<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">배포명</label>
					<div>
						<input name="deploymentName" type="text"  style="float:left;width:60%;" tabindex="1"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">디렉터 명</label>
					<div>
						<input name="directorName" type="text"  style="float:left;width:60%;" tabindex="1"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">BOSH 릴리즈</label>
					<div>
						<input name="boshRelease" type="list"  style="float:left;width:60%;" tabindex="2"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">BOSH CPI 릴리즈</label>
					<div>
						<input name="boshCpiRelease" type="list"  style="float:left;width:60%;" tabindex="3"/>
					</div>
				</div>
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
		    	<button class="btn" style="float: left;" onclick="saveOsBoshInfo('before');">이전</button>
		        <button class="btn" onclick="popupComplete();">취소</button>
		        <button class="btn" style="float: right;padding-right:15%" onclick="saveOsBoshInfo('after');" tabindex="9">다음>></button>
		    </div>
		</div>
	</div>
	
	<div id="openstackInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%;padding:15px 5px 0 5px;">
			<div style="margin-left:2%;">
	            <ul class="progressStep_6" >
		            <li class="active">오픈스텍 설정</li>
		            <li class="before">기본 설정</li>
		            <li class="before">네트워크 정보</li>
		            <li class="before">리소스 정보</li>		            
		            <li class="before">배포 파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" class="cont_title" style="margin-left:1.5%;">▶ OPENSTACK 정보</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">Auth Url</label>
					<div>
						<input name="authUrl" type="text"  style="float:left;width:60%;" tabindex="4"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">Tenant</label>
					<div>
						<input name="tenant" type="text"  style="float:left;width:60%;" tabindex="5"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">USER NAME</label>
					<div>
						<input name="userName" type="text"  style="float:left;width:60%;" placeholder="mybosh"/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">API KEY</label>
					<div>
						<input name="apiKey" type="text"  style="float:left;width:60%;"/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">DEFAULT SECURITY GROUPS</label>
					<div>
						<input name="defaultSecurityGroups" type="text"  style="float:left;width:60%;"/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">PRIVATE KEY NAME</label>
					<div>
						<input name="privateKeyName" type="text"  style="float:left;width:60%;"/>
						<div class="isMessage"></div>
					</div>
				</div>
				<!-- privateKeyPath -->
				<div class="w2ui-field">
		            <label style="text-align: left;width:40%;font-size:11px;">Private Key File</label>
	                <div >
  						<span onclick="changeKeyPathType('file');" style="width:30%;"><label><input type="radio" name="keyPathType" value="file" tabindex="6"/>&nbsp;파일업로드</label></span>
						&nbsp;&nbsp;
						<span onclick="changeKeyPathType('list');" style="width:30%;"><label><input type="radio" name="keyPathType" value="list" tabindex="5"/>&nbsp;목록에서 선택</label></span>
					</div>
		        </div>
		        <div class="w2ui-field">			         	
	                <input name="privateKeyPath" type="text" style="width:200px;" hidden="true" onclick="openBrowse();"/>
		            <label style="text-align: left;width:40%;font-size:11px;" class="control-label"></label>
					<div id="keyPathDiv" ></div>
		        </div>
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: left;" onclick="popupComplete();">취소</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveOpenstackInfo();">다음>></button>
		    </div>
		</div>
	</div>
	
	<div id="osNetworkInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%;padding:15px 5px 0 5px;">
			<div style="margin-left:2%;">
	            <ul class="progressStep_6" >
		            <li class="pass">오픈스텍 설정</li>
		            <li class="pass">기본 설정</li>
		            <li class="active">네트워크 정보</li>
		            <li class="before">리소스 정보</li>		            
		            <li class="before">배포 파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" class="cont_title" style="margin-left:1.5%;">▶ 네트워크 정보</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
		    	<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">SUBNET ID</label>
					<div>
						<input name="subnetId" type="text"  style="float:left;width:330px;" tabindex="1" placeholder="subnet-e8d03a9e"/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">PRIVATE STATIC IP</label>
					<div>
						<input name="privateStaticIp" type="text"  style="float:left;width:330px;" tabindex="2" placeholder="10.0.0.110"/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">PUBLIC STATIC IP</label>
					<div>
						<input name="publicStaticIp" type="text"  style="float:left;width:330px;" tabindex="3" placeholder="52.23.2.85"/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">SUBNET RANGE</label>
					<div>
						<input name="subnetRange" type="text"  style="float:left;width:330px;" tabindex="3" placeholder="예) 52.23.2.85/24"/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">SUBNET GATEWAY</label>
					<div>
						<input name="subnetGateway" type="text"  style="float:left;width:330px;" tabindex="6" placeholder="10.0.0.1"/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">SUBNET DNS</label>
					<div>
						<input name="subnetDns" type="text"  style="float:left;width:330px;" tabindex="7" placeholder="10.0.0.2"/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">NTP</label>
					<div>
						<input name="ntp" type="text"  style="float:left;width:330px;" tabindex="7" placeholder="10.0.0.2"/>
						<div class="isMessage"></div>
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
		            <li class="pass">오픈스텍 설정</li>
		            <li class="pass">기본 설정</li>
		            <li class="pass">네트워크 정보</li>
		            <li class="active">리소스 정보</li>		            
		            <li class="before">배포 파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" class="cont_title" style="margin-left:1.5%;">▶ 리소스 정보</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
		    	<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">스템셀</label>
					<div>
						<div>
							<input type="list" name="stemcell" style="float: left;width:330px;margin-top:1.5px;" tabindex="1">
						</div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">인스턴스 유형</label>
					<div>
						<input name="cloudInstanceType" type="text"  style="float:left;width:330px;" tabindex="2" placeholder="m3.large"/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">BOSH PASSWORD</label>
					<div>
						<input name="boshPassword" type="text"  style="float:left;width:330px;" tabindex="3" placeholder="us-east-1"/>
						<div class="isMessage"></div>
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
			<div style="height:60px;margin:0 15px;">
	            <ul class="progressStep_6" >
		            <li class="pass">오픈스텍 설정</li>
		            <li class="pass">기본 설정</li>
		            <li class="pass">네트워크 정보</li>
		            <li class="pass">리소스 정보</li>		            
		            <li class="active">배포 파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div style="height:84%;">
				<textarea id="deployInfo" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:2%" readonly="readonly"></textarea>
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
			<div style="height:60px;margin:0 15px;">
	            <ul class="progressStep_6" >
		            <li class="pass">오픈스텍 설정</li>
		            <li class="pass">기본 설정</li>
		            <li class="pass">네트워크 정보</li>
		            <li class="pass">리소스 정보</li>		            
		            <li class="pass">배포 파일 정보</li>
		            <li class="active">설치</li>
	            </ul>
	        </div>
			<div style="height:84%;">
				<textarea id="installLogs" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:1%" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
				<!-- 설치 실패 시 -->
				<button class="btn" style="float: left;" onclick="deployPopup();">이전</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="popupComplete();">완료</button>
		</div>		
	</div>	