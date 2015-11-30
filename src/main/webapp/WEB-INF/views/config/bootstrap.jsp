<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" type="text/css" href="/css/bootstrap-progress-step.css"/>
<style type="text/css">
.w2ui-popup .w2ui-msg-body{background-color: #FFF; }
</style>
<script type="text/javascript">

//private variable
var structureType = "";
var awsInfo = "";
var networkInfo = "";
var resourcesInfo = "";
var deployInfo = "";

//Title Tag
var popupTitle = '<b>BOOTSTRAP 설치</b>';

//Body Tag
var instrastructurebody = '<div id="bootSelectBody" style="width: 400px; height: 80px;" hidden="true">';
instrastructurebody += '<div class="w2ui-lefted" style="text-align: left;">설치할 Infrastructure 을 선택하세요<br/><br/></div>';
instrastructurebody += '<div class="row"><div class="col-sm-9"><div class="btn-group" data-toggle="buttons" style="left: 20%">';
instrastructurebody += '<label style="width: 130px;padding-right:20%;"><input type="radio" name="structureType" id="type1" value="AWS" checked="checked"/>';
instrastructurebody += '&nbsp;AWS</label><label style="width: 130px;">';
instrastructurebody += '<input type="radio" name="structureType" id="type2" value="OPENSTACK" />&nbsp;OPENSTACK</label></div></div></div></div>';

var awsSettingBody = '<div rel="sub-title" ><ul class="progressStep" >';
awsSettingBody += '<li class="active">AWS 설정</li><li class="before">Network 설정</li>';
awsSettingBody += '<li class="before">리소스 설정</li><li class="before">배포 Manifest</li>';
awsSettingBody += '<li class="before">설치</li></ul></div>';
awsSettingBody += '<div class="cont_title">▶ AWS 설정정보</div><div class="w2ui-page page-0" style="padding-left:5%;">';
awsSettingBody += '<div class="w2ui-field"><label style="text-align: left;width:200px;font-size:11px;">AWS 키(access-key)</label>';
awsSettingBody += '<div><input name="awsKey" type="text" maxlength="100" size="30" style="float:left;width:330px;"/></div></div>';
awsSettingBody += '<div class="w2ui-field"><label style="text-align: left;width:200px;font-size:11px;">AWS 비밀번호(secret-access-key)</label>';
awsSettingBody += '<div><input name="awsPw" type="password" maxlength="100" size="30" style="float:left;width:330px;"/></div></div>';
awsSettingBody += '<div class="w2ui-field"><label style="text-align: left;width:200px;font-size:11px;">시큐리티 그룹명</label>';
awsSettingBody += '<div><input name="securGroupName" type="text" maxlength="100" size="30" style="float:left;width:330px;"/></div></div>';
awsSettingBody += '<div class="w2ui-field"><label style="text-align: left;width:200px;font-size:11px;">Private Key 명</label>';
awsSettingBody += '<div><input name="privateKeyName" type="text" maxlength="100" size="30" style="float:left;width:330px;"/></div></div>';
awsSettingBody += '<div class="w2ui-field"><label style="text-align: left;width:200px;font-size:11px;">Private Key Path</label>';
awsSettingBody += '<div><input name="privateKeyPath" type="text" maxlength="100" size="30" style="float:left;width:330px;"/></div></div></div>';

var networkSettingBody = '<div rel="body" style="width:100%;padding:15px 5px 15px 5px;"><div ><ul class="progressStep">';
networkSettingBody += '<li class="pass">AWS 설정</li><li class="active">Network 설정</li><li class="before">리소스 설정</li>';
networkSettingBody += '<li class="before">배포 Manifest</li><li class="before">설치</li></ul></div>';
networkSettingBody += '<div rel="sub-title" class="cont_title">▶ Network 설정정보</div><div class="w2ui-page page-0" style="padding-left: 5%;">';
networkSettingBody += '<div class="w2ui-field"><label style="text-align: left; width: 200px; font-size: 11px;">Subnet Range</label>';
networkSettingBody += '<div><input name="subnetRange" type="text" maxlength="100" size="30" style="float:left;width:330px;"/></div></div>';
networkSettingBody += '<div class="w2ui-field"><label style="text-align: left; width: 200px; font-size: 11px;">Gateway</label>';
networkSettingBody += '<div><input name="gateway" type="text" maxlength="100" size="30" style="float:left;width:330px;"/></div></div>';
networkSettingBody += '<div class="w2ui-field"><label style="text-align: left; width: 200px; font-size: 11px;">DNS</label>';
networkSettingBody += '<div><input name="dns" type="text" maxlength="100" size="30" style="float:left;width:330px;"/></div></div>';
networkSettingBody += '<div class="w2ui-field"><label style="text-align: left; width: 200px; font-size: 11px;">Subnet ID</label>';
networkSettingBody += '<div><input name="subnetId" type="text" maxlength="100" size="30" style="float:left;width:330px;"/></div></div>';
networkSettingBody += '<div class="w2ui-field"><label style="text-align: left; width: 200px; font-size: 11px;">Director Private IP</label>';
networkSettingBody += '<div><input name="directorPrivateIp" type="text" maxlength="100" size="30" style="float:left;width:330px;"/></div></div>';
networkSettingBody += '<div class="w2ui-field"><label style="text-align: left; width: 200px; font-size: 11px;">Director Public IP</label>';
networkSettingBody += '<div><input name="directorPublicIp" type="text" maxlength="100" size="30" style="float:left;width:330px;"/></div></div></div>';

//Button Tag
var awsSettingButtons = '<button class="btn" style="float: left;" onclick="w2popup.close();">취소</button>';
awsSettingButtons 	+= '<button class="btn" style="float: right;padding-right:15%" onclick="saveAwsSettingInfo();">다음>></button>';


$(function() {
	
 	$('#config_bootstrapGrid').w2grid({
		name: 'config_bootstrapGrid',
		show: {selectColumn: true, footer: true},
		multiSelect: false,
		style: 'text-align:center',
		columns:[
			 {field: 'status', caption: '상태', size: '20%'}
			,{field: 'name', caption: '이름', size: '20%'}
			,{field: 'iaas', caption: 'IaaS', size: '20%'}
			,{field: 'ip', caption: 'IP', size: '40%'}
		]
	});
 	
 	//BootStrap 설치
 	$("#bootstrapInstallBtn").click(function(){
 		bootSetPopup();
 	});
 	
 	//BootStrap 삭제
	$("#bootstrapDeleteBtn").click(function(){
 		
 	});

});

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
		title 			: popupTitle,
		msg 			: $("#bootSelectBody").html(),
		yes_text 		: "확인",
		no_text 		: "취소",
		yes_callBack 	: function(){
			//alert($("input[name='structureType']").val());
			if($(".w2ui-msg-body input[name='structureType']").val() != ""){
				structureType = $("input[name='structureType']").val(); 
				awsPopup();
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
		height : 390,
		onClose : initSetting
	});
}

//Save AWS Setting Info
function saveAwsSettingInfo(){
	awsInfo = {
			awsKey			: $(".w2ui-msg-body input[name='awsKey']").val(),
			awsPw			: $(".w2ui-msg-body input[name='awsPw']").val(),
			securGroupName	: $(".w2ui-msg-body input[name='securGroupName']").val(),
			privateKeyName	: $(".w2ui-msg-body input[name='privateKeyName']").val(),
			privateKeyPath	: $(".w2ui-msg-body input[name='privateKeyPath']").val()
	}
	
	$.ajax({
		type : "POST",
		url : "/release/bootSetAwsSave",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		data : JSON.stringify(awsInfo), 
		success : function(data, status) {
			networkPopup();
		},
		error : function( e, status ) {
			w2alert("AWS 설정 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
		}
	});
}

//Network Info Setting Popup
function networkPopup(){
	$("#networkSettingInfoDiv").w2popup({
		width : 610,
		height : 420,
		onClose : initSetting
	});
}

//Save Network Setting Info
function saveNetworkSettingInfo(param){
	networkInfo = {
			key					: awsInfo.awsKey,
			subnetRange			: $(".w2ui-msg-body input[name='subnetRange']").val(),
			gateway				: $(".w2ui-msg-body input[name='gateway']").val(),
			dns					: $(".w2ui-msg-body input[name='dns']").val(),
			subnetId			: $(".w2ui-msg-body input[name='subnetId']").val(),
			directorPrivateIp	: $(".w2ui-msg-body input[name='directorPrivateIp']").val(),
			directorPublicIp	: $(".w2ui-msg-body input[name='directorPublicIp']").val()
	}
	
	$.ajax({
		type : "POST",
		url : "/release/bootSetNetworkSave",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		data : JSON.stringify(networkInfo), 
		success : function(data, status) {
			if(param == 'after') resourcesPopup();
			else awsPopup();				
		},
		error : function( e, status ) {
			w2alert("네트워크 설정 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
		}
	});
}


function resourcesPopup(){
	//getStemcellList();
	$("#resourcesSettingInfoDiv").w2popup({
		width : 610,
		height : 400,
		onOpen : function(){
			getStemcellList();//스템셀 리스트 가져오기				
		},
		onClose : initSetting
	});
}

function getStemcellList(){
	$.ajax({
		type : "GET",
		url : "/release/getLocalStemcellList",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		success : function(data, status) {
			$('#w2ui-popup input[type=list]').w2field('list', { items: data , maxDropHeight:300, width:500});
		},
		error : function( e, status ) {
			w2alert("스템셀 목록을 가져오는데 실패하였습니다.", "BOOTSTRAP 설치");
		}
	});
}

function saveResourcesSettingInfo(param){
	resourcesInfo = {
			key				: awsInfo.awsKey,
			targetStemcell	: $(".w2ui-msg-body input[name='targetStemcell']").val(),
			instanceType	: $(".w2ui-msg-body input[name='instanceType']").val(),
			availabilityZone: $(".w2ui-msg-body input[name='availabilityZone']").val(),
			microBoshPw		: $(".w2ui-msg-body input[name='microBoshPw']").val()
	}
	
	$.ajax({
		type : "POST",
		url : "/release/bootSetResourcesSave",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		data : JSON.stringify(networkInfo), 
		success : function(data, status) {
			console.log("### PARAM ::: "+ param);
			if( param == 'after') deployPopup();
			else networkPopup();
		},
		error : function( e, status ) {
			w2alert("리소스 설정 등록에 실패 하였습니다.", "BOOTSTRAP 설치");
		}
	});
}

function deployPopup(){
	$("#deployManifestDiv").w2popup({
		width 	: 610,
		height 	: 470,
		onClose : initSetting
	});
}

function saveDeployInfo(param){
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
		resourcesPopup();
	}
}

function installPopup(){
	$("#installDiv").w2popup({
		width : 610,
		height : 470,
		onOpen : function(){
			//1.Install
			//2.소켓연결(설치 로그)
			w2alert("socket연결");
		},
		onClose : initSetting
	});
}

//팝업창 닫을 경우
function initSetting(){
	structureType 	= null;
	awsInfo			= null;
	networkInfo 	= null;
	resourcesInfo 	= null;
	deployInfo 		= null;
}

function uploadStemcell(){
	$("#targetStemcellUpload").click();
}

function selectedStemcell(){
	$("input[name='targetStemcell']").val($("input[name='targetStemcellUpload']").val());		
}
</script>

<div id="main">
	<div class="page_site">설치관리자 환경설정 > <strong>BOOTSTRAP 설치</strong></div>
	
	<!-- 설치 관리자 -->
<!-- 	<div class="title">설치 관리자</div>
	
	<table class="tbl1" border="1" cellspacing="0">
	<tr>
		<th width="18%" class="th_fb">관리자 이름</th><td class="td_fb">0000</td>
		<th width="18%" class="th_fb">관리자 계정</th><td class="td_fb">0000</td>
	</tr>
	<tr>
		<th width="18%" >관리자 URL</th><td>0000</td>
		<th width="18%" >관리자 UUID</th><td >0000</td>
	</tr>
	</table>
	
	<div id="hMargin"/>
 -->
 
	<!-- BOOTSTRAP 목록-->
	<div class="pdt20"> 
		<div class="title fl">BOOTSTRAP 목록</div>
		<div class="fr"> 
		<!-- Btn -->
		<span class="boardBtn" id="bootstrapInstallBtn"><a href="#" class="btn btn-primary" style="width:140px"><span>BOOTSTRAP 설치</span></a></span>
		<span class="boardBtn" id="bootstrapDeleteBtn"><a href="#" class="btn btn-danger" style="width:140px"><span>BOOTSTRAP 삭제</span></a></span>
		<!-- //Btn -->
	    </div>
	</div>
	<div id="config_bootstrapGrid" style="width:100%; height:500px"/>	
	
</div>

<!-- Start Popup Resion -->
	<!-- Infrastructure  설정 DIV -->
	<div id="bootSelectBody" style="width:100%; height: 80px;" hidden="true">
		<div class="w2ui-lefted" style="text-align: left;">
			설치할 Infrastructure 을 선택하세요<br />
			<br />
		</div>
		<div class="col-sm-9">
			<div class="btn-group" data-toggle="buttons" >
				<label style="width: 100px;margin-left:40px;">
					<input type="radio" name="structureType" id="type1" value="AWS" checked="checked" />
					&nbsp;AWS
				</label>
				<label style="width: 130px;margin-left:50px;">
					<input type="radio" name="structureType" id="type2" value="OPENSTACK"  />
					&nbsp;OPENSTACK
				</label>
			</div>
		</div>
	</div>

	<!-- AWS  설정 DIV -->
	<div id="awsSettingInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%;padding:15px 5px 15px 5px;">
			<div rel="sub-title" >
	            <ul class="progressStep" >
		            <li class="active">AWS 설정</li>
		            <li class="before">Network 설정</li>
		            <li class="before">리소스 설정</li>
		            <li class="before">배포 Manifest</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div class="cont_title">▶ AWS 설정정보</div>
		    <div class="w2ui-page page-0" style="padding-left:5%;">
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">AWS 키(access-key)</label>
		            <div>
		                <input name="awsKey" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">AWS 비밀번호(secret-access-key)</label>
		            <div>
		                <input name="awsPw" type="password" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">시큐리티 그룹명</label>
		            <div>
		                <input name="securGroupName" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Private Key 명</label>
		            <div>
		                <input name="privateKeyName" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:250px;font-size:11px;">Private Key Path</label>
		            <div>
		                <input name="privateKeyPath" type="text" maxlength="100" size="30" style="float:left;width:280px;"/>
		            </div>
		        </div>
		    </div>
			<br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
		        <button class="btn" style="float: left;" onclick="w2popup.close();">취소</button>
		        <button class="btn" style="float: right;padding-right:15%" onclick="saveAwsSettingInfo();">다음>></button>
		    </div>
		</div>
	</div>
	
	
	<!-- Network  설정 DIV -->
	<div id="networkSettingInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%;padding:15px 5px 15px 5px;">
			<div >
	            <ul class="progressStep">
		            <li class="pass">AWS 설정</li>
		            <li class="active">Network 설정</li>
		            <li class="before">리소스 설정</li>
		            <li class="before">배포 Manifest</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" class="cont_title">▶ Network 설정정보</div>
			<div class="w2ui-page page-0" style="padding-left: 5%;">
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">Subnet Range</label>
					<div>
						<input name="subnetRange" type="text" maxlength="100" size="30" style="float:left;width:330px;"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">Gateway</label>
					<div>
						<input name="gateway" type="text" maxlength="100" size="30" style="float:left;width:330px;"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">DNS</label>
					<div>
						<input name="dns" type="text" maxlength="100" size="30" style="float:left;width:330px;"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">Subnet ID</label>
					<div>
						<input name="subnetId" type="text" maxlength="100" size="30" style="float:left;width:330px;"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">Director Private IP</label>
					<div>
						<input name="directorPrivateIp" type="text" maxlength="100" size="30" style="float:left;width:330px;"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">Director Public IP</label>
					<div>
						<input name="directorPublicIp" type="text" maxlength="100" size="30" style="float:left;width:330px;"/>
					</div>
				</div>
			</div>
			<br />
			<div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: left;" onclick="saveNetworkSettingInfo('before');">이전</button>
				<button class="btn" onclick="w2popup.close();">취소</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveNetworkSettingInfo('after');">다음>></button>
			</div>
		</div>
	</div>
	
	<!-- Resources  설정 DIV -->
	<div id="resourcesSettingInfoDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%;padding:15px 5px 15px 5px;">
			<div >
	            <ul class="progressStep">
		            <li class="pass">AWS 설정</li>
		            <li class="pass">Network 설정</li>
		            <li class="active">리소스 설정</li>
		            <li class="before">배포 Manifest</li>
		            <li class="before">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" class="cont_title">▶ 리소스 설정정보</div>
			<div class="w2ui-page page-0" style="padding-left: 5%;">
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">스템셀 지정</label>
					<div>
						<!-- <input name="targetStemcell" type="text" maxlength="100" style="float: left;width:330px;margin-top:1.5px;" /> -->
						<div><input type="list" style="float: left;width:330px;margin-top:1.5px;"></div>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">인스턴스 유형</label>
					<div>
						<input name="instanceType" type="text" maxlength="100" size="30" style="float:left;width:330px;"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">Availability Zone</label>
					<div>
						<input name="availabilityZone" type="text" maxlength="100" size="30" style="float:left;width:330px;"/>
					</div>
				</div>
				<div class="w2ui-field">
					<label style="text-align: left; width: 200px; font-size: 11px;">MicroBOSH Password</label>
					<div>
						<input name="microBoshPw" type="password" maxlength="100" size="30" style="float:left;width:330px;"/>
					</div>
				</div>
			</div>
			<br />
			<div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float: left;" onclick="saveResourcesSettingInfo('before');">이전</button>
				<button class="btn" onclick="w2popup.close();">취소</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveResourcesSettingInfo('after');">다음>></button>
			</div>
		</div>
	</div>
	
	<div id="deployManifestDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%;padding:15px 5px 15px 5px;">
			<div >
	            <ul class="progressStep">
		            <li class="pass">AWS 설정</li>
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
			<button class="btn" onclick="w2popup.close();">취소</button>
			<button class="btn" style="float: right; padding-right: 15%" onclick="saveDeployInfo('after');">다음>></button>
		</div>
	</div>
	
	<div id="installDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="width:100%;padding:15px 5px 15px 5px;">
			<div >
	            <ul class="progressStep">
		            <li class="pass">AWS 설정</li>
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
				<button class="btn" onclick="w2popup.close();">취소</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="w2popup.close();">완료</button>
		</div>		
	</div>	
<!-- End Popup Resion -->