<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript">

$(function() {
	
 	// 기본 설치 관리자 정보 조회
 	getDefaultDirector("<c:url value='/directors/default'/>");
 	
	$('#sq_taskHistoryGrid').w2grid({
		name: 'sq_taskHistoryGrid',
		style: 'text-align:center',
		columns:[
			 {field: 'TASK_ID', caption: '번호', size: '10%'}
			,{field: 'TASK_STATUS', caption: '상태', size: '10%'}
			,{field: 'TASK_DATETIME', caption: '시간', size: '20%'}
			,{field: 'TASK_USER', caption: '사용자', size: '20%'}
			,{field: 'TASK_COMMENT', caption: '설명', size: '40%'}
		]
	});

});

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
	
	<div id="hMargin"/>

	<!-- Task실행 이력 -->
	<div class="title">Task 실행 이력</div>
	<div id="sq_taskHistoryGrid" style="width:100%; height:500px"/>	
	
	
</div>