<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
    	ajaxErrorMsg(xhr);
    });
}

</script>

<!-- 왼쪽 메뉴 -->
<div id="left">

	<!-- 설치관리자 환경 설정 -->
	<div class="leftMenu_01"><div class="leftMenu_ti">환경설정 및 관리</div></div>
	<div class="leftMenu li">
		<ul>
		<li><a href="javascript:goPage('<c:url value="/config/listDirector"/>', '설치관리자 설정');">설치관리자 설정</a></li>
		<li><a href="javascript:goPage('<c:url value="/config/stemcellManagement"/>', 'Public 스템셀 다운로드');">스템셀 관리</a></li>
		</ul>
	</div>
	
	<!-- 서비스 설치 관리 -->
	<div class="leftMenu_02"><div class="leftMenu_ti">플랫폼 설치</div></div>
	<div class="leftMenu li">
		<ul>
		<li><a href="javascript:goPage('<c:url value="/deploy/bootstrap"/>', 'BOOTSTRAP 설치');">BOOTSTRAP 설치</a></li>
		<li><a href="javascript:goPage('<c:url value="/deploy/bosh"/>', 'BOSH 설치');">BOSH 설치</a></li>		
		<li><a href="javascript:goPage('<c:url value="/deploy/cf"/>', 'CF 설치');">CF 설치</a></li>
		<li><a href="javascript:goPage('<c:url value="/deploy/diego"/>', 'DIEGO 설치');">Diego 설치</a></li>
		</ul>
	</div>
	
	<!-- 정보조회 -->
	<div class="leftMenu_03"><div class="leftMenu_ti">정보조회</div></div>
	<div class="leftMenu li">
		<ul>
		<li><a href="javascript:goPage('<c:url value="/information/listStemcell"/>', '스템셀 업로드');">스템셀 업로드</a></li>		
		<li><a href="javascript:goPage('<c:url value="/information/listRelease"/>', '릴리즈 업로드');">릴리즈 업로드</a></li>
		<li><a href="javascript:goPage('<c:url value="/information/listDeployment"/>', '배포목록');">배포 정보</a></li>
		<li><a href="javascript:goPage('<c:url value="/information/listTaskHistory"/>', 'Task정보');">Task 정보</a></li>
		</ul>
	</div>
</div>
<!-- //왼쪽 메뉴 끝-->