<%
/* =================================================================
 * 작성일 : 
 * 작성자 : 
 * 상세설명 : 
 * =================================================================
 * 수정일         작성자             내용     
 * -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * 2016.12       이동현        목록 화면 개선 및 코드 버그 수정
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<script type="text/javascript">

var uploadClient = "";
var deleteClient = "";
var bDefaultDirector = "";
$(function() {
	
	 /********************************************************
	  * 설명		: 기본 설치 관리자 정보 조회
	  *********************************************************/
 	bDefaultDirector = getDefaultDirector("<c:url value='/common/use/director'/>");
 	
 	/********************************************************
	 * 설명 :  업로드된 릴리즈 목록
	 *********************************************************/
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
		       , {field: 'jobNames', caption: 'Job템플릿', size: '70%', style: 'text-align:left'}
		       ],
		onSelect: function(event) {
			event.onComplete = function() {
				setDisable($('#doDeleteRelease'), false);
			}
		},
		onUnselect: function(event) {
			event.onComplete = function() {
				setDisable($('#doDeleteRelease'), true);
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
	 * 설명 :  다운로드된 릴리즈 목록
	 *********************************************************/
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
		       , {field: 'releaseFileName', caption: '릴리즈 파일명', size: '50%', style: 'text-align:left'}
		       , {field: 'releaseSize', caption: '릴리즈 파일크기', size: '50%', style: 'text-align:right'}		       
		       ],
       onSelect: function(event) {
			event.onComplete = function() {
				if (  bDefaultDirector ) {
					setDisable($('#doUploadRelease'), false);
				}
			}
		},
		onUnselect: function(event) {
			event.onComplete = function() {
				setDisable($('#doUploadRelease'), true);
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
	 * 설명 :  업로드 된 릴리즈 삭제
	 *********************************************************/
 	$("#doDeleteRelease").click(function(){
 		if($("#doDeleteRelease").attr('disabled') == "disabled") return;
 		doDeleteRelease();
    });
 	
 	/********************************************************
	 * 설명 :  릴리즈 업로드
	 *********************************************************/
 	$("#doUploadRelease").click(function(){
 		if($("#doUploadRelease").attr('disabled') == "disabled") return;
 		LocalReleaseUpload("upload");
    });
 	
 	initView(bDefaultDirector);
 	
});


/********************************************************
 * 설명 		 :  릴리즈 화면 로드 초기 버튼 스타일 설정
 * Function : initView
 *********************************************************/
function initView(bDefaultDirector) {
	
	if ( bDefaultDirector ) {  doSearchUploadedReleases(); }
	
	// 로컬에 다운로드된 릴리즈 조회
	w2ui['ru_localReleasesGrid'].clear();
	doSearchLocalReleases();
	
	
	//버튼 제어 
	setDisable($('#doDeleteRelease'), true);
	setDisable($('#doUploadRelease'), true);
	
}

function setDisable(object, flag) {
	object.attr('disabled', flag);
}

/********************************************************
 * 설명 :  업로드된 릴리즈 조회
 * Function : doSearchUploadedReleases
 *********************************************************/
function doSearchUploadedReleases() {
	w2ui['ru_uploadedReleasesGrid'].load("<c:url value='/info/release/list/upload'/>");
}

/********************************************************
 * 설명 :  로컬에 다운로드된 릴리즈 조회
 * Function : doSearchLocalReleases
 *********************************************************/
function doSearchLocalReleases() {
	w2ui['ru_localReleasesGrid'].load("<c:url value='/info/release/list/local'/>");
}

/********************************************************
 * 설명 :  릴리즈 업로드 확인
 * Function : LocalReleaseUpload
 *********************************************************/
function LocalReleaseUpload(op) {
	var message = "";
	var selected = w2ui['ru_localReleasesGrid'].getSelection();
	if ( selected == "" || selected == null) return;
	
	var record = w2ui['ru_localReleasesGrid'].get(selected);
	if ( record == "" || record == null) return;

	var requestParameter = {
			fileName : record.releaseFileName
	};
	
	if ( op == "upload" )
		message = record.releaseFileName + '릴리즈 파일을<BR>설치관리자에 업로드 하시겠습니까?'
	else return;
	
	w2confirm( { msg : message
		, title : '릴리즈'
		, yes_text:'확인'
		, no_text:'취소'
		})
		.yes(function() {
			if(!lockFileSet(record.releaseFileName)){
				return;
			}
			if ( op == "upload" )
				uploadLogPopup(requestParameter);
		})
		.no(function() {
			initView();
		});	
}

/********************************************************
 * 설명 :  lock 검사
 * Function : lockFileSet
 *********************************************************/
var lockFile = false;
function lockFileSet(releaseFile){
	var FileName = "";
	if( releaseFile.indexOf(".") > -1 ){
		var pathHeader = releaseFile;
	 	var pathMiddle = releaseFile.lastIndexOf('.');
		var pathEnd = releaseFile.length;
		FileName = releaseFile.substring(pathHeader,pathMiddle)+"-upload";
	}
	var message = "현재 다른 플랫폼 설치 관리자가 동일 한 릴리즈를 사용 중 입니다."
	lockFile = commonLockFile("<c:url value='/common/deploy/lockFile/"+FileName+"'/>",message);
	return lockFile;
}

/********************************************************
 * 설명 :  릴리즈 업로드 로그 팝업
 * Function : uploadLogPopup
 *********************************************************/
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
		},  onClose : function(event){
			event.onComplete= function(){
				$("textarea").text("");
				initView();
				if( uploadClient != ""){
					uploadClient.disconnect();
					uploadClient = "";
				}
			}
		},
		onClose : function(event){
			initView(bDefaultDirector);
		}
	});
}

/********************************************************
 * 설명 :  릴리즈 업로드 웹소켓 연결
 * Function : doUploadConnect
 *********************************************************/
function doUploadConnect(requestParameter){
	
	var message = "릴리즈(" + requestParameter.fileName + ") ";
	
	var socket = new SockJS('/info/release/upload/releaseUploading');
	uploadClient = Stomp.over(socket); 
	uploadClient.connect({}, function(frame) {
        uploadClient.subscribe('/user/info/release/upload/socket/logs', function(data){
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
	        	} else{
	        		//progressbar
		       		if( response.messages < 100){
		       			$(".w2ui-box1 .progress-bar").css("width", response.messages+"%").text("Uploading "+response.messages+"% ");
		       		}
		       		else if( response.messages = 100){
		       			$(".w2ui-box1 .progress-bar").css("width", "100%").text("Uploaded");
		       		}
	        	}
        	}
        });
        uploadClient.send('/app/info/release/upload/releaseUploading', {}, JSON.stringify(requestParameter));
	});
}


/********************************************************
 * 설명 :  업로드된 릴리즈 삭제
 * Function : doDeleteRelease
 *********************************************************/
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
		w2ui['ru_uploadedReleasesGrid'].reset();
		initView(bDefaultDirector);
	});	
}

/********************************************************
 * 설명 :  릴리즈 삭제 로그 팝업
 * Function : doDeleteRelease
 *********************************************************/
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
		},
		onClose : function(event){
			event.onComplete= function(){
				$("textarea").text("");
				initView(bDefaultDirector);
				deleteClient.disconnect();
				deleteClient = "";
			}
		}
	});
}

/********************************************************
 * 설명 :  릴리즈 삭제 웹소켓 연결
 * Function : doDeleteConnect
 *********************************************************/
function doDeleteConnect(requestParameter){
	
	var message = requestParameter.version + " 버전의 릴리즈(" + requestParameter.fileName + ") ";
	
	var socket = new SockJS('/info/release/delete/releaseDelete');
	deleteClient = Stomp.over(socket); 
	deleteClient.connect({}, function() {
        deleteClient.subscribe('/user/info/release/delete/socket/logs', function(data){
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
        deleteClient.send('/app/info/release/delete/releaseDelete', {}, JSON.stringify(requestParameter));
    });
}


/********************************************************
 * 설명		: 팝업 닫을 경우 Socket Connection 종료 및 log 영역 초기화
 * Function	: popupClose
 *********************************************************/
function popupClose() {
	if (uploadClient != "") {
		uploadClient.disconnect();
		$("textarea[name='logAppendArea']").text("");
		uploadClient = "";
	}
	
	if (deleteClient != "") {
		deleteClient.disconnect();
		$("textarea[name='logAppendArea']").text("");
		deleteClient ="";
	}
	
	w2popup.close();
	
	// 업로드된 릴리즈 조회
 	doSearchUploadedReleases();
 	setDisable($('#doUploadRelease'), true);
}

/********************************************************
 * 설명		: 다른페이지 이동시 호출
 * Function	: clearMainPage
 *********************************************************/
function clearMainPage() {
	$().w2destroy('ru_uploadedReleasesGrid');
	$().w2destroy('ru_localReleasesGrid');
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
	<div class="page_site">정보조회 > <strong>릴리즈 업로드</strong></div>
	
	<!-- 설치 관리자 -->
	<div id="isDefaultDirector"></div>
	
	<!-- 릴리즈 목록-->
	<div class="pdt20"> 
		<div class="title fl">업로드된 릴리즈 목록</div>
		<div class="fr">
		<sec:authorize access="hasAuthority('INFO_RELEASE_UPLOAD')">
			<span class="btn btn-danger" style="width:120px" id="doDeleteRelease">릴리즈 삭제</span>
		</sec:authorize>
	    </div>
	</div>
	<div id="ru_uploadedReleasesGrid" style="width:100%; height:298px"></div>	
	<div class="pdt20"> 
		<div class="title fl">다운로드된 릴리즈 목록</div>
		<div class="fr">
		<sec:authorize access="hasAuthority('INFO_RELEASE_DELETE')">
			<span class="btn btn-primary" style="width:120px" id="doUploadRelease">릴리즈 업로드</span> 
		</sec:authorize>
		</div>
	</div>
	<div id="ru_localReleasesGrid" style="width:100%; height:298px"></div>
</div>
