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
.w2ui-popup div.w2ui-field{
	display:inline-block;
	text-align:left;
	width:100%;
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
var deploymentFile ;
var bDefaultDirector; 

$(function(){
	
	// 기본 설치 관리자 정보 조회
 	bDefaultDirector = getDefaultDirector("<c:url value='/directors/default'/>");
	console.log("bDefaultDirector : " +bDefaultDirector);
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
		    				return 'N/A';
		    	   }
				}
			, {field: 'deployLog', caption: '배포로그', size: '100px',
				render: function(record) {
					if ( record.deployStatus == 'done' || record.deployStatus == 'error') {
		       				return '<span id="" class="btn btn-primary" style="width:60px" onClick="getDeployLogMsg( \'bosh\', \''+record.iaas+'\', \''+record.id+'\');">로그보기</span>';
						}
		    			else {
		    				return 'N/A';
						}
					}
				}
			, {field: 'deploymentName', caption: '배포명', size: '100px'}
			, {field: 'iaas', caption: 'IaaS', size: '100px'}
			, {field: 'directorUuid', caption: '설치관리자 UUID', size: '220px'}
			, {field: 'releaseVersion', caption: 'BOSH 릴리즈', size: '100px'}
			
			, {field: 'publicStaticIp', caption: '디렉터 공인 IP', size: '100px'}
			, {field: 'subnetRange', caption: '서브넷 범위', size: '100px'}
			, {field: 'subnetStatic', caption: 'VM 할당 IP대역', size: '240px'
				, render:function(record){
						if( record.subnetStaticFrom && record.subnetStaticTo ){
							return record.subnetStaticFrom +" - "+ record.subnetStaticTo;
						}
					}
				}
			, {field: 'subnetGateway', caption: '게이트웨이', size: '100px'}
			, {field: 'subnetDns', caption: 'DNS', size: '100px'}
			, {field: 'subnetId', caption: '서브넷 ID(NET ID)', size: '100px'}
			, {field: 'stemcell', caption: '스템셀', size: '240px'
				, render:function(record){
						if( record.stemcellName && record.stemcellVersion ){
							return record.stemcellName +"/"+ record.stemcellVersion;
						}
					}
				}
			, {field: 'cloudInstanceType', caption: '인스턴스 유형', size: '100px'}
			
			],
		onClick:function(event) {
			var grid = this;
			event.onComplete = function() {
				var sel = grid.getSelection();

				if ( sel == null || sel == "" || bDefaultDirector == false ) {
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
		if($("#installBtn").attr('disabled') == "disabled") return;
		var directorName = $("#directorName").text().toUpperCase();
		
		if ( directorName.indexOf("AWS") > 0 ) {
			iaas = "AWS";
			awsPopup();
		} else if (directorName.indexOf("OPENSTACK") > 0 ) {
			iaas = "OPENSTACk";
			openstackPopup();
		} else {
			selectIaas();
		}
		
	});
	
	//Bosh 수정
	$("#modifyBtn").click(function(){
		if($("#modifyBtn").attr('disabled') == "disabled") return;
		w2confirm({
			title 	: "BOSH 설치",
			msg		: "BOSH설치 정보를 수정하시겠습니까?",
			yes_text: "확인",
			yes_callBack : function(event){
				var selected = w2ui['config_boshGrid'].getSelection();

				if( selected.length == 0 ){
					w2alert("선택된 정보가 없습니다.", "BOSH 설치");
					return;
				}
				else{
					var record = w2ui['config_boshGrid'].get(selected);
					if (record.iaas == "AWS")
						getBoshAwsData(record);
					else
						getBoshOpenstackData(record);
				}
			},
			no_text : "취소"
		});
 	});
 	
 	//Bosh 삭제
	$("#deleteBtn").click(function(){
		if($("#deleteBtn").attr('disabled') == "disabled") return;
		
		var selected = w2ui['config_boshGrid'].getSelection();
		var record = w2ui['config_boshGrid'].get(selected);
		
		var message = "";
		
		if ( record.deploymentName )
			message = "BOSH (배포명 : " + record.deploymentName + ")를 삭제하시겠습니까?";
		else
			message = "선택된 BOSH를 삭제하시겠습니까?";
			
		w2confirm({
			title 	: "BOSH 삭제",
			msg		: message,
			yes_text: "확인",
			yes_callBack : function(event){
				deletePopup(record);
			},
			no_text : "취소"
		});
 	});
 	
	initView();
 	
});

function initView() {
	doSearch();
}	

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
			iaas = $(".w2ui-msg-body input:radio[name='structureType']:checked").val();
			if(iaas){
				if( structureType == "AWS")
					awsPopup();
				else
					openstackPopup();				
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
			if ( data == null || data == "" ){

			} 
			else {
				initSetting();
				settingAWSData(data.contents);
			}
		},
		error : function(request, status, error) {
			var errorResult = JSON.parse(request.responseText);
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
			if ( data ){
				initSetting();
				settingOpenstackData(data.contents);
			}
		},
		error : function(request, status, error) {
			var errorResult = JSON.parse(request.responseText);
			w2alert(errorResult.message, "BOSH 설치");
		}
	});
}

//DELETE
function deletePopup(record){
	
	var requestParameter = {iaas:record.iaas, id:record.id};
	
	if ( record.deployStatus == null || record.deployStatus == '' ) {
		// 단순 레코드 삭제
		var url = "/bosh/delete";
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
				w2alert(errorResult.message, "BOSH 삭제");
			}
		});
		
	} else {
		var message = "";
		var body = '<textarea id="deleteLogs" style="width:95%;height:90%;overflow-y:visible;resize:none;background-color: #FFF; margin:2%" readonly="readonly"></textarea>';
		
		w2popup.open({
			width : 610,
			height : 500,
			title : "<b>BOSH 삭제</b>",
			body  : body,
			buttons : '<button class="btn" style="float: right; padding-right: 15%;" onclick="popupComplete();">닫기</button>',
			showMax : true,
			onOpen : function(event){
				event.onComplete = function(){
					var socket = new SockJS('/boshDelete');
					var deleteClient = Stomp.over(socket); 
					deleteClient.connect({}, function(frame) {
						deleteClient.subscribe('/bosh/boshDelete', function(data){
							
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
									w2alert(message, "BOSH 삭제");
						       	}
				        	}
				        	
				        });
						deleteClient.send('/send/boshDelete', {}, JSON.stringify(requestParameter));
				    });
				}
			},
			onClose : function (event){
				event.onComplete= function(){
					$("textarea").text("");
					w2ui['config_boshGrid'].reset();
					deleteClient.disconnect();
					deleteClient = "";
					doSearch();
				}
			}
		});
	}		
}
//Aws Popup
function awsPopup(){
	$("#awsInfoDiv").w2popup({
		width 	: 700,
		height	: 500,
		title   : "BOSH설치 (AWS)",
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
				if(awsInfo != ""){					
					$(".w2ui-msg-body input[name='accessKeyId']").val(awsInfo.accessKeyId);
					$(".w2ui-msg-body input[name='secretAccessKey']").val(awsInfo.secretAccessKey);
					$(".w2ui-msg-body input[name='defaultSecurityGroups']").val(awsInfo.defaultSecurityGroups);
					$(".w2ui-msg-body input[name='region']").val(awsInfo.region);
					$(".w2ui-msg-body input[name='privateKeyName']").val(awsInfo.privateKeyName);
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
		url : "/common/getKeyPathFileList",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		success : function(data, status) {
			keyPathFileList = data;
			$('.w2ui-msg-body input:radio[name=keyPathType]:input[value="list"]').attr("checked", true);
			changeKeyPathType("list");
		},
		error : function( e, status ) {
			w2alert("Private Key 목록조회 실패하였습니다.", "BOSH 설치");
		}
	});
}

function changeKeyPathType(type){
	var keyPathDiv = $('.w2ui-msg-body #keyPathDiv');
	var fileUploadInput = '<span><input type="file" name="keyPathFile" onchange="setPrivateKeyPathFileName(this);" hidden="true"/>';
	fileUploadInput += '<input type="text" id="keyPathFileName" style="width:45%;" readonly  onClick="openBrowse();" placeholder="Key File을 선택해주세요."/>';
	fileUploadInput += '<a href="#" id="browse" onClick="openBrowse();">Browse </a></span>';
	var selectInput = '<input type="list" name="keyPathList" style="float: left;width:60%;" onchange="setPrivateKeyPath(this.value);"/>';
	
	if(type == "list") {
		keyPathDiv.html(selectInput);
		$('#w2ui-popup #keyPathDiv input[type=list]').w2field('list', { items: keyPathFileList , maxDropHeight:200, width:250});
		if(awsInfo.privateKeyPath) $(".w2ui-msg-body input[name='keyPathList']").data('selected', {text:awsInfo.privateKeyPath});
		if(openstackInfo.privateKeyPath) $(".w2ui-msg-body input[name='keyPathList']").data('selected', {text:openstackInfo.privateKeyPath});
		
	}else{
		keyPathDiv.html(fileUploadInput);
		$(".w2ui-msg-body input[name='keyPathFile']").hide();		
	}
}

function setPrivateKeyPath(value){
	$(".w2ui-msg-body input[name=privateKeyPath]").val(value);
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
			accessKeyId 			: $(".w2ui-msg-body input[name='accessKeyId']").val(),
			secretAccessKey			: $(".w2ui-msg-body input[name='secretAccessKey']").val(),
			defaultSecurityGroups	: $(".w2ui-msg-body input[name='defaultSecurityGroups']").val(),
			region					: $(".w2ui-msg-body input[name='region']").val(),
			privateKeyName			: $(".w2ui-msg-body input[name='privateKeyName']").val(),
			privateKeyPath			: $(".w2ui-msg-body input[name='privateKeyPath']").val()
	}
	
	if(popupValidation()){
		//ajax AwsInfo Save
		$.ajax({
			type : "PUT",
			url : "/bosh/saveAwsInfo",
			contentType : "application/json",
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
}

function keyPathFileUpload(){
	var form = (iaas == "AWS") ? $(".w2ui-msg-body #awsForm")[0] : $(".w2ui-msg-body #osBoshForm")[0];
	
	var formData = new FormData(form);
	$.ajax({
		type : "POST",
		url : "/common/keyPathFileUpload",
		enctype : 'multipart/form-data',
		dataType: "text",
		async : true,
		processData: false, 
		contentType:false,
		data : formData,  
		success : function(data, status) {
			if(iaas == "AWS")
				awsBoshInfoPopup();
			else 
				osBoshInfoPopup();
		},
		error : function( e, status ) {			
			w2alert(iaas + " 설정 등록에 실패 하였습니다.", "BOSH 설치");
		}
	});
}

function settingAWSData(contents){
	boshId = contents.id;
	iaas = "AWS";
	awsInfo = {
			iaas		 			: "AWS",
			accessKeyId				: contents.accessKeyId,
			secretAccessKey			: contents.secretAccessKey,
			defaultSecurityGroups	: contents.defaultSecurityGroups,
			region					: contents.region,
			privateKeyName			: contents.privateKeyName,			
			privateKeyPath			: contents.privateKeyPath
	}
	
	boshInfo = {
			id				: boshId,
			deploymentName  : contents.deploymentName,
			directorUuid	: contents.directorUuid,
			releaseVersion	: contents.releaseVersion
	}
	
	networkInfo = {
			id					: boshId,
			publicStaticIp		: contents.publicStaticIp,
			subnetStaticFrom	: contents.subnetStaticFrom,
			subnetStaticTo		: contents.subnetStaticTo,
			subnetRange			: contents.subnetRange,
			subnetGateway		: contents.subnetGateway,
			subnetDns			: contents.subnetDns,
			subnetId			: contents.subnetId
	}
	
	resourceInfo = {
			id					: contents.id,
			stemcellName		: contents.stemcellName,
			stemcellVersion		: contents.stemcellVersion,
			cloudInstanceType	:  contents.cloudInstanceType,
			boshPassword		: contents.boshPassword
	}
	
	awsPopup();
}

function settingOpenstackData(contents){
	boshId = contents.id;
	iaas = "OPENSTACK";
	
	openstackInfo = {
			id						: boshId,
			authUrl					: contents.authUrl,
			tenant					: contents.tenant,
			userName				: contents.userName,
			apiKey					: contents.apiKey,
			defaultSecurityGroups	: contents.defaultSecurityGroups,
			privateKeyName			: contents.privateKeyName,
			privateKeyPath			: contents.privateKeyPath
	}
	
	boshInfo = {
			id					: boshId,
			deploymentName		: contents.deploymentName,
			directorUuid		: contents.directorUuid,
			releaseVersion		: contents.releaseVersion
	}
	
	networkInfo = {
			id					: boshId,
			publicStaticIp		: contents.publicStaticIp,
			subnetId			: contents.subnetId,
			subnetStaticFrom	: contents.subnetStaticFrom,
			subnetStaticTo		: contents.subnetStaticTo,
			subnetRange			: contents.subnetRange,
			subnetGateway		: contents.subnetGateway,
			subnetDns			: contents.subnetDns
	}
	
	resourceInfo = {
			id					: boshId,
			stemcellName		: contents.stemcellName,
			stemcellVersion		: contents.stemcellVersion,
			cloudInstanceType	: contents.cloudInstanceType,
			boshPassword		: contents.boshPassword
	}
	
	openstackPopup();
}

function  awsBoshInfoPopup(){
	$("#boshInfoDiv").w2popup({
		width 	: 700,
		height	: 350,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){
				if(boshInfo != ""){
					$(".w2ui-msg-body input[name='deploymentName']").val(boshInfo.deploymentName);
					$(".w2ui-msg-body input[name='directorUuid']").val(boshInfo.directorUuid);
				} else {
					if( !checkEmpty($("#directorUuid").text()) ){
						$(".w2ui-msg-body input[name='directorUuid']").val($("#directorUuid").text());
					}
				}
				w2popup.lock("릴리즈를 조회 중입니다.", true);
				getReleaseVersionList();
			}
		},
		onClose : initSetting
	});
}

function saveBoshInfo(type){
	boshInfo = {
			id				: boshId,
			deploymentName	: $(".w2ui-msg-body input[name='deploymentName']").val(),
			directorUuid	: $(".w2ui-msg-body input[name='directorUuid']").val(),
			releaseVersion	: $(".w2ui-msg-body input[name='releaseVersion']").val()
	}
	
	if ( type == 'after' ) {
		if(popupValidation()){
			//Server send Bosh Info
			$.ajax({
				type : "PUT",
				url : "/bosh/saveAwsBoshInfo",
				contentType : "application/json",
				async : true,
				data : JSON.stringify(boshInfo), 
				success : function(data, status) {
					networkPopup();
				},
				error : function( e, status ) {
					w2alert("기본정보 저장 실패하였습니다.", "Bosh 설치(AWS)");
				}
			});
		}
	}
	else if(type == 'before'){
		awsPopup(); 
	}
}

function networkPopup(){
	$("#networkInfoDiv").w2popup({
		width 	: 700,
		height	: 450,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
				if(networkInfo != ""){
					$(".w2ui-msg-body input[name='publicStaticIp']").val(networkInfo.publicStaticIp);
					$(".w2ui-msg-body input[name='subnetStaticFrom']").val(networkInfo.subnetStaticFrom);
					$(".w2ui-msg-body input[name='subnetStaticTo']").val(networkInfo.subnetStaticTo);
					$(".w2ui-msg-body input[name='subnetRange']").val(networkInfo.subnetRange);
					$(".w2ui-msg-body input[name='subnetGateway']").val(networkInfo.subnetGateway);
					$(".w2ui-msg-body input[name='subnetDns']").val(networkInfo.subnetDns);
					$(".w2ui-msg-body input[name='subnetId']").val(networkInfo.subnetId);
				}
			}
		},
		onClose : initSetting
	});
}

function saveNetworkInfo(type){
	networkInfo = {
			id					: boshId,
			publicStaticIp		: $(".w2ui-msg-body input[name='publicStaticIp']").val(),
			subnetStaticFrom	: $(".w2ui-msg-body input[name='subnetStaticFrom']").val(),
			subnetStaticTo		: $(".w2ui-msg-body input[name='subnetStaticTo']").val(),
			subnetRange			: $(".w2ui-msg-body input[name='subnetRange']").val(),
			subnetGateway		: $(".w2ui-msg-body input[name='subnetGateway']").val(),
			subnetDns			: $(".w2ui-msg-body input[name='subnetDns']").val(),
			subnetId			: $(".w2ui-msg-body input[name='subnetId']").val()
	}
	
	if ( type == 'after' ) {
		if(popupValidation()){
			$.ajax({
				type : "PUT",
				url : "/bosh/saveAwsNetworkInfo",
				contentType : "application/json",
				async : true,
				data : JSON.stringify(networkInfo), 
				success : function(data, status) {
					awsResourcePopup();
				},
				error : function( e, status ) {
					w2alert("Bosh Network 등록에 실패 하였습니다.", "Bosh 설치");
				}
			});
		}
	}
	else if(type == 'before'){
		awsBoshInfoPopup();
	}
}

function awsResourcePopup(){
	$("#resourceInfoDiv").w2popup({
		width 	: 700,
		height	: 350,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){
				if(resourceInfo != ""){
					$(".w2ui-msg-body input[name='cloudInstanceType']").val(resourceInfo.cloudInstanceType);
					$(".w2ui-msg-body input[name='boshPassword']").val(resourceInfo.boshPassword);
				}
				w2popup.lock("스템셀을 조회 중입니다.", true);
				getStemcellList();
			}
		},
		onClose : initSetting
	});
}

function saveAwsResourceInfo(type){
	
	var stemcellInfo = $(".w2ui-msg-body input[name='stemcells']").val().split("/");
	resourceInfo = {
			id					: boshId,
			stemcellName		: stemcellInfo[0],
			stemcellVersion		: stemcellInfo[1],
			cloudInstanceType	: $(".w2ui-msg-body input[name='cloudInstanceType']").val(),
			boshPassword		: $(".w2ui-msg-body input[name='boshPassword']").val()
	}
	
	if ( type == 'after' ) {
		if(popupValidation()){
			//Server send Bosh Info
			$.ajax({
				type : "PUT",
				url : "/bosh/saveAwsResourceInfo",
				contentType : "application/json",
				async : true,
				data : JSON.stringify(resourceInfo), 
				success : function(data, status) {
					deploymentFile = data.content.deploymentFile;
					deployPopup();
				},
				error : function( e, status ) {
					w2alert("Bosh Resource 등록에 실패 하였습니다.", "Bosh 설치");
				}
			});
		}
		
	} else if(type == 'before'){
		networkPopup();
	}
}

function deployPopup(){
	var deployDiv = (iaas == "AWS") ? $("#awsDeployDiv") : $("#openstackDeployDiv");
	deployDiv.w2popup({
		width 	: 700,
		height 	: 520,
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
		type : "POST",
		url : "/common/getDeployInfo",
		contentType : "application/json",
		async : true, 
		data : deploymentFile,
		success : function(data, status) {
			if(status == "success"){
				$(".w2ui-msg-body #deployInfo").text(data);
			}
			else if(status == "204"){
				w2alert("배포파일이 존재하지 않습니다.", "BOSH 설치");
			}
		},
		error : function( e, status ) {
			w2alert("Temp 파일을 가져오는 중 오류가 발생하였습니다. ", "BOSH 설치");
		}
	});
}

function boshDeploy(type){
	//Deploy 단에서 저장할 데이터가 있는지 확인 필요
	//Confirm 설치하시겠습니까?
	if(type == 'before' && iaas == "AWS" ){
		awsResourcePopup();
		return;
	} else if(type == 'before' && iaas == "OPENSTACK" ){
		osResourceInfoPopup();
		return;
	}
	
	w2confirm({
		msg			: "설치하시겠습니까?",
		title		: w2utils.lang('BOSH 설치'),
		yes_text	: "예",
		no_text		: "아니오",
		yes_callBack: installPopup
	});
}

//배포파일 설치 팝업
function installPopup(){
	
	var installDiv = (iaas == 'AWS') ? $("#awsInstallDiv") : $("#openstackInstallDiv");
	var message = "BOSH(배포명:" + boshInfo.deploymentName +  ") ";
	
	var requestParameter = {
			id : boshId,
			iaas: iaas
	};
	
	installDiv.w2popup({
		width 	: 700,
		height 	: 520,
		modal	: true,
		showMax : true,
		onOpen : function(event){
			event.onComplete = function(){
				//deployFileName
				var socket = new SockJS('/boshInstall');
				installClient = Stomp.over(socket); 
				installClient.connect({}, function(frame) {
			        installClient.subscribe('/bosh/boshInstall', function(data){
			        	
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
								w2alert(message, "BOSH 설치");
					       	}
			        	}

			        });
			        
			        installClient.send('/send/boshInstall', {}, JSON.stringify(requestParameter));
			    });
			}
		}
	});
}

// Openstack BOSH INFO 
function osBoshInfoPopup(){
	$("#osBoshInfoDiv").w2popup({
		width 	: 700,
		height	: 450,
		title   : "BOSH설치 (OPENSTACK)",
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
				if(boshInfo != ""){
					$(".w2ui-msg-body input[name='deploymentName']").val(boshInfo.deploymentName);
					$(".w2ui-msg-body input[name='directorUuid']").val($("#directorUuid").text());
				} else {
					if( !checkEmpty($("#directorUuid").text()) ){
						$(".w2ui-msg-body input[name='directorUuid']").val($("#directorUuid").text());
					}
				}
				
				w2popup.lock("릴리즈를 조회 중입니다.", true);
				getReleaseVersionList();
			}
		},
		onClose : initSetting
	});
}

//Get Releases Info
function getReleaseVersionList(){
	$.ajax({
		type : "GET",
		url : "/bosh/releases",
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

function setReleaseVersionList(){
	$(".w2ui-msg-body input[name='releaseVersion']").w2field('list', { items: releases , maxDropHeight:200, width:294});
	setReleaseData();
}

function setReleaseData(){
	console.log("boshInfo : releaseVersion : " + boshInfo.releaseVersion);
	if(!checkEmpty(boshInfo.releaseVersion)){
		$(".w2ui-msg-body input[name='releaseVersion']").data('selected', {text:boshInfo.releaseVersion});
	}
	w2popup.unlock();
}

function getStemcellList(){
	$.ajax({
		type : "GET",
		url : "/stemcells",
		contentType : "application/json",
		async : true,
		data : JSON.stringify(boshInfo), 
		success : function(data, status) {
			console.log("Stemcell List");
			stemcells = new Array();
			if(data.records != null){
				data.records.map(function (obj){
				 	stemcells.push(obj.name+"/"+obj.version);
				});
			}
			setStemcellList();
		},
		error : function( e, status ) {
			w2popup.unlock();
			w2alert("Stemcell List 를 가져오는데 실패하였습니다.", "BOSH 설치");
		}
	});
}

function setStemcellList(){
	$(".w2ui-msg-body input[name='stemcells']").w2field('list', { items: stemcells , maxDropHeight:200, width:294});
	setStemcellData();
}

function setStemcellData(){
	if(!checkEmpty(resourceInfo.stemcellName) && !checkEmpty(resourceInfo.stemcellVersion )){
		$(".w2ui-msg-body input[name='stemcells']").data('selected', {text: resourceInfo.stemcellName +'/'+resourceInfo.stemcellVersion });
	}
	w2popup.unlock();
}

function openstackPopup(){
	$("#openstackInfoDiv").w2popup({
		width 	: 700,
		height	: 550,
		title   : "BOSH설치 (오픈스택)",
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
				
				getKeyPathFileList();
			}
		},
		onClose : initSetting
	});
}

function saveOpenstackInfo(){
	openstackInfo = {
			id						: boshId,
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
			url : "/bosh/saveOpenstackInfo",
			contentType : "application/json",
			async : true,
			data : JSON.stringify(openstackInfo), 
			success : function(data, status) {
				boshId = data.id;
				keyPathFileUpload();
			},
			error : function( e, status ) {
				w2alert("오픈스택 정보 등록 실패하였습니다.", "BOSH 설치");
			}
		});
	}
}	
	
function saveOsBoshInfo(type){	
	boshInfo = {
			id				: boshId,
			deploymentName	: $(".w2ui-msg-body input[name='deploymentName']").val(),
			directorUuid 	: $(".w2ui-msg-body input[name='directorUuid']").val(),
			releaseVersion	: $(".w2ui-msg-body input[name='releaseVersion']").val()
	}

	if ( type == 'after') {
		if(popupValidation()){
			$.ajax({
				type : "PUT",
				url : "/bosh/saveOsBoshInfo",
				contentType : "application/json",
				async : true,
				data : JSON.stringify(boshInfo), 
				success : function(data, status) {
					boshId = data.id;
					osNetworkInfoPopup();
				},
				error : function( e, status ) {
					w2alert("기본정보 등록 실패하였습니다.", "BOSH 설치");
				}
			});
		}
	}
	else {
		openstackPopup();
	}
} 

function osNetworkInfoPopup(){
	$("#osNetworkInfoDiv").w2popup({
		width 	: 700,
		height	: 550,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){
				if(networkInfo != ""){
					$(".w2ui-msg-body input[name='publicStaticIp']").val(networkInfo.publicStaticIp);
					$(".w2ui-msg-body input[name='subnetId']").val(networkInfo.subnetId);
					$(".w2ui-msg-body input[name='subnetStaticFrom']").val(networkInfo.subnetStaticFrom);
					$(".w2ui-msg-body input[name='subnetStaticTo']").val(networkInfo.subnetStaticTo);
					$(".w2ui-msg-body input[name='subnetRange']").val(networkInfo.subnetRange);
					$(".w2ui-msg-body input[name='subnetGateway']").val(networkInfo.subnetGateway);
					$(".w2ui-msg-body input[name='subnetDns']").val(networkInfo.subnetDns);
				}
			}
		},
		onClose : initSetting
	});
}

function saveOsNetworkInfo(type){
	networkInfo = {
			id					: boshId,
			publicStaticIp		: $(".w2ui-msg-body input[name='publicStaticIp']").val(),
			subnetId			: $(".w2ui-msg-body input[name='subnetId']").val(),
			subnetStaticFrom	: $(".w2ui-msg-body input[name='subnetStaticFrom']").val(),
			subnetStaticTo		: $(".w2ui-msg-body input[name='subnetStaticTo']").val(),
			subnetRange			: $(".w2ui-msg-body input[name='subnetRange']").val(),
			subnetGateway		: $(".w2ui-msg-body input[name='subnetGateway']").val(),
			subnetDns			: $(".w2ui-msg-body input[name='subnetDns']").val()
	}
	
	if ( type == 'after' ) {
		if(popupValidation()){
			$.ajax({
				type : "PUT",
				url : "/bosh/saveOsNetworkInfo",
				contentType : "application/json",
				async : true,
				data : JSON.stringify(networkInfo), 
				success : function(data, status) {
					osResourceInfoPopup();
				},
				error : function( e, status ) {
					w2alert("네트워크 정보 등록 실패하였습니다.", "BOSH 설치");
				}
			});
		}
	} else {
		osBoshInfoPopup();
	}
}

function osResourceInfoPopup(){
	$("#osResourceInfoDiv").w2popup({
		width 	: 700,
		height	: 450,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
				if(resourceInfo != ""){
					$(".w2ui-msg-body input[name='cloudInstanceType']").val(resourceInfo.cloudInstanceType);
					$(".w2ui-msg-body input[name='boshPassword']").val(resourceInfo.boshPassword);
				}
				w2popup.lock("스템셀을 조회 중입니다.", true);
				getStemcellList();
			}
		},
		onClose : initSetting
	});
}

function saveOsResourceInfo(type){
	var stemcellInfo = $(".w2ui-msg-body input[name='stemcells']").val().split("/");
	resourceInfo = {
			id				: boshId,
			stemcellName		: stemcellInfo[0],
			stemcellVersion		: stemcellInfo[1],
			cloudInstanceType		: $(".w2ui-msg-body input[name='cloudInstanceType']").val(),
			boshPassword	: $(".w2ui-msg-body input[name='boshPassword']").val()
	}
	
	if ( type == 'after' ) {
		if( popupValidation()){
			$.ajax({
				type : "PUT",
				url : "/bosh/saveOsResourceInfo",
				contentType : "application/json",
				async : true,
				data : JSON.stringify(resourceInfo), 
				success : function(data, status) {
					deploymentFile = data.content.deploymentFile;
					deployPopup();
				},
				error : function( e, status ) {
					w2alert("리소스 정보 등록 실패하였습니다.", "BOSH 설치");
				}
			});
		}
	} else {
		osNetworkInfoPopup();	
	}
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
	
	if ( !bDefaultDirector ) {
		console.log("AAAA");
		$('#installBtn').attr('disabled', true);
		$('#modifyBtn').attr('disabled', true);
		$('#deleteBtn').attr('disabled', true);
	} 
	else {
		console.log("HHHHH");
		$('#installBtn').removeAttr("disabled"); 
		$('#modifyBtn').attr('disabled', true);
		$('#deleteBtn').attr('disabled', true);
	}
}

function gridReload(){
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
	<div class="page_site">플랫폼 설치 > <strong>Bosh 설치</strong></div>
	
	<!-- 설치 관리자 -->
	<div class="title">설치 관리자</div>
	
	<table class="tbl1" border="1" cellspacing="0">
	<tr>
		<th width="18%" class="th_fb">관리자 이름</th><td class="td_fb"><b id="directorName"></b></td>
		<th width="18%" class="th_fb">관리자 계정</th><td class="td_fb"><b id="userId"></b></td>
	</tr>
	<tr>
		<th width="18%" >관리자 URL</th><td><b id="directorUrl"></b></td>
		<th width="18%" >관리자 UUID</th><td ><b id="directorUuid"></b></td>
	</tr>
	</table>
	
	<!-- Bosh 목록-->
	<div class="pdt20"> 
		<div class="title fl">BOSH 목록</div>
		<div class="fr"> 
			<!-- Btn -->
			<span id="installBtn" class="btn btn-primary" style="width:120px">설&nbsp;&nbsp;치</span>
			&nbsp;
			<span id="modifyBtn" class="btn btn-info" style="width:120px">수&nbsp;&nbsp;정</span>
			&nbsp;
			<span id="deleteBtn" class="btn btn-danger" style="width:120px">삭&nbsp;&nbsp;제</span>
			<!-- //Btn -->
	    </div>
	</div>
	<div id="config_boshGrid" style="width:100%; height:500px"></div>
</div>

	<!-- IaaS 설정 DIV -->
	<div id="bootSelectBody" style="width:100%; height: 80px;" hidden="true">
		<div class="w2ui-lefted" style="text-align: left;">
			IaaS를 선택하세요
		</div>
		<div class="col-sm-9">
			<div class="btn-group" data-toggle="buttons" >
				<label style="width: 100px;margin-left:40px;">
					<input type="radio" name="structureType" id="type1" value="AWS" checked="checked"  />
					&nbsp;AWS
				</label>
				<label style="width: 130px;margin-left:50px;">
					<input type="radio" name="structureType" id="type2" value="OPENSTACK"  />
					&nbsp;OPENSTACK
				</label>
			</div>
		</div>
	</div>

	<!-- Start AWS Popup  -->

	<!-- AWS  설정 DIV -->
	<div id="awsInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 3%;display:inline-block;width: 97%;">
	            <ul class="progressStep_6" >
		            <li class="active">AWS 정보</li>
		            <li class="before">기본 정보</li>
		            <li class="before">네트워크 정보</li>
		            <li class="before">리소스 정보</li>
		            <li class="before">배포파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div style="margin:15px 1.5%;"><span class="glyphicon glyphicon-stop"></span>&nbsp; AWS정보 설정</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
		        <div class="w2ui-field">
		            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;Access Key ID</label>
		            <div>
		                <input name="accessKeyId" type="text"  style="float:left;width:60%;" required placeholder="AWS Access Key를 입력하세요."/>
		            </div>
				</div>
				
		        <div class="w2ui-field">
		            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;Secret Access Key</label>
		            <div>
		                <input name="secretAccessKey" type="text"  style="float:left;width:60%;" required placeholder="AWS Secret Access Key를 입력하세요."/>
		            </div>
		        </div>

		        <div class="w2ui-field">
		            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;Security Group</label>
		            <div>
		                <input name="defaultSecurityGroups" type="text"  style="float:left;width:60%;" required placeholder="시큐리티 그룹을 입력하세요."/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;Region</label>
		            <div>
		                <input name="region" type="text"  style="float:left;width:60%;"  required placeholder="설치할 Region을 입력하세요.(예: us-east-1)"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;Private Key Name</label>
		            <div>
		                <input name="privateKeyName" type="text"  style="float:left;width:60%;" required placeholder="Key Pair 이름을 입력하세요."/>
		            </div>
		        </div>
		        
		        <div class="w2ui-field">
		            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;Private Key File</label>
	                <div >
  						<span onclick="changeKeyPathType('file');" style="width:200px;"><label><input type="radio" name="keyPathType"  value="file"/>&nbsp;파일 업로드</label></span>
						&nbsp;&nbsp;
						<span onclick="changeKeyPathType('list');" style="width:200px;"><label><input type="radio" name="keyPathType"  value="list"/>&nbsp;목록에서 선택</label></span>
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
		        <button class="btn" style="float: right;padding-right:15%" onclick="saveAwsInfo();" >다음>></button>
		    </div>
		</div>
	</div>
	
	<!-- BOSH INFO  설정 DIV -->
	<div id="boshInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 3%;display:inline-block;width: 97%;">
	            <ul class="progressStep_6" >
		            <li class="pass">AWS 정보</li>
		            <li class="active">기본 정보</li>
		            <li class="before">네트워크 정보</li>
		            <li class="before">리소스 정보</li>
		            <li class="before">배포파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div style="margin:15px 1.5%;"><span class="glyphicon glyphicon-stop"></span>&nbsp; 기본정보 설정</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
		        <div class="w2ui-field">
		            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;배포명</label>
		            <div>
		                <input name="deploymentName" type="text"  style="float:left;width:60%;" required placeholder="배포명을 입력하세요."/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;설치관리자 UUID</label>
		            <div>
		                <input name="directorUuid" type="text" style="float:left;width:60%;"  required placeholder="설치관리자 UUID입력하세요."/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;BOSH 릴리즈</label>
		            <div>
		                <input name="releaseVersion" type="list" style="float:left;width:60%;" placeholder="BOSH 릴리즈를 선택하세요." />
		            </div>
		        </div>
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: left;" onclick="saveBoshInfo('before');" >이전</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveBoshInfo('after');" >다음>></button>
			</div>
		</div>
	</div>
	
	
	<!-- Network 설정 DIV -->
	<div id="networkInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 3%;display:inline-block;width: 97%;">
	            <ul class="progressStep_6">
					<li class="pass">AWS 정보</li>
		            <li class="pass">기본 정보</li>
		            <li class="active">네트워크 정보</li>
		            <li class="before">리소스 정보</li>
		            <li class="before">배포파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div style="margin:15px 1.5%;"><span class="glyphicon glyphicon-stop"></span>&nbsp; 네트워크정보 설정</div>
			<div class="w2ui-page page-0" style="padding-left: 5%;">
				<div class="w2ui-field">
					<label style="text-align: left; width:40%; font-size: 11px;">&bull;&nbsp;서브넷 ID(NET ID)</label>
					<div>
						<input name="subnetId" type="text"  style="float:left;width:60%;"  required placeholder="예) subnet-XXXXXX"/>
					</div>
				</div>
				<div class="w2ui-field">
		            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;디렉터 공인 IP</label>
		            <div>
		                <input name="publicStaticIp" type="text"  style="float:left;width:60%;"  required placeholder="설치관리자에 할당할 디렉터 공인 IP를 입력하세요."/>
		                <div class="isMessage"></div>
		            </div>
		        </div>
				<div class="w2ui-field">
					<label style="text-align:left; width:40%; font-size: 11px;">&bull;&nbsp;VM 할당 IP대역</label>
					<div>
						<div style="display:inline-block;width: 60%;">
							<span style="float:left;width:45%;"><input name="subnetStaticFrom" id="subnetStaticFrom" type="text" style="float:left;width:100%;" placeholder="예) 10.0.0.100"/></span>
							<span style="float:left;width:10%;text-align: center;">&nbsp; &ndash; &nbsp;</span>
							<span style="float:left;width:45%;"><input name="subnetStaticTo" id="subnetStaticTo" type="text" style="float:left;width:100%;" placeholder="예) 10.0.0.106"/></span>
						</div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">&bull;&nbsp;서브넷 범위</label>
					<div>
						<input name="subnetRange" type="text"  style="float:left;width:60%;"  required placeholder="예) 10.0.0.0/24"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">&bull;&nbsp;게이트웨이</label>
					<div>
						<input name="subnetGateway" type="text"  style="float:left;width:60%;"  required placeholder="예) 10.0.0.1"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">&bull;&nbsp;DNS</label>
					<div>
						<input name="subnetDns" type="text"  style="float:left;width:60%;"  required placeholder="예) 8.8.8.8"/>
					</div>
				</div>
			</div>
			<br />
			<div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: left;" onclick="saveNetworkInfo('before');" >이전</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveNetworkInfo('after');" >다음>></button>
			</div>
		</div>
	</div>
	
	<!-- Resource  설정 DIV -->
	<div id="resourceInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 3%;display:inline-block;width: 97%;">
	            <ul class="progressStep_6">
					<li class="pass">AWS 정보</li>
		            <li class="pass">기본 정보</li>
		            <li class="pass">네트워크 정보</li>
		            <li class="active">리소스 정보</li>
		            <li class="before">배포파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div style="margin:15px 1.5%;"><span class="glyphicon glyphicon-stop"></span>&nbsp; 리소스정보 설정</div>
			<div class="w2ui-page page-0" style="padding-left: 5%;">
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">&bull;&nbsp;스템셀</label>
					<div>
						<div>
							<input type="list" name="stemcells" style="float: left;width:60%;margin-top:1.5px;"  required placeholder="스템셀을 선택하세요.">
						</div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">&bull;&nbsp;인스턴스 유형</label>
					<div>
						<input name="cloudInstanceType" type="text" style="float:left;width:60%;"  required placeholder="인스턴스 유형을 입력하세요."/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 40%; font-size: 11px;">&bull;&nbsp;VM Password</label>
					<div>
						<input name="boshPassword" type="text" style="float:left;width:60%;"  required placeholder="VM 비밀번호를 입력하세요."/>
					</div>
				</div>
			</div>
			<br />
			<div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: left;" onclick="saveAwsResourceInfo('before');"  >이전</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveAwsResourceInfo('after');"  >다음>></button>
			</div>
		</div>
	</div>
	
	<div id="awsDeployDiv"  hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 3%;display:inline-block;width: 97%;">
	            <ul class="progressStep_6" >
		            <li class="pass">AWS 정보</li>
		            <li class="pass">BOSH 정보</li>
		            <li class="pass">네트워크 정보</li>
		            <li class="pass">리소스 정보</li>
		            <li class="active">배포파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div style="width:95%;height:84%;float: left;display: inline-block;">
				<textarea id="deployInfo" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:2%" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" style="float: left;" onclick="awsResourcePopup();">이전</button>
			<button class="btn" style="float: right; padding-right: 15%" onclick="boshDeploy('after');">다음>></button>
		</div>
	</div>
	
	<!-- AWS 설치화면 -->
	<div id="awsInstallDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 3%;display:inline-block;width: 97%;">
	            <ul class="progressStep_6">
		            <li class="pass">AWS 정보</li>
		            <li class="pass">기본 정보</li>
		            <li class="pass">네트워크 정보</li>
		            <li class="pass">리소스 정보</li>
		            <li class="pass">배포파일 정보</li>
		            <li class="active">설치</li>
	            </ul>
	        </div>
			<div style="width:95%;height:84%;float: left;display: inline-block;">
				<textarea id="installLogs" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:1%" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" style="float: left;" onclick="deployPopup();">이전</button>
			<button class="btn" style="float: right; padding-right: 15%" onclick="popupComplete();">닫기</button>
		</div>		
	</div>	
	<!-- End AWS Popup -->

	<!-- Start Bosh OPENSTACK POP -->
	<!-- 오픈스택 정보 -->
	<div id="openstackInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 3%;display:inline-block;width: 97%;">
	            <ul class="progressStep_6" >
		            <li class="active">오픈스택 정보</li>
		            <li class="before">기본 정보</li>
		            <li class="before">네트워크 정보</li>
		            <li class="before">리소스 설정</li>
		            <li class="before">배포파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div style="margin:15px 1.5%;"><span class="glyphicon glyphicon-stop"></span>&nbsp; 오픈스택정보 설정</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
		        <div class="w2ui-field">
		            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;AUTH URL</label>
		            <div>
		                <input name="authUrl" type="text"  style="float:left;width:60%;"  required placeholder="Identify API 인증 링크를 입력하세요."/>
		            </div>
		        </div>
		        
		        <div class="w2ui-field">
		            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;Tenant</label>
		            <div>
		                <input name="tenant" type="text"  style="float:left;width:60%;"  required placeholder="Tenant명을 입력하세요."/>
		            </div>
		        </div>
		        
		        <div class="w2ui-field">
		            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;User Name</label>
		            <div>
		                <input name="userName" type="text"  style="float:left;width:60%;"  required placeholder="계정명을 입력하세요."/>
		            </div>
		        </div>
		        
		        <div class="w2ui-field">
		            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;API Key</label>
		            <div>
		                <input name="apiKey" type="text"  style="float:left;width:60%;"  required placeholder="계정 비밀번호를 입력하세요."/>
		            </div>
		        </div>
		        
		        <div class="w2ui-field">
		            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;Security Group</label>
		            <div>
		                <input name="defaultSecurityGroups" type="text"  style="float:left;width:60%;"  required placeholder="시큐리티 그룹을 입력하세요."/>
		            </div>
		        </div>
		        
		        <div class="w2ui-field">
		            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;Private Key Name</label>
		            <div>
		                <input name="privateKeyName" type="text"  style="float:left;width:60%;"  required placeholder="Key Pair명을 입력하세요."/>
		            </div>
		        </div>
		        
		        <div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;Private Key Path</label>
					<div >
						<span onclick="changeKeyPathType('file');" style="width:30%;"><label><input type="radio" name="keyPathType" value="file" />&nbsp;파일업로드</label></span>
						&nbsp;&nbsp;
	  					<span onclick="changeKeyPathType('list');" style="width:30%;"><label><input type="radio" name="keyPathType" value="list" />&nbsp;목록에서 선택</label></span>
					</div>
		        </div>
		        
		        <div class="w2ui-field">			         	
	                <input name="privateKeyPath" type="text" style="width:200px;" hidden="true" onclick="openBrowse();" />
		            <label style="text-align: left;width:40%;font-size:11px;" class="control-label"></label>
					<div id="keyPathDiv" ></div>
		        </div>
		        
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveOpenstackInfo();"  >다음>></button>
		    </div>
		</div>
	</div>
	
	<!-- 기본 정보 -->
	<div id="osBoshInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 3%;display:inline-block;width: 97%;">
	            <ul class="progressStep_6" >
		            <li class="pass">오픈스택 정보</li>
		            <li class="active">기본 정보</li>
		            <li class="before">네트워크 정보</li>
		            <li class="before">리소스 정보</li>
		            <li class="before">배포파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div style="margin:15px 1.5%;"><span class="glyphicon glyphicon-stop"></span>&nbsp; 기본정보 설정</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
		    	<form id="osBoshForm">
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;배포명</label>
			            <div>
			                <input name="deploymentName" type="text"  style="float:left;width:60%;"  required placeholder="배포명을 입력하세요."/>
			                <div class="isMessage"></div>
			            </div>
			        </div>
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;설치관리자 UUID</label>
			            <div>
			                <input name="directorUuid" type="text"  style="float:left;width:60%;"  required placeholder="설치관리자 UUID입력하세요."/>
			                <div class="isMessage"></div>
			            </div>
			        </div>
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;BOSH 릴리즈</label>
			            <div>
			                <input name="releaseVersion" type="list"  style="float:left;width:60%;" required placeholder="BOSH 릴리즈를 선택하세요." />
			            </div>
			        </div>
		        </form>
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
		    	<button class="btn" style="float: left;" onclick="saveOsBoshInfo('before');">이전</button>
		        <button class="btn" style="float: right;padding-right:15%" onclick="saveOsBoshInfo('after');">다음>></button>
		    </div>
		</div>
	</div>	
	
	<!-- 네트워크 정보 -->
	<div id="osNetworkInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 3%;display:inline-block;width: 97%;">
	            <ul class="progressStep_6" >
		            <li class="pass">오픈스택 정보</li>
		            <li class="pass">기본 정보</li>
		            <li class="active">네트워크 정보</li>
		            <li class="before">리소스 정보</li>
		            <li class="before">배포파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div style="margin:15px 1.5%;"><span class="glyphicon glyphicon-stop"></span>&nbsp; 네트워크정보 설정</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
		    
		        <div class="w2ui-field">
		            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;서브넷 ID(NET ID)</label>
		            <div>
		                <input name="subnetId" type="text"  style="float:left;width:60%;" required placeholder="예) subnet-XXXXXX"/>
		                <div class="isMessage"></div>
		            </div>
		        </div>
				<div class="w2ui-field">
		            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;디렉터 공인 IP</label>
		            <div>
		                <input name="publicStaticIp" type="text"  style="float:left;width:60%;" required placeholder="설치관리자에 할당할 디렉터 공인 IP를 입력하세요."/>
		                <div class="isMessage"></div>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;서브넷 범위</label>
		            <div>
		                <input name="subnetRange" type="text"  style="float:left;width:60%;" required placeholder="예) 10.0.0.0/24"/>
		                <div class="isMessage"></div>
		            </div>
		        </div>
		        
		        <div class="w2ui-field">
		            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;게이트웨이</label>
		            <div>
		                <input name="subnetGateway" type="text"  style="float:left;width:60%;" required placeholder="예) 10.0.0.1"/>
		                <div class="isMessage"></div>
		            </div>
		        </div>
		        
		        <div class="w2ui-field">
		            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;DNS</label>
		            <div>
		                <input name="subnetDns" type="text"  style="float:left;width:60%;"  required placeholder="예) 8.8.8.8"/>
		                <div class="isMessage"></div>
		            </div>
		        </div>
		        <div class="w2ui-field">
					<label style="text-align:left; width:40%; font-size: 11px;">&bull;&nbsp;VM 할당 IP대역</label>
					<div>
						<div style="display:inline-block;width: 60%;">
							<span style="float:left;width:45%;"><input name="subnetStaticFrom" id="subnetStaticFrom" type="text" style="float:left;width:100%;" placeholder="예) 10.0.0.100"/></span>
							<span style="float:left;width:10%;text-align: center;">&nbsp; &ndash; &nbsp;</span>
							<span style="float:left;width:45%;"><input name="subnetStaticTo" id="subnetStaticTo" type="text" style="float:left;width:100%;" placeholder="예) 10.0.0.106"/></span>
						</div>
					</div>
				</div>
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: left;" onclick="saveOsNetworkInfo('before');" >이전</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveOsNetworkInfo('after');" >다음>></button>
		    </div>
		</div>
	</div>
	
	<!-- 리소스 정보 -->
	<div id="osResourceInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 3%;display:inline-block;width: 97%;">
	            <ul class="progressStep_6" >
		            <li class="pass">오픈스택 정보</li>
		            <li class="pass">기본 정보</li>
		            <li class="pass">네트워크 정보</li>
		            <li class="active">리소스 정보</li>
		            <li class="before">배포파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div style="margin:15px 1.5%;"><span class="glyphicon glyphicon-stop"></span>&nbsp; 리소스정보 설정</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
				 <div class="w2ui-field">
		            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;스템셀</label>
		            <div>
						<div><input type="list" name="stemcells" style="float: left;width:60%;margin-top:1.5px;"  required placeholder="스템셀을 선택하세요."></div>
						<div class="isMessage"></div>
					</div>
				</div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;인스턴스 유형</label>
		            <div>
		                <input name="cloudInstanceType" type="text"  style="float:left;width:60%;"   required  placeholder="인스턴스 유형을 입력하세요."/>
		                <div class="isMessage"></div>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;VM Password</label>
		            <div>
		                <input name="boshPassword" type="text"  style="float:left;width:60%;"  required  placeholder="VM인스턴스의 비밀번호를 입력하세요."/>
		                <div class="isMessage"></div>
		            </div>
		        </div>
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: left;" onclick="saveOsResourceInfo('before');">이전</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveOsResourceInfo('after');">다음>></button>
		    </div>
		</div>
	</div>
	
	<!-- 배포파일 정보 -->
	<div id="openstackDeployDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 3%;display:inline-block;width: 97%;">
	            <ul class="progressStep_6" >
		            <li class="pass">오픈스택 정보</li>
		            <li class="pass">기본 정보</li>
		            <li class="pass">네트워크 정보</li>
		            <li class="pass">리소스 정보</li>
		            <li class="active">배포파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div style="width:95%;height:84%;float: left;display: inline-block;">
				<textarea id="deployInfo" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:2%" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" style="float: left;" onclick="osResourceInfoPopup();">이전</button>
			<button class="btn" style="float: right; padding-right: 15%" onclick="boshDeploy('after');">다음>></button>
		</div>
	</div>
	
	<!-- 오픈스택 설치화면 -->
	<div id="openstackInstallDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
			<div style="margin-left: 3%;display:inline-block;width: 97%;">
	            <ul class="progressStep_6">
		            <li class="pass">오픈스택 정보</li>
		            <li class="pass">기본 정보</li>
		            <li class="pass">네트워크 정보</li>
		            <li class="pass">리소스 정보</li>
		            <li class="pass">배포파일 정보</li>
		            <li class="active">설치</li>
	            </ul>
	        </div>
			<div style="width:95%;height:84%;float: left;display: inline-block;">
				<textarea id="installLogs" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:1%" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" style="float: left;" onclick="deployPopup()">이전</button>
			<button class="btn" style="float: right; padding-right: 15%" onclick="popupComplete();">닫기</button>
		</div>
	</div>	