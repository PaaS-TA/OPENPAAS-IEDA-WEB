<%
/* =================================================================
 * 작성일 : 
 * 작성자 : 
 * 상세설명 : 스템셀 조회 및 다운로드
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016-12       지향은      스템셀 다운로드 기능 개선
 * =================================================================
 */ 
%>
<%@page import="org.springframework.security.core.context.SecurityContextHolder"%>
<%@page import="org.springframework.security.core.Authentication"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<script type="text/javascript">
var downloadClient = "";
var completeButton = '<div class="btn btn-success btn-xs" style="width:100px;">Downloaded</div>';
var downloadingButton = '<div class="btn btn-info btn-xs" style="width:100px;">Downloading</div>';

var OS_TYPE_CODE = '200';
var IAAS_TYPE_CODE = '100';

$(function() {
	/********************************************************
	 * 설명 :  스템셀 목록 조회 Grid 생성
	 *********************************************************/
 	$('#config_opStemcellsGrid').w2grid({
		name: 'config_opStemcellsGrid',
		show: {selectColumn: true, footer: true},
		multiSelect: false,
		method: 'GET',
		style: 'text-align:center',
		columns:[
			 {field: 'recid', caption: 'recid', hidden: true}
			,{field: 'id', caption: '운영체계', hidden: true} 
			,{field: 'os', caption: '운영체계', size: '10%'}			
			,{field: 'osVersion', caption: '버전', size: '10%'}
			,{field: 'iaas', caption: 'IaaS', size: '10%', sortable: true}
			,{field: 'stemcellVersion', caption: '스템셀버전', size: '10%'}
			,{field: 'stemcellFileName', caption: '파일명', size: '40%', style: 'text-align:left'}
			,{field: 'isExisted', caption: '다운로드 여부', size: '20%',
				render: function(record) {
					if ( record.downloadStatus == 'Y' ){
						return '<div class="btn btn-success btn-xs" id= "downloaded_'+record.id+'" style="width:100px;">Downloaded</div>';
					}
// 					else if ( record.isExisted == 'Y' && record.isDose == 'N' ){
// 						//현재는 다운중 표시만...추후 다운로드 중인 소켓 정보를 가져와서 ProgressBar로 변경
// 						return downloadingButton;
// 					}
					else{
						return '<div class="btn" id="isExisted_'+record.id+'" style="position: relative;width:100px;"></div>';
					}
				}
			}
		],
		onSelect: function(event) {
			var grid = this;
			event.onComplete = function() {
				var sel = grid.getSelection();
				
				var record = grid.get(sel);
				if ( record.downloadStatus == 'Y' ) {
					// 다운로드 버튼 Disable
					$('#doDownload').attr('disabled', true);
					// 삭제 버튼 Enable
					$('#doDelete').attr('disabled', false);
				} else {
					// 다운로드 버튼 Enable
					$('#doDownload').attr('disabled', false);
					// 삭제 버튼 Disable
					$('#doDelete').attr('disabled', true);
				}
			}
		},
		onUnselect: function(event) {
			var grid = this;
			event.onComplete = function() {
				$('#doDownload').attr('disabled', true);
				$('#doDelete').attr('disabled', true);
			}
		}, onLoad:function(event){
			if(event.xhr.status == 403){
				location.href = "/abuse";
				event.preventDefault();
			}
			
		}, onError:function(evnet){
	
		}
		
	});

 	//  화면 초기화에 필요한 데이터 요청
 		initView();
 	
 
 	/********************************************************
	 * 설명 :   목록조회
	 *********************************************************/
 	$("#doSearch").click(function(){
 		doSearch();
    });
 	
 	 /********************************************************
	 * 설명 :   목록 동기화
	 *********************************************************/
 	$("#doSync").click(function(){
 		doSync();
    });
 	
 	 /********************************************************
	 * 설명 :   스템셀 다운로드
	 *********************************************************/
 	$("#doDownload").click(function(){
 		if($("#doDownload").attr('disabled') == "disabled") return;
    	doDownload();
    });
 	
 	 /********************************************************
	 * 설명 :   스템셀 삭제
	 *********************************************************/
 	$("#doDelete").click(function(){
 		if($("#doDelete").attr('disabled') == "disabled") return;
 		doDelete();
    });
 	
 	 /********************************************************
	 * 설명 :   OS구분 코드 변경 시  OS버전 구분코드 조회
	 *********************************************************/
 	$("#os").change(function() {
 		// OS버전 코드  (OS구분과 연관된 하위코드로 선택된 OS구분 코드의 값)
 		setCommonCode('<c:url value="/common/deploy/codes/parent/"/>' + IAAS_TYPE_CODE + '<c:url value="/subcode/"/>' + $("#os option:selected").val(), 'osVersion');
 	});

});

/**************************************************************
 * 설명 공통코드 설정
 * Function : setCommonCode
 **************************************************************/
function setCommonCode(url, id) {
	jQuery.ajax({
		type : "get",
		url : url,
		async : false,
		error : function(xhr, status) {
			if(xhr.status==403){
				location.href = "/abuse";
			}
		},
		success : function(data) {
			var $object = jQuery("#" + id);

			var optionString = "";
			for (i = 0; i < data.length; i++) {
				if (i == 0) {
					optionString += "<option selected='selected' ";
					optionString += "value='" + data[i].codeIdx + "' >";
					optionString += data[i].codeName;
					optionString += "</option>\n";
				} else {
					optionString += "<option ";
					optionString += "value='" + data[i].codeIdx + "'>";
					optionString += data[i].codeName;
					optionString += "</option>\n";
				}
			}
			$object.html(optionString);
		}
	});
}

/**************************************************************
 * 설명 : 정보 조회
 * Function : initView
 **************************************************************/
function initView() {
	//  기본 설치관리자 정보 조회
	getDefaultDirector();

	// OS구분 코드 (코드값 : '200')
	setCommonCode('<c:url value="/common/deploy/codes/parent/"/>'+ OS_TYPE_CODE, 'os');
	
	// OS버전 코드  (OS구분과 연관된 하위코드로 선택된 OS구분 코드의 값)
	setCommonCode('<c:url value="/common/deploy/codes/parent/"/>' + OS_TYPE_CODE + '<c:url value="/subcode/"/>' + $("#os option:selected").val(), 'osVersion');
	
	// IaaS 코드 (코드값 : '1')
	//setCommonCode('<c:url value="/codes/child/"/>' + '1', 'iaas');
	setCommonCode('<c:url value="/common/deploy/codes/parent/"/>' + '100', 'iaas');
	
	// 스템셀 목록 조회
	doSearch();
	
	// 다운로드 & 삭제버튼 Disable
	$('#doDownload').attr('disabled', true);
	$('#doDelete').attr('disabled', true);
}

/**************************************************************
 * 설명 : OS버전 Option 목록 조회
 * Function : changeOS
 **************************************************************/
function changeOS() {
	// OS버전 코드  (OS구분과 연관된 하위코드로 선택된 OS구분 코드의 값)
	setCommonCode('<c:url value="/common/deploy/codes/parent/"/>' + IAAS_TYPE_CODE + '<c:url value="/subcode/"/>' + $("#os option:selected").val(), 'osVersion');
}

/**************************************************************
 * 설명 : 스템셀 목록 조회
 * Function : doSearch
 **************************************************************/
function doSearch() {
	var requestParam = "?os=" + $("#os option:selected").text();
	requestParam += "&osVersion=" + $("#osVersion option:selected").text();
	requestParam += "&iaas=" + $("#iaas option:selected").text();

	w2ui['config_opStemcellsGrid'].load("<c:url value='/config/stemcell/publicStemcells'/>" + requestParam);
}

/**************************************************************
 * 설명 : 스템셀 목록 동기화
 * Function : doSync
 **************************************************************/
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
				url : "/config/stemcell/syncPublicStemcell",
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
					doSearch();
				}
			});
		},
		no_callBack : function(event){
			doSearch();
		}
	});	
}


/**************************************************************
 * 설명 : 스템셀 다운로드
 * Function : doDownload
 **************************************************************/
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
			if(lockFileSet(record.stemcellFileName)==false) {
				return;
			}
			var requestParameter = {
					recid		: record.recid,
					id 			: record.id,
					sublink 	: record.sublink,
					fileName	: record.stemcellFileName,
					fileSize	: record.size
					};
			progressGrow(requestParameter, record );	
			
		},
		no_callBack : function(event){
			doSearch();
		}
		
	});
}

/**************************************************************
 * 설명 : PROGRESSBAR 생성
* Function : progressGrow
**************************************************************/
function progressGrow(requestParameter, record) {
    //var progressbar = $("td #isExisted_" + requestParameter.recid);
    var downloadPercentage = 0;
    //downloading bar 생성
    var progressBarDiv = '<div class="progress">';
    progressBarDiv += '<div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" >';
    progressBarDiv += '</div></div>';
    $("#isExisted_" + requestParameter.id).html(progressBarDiv);
     
    //소켓 연결
    var socket = new SockJS('/config/stemcell/download/stemcellDownloading');
     
    downloadClient = Stomp.over(socket); //Stomp client 구성
    var status = 0;
    downloadClient.connect({}, function(frame) {
        downloadClient.subscribe('/config/stemcell/download/logs', function(data){
            //데이터에서 타겟을 받아서 지정
            //progressbar status change
            status = data.body.split('/')[1]; //recid/percent 중 percent
            var id = data.body.split('/')[0]; //recid/percent 중 recid
            console.log("### Download Status ::: " + status);
             if ( status < 100 ) {
                 $("#isExisted_" + id+ " .progress .progress-bar")
                    .css({"width": status + "%"
                        , "padding-top": "5px"
                        , "text-align": "center"    
                    }).text( status + "%");
            }
            else if(status == 100) {
                $("#isExisted_" + id).parent().html(completeButton);
                if(downloadClient!=null){
               	  downloadClient.disconnect();
                }
                doSearch();
            }           
        });
        //send to message
        downloadClient.send("/send/config/stemcell/download/stemcellDownloading", {}, JSON.stringify(requestParameter));
    });
}


/**************************************************************
 * 설명 : 스템셀 삭제
 * Function : doDelete
 **************************************************************/
function doDelete() {
	var selected = w2ui['config_opStemcellsGrid'].getSelection();
	var record = w2ui['config_opStemcellsGrid'].get(selected);

	var requestParameter = {
		stemcellFileName	: record.stemcellFileName,
		id					: record.id
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
				url 	: "/config/stemcell/deletePublicStemcell",
				contentType : "application/json",
				data 	: JSON.stringify(requestParameter),
				success : function(data, status) {
					record.isExisted = 'N';
					doSearch();
					$('#doDelete').attr('disabled', true);
					w2ui['config_opStemcellsGrid'].selectNone();
				},
				error : function(e) {
					w2alert("오류가 발생하였습니다.");
				}
			});
		},
		no_callBack : function(event){
			doSearch();
		}
	});		
}

/********************************************************
 * 설명 :  lock 검사
 * Function : lockFileSet
 *********************************************************/
var lockFile = false;
function lockFileSet(fileName){
	var FileName = fileName.split(".tgz")[0]+"-download";
	var message = "현재 다른 플랫폼 설치 관리자가 동일 한 릴리즈를 사용 중 입니다."
	lockFile = commonLockFile("<c:url value='/common/deploy/lockFile/"+FileName+"'/>",message);
	return lockFile;
}

/**************************************************************
 * 설명 : Lock 실행
 * Function : lock
 **************************************************************/
function lock (msg) {
    w2popup.lock(msg, true);
}

/********************************************************
 * 설명 :  다른페이지 이동시 호출
 * Function : clearMainPage
 *********************************************************/
function clearMainPage() {
	$().w2destroy('config_opStemcellsGrid');
}

/********************************************************
 * 설명 :  화면 리사이즈시 호출
 *********************************************************/
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
		<sec:authorize access="hasAuthority('CONFIG_STEMCELL_LIST')">
		<span id="doSearch" class="btn btn-info" style="width:100px" >조회</span>
		</sec:authorize>
		<sec:authorize access="hasAuthority('CONFIG_STEMCELL_SYNC')">
		<span id="doSync" class="btn btn-primary" style="width:100px" >목록 동기화</span>
		</sec:authorize>
		<sec:authorize access="hasAuthority('CONFIG_STEMCELL_DOWNLOAD')">
		<span id="doDownload" class="btn btn-primary" style="width:100px" >다운로드</span>
		</sec:authorize>
		<sec:authorize access="hasAuthority('CONFIG_STEMCELL_DELETE')">
		<span id="doDelete" class="btn btn-danger" style="width:100px" >삭제</span>
		</sec:authorize>
		<!-- //Btn -->

	</div>
	
	<!-- 그리드 영역 -->
	<div id="config_opStemcellsGrid" style="width:100%; height:700px"></div>	
	
</div>