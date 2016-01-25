<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript" src="<c:url value='/js/sockjs-0.3.4.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/stomp.js'/>"></script>

<script type="text/javascript">

var uploadClient = null;
var deleteClient = null;

$(function() {
 	// 기본 설치 관리자 정보 조회
 	var bDefaultDirector = getDefaultDirector("<c:url value='/directors/default'/>");
 	
 	$('#ru_uploadedReleasesGrid').w2grid({
		name	: 'ru_uploadedReleasesGrid',
		show	: {	
					selectColumn: true	,
					footer: true
					},
		multiSelect: false,
		method	: 'GET',
		style	: 'text-align:center',
		columns	:[
		         {field: 'recid', caption: 'recid', hidden: true}
		       , {field: 'name', caption: '릴리즈명', size: '20%'}
		       , {field: 'version', caption: '릴리즈버전', size: '10%'}
/* 		       , {field: 'currentDeployed', caption: '배포 여부', size: '15%',
		    	   render: function(record) {
		    		   if ( record.currentDeployed == 'true' )
		    			   return '<span class="btn btn-success" style="width:70px">배포</span>';
		    		   else
		    			   return '';
		    	   }
		       } */
		       , {field: 'jobNames', caption: 'Job템플릿', size: '70%', style: 'text-align:left'}
		       ],
		onClick: function(event) {
			var grid = this;
			event.onComplete = function() {
				var sel = grid.getSelection();
				if ( sel == null || sel == "") {
					setDisable($('#doDeleteRelease'), true);
					return;
				}
				else{
					setDisable($('#doDeleteRelease'), false);
				}
			}
		},
		onError: function(event) {
			this.unlock();
			gridErrorMsg(event);
		}
	});
 	
 	$('#ru_localReleasesGrid').w2grid({
		name	: 'ru_localReleasesGrid',
		show	: {	
					selectColumn: true,
					footer: true
					},
		multiSelect: false,
		method 	: "GET",
		style	: 'text-align:center',
		columns	:[
		         {field: 'recid', caption: 'recid', hidden: true}
		       , {field: 'releaseFile', caption: '릴리즈 파일명', size: '50%', style: 'text-align:left'}
		       , {field: 'releaseFileSize', caption: '릴리즈 파일크기', size: '50%', style: 'text-align:right'}		       
		       ],
		onClick: function(event) {
			var grid = this;
			event.onComplete = function() {
				var sel = grid.getSelection();
				if ( sel == null || sel == "" || bDefaultDirector == false) {
					setDisable($('#doUploadRelease'), true);
					setDisable($('#doDeleteLocalRelease'), true);
					return;
				}
				else{
					setDisable($('#doUploadRelease'), false);
					setDisable($('#doDeleteLocalRelease'), false);
				}
			}
		},
		onError: function(event) {
			this.unlock();
			gridErrorMsg(event);
		}
	});
 	
 	initView(bDefaultDirector);
 	
 	//릴리즈 삭제
 	$("#doDeleteRelease").click(function(){
 		if($("#doDeleteRelease").attr('disabled') == "disabled") return;
 		doDeleteRelease();
    });
 	
 	//릴리즈 업로드
 	$("#doUploadRelease").click(function(){
 		if($("#doUploadRelease").attr('disabled') == "disabled") return;
 		LocalStemcellUploadOrDelete("upload");
    });
 	
 	//로컬 릴리즈 삭제
 	$("#doDeleteLocalRelease").click(function(){
 		if($("#doDeleteLocalRelease").attr('disabled') == "disabled") return;
 		LocalStemcellUploadOrDelete("delete");
    });
 	
});

function initView(bDefaultDirector) {
	
	if ( bDefaultDirector ) { 
		// 업로드된 릴리즈 조회
	 	doSearchUploadedReleases();
		
	}
	
	// 로컬에 다운로드된 릴리즈 조회
	doSearchLocalReleases();


	// 컨트롤 
	setDisable($('#doDeleteRelease'), true);
	setDisable($('#doUploadRelease'), true);
	setDisable($('#doDeleteLocalRelease'), true);
	
}

function setDisable(object, flag) {
	object.attr('disabled', flag);
}

//업로드된 릴리즈 조회
function doSearchUploadedReleases() {
	w2ui['ru_uploadedReleasesGrid'].load("<c:url value='/releases'/>");
}

//로컬에 다운로드된 릴리즈 조회
function doSearchLocalReleases() {
	w2ui['ru_localReleasesGrid'].load("<c:url value='/localReleases'/>");
}

//다른페이지 이동시 호출
function clearMainPage() {
	$().w2destroy('ru_uploadedReleasesGrid');
	$().w2destroy('ru_localReleasesGrid');
}

//화면 리사이즈시 호출
$( window ).resize(function() {
	setLayoutContainerHeight();
});

//릴리즈 삭제
function doDeleteRelease() {
	var selected = w2ui['ru_uploadedReleasesGrid'].getSelection();
	if ( selected == "" || selected == null) return;
	
	var record = w2ui['ru_uploadedReleasesGrid'].get(selected);
	if ( record == "" || record == null) return;

	var requestParameter = {
			fileName : record.name,
			version  : record.version
		};
	
	w2confirm( { msg : record.version + '버전의 ' + record.name + ' 릴리즈를 삭제하시겠습니까?'
		, title : '릴리즈 삭제'
		, yes_text:'확인'
		, no_text:'취소'
	})
	.yes(function() {
		deleteLogPopup(requestParameter);

	})
	.no(function() {
		// do nothing
	});	
}

// delete or upload local release
function LocalStemcellUploadOrDelete(op) {
	
	var selected = w2ui['ru_localReleasesGrid'].getSelection();
	if ( selected == "" || selected == null) return;
	
	var record = w2ui['ru_localReleasesGrid'].get(selected);
	if ( record == "" || record == null) return;
	
	var requestParameter = {
			fileName : record.releaseFile
		};
	
	var message = "";
	if ( op == "upload" )
		message = record.releaseFile + '릴리즈 파일을<BR>설치관리자에 업로드 하시겠습니까?'
	else if ( op == "delete" )
		message = record.releaseFile + ' 릴리즈 파일을 삭제하시겠습니까?';
	else
		return;
	
	w2confirm( { msg : message
		, title : '릴리즈'
		, yes_text:'확인'
		, no_text:'취소'
		})
		.yes(function() {
			if ( op == "upload" )
				uploadLogPopup(requestParameter);
			else if ( op == "delete" )
				doDeleteLocalRelease(requestParameter);
		})
		.no(function() {
			// do nothing
		});	
}

// Delete Local Release
function doDeleteLocalRelease(requestParameter) {
	 
	w2popup.lock("로컬 릴리즈 삭제중...", true);
	
	$.ajax({
		type : "DELETE",
		url : "/deleteLocalRelease",
		contentType : "application/json",
		data : JSON.stringify(requestParameter),
		success : function(data, status) {
			w2popup.unlock();
			w2popup.close();
			
			w2alert(requestParameter.fileName + " 릴리즈 파일이 삭제되었습니다.", "로컬 릴리즈 삭제");
			
			doSearchLocalReleases();
			w2ui['ru_localReleasesGrid'].reset();
		},
		error : function(request, status, error) {
			w2popup.unlock();
			var errorResult = JSON.parse(request.responseText);
			w2alert(errorResult.message, "로컬 릴리즈 삭제");
		}
	});
}

//Log Popup Create
function uploadLogPopup(requestParameter){
	var uploadLogPopupBody = '<br/><textarea name="logAppendArea" readonly="readonly" style="width:100%;height:85%;overflow-y:visible ;resize:none;background-color: #FFF;"></textarea>';
	var uploadLogPopupButton = '<button id="closeBtn" class="btn closeBtn" onclick="popupClose(); disabled">확인</button>'
	var progressLayer = '<div class="progress" style="height:6%;margin:10px 0 0 0;">';
	progressLayer += '<div class="progress-bar progress-bar-striped active" role="progressbar" ';
	progressLayer += 'aria-valuemin="0" aria-valuemax="100" style=";padding-top:0.5%;font-size:13px;"></div></div>';
	
	w2popup.open({
		title	: '<b>릴리즈 업로드</b>',
		body	: progressLayer + uploadLogPopupBody,
		buttons : uploadLogPopupButton,
		width 	: 800,
		height	: 550,
		modal	: true,
		showMax : true,
		onOpen  : function(){
			doUploadConnect(requestParameter);
		}
	});
}

//Log Popup Create
function deleteLogPopup(requestParameter){
	var deleteLogPopupBody = '<br/><textarea name="logAppendArea" readonly="readonly" style="width:100%;height:95%;overflow-y:visible ;resize:none;background-color: #FFF;"></textarea>';
	var deleteLogPopupButton = '<button id="closeBtn" class="btn closeBtn" onclick="popupClose(); disabled">확인</button>';
	
	w2popup.open({
		title	: '<b>릴리즈 삭제</b>',
		body	: deleteLogPopupBody,
		buttons : deleteLogPopupButton,
		width 	: 800,
		height	: 550,
		modal	: true,
		showMax : true,
		onOpen  : function(){
			doDeleteConnect(requestParameter);
		}
	});
}

//Release Upload connect
function doUploadConnect(requestParameter){
	
	var message = "릴리즈(" + requestParameter.fileName + ") ";
	
	var socket = new SockJS('/releaseUploading');
	uploadClient = Stomp.over(socket); 
	uploadClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        uploadClient.subscribe('/socket/uploadRelease', function(data){
        	var response = JSON.parse(data.body);
        	if(requestParameter.fileName == response.tag){
	        	if(  response.state.toLowerCase() != "progress" ) {
		        	if ( response.messages != null ) {
				       	for ( var i=0; i < response.messages.length; i++) {
				       		$("textarea[name='logAppendArea']").append(response.messages[i] + "\n").scrollTop($("textarea[name='logAppendArea']")[0].scrollHeight);
				       	}
				       	
				       	if ( response.state.toLowerCase() != "started" ) {
				            if ( response.state.toLowerCase() == "done" )	message = message + " 업로드 되었습니다."; 
				    		if ( response.state.toLowerCase() == "error" ) message = message + " 업로드 중 오류가 발생하였습니다.";
				    		if ( response.state.toLowerCase() == "cancelled" ) message = message + " 업로드 중 취소되었습니다.";
				    			
				    		uploadClient.disconnect();
							w2alert(message, "릴리즈 업로드");
				       	}
		        	}
	        	}
	        	else{
	        		//progressbar
		       		console.log("#### :" + data);
		       		if( response.messages < 100){
		       			$(".w2ui-box1 .progress-bar").css("width", response.messages+"%").text("Uploading "+response.messages+"% ");
		       		}
		       		else if( response.messages = 100){
		       			$(".w2ui-box1 .progress-bar").css("width", "100%").text("Uploaded");
		       		}
	        	}
        	}
        });
        socketSendUploadData(requestParameter);
    });
}

//Release Delete connect
function doDeleteConnect(requestParameter){
	
	var message = requestParameter.version + " 버전의 릴리즈(" + requestParameter.fileName + ") ";
	
	var socket = new SockJS('/releaseDelete');
	deleteClient = Stomp.over(socket); 
	deleteClient.connect({}, function() {
        deleteClient.subscribe('/socket/deleteRelease', function(data){
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
					w2alert(message, "릴리즈 삭제");
		       	}
	        }
        });
        socketSendDeleteData(requestParameter);
        
    });
}

function socketSendUploadData(requestParameter){
	uploadClient.send('/app/releaseUploading', {}, JSON.stringify(requestParameter));
}

function socketSendDeleteData(requestParameter){
	deleteClient.send('/app/releaseDelete', {}, JSON.stringify(requestParameter));
}

//팝업 닫을 경우 Socket Connection 종료 및 log 영역 초기화
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
	
	// 업로드된 릴리즈 조회
 	doSearchUploadedReleases();
 	setDisable($('#doUploadRelease'), true);
}



</script>

<div id="main">
	<div class="page_site">정보조회 > <strong>릴리즈 업로드</strong></div>
	
	<!-- 설치 관리자 -->
	<div id="isDefaultDirector"></div>
	
	<!-- 릴리즈 목록-->
	<div class="pdt20"> 
		<div class="title fl">업로드된 릴리즈 목록</div>
		<div class="fr">
			<span class="btn btn-danger" style="width:120px" id="doDeleteRelease">릴리즈 삭제</span>
	    </div>
	</div>
	<div id="ru_uploadedReleasesGrid" style="width:100%; height:200px"></div>	
	<div class="pdt20"> 
		<div class="title fl">다운로드된 릴리즈 목록</div>
		<div class="fr">
			<span class="btn btn-primary" style="width:120px" id="doUploadRelease">릴리즈 업로드</span> 
			<span class="btn btn-danger" style="width:120px" id="doDeleteLocalRelease">릴리즈 삭제</span>
		</div>
	</div>
	<div id="ru_localReleasesGrid" style="width:100%; height:200px"></div>
</div>
