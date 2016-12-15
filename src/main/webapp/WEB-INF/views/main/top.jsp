<%
/* =================================================================
 * 작성일 : 
 * 작성자 : 
 * 상세설명 : Top 화면
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016.10      지향은         OpenPaaS 이미지 수정
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style type="text/css">
	.log-out-btn{
		width:100px;
		height:35px;
		font-size:13px;
		font-family: sans-serif;
		text-decoration: none;
		padding:7px 18px;
		border:1px solid #eee;
		color:#eee;
		top:-50px;
		right:30px;
		float: right;
		position: relative;
		background-color: rgb(44,50,72);
	}	

	.log-out-btn:hover{
		cursor: pointer;
		color: rgb(113,113,113);
		background-color:#e9e9e9;
		transition: 0.4s;	
	}
</style>


<a href="<c:url value='/'/>"><div id="header"></div></a>
<a href="<c:url value="/logout"/>" class="log-out-btn">LOG OUT</a>		
