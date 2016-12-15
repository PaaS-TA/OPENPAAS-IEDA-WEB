<%
/* =================================================================
 * 작성일 : 
 * 작성자 : 
 * 상세설명 : 
 * =================================================================
 * 수정일         작성자             내용     
 * -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * 2016.12       이동현        목록 화면 개선
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<script type="text/javascript">

var webSocketClient = null;
//var appendLogPopupBody = '<br/><textarea name="logAppendArea" readonly="readonly" style="width:100%;height:430px;overflow-y:visible ;resize:none;background-color: #FFF;"></textarea>';
var appendLogPopupBody = '<br/><textarea name="logAppendArea" readonly="readonly" style="width:100%;height:96%;overflow-y:visible ;resize:none;background-color: #FFF;"></textarea>';
var appendLogPopupButton = '<button class="btn closeBtn" onclick="popupClose();">닫기</button>'
var bDefaultDirector = "";

$(function() {
	
 	// 기본 설치 관리자 정보 조회
 	 bDefaultDirector = getDefaultDirector("<c:url value='/common/use/director'/>");
 	
	$('#sq_taskHistoryGrid').w2grid({
		name: 'sq_taskHistoryGrid',
		header: '<b>Task 실행 이력</b>',
		method: 'GET',
		multiSelect: false,
		show: {	
				selectColumn: true,
				footer: true},
		style: 'text-align: center',
		columns:[
			 {field: 'recid', 	caption: 'recid', hidden: true}
			,{field: 'id', caption: 'Task ID', size: '7%'}
			,{field: 'state', caption: '상태', size: '8%'}
			,{field: 'runTime', caption: '시간', size: '20%'}
			,{field: 'user', caption: '사용자', size: '10%'}
			,{field: 'description', caption: '실행내용', size: '25%', style:"text-align:left"}
			,{field: 'result', caption: '결과', size: '35%', style:"text-align:left"}
		],
		onSelect: function(event) {
			event.onComplete = function() {
				setDisable($('#downDebugLogBtn'), false);
				//setDisable($('#showDebugLogBtn'), false);
				setDisable($('#showEventLogBtn'), false);
			}
		},
		onUnselect: function(event) {
			event.onComplete = function() {
				setDisable($('#downDebugLogBtn'), true);
				//setDisable($('#showDebugLogBtn'), true);
				setDisable($('#showEventLogBtn'), true);
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
	
	initView(bDefaultDirector)
	
	/********************************************************
	 * 설명		: Task DebugLog File download
	 *********************************************************/
 	$("#downDebugLogBtn").click(function(){
 		//showTaskLog("debug");
 		
 		var selected = w2ui['sq_taskHistoryGrid'].getSelection();
		if ( selected == "" || selected == null) return;
		
		var record = w2ui['sq_taskHistoryGrid'].get(selected);
		if ( record == "" || record == null) return;
		
 		var debugLogdownUrl = "/info/task/list/debugLog/"+ record.id;
 		
 		window.open(debugLogdownUrl, '', ''); 
 		return false;
    });
	
	/********************************************************
	 * 설명		: eventLog
	 *********************************************************/
 	$("#showEventLogBtn").click(function(){
 		showTaskLog("event");
    });
	

});

/********************************************************
 * 설명		: 그리드 재조회 및 버튼 초기화
 * Function	: initView
 *********************************************************/
function initView(bDefaultDirector) {
	if ( bDefaultDirector ) {
		doSearch();
	}

	// 컨트롤 
	setDisable($('#downDebugLogBtn'), true);
	//setDisable($('#showDebugLogBtn'), true);
	setDisable($('#showEventLogBtn'), true);
}

/********************************************************
 * 설명		: flag 설정
 * Function	: setDisable
 *********************************************************/
function setDisable(object, flag) {
	object.attr('disabled', flag);
}

/********************************************************
 * 설명		: Task 로그 조회
 * Function	: showTaskLog
 *********************************************************/
function showTaskLog(type) {
	var selected = w2ui['sq_taskHistoryGrid'].getSelection();
	if ( selected == "" || selected == null) return;
	
	var record = w2ui['sq_taskHistoryGrid'].get(selected);
	if ( record == "" || record == null) return;

	var lineOneYn = "false";
	if(  record.description == "create snapshot" || record.description == "delete snapshot" ){
		lineOneYn = "true";
	}
	var requestParameter = { 
			taskId   : record.id,
			logType  : type,
			lineOneYn : lineOneYn
		};
	
	var typeName = ( type == 'debug') ? '디버그' : '이벤트';
	
	w2confirm( { msg : 'Task ' + record.id + '(' + record.description + ')의 ' + typeName + ' 로그를 확인하시겠습니까?'
		, title : typeName + ' 로그'
		, yes_text:'확인'
		, no_text:'취소'
		, yes_callBack : function(event){
			appendLogPopup(type, typeName, requestParameter);	
		},no_callBack : function(evnet){
			w2ui['sq_taskHistoryGrid'].clear();
			initView(bDefaultDirector);
		}
	});	
}

/********************************************************
 * 설명		: Log Popup Create
 * Function	: appendLogPopup
 *********************************************************/
function appendLogPopup(type, typeName, requestParameter){
	w2popup.open({
		title	: 'Task ' + requestParameter.taskId + typeName + ' 로그',
		body	: appendLogPopupBody,
		buttons : appendLogPopupButton,
		width 	: 800,
		height	: 550,
		modal	: true,
		showMax : true,
		onOpen  : function(){
			doGetTaskLog(type, typeName, requestParameter);
		},
		onClose : function() {
			w2ui['sq_taskHistoryGrid'].clear();
			initView(bDefaultDirector);
			webSocketClient.disconnect();
		}
	});
}

/********************************************************
 * 설명		: event log Popup , event log 조회
 * Function	: doGetTaskLog
 *********************************************************/
function doGetTaskLog(type, typeName, requestParameter) {
	var message = "Task (" + requestParameter.taskId + ")의 " + typeName + "로그 ";
	
	var socket = new SockJS('/info/task/list/eventLog/task');
	webSocketClient = Stomp.over(socket); 
	webSocketClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        webSocketClient.subscribe('/user/info/task/list/eventLog/socket', function(data){
        	
         	var response = JSON.parse(data.body);
	        var checkErrorCode = [];
        	if ( response.messages != null ) {
		       	for ( var i=0; i < response.messages.length; i++) {
		       		$("textarea[name='logAppendArea']").append(response.messages[i] + "\n").scrollTop($("textarea[name='logAppendArea']")[0].scrollHeight);
		       	}
 		       	if ( response.state.toLowerCase() != "started" ) {
		            if ( response.state.toLowerCase() == "done" )	   message = message + " 조회가 완료되었습니다."; 
		    		if ( response.state.toLowerCase() == "error" )     message = message + " 조회 중 오류가 발생하였습니다.";
		    		if ( response.state.toLowerCase() == "cancelled" ) message = message + " 조회 중 취소되었습니다."; 
		    			
		    		webSocketClient.disconnect();
					w2alert(message, "Task " + typeName);
		       	}
        	} 
        });
        requestTaskData(requestParameter);
    });
}

/********************************************************
 * 설명		: Task 이벤트 로그 웹 소켓 연결
 * Function	: requestTaskData
 *********************************************************/
function requestTaskData(requestParameter){
	webSocketClient.send('/app/info/task/list/eventLog/task', {}, JSON.stringify(requestParameter));
}

/********************************************************
 * 설명		: 팝업 닫을 경우 Socket Connection 종료 및 log 영역 초기화
 * Function	: popupClose
 *********************************************************/
function popupClose() {
	if (webSocketClient != null) {
		webSocketClient.disconnect();
		$("textarea[name='logAppendArea']").text("");
	}
	
	w2popup.close();
}

/********************************************************
 * 설명		: 조회 기능
 * Function	: doSearch
 *********************************************************/
function doSearch() {
	w2ui['sq_taskHistoryGrid'].load("<c:url value='/info/task/list'/>");
}

/********************************************************
 * 설명		: 다른페이지 이동시 호출
 * Function	: clearMainPage
 *********************************************************/
function clearMainPage() {
	$().w2destroy('sq_taskHistoryGrid');
}

/********************************************************
 * 설명		: 화면 리사이즈시 호출
 *********************************************************/
$( window ).resize(function() {
	setLayoutContainerHeight();
});

</script>

<div id="main">
	<div class="page_site">정보조회 > <strong>Task 실행 이력</strong></div>

	<!-- 설치 관리자 -->
	<div id="isDefaultDirector"></div>
	
	<!-- Task실행 이력 -->
	<div class="pdt20">
		<div class="title fl">Task 실행 이력</div>
		<div class="fr"> 
			<!-- Btn -->
			<!-- <span class="boardBtn" id="showTaskDebugLogBtn" ><a href="#" class="btn btn-primary" style="width:150px"><span></span></a></span> -->
			<sec:authorize access="hasAuthority('INFO_TASK_DEBUG')">
			<span class="btn btn-primary" style="width:180px" id="downDebugLogBtn">디버그 로그 다운로드</span>
			</sec:authorize>
			<sec:authorize access="hasAuthority('INFO_TASK_EVENT')">
			<span class="btn btn-primary" style="width:120px" id="showEventLogBtn">이벤트 로그</span>
			</sec:authorize>
			<!-- //Btn -->
		</div>
	</div>	
	<div id="hMargin"></div>
	<div id="sq_taskHistoryGrid" style="width:100%; height:650px"></div>
</div>