<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript">

$(function() {
	
 	$('#config_opStemcellsGrid').w2grid({
		name: 'config_opStemcellsGrid',
		show: {selectColumn: true, footer: true},
		multiSelect: false,
		style: 'text-align:center',
		columns:[
			 {field: 'os', caption: '운영체계', size: '10%'}
			,{field: 'os_version', caption: '버전', size: '10%'}
			,{field: 'iaas', caption: 'IaaS', size: '10%'}
			,{field: 'stemcell_version', caption: '스템셀버전', size: '10%'}
			,{field: 'stemcell_filename', caption: '파일명', size: '40%'}
			,{field: 'download', caption: '다운로드여부', size: '10%'}
		]
	});
 	
 	getDefaultDirector();

});

//다른페이지 이동시 호출
function clearMainPage() {
	$().w2destroy('config_opStemcellsGrid');
}

</script>

<div id="main">
	<div class="page_site">설치관리자 환경설정 > <strong>OpenPaaS 스템셀 관리</strong></div>
	
	<!-- OpenPaaS 스템셀 목록-->
	<div class="title">OpenPaaS 스템셀 목록</div>
	

 	<div class="search_box" style align="center">
		<span class="search_li">OS</span>&nbsp;&nbsp;&nbsp;
		<select name="select" class="select" style="width:120px"><option>Ubuntu</option></select>
		<span class="search_li">OS버전</span>&nbsp;&nbsp;&nbsp;
		<select name="select" class="select" style="width:120px"><option>Trusty</option></select>
		<span class="search_li">IaaS</span>&nbsp;&nbsp;&nbsp;
		<select name="select" class="select" style="width:120px"><option>Openstack</option></select>
		<span class="boardBtn">&nbsp;&nbsp;&nbsp;<a href="#" class="btn btn-info" style="width:100px"><span>조회</span></a></span>
		<span class="boardBtn"><a href="#" class="btn btn-primary" style="width:100px"><span>다운로드</span></a></span>
		<span class="boardBtn"><a href="#" class="btn btn-danger" style="width:100px"><span>삭제</span></a></span>
		
	</div> 	
	
	<div id="config_opStemcellsGrid" style="width:100%; height:500px"/>	
	
</div>