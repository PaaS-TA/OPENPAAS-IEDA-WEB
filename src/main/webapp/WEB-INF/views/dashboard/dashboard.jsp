<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<script type="text/javascript">

$(function() {
	
	$('#main_deploymentsGrid').w2grid({
		name: 'main_deploymentsGrid',
		style: 'text-align:center',
		columns:[
			 {field: 'DEPLOYMENT_NAME', caption: '배포 이름', size: '20%'}
			,{field: 'DEPLOYMENT_URL', caption: '릴리즈 목록', size: '40%'}
			,{field: 'DEPLOYMENT_UUID', caption: '스템셀 목록', size: '40%'}
		]
	});
	
	$('#main_releasesGrid').w2grid({
		name: 'main_releasesGrid',
		style: 'text-align:center',
		columns:[
			 {field: 'RELEASE_NAME', caption: '릴리즈 이름', size: '50%'}
			,{field: 'RELEASE_VERSION', caption: '버전', size: '25%'}
			,{field: 'RELEASE_USE_YN', caption: '배포여부', size: '25%'}
		]
	});
	
	$('#main_stemcellsGrid').w2grid({
		name: 'main_stemcellsGrid',
		style: 'text-align:center',
		columns:[
			 {field: 'STEMCELL_NAME', caption: '스템셀 이름', size: '40%'}
			,{field: 'STEMCELL_OS', caption: '운영체계', size: '10%'}
			,{field: 'STEMCELL_VERSION', caption: '버전', size: '10%'}
			,{field: 'STEMCELL_CID', caption: 'CID', size: '40%'}
		]
	});	
	
});


//다른페이지 이동시 호출
function clearMainPage() {
	$().w2destroy('main_deploymentsGrid');
	$().w2destroy('main_releasesGrid');
	$().w2destroy('main_stemcellsGrid');
}

</script>

<div id="main">
	
	<!-- 설치 관리자 -->
	<div class="title">설치 관리자</div>
	
	<table class="tbl1" border="1" cellspacing="0">
	<tr>
		<th width="18%" class="th_fb">관리자 이름</th><td class="td_fb">0000</td>
		<th width="18%" class="th_fb">관리자 계정</th><td class="td_fb">0000</td>
	</tr>
	<tr>
		<th width="18%" >관리자 URL</th><td>0000</td>
		<th width="18%" >관리자 UUID</th><td >0000</td>
	</tr>
	</table>
	
	<div id="hMargin"/>
	
	<!-- 설치 목록 -->
	<div class="title">설치 목록</div>
	<div id="main_deploymentsGrid" style="width:100%; height:150px"/>
	
	<div id="hMargin"/>
	
	<!-- 릴리즈 목록 -->
	<div class="title">릴리즈 목록</div>
	<div id="main_releasesGrid" style="width:100%; height:150px"/>
	
	<div id="hMargin"/>
	
	<!-- 스템셀 목록-->
	<div class="title">스템셀 목록</div>
	<div id="main_stemcellsGrid" style="width:100%; height:150px"/>
          
</div>