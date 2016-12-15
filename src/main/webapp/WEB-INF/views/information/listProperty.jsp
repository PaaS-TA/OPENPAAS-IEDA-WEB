<%
/* =================================================================
 * 작성일 : 2016-09
 * 작성자 : 이동현
 * 상세설명 : Property 관리
 * =================================================================
 * 수정일         작성자             내용     
 * -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * 2016.12       이동현        화면 개선 및 코드 버그 수정
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
 <script>
 var deployment = ""; 
 var properties = [];
 var bDefaultDirector = "";
 $(function() {
	
 	/************************************************
 	 * 설명: 기본 설치 관리자 정보 조회
 	************************************************/
 	 bDefaultDirector = getDefaultDirector("<c:url value='/common/use/director'/>");
 	initView(bDefaultDirector);
 	
	 	$('#us_PropertyGrid').w2grid({
			name: 'us_PropertyGrid',
			method: 'GET',
			header: '<b>Property 목록</b>',
			multiSelect: false,
			show: {	
					selectColumn: true,
					footer: true},
			style: 'text-align: center',
			columns	: [
						 {field: 'recid', 	caption: 'recid', hidden: true}
					   , {field: 'name', caption: 'Property 명', size: '50%', style: 'text-align:center', render : function(record){
						   properties.push(record.name);
						   return record.name;
						}}
				       , {field: 'value', caption: 'Property 값', size: '50%', style: 'text-align:center'}
			       	],
	       	onSelect: function(event) {
				event.onComplete = function() {
					$('#detailProperty').attr('disabled', false);
					$('#modifyProperty').attr('disabled', false);
					$('#deleteProperty').attr('disabled', false);
				}
			},
			onUnselect: function(event) {
				event.onComplete = function() {
					$('#detailProperty').attr('disabled', true);
					$('#modifyProperty').attr('disabled', true);
					$('#deleteProperty').attr('disabled', true);
				}
			},
	       	onLoad:function(event){
				if(event.xhr.status == 403){
					location.href = "/abuse";
					event.preventDefault();
				}
				$('#createProperty').attr('disabled', false);
			}, onError:function(evnet){
			}
		});
 	});
 
	/************************************************
	 * 설명: Property 조회
	************************************************/
	$("#doSearch").click(function(){
			$('#detailProperty').attr('disabled', true);
 			$('#modifyProperty').attr('disabled', true);
 			$('#deleteProperty').attr('disabled', true);
		doSearch($("#deployments").val());
	});
 
	 /************************************************
	  * 설명: 조회기능
	 ************************************************/
	 function doSearch(deployment) {
	 	if( deployment != null ){
	 		w2ui['us_PropertyGrid'].load("<c:url value='/info/property/list/'/>"+deployment);
	 	}else{
	 		if(!bDefaultDirector){
				w2alert("기본 설치 관리자를 등록해 주세요. ","프로퍼티 조회");
			}else{
				w2alert("배포명을 선택해주세요. ","프로퍼티 조회");
			}
	 	}
	 	properties = [];
	 }
 
	 /********************************************************
	  * 설명		: 그리드 재조회 및 버튼 초기화
	 * Function	: initView
	 *********************************************************/
	 function initView(bDefaultDirector) {
			if ( bDefaultDirector ) {
				getDeploymentList();
			}else{
				$("#deployments").html("<option selected='selected' disabled='disabled' value='all' style='color:red'>기본 설치자가 존재 하지 않습니다.</option>");
			}
			$('#createProperty').attr('disabled', true);
			$('#detailProperty').attr('disabled', true);
			$('#modifyProperty').attr('disabled', true);
			$('#deleteProperty').attr('disabled', true);
		}
	 
	 /********************************************************
	  * 설명		: 그리드 재조회 및 버튼 초기화
	 * Function	: initView
	 *********************************************************/
	 function buttonStyle(){
		 w2ui['us_PropertyGrid'].clear();
		$('#createProperty').attr('disabled', false);
		$('#detailProperty').attr('disabled', true);
		$('#modifyProperty').attr('disabled', true);
		$('#deleteProperty').attr('disabled', true);
	 }
	 
	 /********************************************************
	  * 설명		: 배포 조회
	 * Function	: getDeploymentList
	 *********************************************************/
	 function getDeploymentList(){
	 	jQuery.ajax({
	 		type : "GET",
	 		url : '/common/use/deployments',
	 		contentType : "application/json",
	 		async : true,
	 		success : function(data) {
	 			var $object = jQuery("#deployments");
	 			var optionString = "";
	 			if(data.contents != null){
	 				optionString = "<option selected='selected' disabled='disabled' value='all' style='color:gray'>조회할 배포명을 선택하세요.(필수)</option>";
	 				for (i = 0; i < data.contents.length; i++) {
	 					optionString += "<option value='" + data.contents[i].name + "' >";
	 					optionString += data.contents[i].name;
	 					optionString += "</option>\n";
	 				}
	 			}else{
	 				optionString = optionString = "<option selected='selected' disabled='disabled' value='all' style='color:red'>조회할 배포명이 없습니다.</option>";
	 			}
	 			$object.html(optionString);
	 		}
	 	});
	 }
	 
	 /****************************************
	  * 설명:  Property 생성 팝업
	  ****************************************/
	 $("#createProperty").on("click",function(){
		 if($("#createProperty").attr('disabled') == "disabled") return;
		 w2popup.open({
				title : "<b>Property 생성</b>",
				width : 600,
				height :450,
				modal :true,
				body	: $("#createPropertyDiv").html(),
				buttons : $("#createPropertyDivBtn").html(),
				onOpen :function(event) {
					event.onComplete = function() {
						
					}
				},
				onClose :function(event) {
					event.onComplete = function() {
						doSearch($("#deployments").val());
					}
				}
			});
	});	
	 
	 /****************************************
	  * 설명:  Property 생성
	  ****************************************/
		function saveProperty(modify){
		  if(modify!="Y"){
		  	for(var i=0;i<properties.length;i++){
				if($(".w2ui-msg-body input[name='propertyName']").val()==properties[i]
				&& properties.name != $(".w2ui-msg-body input[name='propertyName']").val() ){
					w2alert("중복 된 Property 명 입니다.", "Property 생성");
					return;
				}
			} 
		  }
		  var value =  $(".w2ui-msg-body input[name='propertyName']").val().length;
		  var replacevalue =  $(".w2ui-msg-body input[name='propertyName']").val().replace(/[\ㄱ-ㅎㅏ-ㅣ가-힣]/g, '');
		  if(value!=replacevalue.length){
			  w2alert("Property 명에는 한글을 입력 하실 수 없습니다..", "Property 생성");
			  return;
		  }
		 var propertyParam = {
					name : $(".w2ui-msg-body input[name='propertyName']").val(),
					value : $(".w2ui-msg-body textarea[name='propertyValue']").val(),
					deploymentName : $("#deployments").val()
			};
		 if(modify!="Y"){
			if(popupValidation()){
				$.ajax({
					type : "POST",
					url : "/info/property/modify/createProperty",
					contentType : "application/json",
					async : true,		
					data : JSON.stringify(propertyParam),
					success : function(status) {
						w2popup.unlock();
						w2popup.close();
						doSearch($("#deployments").val());
					},
					error : function(request, status, error) {
						// ajax가 실패할때 처리...
						w2popup.unlock();
						w2popup.close();
						var errorResult = JSON.parse(request.responseText);
						w2alert(errorResult.message);
						doSearch($("#deployments").val());	
					}
				});
			}
		 }else{
			 if(popupValidation()){
					$.ajax({
						type : "PUT",
						url : "/info/property/modify/updateProperty",
						contentType : "application/json",
						async : true,		
						data : JSON.stringify(propertyParam),
						success : function(status) {
							w2popup.close();
							w2ui['us_PropertyGrid'].clear();
							doSearch($("#deployments").val());			
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
	 	}	
	 
	/********************************************************
	 * 설명			: Property 수정 버튼 클릭
	 *********************************************************/	
	 $("#modifyProperty").click(function(){
		 if($("#modifyProperty").attr('disabled') == "disabled") return;
		 var selected = w2ui['us_PropertyGrid'].getSelection();
		 var recodes = w2ui['us_PropertyGrid'].get(selected);
		 w2confirm({
				title		: "Property 수정",
				msg			: "Property (" + recodes.name + ")을 수정하시겠습니까?",
				yes_text	: "확인",
				no_text		: "취소",
				yes_callBack: function(event){
					$("#createPropertyDiv").w2popup({
						title : "<b>Property 수정 </b>",
						width : 600,
						height :450,
						modal :true,
						buttons : $("#modifyPropertyDivBtn").html(),
						showMax :false,
						onOpen :function(event) {
							event.onComplete = function() {
								$(".w2ui-msg-body input[name='propertyName']").val(recodes.name);
								$(".w2ui-msg-body input[name='propertyName']").prop('readonly','true');
								$(".w2ui-msg-body textarea[name='propertyValue']").val(recodes.value);
							}
						},
						onClose :function(event) {
							event.onComplete = function() {
								doSearch($("#deployments").val());			
								buttonStyle();
							}
						}
					});
				},
				no_callBack	: function(){
					doSearch($("#deployments").val());			
					buttonStyle();
				}
			});
	});	
	 	
	/********************************************************
	 * 설명			: Property 삭제 버튼 클릭
	 *********************************************************/	
	 $("#deleteProperty").on("click",function(){
		 if($("#deleteProperty").attr('disabled') == "disabled") return;
		 var selected = w2ui['us_PropertyGrid'].getSelection();
		 var recodes = w2ui['us_PropertyGrid'].get(selected);
			w2confirm({
				title		: "Property 삭제",
				msg			: "Property (" + recodes.name + ")을 삭제하시겠습니까?",
				yes_text	: "확인",
				no_text		: "취소",
				yes_callBack: function(event){
					deleteProperty(recodes);
				},
				no_callBack	: function(){
					doSearch($("#deployments").val());			
					buttonStyle();
				}
			});
	 });
	 	
	/********************************************************
	 * 설명			: Property 삭제 Ajax
	 * Function	: deleteProperty
	 *********************************************************/
	 function deleteProperty(recodes){
		 	var propertyParam = {
					name : recodes.name,
					value : recodes.value,
					deploymentName : $("#deployments").val()
			};
			$.ajax({
				type : "DELETE",
				url : "/info/property/modify/deleteProperty",
				contentType : "application/json",
				async : true,		
				data : JSON.stringify(propertyParam),
				success : function(status) {
					doSearch($("#deployments").val());			
				},
				error : function(request, status, error) {
					w2popup.unlock();
					w2popup.close();
					var errorResult = JSON.parse(request.responseText);
					w2alert(errorResult.message);
					doSearch($("#deployments").val());	
				}
			});
	 }
	
	/********************************************************
	 * 설명			: Property 상세 보기 버튼 클릭
	 *********************************************************/	
	 $("#detailProperty").on("click",function(){
		 if($("#detailProperty").attr('disabled') == "disabled") return;
		 var selected = w2ui['us_PropertyGrid'].getSelection();
		 var recodes = w2ui['us_PropertyGrid'].get(selected);
	 	 var propertyParam = {
				name : recodes.name,
				value : recodes.value,
				deploymentName : $("#deployments").val()
		 };
			$.ajax({
				type : "POST",
				url : "/info/property/list/detailInfo",
				contentType : "application/json",
				async : true,		
				data : JSON.stringify(propertyParam),
				success : function(data) {
					propertyDetailPopUp(recodes,data.records);
				},
				error : function(request, status, error) {
					w2popup.unlock();
					w2popup.close();
					var errorResult = JSON.parse(request.responseText);
					w2alert(errorResult.message);
				}
			});
	 });
	 
	 /********************************************************
	  * 설명		: 상세 보기 팝업
	 * Function	: propertyDetailPopUp
	 *********************************************************/
	 function propertyDetailPopUp(uiInfo,data){
			$("#propertyInfoDiv").w2popup({
				title : "<b>Property 상세 보기</b>",
				width : 600,
				height :450,
				buttons : $("#detailPropertyDivBtn").html(),
				modal :true,
				showMax :true,
				onOpen :function(event) {
					event.onComplete = function() {
						$(".w2ui-msg-body input[name='propertyName']").val(uiInfo.name);
						$(".w2ui-msg-body textarea[name='propertyValue']").val(data.value);
					}
				},
				onClose : function(event){
					doSearch($("#deployments").val());			
					buttonStyle();
				}
			});
	 }
 
	/********************************************************
	 * 설명			: 다른페이지 이동시 호출
	 * Function	: clearMainPage
	 *********************************************************/
	function clearMainPage() {
		$().w2destroy('us_PropertyGrid');
	}

	/********************************************************
	 * 설명			: 화면 리사이즈시 호출
	 * Function	: resize
	 *********************************************************/
	$( window ).resize(function() {
		setLayoutContainerHeight();
	});
 </script>
 <style>
 	.w2ui-popup .w2ui-msg-body { overflow:initial; }
 	.fr{
 		margin-left: 100px;
 	}
 </style>
 <div id="main">
	<div class="page_site">정보조회 > <strong>Property 관리</strong></div>
	<!-- 설치 관리자 -->
	<div id="isDefaultDirector"></div>
	
	<div class="pdt20"> 
		<div class="search_box" align="left" style="padding-left:10px;">
			<label  style="font-size:11px">배포명</label>
			&nbsp;&nbsp;&nbsp;
			<select name="select" id="deployments" class="select" style="width:300px">
			</select>
			&nbsp;&nbsp;&nbsp;
			<span id="doSearch" class="btn btn-info" style="width:50px" >조회</span>
		</div>
		
		<div class="title fl">Property 목록</div>
		<div class="fr"> 
			<!-- Btn -->
			<sec:authorize access="hasAuthority('INFO_PROPERTY_MODIFY')">
			<span class="btn btn-primary" style="width:120px" id="createProperty">Property 생성</span>
			<span class="btn btn-info" style="width:120px" id="modifyProperty">Property 수정</span>
			<span class="btn btn-danger" style="width:120px" id="deleteProperty">Property 삭제</span>
			<span class="btn btn-success" style="width:140px" id="detailProperty">Property 상세보기</span>
			</sec:authorize>
			<!-- //Btn -->
		</div>
		<div id="us_PropertyGrid" style="width:100%; height:573px"></div>	
	</div>
</div>

	<!-- Property 생성 팝업 -->
	<div id="createPropertyDiv"  hidden="true">
			<div class="panel panel-info" style="margin-top:30px;">	
				<div class="panel-heading"><b>Property 정보</b></div>
				<div class="panel-body" style="padding:5px 5px 5px 5px; height: 260px;">
					<div class="w2ui-field">
						<label style="width:100%%;text-align: left;padding-left: 20px;">Property 명</label>
						<div >
							<input name="propertyName" type="text" maxlength="100" style="width: 365px" required="required" />
							<div class="isMessage"></div>
						</div>
					</div>

					<div class="w2ui-field">
						<label style="width:100%%;text-align: left;padding-left: 20px;">Property 값</label>
						<div style="width: 100%;">
							<textarea name="propertyValue" style="float: left; width: 365px; height: 212px; margin-bottom:10px; overflow-y: visible; resize: none; background-color: #FFF;" required ></textarea>
						</div>
					</div>								
				</div>
			</div>
	</div>
	<div id="createPropertyDivBtn" hidden="true">
		<button class="btn" id="savePropertyBtn" onclick="saveProperty();">저장</button>
		<button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
	</div>
	
	<!--  Property 수정 버튼 -->
	<div id="modifyPropertyDivBtn" hidden="true">
		<button class="btn" id="savePropertyBtn" onclick="saveProperty('Y');">수정</button>
		<button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
	</div>
	
	<div id="propertyInfoDiv" hidden="true">
		<div class="panel panel-info" style="margin-top:30px;">	
			<div class="panel-heading"><b>Property 정보</b></div>
			<div class="panel-body" style="padding:5px 5px 5px 5px; height: 260px;">
				<div class="w2ui-field">
					<label style="width:100%%;text-align: left;padding-left: 20px;">Property 명</label>
					<div >
						<input name="propertyName" type="text" maxlength="100" style="width: 365px" required="required" readonly="readonly" />
						<div class="isMessage"></div>
					</div>
				</div>

				<div class="w2ui-field">
					<label style="width:100%%;text-align: left;padding-left: 20px;">Property 값</label>
					<div style="width: 100%;">
						<textarea readonly name="propertyValue" style=" color:#777; float: left; width: 365px; height: 212px; margin-bottom:10px; overflow-y: visible; resize: none; background-color: #f1f1f1;" required ></textarea>
					</div>
				</div>								
			</div>
		</div>
		<div id="detailPropertyDivBtn" hidden="true">
			<button class="btn" id="popClose"  onclick="w2popup.close();">닫기</button>
		</div>
	</div>
	
	
	
