<%
/* =================================================================
 * 작성일 : 2016.08
 * 작성자 : 지향은
 * 상세설명 : VM 관리
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016.12       이동현        코드 버그 수정 및 목록 화면 개선
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<script type="text/javascript">
var webSocketClient = null;
var jobParam = "";
var snapshotClient = null;
var snapshotParam = "";
var bDefaultDirector ="";
$(function() {
	
 	/************************************************
 	 * 설명: 기본 설치 관리자 정보 조회
 	************************************************/
 	bDefaultDirector = getDefaultDirector("<c:url value='/common/use/director'/>");
 	
 	/************************************************
 	 * 설명: VM 조회
 	************************************************/
 	$('#us_vmsGrid').w2grid({
		name: 'us_vmsGrid',
		header: '<b>VM 목록</b>',
		style	: 'text-align:center',
		method	: 'GET',
		msgAJAXerror : 'VM 조회 실패',
		multiSelect: false,
		show: {	
			selectColumn: true,
			footer: true},
		style: 'text-align: center',
		columns	: [
					 {field: 'recid', 	caption: 'recid', hidden: true}
				   , {field: 'deploymentName', caption: 'deployment', hidden: true}
		       	   , {field: 'jobName', caption: 'VM', size: '120px', style: 'text-align:center'}
		       	   , {field: 'jobState', caption: 'State', size: '80px', style: 'text-align:center'}
		       	   , {field: 'az', caption: 'AZ', size: '50px', style: 'text-align:center'}
		       	   , {field: 'vmType', caption: 'VM Type', size: '80px', style: 'text-align:center'}
		       	   , {field: 'ips', caption: 'IPs', size: '100px', style: 'text-align:center'}
		       	   , {field: 'load', caption: 'Load<br/>(avg01, avg05, avg15)', size: '120px', style: 'text-align:center'}
		       	   , {field: 'cpuUser', caption: 'Cpu User', size: '80px', style: 'text-align:center'}
		       	   , {field: 'cpuSys', caption: 'Cpu Sys', size: '80px', style: 'text-align:center'}
		       	   , {field: 'cpuWait', caption: 'Cpu Wait', size: '80px', style: 'text-align:center'}
		       	   , {field: 'memoryUsage', caption: 'Memory<br/>Usage', size: '90px', style: 'text-align:center'}
		       	   , {field: 'swapUsage', caption: 'Swap<br/>Usage', size: '90px', style: 'text-align:center'}
		       	   , {field: 'diskSystem', caption: 'System<br/>DIsk Usage', size: '90px', style: 'text-align:center'}
		       	   , {field: 'diskEphemeral', caption: 'Ephemeral<br/>DIsk Usage', size: '90px', style: 'text-align:center'}
		       	   , {field: 'diskPersistent', caption: 'Persistent<br/>DIsk Usage', size: '90px', style: 'text-align:center'}
		       	],
       	onSelect: function(event) {
			var grid = this;
			event.onComplete = function() {
				$('#selectLogType').attr('disabled', false);
				$('#startedJob').attr('disabled', false);
				$('#stoppedJob').attr('disabled', false);
				$('#restartJob').attr('disabled', false);
				$('#recreateJob').attr('disabled', false);
				$('#takeSnapshot').attr('disabled', false);
			}
		},
		onUnselect: function(event) {
			event.onComplete = function() {
				$('#selectLogType').attr('disabled', true);
				$('#startedJob').attr('disabled', true);
				$('#stoppedJob').attr('disabled', true);
				$('#restartJob').attr('disabled', true);
				$('#recreateJob').attr('disabled', true);
				$('#takeSnapshot').attr('disabled', true);
			}
		},
       	onLoad:function(event){
			if(event.xhr.status == 403){
				location.href = "/abuse";
				event.preventDefault();
			}
		}, onError:function(evnet){
		}
	});
	
 	initView(bDefaultDirector);
 	
 	/************************************************
 	 * 설명: VM 조회
 	************************************************/
 	$("#doSearch").click(function(){
 		doSearch($("#deployments").val());
 	});
 	
 	/************************************************
 	 * 설명: 로그 유형 선택
 	************************************************/
 	$("#selectLogType").click(function(){
 		if($("#selectLogType").attr('disabled') == "disabled") return;
 		w2confirm({
			width 			: 550,
			height 			: 180,
			title 				: '<b>다운로드 로그 유형 선택</b>',
			msg 				: $("#selectLogTypeDiv").html(),
			modal			: true,
			yes_text 		: "확인",
			no_text 			: "취소",
			yes_callBack 	: function(){
				logType = $(".w2ui-msg-body input:radio[name='logSelect']:checked").val();
				downloadLog(logType);
				return;
			},no_callBack : function(){
				doSearch($("#deployments").val());
			}
		});
 	});
})

/****************************************
 * 설명:  스냅샵 생성 버튼 제어
 ****************************************/
function showSnapshotBtn(){
	 jQuery.ajax({
		type : "GET",
		url :  '/info/vms/list/snapshot',
		contentType : "application/json",
		async : true,
		success : function(data, status) {
			if( Number(data.contents) > 0 ){
				$("#takeSnapshot").show();
			}else{
				$("#takeSnapshot").hide();
			}
		},
		error : function(request, status, error) {
			var errorResult = JSON.parse(request.responseText);
			w2alert(errorResult.message, "스냅샷  사용 여부");
		}
	});
	
}

/****************************************
 * 설명:  배포 조회
 ****************************************/
function getDeploymentList(){
	jQuery.ajax({
		type : "GET",
		url : '/common/use/deployments',
		contentType : "application/json",
		async : true,
		success : function(data) {
			var $object = jQuery("#deployments");
			var optionString = "";
			if(data.contents != null){
				optionString = "<option selected='selected' disabled='disabled' value='all' style='color:gray'>조회할 배포명을 선택하세요.(필수)</option>";
				for (i = 0; i < data.contents.length; i++) {
					optionString += "<option value='" + data.contents[i].name + "' >";
					optionString += data.contents[i].name;
					optionString += "</option>\n";
				}
			}else{
				optionString = optionString = "<option selected='selected' disabled='disabled' value='all' style='color:red'>조회할 배포명이 없습니다.</option>";
			}
			$object.html(optionString);
		}
	});
}
	

/************************************************
 * 설명: 로그 다운로드
************************************************/
function downloadLog(type){
	
	var selected = w2ui['us_vmsGrid'].getSelection();
	if ( selected == "" || selected == null) return;
	
	var record = w2ui['us_vmsGrid'].get(selected);
	if ( record == "" || record == null) return;
	
	var logDownloadUrl = "/info/vms/download/"+ record.jobName+"/"+record.deploymentName+"/"+type; 
	
	window.open(logDownloadUrl, '', ''); 
	return false;
}

/************************************************
 * 설명: 조회기능
************************************************/
function doSearch(deployment) {
	if( deployment != null ){
		w2ui['us_vmsGrid'].load("<c:url value='/info/vms/list/'/>"+deployment);	
	}else{
		if(!bDefaultDirector){
			w2alert("기본 설치 관리자를 등록해 주세요. ","VM 조회");
		}else{
			w2alert("배포명을 선택해주세요. ","VM 조회");
		}
	}
}

/************************************************
 * 설명: Job 상태 로그 설정
************************************************/
function changeJobState(type){
	 
	var typeName = "";
	var state = "";
	if( type == 'started' ){ typeName = "Job 시작"; state = "running";  }
	else if( type == 'restart' ){ typeName = 'Job 재시작'; state = "restart"; }
	else if( type == 'stopped' ){ typeName = 'Job 중지'; state = "stopped"; }
	else if( type == 'recreate' ){ typeName = 'Job 재생성'; state = "recreate"; }
	
	var selected = w2ui['us_vmsGrid'].getSelection();
	if ( selected == "" || selected == null) return;
	
	var record = w2ui['us_vmsGrid'].get(selected);
	if ( record == "" || record == null) return;
	
	if( record.jobState.indexOf(state) > -1 ){
		w2alert('이미 '+typeName +  '상태 입니다.',  typeName);
		return;
	}
	
	jobParam = {
		deploymentName	: record.deploymentName,
		jobName				: record.jobName.split("/")[0],
		index						: record.jobName.split("/")[1],
		state						: type
	}
	
	showJobLog(type, typeName, jobParam);
	
}

/************************************************
 * 설명: Job 상태 로그 화면
************************************************/
function showJobLog(type, typeName, jobParam ){
	 var message = jobParam.jobName + " (배포명:" + jobParam.deploymentName + ")의 " + typeName + " 로그 ";
	 
	$("#jobStateDiv").w2popup({
		title 	: typeName,
		width : 850,
		height : 550,
		modal	: true,
		showMax : true,
		onOpen : function(event){
			event.onComplete = function(){
				var socket = new SockJS('/info/vms/vmLogs/job');
				
				webSocketClient = Stomp.over(socket); 
				webSocketClient.connect({}, function(frame) {
					webSocketClient.subscribe('/user/info/vms/vmLogs/socket', function(data){
						
						var jobStateLogs = $(".w2ui-msg-body #jobStateLogs");
			        	var response = JSON.parse(data.body);
			        	
						if ( response.messages != null ) {
							for ( var i=0; i < response.messages.length; i++) {
								jobStateLogs.append(response.messages[i] + "\n").scrollTop( jobStateLogs[0].scrollHeight );
					       	}
							
							if ( response.state.toLowerCase() != "started" ) {
					            if ( response.state.toLowerCase() == "done" )	   message = message + " 조회가 완료되었습니다."; 
					    		if ( response.state.toLowerCase() == "error" )     message = message + " 조회 중 오류가 발생하였습니다.";
					    		if ( response.state.toLowerCase() == "cancelled" ) message = message + " 조회 중 취소되었습니다."; 
					    		
					    		webSocketClient.disconnect();
								w2alert(message,  typeName);
					       	}
						}
			        });
			        webSocketClient.send('/send/info/vms/vmLogs/job', {}, JSON.stringify(jobParam));
			    });
			}
		},onClose : function(evnet){
			doSearch(jobParam.deploymentName);
		}
	});
}

/************************************************
 * 설명: 스냅샷 생성 요청
************************************************/
function takeSnapshot(){
	var selected = w2ui['us_vmsGrid'].getSelection();
	if ( selected == "" || selected == null) return;
	
	var record = w2ui['us_vmsGrid'].get(selected);
	if ( record == "" || record == null) return;
	
	snapshotParam = {
		deploymentName	: record.deploymentName,
		jobName				: record.jobName.split("/")[0],
		index						: record.jobName.split("/")[1],
	}
	showSnapshotLog(snapshotParam);
}
/************************************************
 * 설명: 스냅샷 생성 로그
************************************************/
function showSnapshotLog(snapshotParam){
	var message = snapshotParam.jobName+"/"+snapshotParam.index + " (배포명:" + snapshotParam.deploymentName + ")의 스냅샷 생성";
	
	$("#takeSnapshotDiv").w2popup({
		title	: '스냅샷 생성',
		width : 550,
		height : 350,
		modal	: true,
		showMax : true,
		onOpen : function(event){
			event.onComplete = function(){
				var socket = new SockJS('/info/vms/snapshotLog/snapshotTaking');
				
				snapshotClient = Stomp.over(socket); 
				snapshotClient.connect({}, function(frame) {
					snapshotClient.subscribe('/user/info/vms/snapshotLog/socket', function(data){
						
						var takeSnapshotLogs = $(".w2ui-msg-body #takeSnapshotLogs");
			        	var response = JSON.parse(data.body);
			        	
						if ( response.messages != null ) {
							for ( var i=0; i < response.messages.length; i++) {
								takeSnapshotLogs.append(response.messages[i] + "\n").scrollTop( takeSnapshotLogs[0].scrollHeight );
					       	}
							if ( response.state.toLowerCase() != "started" ) {
								if ( response.state.toLowerCase() == "done" )	message = message + "이 완료되었습니다."; 
								if ( response.state.toLowerCase() == "error" ) message = message + " 중 오류가 발생하였습니다.";
								if ( response.state.toLowerCase() == "cancelled" ) message = message + " 중 취소되었습니다.";
					    		
								snapshotClient.disconnect();
								w2alert(message, "스냅샷 생성 ");
					       	}
						}
			        });
			        snapshotClient.send('/send/info/vms/snapshotLog/snapshotTaking', {}, JSON.stringify(snapshotParam));
			    });
			}
		}, onClose : function(event){
			doSearch(snapshotParam.deploymentName);
		}
	});
}

/************************************************
 * 설명: 초기 설정
************************************************/
function initView(bDefaultDirector) {
	 $("#takeSnapshot").hide();
	 if ( bDefaultDirector ) {
		showSnapshotBtn();
		getDeploymentList();
	}else{
		$("#deployments").html("<option selected='selected' disabled='disabled' value='all' style='color:red'>기본 설치자가 존재 하지 않습니다.</option>");
	}
	$('#selectLogType').attr('disabled', true);
	$('#startedJob').attr('disabled', true);
	$('#stoppedJob').attr('disabled', true);
	$('#restartJob').attr('disabled', true);
	$('#recreateJob').attr('disabled', true);
	$('#takeSnapshot').attr('disabled', true);
}
	
/********************************************************
 * 설명			: 팝업 종료시 이벤트
 * Function	: popupComplete
 *********************************************************/
function popupComplete(){
	var msg;
	msg = $(".w2ui-msg-title b").text() + " 화면을 닫으시겠습니까?";
	w2confirm({
		title 	: $(".w2ui-msg-title b").text(),
		msg		: msg,
		yes_text: "확인",
		yes_callBack : function(envent){
			if (webSocketClient != null) {
				webSocketClient.disconnect();
				webSocketClient = null;
				$("textarea[name='jobStateLogs']").text("");
				doSearch(jobParam.deploymentName);
				jobParam = "";
			} else if ( snapshotClient != null) {
				snapshotClient.disconnect();
				$("textarea[name='takeSnapshotLogs']").text("");
				snapshotClient = null;
				doSearch(snapshotParam.deploymentName);
				snapshotParam = "";
			}
			w2popup.close();
		},
		no_text : "취소"
	});
}


/********************************************************
 * 설명			: 다른페이지 이동시 호출
 * Function	: clearMainPage
 *********************************************************/
function clearMainPage() {
	$().w2destroy('us_vmsGrid');
}

/********************************************************
 * 설명			: 화면 리사이즈시 호출
 * Function	: resize
 *********************************************************/
$( window ).resize(function() {
	setLayoutContainerHeight();
});
</script>

<div id="main">
	<div class="page_site">정보조회 > <strong>VM 관리</strong></div>

	<!-- 설치 관리자 -->
	<div id="isDefaultDirector"></div>
	
	<div class="pdt20"> 
		<div class="search_box" align="left" style="padding-left:10px;">
			<label  style="font-size:11px">배포명</label>
			&nbsp;&nbsp;&nbsp;
			<select name="select" id="deployments" class="select" style="width:300px">
			</select>
			&nbsp;&nbsp;&nbsp;
			<span id="doSearch" class="btn btn-info" style="width:50px" >조회</span>
		</div>
		
		<div class="title fl">VM 목록</div>
		<div class="fr"> 
			<!-- Btn -->
			<sec:authorize access="hasAuthority('INFO_VM_LOG')">
			<span class="btn btn-primary" style="width:180px" id="selectLogType">로그 다운로드</span>
			</sec:authorize>
			<sec:authorize access="hasAuthority('INFO_VM_JOB')">
			<span class="btn btn-primary" style="width:120px" id="takeSnapshot" onclick="takeSnapshot();">스냅샵 생성</span>
			<span class="btn btn-primary" style="width:120px" id="startedJob" onclick="changeJobState('started');">Job 시작</span>
			<span class="btn btn-primary" style="width:120px" id="stoppedJob" onclick="changeJobState('stopped');">Job 중지</span>
			<span class="btn btn-primary" style="width:120px" id="restartJob" onclick="changeJobState('restart');">Job 재시작</span>
			<span class="btn btn-primary" style="width:120px" id="recreateJob" onclick="changeJobState('recreate');">Job 재생성</span>
			</sec:authorize>
			<!-- //Btn -->
		</div>
		<div id="us_vmsGrid" style="width:100%; height:533px"></div>	
	</div>
</div>

<div id="selectLogTypeDiv" style="width:100%; height: 80px;" hidden="true">
	<div class="w2ui-lefted" style="text-align: center;">
		다운로드 할 로그를 선택하세요.<br />
		<br/>
		<div class="btn-group" data-toggle="buttons" >
			<label style="width: 100px; ">
				<input type="radio" name="logSelect" id="type1" value="agent" checked="checked"  />
				&nbsp;Agent
			</label>
			<label style="width: 120px;margin-left:30px;">
				<input type="radio" name="logSelect" id="type2" value="job"  />
				&nbsp;Job
			</label>
		</div>
	</div>
</div>

<!-- Job Popup -->
<div id="jobStateDiv"  style="width: 100%; height: 100%;" hidden="true">
	<div rel="title"><b>Job</b></div>
	<div rel="body" style="width:100%;height:100%;padding:15px 5px 0 5px;margin:0 auto;">
		<div style="width:95%;height:84%;float: left;display: inline-block;margin-top:1%;">
			<textarea id="jobStateLogs" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:3%;" readonly="readonly"></textarea>
		</div>
	</div>
	<div class="w2ui-buttons" rel="buttons" hidden="true">
		<button class="btn" style="float: right; padding-right: 15%" onclick="popupComplete();">닫기</button>
	</div>
</div>

<!-- Take SnapShot -->
<div id="takeSnapshotDiv"  style="width: 100%; height: 100%;" hidden="true">
	<div rel="title"><b>Job</b></div>
	<div rel="body" style="width:100%;height:100%;padding:15px 5px 0 5px;margin:0 auto;">
		<div style="width:95%;height:84%;float: left;display: inline-block;margin-top:1%;">
			<textarea id="takeSnapshotLogs" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:3%;" readonly="readonly"></textarea>
		</div>
	</div>
	<div class="w2ui-buttons" rel="buttons" hidden="true">
		<button class="btn" style="float: right; padding-right: 15%" onclick="popupComplete();">닫기</button>
	</div>
</div>