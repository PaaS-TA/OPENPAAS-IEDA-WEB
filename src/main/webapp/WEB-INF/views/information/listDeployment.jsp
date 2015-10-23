<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript">

$(function() {
	
 	// 기본 설치 관리자 정보 조회
 	getDefaultDirector("<c:url value='/directors/default'/>");
 	
	$('#sq_deploymentsGrid').w2grid({
		name: 'sq_deploymentsGrid',
		style: 'text-align:center',
		columns:[
			 {field: 'DEPLOYMENT_NAME', caption: '배포 이름', size: '20%'}
			,{field: 'DEPLOYMENT_URL', caption: '릴리즈 목록', size: '40%'}
			,{field: 'DEPLOYMENT_UUID', caption: '스템셀 목록', size: '40%'}
		]
	});
	


});

//다른페이지 이동시 호출
function clearMainPage() {
	$().w2destroy('sq_deploymentsGrid');
}

</script>

<div id="main">
	<div class="page_site">서비스 정보조회 > <strong>설치 목록</strong></div>
	
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
	
	<!-- 설치 목록 -->
	<div class="title">설치 목록</div>
	<div id="sq_deploymentsGrid" style="width:100%; height:500px"/>	
	
</div>