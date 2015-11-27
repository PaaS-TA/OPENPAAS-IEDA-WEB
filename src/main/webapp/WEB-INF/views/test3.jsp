<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
	response.setHeader("Cache-Control", "no-cache");
	response.setHeader("Pragma", "no-cache");
	response.setDateHeader("Expires", 0);
%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Scoket Test</title>

<!-- CSS  -->
<link rel="stylesheet" type="text/css"
	href="/webjars/bootstrap/3.3.5/css/bootstrap.min.css" />
<link rel="stylesheet" type="text/css"
	href="/webjars/w2ui/1.4.2/w2ui.min.css" />
<link rel="stylesheet" type="text/css"
	href="/webjars/jquery-ui/1.11.4/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="/css/default.css" />
<link rel="stylesheet" type="text/css" href="/css/guide.css" />

<!-- JQuery -->
<script type="text/javascript" src="/webjars/jquery/2.1.1/jquery.min.js"></script>
<script type="text/javascript"
	src="/webjars/jquery-ui/1.11.4/jquery-ui.js"></script>

<!-- JQuery Form -->
<script type="text/javascript"
	src="/webjars/jquery-form/3.51/jquery.form.js"></script>

<!-- W2UI -->
<script type="text/javascript" src="/webjars/w2ui/1.4.2/w2ui.min.js"></script>

<!-- Bootstrap button.js -->
<script type="text/javascript" src="/webjars/bootstrap/3.3.5/css/bootstrap.min.js"></script>
<script type="text/javascript" src="/webjars/bootstrap/3.3.5/css/button.js"></script>
<!-- Common -->
<script type="text/javascript" src="/js/common.js"></script>
<script type="text/javascript" src="/js/commonPopup.js"></script>
<script type="text/javascript" src="/js/sockjs-0.3.4.js"></script>
<script type="text/javascript" src="/js/stomp.js"></script>
<script type="text/javascript">
	$(function(){
		$("#matchFile").click(function(){
			console.log("###########filePath" + $("#testFile").val());
			
			$.ajax({
				url: '/release/test3',
				method : 'post',
				contentType : "application/json",
				data :  JSON.stringify({"filePath" : $("#testFile").val()}),
				sucess : function(status){
										
				},
				error: function(request , status, request){
					
				}
			})			
		});
	});
</script>
</head>
<body>
	<div>
		<input id="testFile" type="text" style="width: 300px;" value="src\main\resources\static\template\bosh-init-aws-micro-input-sample.yml"/>
		<button id="matchFile">matchFile</button>
	</div>
</body>
</html>
