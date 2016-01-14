<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript">

$(function() {
	
 	// 기본 설치 관리자 정보 조회
 	var bDefaultDirector = getDefaultDirector("<c:url value='/directors/default'/>");
 	
	$('#sq_deploymentsGrid').w2grid({
		name	: 'sq_deploymentsGrid',
		style	: 'text-align:center',
		method	: 'GET',
		multiSelect : false,
		columns	: [
					 {field: 'recid', 	caption: 'recid', hidden: true}
		       	   , {field: 'name', caption: '배포 이름', size: '20%', style: 'text-align:left'}
		       	   , {field: 'releaseInfo', caption: '릴리즈 정보', size: '40%', style: 'text-align:left'}
		       	   , {field: 'stemcellInfo', caption: '스템셀 정보', size: '40%', style: 'text-align:left'}

		       	],
		onError: function(event) {
			w2alert("ERROR");
			//this.unlock();
			//gridErrorMsg(event);
		}
	});
	
	initView(bDefaultDirector);

});

function initView(bDefaultDirector) {
	if ( bDefaultDirector ) {
		doSearch();
	}
}

//조회기능
function doSearch() {
	w2ui['sq_deploymentsGrid'].load("<c:url value='/deployments'/>");
}

//다른페이지 이동시 호출
function clearMainPage() {
	$().w2destroy('sq_deploymentsGrid');
}

//화면 리사이즈시 호출
$( window ).resize(function() {
	setLayoutContainerHeight();
});

</script>

<div id="main">
	<div class="page_site">정보조회 > <strong>설치 목록</strong></div>
	
	<!-- 설치 관리자 -->
	<div id="isDefaultDirector"></div>
	
	<!-- 설치 목록 -->
	<div class="title">설치 목록</div>
	<div id="sq_deploymentsGrid" style="width:100%; height:500px"></div>	
	
</div>