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

	<!-- 서비스 설치 관리 -->
	<div class="leftMenu_01"><div class="leftMenu_ti">서비스 설치 관리</div></div>
	<div class="leftMenu li">
		<ul>
		<li><a href="javascript:goPage('<c:url value="/deploy/list"/>', '서비스 설치');">서비스 설치</a></li>
		<li><a href="javascript:goPage('<c:url value="/deploy/listRelease"/>', '릴리즈 업로드');">릴리즈 업로드</a></li>
		<li><a href="javascript:goPage('<c:url value="/deploy/listStemcell"/>', '스템셀 업로드');">스템셀 업로드</a></li>
		</ul>
	</div>
	
	<!-- 서비스 정보 조회 -->
	<div class="leftMenu_02"><div class="leftMenu_ti">서비스 정보조회</div></div>
	<div class="leftMenu li">
		<ul>
		<li><a href="javascript:goPage('<c:url value="/information/listDeployment"/>', '설치목록');">설치 목록</a></li>
		<li><a href="javascript:goPage('<c:url value="/information/listTaskHistory"/>', 'Task실행이력');">Task 실행 이력</a></li>
		</ul>
	</div>
	
	<!-- 설치관리자 환경 설정 -->
	<div class="leftMenu_03"><div class="leftMenu_ti">설치관리자 환경설정</div></div>
	<div class="leftMenu li">
		<ul>
		<li><a href="javascript:goPage('<c:url value="/config/listDirector"/>', '설치관리자 설정');">설치관리자 설정</a></li>
		<li><a href="javascript:goPage('<c:url value="/config/bootstrap"/>', 'BOOTSTRAP 설치');">BOOTSTRAP 설치</a></li>
		<li><a href="javascript:goPage('<c:url value="/config/bosh"/>', 'BOSH 설치');">BOSH 설치</a></li>
		<li><a href="javascript:goPage('<c:url value="/config/stemcellManagement"/>', '스템셀 관리');">스템셀 관리</a></li>
		<li><a href="javascript:goPage('<c:url value="/config/releaseManagement"/>', '릴리즈 관리');">릴리즈 관리</a></li>
		</ul>
	</div>           
</div>
<!-- //왼쪽 메뉴 끝-->