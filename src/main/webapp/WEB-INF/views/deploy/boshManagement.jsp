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
    background : #CCC;
    color : #FFF;
    left: 5px;
    text-decoration : none;
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
//private var
var iaas = "";
var boshId = "";
var awsInfo = "";
var openstackInfo = "";
var boshInfo = "";
var networkInfo = "";
var resourceInfo = "";
var keyPathFileList = "";
var releases;
var stemcells;

$(function(){
	$('#config_boshGrid').w2grid({
		name: 'config_boshGrid',
		header: '<b>Bosh 목록</b>',
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
	
	$("#installBtn").click(function(){
		selectIaas();
	});
	
	//Bosh 수정
	$("#modifyBtn").click(function(){
		if($("#modifyBtn").attr('disabled') == "disabled") return;
		w2confirm({
			title 	: "BOSH 설치",
			msg		: "BOSH 를 수정하시겠습니까?",
			yes_text: "확인",
			yes_callBack : function(event){
				var selected = w2ui['config_boshGrid'].getSelection();
				console.log("modify Click!!!");
				if( selected.length == 0 ){
					w2alert("선택된 정보가 없습니다.", "BOSH 설치");
					return;
				}
				else{
					var record = w2ui['config_boshGrid'].get(selected);
					if(record.iaas == "AWS") getBoshAwsData(record);
					else getBoshOpenstackData(record);
				}
			},
			no_text : "취소"
		});
 	});
 	
 	//Bosh 삭제
	$("#deleteBtn").click(function(){
		if($("#deleteBtn").attr('disabled') == "disabled") return;
		w2confirm({
			title 	: "BOSH 삭제",
			msg		: "BOSH 를 삭제하시겠습니까?",
			yes_text: "확인",
			yes_callBack : function(event){
				//event.onComplete= function(){
					//ajax data call
					var selected = w2ui['config_boshGrid'].getSelection();
					console.log("Delete Click!!!");
					if( selected.length == 0 ){
						w2alert("선택된 정보가 없습니다.", "BOSH 삭제");
						return;
					}
					else{
						var record = w2ui['config_boshGrid'].get(selected);
						console.log(record.iaas);
						if(record.iaas == "AWS") deleteBoshAwsPop(record);
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
	w2ui['config_boshGrid'].load("<c:url value='/deploy/boshList'/>",
			function (){
				doButtonStyle();
			});
}
function selectIaas(){
	//Bootstrap 
	w2confirm({
		width 			: 500,
		height 			: 180,
		title 			: '<b>BOSH 설치</b>',
		msg 			: $("#bootSelectBody").html(),
		modal			: true,
		yes_text 		: "확인",
		no_text 		: "취소",
		yes_callBack 	: function(){
			//alert($("input[name='structureType']").val());
			structureType = $(".w2ui-msg-body input:radio[name='structureType']:checked").val();
			if(structureType){
				iaas = structureType;
				console.log("iaas ::: " + structureType);
				getReleaseVersionList();
				
				if( structureType == "AWS") awsPopup();
				else osBoshInfoPopup();				
			}
			else{
				w2alert("설치할 Infrastructure 을 선택하세요");
			}
		}
	});
}

//MODIFY
function getBoshAwsData(record){
	var url = "/bosh/aws/"+record.id;
	$.ajax({
		type : "GET",
		url : url,
		contentType : "application/json",
		success : function(data, status) {
			console.log("== /bosh/aws/ RESULT :: ");
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
			w2alert(errorResult.message, "BOSH 설치");
		}
	});	
}

function getBoshOpenstackData(record){
	var url = "/bosh/openstack/"+record.id;
	$.ajax({
		type : "GET",
		url : url,
		contentType : "application/json",
		success : function(data, status) {
			console.log("== /bosh/openstack/ RESULT :: ");
			if ( data ){
				initSetting();
				console.log("=== Content ::: " + data.contents);
				settingOpenstackData(data.contents);
			}
		},
		error : function(request, status, error) {
			var errorResult = JSON.parse(request.responseText);
			console.log("console log ::: " +errorResult.message);
			w2alert(errorResult.message, "BOSH 설치");
		}
	});
}

//DELETE
function deleteBoshAwsPop(record){
	var body = '<textarea name="deleteLogs" style="width:93%;height:93%;overflow-y:visible;resize:none;background-color: #FFF;margin:2%" readonly="readonly"></textarea>';
	//body += '</div>';
	
	w2popup.open({
		width : 610,
		height : 400,
		title : "<b>Bosh 삭제</b>",
		body  : body,
		buttons : '<button class="btn" style="float: right; padding-right: 15%;" onclick="popupComplete();;">완료</button>',
		showMax : true,
		onOpen : function(event){
			event.onComplete = function(){
			console.log("Delete Pop");
				var socket = new SockJS('/boshDelete');
				deleteClient = Stomp.over(socket); 
				deleteClient.connect({}, function(frame) {
					console.log('Connected Frame : ' + frame);
					deleteClient.subscribe('/bosh/boshDelete', function(data){
				        console.log('Connected: Data : ' + data);
				        var deleteLogs = $(".w2ui-msg-body textarea[name=deleteLogs]");
			        	deleteLogs.append(data.body + "\n").scrollTop( deleteLogs[0].scrollHeight );
			        	
			        	if( data == "complete"){
			        		deleteClient.disconnect(function(){
			        			console.log("disconnect");
			        		});//callback
			        	}
			        });
					deleteClient.send('/send/boshDelete', {}, JSON.stringify({iaas:record.iaas, id:record.id}));
			    });
			}
		},
		onClose : function (event){
			event.onComplete= function(){
				/* $("body ").remove("#deleteLogs");
				deleteLogs.text(""); */
				$("textarea").text("");
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
//Aws Popup
function awsPopup(){
	$("#awsInfoDiv").w2popup({
		width 	: 670,
		height	: 500,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
				if(awsInfo != ""){					
					$(".w2ui-msg-body input[name='dnsRecursor']").val(awsInfo.dnsRecursor);
					$(".w2ui-msg-body input[name='accessKeyId']").val(awsInfo.accessKeyId);
					$(".w2ui-msg-body input[name='secretAccessKey']").val(awsInfo.secretAccessKey);
					$(".w2ui-msg-body input[name='defaultKeyName']").val(awsInfo.defaultKeyName);
					$(".w2ui-msg-body input[name='defaultSecurityGroups']").val(awsInfo.defaultSecurityGroups);
					$(".w2ui-msg-body input[name='region']").val(awsInfo.region);
					$(".w2ui-msg-body input[name='privateKeyPath']").val(awsInfo.privateKeyPath);
					
				}
				getKeyPathFileList();
			}
		},
		onClose : initSetting
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
			$('.w2ui-msg-body input:radio[name=keyPathType]:input[value="list"]').attr("checked", true);
			changeKeyPathType("list");
		},
		error : function( e, status ) {
			w2alert("KeyPath File 목록을 가져오는데 실패하였습니다.", "BOSH 설치");
		}
	});
}

function changeKeyPathType(type){
	console.log(type);
	var keyPathDiv = $('.w2ui-msg-body #keyPathDiv');
	var fileUploadInput = '<span><input type="file" name="keyPathFile" style="width:200px;" onchange="setPrivateKeyPathFileName(this);" hidden="true"/>';
	fileUploadInput += '<input type="text" id="keyPathFileName" style="width:200px;" readonly  onClick="openBrowse();" placeholder="Key File을 선택해주세요."/>';
	fileUploadInput += '<a href="#" id="browse" onClick="openBrowse();">Browse </a></span>';
	var selectInput = '<input type="list" name="keyPathList" style="float: left;width:280px;" onchange="setPrivateKeyPath(this.value);"/>';
	
	if(type == "list") {
		keyPathDiv.html(selectInput);
		$('#w2ui-popup #keyPathDiv input[type=list]').w2field('list', { items: keyPathFileList , maxDropHeight:200, width:250});
		console.log("======== : " + awsInfo.privateKeyPath);
		if(awsInfo.privateKeyPath) $(".w2ui-msg-body input[name='keyPathList']").data('selected', {text:awsInfo.privateKeyPath});
	}else{		
		keyPathDiv.html(fileUploadInput);
		$(".w2ui-msg-body input[name='keyPathFile']").hide();		
	}
}

function openBrowse(){
	$(".w2ui-msg-body input[name='keyPathFile']").click();
}

function setPrivateKeyPathFileName(fileInput){
	var file = fileInput.files;
	$(".w2ui-msg-body input[name=privateKeyPath]").val(file[0].name);
	$(".w2ui-msg-body #keyPathFileName").val(file[0].name);
}

//AWS POPUP NEXT BUTTON EVENT
function saveAwsInfo(){
	//AwsInfo Save
	awsInfo = {
			id						: boshId,
			iaas					: "AWS",
			dnsRecursor				: $(".w2ui-msg-body input[name='dnsRecursor']").val(),
			accessKeyId 			: $(".w2ui-msg-body input[name='accessKeyId']").val(),
			secretAccessKey			: $(".w2ui-msg-body input[name='secretAccessKey']").val(),
			defaultKeyName			: $(".w2ui-msg-body input[name='defaultKeyName']").val(),
			defaultSecurityGroups	: $(".w2ui-msg-body input[name='defaultSecurityGroups']").val(),
			region					: $(".w2ui-msg-body input[name='region']").val(),
			privateKeyPath			: $(".w2ui-msg-body input[name='privateKeyPath']").val()
	}
	
	if( $(".w2ui-msg-body input[name='keyPathFile']").val() != null){
		var keyPathFile =  $(".w2ui-msg-body input[name='keyPathFile']").val().split('.').pop().toLowerCase();
		
		if($.inArray(keyPathFile, ['pem']) == -1) {
			w2alert("KeyPath File은 .pem 파일만 등록 가능합니다.", "BOSH 설치");
			return;
		}
	}
	//ajax AwsInfo Save
	$.ajax({
		type : "PUT",
		url : "/bosh/saveAwsInfo",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		data : JSON.stringify(awsInfo), 
		success : function(data, status) {
			boshId = data;
			keyPathFileUpload();
			
		},
		error : function( e, status ) {
			w2alert("AWS 설정 등록에 실패 하였습니다.", "BOSH 설치");
		}
	});
}

function keyPathFileUpload(){
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
			boshInfoPopup();
		},
		error : function( e, status ) {
			w2alert("AWS 설정 등록에 실패 하였습니다.", "BOSH 설치");
		}
	});
}

function settingAWSData(contents){
	boshId = contents.id;
	awsInfo = {
			iaas		 			: "AWS",
			dnsRecursor				: contents.dnsRecursor,
			accessKeyId				: contents.accessKeyId,
			secretAccessKey			: contents.secretAccessKey,
			defaultKeyName			: contents.defaultKeyName,
			defaultSecurityGroups	: contents.defaultSecurityGroups,
			region					: contents.region,
			privateKeyPath			: contents.privateKeyPath
	}
	boshInfo = {
			id				: boshId,
			boshName 		: contents.boshName,
			directorUuid	: contents.directorUuid,
			releaseVersion	: contents.releaseVersion
	}
	networkInfo = {
			id					: boshId,			
			subnetStatic		: contents.subnetStatic,
			subnetRange			: contents.subnetRange,
			subnetGateway		: contents.subnetGateway,
			subnetDns			: contents.subnetDns,
			cloudSubnet			: contents.cloudSubnet,
			cloudSecurityGroups	: contents.cloudSecurityGroups
	}
	resourceInfo = {
			id				: contents.id,
			stemcellName	: contents.stemcellName,
			stemcellVersion	: contents.stemcellVersion,
			boshPassword	: contents.boshPassword
	}
	awsPopup();	
}

function settingOpenstackData(contents){
	boshId = contents.id;
	iaas = "OPENDTACK";
	boshInfo = {
			id				: boshId,
			boshName 		: contents.boshName,
			directorUuid	: contents.directorUuid,
			releaseVersion	: contents.releaseVersion
	}
	
	openstackInfo = {
			id						: boshId,
			directorName			: contents.directorName,
			directorStaticIp		: contents.directorStaticIp,
			dnsRecursor				: contents.dnsRecursor,
			authUrl					: contents.authUrl,
			tenant					: contents.tenant,
			userName				: contents.userName,
			apiKey					: contents.apiKey,
			defaultKeyName			: contents.defaultKeyName,
			defaultSecurityGroups	: contents.defaultSecurityGroups,
			ntp						: contents.ntp
	}
	
	networkInfo = {
			id				: boshId,
			subnetStatic	: contents.subnetStatic,
			subnetRange		: contents.subnetRange,
			subnetGateway	: contents.subnetGateway,
			subnetDns		: contents.subnetDns,
			cloudNetId		: contents.cloudNetId
	}
	
	resourceInfo = {
			id					: boshId,
			stemcellName		: contents.stemcellName,
			stemcellVersion		: contents.stemcellVersion,
			cloudInstanceType	: contents.cloudInstanceType,
			boshPassword		: contents.boshPassword
	}
	osBoshInfoPopup();	
}

function  boshInfoPopup(){
	$("#boshInfoDiv").w2popup({
		width 	: 670,
		height	: 350,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
				if(boshInfo != ""){
					$(".w2ui-msg-body input[name='boshName']").val(boshInfo.boshName);
					$(".w2ui-msg-body input[name='directorUuid']").val(boshInfo.directorUuid);
					$(".w2ui-msg-body input[name='releaseVersion']").val(boshInfo.releaseVersion);
				}
			}
		},
		onClose : initSetting
	});
}

function saveBoshInfo(param){
	console.log("### saveBoshInfo :: " + param)
	boshInfo = {
			id				: boshId,
			boshName 		: $(".w2ui-msg-body input[name='boshName']").val(),
			directorUuid	: $(".w2ui-msg-body input[name='directorUuid']").val(),
			releaseVersion	: $(".w2ui-msg-body input[name='releaseVersion']").val()
	}
	
	if(param == 'before'){
		awsPopup(); return;
	}
		
	//Server send Bosh Info
	$.ajax({
		type : "PUT",
		url : "/bosh/saveAwsBoshInfo",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		data : JSON.stringify(boshInfo), 
		success : function(data, status) {
			networkPopup();
		},
		error : function( e, status ) {
			w2alert("Bosh Info 등록에 실패 하였습니다.", "Bosh 설치");
		}
	});
}

function networkPopup(){
	$("#networkInfoDiv").w2popup({
		width 	: 670,
		height	: 450,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
				if(networkInfo != ""){
					$(".w2ui-msg-body input[name='subnetStatic']").val(networkInfo.subnetStatic);
					$(".w2ui-msg-body input[name='subnetRange']").val(networkInfo.subnetRange);
					$(".w2ui-msg-body input[name='subnetGateway']").val(networkInfo.subnetGateway);
					$(".w2ui-msg-body input[name='subnetDns']").val(networkInfo.subnetDns);
					$(".w2ui-msg-body input[name='cloudSubnet']").val(networkInfo.cloudSubnet);
					$(".w2ui-msg-body input[name='cloudSecurityGroups']").val(networkInfo.cloudSecurityGroups);
				}
			}
		},
		onClose : initSetting
	});
}

function saveNetworkInfo(param){
	networkInfo = {
			id					: boshId,
			subnetStatic		: $(".w2ui-msg-body input[name='subnetStatic']").val(),
			subnetRange			: $(".w2ui-msg-body input[name='subnetRange']").val(),
			subnetGateway		: $(".w2ui-msg-body input[name='subnetGateway']").val(),
			subnetDns			: $(".w2ui-msg-body input[name='subnetDns']").val(),
			cloudSubnet			: $(".w2ui-msg-body input[name='cloudSubnet']").val(),
			cloudSecurityGroups	: $(".w2ui-msg-body input[name='cloudSecurityGroups']").val()
	}
	
	if(param == 'before'){
		boshInfoPopup();
		return;
	}
		
	//Server send Bosh Info
	$.ajax({
		type : "PUT",
		url : "/bosh/saveAwsNetworkInfo",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		data : JSON.stringify(networkInfo), 
		success : function(data, status) {
			resourcePopup();
		},
		error : function( e, status ) {
			w2alert("Bosh Network 등록에 실패 하였습니다.", "Bosh 설치");
		}
	});
	
}

function resourcePopup(){
	$("#resourceInfoDiv").w2popup({
		width 	: 670,
		height	: 350,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
				if(resourceInfo != ""){
					$(".w2ui-msg-body input[name='stemcellName']").val(resourceInfo.stemcellName);
					$(".w2ui-msg-body input[name='stemcellVersion']").val(resourceInfo.stemcellVersion);
					//$(".w2ui-msg-body input[name='cloudInstanceType']").val(resourceInfo.cloudInstanceType);
					$(".w2ui-msg-body input[name='boshPassword']").val(resourceInfo.boshPassword);
				}
			}
		},
		onClose : initSetting
	});
}

function saveAwsResourceInfo(param){
	resourceInfo = {
			id					: boshId,
			stemcellName		: $(".w2ui-msg-body input[name='stemcellName']").val(),
			stemcellVersion		: $(".w2ui-msg-body input[name='stemcellVersion']").val(),
			//cloudInstanceType	: $(".w2ui-msg-body input[name='cloudInstanceType']").val(),
			boshPassword		: $(".w2ui-msg-body input[name='boshPassword']").val()
	}
	
	if(param == 'before'){
		networkPopup();
		return;
	}
	console.log("Release Save");
	//Server send Bosh Info
	$.ajax({
		type : "PUT",
		url : "/bosh/saveAwsResourceInfo",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		data : JSON.stringify(resourceInfo), 
		success : function(data, status) {
			deployPopup();
		},
		error : function( e, status ) {
			w2alert("Bosh Network 등록에 실패 하였습니다.", "Bosh 설치");
		}
	});
	deployPopup();
}

function deployPopup(){
	var deployDiv = (iaas == "AWS") ? $("#deployManifestDiv") : $("#osDeployManifestDiv");
	deployDiv.w2popup({
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

function getDeployInfo(){
	$.ajax({
		type : "GET",
		url : "/bosh/getBoshDeployInfo/"+ boshId,
		contentType : "application/json",
		//dataType: "json",
		async : true, 
		success : function(data, status) {
			if(status == "success"){
				//deployInfo = data;
				$(".w2ui-msg-body #deployInfo").text(data);
			}
			else if(status == "204"){
				w2alert("sampleFile이 존재하지 않습니다.", "BOSH 설치");
			}
		},
		error : function( e, status ) {
			w2alert("Temp 파일을 가져오는 중 오류가 발생하였습니다. ", "BOSH 설치");
		}
	});
}

function boshDeploy(param){
	//Deploy 단에서 저장할 데이터가 있는지 확인 필요
	//Confirm 설치하시겠습니까?
	if(param == 'before' && iaas == "AWS" ){
		resourcePopup();
		return;
	} else if(param == 'before' && iaas == "OPENSTACK" ){
		osResourcePopup();
		return;
	}
	
	w2confirm({
		msg			: "설치하시겠습니까?",
		title		: w2utils.lang('Bosh 설치'),
		yes_text	: "예",
		no_text		: "아니오",
		yes_callBack: installPopup
	});
}

function installPopup(){
	var installDiv = (iaas == 'AWS') ? $("#installDiv") : $("#osInstallDiv");
	installDiv.w2popup({
		width 	: 670,
		height 	: 470,
		modal	: true,
		showMax : true,
		onOpen : function(event){
			event.onComplete = function(){
				//deployFileName
				var socket = new SockJS('/boshInstall');
				installClient = Stomp.over(socket); 
				installClient.connect({}, function(frame) {
					console.log('Connected Frame : ' + frame);
			        installClient.subscribe('/bosh/boshInstall', function(data){
				        console.log('Connected: Data : ' + data);
			        	var installLogs = $(".w2ui-msg-body #installLogs");
			        	installLogs.append(data.body + "\n").scrollTop( installLogs[0].scrollHeight );
			        	
			        	if( data == "complete"){
			        		installClient.close();
			        		installClient.disconnect();//callback
			        		installClient = "";			        		
			        	}
			        });
			        installClient.send('/send/boshInstall', {}, JSON.stringify({deployFileName:deployFileName}));
			    });
			}
		},
		onClose : initSetting
	});
}

//OPENSTACK BOSH INFO POPUP
function osBoshInfoPopup(){
	$("#osBoshInfoDiv").w2popup({
		width 	: 670,
		height	: 450,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
				$(".w2ui-msg-body input[name='releaseVersion']").w2field('list', { items: releases , maxDropHeight:200, width:250});
				if(boshInfo != ""){
					$(".w2ui-msg-body input[name='boshName']").val(boshInfo.boshName);
					$(".w2ui-msg-body input[name='directorUuid']").val(boshInfo.directorUuid);
					//list Type
					$(".w2ui-msg-body input[name='releaseVersion']").data('selected', {text:boshInfo.releaseVersion});
				}				
			}
		},
		onClose : initSetting
	});
}

//Get Releases Info
function getReleaseVersionList(){
	$.ajax({
		type : "GET",
		url : "/releases",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		data : JSON.stringify(boshInfo), 
		success : function(data, status) {
			console.log("Release List");
			releases = new Array();
			data.records.map(function (obj){
				releases.push(obj.name+"/"+obj.version);
			});
			getStamcellList();
		},
		error : function( e, status ) {
			w2alert("Release Version List 를 가져오는데 실패하였습니다.", "Bosh 설치");
		}
	});
}
function getStamcellList(){
	$.ajax({
		type : "GET",
		url : "/stemcells",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		data : JSON.stringify(boshInfo), 
		success : function(data, status) {
			console.log("Stemcell List");
			stemcells = new Array();
			data.records.map(function (obj){
			 	stemcells.push(obj.name+"/"+obj.version);
			});
		},
		error : function( e, status ) {
			w2alert("Stemcell List 를 가져오는데 실패하였습니다.", "Bosh 설치");
		}
	});
}
function saveOsBoshInfo(){
	boshInfo = {
			id				: boshId,
			boshName 		: $(".w2ui-msg-body input[name='boshName']").val(),
			directorUuid 	: $(".w2ui-msg-body input[name='directorUuid']").val(),
			releaseVersion	: $(".w2ui-msg-body input[name='releaseVersion']").val()
	}
	
	$.ajax({
		type : "PUT",
		url : "/bosh/saveOsBoshInfo",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		data : JSON.stringify(boshInfo), 
		success : function(data, status) {
			boshId = data.id;
			openstackInfoPopup();
		},
		error : function( e, status ) {
			w2alert("Bosh Info 등록에 실패 하였습니다.", "Bosh 설치");
		}
	});
} 

function openstackInfoPopup(){
	$("#openstackInfoDiv").w2popup({
		width 	: 670,
		height	: 550,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
				if(openstackInfo != ""){
					$(".w2ui-msg-body input[name='directorName']").val(openstackInfo.directorName);
					$(".w2ui-msg-body input[name='dnsRecursor']").val(openstackInfo.dnsRecursor);
					$(".w2ui-msg-body input[name='authUrl']").val(openstackInfo.authUrl);
					$(".w2ui-msg-body input[name='tenant']").val(openstackInfo.tenant);
					$(".w2ui-msg-body input[name='userName']").val(openstackInfo.userName);
					$(".w2ui-msg-body input[name='apiKey']").val(openstackInfo.apiKey);
					$(".w2ui-msg-body input[name='defaultKeyName']").val(openstackInfo.defaultKeyName);
					$(".w2ui-msg-body input[name='defaultSecurityGroups']").val(openstackInfo.defaultSecurityGroups);
					$(".w2ui-msg-body input[name='ntp']").val(openstackInfo.ntp);
				}
			}
		},
		onClose : initSetting
	});
}

function saveOpenstackInfo(type){
	openstackInfo = {
			id						: boshId,
			directorName			: $(".w2ui-msg-body input[name='directorName']").val(),
			directorStaticIp		: $(".w2ui-msg-body input[name='directorStaticIp']").val(),
			dnsRecursor				: $(".w2ui-msg-body input[name='dnsRecursor']").val(),
			authUrl					: $(".w2ui-msg-body input[name='authUrl']").val(),
			tenant					: $(".w2ui-msg-body input[name='tenant']").val(),
			userName				: $(".w2ui-msg-body input[name='userName']").val(),
			apiKey					: $(".w2ui-msg-body input[name='apiKey']").val(),
			defaultKeyName			: $(".w2ui-msg-body input[name='defaultKeyName']").val(),
			defaultSecurityGroups	: $(".w2ui-msg-body input[name='defaultSecurityGroups']").val(),
			ntp						: $(".w2ui-msg-body input[name='ntp']").val()
	}
	
	if( type == 'before'){
		osBoshInfoPopup();
		return;
	}
	
	$.ajax({
		type : "PUT",
		url : "/bosh/saveOpenstackInfo",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		data : JSON.stringify(openstackInfo), 
		success : function(data, status) {
			osNetworkInfoPopup();
		},
		error : function( e, status ) {
			w2alert("Openstack Info 등록에 실패 하였습니다.", "Bosh 설치");
		}
	});
}

function osNetworkInfoPopup(){
	$("#osNetworkInfoDiv").w2popup({
		width 	: 670,
		height	: 450,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
				if(networkInfo != ""){
					$(".w2ui-msg-body input[name='subnetStatic']").val(networkInfo.subnetStatic);
					$(".w2ui-msg-body input[name='subnetRange']").val(networkInfo.subnetRange);
					$(".w2ui-msg-body input[name='subnetGateway']").val(networkInfo.subnetGateway);
					$(".w2ui-msg-body input[name='subnetDns']").val(networkInfo.subnetDns);
					$(".w2ui-msg-body input[name='cloudNetId']").val(networkInfo.cloudNetId);
				}
			}
		},
		onClose : initSetting
	});
}

function saveOsNetworkInfo(type){
	networkInfo = {
			id				: boshId,
			subnetStatic	: $(".w2ui-msg-body input[name='subnetStatic']").val(),
			subnetRange		: $(".w2ui-msg-body input[name='subnetRange']").val(),
			subnetGateway	: $(".w2ui-msg-body input[name='subnetGateway']").val(),
			subnetDns		: $(".w2ui-msg-body input[name='subnetDns']").val(),
			cloudNetId		: $(".w2ui-msg-body input[name='cloudNetId']").val()
	}
	
	if( type == 'before'){
		openstackInfoPopup();
		return;
	}
	
	$.ajax({
		type : "PUT",
		url : "/bosh/saveOsNetworkInfo",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		data : JSON.stringify(networkInfo), 
		success : function(data, status) {
			osResourceInfoPopup();
		},
		error : function( e, status ) {
			w2alert("Network Info 등록에 실패 하였습니다.", "Bosh 설치");
		}
	});
}

function osResourceInfoPopup(){
	$("#osResourceInfoDiv").w2popup({
		width 	: 670,
		height	: 450,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
				if(resourceInfo != ""){
					$(".w2ui-msg-body input[name='stemcellName']").val(resourceInfo.stemcellName);
					$(".w2ui-msg-body input[name='stemcellVersion']").val(resourceInfo.stemcellVersion);
					$(".w2ui-msg-body input[name='cloudInstanceType']").val(resourceInfo.cloudInstanceType);
					$(".w2ui-msg-body input[name='boshPassword']").val(resourceInfo.boshPassword);
				}
			}
		},
		onClose : initSetting
	});
}

function saveOsResourceInfo(type){
	resourceInfo = {
			id				: boshId,
			stemcellName	: $(".w2ui-msg-body input[name='stemcellName']").val(),
			stemcellVersion	: $(".w2ui-msg-body input[name='stemcellVersion']").val(),
			cloudInstan		: $(".w2ui-msg-body input[name='cloudInstanceType']").val(),
			boshPassword	: $(".w2ui-msg-body input[name='boshPassword']").val()
	}
	
	if( type == 'before'){
		osNetworkInfoPopup();	
		return;
	}
	
	$.ajax({
		type : "PUT",
		url : "/bosh/saveOsResourceInfo",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		data : JSON.stringify(resourceInfo), 
		success : function(data, status) {
			deployPopup();
		},
		error : function( e, status ) {
			w2alert("Resource Info 등록에 실패 하였습니다.", "Bosh 설치");
		}
	});
}

function initSetting(){
	boshId = "";
	awsInfo = "";
	boshInfo = "";
	networkInfo = "";
	resourceInfo = "";
}

function popupComplete(){
	initSetting();
	w2popup.close();
	gridReload();	
}

function doButtonStyle(){
	//Button Style init
	$('#modifyBtn').attr('disabled', true);
	$('#deleteBtn').attr('disabled', true);
}

function gridReload(){
	console.log("delete complete!");
	w2ui['config_boshGrid'].clear();
	doSearch();
}

//다른페이지 이동시 호출
function clearMainPage() {
	$().w2destroy('config_boshGrid');
}

//화면 리사이즈시 호출
$( window ).resize(function() {
	setLayoutContainerHeight();
});
</script>

<div id="main">
	<div class="page_site">설치관리자 환경설정 > <strong>Bosh 설치</strong></div>
	
	<!-- Bosh 목록-->
	<div class="pdt20"> 
		<div class="title fl">Bosh 목록</div>
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
	<div id="config_boshGrid" style="width:100%; height:500px"></div>	
	
</div>

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

<!-- Start AWS Popup  -->

	<!-- AWS  설정 DIV -->
	<div id="awsInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width:100%;height:100%;padding:15px 5px 0 5px;margin:0 auto;">
			<div style="margin-left:3%;">
	            <ul class="progressStep_6" >
		            <li class="active">AWS 설정</li>
		            <li class="before">Bosh Info 설정</li>
		            <li class="before">Network 설정</li>
		            <li class="before">리소스 설정</li>
		            <li class="before">배포 Manifest</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div class="cont_title">▶ AWS 설정정보</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
<!-- 		    	<div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">MicroBOSH(Bootstrap) IP</label>
		            <div>
		            	<input name="dnsRecursor" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div> -->
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Access Key ID</label>
		            <div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Secret Access Key</label>
		            <div>
		                <input name="secretAccessKey" type="password" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>

		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Security Group</label>
		            <div>
		                <input name="defaultSecurityGroups" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Region</label>
		            <div>
		                <input name="region" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Private Key</label>
		            <div>
		                <input name="defaultKeyName" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Private Key Path</label>
	               
	                <div >
  						<span onclick="changeKeyPathType('file');" style="width:200px;"><label><input type="radio" name="keyPathType" value="file"/>&nbsp;파일업로드</label></span>
						&nbsp;&nbsp;
						<span onclick="changeKeyPathType('list');" style="width:200px;"><label><input type="radio" name="keyPathType" value="list"/>&nbsp;리스트</label></span>
					</div>
		        </div>
		        <div class="w2ui-field">			         	
	                <input name="privateKeyPath" type="text" style="width:200px;" hidden="true" onclick="openBrowse();"/>
		            <label style="text-align: left;width:250px;font-size:11px;" class="control-label"></label>
					<div id="keyPathDiv" ></div>
		        </div>
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
		        <button class="btn" style="float: left;" onclick="popupComplete();">취소</button>
		        <button class="btn" style="float: right;padding-right:15%" onclick="saveAwsInfo();">다음>></button>
		    </div>
		</div>
	</div>
	
	<!-- BOSH INFO  설정 DIV -->
	<div id="boshInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width:100%;height:100%;padding:15px 5px 0 5px;margin:0 auto;">
			<div style="margin-left:3%;">
	            <ul class="progressStep_6" >
		            <li class="pass">AWS 설정</li>
		            <li class="active">Bosh Info 설정</li>
		            <li class="before">Network 설정</li>
		            <li class="before">리소스 설정</li>
		            <li class="before">배포 Manifest</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div class="cont_title">▶ Bosh Info 설정정보</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
		    	<div class="w2ui-field" hidden="true">
		            <label>Iaas</label>
		            <div>
		                <input name="iaas" type="text" maxlength="100" />
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">배포명</label>
		            <div>
		                <input name="boshName" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Micro bosh 디렉터 UUID</label>
		            <div>
		                <input name="directorUuid" type="password" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Bosh 릴리즈 버전</label>
		            <div>
		                <input name="releaseVersion" type="list" style="float:left;width:280px;"/>
		            </div>
		        </div>
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: left;" onclick="saveBoshInfo('before');">이전</button>
				<button class="btn" onclick="popupComplete();">취소</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveBoshInfo('after');">다음>></button>
			</div>
		</div>
	</div>
	
	<!-- Network  설정 DIV -->
	<div id="networkInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width:100%;height:100%;padding:15px 5px 0 5px;margin:0 auto;">
			<div style="margin-left:3%;">
	            <ul class="progressStep_6">
		            <li class="pass">AWS 설정</li>
		            <li class="pass">Bosh Info 설정</li>
		            <li class="active">Network 설정</li>
		            <li class="before">리소스 설정</li>
		            <li class="before">배포 Manifest</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" class="cont_title">▶ Network 설정정보</div>
			<div class="w2ui-page page-0" style="padding-left: 5%;">
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">배열	고정 IP 대역</label>
					<div>
						<input name="subnetStatic" type="text" maxlength="100" size="30" style="float:left;width:330px;"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">VPC IP 대역</label>
					<div>
						<input name="subnetRange" type="text" maxlength="100" size="30" style="float:left;width:330px;"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">VPC 게이트웨이 IP</label>
					<div>
						<input name="subnetGateway" type="text" maxlength="100" size="30" style="float:left;width:330px;"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">DNS</label>
					<div>
						<input name="subnetDns" type="text" maxlength="100" size="30" style="float:left;width:330px;"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">VPC 서브넷 아이디</label>
					<div>
						<input name="cloudSubnet" type="text" maxlength="100" size="30" style="float:left;width:330px;"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">VPC 시큐리티 그룹명</label>
					<div>
						<input name="cloudSecurityGroups" type="text" maxlength="100" size="30" style="float:left;width:330px;"/>
					</div>
				</div>
			</div>
			<br />
			<div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: left;" onclick="saveNetworkInfo('before');">이전</button>
				<button class="btn" onclick="popupComplete();">취소</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveNetworkInfo('after');">다음>></button>
			</div>
		</div>
	</div>
	
	<!-- Resource  설정 DIV -->
	<div id="resourceInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width:100%;height:100%;padding:15px 5px 0 5px;margin:0 auto;">
			<div style="margin-left:3%;">
	            <ul class="progressStep_6">
		            <li class="pass">AWS 설정</li>
		            <li class="pass">Bosh Info 설정</li>
		            <li class="pass">Network 설정</li>
		            <li class="active">리소스 설정</li>
		            <li class="before">배포 Manifest</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" class="cont_title">▶ 리소스 설정정보</div>
			<div class="w2ui-page page-0" style="padding-left: 5%;">
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">스템셀 명</label>
					<div>
						<!-- <input name="targetStemcell" type="text" maxlength="100" style="float: left;width:330px;margin-top:1.5px;" /> -->
						<div><input type="list" name="stemcellName" style="float: left;width:330px;margin-top:1.5px;"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">스템셀 버전</label>
					<div>
						<input name="stemcellVersion" type="text" maxlength="100" size="30" style="float:left;width:330px;"/>
					</div>
				</div>
				<!-- <div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">인스턴스 유형</label>
					<div>
						<input name="cloudInstanceType" type="text" maxlength="100" size="30" style="float:left;width:330px;"/>
					</div>
				</div> -->
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">Bosh Password</label>
					<div>
						<input name="boshPassword" type="text" maxlength="100" size="30" style="float:left;width:330px;"/>
					</div>
				</div>
			</div>
			<br />
			<div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: left;" onclick="saveAwsResourceInfo('before');">이전</button>
				<button class="btn" onclick="popupComplete();">취소</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveAwsResourceInfo('after');">다음>></button>
			</div>
		</div>
	</div>
	
	<div id="deployManifestDiv"  hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width:100%;height:100%;padding:15px 5px 0 5px;margin:0 auto;">
			<div style="margin-left:2%;">
	            <ul class="progressStep_6" >
		            <li class="pass">Bosh Info 설정</li>
		            <li class="pass">Openstack Info 설정</li>
		            <li class="pass">Network 설정</li>
		            <li class="pass">리소스 설정</li>
		            <li class="active">배포 Manifest</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" class="cont_title">▶ 배포 Manifest 정보</div>
			<div style="width:97%;height:72%;float:inherit;">
				<textarea id="deployInfo" style="width:100%;height:100%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:1%" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" style="float: left;" onclick="resourcePopup();">이전</button>
			<button class="btn" onclick="popupComplete();">취소</button>
			<button class="btn" style="float: right; padding-right: 15%" onclick="boshDeploy('after');">다음>></button>
		</div>
	</div>
	
	<div id="installDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width:100%;height:100%;padding:15px 5px 0 5px;margin:0 auto;">
			<div style="margin-left:3%;">
	            <ul class="progressStep_6">
		            <li class="pass">AWS 설정</li>
		            <li class="pass">Bosh Info 설정</li>
		            <li class="pass">Network 설정</li>
		            <li class="pass">리소스 설정</li>
		            <li class="pass">배포 Manifest</li>
		            <li class="active">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" class="cont_title">▶ 설치 로그</div>
			<div style="width:95%;height:72%;float: left;">
				<textarea id="installLogs" style="width:95%;height:90%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:1%" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" style="float: left;" onclick="resourcePopup();">이전</button>
			<button class="btn" onclick="popupComplete();">취소</button>
			<button class="btn" style="float: right; padding-right: 15%" onclick="popupComplete();">완료</button>
		</div>		
	</div>	
	<!-- End AWS Popup -->

	<!-- Start Bosh OPENSTACK POP -->
	<div id="osBoshInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width:100%;height:100%;padding:15px 5px 0 5px;margin:0 auto;">
			<div style="margin-left:3%;">
	            <ul class="progressStep_6" >
		            <li class="active">Bosh Info 설정</li>
		            <li class="before">Openstack Info 설정</li>
		            <li class="before">Network 설정</li>
		            <li class="before">리소스 설정</li>
		            <li class="before">배포 Manifest</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div class="cont_title">▶ OPENSTACK 설정정보</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Bosh Name</label>
		            <div>
		                <input name="boshName" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Director UUID</label>
		            <div>
		                <input name="directorUuid" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Release Version</label>
		            <div>
		                <input name="releaseVersion" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
		        <button class="btn" style="float: left;" onclick="popupComplete();">취소</button>
		        <button class="btn" style="float: right;padding-right:15%" onclick="saveOsBoshInfo();">다음>></button>
		    </div>
		</div>
	</div>
	
	<!-- OPENSTACK Info POP -->
	<div id="openstackInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width:100%;height:100%;padding:15px 5px 0 5px;margin:0 auto;">
			<div style="margin-left:3%;">
	            <ul class="progressStep_6" >
		            <li class="pass">Bosh Info 설정</li>
		            <li class="active">Openstack Info 설정</li>
		            <li class="before">Network 설정</li>
		            <li class="before">리소스 설정</li>
		            <li class="before">배포 Manifest</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div class="cont_title">▶ OPENSTACK 설정정보</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Director Name</label>
		            <div>
		                <input name="directorName" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Director Elastic Ip</label>
		            <div>
		                <input name="directorStaticIp" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Dns Recursor</label>
		            <div>
		                <input name="dnsRecursor" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Auth Url</label>
		            <div>
		                <input name="authUrl" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Tenant</label>
		            <div>
		                <input name="tenant" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">User Name</label>
		            <div>
		                <input name="userName" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Api Key</label>
		            <div>
		                <input name="apiKey" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Default Key Name</label>
		            <div>
		                <input name="defaultKeyName" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">default Security Groups</label>
		            <div>
		                <input name="defaultSecurityGroups" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">NTP</label>
		            <div>
		                <input name="ntp" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
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
	
	<!-- OPENSTACK Network Info POP -->
	<div id="osNetworkInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width:100%;height:100%;padding:15px 5px 0 5px;margin:0 auto;">
			<div style="margin-left:3%;">
	            <ul class="progressStep_6" >
		            <li class="pass">Bosh Info 설정</li>
		            <li class="pass">Openstack Info 설정</li>
		            <li class="active">Network 설정</li>
		            <li class="before">리소스 설정</li>
		            <li class="before">배포 Manifest</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div class="cont_title">▶ OPENSTACK 설정정보</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Dubnet Static</label>
		            <div>
		                <input name="subnetStatic" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Subnet Range</label>
		            <div>
		                <input name="subnetRange" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Subnet Gateway</label>
		            <div>
		                <input name="subnetGateway" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Subnet Dns</label>
		            <div>
		                <input name="subnetDns" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Cloud NetId</label>
		            <div>
		                <input name="cloudNetId" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: left;" onclick="saveOsNetworkInfo('before');">이전</button>
				<button class="btn" onclick="popupComplete();">취소</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveOsNetworkInfo('after');">다음>></button>
		    </div>
		</div>
	</div>
	
	<!-- OPENSTACK Resource Info POP -->
	<div id="osResourceInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width:100%;height:100%;padding:15px 5px 0 5px;margin:0 auto;">
			<div style="margin-left:3%;">
	            <ul class="progressStep_6" >
		            <li class="pass">Bosh Info 설정</li>
		            <li class="pass">Openstack Info 설정</li>
		            <li class="pass">Network 설정</li>
		            <li class="active">리소스 설정</li>
		            <li class="before">배포 Manifest</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div class="cont_title">▶ OPENSTACK 설정정보</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Stemcell Name</label>
		            <div>
		                <input name="stemcellName" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Stemcell Version</label>
		            <div>
		                <input name="stemcellVersion" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Cloud Instance Type</label>
		            <div>
		                <input name="cloudInstanceType" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Bosh Password</label>
		            <div>
		                <input name="boshPassword" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: left;" onclick="saveOsResourceInfo('before');">이전</button>
				<button class="btn" onclick="popupComplete();">취소</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveOsResourceInfo('after');">다음>></button>
		    </div>
		</div>
	</div>
	
	<div id="osDeployManifestDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width:100%;height:100%;padding:15px 5px 0 5px;margin:0 auto;">
			<div style="margin-left:3%;">
	            <ul class="progressStep_6" >
		            <li class="pass">Bosh Info 설정</li>
		            <li class="pass">Openstack Info 설정</li>
		            <li class="pass">Network 설정</li>
		            <li class="pass">리소스 설정</li>
		            <li class="active">배포 Manifest</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" class="cont_title">▶ 배포 Manifest 정보</div>
				<div style="width:95%;height:72%;float: left;">
				<textarea id="deployInfo" style="width:100%;height:100%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:1%" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" style="float: left;" onclick="osResourcePopup();">이전</button>
			<button class="btn" onclick="popupComplete();">취소</button>
			<button class="btn" style="float: right; padding-right: 15%" onclick="boshDeploy('after');">다음>></button>
		</div>
	</div>
	
	<div id="osInstallDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width:100%;height:100%;padding:15px 5px 0 5px;margin:0 auto;">
			<div style="margin-left:3%;">
	            <ul class="progressStep_6">
		            <li class="pass">Bosh Info 설정</li>
		            <li class="pass">Openstack Info 설정</li>
		            <li class="pass">Network 설정</li>
		            <li class="pass">리소스 설정</li>
		            <li class="pass">배포 Manifest</li>
		            <li class="active">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" class="cont_title">▶ 설치 로그</div>
			<div style="width:95%;height:72%;float: left;">
				<textarea id="installLogs" style="width:95%;height:90%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:1%" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" style="float: left;" onclick="osResourceInfoPopup()">이전</button>
			<button class="btn" onclick="popupComplete();">취소</button>
			<button class="btn" style="float: right; padding-right: 15%" onclick="popupComplete();">완료</button>
		</div>		
	</div>	