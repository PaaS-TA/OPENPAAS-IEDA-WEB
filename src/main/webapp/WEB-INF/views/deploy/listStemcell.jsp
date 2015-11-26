<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="/js/sockjs-0.3.4.js"></script>
<script type="text/javascript" src="/js/stomp.js"></script>
<script type="text/javascript">
var uploadClient = null;
var deleteClient = null;
var appendLogPopupBody = '<br/><textarea name="logAppendArea" readonly="readonly" style="width:100%;height:430px;overflow-y:visible ;resize:none;background-color: #FFF;"></textarea>';
var appendLogPopupButton = '<button class="btn closeBtn" onclick="popupClose();">닫기</button>'

$(function() {
	
 	// 기본 설치 관리자 정보 조회
 	var bDefaultDirector = getDefaultDirector("<c:url value='/directors/default'/>");
 	
 	$('#us_uploadStemcellsGrid').w2grid({
		name	: 'us_uploadStemcellsGrid',
		show: {selectColumn: true, footer: true},
		multiSelect: false,
		method: 'GET',
		style: 'text-align:center',
		columns	:[
		           {field: 'recid', caption: 'recid', hidden: true}
		         , {field: 'operating_system', caption: 'OS', size: '20%'}
		         , {field: 'name', caption: '스템셀명', size: '30%'}		         
		         , {field: 'version', caption: '스템셀버전', size: '10%'}
		         , {field: 'cid', caption: 'CID', size: '30%'}
		         ],
 		onClick: function(event) {
			var grid = this;
			event.onComplete = function() {
 				var sel = grid.getSelection();
				if ( sel == null || sel == "") {
					setDisable($('#doDeleteStemcell'), true);
					return;
				}
				else{
					setDisable($('#doDeleteStemcell'), false);
				}
			}
		},
		onError: function(event) {
			this.unlock();
			gridErrorMsg(event);
		}
	});
	
 	$('#us_localStemcellsGrid').w2grid({
		name: 'us_localStemcellsGrid',
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
		onClick: function(event) {
			var grid = this;
			event.onComplete = function() {
 				var sel = grid.getSelection();
				if ( sel == null || sel == "") {
					setDisable($('#doUploadStemcell'), true);
					return;
				}
				
				setDisable($('#doUploadStemcell'), false);
			}
		}
		
	});
 	
 	initView(bDefaultDirector);
 	
 	// 스템셀 삭제
 	$("#doDeleteStemcell").click(function(){
 		doDeleteStemcell();
    });
 	
 	// 스템셀 업로드
 	$("#doUploadStemcell").click(function(){
 		doUploadStemcell();
    });

});


function initView(bDefaultDirector) {
	if ( bDefaultDirector ) {
		// 업로드된 스템셀 조회
	 	doSearchUploadedStemcells();
		
		// 로컬에 다운로드된 스템셀 조회
		doSearchLocalStemcells();
	}

	// 컨트롤 
	setDisable($('#doDeleteStemcell'), true);
	setDisable($('#doUploadStemcell'), true);
}

function setDisable(object, flag) {
	object.attr('disabled', flag);
}

//업로드된 스템셀 조회
function doSearchUploadedStemcells() {
	w2ui['us_uploadStemcellsGrid'].load("<c:url value='/stemcells'/>");
}

//로컬에 다운로드된 스템셀 조회
function doSearchLocalStemcells() {
	w2ui['us_localStemcellsGrid'].load("<c:url value='/localStemcells'/>");
}

//다른페이지 이동시 호출
function clearMainPage() {
	$().w2destroy('us_uploadStemcellsGrid');
	$().w2destroy('us_localStemcellsGrid');
}

// 스템셀 삭제
function doDeleteStemcell() {
	var selected = w2ui['us_uploadStemcellsGrid'].getSelection();
	if ( selected == "" || selected == null) return;
	
	var record = w2ui['us_uploadStemcellsGrid'].get(selected);
	if ( record == "" || record == null) return;

	var requestParameter = {
			fileName : record.name,
			version  : record.version
		};
	
	w2confirm( { msg : '설치관리자에 업로드된 스템셀 <br>' + record.name + '<br>을 삭제하시겠습니까?'
		, title : '스템셀 삭제'
		, yes_text:'확인'
		, no_text:'취소'
	})
	.yes(function() {
		appendLogPopup("delete",requestParameter);

	})
	.no(function() {
		// do nothing
	});
}

//스템셀 업로드
function doUploadStemcell() {
	var selected = w2ui['us_localStemcellsGrid'].getSelection();
	if ( selected == "" || selected == null) return;
	
	var record = w2ui['us_localStemcellsGrid'].get(selected);
	if ( record == "" || record == null) return;
	
	var requestParameter = {
			fileName : record.stemcellFileName
		};
	
	w2confirm( { msg : '선택된 스템셀  <br>' + record.stemcellFileName + ' <br>을 설치관리자에 업로드하시겠습니까?'
		, title : '스템셀 업로드'
		, yes_text:'확인'
		, no_text:'취소'
		})
		.yes(function() {
			appendLogPopup("upload",requestParameter);
		})
		.no(function() {
			// do nothing
		});	
}

//Log Popup Create
function appendLogPopup(type, requestParameter){
	console.log("============================================================================");
	w2popup.open({
		title	: (type =="upload") ? '<b>스템셀 업로드 로그</b>': '<b>스템셀 삭제 로그</b>',
		body	: appendLogPopupBody,
		buttons : appendLogPopupButton,
		width 	: 800,
		height	: 550,		
		modal	: true,		
		onOpen  : function(){
			if(type =="upload") doUploadConnect(requestParameter);
			else doDeleteConnect(requestParameter);
		}
	});
}


//Stemcell Upload connect
function doUploadConnect(requestParameter){
	console.log("2.=====doUploadConnect=====");
	var socket = new SockJS('/stemcellUploading');
	uploadClient = Stomp.over(socket); 
	uploadClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        uploadClient.subscribe('/socket/uploadStemcell', function(data){
        	$("textarea[name='logAppendArea']").append(data.body + "\n").scrollTop($("textarea[name='logAppendArea']")[0].scrollHeight);
        });
        socketSendUploadData( requestParameter);
    });
}

//Stemcell Delete connect
function doDeleteConnect(requestParameter){
	console.log("2.=====doDeleteConnect=====");
	var socket = new SockJS('/stemcellDelete');
	deleteClient = Stomp.over(socket); 
	deleteClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        deleteClient.subscribe('/socket/deleteStemcell', function(data){
        	$("textarea[name='logAppendArea']").append(data.body + "\n").scrollTop($("textarea[name='logAppendArea']")[0].scrollHeight);
        });
        socketSendDeleteData(requestParameter);
    });
}

//socket으로 데이터 전송
function socketSendUploadData(requestParameter){
	uploadClient.send('/app/stemcellUploading', {}, JSON.stringify(requestParameter));
}

function socketSendDeleteData(requestParameter){
	deleteClient.send('/app/stemcellDelete', {}, JSON.stringify(requestParameter));
}

//팝업 닫을 경우 Socket Connection 종료 및 log 영역 초기화
function popupClose() {
	if (uploadClient != null) {
		uploadClient.disconnect();
		$("textarea[name='logAppendArea']").text("");
	}
	else if (deleteClient != null) {
		deleteClient.disconnect();
		$("textarea[name='logAppendArea']").text("");
	}
	
	console.log("Disconnected");
	w2popup.close();
	
	// 업로드된 스템셀 조회
 	doSearchUploadedStemcells();
 	setDisable($('#doUploadStemcell'), true);
	
}

//화면 리사이즈시 호출
$( window ).resize(function() {
	setLayoutContainerHeight();
});
</script>

<div id="main">
	<div class="page_site">서비스 설치관리 > <strong>스템셀 업로드</strong></div>
	
	<!-- 설치 관리자 -->
	<div class="title">설치 관리자</div>
	
	<table class="tbl1" border="1" cellspacing="0">
	<tr>
		<th width="18%" class="th_fb">관리자 이름</th><td class="td_fb"><b id="directorName"></b></td>
		<th width="18%" class="th_fb">관리자 계정</th><td class="td_fb"><b id="userId"></b></td>
	</tr>
	<tr>
		<th width="18%" >관리자 URL</th><td><b id="directorUrl"></b></td>
		<th width="18%" >관리자 UUID</th><td ><b id="directorUuid"></b></td>
	</tr>
	</table>
	
	<!-- 업로드된 스템셀 목록-->
	<div class="pdt20">
		<div class="title fl">디렉터에 업로드된 스템셀 목록</div>
		<div class="fr"> 
			<span class="btn btn-danger" style="width:120px" id="doDeleteStemcell">스템셀 삭제</span>
	    </div>
	</div>
	<div id="us_uploadStemcellsGrid" style="width:100%; height:200px"></div>
	
	<!-- 로컬 스템셀 목록-->
	<div class="pdt20">
		<div class="title fl">다운로드된 스템셀 목록</div>
		<div class="fr"> 
			<span class="btn btn-primary" style="width:120px" id="doUploadStemcell">스템셀 업로드</span>
	    </div>
	</div>
		
	<div id="us_localStemcellsGrid" style="width:100%; height:200px"></div>
</div>
