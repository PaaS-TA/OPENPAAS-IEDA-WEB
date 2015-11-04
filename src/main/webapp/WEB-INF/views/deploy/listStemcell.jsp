<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript">

$(function() {
	
 	// 기본 설치 관리자 정보 조회
 	getDefaultDirector("<c:url value='/directors/default'/>");
 	
 	$('#us_uploadStemcellsGrid').w2grid({
		name	: 'us_uploadStemcellsGrid',
		method 	: "GET",
		style	: 'text-align:center',
		show	: {		
					lineNumbers: true,
					selectColumn: true	,
					footer: true},
		columns	:[
		           {field: 'recid', caption: 'recid', hidden: true}
		         , {field: 'name', caption: '이름', size: '30%'}
		         , {field: 'operating_system', caption: 'OS', size: '20%'}
		         , {field: 'version', caption: '버전', size: '10%'}
		         , {field: 'cid', caption: 'CID', size: '30%'}
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
	w2ui['us_uploadStemcellsGrid'].load("<c:url value='/stemcells'/>");
}

//다른페이지 이동시 호출
function clearMainPage() {
	$().w2destroy('us_uploadStemcellsGrid');
}

//화면 리사이즈시 호출
$( window ).resize(function() {
	setLayoutContainerHeight();
});

</script>

<div id="main">
	<div class="page_site">서비스 설치관리 > <strong>스템셀 업로드</strong></div>
	
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
	
	<!-- 스템셀 목록-->
	<div class="pdt20">
		<div class="title fl">스템셀 목록</div>
		<div class="fr"> 
			<span class="boardBtn"><a href="#" class="btn btn-primary" style="width:100px"><span>업로드</span></a></span>
			<span class="boardBtn"><a href="#" class="btn btn-danger" style="width:100px"><span>삭제</span></a></span>
	    </div>
		
	<!-- <div id="hMargin"></div> -->
	</div>
	<div id="us_uploadStemcellsGrid" style="width:100%; height:500px">	
	
</div>