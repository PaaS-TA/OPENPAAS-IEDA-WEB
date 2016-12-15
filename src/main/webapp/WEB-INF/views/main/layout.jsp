<%
/* =================================================================
 * 작성일 : 
 * 작성자 : 
 * 상세설명 : layout 화면(top/menu/main)
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
<meta http-equiv="Cache-Control" content="no-cache" /> 
<meta http-equiv="Expires" content="0" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" /> 
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Open PaaS 플랫폼 설치 자동화</title>

<!-- CSS  -->
<link rel="stylesheet" type="text/css" href="<c:url value='/webjars/bootstrap/3.3.5/css/bootstrap.min.css'/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value='/webjars/w2ui/1.4.2/w2ui.min.css'/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value='/webjars/jquery-ui/1.11.4/jquery-ui.css'/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css'/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value='/css/guide.css'/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value='/css/common.css'/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value='/css/content.css'/>"/>

<link rel="stylesheet" type="text/css" href="<c:url value='/css/progress-step.css'/>"/>

<!-- JQuery -->
<script type="text/javascript" src="<c:url value='/webjars/jquery/2.1.1/jquery.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/webjars/jquery-ui/1.11.4/jquery-ui.js'/>"></script>

<!-- JQuery Form -->
<script type="text/javascript" src="<c:url value='/webjars/jquery-form/3.51/jquery.form.js'/>"></script>

<!-- W2UI -->
<script type="text/javascript" src="<c:url value='/webjars/w2ui/1.4.2/w2ui.min.js'/>"></script>

<!-- Common -->
<script type="text/javascript" src="<c:url value='/js/common.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/commonPopup.js'/>"></script>

<!-- socket -->
<script type="text/javascript" src="<c:url value='/js/sockjs-0.3.4.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/stomp.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/yaml.js'/>"></script>

<script type="text/javascript">
(function($) {
    $.ajaxSetup({
        error: function(xhr, status, err) {
            if (xhr.status == 403) {
                   location.href="/abuse";
            }
        }
    });
})(jQuery);

$(function() {
	var pstyle = 'background-color: white; overflow-y: hidden;';
	$('#layout').w2layout({
		name: 'layout',
		panels: [
			 { type: 'top', style: pstyle, size: 71}
			,{ type: 'left', style: pstyle, size:235}
			,{ type: 'main', style: pstyle}
		],  onError: function(event) {
	    }        
	});
	
	setLayoutContainerHeight();
		w2ui['layout'].load('top', 'top');
		w2ui['layout'].load('left', 'menu');
		w2ui['layout'].load('main', 'main/dashboard');
});

function setLayoutContainerHeight(login){
    var layoutHeight = $(window).height();
    var layoutWidth = $(window).width();
    
    $('#wrap1').height(layoutHeight);
    if(login=="login"){
    	setLayout = true;
    	w2ui['layout'].destroy();
    	location.href="/login?code=abuse";
    }else{
    	w2ui['layout'].resize();
    }
}
</script>

</head>
<body>

<div id="wrap1">
	<div id="layout" class='fullBox'></div>
</div>

</body>
</html>