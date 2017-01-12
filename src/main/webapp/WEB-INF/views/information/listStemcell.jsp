<%
/* =================================================================
 * 작성일 : 
 * 작성자 : 
 * 상세설명 : 
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016.12       이동현        목록 화면 개선 및 코드 버그 수정
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" type="text/css" href="<c:url value='/css/progress-step.css'/>"/>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<script type="text/javascript">
var uploadClient = null;
var deleteClient = null;
var bDefaultDirector = "";
var iaas = "";
$(function() {
	
	/********************************************************
	  * 설명		: 기본 설치 관리자 정보 조회
	  *********************************************************/
	bDefaultDirector = getDefaultDirector("<c:url value='/common/use/director'/>");
 	
 	/********************************************************
	 * 설명 :  업로드된 스템셀 목록
	 *********************************************************/
 	$('#us_uploadStemcellsGrid').w2grid({
		name	: 'us_uploadStemcellsGrid',
		show: {selectColumn: true, footer: true},
		multiSelect: false,
		method: 'GET',
		msgAJAXerror : '업로드 된 스템셀 조회 실패',
		style: 'text-align:center',
		columns	:[
		           {field: 'recid', caption: 'recid', hidden: true}
		         , {field: 'os', caption: '운영체계', size: '30%'}
		         , {field: 'stemcellFileName', caption: '스템셀명', size: '40%'}		         
		         , {field: 'stemcellVersion', caption: '스템셀버전', size: '30%'}
		         ],
 		onSelect: function(event) {
			event.onComplete = function() { 				
				setDisable($('#doDeleteStemcell'), false);
			}
		},
		onUnselect: function(event) {
			event.onComplete = function() {
				setDisable($('#doDeleteStemcell'), true);
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
	
 	/********************************************************
	 * 설명 :  다운로드된 스템셀 목록
	 *********************************************************/
 	$('#us_localStemcellsGrid').w2grid({
		name: 'us_localStemcellsGrid',
		header: '<b>Local Stemcell 목록</b>',
		show: {selectColumn: true, footer: true},
		multiSelect: false,
		method: 'GET',
		style: 'text-align:center',
		columns:[
			 {field: 'recid', caption: '운영체계', hidden: true}
			,{field: 'os', caption: '운영체계', size: '10%'}
			,{field: 'osVersion', caption: '버전', size: '10%'}
			,{field: 'iaas', caption: 'IaaS', size: '10%', sortable: true}
			,{field: 'stemcellVersion', caption: '스템셀버전', size: '10%'}
			,{field: 'stemcellFileName', caption: '파일명', size: '60%', style: 'text-align:left'}
		],
		onSelect: function(event) {
			event.onComplete = function() {
				if ( bDefaultDirector ) {
					setDisable($('#doUploadStemcell'), false);
				}
			}
		},
		onUnselect: function(event) {
			event.onComplete = function() {
				setDisable($('#doUploadStemcell'), true);
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
 	
 	/********************************************************
	 * 설명 :  스템셀 삭제
	 *********************************************************/
 	$("#doDeleteStemcell").click(function(){
 		if($("#doDeleteStemcell").attr('disabled') == "disabled") return;
 		doDeleteStemcell();
    });
 	
 	/********************************************************
	 * 설명 :  스템셀 업로드
	 *********************************************************/
 	$("#doUploadStemcell").click(function(){
 		if($("#doUploadStemcell").attr('disabled') == "disabled") return;
 		doUploadStemcell();
    });

});

/********************************************************
 * 설명 		 :  스템셀 화면 로드 초기
 * Function : initView
 *********************************************************/
function initView(bDefaultDirector) {

	 // 업로드된 스템셀 조회
	if ( bDefaultDirector ) {
		w2ui['us_uploadStemcellsGrid'].clear(); doSearchUploadedStemcells();
	}
	
	w2ui['us_localStemcellsGrid'].clear();
	// 로컬에 다운로드된 스템셀 조회
	doSearchLocalStemcells();

	//  버튼 제어 
	setDisable($('#doDeleteStemcell'), true);
	setDisable($('#doUploadStemcell'), true);
}


/********************************************************
 * 설명 :  업로드된 스템셀 조회
 * Function : doSearchUploadedStemcells
 *********************************************************/
function doSearchUploadedStemcells() {
	w2ui['us_uploadStemcellsGrid'].load("<c:url value='/info/stemcell/list/upload'/>");
}

/********************************************************
 * 설명 :  로컬에 다운로드된 스템셀 조회
 * Function : doSearchLocalStemcells
 *********************************************************/
function doSearchLocalStemcells() {
		var directorName = $("#directorName").text().toUpperCase();
		if( directorName.indexOf("_CPI") > 0  ) {
				var start = directorName.indexOf("(");
				var end = directorName.indexOf("_CPI)", start+1);
				iaas = directorName.substring(start+1, end)
		}
	w2ui['us_localStemcellsGrid'].load("<c:url value='/info/stemcell/list/local'/>");
}

/********************************************************
 * 설명 :  lock 파일 생성
 * Function : lockFileSet
 *********************************************************/
var lockFile = false;
function lockFileSet(fileName){
	var FileName = fileName.split(".tgz")[0]+"-upload";
	var message = "현재 다른 플랫폼 설치 관리자가 동일 한 스템셀을 사용 중 입니다."
	lockFile = commonLockFile("<c:url value='/common/deploy/lockFile/"+FileName+"'/>",message);
	return lockFile;
}

/********************************************************
 * 설명 :  스템셀 업로드 확인
 * Function : doUploadStemcell
 *********************************************************/
function doUploadStemcell() {
	var selected = w2ui['us_localStemcellsGrid'].getSelection();
	if ( selected == "" || selected == null) return;
	
	var record = w2ui['us_localStemcellsGrid'].get(selected);
	if ( record == "" || record == null) return;
	
	var requestParameter = {
			fileName : record.stemcellFileName,
			version: record.stemcellVersion
		};
	
	
	w2confirm( { msg : '스템셀  <br>' + record.stemcellFileName + '을(를)<br> 설치관리자에 업로드하시겠습니까?'
		, title : '스템셀 업로드'
		, yes_text:'확인'
		, no_text:'취소'
			, width        : 550
			, height       : 220
		, yes_callBack : function (){
			if(!lockFileSet(record.stemcellFileName)){
				return;
			}
			uploadLogPopup(requestParameter);
		}, no_callBack : function (){
			initView(bDefaultDirector);
		}
	});
}

/********************************************************
 * 설명 :  스템셀 업로드 로그 팝업
 * Function : uploadLogPopup
 *********************************************************/
function uploadLogPopup(requestParameter){
	var uploadLogPopupBody = '<br/><textarea name="logAppendArea" readonly="readonly" style="width:100%;height:85%;overflow-y:visible ;resize:none;background-color: #FFF;"></textarea>';
	var uploadLogPopupButton = '<button id="closeBtn" class="btn closeBtn" onclick="popupClose(); disabled">확인</button>'
	var progressLayer = '<div class="progress" style="height:6%;margin:10px 0 0 0;">';
	progressLayer += '<div class="progress-bar progress-bar-striped active" role="progressbar" ';
	progressLayer += 'aria-valuemin="0" aria-valuemax="100" style=";padding-top:0.5%;font-size:13px;"></div></div>';
	
	w2popup.open({
		title	: '<b>스템셀 업로드</b>',
		body	: progressLayer + uploadLogPopupBody,
		buttons : uploadLogPopupButton,
		width 	: 800,
		height	: 550,
		modal	: true,
		showMax : true,
		onOpen  : function(){
			doUploadConnect(requestParameter);
		},
		onClose : function(){
			$("textarea").text("");
			initView(bDefaultDirector);
			if( uploadClient != null){
				uploadClient.disconnect();
				uploadClient = null;
			}
		}
	});
}

/********************************************************
 * 설명 :  스템셀 업로드 웹소켓 연결
 * Function : doUploadConnect
 *********************************************************/
function doUploadConnect(requestParameter){
	
	var message = requestParameter.version + " 버전의 스템셀(" + requestParameter.fileName + ") ";
	
	var socket = new SockJS('/info/stemcell/upload/stemcellUploading');
	uploadClient = Stomp.over(socket); 
	uploadClient.connect({}, function(frame) {
        uploadClient.subscribe('/user/info/stemcell/upload/logs', function(data){

        	var response = JSON.parse(data.body);
	        if(requestParameter.fileName == response.tag){
	        	if ( response.messages != null ) {
	        		if(  response.state.toLowerCase() != "progress" ) {
				       	for ( var i=0; i < response.messages.length; i++) {
				       		$("textarea[name='logAppendArea']").append(response.messages[i] + "\n").scrollTop($("textarea[name='logAppendArea']")[0].scrollHeight);
				       	}
				       	
				       	if ( response.state.toLowerCase() != "started" ) {
				            if ( response.state.toLowerCase() == "done" )	message = message + " 업로드 되었습니다."; 
				    		if ( response.state.toLowerCase() == "error" ) message = message + " 업로드 중 오류가 발생하였습니다.";
				    		if ( response.state.toLowerCase() == "cancelled" ) message = message + " 업로드 중 취소되었습니다.";
				    			
				    		uploadClient.disconnect();
							w2alert(message, "스템셀 업로드");
				       	}
	        		}
	        		else { 
			       		//progressbar
			       		if( response.messages < 100){
			       			$(".w2ui-box1 .progress-bar").css("width", response.messages+"%").text("Uploading "+response.messages+"% ");
			       		}
			       		else if( response.messages = 100){
			       			$(".w2ui-box1 .progress-bar").css("width", "100%").text("Uploaded");
			       		}
			       	}
	        	}
	        }
        });
        uploadClient.send('/app/info/stemcell/upload/stemcellUploading', {}, JSON.stringify(requestParameter));
    });
}

/********************************************************
 * 설명 :  업로드된 스템셀 삭제
 * Function : doDeleteStemcell
 *********************************************************/
function doDeleteStemcell() {
	var selected = w2ui['us_uploadStemcellsGrid'].getSelection();
	if ( selected == "" || selected == null) return;
	
	var record = w2ui['us_uploadStemcellsGrid'].get(selected);
	if ( record == "" || record == null) return;

	var requestParameter = {
			stemcellName : record.stemcellFileName,
			version  : record.stemcellVersion
		};
	
	w2confirm( { msg : record.stemcellVersion + '버전의 스템셀 <br>' + record.stemcellFileName + '<br>을 삭제하시겠습니까?'
		, title : '스템셀 삭제'
		, width        : 550
		, height       : 220
		, yes_text:'확인'
		, no_text:'취소'
		, yes_callBack : function (){
				deleteLogPopup(requestParameter);	
			}
		, no_callBack : function(){
			initView(bDefaultDirector);
		}
	});	
}

/********************************************************
 * 설명 :  스템셀 삭제 로그 팝업
 * Function : deleteLogPopup
 *********************************************************/
function deleteLogPopup(requestParameter){
	var deleteLogPopupBody = '<br/><textarea name="logAppendArea" readonly="readonly" style="width:100%;height:95%;overflow-y:visible ;resize:none;background-color: #FFF;"></textarea>';
	var deleteLogPopupButton = '<button id="closeBtn" class="btn closeBtn" onclick="popupClose(); disabled">확인</button>';
	
	w2popup.open({
		title	: '<b>스템셀 삭제</b>',
		body	: deleteLogPopupBody,
		buttons : deleteLogPopupButton,
		width 	: 800,
		height	: 550,
		modal	: true,
		showMax : true,
		onOpen  : function(){
			doDeleteConnect(requestParameter);
		},
		onClose : function(){
			if(deleteClient != null){
			$("textarea").text("");
			deleteClient.disconnect();
			deleteClient = null;
			}
			initView(bDefaultDirector);
		}
	});
}

/********************************************************
 * 설명 :  스템셀 삭제 웹소켓 연결
 * Function : doDeleteConnect
 *********************************************************/
function doDeleteConnect(requestParameter){
	
	var message = requestParameter.version + " 버전의 스템셀(" + requestParameter.stemcellName + ") ";
	var socket = new SockJS('/info/stemcell/delete/stemcellDelete');
	deleteClient = Stomp.over(socket); 
	deleteClient.connect({}, function(frame) {
     deleteClient.subscribe('/user/info/stemcell/delete/logs', function(data){
	        var response = JSON.parse(data.body);
	        
	        if ( response.messages != null ) {
		       	for ( var i=0; i < response.messages.length; i++) {
		       		$("textarea[name='logAppendArea']").append(response.messages[i] + "\n").scrollTop($("textarea[name='logAppendArea']")[0].scrollHeight);
		       	}
		       	
		       	if ( response.state.toLowerCase() != "started" ) {
		            if ( response.state.toLowerCase() == "done" )	message = message + " 삭제되었습니다."; 
		    		if ( response.state.toLowerCase() == "error" ) message = message + " 삭제 중 오류가 발생하였습니다.";
		    		if ( response.state.toLowerCase() == "cancelled" ) message = message + " 삭제 중 취소되었습니다.";
		    		deleteClient.disconnect();
					w2alert(message, "스템셀 삭제");
		       	}
	        }
     });
     deleteClient.send('/app/info/stemcell/delete/stemcellDelete', {}, JSON.stringify(requestParameter));
 });
}

/********************************************************
 * 설명		: 팝업 닫을 경우 Socket Connection 종료 및 log 영역 초기화
 * Function	: popupClose
 *********************************************************/
function popupClose() {
	if (uploadClient != null) {
		uploadClient.disconnect();
		$("textarea[name='logAppendArea']").text("");
	}
	
	if (deleteClient != null) {
		deleteClient.disconnect();
		$("textarea[name='logAppendArea']").text("");
	}
	
	w2popup.close();
	
	// 업로드된 스템셀 조회
 	doSearchUploadedStemcells();
 	setDisable($('#doUploadStemcell'), true);
	
}

/********************************************************
 * 설명		: 다른페이지 이동시 호출
 * Function	: clearMainPage
 *********************************************************/
function clearMainPage() {
	$().w2destroy('us_uploadStemcellsGrid');
	$().w2destroy('us_localStemcellsGrid');
}

/********************************************************
 * 설명		: 버튼 제어
 * Function	: setDisable
 *********************************************************/
function setDisable(object, flag) {
	object.attr('disabled', flag);
}

/********************************************************
 * 설명		: 화면 리사이즈시 호출
 * Function	: clearMainPage
 *********************************************************/
$( window ).resize(function() {
	setLayoutContainerHeight();
});
</script>

<div id="main">
	<div class="page_site">정보조회 > <strong>스템셀 업로드</strong></div>
	
	<!-- 설치 관리자 -->
	<div id="isDefaultDirector"></div>
	
	<!-- 업로드된 스템셀 목록-->
	<div class="pdt20">
		<div class="title fl">업로드된 스템셀 목록</div>
		<div class="fr"> 
			<sec:authorize access="hasAuthority('INFO_STEMCELL_DELETE')">
			<span class="btn btn-danger" style="width:120px" id="doDeleteStemcell">스템셀 삭제</span>
			</sec:authorize>
	    </div>
	</div>
	<div id="us_uploadStemcellsGrid" style="width:100%; height:278px"></div>
	
	<!-- 로컬 스템셀 목록-->
	<div class="pdt20">
		<div class="title fl">다운로드된 스템셀 목록</div>
		<div class="fr"> 
			<sec:authorize access="hasAuthority('INFO_STEMCELL_UPLOAD')">
			<span class="btn btn-primary" style="width:120px" id="doUploadStemcell">스템셀 업로드</span>
			</sec:authorize>
	    </div>
	</div>
		
	<div id="us_localStemcellsGrid" style="width:100%; height:278px"></div>
</div>
