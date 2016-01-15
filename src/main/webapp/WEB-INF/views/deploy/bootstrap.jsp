<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" type="text/css" href="<c:url value='/css/progress-step.css'/>"/>
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

<script type="text/javascript" src="<c:url value='/js/sockjs-0.3.4.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/stomp.js'/>"></script>

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
			, {field: 'deployStatus', caption: '배포상태', size: '80px', 
				render: function(record) {
		    			if ( record.deployStatus == 'done' )
		    				return '<span class="btn btn-primary" style="width:60px">성공</span>';
		    			else	if ( record.deployStatus == 'error' )
		    				return '<span class="btn btn-danger" style="width:60px">오류</span>';
		    			else	if ( record.deployStatus == 'cancelled' )
		    				return '<span class="btn btn-danger" style="width:60px">취소</span>';
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
		       				return '<span id="" class="btn btn-primary" style="width:60px" onClick="getDeployLogMsg( \'bootstrap\', \''+record.iaas+'\', \''+record.id+'\');">로그보기</span>';
						}
		    			else {
		    				return 'N/A';
						}
					}
				}
			, {field: 'deploymentName', caption: '배포명', size: '120px'}
			, {field: 'directorName', caption: '디렉터 명', size: '100px'}
			, {field: 'iaas', caption: 'IaaS', size: '100px'}
			, {field: 'boshRelease', caption: 'BOSH 릴리즈', size: '100px'}
			, {field: 'boshCpiRelease', caption: 'BOSH CPI 릴리즈', size: '200px'}
			, {field: 'subnetId', caption: '서브넷 ID(NET ID)', size: '100px'}
			, {field: 'subnetRange', caption: '서브넷 범위', size: '100px'}
			, {field: 'publicStaticIp', caption: '디렉터 공인 IP', size: '100px'}
			, {field: 'privateStaticIp', caption: '디렉터 내부 IP', size: '100px'}
			, {field: 'subnetGateway', caption: '게이트웨이', size: '100px'}
			, {field: 'subnetDns', caption: 'DNS', size: '100px'}
			, {field: 'ntp', caption: 'NTP', size: '100px'}
			, {field: 'stemcell', caption: '스템셀', size: '320px'}
			, {field: 'instanceType', caption: '인스턴스 유형', size: '100px'}
			, {field: 'boshPassword', caption: 'VM 비밀번호', size: '100px'}
			, {field: 'deploymentFile', caption: '배포파일명', size: '150px'}
			, {field: 'createdDate', caption: '생성일자', size: '100px'}
			, {field: 'updatedDate', caption: '수정일자', size: '100px'}
			
			],
		onClick:function(event) {
			var grid = this;
			event.onComplete = function() {
				var sel = grid.getSelection();
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
		
		var selected = w2ui['config_bootstrapGrid'].getSelection();
		var record = w2ui['config_bootstrapGrid'].get(selected);
		
		var message = "";
		
		if ( record.deploymentName )
			message = "BOOTSTRAP (배포명 : " + record.deploymentName + ")를 삭제하시겠습니까?";
		else
			message = "선택된 BOOTSTRAP을 삭제하시겠습니까?";
		
		w2confirm({
			title 	: "BOOTSTRAP 삭제",
			msg		: "BOOTSTRAP 정보를 삭제하시겠습니까?",
			yes_text: "확인",
			yes_callBack : function(event){
					deletePop(record);						
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
			stemcell			: contents.stemcell,
			boshPassword		: contents.boshPassword,
			cloudInstanceType	: contents.cloudInstanceType
	}
	
	openstackPopup();
			
}

//BOOTSTRAP 삭제 실행
function deletePop(record){
	
	var requestParameter = { id:record.id,iaas:record.iaas};
	
	if ( record.deployStatus == null || record.deployStatus == '' ) {
		// 단순 레코드 삭제
		var url = "/bootstrap/delete";
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
				w2alert(errorResult.message, "BOOTSTRAP 삭제");
			}
		});
		
	} else {
		
		var message = "BOOTSTRAP";
		var body = '<textarea id="deleteLogs" style="width:95%;height:90%;overflow-y:visible;resize:none;background-color: #FFF; margin:2%" readonly="readonly"></textarea>';
		
		w2popup.open({
			width   : 610,
			height  : 500,
			title   : "<b>BOOTSTRAP 삭제</b>",
			body    : body,
			buttons : '<button class="btn" style="float: right; padding-right: 15%;" onclick="popupComplete();">닫기</button>',
			modal   : true,
			showMax : true,
			onOpen  : function(event){
				event.onComplete = function(){
					var socket = new SockJS('/bootstrapDelete');
					deleteClient = Stomp.over(socket);
 					deleteClient.connect({}, function(frame) {
						deleteClient.subscribe('/bootstrap/bootstrapDelete', function(data){
							
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
									w2alert(message, "BOOTSTRAP 삭제");
						       	}
				        	}
				        });
						deleteClient.send('/send/bootstrapDelete', {}, JSON.stringify(requestParameter));
				    });
				}
			},
			onClose : function (event){
				event.onComplete= function(){
					w2ui['config_bootstrapGrid'].reset();
					//deleteClient.disconnect();
					deleteClient = "";
					doSearch();
				}
			} 
		});
	}		
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
		url : "/release/localBoshOpenstackCpiList",
		contentType : "application/json",
		async : true,
		success : function(data, status) {
			$('#w2ui-popup input[name=boshCpiRelease][type=list]').w2field('list', { items: data , maxDropHeight:200, width:250});
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
		url : "/release/localBoshList",
		contentType : "application/json",
		async : true,
		success : function(data, status) {
			boshReleases = data;
			$('#w2ui-popup input[name=boshRelease][type=list]').w2field('list', { items: data , maxDropHeight:200, width:250});
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
	fileUploadInput += '<input type="text" id="keyPathFileName" style="width:40%;" readonly  onClick="openBrowse();" placeholder="업로드할 Key 파일을 선택하세요."/>';
	fileUploadInput += '<a href="#" id="browse" onClick="openBrowse();">Browse </a></span>';
	var selectInput = '<input type="list" name="keyPathList" style="float: left;width:60%;" onchange="setPrivateKeyPath(this.value);" placeholder="Private Key 파일을 선택하세요."/>';
	
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
	
	if(popupValidation()){
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
		onClose : popupClose,
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

function saveAwsDefaultInfo(type){
	defaultInfo = {
			id				: bootstrapId,
			deploymentName 	: $(".w2ui-msg-body input[name='deploymentName']").val(),
			directorName 	:$(".w2ui-msg-body input[name='directorName']").val(),
			boshRelease 	:$(".w2ui-msg-body input[name='boshRelease']").val(),
			boshCpiRelease 	:$(".w2ui-msg-body input[name='boshCpiRelease']").val()
	}
	
	if( type == 'after'){
		if(popupValidation()){
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

//Network Info Setting Popup
function awsNetworkPopup(){
	$("#awsNetworkInfoDiv").w2popup({
		width : 670,
		height : 600,
		modal	: true,
		onClose : popupClose,
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
		if(popupValidation()){
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
	}
}

function awsResourcePopup(){
	console.log("resorce POPUP!!");
	$("#awsResourceInfoDiv").w2popup({
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
	var url = (iaas == "AWS") ? "/information/localAwsStemcells":"/information/localOpenstackStemcells";
	$.ajax({
		type : "GET",
		url : url,
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
		if(popupValidation()){
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
	}
}

function deployPopup(){
	var deployDiv = (iaas == "AWS") ? $("#deployManifestDiv") : $("#osDeployManifestDiv");
	deployDiv.w2popup({
		width 	: 670,
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

function getDeployInfo(){
	$.ajax({
		type : "POST",
		url : "/common/getDeployInfo",
		contentType : "application/json",
		async : true,
		data : deployFileName,
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
			msg			: "BOOTSTRAP을 설치하시겠습니까?",
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
	var message = "BOOTSTRAP ";
	
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
						
			        	var response = JSON.parse(data.body);
			        	
			        	console.log(response.messages);
			        	
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
								w2alert(message, "BOOTSTRAP 설치");
					       	}
			        	}
			        });
			        installClient.send('/send/bootstrapInstall', {}, JSON.stringify(requestParameter));
			    });
			}
		},
		onClose : function(event){
			event.onComplete = function(){
				w2ui['config_bootstrapGrid'].reset();
				//installClient.disconnect();
				installClient = "";
				doSearch();
			}
		}
	});
}

//팝업창 닫을 경우
function initSetting(){
	iaas = "";
	bootstrapId= "";
	
	awsInfo = "";
	openstackInfo = "";
	osBoshInfo = "";
	networkInfo = "";
	resourceInfo = "";
	deployInfo = "";
	deployFileName = "";
	
	installClient = "";
	deleteClient = "";
}

function popupClose() {
	//params init
	initSetting();
	//grid Reload
	gridReload();
}

function popupComplete(){
	
	w2confirm({
		title 	: $(".w2ui-msg-title b").text(),
		msg		: $(".w2ui-msg-title b").text() + " 화면을 닫으시겠습니까?<BR>(닫은 후에도 완료되지 않는 설치 또는 삭제 작업은 계속 진행됩니다.)",
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

// Openstack Info Popup
function openstackPopup(){
	$("#openstackInfoDiv").w2popup({
		width : 670,
		height : 600,
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
	
	if(popupValidation()){		
		//SAVE
		$.ajax({
			type : "PUT",
			url : "/bootstrap/setOpenstackInfo",
			contentType : "application/json",
			async : true,
			data : JSON.stringify(openstackInfo),
			success : function(data, status) {
				bootstrapId = data.id;
				osBoshInfoPop();
			},
			error : function( e, status ) {
				console.log(e + "status ::: " + status);
				w2alert("OPENSTACK 설정 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
			}
		});
	}
}

function osBoshInfoPop(){
	$("#osBoshInfoDiv").w2popup({
		width : 670,
		height : 430,
		onClose : popupClose,
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
	console.log("!!!! "+ bootstrapId);
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
		if(popupValidation()){
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
					w2alert("오픈스택 기본정보 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
				}
			});
		}
	}
}


// Openstack Network Info Popup
function osNetworkInfoPopup(){
	$("#osNetworkInfoDiv").w2popup({
		width : 670,
		height : 600,
		onClose : popupClose,
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
		if(popupValidation()){		
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
}

//Openstack Resource Info Popup
function osResourceInfoPopup(){
	$("#osResourceInfoDiv").w2popup({
		width : 670,
		height : 400,
		onClose : popupClose,
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
			stemcell			: $(".w2ui-msg-body input[name='stemcell']").val(),
			boshPassword			: $(".w2ui-msg-body input[name='boshPassword']").val(),
			cloudInstanceType	: $(".w2ui-msg-body input[name='cloudInstanceType']").val()
	}
	
	if( type == "before"){
		osNetworkInfoPopup();
		return;
	}
	else{
		if(popupValidation()){
			$.ajax({
				type : "PUT",
				url : "/bootstrap/setOsResourceInfo",
				contentType : "application/json",
				async : true,
				data : JSON.stringify(resourceInfo),
				success : function(data, status) {
					deployFileName = data.deploymentFile;
					deployPopup();
				},
				error : function( e, status ) {
					w2alert("OPENSTACK Resource 설정 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
				}
			});
		}
	}
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
	<div class="page_site">플랫폼 설치 > <strong>BOOTSTRAP 설치</strong></div>
	
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
					<input type="radio" name="iaas" id="type1" value="AWS" checked="checked"  />
					&nbsp;AWS
				</label>
				<label style="width: 130px;margin-left:50px;">
					<input type="radio" name="iaas" id="type2" value="OPENSTACK"  />
					&nbsp;OPENSTACK
				</label>
			</div>
		</div>
	</div>

	<!-- AWS  설정 DIV -->
	<div id="awsDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:3%;display:inline-block;width:97%;">
	            <ul class="progressStep_6" >
		            <li class="active">AWS 정보</li>
		            <li class="before">기본 정보</li>
		            <li class="before">네트워크 정보</li>
		            <li class="before">리소스 정보</li>
		            <li class="before">배포 파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div style="margin:15px 1.5%;"><span class="glyphicon glyphicon-stop"></span>&nbsp; AWS 정보</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
		    	<form id="awsForm" data-toggle="validator" >
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;Access Key ID</label>
			            <div>
			                <input name="accessKeyId" type="text"  style="float:left;width:60%;"  required placeholder="AWS Access Key를 입력하세요."/>
			                <div class="isMessage"></div>			                
			            </div>
			        </div>
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;Secret Access Key</label>
			            <div>
			                <input name="secretAccessId" type="text"  style="float:left;width:60%;"  required placeholder="AWS Secret Access Key를 입력하세요."/>
			                <div class="isMessage"></div>
			            </div>
			        </div>
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;Security Group</label>
			            <div>
			                <input name="defaultSecurityGroups" type="text"  style="float:left;width:60%;"  required placeholder="시큐리티 그룹을 입력하세요."/>
			                <div class="isMessage"></div>
			            </div>
			        </div>
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;Region</label>
			            <div>
			                <input name="region" type="text"  style="float:left;width:60%;"  required placeholder="설치할 Region을 입력하세요.(예: us-east-1)"/>
			                <div class="isMessage"></div>
			            </div>
			        </div>
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;Availability Zone</label>
			            <div>
			                <input name="availabilityZone" type="text" style="display: inline-block;float:left;width:60%;" required placeholder="Availability Zone을 입력하세요."/>
			                <div class="isMessage"></div>
			            </div>
			        </div>
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;Private Key Name</label>
			            <div>
			                <input name="privateKeyName" type="text"  style="display: inline-block;float:left;width:60%;"  required placeholder="Key Pair 이름을 입력하세요."/>
			                <div class="isMessage"></div>
			            </div>
			        </div>
			        
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;Private Key File</label>
		                <div >
	  						<span onclick="changeKeyPathType('file');" style="width:30%;"><label><input type="radio" name="keyPathType" value="file" />&nbsp;파일업로드</label></span>
							&nbsp;&nbsp;
							<span onclick="changeKeyPathType('list');" style="width:30%;"><label><input type="radio" name="keyPathType" value="list" />&nbsp;목록에서 선택</label></span>
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
		        <button class="btn" style="float: right;padding-right:15%" onclick="saveAwsInfo();" >다음>></button>
		    </div>
		</div>
	</div>
	
	<div id="awsDefaultDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:3%;display:inline-block;width:97%;">
	            <ul class="progressStep_6" >
		            <li class="pass">AWS 정보</li>
		            <li class="active">기본 정보</li>
		            <li class="before">네트워크 정보</li>
		            <li class="before">리소스 정보</li>
		            <li class="before">배포 파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div style="margin:15px 1.5%;"><span class="glyphicon glyphicon-stop"></span>&nbsp; 기본 정보</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
		    	<form id="awsDefaultForm" data-toggle="validator" >
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;배포명</label>
			            <div>
			                <input name="deploymentName" type="text"  style="float:left;width:60%;"  required placeholder="배포명을 입력하세요."/>
			                <div class="isMessage"></div>
			            </div>
			        </div>
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;디렉터 명</label>
			            <div>
			                <input name="directorName" type="text"  style="float:left;width:60%;"  required placeholder="디렉터 명을 입력하세요."/>
			                <div class="isMessage"></div>
			            </div>
			        </div>
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;BOSH 릴리즈</label>
			            <div>
			                <input name="boshRelease" type="list"  style="float:left;width:60%;"  required placeholder="BOSH 릴리즈를 선택하세요"/>
			                <!-- <br/>
			                <div class="isMessage"></div> -->
			            </div>
			        </div>
			        <div class="w2ui-field">
			            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;BOSH CPI 릴리즈</label>
			            <div>
			                <input name="boshCpiRelease" type="list"  style="float:left;width:60%;"  required placeholder="BOSH CPI 릴리즈를 선택하세요"/>
			                <!-- <div class="isMessage"></div> -->
			            </div>
			        </div>			        
		        </form>
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
		    	<button class="btn" style="float: left;" onclick="saveAwsDefaultInfo('before');" >이전</button>
		        <button class="btn" style="float: right;padding-right:15%" onclick="saveAwsDefaultInfo('after');" >다음>></button>
		    </div>
		</div>
	</div>
	
	<!-- 네트워크  설정 DIV -->
	<div id="awsNetworkInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:3%;display:inline-block;width:97%;">
	            <ul class="progressStep_6">
		            <li class="pass">AWS 정보</li>
		            <li class="pass">기본 정보</li>
		            <li class="active">네트워크 정보</li>
		            <li class="before">리소스 정보</li>
		            <li class="before">배포 파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div style="margin:15px 1.5%;"><span class="glyphicon glyphicon-stop"></span>&nbsp;네트워크 정보</div>
			<div class="w2ui-page page-0" style="padding-left: 5%;">
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;서브넷 ID(NET ID)</label>
					<div>
						<input name="subnetId" type="text"  style="float:left;width:330px;"  placeholder="예) subnet-XXXXXX"/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;디렉터 내부 IP</label>
					<div>
						<input name="privateStaticIp" type="text"  style="float:left;width:330px;" placeholder="설치관리자에 할당할 디렉터 내부 IP를 입력하세요."/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;디렉터 공인 IP</label>
					<div>
						<input name="publicStaticIp" type="text"  style="float:left;width:330px;" required placeholder="설치관리자에 할당할 디렉터 공인 IP를 입력하세요."/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;서브넷 범위</label>
					<div>
						<input name="subnetRange" type="text"  style="float:left;width:330px;" placeholder="예) 10.0.0.0/24"/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;게이트웨이</label>
					<div>
						<input name="subnetGateway" type="text"  style="float:left;width:330px;" placeholder="예) 10.0.0.1"/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;DNS</label>
					<div>
						<input name="subnetDns" type="text"  style="float:left;width:330px;" placeholder="예) 8.8.8.8"/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;NTP</label>
					<div>
						<input name="ntp" type="text"  style="float:left;width:330px;" placeholder="예) 10.0.0.2"/>
						<div class="isMessage"></div>
					</div>
				</div>
			</div>
			<br />
			<div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: left;" onclick="saveAwsNetworkInfo('before');" >이전</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveAwsNetworkInfo('after');" >다음>></button>
			</div>
		</div>
	</div>
	
	<!-- Resource  설정 DIV -->
	<div id="awsResourceInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:3%;display:inline-block;width:97%;">
	            <ul class="progressStep_6">
		            <li class="pass">AWS 정보</li>
		            <li class="pass">기본 정보</li>
		            <li class="pass">네트워크 정보</li>
		            <li class="active">리소스 정보</li>
		            <li class="before">배포 파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div style="margin:15px 1.5%;"><span class="glyphicon glyphicon-stop"></span>&nbsp; 리소스 정보</div>
			<div class="w2ui-page page-0" style="padding-left: 5%;">
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;스템셀</label>
					<div>
						<div>
							<input type="list" name="stemcell" style="float: left;width:330px;margin-top:1.5px;"  required placeholder="스템셀을 선택하세요.">
						</div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;인스턴스 유형</label>
					<div>
						<input name="cloudInstanceType" type="text"  style="float:left;width:330px;" required placeholder="인스턴스 유형을 입력하세요."/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;VM 패스워드</label>
					<div>
						<input name="boshPassword" type="text"  style="float:left;width:330px;"  required placeholder="VVM 패스워드를 입력하세요."/>
						<div class="isMessage"></div>
					</div>
				</div>
			</div>
			<br />
			<div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: left;" onclick="saveResourceInfo('before');" >이전</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveResourceInfo('after');" >다음>></button>
			</div>
		</div>
	</div>
	
	<!-- AWS Deploy DIV -->
	<div id="deployManifestDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:3%;display:inline-block;width:97%;">
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
			<button class="btn" style="float: right; padding-right: 15%" onclick="confirmDeploy('after');">다음>></button>
		</div>
	</div>
	
	<!-- AWS Install DIV -->
	<div id="installDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:3%;display:inline-block;width:97%;">
	            <ul class="progressStep_6">
		            <li class="pass">AWS 정보</li>
		            <li class="pass">기본 정보</li>
		            <li class="pass">네트워크 정보</li>
		            <li class="pass">리소스 정보</li>
		            <li class="pass">배포 파일 정보</li>
		            <li class="active">설치</li>
	            </ul>
	        </div>
			<div style="width:95%;height:84%;float: left;display: inline-block;">
				<textarea id="installLogs" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:2%" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" id="deployPopupBtn" style="float: left;" onclick="deployPopup();" disabled>이전</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="popupComplete();">닫기</button>
		</div>		
	</div>	
<!-- End AWS Popup -->

<!-- Start OPENSTACK POPUP  -->
	<div id="openstackInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:3%;display:inline-block;width:97%;">
	            <ul class="progressStep_6" >
		            <li class="active">오픈스텍 정보</li>
		            <li class="before">기본 정보</li>
		            <li class="before">네트워크 정보</li>
		            <li class="before">리소스 정보</li>		            
		            <li class="before">배포 파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div style="margin:15px 1.5%;"><span class="glyphicon glyphicon-stop"></span>&nbsp; OPENSTACK 정보</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;AUTH URL</label>
					<div>
						<input name="authUrl" type="text"  style="float:left;width:60%;"  required placeholder="Identify API 인증 링크를 입력하세요."/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;Tenant</label>
					<div>
						<input name="tenant" type="text"  style="float:left;width:60%;"  required placeholder="Tenant명을 입력하세요."/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;User Name</label>
					<div>
						<input name="userName" type="text"  style="float:left;width:60%;" required placeholder="계정명을 입력하세요."/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;API KEY</label>
					<div>
						<input name="apiKey" type="text"  style="float:left;width:60%;"   required placeholder="계정 비밀번호를 입력하세요."/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;Security Group</label>
					<div>
						<input name="defaultSecurityGroups" type="text"  style="float:left;width:60%;"  required placeholder="시큐리티 그룹을 입력하세요."/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;Private Key Name</label>
					<div>
						<input name="privateKeyName" type="text"  style="float:left;width:60%;"  required placeholder="Key Pair명을 입력하세요."/>
						<div class="isMessage"></div>
					</div>
				</div>
				<!-- privateKeyPath -->
				<div class="w2ui-field">
		            <label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;Private Key File</label>
	                <div >
  						<span onclick="changeKeyPathType('file');" style="width:30%;"><label><input type="radio" name="keyPathType" value="file" />&nbsp;파일업로드</label></span>
						&nbsp;&nbsp;
						<span onclick="changeKeyPathType('list');" style="width:30%;"><label><input type="radio" name="keyPathType" value="list" />&nbsp;목록에서 선택</label></span>
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
				<!-- <button class="btn" style="float: left;" onclick="popupComplete();">취소</button> -->
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveOpenstackInfo();">다음>></button>
		    </div>
		</div>
	</div>
	
	<div id="osBoshInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:3%;display:inline-block;width:97%;">
	            <ul class="progressStep_6" >
		            <li class="pass">오픈스텍 정보</li>
		            <li class="active">기본 정보</li>
		            <li class="before">네트워크 정보</li>
		            <li class="before">리소스 정보</li>		            
		            <li class="before">배포 파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div style="margin:15px 1.5%;"><span class="glyphicon glyphicon-stop"></span>&nbsp; 기본 정보</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
		    	<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;배포명</label>
					<div>
						<input name="deploymentName" type="text"  style="float:left;width:60%;" required placeholder="배포명을 입력하세요."/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;디렉터 명</label>
					<div>
						<input name="directorName" type="text"  style="float:left;width:60%;" required placeholder="디렉터 명을 입력하세요."/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;BOSH 릴리즈</label>
					<div>
						<input name="boshRelease" type="list"  style="float:left;width:60%;"  required placeholder="BOSH 릴리즈를 선택하세요."/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;BOSH CPI 릴리즈</label>
					<div>
						<input name="boshCpiRelease" type="list"  style="float:left;width:60%;" required placeholder="BOSH CPI 릴리즈를 선택하세요."/>
					</div>
				</div>
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
		    	<button class="btn" style="float: left;" onclick="saveOsBoshInfo('before');">이전</button>
		        <button class="btn" style="float: right;padding-right:15%" onclick="saveOsBoshInfo('after');" >다음>></button>
		    </div>
		</div>
	</div>	
	
	<div id="osNetworkInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:3%;display:inline-block;width:97%;">
	            <ul class="progressStep_6" >
		            <li class="pass">오픈스텍 정보</li>
		            <li class="pass">기본 정보</li>
		            <li class="active">네트워크 정보</li>
		            <li class="before">리소스 정보</li>		            
		            <li class="before">배포 파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div style="margin:15px 1.5%;"><span class="glyphicon glyphicon-stop"></span>&nbsp; 네트워크 정보</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
		    	<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;서브넷 ID(NET ID)</label>
					<div>
						<input name="subnetId" type="text"  style="float:left;width:330px;" required placeholder="예) subnet-XXXXXX"/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;디렉터 내부 IP</label>
					<div>
						<input name="privateStaticIp" type="text"  style="float:left;width:330px;" required placeholder="설치관리자에 할당할 디렉터 내부 IP를 입력하세요."/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;디렉터 공인 IP</label>
					<div>
						<input name="publicStaticIp" type="text"  style="float:left;width:330px;"  required placeholder="설치관리자에 할당할 디렉터 공인 IP를 입력하세요."/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;서브넷 범위</label>
					<div>
						<input name="subnetRange" type="text"  style="float:left;width:330px;"  required placeholder="예) 10.0.0.0/24"/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;게이트웨이</label>
					<div>
						<input name="subnetGateway" type="text"  style="float:left;width:330px;"  required placeholder="예) 10.0.0.1"/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;DNS</label>
					<div>
						<input name="subnetDns" type="text"  style="float:left;width:330px;" required placeholder="예) 8.8.8.8"/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;NTP</label>
					<div>
						<input name="ntp" type="text"  style="float:left;width:330px;"  required placeholder="예) 10.0.0.2"/>
						<div class="isMessage"></div>
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
	
	<div id="osResourceInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:3%;display:inline-block;width:97%;">
	            <ul class="progressStep_6" >
		            <li class="pass">오픈스텍 정보</li>
		            <li class="pass">기본 정보</li>
		            <li class="pass">네트워크 정보</li>
		            <li class="active">리소스 정보</li>		            
		            <li class="before">배포 파일 정보</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div style="margin:15px 1.5%;"><span class="glyphicon glyphicon-stop"></span>&nbsp; 리소스 정보</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
		    	<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;스템셀</label>
					<div>
						<div>
							<input type="list" name="stemcell" style="float: left;width:330px;margin-top:1.5px;"  required placeholder="스템셀을 선택하세요."/>
						</div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;인스턴스 유형</label>
					<div>
						<input name="cloudInstanceType" type="text"  style="float:left;width:330px;"  required placeholder="인스턴스 유형을 입력하세요."/>
						<div class="isMessage"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left;width:40%;font-size:11px;">&bull;&nbsp;VM 패스워드</label>
					<div>
						<input name="boshPassword" type="text"  style="float:left;width:330px;"  required placeholder="VM 패스워드를 입력하세요."/>
						<div class="isMessage"></div>
					</div>
				</div>
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
		        <button class="btn" style="float: left;" onclick="saveOsResourceInfo('before');" >이전</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveOsResourceInfo('after');" >다음>></button>
		    </div>
		</div>
	</div>
	
	<!-- OPENSTACK Deploy DIV -->
	<div id="osDeployManifestDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:3%;display:inline-block;width:97%;">
	            <ul class="progressStep_6" >
		            <li class="pass">오픈스텍 정보</li>
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
			<!-- <button class="btn" onclick="popupComplete();">취소</button> -->
			<button class="btn" style="float: right; padding-right: 15%" onclick="confirmDeploy('after');">다음>></button>
		</div>
	</div>
	
	<!-- OPENSTACK Install DIV -->
	<div id="osInstallDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:3%;display:inline-block;width:97%;">
	            <ul class="progressStep_6" >
		            <li class="pass">오픈스텍 정보</li>
		            <li class="pass">기본 정보</li>
		            <li class="pass">네트워크 정보</li>
		            <li class="pass">리소스 정보</li>		            
		            <li class="pass">배포 파일 정보</li>
		            <li class="active">설치</li>
	            </ul>
	        </div>
	        <div style="width:95%;height:84%;float: left;display: inline-block;">
				<textarea id="installLogs" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:2%" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
				<!-- 설치 실패 시 -->
				<button class="btn" id="deployPopupBtn" style="float: left;" onclick="deployPopup();" disabled>이전</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="popupComplete();">닫기</button>
		</div>		
	</div>	