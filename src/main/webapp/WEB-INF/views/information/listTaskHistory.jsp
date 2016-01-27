<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript" src="<c:url value='/js/sockjs-0.3.4.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/stomp.js'/>"></script>

<script type="text/javascript">

var webSocketClient = null;
//var appendLogPopupBody = '<br/><textarea name="logAppendArea" readonly="readonly" style="width:100%;height:430px;overflow-y:visible ;resize:none;background-color: #FFF;"></textarea>';
var appendLogPopupBody = '<br/><textarea name="logAppendArea" readonly="readonly" style="width:100%;height:96%;overflow-y:visible ;resize:none;background-color: #FFF;"></textarea>';
var appendLogPopupButton = '<button class="btn closeBtn" onclick="popupClose();">닫기</button>'


$(function() {
	
 	// 기본 설치 관리자 정보 조회
 	var bDefaultDirector = getDefaultDirector("<c:url value='/directors/default'/>");
 	
	$('#sq_taskHistoryGrid').w2grid({
		name: 'sq_taskHistoryGrid',
		style: 'text-align:center',
		method: 'GET',
		multiselect: false,
		show: {selectColumn: true, footer: true},
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
				setDisable($('#showDebugLogBtn'), false);
				setDisable($('#showEventLogBtn'), false);
			}
		},
		onUnselect: function(event) {
			event.onComplete = function() {
				setDisable($('#showDebugLogBtn'), true);
				setDisable($('#showEventLogBtn'), true);
			}
		},
		onError: function(event) {
			this.unlock();
			gridErrorMsg(event);
		}
	});
	
	initView(bDefaultDirector)
	
 	//릴리즈 삭제
 	$("#showDebugLogBtn").click(function(){
 		showTaskLog("debug");
    });
	
 	$("#showEventLogBtn").click(function(){
 		showTaskLog("event");
    });
	

});

function initView(bDefaultDirector) {
	if ( bDefaultDirector ) {
		doSearch();
	}

	// 컨트롤 
	setDisable($('#showDebugLogBtn'), true);
	setDisable($('#showEventLogBtn'), true);
}

function setDisable(object, flag) {
	object.attr('disabled', flag);
}

function showTaskLog(type) {
	var selected = w2ui['sq_taskHistoryGrid'].getSelection();
	if ( selected == "" || selected == null) return;
	
	var record = w2ui['sq_taskHistoryGrid'].get(selected);
	if ( record == "" || record == null) return;

	var requestParameter = { 
			taskId   : record.id,
			logType  : type
		};
	
	var typeName = ( type == 'debug') ? '디버그' : '이벤트';
	
	w2confirm( { msg : 'Task ' + record.id + '(' + record.description + ')의 ' + typeName + ' 로그를 확인하시겠습니까?'
		, title : typeName + ' 로그'
		, yes_text:'확인'
		, no_text:'취소'
	})
	.yes(function() {
		appendLogPopup(type, typeName, requestParameter);
	})
	.no(function() {
		// do nothing
	});	
}

//Log Popup Create
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
			webSocketClient.disconnect();
		}
	});
}

function doGetTaskLog(type, typeName, requestParameter) {
	var message = "Task (" + requestParameter.taskId + ")의 " + typeName + "로그 ";
	
	var socket = new SockJS('/task');
	webSocketClient = Stomp.over(socket); 
	webSocketClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        webSocketClient.subscribe('/socket/task', function(data){
        	
         	var response = JSON.parse(data.body);
	        
        	if ( response.messages != null ) {
		       	for ( var i=0; i < response.messages.length; i++) {
		       		$("textarea[name='logAppendArea']").append(response.messages[i] + "\n").scrollTop($("textarea[name='logAppendArea']")[0].scrollHeight);
		       	}

 		       	if ( response.state.toLowerCase() != "started" ) {
/* 		            if ( response.state.toLowerCase() == "done" )	   message = message + " 조회가 완료되었습니다."; 
		    		if ( response.state.toLowerCase() == "error" )     message = message + " 조회 중 오류가 발생하였습니다.";
		    		if ( response.state.toLowerCase() == "cancelled" ) message = message + " 조회 중 취소되었습니다."; */
		    		
		    		if ( response.state.toLowerCase() == "done" || response.state.toLowerCase() == "error" || response.state.toLowerCase() == "cancelled" )
						message = message + " 조회가 완료되었습니다."; 	    			
		    		
		    		webSocketClient.disconnect();
					w2alert(message, "Task " + typeName);
		       	}
        	} 
        });
        
        requestTaskData(requestParameter);
    });
}

function requestTaskData(requestParameter){
	webSocketClient.send('/app/task', {}, JSON.stringify(requestParameter));
}

//팝업 닫을 경우 Socket Connection 종료 및 log 영역 초기화
function popupClose() {
	if (webSocketClient != null) {
		webSocketClient.disconnect();
		$("textarea[name='logAppendArea']").text("");
	}
	
	w2popup.close();
}

//조회기능
function doSearch() {
	w2ui['sq_taskHistoryGrid'].load("<c:url value='/tasks'/>");
}

//다른페이지 이동시 호출
function clearMainPage() {
	$().w2destroy('sq_taskHistoryGrid');
}

//화면 리사이즈시 호출
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
			<span class="btn btn-primary" style="width:120px" id="showDebugLogBtn">디버그 로그</span>
			<span class="btn btn-primary" style="width:120px" id="showEventLogBtn">이벤트 로그</span>
			<!-- //Btn -->
		</div>
	</div>	
	<div id="hMargin"></div>
	<div id="sq_taskHistoryGrid" style="width:100%; height:500px"></div>
</div>