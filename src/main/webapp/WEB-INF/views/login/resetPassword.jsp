<%
/* =================================================================
 * 작성일 : 2016.05.24
 * 작성자 : 박병주
 * 상세설명 : 비밀번호 변경 입력 화면
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 *
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String code = request.getParameter("code");
%>
</!DOCTYPE html>
<html>
<head>
<meta charset="utf-8"/>

<!-- CSS  -->
<link rel="stylesheet" type="text/css" href="<c:url value='/webjars/bootstrap/3.3.5/css/bootstrap.min.css'/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value='/webjars/w2ui/1.4.2/w2ui.min.css'/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value='/webjars/jquery-ui/1.11.4/jquery-ui.css'/>"/>

<!-- JQuery -->
<script type="text/javascript" src="<c:url value='/webjars/jquery/2.1.1/jquery.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/webjars/jquery-ui/1.11.4/jquery-ui.js'/>"></script>

<!-- JQuery Form -->
<script type="text/javascript" src="<c:url value='/webjars/jquery-form/3.51/jquery.form.js'/>"></script>

<!-- W2UI -->
<script type="text/javascript" src="<c:url value='/webjars/w2ui/1.4.2/w2ui.min.js'/>"></script>
<style type="text/css">
a{ text-decoration: none; }

#div_wrap {  
	width:100%;
}
.login-field{
	margin:10% auto;
	display: block;	
	height: 500px;
	width:500px;	
	border:1px solid #e9e9e9;
	color:#999;	
}

.login-field > p{
	text-align:center;
	font-size: 30px; 
	margin-top:70px;
}

.box-inner{
	margin-top:50px;
}

.box-inner > ul {  padding:0px; }

.box-inner ul li{
	padding:0px;
	margin:0 auto;
	text-align:center;
/* 	width:100%; */
	line-height:2;
	list-style: none;
}

.box-inner ul li > ul{
	width:300px;
	padding:0px;
	margin: 0 auto;
}

.labels{
	width:200px; 
	text-align:left !important;
}

.input-box{
	width: 200px;
	height:40px;	
	border:1px solid #999 !important;
	position: relative;	
}	

.mesg-alert { text-align:center }
.mesg-alert p{
	color:red;
	font-size: 12px;
	margin-top: 5px;
}

.submit-btn{
	display:inline-block;
	text-align:center;
	height:40px;
	line-height: 40px;
	width: 200px;
	color:#fff !important;
	background-color:#000;
	margin-top: 65px;	
}

.submit-btn:hover{
	cursor: pointer;
	background-color: #999;	
	
}

.text12{
	font-size:12px;
}

.u-line{
	text-decoration: underline;
}

.u-line:hover{
	cursor: pointer;
}
</style>

<script type="text/javascript">
$(document).ready(function(){ 
	$("#errMsg").css("color", "black");
	$("#errMsg").text('패스워드를 재설정 해주십시오.');
});	

function validateAllInputField() {
	var isOk = true;
	
	$("#errMsg").text('');
	$("#errMsg").css("color", "black");

	if($("#password").val()==''){
		$("#errMsg").css("color", "red");
		$("#password").focus();
		$("#errMsg").html("패스워드를 입력해 주십시오.");
		return false;
	}
	
	if($("#password2").val()==''){
		$("#errMsg").css("color", "red");
		$("#password2").focus();
		$("#errMsg").html("패스워드확인을 입력해 주십시오.");
		return false;
	}
	
	if($("#password").val()!=$("#password2").val()){
		$("#errMsg").css("color", "red");
		$("#errMsg").html("패스워드가 확인과 일치 하지 않습니다.");
		return false;
	}
	
	return isOk;
} 

function saveConfirm() {
	
	if ( !validateAllInputField() ) { return; }	
	
	w2confirm({
		title 	: "패스워드 재설정",
		msg		: "저장 하시겠습니까?",
		yes_text	: "확인",
		no_text		: "취소",
		yes_callBack: function(event){
			$.ajax({
				type : "POST",
				url : "/common/user/savePassword",
				contentType : "application/json",
				async : true,		
				data : JSON.stringify({
					password : $("#password").val(),
				}),
				success : function(data, status) {
					$("#errMsg").text('');
					$("#errMsg").css("color", "black");
					saved();				
				},
				error : function(request, status, error) {
					var errorResult = JSON.parse(request.responseText);
					$("#errMsg").css("color", "red");
					$("#errMsg").html("저장 실패하였습니다. 관리자에게 문의 하십시오.");			
				}
			}); 
		},
		no_callBack	: function(){
		}
	});
}

function saved() {
 	w2confirm({
		title 	: "패스워드 저장완료",
		msg		: "저장 되었습니다",
		yes_text	: "확인",
		no_text		: "닫기",
		yes_callBack: function(event){
			// 메인화면을 표시한다.
			var page = "<c:url value="/" />";			
			window.location.href = page;
		}				
	}); 
}
</script>
</head>
<body>
	<div id="div_wrap">
		<div class="login-field">
			<p style=""> 비밀번호 변경 </p>
			<div class="box-inner">
				<ul>
					<li>							
						<ul>
							<li class="labels">
								<label for="">Password: </label>
							</li>
							<li>
								<input name="password" id="password" type="password" class="input-box login-box" maxlength="50" autocomplete="off" value="" placeholder="">	
							</li>
						</ul>
					</li>
					<li>
						<ul>
							<li class="labels">
								<label for="">Confirm Password: </label>
							</li>
							<li>
								<input name="password" id="password2" type="password" class="input-box password" maxlength="20" autocomplete="off" placeholder="">						
							</li>
						</ul>
					</li>
					
					<li class="mesg-alert">
						<p><span id="errMsg"></span></p>
					</li>			
					<li style="text-align:center">
						<a href="#" onclick="javascript:saveConfirm(); return false;"><span class="submit-btn">확인</span></a>											
					</li>
				</ul>		
			</div>
		</div>
	</div>
</body>
</html>