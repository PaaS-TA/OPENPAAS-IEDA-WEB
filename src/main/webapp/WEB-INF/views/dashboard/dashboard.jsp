<%
/* =================================================================
 * 작성일 : 
 * 작성자 : 
 * 상세설명 : 대시보드 화면(설치/업로드된 릴리즈/업로르된 스템셀 목록 조회)
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">

$(function() {
	// 기본 설치 관리자 정보 조회
 	var isOk = getDefaultDirector("<c:url value='/common/use/director'/>");
	
	$('#main_deploymentsGrid').w2grid({
		name: 'main_deploymentsGrid',
		style	: 'text-align:center',
		msgAJAXerror : '배포 정보 조회 실패',
		method	: 'GET',
		multiSelect: false,
		show: {	
				selectColumn: true,
				footer: false},
		columns	: [
		 	 {field: 'recid', 	caption: 'recid', hidden: true}
       	   , {field: 'name', caption: '배포 이름', size: '20%', style: 'text-align:left'}
       	   , {field: 'releaseInfo', caption: '릴리즈 정보', size: '40%', style: 'text-align:left'}
       	   , {field: 'stemcellInfo', caption: '스템셀 정보', size: '40%', style: 'text-align:left'}
		 ],onError: function(event) {

		},onLoad : function(event){
		 	if(event.xhr.status == 403){
				location.href = "/abuse";
		 		event.preventDefault();
		 	}
		}
		 
	});
	
	$('#main_releasesGrid').w2grid({
		name: 'main_releasesGrid',
		method 	: "GET",
		msgAJAXerror : '업로드 된 릴리즈 조회 실패',
		style: 'text-align:center',
		columns:[
         	 {field: 'recid', caption: 'recid', hidden: true}
	       , {field: 'name', caption: '릴리즈명', size: '20%'}
	       , {field: 'version', caption: '릴리즈버전', size: '10%'}
	       , {field: 'jobNames', caption: 'Job템플릿', size: '55%', style: 'text-align:left'}
		],onError: function(event) {
		},onLoad : function(event){
		 	if(event.xhr.status == 403){
				location.href = "/abuse";
		 		event.preventDefault();
		 	}
		}
	});
	
	$('#main_stemcellsGrid').w2grid({
		name: 'main_stemcellsGrid',
		method 	: "GET",
		msgAJAXerror : '업로드 된 스템셀 조회 실패',
		style: 'text-align:center',
		columns:[
           	   {field: 'recid', caption: 'recid', hidden: true}
	         , {field: 'os', caption: '운영체계', size: '30%'}
	         , {field: 'stemcellFileName', caption: '스템셀명', size: '40%'}		         
	         , {field: 'stemcellVersion', caption: '스템셀버전', size: '30%'}
		],onError: function(event) {
	
		},onLoad : function(event){
		 	if(event.xhr.status == 403){
				location.href = "/abuse";
		 		event.preventDefault();
		 	}
		}
	});	
	
	if ( isOk == true) doSearch();
});

//조회기능
function doSearch() {
	//1.1설치 목록
	w2ui['main_deploymentsGrid'].load("<c:url value='/main/dashboard/deployments'/>","",function(event){}); 
	w2ui['main_releasesGrid'].load("<c:url value='/main/dashboard/releases'/>","",function(event){}); //업로드 릴리즈 목록
	w2ui['main_stemcellsGrid'].load("<c:url value='/main/dashboard/stemcells'/>","",function(event){});	//업로드 스템셀 목록
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
	<div id="isDefaultDirector"></div>
	<div id="hMargin"></div>
	
	<!-- 설치 목록 -->
	<div class="title">설치 목록</div>
	<div id="main_deploymentsGrid" style="width:100%; height:150px"></div>
	
	<div id="hMargin"></div>
	
	<!-- 릴리즈 목록 -->
	<div class="title">업로드 릴리즈 목록</div>
	<div id="main_releasesGrid" style="width:100%; height:150px"></div>
	
	<div id="hMargin"></div>
	
	<!-- 스템셀 목록-->
	<div class="title">업로드 스템셀 목록</div>
	<div id="main_stemcellsGrid" style="width:100%; height:150px"></div>
          
</div>