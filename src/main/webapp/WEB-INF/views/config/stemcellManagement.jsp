<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
	.ui-progressbar {
	  position: relative;
	}
	.progress-label {
	  position: absolute;
	  left: 30%;
	  top: 4px;	  
	  font-weight: bold;
	  text-shadow: 1px 1px #58ACFA;
	}
	.ui-progressbar {
		width: 70px;
		height: 20px;
		text-align: left;
		overflow: hidden;
		/* margin-left: 20px; */
	}
	.ui-progressbar .ui-progressbar-value {
		margin: -1px;
		height: 100%;
	}
</style>

<script type="text/javascript" src="<c:url value='/js/sockjs-0.3.4.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/stomp.js'/>"></script>

<script type="text/javascript">
var downloadClient = "";

var completeButton = '<div class="btn btn-success btn-xs" style="width:100px;">Downloaded</div>';
var downloadingButton = '<div class="btn btn-info btn-xs" style="width:100px;">Downloading</div>';

$(function() {
 	$('#config_opStemcellsGrid').w2grid({
		name: 'config_opStemcellsGrid',
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
			,{field: 'stemcellFileName', caption: '파일명', size: '40%', style: 'text-align:left'}
			,{field: 'isExisted', caption: '다운로드 여부', size: '20%',
				render: function(record) {
					if ( record.isExisted == 'Y' && record.isDose == 'Y' ){
						return completeButton;
					}
					else if ( record.isExisted == 'Y' && record.isDose == 'N' ){
						//현재는 다운중 표시만...추후 다운로드 중인 소켓 정보를 가져와서 ProgressBar로 변경
						return downloadingButton;
					}
					else{
						return '<div class="btn" id="isExisted_'+record.recid+'" style="position: relative;width:100px;"></div>';
					}
				}
			}
		],
		onClick: function(event) {
			var grid = this;
			event.onComplete = function() {
				var sel = grid.getSelection();
				if ( sel == null || sel == "") {
					$('#doDownload').attr('disabled', true);
					$('#doDelete').attr('disabled', true);
					return;
				}
				
				var record = grid.get(sel);
				if ( record.isExisted == 'Y' ) {
					// 다운로드 버튼 Disable
					$('#doDownload').attr('disabled', true);
					// 삭제 버튼 Enable
					$('#doDelete').attr('disabled', false);
				}
				else {
					// 다운로드 버튼 Enable
					$('#doDownload').attr('disabled', false);
					// 삭제 버튼 Disable
					$('#doDelete').attr('disabled', true);
				}
			}
		}
		
	});

 	//  화면 초기화에 필요한 데이터 요청
 	initView();
 	
 	// 목록조회
 	$("#doSearch").click(function(){
 		doSearch();
    });
 	
 	// 목록 동기화
 	$("#doSync").click(function(){
 		doSync();
    });
 	
 	//  스템셀 다운로드
 	$("#doDownload").click(function(){
 		if($("#doDownload").attr('disabled') == "disabled") return;
    	doDownload();
    });
 	
 	//	스템셀 삭제
 	$("#doDelete").click(function(){
 		if($("#doDelete").attr('disabled') == "disabled") return;
 		doDelete();
    });
 	
 	// OS구분 코드 변경 시  OS버전 구분코드 조회
 	$("#os").change(function() {
 		// OS버전 코드  (OS구분과 연관된 하위코드로 선택된 OS구분 코드의 값)
 		setCommonCode('<c:url value="/codes/child/"/>' + $("#os option:selected").val(), 'osVersion');
 	});

});

function initView() {
	//  기본 설치관리자 정보 조회
	getDefaultDirector();

	// OS구분 코드 (코드값 : '2')
	setCommonCode('<c:url value="/codes/child/"/>'+'2', 'os');
	
	// OS버전 코드  (OS구분과 연관된 하위코드로 선택된 OS구분 코드의 값)
	setCommonCode('<c:url value="/codes/child/"/>' + $("#os option:selected").val(), 'osVersion');
	
	// IaaS 코드 (코드값 : '1')
	setCommonCode('<c:url value="/codes/child/"/>' + '1', 'iaas');
	
	// 스템셀 목록 조회
	doSearch();
	
	// 다운로드 & 삭제버튼 Disable
	$('#doDownload').attr('disabled', true);
	$('#doDelete').attr('disabled', true);
}

// OS버전 Option 목록 조회
function changeOS() {
	// OS버전 코드  (OS구분과 연관된 하위코드로 선택된 OS구분 코드의 값)
	setCommonCode('<c:url value="/codes/child/"/>' + $("#os option:selected").val(), 'osVersion');
}

// 스템셀 목록 조회
function doSearch() {
	var requestParam = "?os=" + $("#os option:selected").text();
	requestParam += "&osVersion=" + $("#osVersion option:selected").text();
	requestParam += "&iaas=" + $("#iaas option:selected").text();

	w2ui['config_opStemcellsGrid'].load("<c:url value='/publicStemcells'/>" + requestParam);
}

// 스템셀 목록 동기화
function doSync() {
	w2confirm({
		msg : '스템셀 목록을 동기화 하시겠습니까?',
		title : '스템셀 목록 동기화',
		yes_text : '확인',
		no_text : '취소',
		yes_callBack : function(event){
			w2ui['config_opStemcellsGrid'].lock('목록 동기화 중입니다.', true);
			$.ajax({
				method : 'PUT',
				type : "json",
				url : "/syncPublicStemcell",
				contentType : "application/json",
				success : function(data, status) {
					
					w2ui['config_opStemcellsGrid'].reset();
					doSearch();
					w2ui['config_opStemcellsGrid'].unlock();
					w2alert("스템셀 목록 동기화 처리가 완료되었습니다.", "스템셀 목록 동기화");
				},
				error : function(request, status, error) {
					w2ui['config_opStemcellsGrid'].unlock();
					var errorResult = JSON.parse(request.responseText);
					w2alert(errorResult.message, "스템셀 목록 동기화");
				}
			});
		}
	});	
}

// 스템셀 다운로드
function doDownload() {
	var selected = w2ui['config_opStemcellsGrid'].getSelection();
	var record = w2ui['config_opStemcellsGrid'].get(selected);
	
	var message = record.stemcellVersion + '버전의 스템셀 ' + record.stemcellFileName + '을 다운로드 하시겠습니까?';
	
	w2confirm({
		title 			: '스템셀 다운로드',
		msg 			: message,
		yes_text 		: '확인',
		no_text 		: '취소',
		yes_callBack	: function(event){
			var requestParameter = {
					recid 		: record.recid,
					key 		: record.key,
					fileName	: record.stemcellFileName,
					fileSize	: record.size
					};
			progressGrow(requestParameter);		
			
		}
	});
}

//PROGRESSBAR 생성
function progressGrow(requestParameter) {
	//var progressbar = $("td #isExisted_" + requestParameter.recid);
	var downloadPercentage = 0;
	var progressBarDiv = '<div class="progress">';
	progressBarDiv += '<div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" >';
	progressBarDiv += '</div></div>';
	$("#isExisted_" + requestParameter.recid).html(progressBarDiv);	
	//소켓 연결
	var socket = new SockJS('/stemcellDownloading');
	downloadClient = Stomp.over(socket);
	var status = 0;
	downloadClient.connect({}, function(frame) {
        downloadClient.subscribe('/socket/downloadStemcell', function(data){
        	//데이터에서 타겟을 받아서 지정
        	//progressbar status change
        	status = data.body.split('/')[1];
        	var recid = data.body.split('/')[0];
        	console.log("### Download Status ::: " + status);
        	 if ( status < 100 ) {
        		 $("#isExisted_" + recid+ " .progress .progress-bar")
        		 	.css({"width": status + "%"
        		 		, "padding-top": "5px"
        		 		, "text-align": "center"	
        		 	}).text( status + "%");
		    }
		    else if(status == 100) {
		    	$("#isExisted_" + recid).parent().html(completeButton);
		    }      		
        });
        downloadClient.send("/send/stemcellDownloading", {}, JSON.stringify(requestParameter));
	});
}


// 스템셀 삭제
function doDelete() {
	var selected = w2ui['config_opStemcellsGrid'].getSelection();
	var record = w2ui['config_opStemcellsGrid'].get(selected);

	var requestParameter = {
		stemcellFileName : record.stemcellFileName
	};

	w2confirm({
		title 		 : '스템셀 삭제',
		msg 		 : '선택된 스템셀 ' + record.stemcellFileName + '을 삭제하시겠습니까?',
		yes_text 	 : '확인',
		no_text 	 : '취소',
		yes_callBack : function(event){
			$.ajax({
				method 	: 'delete',
				type 	: "json",
				url 	: "/deletePublicStemcell",
				contentType : "application/json",
				data 	: JSON.stringify(requestParameter),
				success : function(data, status) {
					record.isExisted = 'N';
					w2ui['config_opStemcellsGrid'].reload();
					$('#doDelete').attr('disabled', true);
					w2ui['config_opStemcellsGrid'].selectNone();
					w2alert("삭제 처리가 완료되었습니다.", "스템셀  삭제");
				},
				error : function(e) {
					w2alert("오류가 발생하였습니다.");
				}
			});
		}
	});		
}

function lock (msg) {
    w2popup.lock(msg, true);
}

//다른페이지 이동시 호출
function clearMainPage() {
	$().w2destroy('config_opStemcellsGrid');
}

//화면 리사이즈시 호출
$(window).resize(function() {
	setLayoutContainerHeight();
});
</script>

<div id="main">
	<div class="page_site">환경설정 및 관리 > <strong>스템셀 관리</strong></div>
	
	<!-- OpenPaaS 스템셀 목록-->
	<div class="title">스템셀 목록</div>
	
 	<div class="search_box" align="center">
		<span class="search_li">OS</span>&nbsp;&nbsp;&nbsp;
		<!-- OS구분 -->
		<select name="select" id="os" class="select" style="width:120px">
		</select>
		<span class="search_li">OS버전</span>&nbsp;&nbsp;&nbsp;
		<!-- OS버전구분 -->
		<select name="select" id="osVersion" class="select" style="width:120px">
		</select>
		<span class="search_li">IaaS</span>&nbsp;&nbsp;&nbsp;
		<!-- IaaS구분 -->
		<select name="select" id="iaas" class="select" style="width:120px">
		</select>
		&nbsp;&nbsp;&nbsp;
		
		<!-- Btn -->
		<span id="doSearch" class="btn btn-info" style="width:100px" >조회</span>
		<span id="doSync" class="btn btn-primary" style="width:100px" >목록 동기화</span>
		<span id="doDownload" class="btn btn-primary" style="width:100px" >다운로드</span>
		<span id="doDelete" class="btn btn-danger" style="width:100px" >삭제</span>
		<!-- //Btn -->

	</div>
	
	<!-- 그리드 영역 -->
	<div id="config_opStemcellsGrid" style="width:100%; height:500px"></div>	
	
</div>