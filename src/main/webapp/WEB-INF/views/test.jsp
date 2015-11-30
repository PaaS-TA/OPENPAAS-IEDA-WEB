<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
	response.setHeader("Cache-Control", "no-cache");
	response.setHeader("Pragma", "no-cache");
	response.setDateHeader("Expires", 0);
%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Scoket Test</title>

<!-- CSS  -->
<link rel="stylesheet" type="text/css" ref="/webjars/bootstrap/3.3.5/css/bootstrap.min.css" />
<link rel="stylesheet" type="text/css" href="/webjars/w2ui/1.4.2/w2ui.min.css" />
<link rel="stylesheet" type="text/css" href="/webjars/jquery-ui/1.11.4/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="/css/default.css" />
<link rel="stylesheet" type="text/css" href="/css/guide.css" />
<link rel="stylesheet" type="text/css" href="/css/bootstrap-progress-step.css"/>
<style type="text/css">
.w2ui-popup .w2ui-msg-body{background-color: #FFF; }
</style>
<!-- JQuery -->
<script type="text/javascript" src="/webjars/jquery/2.1.1/jquery.min.js"></script>
<script type="text/javascript" src="/webjars/jquery-ui/1.11.4/jquery-ui.js"></script>

<!-- JQuery Form -->
<script type="text/javascript" src="/webjars/jquery-form/3.51/jquery.form.js"></script>

<!-- W2UI -->
<script type="text/javascript" src="/webjars/w2ui/1.4.2/w2ui.min.js"></script>

<!-- Bootstrap button.js -->
<!-- <script type="text/javascript" src="/webjars/bootstrap/3.3.5/css/bootstrap.min.js"></script>
<script type="text/javascript" src="/webjars/bootstrap/3.3.5/css/button.js"></script> -->
<!-- Common -->
<script type="text/javascript" src="/js/common.js"></script>
<script type="text/javascript" src="/js/commonPopup.js"></script>
<script type="text/javascript" src="/js/sockjs-0.3.4.js"></script>
<script type="text/javascript" src="/js/stomp.js"></script>
<script type="text/javascript">
	//private var
	var structureType = "";
	var awsInfo = "";
	var networkInfo = "";
	var resourcesInfo = "";
	var deployInfo = "";
	var stemcells = "";
	
	//Title Tag
	var popTitle1 = '<b>BOOTSTRAP 설치</b>';

	//Body Tag
	var popBody1 = '<div id="bootSelectBody" style="width:400px;height:80px;">';
	popBody1 += '<div class="w2ui-lefted" style="text-align:left;">설치할 Infrastructure 을 선택하세요<br/><br/></div>';
	popBody1 += '<div class="row" ><div class="col-sm-9"><div class="btn-group" data-toggle="buttons"  style="left:20%">';
	popBody1 += '<label style="width:130px;"><input type="radio" name="structureType" id="type1" value="AWS" checked="checked"/>&nbsp;AWS</label>';
	popBody1 += '<label style="width:130px;"><input type="radio" name="structureType" id="type2" value="OPENSTACK"/>&nbsp;OPENSTACK</label>';
	popBody1 += '</div></div></div></div>';

	//Button Tag
	var popButton1 = '<button id="checkBtn">확인</button><button id="cancleBtn" onlclick="initSetting();">취소</button>';

	$(function() {

		$("#bootSetPopup").click(function() {
			bootSetPopup();
		})

	})

	//Bootstrap 
	function bootSetPopup() {
		w2confirm({
			width 			: 450,
			height 			: 180,
			title 			: popTitle1,
			msg 			: $("#bootSelectBody").html(),//popBody1,
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
				console.log("### Stemcell Size : "+data.length);
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
			console.log("#### 여기오나?");
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
		console.log(" file name :::" + $("input[name='targetStemcellUpload']").val());
		$("input[name='targetStemcell']").val($("input[name='targetStemcellUpload']").val());		
	}
	
</script>
</head>
<body>
	<div>
		<div>
			<button id="bootSetPopup" style="width: 300px; height: 50px;">bootSetPopup</button>
		</div>
	</div>

	<!-- Infrastructure  설정 DIV -->
	<div id="bootSelectBody" style="width: 400px; height: 80px;" hidden="true">
		<div class="w2ui-lefted" style="text-align: left;">
			설치할 Infrastructure 을 선택하세요<br />
			<br />
		</div>
		<div class="row">
			<div class="col-sm-9">
				<div class="btn-group" data-toggle="buttons" style="left: 20%">
					<label style="width: 130px;padding-right:20%;">
						<input type="radio" name="structureType" id="type1" value="AWS" checked="checked"/>
						&nbsp;AWS
					</label>
					<label style="width: 130px;">
						<input type="radio" name="structureType" id="type2" value="OPENSTACK" />
						&nbsp;OPENSTACK
					</label>
				</div>
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
		            <label style="text-align: left;width:200px;font-size:11px;">AWS 키(access-key)</label>
		            <div>
		                <input name="awsKey" type="text" maxlength="100" size="30" style="float:left;width:330px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:200px;font-size:11px;">AWS 비밀번호(secret-access-key)</label>
		            <div>
		                <input name="awsPw" type="password" maxlength="100" size="30" style="float:left;width:330px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:200px;font-size:11px;">시큐리티 그룹명</label>
		            <div>
		                <input name="securGroupName" type="text" maxlength="100" size="30" style="float:left;width:330px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:200px;font-size:11px;">Private Key 명</label>
		            <div>
		                <input name="privateKeyName" type="text" maxlength="100" size="30" style="float:left;width:330px;"/>
		            </div>
		        </div>
		        <div class="w2ui-field">
		            <label style="text-align: left;width:200px;font-size:11px;">Private Key Path</label>
		            <div>
		                <input name="privateKeyPath" type="text" maxlength="100" size="30" style="float:left;width:330px;"/>
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
	<div id="localStemcellList" hidden="true">
	
	</div>
</body>
</html>
