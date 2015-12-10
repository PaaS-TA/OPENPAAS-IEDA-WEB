<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" type="text/css" href="/css/progress-step.css"/>
<style type="text/css">
.w2ui-popup .w2ui-msg-body{background-color: #FFF; }
</style>
<script type="text/javascript" src="/js/sockjs-0.3.4.js"></script>
<script type="text/javascript" src="/js/stomp.js"></script>
<script type="text/javascript">
//private var
var boshId = "";
var awsInfo = "";
var boshInfo = "";
var networkInfo = "";
var resourcesInfo = "";

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
		awsPopup();
	});
	
	//Bosh 수정
	$("#modifyBtn").click(function(){
		if($("#modifyBtn").attr('disabled') == "disabled") return;
		w2confirm({
			title 	: "BOSH 수정",
			msg		: "BOSH 를 수정하시겠습니까?",
			yes_text: "확인",
			yes_callBack : function(envent){
				//ajax data call
				var selected = w2ui['config_boshGrid'].getSelection();
				console.log("modify Click!!!");
				if( selected.length == 0 ){
					w2alert("선택된 정보가 없습니다.", "BOSH 수정");
					return;
				}
				else{
					var record = w2ui['config_boshGrid'].get(selected);
					console.log(record.iaas);
					if(record.iaas == "AWS") getBoshAwsData(record);
					//else getBootstrapOpenstackData(record);
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
			w2alert(errorResult.message, "BOOTSTRAP 수정");
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

function awsPopup(){
	$("#awsInfoDiv").w2popup({
		width	: 650,
		height	: 400,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
				if(awsInfo != ""){
					$(".w2ui-msg-body input[name='accessKeyId']").val(awsInfo.accessKeyId);
					$(".w2ui-msg-body input[name='secretAccessKey']").val(awsInfo.secretAccessKey);
					$(".w2ui-msg-body input[name='defaultKeyName']").val(awsInfo.defaultKeyName);
					$(".w2ui-msg-body input[name='defaultSecurityGroups']").val(awsInfo.defaultSecurityGroups);
					$(".w2ui-msg-body input[name='region']").val(awsInfo.region);
				}
			}
		},
		onClose : initSetting
	});
}

//AWS POPUP NEXT BUTTON EVENT
function saveAwsInfo(){
	//AwsInfo Save
	awsInfo = {
			iaas					: "AWS",
			accessKeyId 			: $(".w2ui-msg-body input[name='accessKeyId']").val(),
			secretAccessKey			: $(".w2ui-msg-body input[name='secretAccessKey']").val(),
			defaultKeyName			: $(".w2ui-msg-body input[name='defaultKeyName']").val(),
			defaultSecurityGroups	: $(".w2ui-msg-body input[name='defaultSecurityGroups']").val(),
			region					: $(".w2ui-msg-body input[name='region']").val()
	}
	//ajax AwsInfo Save
	$.ajax({
		type : "PUT",
		url : "/deploy/bosh/saveAwsInfo",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		data : JSON.stringify(awsInfo), 
		success : function(data, status) {
			boshId = data;
			boshInfoPopup();
		},
		error : function( e, status ) {
			w2alert("AWS 설정 등록에 실패 하였습니다.", "Bosh 설치");
		}
	});
}

function settingAWSData(contents){
	boshId = contents.id;
	awsInfo = {
			iaas		 			: "AWS",
			accessKeyId				: contents.accessKeyId,
			secretAccessKey			: contents.secretAccessKey,
			defaultKeyName			: contents.defaultKeyName,
			defaultSecurityGroups	: contents.defaultSecurityGroups,
			region					: contents.region
	}
	boshInfo = {
			id				: boshId,
			boshName 		: contents.boshName,
			directorUuid	: contents.directorUuid,
			releaseVersion	: contents.releaseVersion
	}
	networkInfo = {
			id					: boshId,			
			subnetReserved		: contents.subnetReserved,
			subnetStatic		: contents.subnetStatic,
			publicStaticIps		: contents.publicStaticIps,
			subnetRange			: contents.subnetRange,
			subnetGateway		: contents.subnetGateway,
			subnetDns			: contents.subnetDns,
			cloudSubnet			: contents.cloudSubnet,
			cloudSecurityGroups	: contents.cloudSecurityGroups
	}
	resourcesInfo = {
			id				: contents.id,
			stemcellName	: contents.stemcellName,
			stemcellVersion	: contents.stemcellVersion,
			boshPassword	: contents.boshPassword
	}
	awsPopup();	
}


function  boshInfoPopup(){
	$("#boshInfoDiv").w2popup({
		width	: 650,
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
		url : "/deploy/bosh/saveBoshInfo",
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
		width	: 650,
		height	: 510,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
				if(networkInfo != ""){
					$(".w2ui-msg-body input[name='subnetReserved']").val(networkInfo.subnetReserved);
					$(".w2ui-msg-body input[name='subnetStatic']").val(networkInfo.subnetStatic);
					$(".w2ui-msg-body input[name='publicStaticIps']").val(networkInfo.publicStaticIps);
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
			subnetReserved		: $(".w2ui-msg-body input[name='subnetReserved']").val(),
			subnetStatic		: $(".w2ui-msg-body input[name='subnetStatic']").val(),
			publicStaticIps		: $(".w2ui-msg-body input[name='publicStaticIps']").val(),
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
		url : "/deploy/bosh/saveBoshInfo",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		data : JSON.stringify(boshInfo), 
		success : function(data, status) {
			resourcesPopup();
		},
		error : function( e, status ) {
			w2alert("Bosh Network 등록에 실패 하였습니다.", "Bosh 설치");
		}
	});
	
}

function resourcesPopup(){
	$("#resourcesInfoDiv").w2popup({
		width	: 650,
		height	: 350,
		modal	: true,
		onOpen:function(event){
			event.onComplete = function(){				
				if(resourcesInfo != ""){
					$(".w2ui-msg-body input[name='stemcellName']").val(resourcesInfo.stemcellName);
					$(".w2ui-msg-body input[name='stemcellVersion']").val(resourcesInfo.stemcellVersion);
					//$(".w2ui-msg-body input[name='cloudInstanceType']").val(resourcesInfo.cloudInstanceType);
					$(".w2ui-msg-body input[name='boshPassword']").val(resourcesInfo.boshPassword);
				}
			}
		},
		onClose : initSetting
	});
}

function saveResourcesInfo(param){
	resourcesInfo = {
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
		
	//Server send Bosh Info
	deployPopup();
}


function deployPopup(){
	$("#deployManifestDiv").w2popup({
		width 	: 650,
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
	/* $.ajax({
		type : "POST",
		url : "/bootstrap/getBootstrapDeployInfo",
		contentType : "application/json",
		//dataType: "json",
		async : true, 
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
	}); */
}

function saveDeployInfo(param){
	//Deploy 단에서 저장할 데이터가 있는지 확인 필요
	//Confirm 설치하시겠습니까?
	if(param == 'before'){
		resourcesPopup();
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
	$("#installDiv").w2popup({
		width : 650,
		height : 470,
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

function initSetting(){
	boshId = "";
	awsInfo = "";
	boshInfo = "";
	networkInfo = "";
	resourcesInfo = "";
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

<!-- Start Popup Resion -->

	<!-- AWS  설정 DIV -->
	<div id="awsInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width:100%;padding:15px 5px 15px 5px;">
			<div rel="sub-title" >
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
		    	<div class="w2ui-field" hidden="true">
		            <label>Iaas</label>
		            <div>
		                <input name="iaas" type="text" maxlength="100" />
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">AWS 접근 키 아이디</label>
		            <div>
		                <input name="accessKeyId" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">AWS 접근 키 암호</label>
		            <div>
		                <input name="secretAccessKey" type="password" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">VPC 접속 키 명</label>
		            <div>
		                <input name="defaultKeyName" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">VPC 접속 보안 정책 그룹명</label>
		            <div>
		                <input name="defaultSecurityGroups" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">VPC 네트워크 설정 지역</label>
		            <div>
		                <input name="region" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
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
		<div rel="body" style="width:100%;padding:15px 5px 15px 5px;">
			<div rel="sub-title" >
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
		                <input name="releaseVersion" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
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
		<div rel="body" style="width:100%;padding:15px 5px 15px 5px;">
			<div >
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
					<label style="text-align: left; width: 200px; font-size: 11px;">할당금지 IP 대역</label>
					<div>
						<input name="subnetReserved" type="text" maxlength="100" size="30" style="float:left;width:330px;"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">배열	고정 IP 대역</label>
					<div>
						<input name="subnetStatic" type="text" maxlength="100" size="30" style="float:left;width:330px;"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">배열	공인 IP</label>
					<div>
						<input name="staticIps" type="text" maxlength="100" size="30" style="float:left;width:330px;"/>
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
	
	<!-- Resources  설정 DIV -->
	<div id="resourcesInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width:100%;padding:15px 5px 15px 5px;">
			<div >
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
				<button class="btn" style="float: left;" onclick="saveResourcesInfo('before');">이전</button>
				<button class="btn" onclick="popupComplete();">취소</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveResourcesInfo('after');">다음>></button>
			</div>
		</div>
	</div>
	
	<div id="deployManifestDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width:100%;padding:15px 5px 15px 5px;">
			<div >
	            <ul class="progressStep_6">
		            <li class="pass">AWS 설정</li>
		            <li class="pass">Bosh Info 설정</li>
		            <li class="pass">Network 설정</li>
		            <li class="pass">리소스 설정</li>
		            <li class="active">배포 Manifest</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" class="cont_title">▶ 배포 Manifest 정보</div>
			<div>
				<textarea id="deployInfo" style="width:95%;height:250px;overflow-y:visible;resize:none;background-color: #FFF;margin-left:1%" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" style="float: left;" onclick="saveDeployInfo('before');">이전</button>
			<button class="btn" onclick="popupComplete();">취소</button>
			<button class="btn" style="float: right; padding-right: 15%" onclick="saveDeployInfo('after');">다음>></button>
		</div>
	</div>
	
	<div id="installDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOSH 설치</b></div>
		<div rel="body" style="width:100%;padding:15px 5px 15px 5px;">
			<div >
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
			<div>
				<textarea id="installLogs" style="width:95%;height:250px;overflow-y:visible;resize:none;background-color: #FFF;margin-left:1%" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
				<!-- 설치 실패 시 -->
				<button class="btn" style="float: left;" onclick="saveDeployInfo('before');">이전</button>
				<button class="btn" onclick="popupComplete();">취소</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="popupComplete();">완료</button>
		</div>		
	</div>	
<!-- End Popup Resion -->