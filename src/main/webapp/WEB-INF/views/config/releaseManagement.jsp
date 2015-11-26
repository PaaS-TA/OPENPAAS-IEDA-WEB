<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<script type="text/javascript">

$(function() {
 	$('#config_opReleaseGrid').w2grid({
		name: 'config_opReleaseGrid',
		show: {selectColumn: true, footer: true},
		multiSelect: false,
		method: 'GET',
		style: 'text-align:center',
		columns:[
			 {field: 'recid', caption: 'recid', hidden: true}
			,{field: 'isMandatory', caption: 'mandatory', hidden: true}
			,{field: 'releaseName', caption: '릴리즈 명', size: '60%'}
			,{field: 'releaseVersion', caption: '릴리즈 버전', size: '20%'}
			,{field: 'isExisted', caption: '다운로드 여부', size: '20%',
				render: function(record) {
					if ( record.isExisted == 'Y')
						return '<div class="btn btn-success btn-xs" style="width:70px;">' + '완료 ' + '</div>';
				}
			}
		],
		onClick: function(event) {
			var grid = this;
			event.onComplete = function() {
/* 				var sel = grid.getSelection();
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
				} */
			}
		}
		
	});

 	//  화면 초기화에 필요한 데이터 요청
 	initView();
 	
 	// 목록조회
 	$("#doSearch").click(function(){
 		doSearch();
    });
 	
 	//  스템셀 다운로드
 	$("#doDownload").click(function(){
    	doDownload();
    });
 	
 	//	스템셀 삭제
 	$("#doDelete").click(function(){
 		doDelete();
    });
 	
});

function initView() {

}

//스템셀 목록 조회
function doSearch() {

}

// 스템셀 다운로드
function doDownload() {

}

// 스템셀 삭제
function doDelete() {

}

//다른페이지 이동시 호출
function clearMainPage() {
	$().w2destroy('config_opReleaseGrid');
}

//화면 리사이즈시 호출
$(window).resize(function() {
	setLayoutContainerHeight();
});
</script>

<div id="main">
	<div class="page_site">설치관리자 환경설정 > <strong>BOSH 릴리즈 관리</strong></div>
	
	<!-- OpenPaaS 스템셀 목록-->
	<div class="title">릴리즈 목록</div>
	
 	<div class="search_box" align="center">
		<span class="search_li">릴리즈 선택</span>&nbsp;&nbsp;&nbsp;
		<!-- OS구분 -->
		<select name="select" id="os" class="select" style="width:120px">
		</select>
		&nbsp;&nbsp;&nbsp;
		
		<!-- Btn -->
		<span id="doSearch" class="btn btn-info" style="width:100px" >조회</span>
		<span id="doDownload" class="btn btn-primary" style="width:100px" >다운로드</span>
		<span id="doDelete" class="btn btn-danger" style="width:100px" >삭제</span>
		<!-- Btn -->

	</div>
	
	<!-- 그리드 영역 -->
	<div id="config_opReleaseGrid" style="width:100%; height:500px"></div>	
	
</div>