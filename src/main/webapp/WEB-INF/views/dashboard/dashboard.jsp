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
         	 {field: 'recid', caption: 'recid', hidden: true}
	       , {field: 'name', caption: '릴리즈명', size: '20%'}
	       , {field: 'version', caption: '릴리즈버전', size: '10%'}
/* 	       , {field: 'currentDeployed', caption: '배포 사용중 여부', size: '15%',
	    	   render: function(record) {
	    		   if ( record.currentDeployed == 'true' )
	    			   return '<span class="btn btn-success" style="width:70px">배포</span>';
	    		   else
	    			   return '';
	    	   }
	       } */
	       , {field: 'jobNames', caption: 'Job템플릿', size: '55%', style: 'text-align:left'}
		]
	});
	
	$('#main_stemcellsGrid').w2grid({
		name: 'main_stemcellsGrid',
		method 	: "GET",
		style: 'text-align:center',
		columns:[
           	   {field: 'recid', caption: 'recid', hidden: true}
	         , {field: 'operatingSystem', caption: '운영체계', size: '30%'}
	         , {field: 'name', caption: '스템셀명', size: '40%'}		         
	         , {field: 'version', caption: '스템셀버전', size: '30%'}
/* 	         , {field: 'deploymentInfo', caption: '배포명', size: '20%',
	        	 	render: function(record) {
	 	    		   if ( record.deploymentInfo != '' )
		    			   return '<span class="btn btn-success" style="width:70px">' + record.deploymentInfo + '</span>';
		    		   else
		    			   return '';
	        	 	}
	        	 } */
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