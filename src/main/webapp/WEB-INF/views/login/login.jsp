<%
/* =================================================================
 * 작성일 : 2016.05.24
 * 작성자 : 박병주
 * 상세설명 : 로그인 정보 입력 화면
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016.12       이동현        코드 버그 수정 및 script 기능 개선
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String code = request.getParameter("code");
	String errorMsg = (String)request.getParameter("exceptionmsgname");
%>
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
<!-- JQuery -->
<script type="text/javascript" src="<c:url value='/webjars/jquery/2.1.1/jquery.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/webjars/jquery-ui/1.11.4/jquery-ui.js'/>"></script>
<!-- W2UI -->
<script type="text/javascript" src="<c:url value='/webjars/w2ui/1.4.2/w2ui.min.js'/>"></script>


<script type="text/javascript">

$(document).ready(function(){ 
	$("#errMsg").text('');
	$("#errMsg").css("color", "black");
	$("#errMsg").css("text-align", "center");
	
	if('<%=code%>'!='' && '<%=code%>' =='authFail'){
		$("#errMsg").css("color", "red");	
		$("#errMsg").html("아이디 혹은 비밀번호를 잘못 입력했습니다."); 
	};
	
	if('<%=code%>'!='' && '<%=code%>' == 'logout'){ 
		$("#errMsg").html("로그아웃 되었습니다."); 
	};
	
	if('<%=code%>'!='' && '<%=code%>' == 'abuse'){ 
		$("#errMsg").css("color", "red");
		$("#errMsg").html("부적절한 접근시도로 로그아웃 되었습니다."); 
	};
	
	if('<%=code%>'!='' && '<%=code%>' == 'loging'){ 
		$("#errMsg").css("color", "red");
		$("#errMsg").html("현재 접속 중입니다. 로그아웃 후 접속해주세요."); 
	};
	
	if('<%=code%>'!='' && '<%=code%>' == 'expire'){ 
		w2ui['layout'].destroy();
		location.href = "/login?code=abuse";
	};
	
	if('<%=code%>'!='' && '<%=code%>' == 'invalid'){ 
		w2ui['layout'].destroy();
		location.href = "/login?code=logout";
	};
});	

function loginFn() {
	
	$("#errMsg").text('');
	$("#errMsg").css("color", "black");

	if($("#username").val()==''){
		$("#errMsg").css("color", "red");
		$("#errMsg").html("아이디를 입력해 주십시오.");
		return;
	}
	
	if($("#password").val()==''){
		$("#errMsg").css("color", "red");
		$("#errMsg").html("패스워드를 입력해 주십시오.");
		return;
	}
	
	loginForm.action="/login";
	loginForm.submit();
}

</script>
<style>
	body {
		background-color: white;
	}
	#loginbox {
		margin-top: 30px;
	}
	#loginbox > div:first-child {
		padding-bottom: 10px;
	}
	.iconmelon {
		display: block;
		margin: auto;
	}
	#form > div {
		margin-bottom: 25px;
	}
	#form > div:last-child {
		margin-top: 10px;
		margin-bottom: 10px;
	}
	.panel {
		background-color: transparent;
	}
	.panel-body {
		padding-top: 30px;
		background-color: rgba(2555,255,255,.3);
	}
	.iconmelon,
	.im {
		position: relative;
		width: 150px;
		height: 150px;
		display: block;
		fill: #525151;
	}
	.iconmelon:after,
	.im:after {
		content: '';
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
	}
	.btn-login {
		color: #fff;
		border-color: rgb(75, 84, 100);
		background: rgb(75, 84, 100);
	}
	.span-left {
		float: left;
	}
	.span-right {
		float: right;
	}
	.panel-body2 {
		padding: 15px;
		border: 1px #cccccc solid;
	}
	.custom-login-form {
		padding: 10px 0;
	}
</style>
</head>
<body>
	<div class="container">
		<div id="loginbox" class="mainbox col-md-6 col-md-offset-3 col-sm-6 col-sm-offset-3">

		<div class="row">
			<div class="iconmelon">
			</div>
		</div>
		
		<div class="panel panel-default" >
			<div class="panel-heading" style="background: rgb(75, 84, 100);">
				<div class="panel-title text-center" style="background: rgb(75, 84, 100);"><img src="../images/logo.png"></div>
			</div>
			
		<div class="panel-body2" >
				<form id="loginForm" method="post">
					<fieldset>
						<div class="form-group text-center custom-login-form">
							<span>플랫폼 설치 자동화에 오신 것을 환영합니다.</span>
						</div>
						<div class="form-group custom-login-form">
							<input class="form-control" placeholder="userId" id="Username" name="username" type="text" />
						</div>
						<div class="form-group custom-login-form">
						    	<input class="form-control" placeholder="Password" type="password" id="password" name="password" />
						 		<span id="errMsg"></span>
						 </div>
						   <button class="btn btn-lg btn-login btn-block"  id="login" onclick="javascript:loginFn(); return false;">LOGIN</button>
				    </fieldset>
				</form>
				
		</div>
		</div>
		</div>
	</div>
</body>