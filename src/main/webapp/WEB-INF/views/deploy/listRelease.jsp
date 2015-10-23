<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript">

$(function() {
 	// 기본 설치 관리자 정보 조회
 	getDefaultDirector("<c:url value='/directors/default'/>");
 	
 	$('#ru_uploadedReleasesGrid').w2grid({
		name: 'ru_uploadedReleasesGrid',
		style: 'text-align:center',
		columns:[
			 {field: 'name', caption: '이름', size: '20%'}
			,{field: 'version', caption: '버전', size: '40%'}
			,{field: 'commit_hash', caption: 'Commit Hash', size: '40%'}
		]
	});
 	

});

//다른페이지 이동시 호출
function clearMainPage() {
	$().w2destroy('ru_uploadedReleasesGrid');
}

</script>

<div id="main">
	<div class="page_site">서비스 설치관리 > <strong>릴리즈 업로드</strong></div>
	
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
	
<!-- 	<div id="hMargin"/> -->
	
	<!-- 릴리즈 목록-->
	<div class="pdt20"> 
		<div class="title fl">릴리즈 목록</div>
		<div class="fr"> 
		<!-- Btn -->
		<span class="boardBtn"><a href="#" class="btn btn-primary" style="width:130px"><span>릴리즈 업로드</span></a></span>
		<span class="boardBtn"><a href="#" class="btn btn-danger" style="width:130px"><span>릴리즈 삭제</span></a></span>
		<!-- //Btn -->
	    </div>
	</div>
	<div id="ru_uploadedReleasesGrid" style="width:100%; height:500px"/>	
	
</div>