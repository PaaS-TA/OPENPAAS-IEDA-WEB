<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript">

$(function() {
	
 	// 기본 설치 관리자 정보 조회
 	getDefaultDirector("<c:url value='/directors/default'/>");
 	
	$('#sq_taskHistoryGrid').w2grid({
		name: 'sq_taskHistoryGrid',
		style: 'text-align:center',
		method: 'GET',
		show: {	
			lineNumbers: true,
			selectColumn: true	,
			footer: true},
		columns:[
			 {field: 'recid', 	caption: 'recid', 			hidden: true}
			,{field: 'id', caption: 'Task ID', size: '7%'}
			,{field: 'state', caption: '상태', size: '8%'}
			,{field: 'runTime', caption: '시간', size: '20%'}
			,{field: 'user', caption: '사용자', size: '10%'}
			,{field: 'description', caption: '설명', size: '25%', style:"text-align:left"}
			,{field: 'result', caption: '결과', size: '35%', style:"text-align:left"}
		],
		onError: function(event) {
			this.unlock();
			gridErrorMsg(event);
		}
	});

	doSearch();
});

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
	<div class="page_site">서비스 정보조회 > <strong>Task 실행 이력</strong></div>

	<!-- 설치 관리자 -->
	<div class="title">Task 실행 이력</div>
	
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
	
	<!-- Task실행 이력 -->
	<div class="pdt20">
		<div class="title fl">Task 실행 이력</div>
		<div class="fr"> 
			<!-- Btn -->
			<span class="boardBtn" id="detailTaskBtn" ><a href="#" class="btn btn-primary" style="width:150px"><span>Task 상세</span></a></span>
			<!-- //Btn -->
		</div>
	</div>	
	<div id="hMargin"></div>
	<div id="sq_taskHistoryGrid" style="width:100%; height:500px"></div>
</div>