<%
/* =================================================================
 * 작성일 : 2016.07
 * 작성자 : 이동현
 * 상세설명 : 사용자 관리 화면
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016.12      이동현         사용자 관리 화면 버그 수정
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script>
	var roleArray = [];
	$(function(){
		 /********************************************************
		  *  설명		: 사용자 목록 Grid
		 *********************************************************/
			$('#user_GroupGrid').w2grid({
				name: 'user_GroupGrid',
				header: '<b>사용자 목록</b>',
				method: 'GET',
					multiSelect: false,
				show: {	
						selectColumn: true,
						footer: true},
				style: 'text-align: center',
				columns:[
					  { field: 'recid', hidden: true },
			          { field: 'userId', caption: '아이디', size:'35%', style:'text-align:center;' },
			          { field: 'userName', caption: '이름', size:'65%', style:'text-align:center;'},
			          { field: 'email', caption: '이메일', size:'65%', style:'text-align:center;'},
			          { field: 'roleName', caption: '권한', size:'65%', style:'text-align:center;'}
			      	],
			    onSelect : function(event) {
					event.onComplete = function() {
						$('#modifyBtn').attr('disabled', false);
						$('#deleteBtn').attr('disabled', false);
						return;
					}
				},
				onUnselect : function(event) {
					event.onComplete = function() {
						$('#modifyBtn').attr('disabled', true);
						$('#deleteBtn').attr('disabled', true);
						return;
					}
				},onLoad:function(event){
					if(event.xhr.status == 403){
						location.href = "/abuse";
						event.preventDefault();
					}
				},onError : function(event) {
				}
			 });
		
	
		/********************************************************
		  *  설명		: 사용자 관리 등록 버튼 클릭
		 *********************************************************/
		$("#addBtn").click(function(){
			w2popup.open({
				title 	: "<b>사용자 등록</b>",
				width 	: 555,
				height	: 600,
				modal	: true,
				body	: $("#regPopupDiv").html(),
				buttons : $("#regPopupBtnDiv").html(),
				onClose : function(event){
					w2ui['user_GroupGrid'].clear();
					doSearch();
				}
			});
			roleArray = [];
			getRoleListAjax();
			$(".w2ui-msg-body input[name='userPassword']").attr("readonly","readonly");
			$(".w2ui-msg-body input[name='userPassword']").val("1234");
		});
		
		/* ****************** 사용자 관리 수정 버튼 클릭 ****************** */
		$("#modifyBtn").click(function(){
			if($("#modifyBtn").attr('disabled') == "disabled") return;	
			var selected = w2ui['user_GroupGrid'].getSelection();
			if( selected.length == 0 ){
				w2alert("선택된 정보가 없습니다.", "사용자 관리 수정");
				return;
			}
			var recode = w2ui['user_GroupGrid'].get(selected);
			w2confirm({
				title		: "사용자 수정",
				msg			: "사용자("+recode.userId + ")을 수정하시겠습니까?",
				yes_text	: "확인",
				no_text		: "취소",
				yes_callBack: function(event){
					updatePopup(w2ui['user_GroupGrid'].get(selected));
				},
				no_callBack	: function(){
					w2ui['user_GroupGrid'].clear();
					doSearch();
				}
			});
			
		});
		
		/********************************************************
		  *  설명		: 사용자 관리 삭제 버튼 클릭
		 *********************************************************/
		$("#deleteBtn").click(function(){
			if($("#deleteBtn").attr('disabled') == "disabled") return;
			var selected = w2ui['user_GroupGrid'].getSelection();		
			if( selected.length == 0 ){
				w2alert("선택된 정보가 없습니다.", "사용자 삭제");
				return;
			}
			else {
				var record = w2ui['user_GroupGrid'].get(selected);
				w2confirm({
					title		: "사용자 삭제",
					msg			: "사용자("+record.userId + ")을 삭제하시겠습니까?",
					yes_text	: "확인",
					no_text		: "취소",
					yes_callBack: function(event){
						deleteUser(record.userId);
					},
					no_callBack	: function(){
						w2ui['user_GroupGrid'].clear();
						doSearch();
					}
				});
			}
		});
		
		doSearch();
	})
	
	/********************************************************
	 * 설명		: 사용자 조회
	 * Function	: doSearch
	 *********************************************************/
	function doSearch() {
		w2ui['user_GroupGrid'].load('/admin/user/list');
		doButtonStyle(); 
	}
	
	/********************************************************
	 * 설명		: 초기 버튼 스타일
	 * Function	: doButtonStyle
	 *********************************************************/
	function doButtonStyle() {
		$('#modifyBtn').attr('disabled', true);
		$('#deleteBtn').attr('disabled', true);
	}
	
	/********************************************************
	 * 설명		: 화면 리사이즈시 호출
	 *********************************************************/
	$( window ).resize(function() {
		setLayoutContainerHeight();
	});
	
	/********************************************************
	 * 설명		: Lock 팝업 메세지 Function
	 * Function	: lock
	 *********************************************************/
	function lock (msg) {
	    w2popup.lock(msg, true);
	}
	
	/********************************************************
	 * 설명		: 다른 페이지 이동 시 호출 Function
	 * Function	: clearMainPage
	 *********************************************************/
	function clearMainPage() {
		$().w2destroy('user_GroupGrid');
	}
	
	/********************************************************
	 * 설명		: 권한 정보 조회 Ajax
	 * Function	: getRoleListAjax
	 *********************************************************/
	function getRoleListAjax(type,record){
		roleArray = [];
		updateroleArray = [];
		$.ajax({
			type : "GET",
			url : "/admin/role/group/list",
			async : true,
			contentType : "application/json",
			success : function(data, status) {
				var result = "";
				result += '<table style="width:100%; text-align:center;" class="table table-hover">'
				for(var i=0;i<data.records.length;i++){
					result +='<tr style="margin: 0 auto;"><label><td style="width:12%;"><input type="radio" name="roleRadio" value="'+data.records[i].roleId+'" id="'+data.records[i].roleId+'i"></td><td style="width:42%"; class="active">';
					result +=data.records[i].roleName +'</label></td><td>'+ data.records[i].roleDescription +'</td></tr>';
				}
				result += '</table>'
				$('.writeWarrper').html(''+result+'');	
				if(type=='update'){				
					 $('.w2ui-msg-body input:radio[name=roleRadio]:input[value='+record.roleId+']').attr("checked",true);
				}
			},
			error : function(request, status, error) {
				w2popup.unlock();
				w2popup.close();
				var errorResult = JSON.parse(request.responseText);
				w2alert(errorResult.message);
			}
		});
	}
	
	/********************************************************
	 * 설명		: 사용자 등록 Ajax
	 * Function	: registUser
	 *********************************************************/
	var rolechecked;
	function registUser(){
		roleArray = [];
	 	lock( '사용자 등록 중입니다.', true); 
	 	$('input:radio[name=roleRadio]:checked').each(function(){
	 		rolechecked = $(this).val();
	 		roleArray.push(rolechecked);
	 	});
 		if(roleArray.length>1){
 			w2alert("한개의 권한만 등록 가능 합니다.");
 			return;
 		}
 		if(roleArray.length==0){
 			w2alert("한개의 권한을 등록 하세요.");
 			return;}
		userInfo = {
				userId : $(".w2ui-msg-body input[name='userId']").val(),			
				userName : $(".w2ui-msg-body input[name='userName']").val(),
				userPassword : $(".w2ui-msg-body input[name='userPassword']").val(),
				email : $(".w2ui-msg-body input[name='email']").val(),
				initPassYn :$("input[name='initPassYn']").val(),
				roleId : rolechecked
		}
		
		if(popupValidation()){
			$.ajax({
				type : "POST",
				url : "/admin/user/add",
				contentType : "application/json",
				//dataType: "json",
				async : true,		
				data : JSON.stringify(userInfo),
				success : function(status) {
					w2popup.unlock();
					w2popup.close();
					w2ui['user_GroupGrid'].clear();
					doSearch();
					roleArray = [];
				},
				error : function(request, status, error) {
					// ajax가 실패할때 처리...
					w2popup.unlock();
					w2popup.close();
					var errorResult = JSON.parse(request.responseText);
					w2alert(errorResult.message);
				}
			});
		}
	}
	
	/********************************************************
	 * 설명		: 사용자 관리 수정팝업
	 * Function	: updatePopup
	 *********************************************************/
	function updatePopup(record) {
		updateroleArray = [];
				w2popup.open({
					title 	: "<b>사용자 수정</b>",
					width 	: 555,
					height	: 600,
					modal	: true,
					body	: $("#regPopupDiv").html(),
					buttons : $("#updatePopupBtnDiv").html(),
					onOpen : function(event){
						event.onComplete = function(){
							$(".w2ui-msg-body input[name='uid']").val(record.uid);
							$(".w2ui-msg-body input[name='userId']").val(record.userId);
							$(".w2ui-msg-body input[name='userName']").val(record.userName);
							$(".w2ui-msg-body input[name='userPassword']").val(record.userPassword);
							$(".w2ui-msg-body input[name='email']").val(record.email);
							$(".w2ui-msg-body input[name='roleId']").val(record.roleId);
							$(".w2ui-msg-body input[name='userId']").attr("readonly","readonly");
							getRoleListAjax("update",record);
						}
					},onClose : function(event){
						w2ui['user_GroupGrid'].clear();
						doSearch();
					}
				});
	}
	
	/********************************************************
	 * 설명		: 사용자 관리 수정 Function
	 * Function	: updateUser
	 *********************************************************/
	var updaterolechecked;
	var updateroleArray = [];
	function updateUser() {
		updateroleArray = [];
		$('input:radio[name=roleRadio]:checked').each(function(){
			updaterolechecked = $(this).val();
			updateroleArray.push(rolechecked);
	 	});
 		if(updateroleArray.length>1){
 			w2alert("한개의 권한만 등록 가능 합니다.");
 			return;
 		}
 		if(updateroleArray.length==0){
 			w2alert("한개의 권한을 등록 하세요.");
 			return;
 		}
		authInfo = {
				userId : $(".w2ui-msg-body input[name='userId']").val(),			
				userName : $(".w2ui-msg-body input[name='userName']").val(),
				userPassword : $(".w2ui-msg-body input[name='userPassword']").val(),
				email : $(".w2ui-msg-body input[name='email']").val(),
				initPassYn :$("input[name='initPassYn']").val(),
				roleId : updaterolechecked
		}
		if(popupValidation()){
			$.ajax({
				type : "PUT",
				url : "/admin/user/update/" + $(".w2ui-msg-body input[name='userId']").val(),
				contentType : "application/json",
				async : true,		
				data : JSON.stringify(authInfo),
				success : function(status) {
					lock( '수정 중입니다.', true);		
					w2popup.unlock();
					w2popup.close();
					w2ui['user_GroupGrid'].clear();
					doSearch();				
					updateroleArray = [];
				},
				error : function(request, status, error) {
					// ajax가 실패할때 처리...
					w2popup.unlock();
					w2ui['user_GroupGrid'].clear();
					doSearch();
					w2popup.close();			
					var errorResult = JSON.parse(request.responseText);
					w2alert(errorResult.message);
				}
			});
		}
	}
	
	/********************************************************
	 * 설명		: 권한 그룹 삭제 Function
	 * Function	: deleteUser
	 *********************************************************/
	function deleteUser(userId){
		$.ajax({
			type : "DELETE",
			url : "/admin/user/delete/"+ userId,
			async : true,
			contentType : "application/json",
			success : function(status) {
				w2popup.unlock();
				w2popup.close();
				w2ui['user_GroupGrid'].clear();
				doSearch();
			},
			error : function(request, status, error) {
				var errorResult = JSON.parse(request.responseText);
				w2ui['user_GroupGrid'].clear();
				doSearch();
				w2alert(errorResult.message, "삭제 실패");
			}
		});
	}


</script>	
<style>
 	.w2ui-popup .w2ui-msg-body { overflow:initial; }
</style>
<div id="main">
	<div class="page_site">플랫폼 설치 자동화 관리 > <strong>사용자 관리</strong></div>

	<!-- 사용자 목록-->
	<div class="pdt20">
		<div class="title fl">사용자 목록</div>
		<div class="fr"> 
			<span id="addBtn" class="btn btn-primary" style="width:120px">등록</span>
			<span id="modifyBtn" class="btn btn-info" style="width:120px">수정</span>
			<span id="deleteBtn" class="btn btn-danger" style="width:120px">삭제</span>
	    </div>
	</div>
	<div id="user_GroupGrid" style="width:100%;  height:718px;"></div>
	<!-- 사용자 추가/수정 팝업 -->
	<div id="regPopupDiv" hidden="true">
		<form id="settingForm" action="POST" style="padding:2%; margin:0;">
			<input name="initPassYn" type="hidden" value="N"/>
			<input name="roleId" type="hidden">
			<input name="uid" type="hidden">
			<div class="panel panel-info" >	
				<div class="panel-heading"><b>사용자 관리</b></div>
				<div class="panel-body" style="padding:5px 5px 5px 5px;">
					<div class="w2ui-field">
						<label style="width:20%;text-align: left;padding-left: 20px;">아이디</label>
						<div >
							<input name="userId" type="text" maxlength="100" style="width: 280px" required="required" placeholder="아이디를 입력 하세요."/>
							<div class="isMessage"></div>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="width:20%;text-align: left;padding-left: 20px;">비밀번호</label>
						<div >
							<input name="userPassword" type="password" maxlength="100" style="width: 280px" required="required" placeholder="비밀번호를 입력 하세요."/>
							<div class="isMessage"></div>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="width:20%;text-align: left;padding-left: 20px;">이름</label>
						<div >
							<input name="userName" type="text" maxlength="100" style="width: 280px" required="required" placeholder="이름을 입력 하세요."/>
							<div class="isMessage"></div>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="width:20%;text-align: left;padding-left: 20px;">Email</label>
						<div >
							<input name="email" type="text" maxlength="100" style="width: 280px" required="required" placeholder="이메일을 입력 하세요."/>
							<div class="isMessage"></div>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="width:100%;text-align: left;padding-left: 20px;">권한</label>
						<div  style="width:85%; float:left; height:44px;  position:relative; margin:0px 0px 0px 40px;  ">
							<table class="table table-striped" >
								<tr class="info" style="line-height: 25px;">
									<th width="12%" style="background-color: #d9d9da; border-right:1px solid #9e9e9e;background: linear-gradient(to bottom,#f1f1f1 0,#c8c8c8 100%); box-shadow: 3px 2px 10px #c1c1c1;" >비고</th>
									<th width="41%" style="background-color: #d9d9da; border-right:1px solid #9e9e9e;background: linear-gradient(to bottom,#f1f1f1 0,#c8c8c8 100%); box-shadow: 3px 2px 10px #c1c1c1;">권한</th>
									<th style="background-color: #d9d9da; background: linear-gradient(to bottom,#f1f1f1 0,#c8c8c8 100%);box-shadow: 3px 2px 10px #c1c1c1;">권한 설명</th>
								</tr>
							</table>
						</div>
						<div class= "writeWarrper" style="width:84%; float:left; margin:-25px 10px 10px 41px; overflow:auto;  overflow-x:hidden; height:200px;" ></div>
					</div>
				</div>
			</div>
		</form>	
	</div>
	<div id="regPopupBtnDiv" hidden="true">
		<button class="btn" id="registBtn" onclick="registUser();">확인</button>
		<button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
	</div>

	<div id="updatePopupBtnDiv" hidden="true">
		<button class="btn" id="updateBtn" onclick="updateUser();">확인</button>
		<button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
	</div>
	
</div>