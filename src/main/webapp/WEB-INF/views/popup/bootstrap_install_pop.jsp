<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<div id="installDiv" style="width:100%;height:100%;" hidden="true">
		<div rel="title"><b>BOOTSTRAP 설치</b></div>
		<div rel="body" style="padding:15px 5px 15px 5px;">
			<div class="processbar">
	            <ul class="process_s05">
		            <li class="process_sti1">AWS 설정</li>
		            <li class="process_sti2">Network 설정</li>
		            <li class="process_sti3">리소스 설정</li>
		            <li class="process_sti4">배포 Manifest</li>
		            <li class="process_sti5">설치</li>
	            </ul>
	        </div>
			<div rel="sub-title" style="margin-bottom: 10px;">▶ 설치 로그</div>
			<div>
				<textarea id="installLogs" style="width:95%;height:250px;overflow-y:visible;resize:none;background-color: #FFF;margin-left:1%" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
				<!-- 설치 실패 시 -->
				<button class="btn" style="float: left;" name="cancleBtn" onclick="goDeployInfo();">이전</button>
				<button class="btn" name="cancleBtn" onclick="popupClose();">취소</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="popupClose();">완료</button>
		</div>		
	</div>
</body>
</html>