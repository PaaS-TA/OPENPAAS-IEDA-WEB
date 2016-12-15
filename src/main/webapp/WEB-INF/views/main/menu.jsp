<%
/* =================================================================
 * 작성일 : 
 * 작성자 : 
 * 상세설명 : 메뉴 화면
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016.05      박병주        시큐리티 기능 추가
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>  
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<script type="text/javascript">

 function goPage(page, title) {
 	if (typeof(window["clearMainPage"]) == "function") {
		clearMainPage();
	}
 	$.get(page, function (data, status, xhr) {
		w2ui['layout'].content('main', xhr.responseText);
		w2ui['layout'].resize();
		if (window.navigator.userAgent.indexOf('MSIE') != -1)
			setTimeout(function () { w2ui['layout'].resize(); }, 100);
		
    }).fail(function(xhr, status) {
    });
}	 
	//버튼 Onclick
	function btnHideAndShow(btn){
		if(btn=="configMenu"){
			if($(".configMenu").css('display') == 'none'){
				$(".configMenu").slideDown();
				$(".adminMenu").slideUp();
				$(".deployMenu").slideUp();
				$(".deploymentMenu").slideUp();
			}else{
				$(".configMenu").slideUp();
			}
		}else if(btn=="adminMenu"){
			if($(".adminMenu").css('display') == 'none'){
				$(".adminMenu").slideDown();
				$(".configMenu").slideUp();
				$(".deployMenu").slideUp();
				$(".deploymentMenu").slideUp();
			}else{
				$(".adminMenu").slideUp();
			}
		}else if(btn=="deployMenu"){
			if($(".deployMenu").css('display') == 'none'){
				$(".deployMenu").slideDown();
				$(".configMenu").slideUp();
				$(".adminMenu").slideUp();
				$(".deploymentMenu").slideUp();
			}else{
				$(".deployMenu").slideUp();
			}
		}else if(btn=="deploymentMenu"){
			if($(".deploymentMenu").css('display') == 'none'){
				$(".deploymentMenu").slideDown();
				$(".configMenu").slideUp();
				$(".adminMenu").slideUp();
				$(".deployMenu").slideUp();
			}else{
				$(".deploymentMenu").slideUp();
			}
		}
	}
	//초기 버튼 스타일
	$(function(){
			$(".configMenu").show();
			$(".adminMenu").hide();
			$(".deployMenu").hide();
			$(".deploymentMenu").hide();
	});

</script>

<!-- 왼쪽 메뉴 -->
<div id="left">
	<!-- 설치관리자 환경 설정 -->
	<div class="leftMenu_01"><div class="leftMenu_ti"><a href="#" onclick="btnHideAndShow('configMenu')">환경설정 및 관리</a></div></div>
	<div class="leftMenu li">
		<ul class="configMenu">
		<sec:authorize access="hasAuthority('CONFIG_DIRECTOR_MENU')">
			<li><a href="javascript:goPage('<c:url value="/config/director"/>', '설치관리자 설정');">설치관리자 설정</a></li>
		</sec:authorize>
		<sec:authorize access="hasAuthority('CONFIG_STEMCELL_MENU')">
			<li><a href="javascript:goPage('<c:url value="/config/stemcell"/>', 'Public 스템셀 다운로드');">스템셀 관리</a></li>
		</sec:authorize>
		<sec:authorize access="hasAuthority('CONFIG_RELEASE_MENU')">
			<li><a href="javascript:goPage('<c:url value="/config/systemRelease"/>', '릴리즈 관리');">릴리즈 관리</a></li>
		</sec:authorize>
		</ul>
	</div>
	
	<!-- 플랫폼 설치 자동화 관리 설정 -->
	<div class="leftMenu_02"><div class="leftMenu_ti"><a href="#" onclick="btnHideAndShow('adminMenu')">플랫폼 설치 자동화 관리</a></div></div>
	<div class="leftMenu li">
		<ul class="adminMenu">
			<sec:authorize access="hasAuthority('ADMIN_CODE_MENU')">
				<li><a href="javascript:goPage('<c:url value="/admin/code"/>', '코드 관리');">코드 관리</a></li>
			</sec:authorize>			
			<sec:authorize access="hasAuthority('ADMIN_ROLE_MENU')">
				<li><a href="javascript:goPage('<c:url value="/admin/role"/>', '권한 관리');">권한 관리</a></li>
			</sec:authorize>
			<sec:authorize access="hasAuthority('ADMIN_USER_MENU')">
				<li><a href="javascript:goPage('<c:url value="/admin/user"/>', '사용자 관리');">사용자 관리</a></li>
			</sec:authorize>
		</ul>
	</div>
	
	
	<!-- 서비스 설치 관리 -->
	<div class="leftMenu_03"><div class="leftMenu_ti"><a href="#" onclick="btnHideAndShow('deployMenu')">플랫폼 설치</a></div></div>
	<div class="leftMenu li">
		<ul class="deployMenu">
			<sec:authorize access="hasAuthority('DEPLOY_BOOTSTRAP_MENU')">
				<li><a href="javascript:goPage('<c:url value="/deploy/bootstrap"/>', 'BOOTSTRAP 설치');">BOOTSTRAP 설치</a></li>
			</sec:authorize>
			<sec:authorize access="hasAuthority('DEPLOY_BOSH_MENU')">
				<li><a href="javascript:goPage('<c:url value="/deploy/bosh"/>', 'BOSH 설치');">BOSH 설치</a></li>		
			</sec:authorize>
			<sec:authorize access="hasAuthority('DEPLOY_CF_MENU')">
				<li><a href="javascript:goPage('<c:url value="/deploy/cf"/>', 'CF 설치');">CF 설치</a></li>
			</sec:authorize>
			<sec:authorize access="hasAuthority('DEPLOY_DIEGO_MENU')">
				<li><a href="javascript:goPage('<c:url value="/deploy/diego"/>', 'DIEGO 설치');">DIEGO 설치</a></li>
			</sec:authorize>
			<sec:authorize access="hasAuthority('DEPLOY_CF_DIEGO_MENU')">
				<li><a href="javascript:goPage('<c:url value="/deploy/cfDiego"/>', 'CF & DIEGO 통합 설치');">CF & DIEGO 통합 설치</a></li>
			</sec:authorize>
			<sec:authorize access="hasAuthority('DEPLOY_SERVICEPACK_MENU')">
				<li><a href="javascript:goPage('<c:url value="/deploy/servicePack"/>', '서비스팩 설치');">서비스팩 설치</a></li>
			</sec:authorize>
		</ul>
	</div>
	
	<!-- 정보조회 -->
	<div class="leftMenu_04"><div class="leftMenu_ti"><a href="#" onclick="btnHideAndShow('deploymentMenu')">배포 정보 조회 및 관리</a></div></div>
	<div class="leftMenu li">
		<ul class="deploymentMenu">
			<sec:authorize access="hasAuthority('INFO_STEMCELL_MENU')">
				<li><a href="javascript:goPage('<c:url value="/info/stemcell"/>', '스템셀 업로드');">스템셀 업로드</a></li>		
			</sec:authorize>
			<sec:authorize access="hasAuthority('INFO_RELEASE_MENU')">
				<li><a href="javascript:goPage('<c:url value="/info/release"/>', '릴리즈 업로드');">릴리즈 업로드</a></li>
			</sec:authorize>
			<sec:authorize access="hasAuthority('INFO_DEPLOYMENT_MENU')">
				<li><a href="javascript:goPage('<c:url value="/info/deployment"/>', '배포목록');">배포 정보</a></li>
			</sec:authorize>
			<sec:authorize access="hasAuthority('INFO_TASK_MENU')">
				<li><a href="javascript:goPage('<c:url value="/info/task"/>', 'Task정보');">Task 정보</a></li>
			</sec:authorize>
			<sec:authorize access="hasAuthority('INFO_VM_MENU')">
				<li><a href="javascript:goPage('<c:url value="/info/vms"/>', 'VM 관리');">VM 관리</a></li>
			</sec:authorize>
			<sec:authorize access="hasAuthority('INFO_PROPERTY_MENU')">
				<li><a href="javascript:goPage('<c:url value="/info/property"/>', 'Property 관리');">Property 관리</a></li>
			</sec:authorize>
			<sec:authorize access="hasAuthority('INFO_SNAPSHOT_MENU')">
				<li><a href="javascript:goPage('<c:url value="/info/snapshot"/>', '스냅샷 관리');">스냅샷 관리</a></li>
			</sec:authorize>
			<sec:authorize access="hasAuthority('INFO_MANIFEST_MENU')">
				<li><a href="javascript:goPage('<c:url value="/info/manifest"/>', 'Manifest 관리');">Manifest 관리</a></li>
			</sec:authorize>
		</ul>
	</div>
</div>
<!-- //왼쪽 메뉴 끝-->