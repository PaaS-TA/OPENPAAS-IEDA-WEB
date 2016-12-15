<%
/* =================================================================
 * 작성일 : 2016.07
 * 작성자 : 이동현
 * 상세설명 : 권한 관리 화면
 * =================================================================
 * 수정일         작성자             내용     
 * -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * 2016.12      이동현         권한 관리 화면 버그 수정
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script>
	var parentCode;
	var arrAuthCode = [];
	$(function() {	
	   	 /********************************************************
		  * 설명		: 권한 그룹 목록 Grid
		 *********************************************************/
		$('#auth_GroupGrid').w2grid({
			name: 'auth_GroupGrid',
			header: '<b>권한 그룹 목록</b>',
			method: 'GET',
			multiSelect: false,
			show: {	
					selectColumn: true,
					footer: true},
			style: 'text-align: center',
			columns:[
				  { field: 'recid', hidden: true },
		          { field: 'roleName', caption: '권한그룹명', size:'35%', style:'text-align:center;' },
		          { field: 'roleDescription', caption: '설명', size:'65%', style:'text-align:center;'},
		          { field: 'authCode', hidden: true }
		      	],
		    onSelect : function(event) {
				event.onComplete = function() {
					$('#modifyBtn').attr('disabled', false);
					$('#deleteBtn').attr('disabled', false);
					$('#addAuthBtn').attr('disabled', false);
					$('#modifyAuthBtn').attr('disabled', false);
					$('#deleteAuthBtn').attr('disabled', false);
					doSearchByIdx(event.recid);
					return;
				}
			},
			onUnselect : function(event) {
				event.onComplete = function() {
					w2ui['auth_sub_Grid'].clear();
					$('#modifyBtn').attr('disabled', true);
					$('#deleteBtn').attr('disabled', true);
					$('#addAuthBtn').attr('disabled', true);
					$('#modifyAuthBtn').attr('disabled', true);
					$('#deleteAuthBtn').attr('disabled', true);
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
		  * 설명		: 권한 그룹 하위 코드 Grid
		 *********************************************************/
		$('#auth_sub_Grid').w2grid({
			name	: 'auth_sub_Grid',
			show	: {	selectColumn: false,
						footer: true
					  },
			multiSelect: false,
			method 	: "GET",
			style	: 'text-align:center',
			columns	: [{ field: 'RECID', hidden: true },
		       	 	  { field: 'roleName', caption: '권한명', size:'25%', style:'text-align:left;'},
		       	 	  { field: 'roleDescription', caption: '설명', size:'35%', style:'text-align:left;'},
		       	 	  { field: 'authCode', caption: '사용 여부', size:'25%', style:'text-align:center;',
		       	 	 	render: function(record){ 
		       	 	 		if(record.authCode != null) return '허용';
		           		 	else return '거부';       		  
		           	   	}
		    		   }],
		   	onLoad: function(event) {
		   		arrAuthCode = [];
				var jsondata = JSON.parse(event.xhr.responseText);
				var count = 0;
				if(jsondata.records.length==0) {
					w2alert("권한 관련 코드 목록을 확인해 주세요.", "상세 권한 조회");
					$('#modifyAuthBtn').attr('disabled', true);
					return;
				}
					for(var i=0; i<jsondata.records.length; i++){
						if(jsondata.records[i].authCode != null || jsondata.records[i].authCode != undefined){
							arrAuthCode.push(jsondata.records[i].authCode);
						}						
					}		
				}
		});
		
	   	 /********************************************************
		  * 설명		: 권한 그룹 등록 버튼 클릭
		 *********************************************************/
		$("#addBtn").click(function(){
			$(".w2ui-msg-body input[name='parentCode']").val("");
			w2popup.open({
				title 	: "<b>권한 그룹 등록</b>",
				width 	: 600,
				height	: 270,
				modal	: true,
				body	: $("#regPopupDiv").html(),
				buttons : $("#regPopupBtnDiv").html(),
				onClose : function(event){
					w2ui['auth_GroupGrid'].clear();
					doSearch();
					w2ui['auth_sub_Grid'].clear();
				}
			});
			/* authInfoList('regist'); */
		});
		
	   	 /********************************************************
		  * 설명		: 권한 그룹 수정 버튼 클릭
		 *********************************************************/
		$("#modifyBtn").click(function(){
			if($("#modifyBtn").attr('disabled') == "disabled") return;	
			var selected = w2ui['auth_GroupGrid'].getSelection();
			if( selected.length == 0 ){
				w2alert("선택된 정보가 없습니다.", "권한 그룹 수정");
				return;
			}
			var record = w2ui['auth_GroupGrid'].get(selected);
			w2confirm({
				title		: "권한 그룹 수정",
				msg			: "권한 그룹(" + record.roleName + ")을 수정하시겠습니까?",
				yes_text	: "확인",
				no_text		: "취소",
				yes_callBack: function(event){
					// 권한 그룹 수정
					updatePopup(w2ui['auth_GroupGrid'].get(selected));;
				},
				no_callBack	: function(){
					w2ui['auth_GroupGrid'].clear();
					doSearch();
					w2ui['auth_sub_Grid'].clear();
					console.log("user clicked NO");
				}
			});
		});
		
	   	 /********************************************************
		  * 설명		: 권한 그룹 삭제 버튼 클릭
		 *********************************************************/
		$("#deleteBtn").click(function(){
			if($("#deleteBtn").attr('disabled') == "disabled") return;
			var selected = w2ui['auth_GroupGrid'].getSelection();		
			if( selected.length == 0 ){
				w2alert("선택된 정보가 없습니다.", "코드 그룹 삭제");
				return;
			}
			else {
				var record = w2ui['auth_GroupGrid'].get(selected);
				w2confirm({
					title		: "권한 그룹 삭제",
					msg			: "권한 그룹(" + record.roleName + ")을 삭제하시겠습니까?",
					yes_text	: "확인",
					no_text		: "취소",
					yes_callBack: function(event){
						// 코드 삭제
						deleteAuth(record.roleId);
					},
					no_callBack	: function(){
						w2ui['auth_GroupGrid'].clear();
						doSearch();
						w2ui['auth_sub_Grid'].clear();
						console.log("user clicked NO");
					}
				});
			}
		});
		doSearch();
	});
	
  	 /********************************************************
	  * 설명		: 권한 등록 버튼 
	 *********************************************************/
	$("#addAuthBtn").click(function(){
		if($("#addAuthBtn").attr('disabled') == "disabled") return;
		var selected = w2ui['auth_GroupGrid'].getSelection();
		var record = w2ui['auth_GroupGrid'].get(selected);
		w2popup.open({
			title 	: "<b>권한 등록</b>",
			width 	: 550,
			height	: 600,
			modal	: true,
			body	: $("#regAuthPopupDiv").html(),
			buttons : $("#regAuthPopupBtnDiv").html(),
			onOpen : function(event){
				event.onComplete = function(){
					$(".w2ui-msg-body input[name='parentAuthName']").val(record.roleName);	
					$(".w2ui-msg-body input[name='parentAuthRoleId']").val(record.recid);	
					authInfoList('regist');
				}					
			},onClose: function(event){
			}
		});
	});
	
 	 /********************************************************
	  * 설명		: 권한 수정 버튼 
	 *********************************************************/
	$("#modifyAuthBtn").click(function(){
		if($("#modifyAuthBtn").attr('disabled') == "disabled") return;
		
		var selected = w2ui['auth_GroupGrid'].getSelection();
		
		if( selected.length == 0 ){
			w2alert("선택된 정보가 없습니다.", "코드 수정");
			return;
		}
		
		updateAuthPopup(w2ui['auth_GroupGrid'].get(selected));
	});
	
	

	/***********************************************************************
 	* 				Start Main View Event Function
	********************************************************************* */
	
	/********************************************************
	 * 설명		: 권한 그룹 조회 Function 
	 * Function	: doSearch
	 *********************************************************/
	function doSearch() {
		w2ui['auth_GroupGrid'].load('/admin/role/group/list');
		doButtonStyle(); 
	}
	
	/********************************************************
	 * 설명		: 초기 버튼 스타일
	 * Function	: doButtonStyle
	 *********************************************************/
	function doButtonStyle() {
		$('#modifyBtn').attr('disabled', true);
		$('#deleteBtn').attr('disabled', true);
		
		$('#addAuthBtn').attr('disabled', true);
		$('#modifyAuthBtn').attr('disabled', true);
		$('#deleteAuthBtn').attr('disabled', true);
	}
	
	/********************************************************
	 * 설명		: 권한 그룹 하부 코드 조회 Function
	 * Function	: doSearchByIdx
	 *********************************************************/
	function doSearchByIdx(codeIdx) {
	 	if(codeIdx == undefined || codeIdx == 'undefined' || codeIdx == -1) {
			return;
		} 
		parentCode = codeIdx;
		w2ui['auth_sub_Grid'].load('/admin/role/group/' + codeIdx);
	}
	
	/********************************************************
	 * 설명		: 팝업 메세지 Lock Function
	 * Function	: lock
	 *********************************************************/
	function lock (msg) {
	    w2popup.lock(msg, true);
	}
	
	/********************************************************
	 * 설명		: 화면 리사이즈시 호출
	 *********************************************************/
	$( window ).resize(function() {
		setLayoutContainerHeight();
	});
	
	/********************************************************
	 * 설명		: 다른 페이지 이동 시 호출 Function
	 * Function	: clearMainPage
	 *********************************************************/
	function clearMainPage() {
		$().w2destroy('auth_GroupGrid');
		$().w2destroy('auth_sub_Grid');
	}
	
	/********************************************************
	 * 설명		: 등록 버튼 클릭 시 권한 코드 조회 Function
	 * Function	: authInfoList
	 *********************************************************/
	var length = 0;
	function authInfoList(type){
		$.ajax({
			type : "GET",
			url : "/admin/role/commonCodeList",
			contentType : "application/js10pxon",
			success : function(data, status) {
				if ( data == null || data == "" ){
					//isOk = false;
				} else {	
					length = data.length;
					var result="";
					result += '<table style="width:100%;" class="table table-hover">';
					for(var i=0;i<data.length;i++){						
						if(data[i].subGroupCode==null){						
							result += '<tr class="active" style="border-bottom:1.5px solid white;"><td callsapn="2" ><label>'+data[i].codeNameKR+'</label></td>';
							result += '<td style="width:17%"><input onchange="changeValue('+data[i].codeValue+',this.value);" type="radio" name='+data[i].codeValue+''
							+' value="Y" ></td>'					
							+'<td style="width:10%"><input checked="checked" onchange="changeValue('+data[i].codeValue+',this.value);"   type="radio" name='+data[i].codeValue+''
							+' value="N" ></td></tr>';
						}
						if(data[i].subGroupCode!=null){
							result +='<tr><td class="'+data[i].subGroupCode+'">&nbsp&nbsp&nbsp'+ data[i].codeNameKR+'</td>'
							+'<td class='+data[i].subGroupCode+'><input value="Y" class="disabledOn" disabled="false" type="radio" name='+data[i].codeValue+''
							+' ></td>'					
							+'<td><input  class='+data[i].subGroupCode+' checked="checked"  value="N" type="radio" name='+data[i].codeValue+''
							+' ></td></tr>';
							}	
						}
					}
					result += '</table>'
					$('.writeWarrper').html(''+result+'');				
					if(type=='update'){				 
	 					 for(var i=0;i<arrAuthCode.length;i++){
	 						$('.'+arrAuthCode[i]+' input.disabledOn').removeAttr('disabled');
							 $('.w2ui-msg-body input:radio[name='+arrAuthCode[i]+']:input[value=Y]').attr("checked",true);				
						 }	 
					}
					//초기 권한이 모두 거부일 시 defaultYn() 호출하여 기본 시스템 조회, 사용자 권한의 defalut값을 Y로 변경
					if(arrAuthCode.length==0){
						defaultYn();
					}
			},
			error : function(request, status, error) {
				var errorResult = JSON.parse(request.responseText);
			}
		});
		
	}
	
	/********************************************************
	 * 설명		: 상세 권한 등록 시 defalut Y 값
	 * Function	: defaultYn
	 *********************************************************/
	function defaultYn(){
		//탑(테스트)
		$('.w2ui-msg-body :radio[name="10101"]:input[value="Y"]').attr("checked",true);
		//메뉴(테스트)
		$('.w2ui-msg-body :radio[name="10102"]:input[value="Y"]').attr("checked",true);
		//로그인(테스트)
		$('.w2ui-msg-body :radio[name="10103"]:input[value="Y"]').attr("checked",true);
		//메인(테스트)
		$('.w2ui-msg-body :radio[name="10104"]:input[value="Y"]').attr("checked",true);
		//init(테스트)
		$('.w2ui-msg-body :radio[name="10600"]:input[value="Y"]').attr("checked",true);
		//DashBoard
		$('.w2ui-msg-body :radio[name="10100"]:input[value="Y"]').attr("checked",true);
		//기본 시스템 사용자 권한
		$('.w2ui-msg-body :radio[name="19000"]:input[value="Y"]').attr("checked",true);
		//기본 시스템 조회 권한
		$('.w2ui-msg-body :radio[name="19100"]:input[value="Y"]').attr("checked",true);
	}
	
	
	/********************************************************
	 * 설명		: 권한 그룹 하부 코드 조회 시 허용/거부 관련 ChangeEvent Function
	 * Function	: changeValue
	 *********************************************************/
	function changeValue(codeld,val){		
		if(val=="Y"){
			 $('.'+codeld+' input.disabledOn').removeAttr('disabled');		
		}else{
			$('input.'+codeld+'').prop('checked',true);
			$('.'+codeld+' input.disabledOn').attr('disabled','false');
		}	
	};

	/***********************************************************************
	 * 							권한 그룹 등록
	********************************************************************* */
	
	/********************************************************
	 * 설명		: 권한 그룹 등록 Function
	 * Function	: registAuthGroup
	 *********************************************************/
	function registAuthGroup(){
	 	lock( '등록 중입니다.', true); 
		var flag = false;
		authInfo = {
				roleName : $(".w2ui-msg-body input[name='roleName']").val(),			
				roleDescription : $(".w2ui-msg-body textarea[name='roleDescription']").val()
		}
		if(popupValidation()){
			$.ajax({
				type : "POST",
				url : "/admin/role/group/add",
				contentType : "application/json",
				async : true,		
				data : JSON.stringify(authInfo),
				success : function(status) {
					// ajax가 성공할때 처리...
					w2popup.unlock();
					w2popup.close();
					w2ui['auth_GroupGrid'].clear();
					doSearch();
					w2ui['auth_sub_Grid'].clear();				
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
	
	/***********************************************************************
	 * 							권한 그룹 삭제
	********************************************************************* */
	
	/********************************************************
	 * 설명		: 권한 그룹 삭제 Function
	 * Function	: deleteAuth
	 *********************************************************/
	function deleteAuth(roleId){
		$.ajax({
			type : "DELETE",
			url : "/admin/role/group/delete/"+ roleId,
			contentType : "application/json",
			success : function(status) {
				w2popup.unlock();
				w2popup.close();
				w2ui['auth_GroupGrid'].clear();
				doSearch();
				w2ui['auth_sub_Grid'].clear();
			},
			error : function(request, status, error) {
				var errorResult = JSON.parse(request.responseText);
				w2alert(errorResult.message, "삭제 실패");
			}
		});
	}

	/***********************************************************************
	 * 							권한 그룹 수정
	********************************************************************* */
	/********************************************************
	 * 설명		: 권한 그룹 수정 Function
	 * Function	: updateAuthGroup
	 *********************************************************/
	function updateAuthGroup() {
		var flag = false;
		authInfo = {
				roleName : $(".w2ui-msg-body input[name='roleName']").val(),			
				roleDescription : $(".w2ui-msg-body textarea[name='roleDescription']").val(),
				
		}
		if(popupValidation()){
			$.ajax({
				type : "PUT",
				url : "/admin/role/group/update/" + $(".w2ui-msg-body input[name='roleIdx']").val(),
				contentType : "application/json",
				//dataType: "json",
				async : true,		
				data : JSON.stringify(authInfo),
				success : function(status) {
					lock( '수정 중입니다.', true);		
					w2popup.unlock();
					w2popup.close();
					w2ui['auth_GroupGrid'].clear();
					doSearch();				
					w2ui['auth_sub_Grid'].clear();
					arrAuthCode = [];
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
	 * 설명		: 권한 그룹 수정 팝업 Function
	 * Function	: updatePopup
	 *********************************************************/
	function updatePopup(record) {
			
	 			w2popup.open({
					title 	: "<b>권한 그룹 수정</b>",
					width 	: 600,
					height	: 270,
					modal	: true,
					body	: $("#regPopupDiv").html(),
					buttons : $("#updatePopupBtnDiv").html(),
					onOpen : function(event){
						event.onComplete = function(){
							$(".w2ui-msg-body input[name='roleIdx']").val(record.recid);
							$(".w2ui-msg-body input[name='roleName']").val(record.roleName);
							$(".w2ui-msg-body textarea[name='roleDescription']").val(record.roleDescription);
							/* authInfoList("update",record); */
						}
					},
					onClose : function(event){
						w2ui['auth_GroupGrid'].clear();
						doSearch();
						w2ui['auth_sub_Grid'].clear();
					}
				});
	}
	
	/***********************************************************************
	 * 							권한 코드 수정
	********************************************************************* */
	
	/********************************************************
	 * 설명		: 권한 그룹 수정 Function
	 * Function	: updateAuth
	 *********************************************************/
	function updateAuth() {
		var activeYnArray = new Array();
		var flag = false;
		for(var i=0; i < length * 2; i++){
			var radioName = $("input:radio")[i].name;
			if($('.w2ui-msg-body :radio[name='+radioName+']:checked').val() != 'N' && !flag){
				activeYnArray.push(radioName);
			}
			flag = !flag;
		}
		authInfo = {			
				activeYn : activeYnArray
		}
		
		if(popupValidation()){
			$.ajax({
				type : "POST",
				url : "/admin/role/detail/update/" + $(".w2ui-msg-body input[name='parentAuthRoleId']").val(),
				contentType : "application/json",
				//dataType: "json",
				async : true,		
				data : JSON.stringify(authInfo),
				success : function(status) {
					w2confirm({
						title 	: "상세 권한 등록",
						msg		: "상세 권한 정보를 등록하시겠습니까?",
						yes_text: "확인",
						yes_callBack : function(envent){
					lock( '등록 중입니다.', true);		
					w2popup.unlock();
					w2popup.close();
					w2ui['auth_GroupGrid'].clear();
					doSearch();		
					w2ui['auth_sub_Grid'].clear();
					doSearchByIdx($(".w2ui-msg-body input[name='parentAuthRoleId']").val());
					},
						no_text : "취소",
						no_callBack : function(event){
							arrAuthCode = [];
						}
					});
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
	 * 설명		: 상세 권한 등록
	 * Function	: updateAuthPopup
	 *********************************************************/
	function updateAuthPopup(record) {
				w2popup.open({
					title 	: "<b>상세 권한 등록</b>",
					width 	: 550,
					height	: 600,
					modal	: true,
					body	: $("#regAuthPopupDiv").html(),
					buttons : $("#updateAuthPopupBtnDiv").html(),
					onOpen : function(event){
						event.onComplete = function(){
							$(".w2ui-msg-body input[name='parentAuthRoleId']").val(record.roleId);
							$(".w2ui-msg-body input[name='parentAuthName']").val(record.roleName);
							authInfoList('update',record);
						}
					},
					onClose : function(event){
						doSearchByIdx(record.roleId);
					}
				});
	}
	
	
</script> 

<style>
 	.w2ui-popup .w2ui-msg-body { overflow:initial; }
</style>
<div id="main">
	<div class="page_site">플랫폼 설치 자동화 관리 > <strong>권한 관리</strong></div>
	
	<!-- 권한 그룹 목록-->
	<div class="pdt20">
		<div class="title fl">권한 그룹 목록</div>
		<div class="fr"> 
			<span id="addBtn" class="btn btn-primary" style="width:120px">등록</span>
			<span id="modifyBtn" class="btn btn-info" style="width:120px">수정</span>
			<span id="deleteBtn" class="btn btn-danger" style="width:120px">삭제</span>
	    </div>
	</div>
	<div id="auth_GroupGrid" style="width:100%; height:250px"></div>
	
	<!-- 권한 목록-->
	<div class="pdt20">
		<div class="title fl">권한 목록</div>
		<div class="fr"> 
			<span id="modifyAuthBtn" class="btn btn-primary" style="width:120px">등록</span>
	    </div>
		
	</div>	
	<div id="auth_sub_Grid" style="width:100%; height:450px"></div>
	
	<!-- 코드 그룹 추가/수정 팝업 -->
	<div id="regPopupDiv" hidden="true">
		<form id="settingForm" action="POST" style="padding:2%; margin:0;">
			<input name="roleIdx" type="hidden"/>
			<div class="panel panel-info" >	
				<div class="panel-heading"><b>권한 그룹 정보</b></div>
				<div class="panel-body" style="padding:5px 5px 5px 5px;">
					<div class="w2ui-field">
						<label style="width:100%%;text-align: left;padding-left: 20px;">권한 그룹명</label>
						<div >
							<input name="roleName" type="text" maxlength="100" style="width: 365px" required="required" />
							<div class="isMessage"></div>
						</div>
					</div>

					<div class="w2ui-field">
						<label style="width:100%%;text-align: left;padding-left: 20px;">설명</label>
						<div style="width: 100%;">
							<textarea name="roleDescription" style="float: left; width: 365px; height: 60px; margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;" required ></textarea>
						</div>
					</div>
				</div>
			</div>
		</form>	
	</div>
	<div id="regPopupBtnDiv" hidden="true">
		<button class="btn" id="registBtn" onclick="registAuthGroup();">확인</button>
		<button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
	</div>

	<div id="updatePopupBtnDiv" hidden="true">
		<button class="btn" id="updateBtn" onclick="updateAuthGroup();">확인</button>
		<button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
	</div>
	
	
	<!-- 권한 추가/수정 팝업 -->
	<div id="regAuthPopupDiv"  hidden="true">
		<form id="settingForm" action="POST" style="padding:5px 0 5px 0;margin:0;">
			<input name="codeIdx" type="hidden"/>
			<div class="panel panel-info" >	
				<div class="panel-heading"><b>권한 정보</b></div>
				<div class="panel-body" style="padding:5px 5% 10px 5%;height:500px;">
					<div class="w2ui-field">
						<label style="width:30%;text-align: left;padding-left: 20px;">권한 그룹명</label>
						<div style="width: 70%;">
							<input name="parentAuthName" type="text" maxlength="100" style="width: 250px" readonly />
							<input type="hidden" name="parentAuthRoleId">
						</div>
					</div>										
 					<div class="w2ui-field">
						<label style="width:88%;text-align: left;padding-left: 20px;">권한 설정</label>
						<div  style="width:90%; height:34px; float:left; position:relative; margin:0px 10px 5px 20px; top:10px; ">
							<table class="table table-striped"; style="height: 20px;" >
								<tr class="info" style="line-height: 25px;">
									<th width="67%" style="background-color: #d9d9da; background: linear-gradient(to bottom,#f1f1f1 0,#c8c8c8 100%); font-size:13px; border-right:1px solid #9e9e9e; box-shadow: 3px 2px 10px #c1c1c1;" >권한 명</th>
									<th style="background-color: #d9d9da; background: linear-gradient(to bottom,#f1f1f1 0,#c8c8c8 100%);font-size:13px;border-right:1px solid #9e9e9e; text-align: center; box-shadow: 3px 2px 10px #c1c1c1;">허용</th>
									<th style="background-color: #d9d9da; background: linear-gradient(to bottom,#f1f1f1 0,#c8c8c8 100%);font-size:13px; box-shadow: 3px 2px 10px #c1c1c1;  text-align: center;">거부</th>
								</tr>
							</table>
						</div>
						<div class= "writeWarrper" style="width:90%; float:left; margin:10px 10px 10px 20px; overflow-y:scroll; height:320px;" ></div>
					</div>
				</div>
			</div>
		</form>	
	</div>
	<div id="updateAuthPopupBtnDiv" hidden="true">
		<button class="btn" id="updateAuthBtn" onclick="updateAuth();">확인</button>
		<button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
	</div>
	
	
	
	
</div>