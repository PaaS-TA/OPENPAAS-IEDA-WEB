<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript">

$(function() {
	
 	// 기본 설치 관리자 정보 조회
 	getDefaultDirector("<c:url value='/directors/default'/>");
 	
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
				
				setDisable($('#doDeleteStemcell'), false);
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
 	
 	initView();
 	
 	// 스템셀 삭제
 	$("#doDeleteStemcell").click(function(){
 		doDeleteStemcell();
    });
 	
 	// 스템셀 업로드
 	$("#doUploadStemcell").click(function(){
 		doUploadStemcell();
    });

});

function initView() {
	// 업로드된 스템셀 조회
 	doSearchUploadedStemcells();
	
	// 로컬에 다운로드된 스템셀 조회
	doSearchLocalStemcells();

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
			$.ajax({
				method : 'post',
				type : "json",
				url : "/deleteStemcell",
				contentType : "application/json",
				data : JSON.stringify(requestParameter),
				success : function(data, status) {
					w2alert("웹소켓 연결 + 팝업창 띄워서 로그 보여주기");
				},
				error : function(request, status, error) {
                    //w2alert("code:"+request.status+ ", message: "+request.responseText+", error:"+error);
                    //w2alert(request.responseText);
					w2alert("오류가 발생하였습니다");
                }
			});

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
			$.ajax({
				method : 'post',
				type : "json",
				url : "/uploadStemcell",
				contentType : "application/json",
				data : JSON.stringify(requestParameter),
				success : function(data, status) {
					w2alert("웹소켓 연결 + 팝업창 띄워서 로그 보여주기");
				},
				error : function(request, status, error) {
                    //w2alert("code:"+request.status+ ", message: "+request.responseText+", error:"+error);
                    //w2alert(request.responseText);
					w2alert("오류가 발생하였습니다");
                }
			});

		})
		.no(function() {
			// do nothing
		});	
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
		<th width="18%" class="th_fb">관리자 이름</th><td class="td_fb"><b id="directorName"/></td>
		<th width="18%" class="th_fb">관리자 계정</th><td class="td_fb"><b id="userId"/></td>
	</tr>
	<tr>
		<th width="18%" >관리자 URL</th><td><b id="directorUrl"/></td>
		<th width="18%" >관리자 UUID</th><td ><b id="directorUuid"/></td>
	</tr>
	</table>
	
	<!-- 업로드된 스템셀 목록-->
	<div class="pdt20">
		<div class="title fl">디렉터에 업로드된 스템셀 목록</div>
		<div class="fr"> 
			<span class="btn btn-danger" style="width:120px" id="doDeleteStemcell">스템셀 삭제</span>
	    </div>
	</div>
	<div id="us_uploadStemcellsGrid" style="width:100%; height:200px"/>
	
	<!-- 로컬 스템셀 목록-->
	<div class="pdt20">
		<div class="title fl">다운로드된 스템셀 목록</div>
		<div class="fr"> 
			<span class="btn btn-primary" style="width:120px" id="doUploadStemcell">스템셀 업로드</span>
	    </div>
	</div>
		
	<div id="us_localStemcellsGrid" style="width:100%; height:200px"/>
	
</div>