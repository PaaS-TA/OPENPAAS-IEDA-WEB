<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript">

$(function() {
	
 	$('#config_bootstrapGrid').w2grid({
		name: 'config_bootstrapGrid',
		show: {selectColumn: true, footer: true},
		multiSelect: false,
		style: 'text-align:center',
		columns:[
			 {field: 'status', caption: '상태', size: '20%'}
			,{field: 'name', caption: '이름', size: '20%'}
			,{field: 'iaas', caption: 'IaaS', size: '20%'}
			,{field: 'ip', caption: 'IP', size: '40%'}
		]
	});

});

//다른페이지 이동시 호출
function clearMainPage() {
	$().w2destroy('config_bootstrapGrid');
}

</script>

<div id="main">
	<div class="page_site">설치관리자 환경설정 > <strong>BOOTSTRAP 설치</strong></div>
	
	<!-- 설치 관리자 -->
<!-- 	<div class="title">설치 관리자</div>
	
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
 -->
 
	<!-- BOOTSTRAP 목록-->
	<div class="pdt20"> 
		<div class="title fl">BOOTSTRAP 목록</div>
		<div class="fr"> 
		<!-- Btn -->
		<span class="boardBtn"><a href="#" class="btn btn-primary" style="width:140px"><span>BOOTSTRAP 설치</span></a></span>
		<span class="boardBtn"><a href="#" class="btn btn-danger" style="width:140px"><span>BOOTSTRAP 삭제</span></a></span>
		<!-- //Btn -->
	    </div>
	</div>
	<div id="config_bootstrapGrid" style="width:100%; height:500px"/>	
	
</div>