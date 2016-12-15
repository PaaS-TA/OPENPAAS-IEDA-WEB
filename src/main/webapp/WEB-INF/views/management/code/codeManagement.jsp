<%
/* =================================================================
 * 작성일 : 2016.07
 * 작성자 : 황보유정
 * 상세설명 : 코드 관리 화면
 * =================================================================
 * 수정일         작성자             내용     
 * -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * 2016.12      이동현         코드 관리 화면 버그 수정
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript">
var code = "";
$(function(){
	
	/********************************************************
	 * 설명		: 코드 그룹 목록 grid 설정
	 *********************************************************/
	$('#us_codeGroupGrid').w2grid({
		name: 'us_codeGroupGrid',
		header: '<b>코드 그룹 목록</b>',
		method: 'GET',
			multiSelect: false,
		show: {	
				selectColumn: true,
				footer: true},
		style: 'text-align: center',
		columns:[
			  { field: 'recid', hidden: true },
	          { field: 'codeName', caption: '코드그룹명', size:'25%', style:'text-align:center;' },
	          { field: 'codeValue', caption: '코드 그룹값', size:'25%', style:'text-align:center;' },
	          { field: 'codeDescription', caption: '설명', size:'35%', style:'text-align:center;'}
	      	],
	      	onSelect : function(event) {
				event.onComplete = function() {
					$('#modifyBtn').attr('disabled', false);
					$('#deleteBtn').attr('disabled', false);
					$('#addCodeBtn').attr('disabled', false);
					var name =  w2ui.us_codeGroupGrid.get(event.recid).codeName;
			        var codeValue = w2ui.us_codeGroupGrid.get(event.recid).codeValue;
			        doSearchByIdx(codeValue, name);
					return;
				}
			},
			onUnselect : function(event) {
				event.onComplete = function() {
					w2ui['us_codeGrid'].clear();
					$('#addCodeBtn').attr('disabled', true);
					$('#modifyBtn').attr('disabled', true);
					$('#deleteBtn').attr('disabled', true);
					$('#modifyCodeBtn').attr('disabled', true);
					$('#deleteCodeBtn').attr('disabled', true);
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
	 * 설명		: 코드 목록 Grid 설정
	 *********************************************************/
	$('#us_codeGrid').w2grid({
		name	: 'us_codeGrid',
		show	: {	
					selectColumn: true,
					footer: true
					},
		multiSelect: false,
		method 	: "GET",
		style	: 'text-align:center',
		columns	:[
				  { field: 'recid', hidden: true },
				  { field: 'parentCode', hidden: true },
	              { field: 'codeNameKR', caption: '코드명', size:'25%', style:'text-align:left;' },
	              { field: 'codeValue', caption: '코드값', size:'25%', style:'text-align:center;' },
	              { field: 'codeDescription', caption: '설명', size:'35%', style:'text-align:left;'},
	              { field: 'subGroupCode', caption: '서브 그룹', size:'25%', style:'text-align:center;' }
	          	],
	          	onSelect : function(event) {
	    			event.onComplete = function() {
	    				$("#addCodeBtn").attr('disabled', false);
	    				$('#modifyCodeBtn').attr('disabled', false);
	    				$('#deleteCodeBtn').attr('disabled', false);
	    				return;
	    			}
	    		},
	    		onUnselect : function(event) {
	    			event.onComplete = function() {
	    				$('#modifyCodeBtn').attr('disabled', true);
	    				$('#deleteCodeBtn').attr('disabled', true);
	    				return;
	    		}
	    	}
	});
	
	/*************************************************************** 
	 *				 	코드 그룹 목록 버튼 기능
	****************************************************************/
	
	/********************************************************
	 * 설명		: 코드 그룹 등록 버튼
	 *********************************************************/
	$("#addBtn").click(function(){
	// 	parentCode = -1;
		$(".w2ui-msg-body input[name='parentCode']").val("");
		w2popup.open({
			title 	: "<b>코드 그룹 등록</b>",
			width 	: 550,
			height	: 380,
			modal	: true,
			body	: $("#regPopupDiv").html(),
			buttons : $("#regPopupBtnDiv").html()
			,onClose:function(event){
				w2ui['us_codeGroupGrid'].clear();
				doSearch();
				w2ui['us_codeGrid'].clear();
			}
		});
		
	});
	
	/********************************************************
	 * 설명		: 코드 그룹 수정 버튼
	 *********************************************************/
	$("#modifyBtn").click(function(){
		if($("#modifyBtn").attr('disabled') == "disabled") return;
		var selected = w2ui['us_codeGroupGrid'].getSelection();
		var recode = w2ui['us_codeGroupGrid'].get(selected);
		if( selected.length == 0 ){
			w2alert("선택된 정보가 없습니다.", "코드 그룹 수정");
			return;
		}
		w2confirm({
			title		: "코드 그룹 수정",
			msg			: "코드 그룹(" + recode.codeName + ")을 수정하시겠습니까?",
			yes_text	: "확인",
			no_text		: "취소",
			yes_callBack: function(event){
				updatePopup(w2ui['us_codeGroupGrid'].get(selected));
			},
			no_callBack	: function(){
				w2ui['us_codeGroupGrid'].clear();
				doSearch();
				w2ui['us_codeGrid'].clear();
			}
		});
	});
	
	/********************************************************
	 * 설명		: 코드 그룹 삭제 버튼
	 *********************************************************/
	$("#deleteBtn").click(function(){
		if($("#deleteBtn").attr('disabled') == "disabled") return;

		var selected = w2ui['us_codeGroupGrid'].getSelection();
		
		if( selected.length == 0 ){
			w2alert("선택된 정보가 없습니다.", "코드 그룹 삭제");
			return;
		}else {
			var record = w2ui['us_codeGroupGrid'].get(selected);

			w2confirm({
				title		: "코드 그룹 삭제",
				msg			: "코드 그룹(" + record.codeName + ")을 삭제하시겠습니까?",
				yes_text	: "확인",
				no_text		: "취소",
				yes_callBack: function(event){
					deleteCode(record.codeIdx);// 코드 삭제
				},
				no_callBack	: function(){
					w2ui['us_codeGroupGrid'].clear();
					doSearch();
					w2ui['us_codeGrid'].clear();
				}
			});
		}
	});
	
	/*************************************************************** 
	 *				 	코드 목록 버튼 기능
	****************************************************************/
	/********************************************************
	 * 설명		: 코드 등록 버튼
	 *********************************************************/
	$("#addCodeBtn").click(function(){
		if($("#addCodeBtn").attr('disabled') == "disabled") return;
		var selected = w2ui['us_codeGroupGrid'].getSelection();
		var record = w2ui['us_codeGroupGrid'].get(selected);
		w2popup.open({
			title 	: "<b>코드 등록</b>",
			width 	: 550,
			height	: 420,
			modal	: true,
			body	: $("#regCodePopupDiv").html(),
			buttons : $("#regCodePopupBtnDiv").html(),
			onOpen : function(event){
				event.onComplete = function(){
					$(".w2ui-msg-body input[name='parentCodeVal']").val(record.codeValue);
					$(".w2ui-msg-body input[name='parentCodeName']").val(record.codeName);
					lock("하위 그룹 목록을 조회 중입니다.", true);
					getSubList(record.codeValue);
				}					
			},onClose:function(event){
				doSearchByIdx(record.codeValue);
			}
		});
	});
	
	/********************************************************
	 * 설명		: 코드 수정 버튼
	 *********************************************************/
	$("#modifyCodeBtn").click(function(){
		if($("#modifyCodeBtn").attr('disabled') == "disabled") return;
		
		var selected = w2ui['us_codeGrid'].getSelection();
		
		if( selected.length == 0 ){
			w2alert("선택된 정보가 없습니다.", "코드 수정");
			return;
		}
		
		updateCodePopup(w2ui['us_codeGrid'].get(selected));
	});

	/********************************************************
	 * 설명		: 코드 삭제 버튼
	 *********************************************************/
	$("#deleteCodeBtn").click(function(){
		if($("#deleteCodeBtn").attr('disabled') == "disabled") return;
		var selected = w2ui['us_codeGrid'].getSelection();
		
		if( selected.length == 0 ){
			w2alert("선택된 정보가 없습니다.", "코드 삭제");
			return;
		}else {
			var record = w2ui['us_codeGrid'].get(selected);
		 	w2confirm({
				title		: "코드 삭제",
				msg			: "코드 (" + record.codeName + ")을 삭제하시겠습니까?",
				yes_text	: "확인",
				no_text		: "취소",
				yes_callBack: function(event){
					deleteCode(record.codeIdx);// 코드 삭제
				},
				no_callBack	: function(){
					doSearchByIdx(record.parentCode)
				}
			});
		}
	});
	
	doSearch();
	
});


/*************************************************************** 
 *				 			조회 기능
****************************************************************/
/********************************************************
 * 설명		: 코드 삭제 버튼
 * Function	: doSearch
 *********************************************************/
function doSearch() {	
	w2ui['us_codeGroupGrid'].load('/admin/code/groupList',
	function() {
		doButtonStyle();
	});
}

/********************************************************
 * 설명		: 코드 목록 조회
 * Function	: doSearchByIdx
 *********************************************************/
function doSearchByIdx(parentVal) {
	if(parentVal == undefined || parentVal == 'undefined' || parentVal == -1) {
		return;
	}
	w2ui['us_codeGrid'].load('/admin/code/codeList/' + parentVal); //코드 목록 조회
}

/********************************************************
 * 설명		: 그리드 재조회
 * Function	: gridReload
 *********************************************************/
function gridReload() {
	w2ui['us_codeGroupGrid'].clear();
	w2ui['us_codeGrid'].clear();
	doSearch();
}

/********************************************************
 * 설명		: 하위 그룹 목록 조회(코드 목록 - 등록 화면)
 * Function	: getSubList
 *********************************************************/
var subGroupList= {};
function getSubList(parentCode, selectCodeVal) {
	$.ajax({
		type : "GET",
		url : "/common/deploy/codes/parent/" + parentCode,
		contentType : "application/json",
		//dataType: "json",
		async : true,
		data : "{}",
		success : function(data, status) {
			w2popup.unlock();
			subGroupList = {};
			for(var i=0; i < data.length; i++) {
				subGroupList[data[i].codeValue] = data[i].codeName; //codeValue
			}
			var subGroupDiv = $(".w2ui-msg-body #subGroupCodeDiv");
			var selectInput = '<select name="subGroupList" id="subGroupList"  onchange="setSubGroupCode(this.value);"  style="float:left;width:250px;"></select>';
			subGroupDiv.html(selectInput);
			var option = "";
			if( data.length == 0 ){ 
				$("#subGroupList").append($('<option>', {value: ''}).text("선택안함")); 
			}else{
				$("#subGroupList").append($('<option>', {value: ''}).text("선택안함")); 
				$.each(subGroupList, function(key, value){
					$("#subGroupList").append($('<option>', {value: key}).text(value));
				});
				if($(".w2ui-msg-body input[name='subGroupCode']").val() != ""){
					$(".w2ui-msg-body select[name='subGroupList']").val(selectCodeVal);
				}
			}

		},
		error : function(request, status, error) {
			var errorResult = JSON.parse(request.responseText);
			//w2alert(errorResult.message);
		}
	});
}

/********************************************************
 * 설명		: 하위 그룹 목록 선택 값
 * Function	: setSubGroupCode
 *********************************************************/
function setSubGroupCode(value){
	$(".w2ui-msg-body input[name=subGroupCode]").val(value);
}



/*************************************************************** 
 *				 		코드 그룹 기능
****************************************************************/
/********************************************************
 * 설명		: 코드 그룹 등록 확인 버튼
 * Function	: registCodeGroup
 *********************************************************/
function registCodeGroup(type, codeType){
	codeGroup = {
			codeIdx						: $(".w2ui-msg-body input[name='codeGroupIdx']").val(),
			codeName 				: $(".w2ui-msg-body input[name='codeName']").val(),
			codeValue 				: $(".w2ui-msg-body input[name='codeValue']").val(),
			codeDescription 		: $(".w2ui-msg-body textarea[name='codeDescription']").val(),
	}
	if(type == 'regist'){
		regist(codeGroup, codeType);
	}else{
		update(codeGroup, codeType);
	}
	
}

/********************************************************
 * 설명		: 코드 등록 확인 버튼
 * Function	: registCode
 *********************************************************/
function registCode(type, codeType){
	code = {
			codeIdx			: $(".w2ui-msg-body input[name='codeIdx']").val(),
			parentCode		: $(".w2ui-msg-body input[name='parentCodeVal']").val(),
			subGroupCode	: $(".w2ui-msg-body input[name='subGroupCode']").val() == "" ? 'y' : $(".w2ui-msg-body input[name='subGroupCode']").val(),
			codeName 		: $(".w2ui-msg-body input[name='subCodeName']").val(),
			codeNameKR		: $(".w2ui-msg-body input[name='subCodeNameKR']").val(),
			codeValue 		: $(".w2ui-msg-body input[name='subCodeValue']").val(),
			codeDescription : $(".w2ui-msg-body textarea[name='subCodeDescription']").val(),
	}
	if(type == 'regist'){
		regist(code, codeType);
	}else{
		update(code, codeType);
	}
}

/********************************************************
 * 설명		: 코드 등록
 * Function	: regist
 *********************************************************/
function regist(value, codeType){
	
	lock( '등록 중입니다.', true);
	if (!popupValidation()) return
	if( value.subGroupCode && value.subGroupCode == 'y' ) { 
		value.subGroupCode = null; 
	}
			$.ajax({
				type : "POST",
				url : "/admin/code/add",
				contentType : "application/json",
				//dataType: "json",
				async : true,
				data : JSON.stringify(value),
				success : function(data, status) {
					w2popup.unlock();
					w2popup.close();	
					if(codeType == 'codeGroup') { 
						doSearch(); 
					}else{ 
						doSearchByIdx(value.parentCode); 
					}
				},
				error : function(request, status, error) {
					w2popup.unlock();
					var errorResult = JSON.parse(request.responseText);
					w2alert(errorResult.message);
				}
			});
}

/********************************************************
 * 설명		: 코드 그룹 수정팝업
 * Function	: updatePopup
 *********************************************************/
function updatePopup(record) {
			w2popup.open({
			title 	: "<b>코드 그룹 수정</b>",
			width 	: 550,
			height	: 380,
			modal	: true,
			body	: $("#regPopupDiv").html(),
			buttons : $("#updatePopupBtnDiv").html(),
			onOpen : function(event){
				event.onComplete = function(){
					$(".w2ui-msg-body input[name='codeValue']").attr("readonly", true);
					$(".w2ui-msg-body input[name='codeGroupIdx']").val(record.recid);
					$(".w2ui-msg-body input[name='codeName']").val(record.codeName);
					$(".w2ui-msg-body input[name='codeValue']").val(record.codeValue);
					$(".w2ui-msg-body textarea[name='codeDescription']").val(record.codeDescription);
				}
			},onClose:function(event){
				w2ui['us_codeGroupGrid'].clear();
				doSearch();
				w2ui['us_codeGrid'].clear();
			}
		});
}

/********************************************************
 * 설명		: 코드 그룹 수정
 * Function	: update
 *********************************************************/
function update(record, codeType) {
	lock( '수정 중입니다.', true);
	if (!popupValidation()) return
	if( record.subGroupCode && record.subGroupCode == 'y' ) { 
		record.subGroupCode = null; 
	}
			$.ajax({
				type : "PUT",
				url : "/admin/code/update/" + record.codeIdx,
				contentType : "application/json",
				async : true,
				data : JSON.stringify(record),
				success : function(data, status) {
					// ajax가 성공할때 처리...
					w2popup.unlock();
					w2popup.close();
					if(codeType == 'codeGroup') { 
						doSearch(); 
					}else{ 
						doSearchByIdx(record.parentCode); 
					}
				},
				error : function(request, status, error) {
					// ajax가 실패할때 처리...
					w2popup.unlock();
					var errorResult = JSON.parse(request.responseText);
					w2alert(errorResult.message);
				}
			});
}

/********************************************************
 * 설명		: 코드 그룹 삭제
 * Function	: deleteCode
 *********************************************************/
function deleteCode(codeIdx){
	$.ajax({
		type : "DELETE",
		url : "/admin/code/delete/"+ codeIdx,
		contentType : "application/json",
		success : function(data, status) {
			w2popup.unlock();
			w2popup.close();
			gridReload();
		},
		error : function(request, status, error) {
			var errorResult = JSON.parse(request.responseText);
			w2alert(errorResult.message, "삭제");
		}
	});
}



/********************************************************
 * 설명		: 코드 수정팝업
 * Function	: updateCodePopup
 *********************************************************/
function updateCodePopup(record) {
	w2popup.open({
		title 	: "<b>코드 수정</b>",
		width 	: 550,
		height	: 420,
		modal	: true,
		body	: $("#regCodePopupDiv").html(),
		buttons : $("#updateCodePopupBtnDiv").html(),
		onOpen : function(event){
			event.onComplete = function(){
				var selected = w2ui['us_codeGroupGrid'].getSelection();
				var groupRecord = w2ui['us_codeGroupGrid'].get(selected);
				
				$(".w2ui-msg-body input[name='subCodeValue']").attr("readonly", true); 
				$(".w2ui-msg-body input[name='codeIdx']").val(record.recid);
				
				$(".w2ui-msg-body input[name='parentCodeName']").val(groupRecord.codeName);
				$(".w2ui-msg-body input[name='parentCodeVal']").val(record.parentCode);
				
				$(".w2ui-msg-body input[name='subGroupCode']").val(record.subGroupCode);
				$(".w2ui-msg-body input[name='subCodeName']").val(record.codeName);
				$(".w2ui-msg-body input[name='subCodeValue']").val(record.codeValue);
				$(".w2ui-msg-body input[name='subCodeNameKR']").val(record.codeNameKR);
				$(".w2ui-msg-body textarea[name='subCodeDescription']").val(record.codeDescription);
				getSubList(record.parentCode, record.subGroupCode);
			}
		},onClose:function(event){
			doSearchByIdx(record.parentCode);
		}
	});
}


/********************************************************
 * 설명		: 버튼 스타일 변경
 * Function	: doButtonStyle
 *********************************************************/
function doButtonStyle() {
	//코드 그룹 목록
	$('#modifyBtn').attr('disabled', true);
	$('#deleteBtn').attr('disabled', true);
	
	//코드 목록 버튼
	$('#addCodeBtn').attr('disabled', true);
	$('#modifyCodeBtn').attr('disabled', true);
	$('#deleteCodeBtn').attr('disabled', true);
	
}

/********************************************************
 * 설명		: 화면 초기화에 필요한 데이터 요청
 * Function	: initView
 *********************************************************/
function initView() {
	$('#deleteBtn').attr('disabled', true);
	gridReload();
}

/********************************************************
 * 설명		: 다른페이지 이동시 호출
 * Function	: clearMainPage
 *********************************************************/
function clearMainPage() {
	$().w2destroy('us_codeGroupGrid');
	$().w2destroy('us_codeGrid');
}

/********************************************************
 * 설명		: Lock 실행
 * Function	: lock
 *********************************************************/
function lock (msg) {
    w2popup.lock(msg, true);
}


</script>
<div id="main">
	<div class="page_site">플랫폼 설치 자동화 관리 > <strong>코드 관리</strong></div>
	
	<!-- 코드 그룹 목록-->
	<div class="pdt20">
		<div class="title fl">코드 그룹 목록</div>
		<div class="fr"> 
			<span id="addBtn" class="btn btn-primary" style="width:120px">등록</span>
			<span id="modifyBtn" class="btn btn-info" style="width:120px">수정</span>
			<span id="deleteBtn" class="btn btn-danger" style="width:120px">삭제</span>
	    </div>
	</div>
	<div id="us_codeGroupGrid" style="width:100%; height:250px"></div>
	
	<!-- 코드 목록-->
	<div class="pdt20">
		<div class="title fl">코드 목록</div>
		<div class="fr"> 
			<span id="addCodeBtn" class="btn btn-primary" style="width:120px">등록</span>
			<span id="modifyCodeBtn" class="btn btn-info" style="width:120px">수정</span>
			<span id="deleteCodeBtn" class="btn btn-danger" style="width:120px">삭제</span>
	    </div>
	</div>	
	<div id="us_codeGrid" style="width:100%; height:450px"></div>
	
	<!-- 코드 그룹 추가/수정 팝업 -->
	<div id="regPopupDiv" hidden="true">
		<form id="settingForm" action="POST" style="padding:5px 0 5px 0;margin:0;">
			<input name="codeGroupIdx"  type="hidden"/>
			<div class="panel panel-info" >	
				<div class="panel-heading"><b>코드 그룹 정보</b></div>
				<div class="panel-body" style="padding:5px 5% 10px 5%;height:210px;">
					<div class="w2ui-field">
						<label style="width:30%;text-align: left;padding-left: 20px;">코드 그룹명</label>
						<div style="width: 70%;">
							<input name="codeName" type="text" placeholder="예) 스템셀 그룹" maxlength="100" style="width: 250px" onkeypress="Keycode(event);" required="required" />
							<div class="isMessage"></div>
							<span id="codeValueSuccMsg"></span><BR><span id="codeValueErrMsg" style="color:'red'"></span>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="width:30%;text-align: left;padding-left: 20px;">코드 그룹값</label>
						<div style="width: 70%">
							<input name="codeValue" type="text" maxlength="100" style="width: 250px" required="required" onkeypress="Keycode(event);" placeholder="예) 10000" />
							<div class="isMessage"></div>
							<span id="codeValueSuccMsg"></span><BR><span id="codeValueErrMsg" style="color:'red'"></span>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="width:30%;text-align: left;padding-left: 20px;">설명</label>
						<div style="width: 70%;">
							<textarea name="codeDescription" onkeypress="Keycode(event);" placeholder="설명을 입력하세요." style="float: left; width: 250px; height: 60px; overflow-y: visible; resize: none;" required ></textarea>
							<div class="isMessage"></div>
							<span id="codeValueSuccMsg"></span><BR><span id="codeValueErrMsg" style="color:'red'"></span>
						</div>
					</div>
				</div>
			</div>
		</form>	
	</div>
	<div id="regPopupBtnDiv" hidden="true">
		<button class="btn" id="registBtn" onclick="registCodeGroup('regist', 'codeGroup');">확인</button>
		<button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
	</div>

	<div id="updatePopupBtnDiv" hidden="true">
		<button class="btn" id="updateBtn" onclick="registCodeGroup('update', 'codeGroup');">확인</button>
		<button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
	</div>
	
	<!-- 코드 추가/수정 팝업 -->
	<div id="regCodePopupDiv"  hidden="true">
		<form id="settingForm" action="POST" style="padding:5px 0 5px 0;margin:0;">
			<input name="codeIdx" type="hidden"/>
			<div class="panel panel-info" >	
				<div class="panel-heading"><b>코드 정보</b></div>
				<div class="panel-body" style="padding:5px 5% 10px 5%;height:300px;">
					<div class="w2ui-field">
						<label style="width:30%;text-align: left;padding-left: 20px;">코드 그룹명</label>
						<div style="width: 70%;">
							<input name="parentCodeName" type="text" maxlength="100" style="width: 250px" readonly />
							<input name="parentCodeVal" type="text" hidden="true" />
						</div>
					</div>
					<div class="w2ui-field">
						 <input name="subGroupCode" type="text" hidden="true" />
						 <input name="subGroupName" type="text" hidden="true" />
					    <label style="width:30%;text-align: left;padding-left: 20px;">하위 그룹</label>
						<div id="subGroupCodeDiv" style="width: 70%;"></div>
					</div>
					<div class="w2ui-field">
						<label style="width:30%;text-align: left;padding-left: 20px;">코드명(영문)</label>
						<div style="width: 70%;">
							<input name="subCodeName" type="text" maxlength="100" style="width: 250px" placeholder="예) sub_code" required="required"  />
							<div class="isMessage"></div>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="width:30%;text-align: left;padding-left: 20px;">코드명(한글)</label>
						<div style="width: 70%;">
							<input name="subCodeNameKR" type="text" maxlength="100" style="width: 250px" placeholder="예) 하위 코드" required="required"  />
							<div class="isMessage"></div>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="width:30%;text-align: left;padding-left: 20px;">코드값</label>
						<div style="width: 70%">
							<input name="subCodeValue" type="text" maxlength="100" placeholder="예)11000" style="width: 250px" required="required" onkeypress="Keycode(event);"/>
							<div class="isMessage"></div>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="width:30%;text-align: left;padding-left: 20px;">설명</label>
						<div style="width: 70%;">
							<textarea name="subCodeDescription" placeholder="예)sub code"  style="float: left; width: 250px; height: 70px; overflow-y: visible; resize:none; " required ></textarea>
							<div class="isMessage"></div>
						</div>
					</div>
				</div>
			</div>
		</form>	
	</div>
	<div id="regCodePopupBtnDiv" hidden="true">
		<button class="btn" id="registCodeBtn" onclick="registCode('regist', 'code');">확인</button>
		<button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
	</div>

	<div id="updateCodePopupBtnDiv" hidden="true">
		<button class="btn" id="updateCodeBtn" onclick="registCode('update', 'code');">확인</button>
		<button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
	</div>
	
</div>
