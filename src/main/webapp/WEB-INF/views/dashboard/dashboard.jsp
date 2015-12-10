<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<script type="text/javascript">

$(function() {
	// 기본 설치 관리자 정보 조회
 	var isOk = getDefaultDirector("<c:url value='/directors/default'/>");
	
	$('#main_deploymentsGrid').w2grid({
		name: 'main_deploymentsGrid',
		style	: 'text-align:center',
		method	: 'GET',
		columns	: [
		 	 {field: 'recid', 	caption: 'recid', hidden: true}
       	   , {field: 'name', caption: '배포 이름', size: '20%', style: 'text-align:left'}
       	   , {field: 'releaseInfo', caption: '릴리즈 정보', size: '40%', style: 'text-align:left'}
       	   , {field: 'stemcellInfo', caption: '스템셀 정보', size: '40%', style: 'text-align:left'}
		       	],
		onError: function(event) {
			w2alert("ERROR");
		}
	});
	
	$('#main_releasesGrid').w2grid({
		name: 'main_releasesGrid',
		method 	: "GET",
		style: 'text-align:center',
		columns:[
			 {field: 'name', caption: '릴리즈 이름', size: '50%'}
			,{field: 'version', caption: '버전', size: '25%'}
			,{field: 'commitHash', caption: '배포여부', size: '25%'}
		]
	});
	
	$('#main_stemcellsGrid').w2grid({
		name: 'main_stemcellsGrid',
		method 	: "GET",
		style: 'text-align:center',
		columns:[
			 {field: 'name', caption: '스템셀 이름', size: '40%'}
			,{field: 'operating_system', caption: '운영체계', size: '10%'}
			,{field: 'version', caption: '버전', size: '10%'}
			,{field: 'cid', caption: 'CID', size: '40%'}
		]
	});	
	
	if ( isOk ) doSearch();
});

//조회기능
function doSearch() {
	w2ui['main_deploymentsGrid'].load("<c:url value='/dashboard/deployments'/>");
	w2ui['main_releasesGrid'].load("<c:url value='/dashboard/releases'/>");
	w2ui['main_stemcellsGrid'].load("<c:url value='/dashboard/stemcells'/>");	
}

//다른페이지 이동시 호출
function clearMainPage() {
	$().w2destroy('main_deploymentsGrid');
	$().w2destroy('main_releasesGrid');
	$().w2destroy('main_stemcellsGrid');
}

//화면 리사이즈시 호출
$( window ).resize(function() {
	setLayoutContainerHeight();
});

</script>

<div id="main">
	
	<!-- 설치 관리자 -->
	<div class="title">설치 관리자</div>
	
	<table class="tbl1" border="1" >
	<tr>
		<th width="18%" class="th_fb">관리자 이름</th><td class="td_fb"><b id="directorName"></b></td>
		<th width="18%" class="th_fb">관리자 계정</th><td class="td_fb"><b id="userId"></b></td>
	</tr>
	<tr>
		<th width="18%" >관리자 URL</th><td><b id="directorUrl"></b></td>
		<th width="18%" >관리자 UUID</th><td ><b id="directorUuid"></b></td>
	</tr>
	</table>
	
	<div id="hMargin"></div>
	
	<!-- 설치 목록 -->
	<div class="title">설치 목록</div>
	<div id="main_deploymentsGrid" style="width:100%; height:150px"></div>
	
	<div id="hMargin"></div>
	
	<!-- 릴리즈 목록 -->
	<div class="title">릴리즈 목록</div>
	<div id="main_releasesGrid" style="width:100%; height:150px"></div>
	
	<div id="hMargin"></div>
	
	<!-- 스템셀 목록-->
	<div class="title">스템셀 목록</div>
	<div id="main_stemcellsGrid" style="width:100%; height:150px"></div>
          
</div>