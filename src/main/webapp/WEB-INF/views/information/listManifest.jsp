<%
/* =================================================================
 * 작성일 : 2016-09
 * 작성자 : 지향은
 * 상세설명 : Manifest 관리
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016.12       지향은        화면 개선 및 코드 버그 수정
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<script type="text/javascript">

$(function() {
	
 	/************************************************
 	 * 설명: Manifest 조회
 	************************************************/
 	$('#us_manifestGrid').w2grid({
		name: 'us_manifestGrid',
		header: '<b>Manifest 목록</b>',
		style	: 'text-align:center',
		msgAJAXerror : 'Manifset 조회 실패',
		method	: 'GET',
		multiSelect: false,
		show: {	
			selectColumn: true,
			footer: true},
		style: 'text-align: center',
		columns	: [
					 {field: 'recid', 	caption: 'recid', hidden: true}
				   , {field: 'id', 	caption: 'id', hidden: true}
				   , {field: 'fileName', caption: 'Manifest 파일명', size: '20%', style: 'text-align:center'}
				   , {field: 'iaas', caption: 'iaas', size: '20%', style: 'text-align:center'}
				   , {field: 'deploymentName', caption: '배포명', size: '20%', style: 'text-align:center'}
		       	   , {field: 'description', caption: '설명', size: '70%', style: 'text-align:center'}
		       	   , {field: 'deployStatus', caption: '배포 상태', size: '20%', style: 'text-align:center',
		       		   render: function(record) {
		       			   if( record.deployStatus == "DEPLOY_STATUS_PROCESSING" ){
		       					return '<span class="btn btn-primary" style="width:60px">배포 중</span>';
		       			   }else if( record.deployStatus == "DEPLOY_STATUS_DONE" ){
		       					return '<span class="btn btn-primary" style="width:60px">배포 완료</span>';
		       			   }else if( record.deployStatus == "DEPLOY_STATUS_CANCELLED" ){
		       					return '<span class="btn btn-primary" style="width:60px">배포 취소</span>';
		       			   }else if( record.deployStatus == "DEPLOY_STATUS_FAILED" ){
		       					return '<span class="btn btn-primary" style="width:60px">배포 실패</span>';
		       			   }
		       		   }
		       	   }
		       	],
       	onSelect: function(event) {
			var grid = this;
			event.onComplete = function() {
				$('#manifestDownload').attr('disabled', false);
				$('#manifestUpdate').attr('disabled', false);
				$('#manifestDelete').attr('disabled', false);
			}
		},
		onUnselect: function(event) {
			event.onComplete = function() {
				$('#manifestDownload').attr('disabled', true);
				$('#manifestUpdate').attr('disabled', true);
				$('#manifestDelete').attr('disabled', true);
			}
		},
       	onLoad:function(event){
			if(event.xhr.status == 403){
				location.href = "/abuse";
				event.preventDefault();
			}
		}, onError:function(evnet){
		}
	});
	
 	/************************************************
 	 * 설명: Manifest 업로드
 	************************************************/
 	$("#manifestUpload").click(function(){
 		w2popup.open({
 			title 	: "<b>Manifest 업로드</b>",
 			width 	: 550,
 			height	: 300,
 			modal	: true,
 			body	: $("#uploadPopupDiv").html(),
 			buttons : $("#uploadPopupBtnDiv").html(),
 			onClose : function(event){
 				initView();
 			}
 		});
 		uploadManifestPath();
 	});
 	
	/************************************************
 	 * 설명: Manifest 삭제 확인
 	************************************************/
 	$("#manifestDelete").click(function(){
 		if($("#manifestDelete").attr('disabled') == "disabled") return;
 		//select grid row info
 		var selected = w2ui['us_manifestGrid'].getSelection();
		if ( selected == "" || selected == null) return;
		var record = w2ui['us_manifestGrid'].get(selected);
		if ( record == "" || record == null) return;
		
		var msg = "";
		if( record.deployStatus != "" && record.deployStatus != null  ){
			msg = "현재 사용 중인 "+record.fileName + "파일입니다. <br/> 그래도 지우시겠습니까?";
		} else{
			msg = record.fileName +"파일을 삭제하시겠습니까?";
		}
 		w2confirm({
 			title 	: "Manifest 삭제",
 			msg		: msg,
 			yes_text: "확인",
 			yes_callBack : function(envent){
 				deleteManifest(record.id, record.fileName);
 				w2popup.close();
 			},
 			no_text : "취소",
 			no_callBack : function(event){
 				initView();
 			}
 		});
 	});
 	
 	initView();
});

/********************************************************
 * 설명 		:  조회 기능
 * Function : doSearch
 *********************************************************/
function doSearch() {
	w2ui['us_manifestGrid'].load("<c:url value='/info/manifest/list'/>");	
}

/********************************************************
 * 설명 		:  로컬 파일 업로드
 * Function : uploadManifestPath
 *********************************************************/
function uploadManifestPath(){
	//iaas select
	 var iaasDiv = $('.w2ui-msg-body #iaasDiv');
	 var selectInput = '<input type="list" name="iaasList" id="iaasList"  style="float: left;width:217px;"   onchange="setIaasInfo(this.value);" placeholder="iaas를 선택하세요."/>';
	 iaasDiv.html(selectInput);
	 $('.w2ui-msg-body #iaasDiv input[type=list]').w2field('list', { items: ['OPENSTACK', 'AWS', 'VSPHERE'] , maxDropHeight:200, width:250});
	 
	 //upload div
	 var uploadPathDiv = $('.w2ui-msg-body #uploadPathDiv');
	 
	 var fileUploadInput = '<span><input type="file" name="uploadPathFile" id="uploadPathFile" onchange="setUploadFilePath(this);"   hidden="true"/>';
	 fileUploadInput += '<input type="text" id="uploadPathFileName" name="uploadPathFileName" style="width:70%;" readonly  onClick="openBrowse();" placeholder="업로드할 Manifest 파일을 선택하세요."/>';
	 fileUploadInput += '<a href="#" id="browse" onClick="openBrowse();">Browse </a></span>';
	 fileUploadInput += '<div class="isMessage"></div>';
	
	 uploadPathDiv.html(fileUploadInput);		
	 $(".w2ui-msg-body input[name='uploadPathFile']").hide();		
}


 /********************************************************
  * 설명 		:  iaas 정보 설정
  * Function : setIaasInfo
  *********************************************************/
function setIaasInfo(value){
	$(".w2ui-msg-body #iaasInput").val(value);
}

 /********************************************************
  * 설명 		:  Manifest 브라우저 찾기
  * Function : openBrowse
  *********************************************************/
function openBrowse(){
	$(".w2ui-msg-body input[name='uploadPathFile']").click();
}

 /********************************************************
  * 설명 		:  Manifest 파일 경로
  * Function : setUploadFilePath
  *********************************************************/
function setUploadFilePath(fileInput){
	
	var file = fileInput.files;
	var files = $('#uploadPathFile')[0].files;
	$(".w2ui-msg-body input[name=manifestPath]").val(file[0].name);
	$(".w2ui-msg-body #uploadPathFileName").val(file[0].name);
	
}

 /********************************************************
  * 설명 		:  Manifest validation
  * Function : uploadManifestValidate
  *********************************************************/
function uploadManifestValidate(){
	if (popupValidation()) {
		w2popup.lock("Manifest 파일 업로드 중",true);
		var form = $(".w2ui-msg-body #settingForm");
		var formData = new FormData(form);
	
		var files = $('#uploadPathFile')[0].files;
		formData.append("file", files[0]);
		formData.append("description", $(".w2ui-msg-body #description").val());
		formData.append("iaas", $(".w2ui-msg-body #iaasInput").val());
		
		if(files[0] == null || files[0] == undefined ){
			w2alert("업로드할 Manifest 파일을 선택해주세요.", "Manifest 파일 업로드");
	 		return false;
		}
		
		if(files[0].size == 0){
	 		w2alert("Manifest 파일을 찾을 수 없습니다. 확인해주세요.", "Manifest 파일 업로드");
	 		return false;
	 	}
		if( $(".w2ui-msg-body #uploadPathFileName").val().indexOf(".yml") < 0 ){
			w2alert(".yml 확장자를 가진 파일만 가능합니다. 확인해주세요.", "Manifest 파일 업로드");
	 		return false;
		}
		
		uploadManifest(formData, name);
	}
}

 /********************************************************
  * 설명 		:  Manifest 업로드 요청
  * Function : uploadManifest
  *********************************************************/
function uploadManifest(formData, name){
	
 	$.ajax({
		type : "POST",
		url : "/info/manifest/upload/N",
		enctype : 'multipart/form-data',
		dataType: "text",
		async : true,
		processData: false, 
		contentType:false,
		data : formData,  
		success : function(data, status) {
			w2popup.close();
			doSearch();
		},
		error : function(e, status) {
			w2ui['us_manifestGrid'].reset();
			if((JSON.parse(e.responseText).code).indexOf("yaml") > -1){
				var errorResult = JSON.parse(e.responseText).message;
				errorResult =  "<span style='font-weight:bold'>YAML 형식 오류:</span>" +
					"<br/><br/><div style='text-align:left'>" + errorResult+"</div>";
				w2alert(errorResult, "Manifest 업로드");
			}else{
				w2alert(JSON.parse(e.responseText).message, "Manifest 업로드");
			}
		}
	});
}


 /********************************************************
 * 설명 		:  Manifest 다운로드
 * Function : downloadManifest
 *********************************************************/
function downloadManifest( ){
	 if($("#manifestDownload").attr('disabled') == "disabled") return;
	//select grid row info
	var selected = w2ui['us_manifestGrid'].getSelection();
	if ( selected == "" || selected == null) return;
	var record = w2ui['us_manifestGrid'].get(selected);
	if ( record == "" || record == null) return;
	
	var logDownloadUrl = "/info/manifest/download/"+ record.id; 
	
	window.open(logDownloadUrl, '', ''); 
	initView();
	return false;
}

 /********************************************************
 * 설명 		:  Manifest 수정 Popup
 * Function : updateManifest
 *********************************************************/
function updateManifest(){
	if($("#manifestUpdate").attr('disabled') == "disabled") return;
	if($("#manifestDownload").attr('disabled') == "disabled") return;
	//select grid row info
	var selected = w2ui['us_manifestGrid'].getSelection();
	if ( selected == "" || selected == null) return;
	var record = w2ui['us_manifestGrid'].get(selected);
	if ( record == "" || record == null) return;
	
	var msg = record.fileName +"파일을 수정하시겠습니까?";
	
	w2confirm({
		title 	: "Manifest 수정",
		msg		: msg,
		yes_text: "확인",
		yes_callBack : function(envent){
			$("#updatePopupDiv").w2popup({
				width : 850,
				height : 650,
				modal : true,
				showMax : true,
				onClose : doSearch,
				onOpen : function(event) {
					event.onComplete = function() {
						getManifestInfo(record.id);
					}
				},onClose : function(event){
					initView();
				}
			});
			w2popup.close();
		},
		no_text : "취소",
		no_callBack : function(event){
			initView();
		}
	});
}

/********************************************************
 * 설명 		:  Manifest 내용 조회
 * Function : getManifestInfo
 *********************************************************/
function getManifestInfo(id){
	 
	$.ajax({
		type : "GET",
		url : "/info/manifest/update/"+id,
		contentType : "application/json",
		async : true,
		success : function(data, status) {
			$(".w2ui-msg-body #manifestContent").text(data);
		},
		error : function(e, status) {
			w2alert("Manifest 내용을 가져오는 중 오류가 발생하였습니다. ", "Manifest 조회");
		}
	});
}

/********************************************************
 * 설명 		:  내용 수정 요청
 * Function : updateManifestContnet
 *********************************************************/
function updateManifestContnet(){
	w2popup.lock("수정 중",true);
	var selected = w2ui['us_manifestGrid'].getSelection();
	if ( selected == "" || selected == null) return;
	var record = w2ui['us_manifestGrid'].get(selected);
	if ( record == "" || record == null) return;

	//1.2
	var manifest = {
			id					: record.id,
			content 			: $(".w2ui-msg-body #manifestContent").val(),
			fileName 		: record.fileName
	}
	
	if( manifest.content != "" ) {
		$.ajax({
			type : "PUT",
			url : "/info/manifest/update",
			contentType : "application/json",
			data : JSON.stringify(manifest),
			success : function(data, status) {
				w2popup.unlock();
				w2popup.close();
				doSearch();
			},
			error : function(e, status) {
				w2popup.unlock();
				if((JSON.parse(e.responseText).code).indexOf("yaml") > -1){
					var errorResult = JSON.parse(e.responseText).message;
					errorResult =  "<span style='font-weight:bold'>YAML 형식 오류:</span>" +
						"<br/><br/><div style='text-align:left'>" + errorResult+"</div>";
					w2alert(errorResult, "YAML 형식 오류");
				}else{
					w2alert( JSON.parse(e.responseText).message, "Manifest 수정")
				}
			}
		});
	}
}

/********************************************************
 * 설명 		:  Manifest 삭제
 * Function : deleteManifest
 *********************************************************/
function deleteManifest( id, manifestFile ){
	$.ajax({
		type : "DELETE",
		url : "/info/manifest/delete/"+ id,
		success : function(data, status) {
			initView();
		}, error : function(e, status) {
			w2ui['us_manifestGrid'].reset();
			var errorResult = JSON.parse(e.responseText);
			w2alert(errorResult.message, "Manifest 삭제");
		}
	});
}


 /********************************************************
  * 설명 		:  팝업 종료시 이벤트
  * Function : popupComplete
  *********************************************************/
function popupComplete(){
	var msg = $(".w2ui-msg-title b").text() + " 화면을 닫으시겠습니까?";
	w2confirm({
		title 	: $(".w2ui-msg-title b").text(),
		msg		: msg,
		yes_text: "확인",
		yes_callBack : function(envent){
			w2popup.close();
			doSearch();
		},
		no_text : "취소"
	});
}

/********************************************************
 * 설명 		:  초기 설정
 * Function : initView
 *********************************************************/
function initView(bDefaultDirector) {
	w2ui['us_manifestGrid'].clear();
	doSearch();
	$('#manifestDownload').attr('disabled', true);
	$('#manifestUpdate').attr('disabled', true);
	$('#manifestDelete').attr('disabled', true);
}

/********************************************************
 * 설명			: 다른페이지 이동시 호출
 * Function	: clearMainPage
 *********************************************************/
function clearMainPage() {
	$().w2destroy('us_manifestGrid');
}

/********************************************************
 * 설명			: 화면 리사이즈시 호출
 * Function	: resize
 *********************************************************/
$( window ).resize(function() {
	setLayoutContainerHeight();
});
</script>

<div id="main">
	<div class="page_site">정보조회 > <strong>Manifest 관리</strong></div>
	<div class="pdt20"> 
		<div class="title fl">Manifest 목록</div>
		<div class="fr"> 
			<!-- Btn -->
			<sec:authorize access="hasAuthority('INFO_MANIFEST_UPLOAD')">
			<span class="btn btn-warning" style="width:120px" id="manifestUpload">업로드</span>
			</sec:authorize>
			<sec:authorize access="hasAuthority('INFO_MANIFEST_DOWNLOAD')">
			<span class="btn btn-primary" style="width:120px" id="manifestDownload" onclick="downloadManifest();">다운로드</span>
			</sec:authorize>
			<sec:authorize access="hasAuthority('INFO_MANIFEST_UPDATE')">
			<span class="btn btn-info" style="width:120px" id="manifestUpdate" onclick="updateManifest();">수정</span>
			</sec:authorize>
			<sec:authorize access="hasAuthority('INFO_MANIFEST_DELETE')">
			<span class="btn btn-danger" style="width:120px" id="manifestDelete">삭제</span>
			</sec:authorize>
			<!-- //Btn -->
		</div>
	</div>
	<!-- Manifest Grid -->
	<div id="us_manifestGrid" style="width:100%; height:718px"></div>	
	<!-- Manifest 업로드  -->
	<form id="settingForm" action="POST" >
		<div id="uploadPopupDiv" hidden="true">
			<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
				<div class="panel panel-info" >	
					<div class="panel-heading"><b>Manifest 업로드</b></div>
					<div class="panel-body" style="padding:5px 5% 10px 5%;">
						<input name="iaasInput" id="iaasInput" type="text" style="width:200px;" hidden="true" />
						<div class="w2ui-field">
							<label style="width:30%;text-align: left;padding-left: 20px;">IaaS</label>
							<div id="iaasDiv" >	
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="width:30%;text-align: left;padding-left: 20px;">설명</label>
							<div>
								<input name="description" id="description" type="text" style="width:217px;"/>
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="width:30%;text-align: left;padding-left: 20px;">파일</label>
							<input name="manifestPath" type="text" hidden="true"/>
							<div id="uploadPathDiv" style="width:72%"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div id="uploadPopupBtnDiv" hidden="true">
			<button class="btn" id="registBtn" onclick="uploadManifestValidate()">업로드</button>
			<button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
		</div>
	</form>
	<!--  Manifest 수정  -->
	<div id="updatePopupDiv" hidden="true">
		<div rel="title"><b>Manifest 수정</b></div>
		<div rel="body" style="width:100%;height:100%;padding:5px;margin:0 auto;">
			<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
				<div class="panel panel-info" >	
						<div style="padding:5px 5% 5px 5px;">
							<textarea id="manifestContent" style="width:100%;height:500px; overflow-y:visible;resize:none;background-color: #FFF;margin-left:3%;"></textarea>
						</div>
				</div>
			</div>
			<div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" id="updateManifestContnet" onclick="updateManifestContnet()">저장</button>
				<button class="btn" onclick="popupComplete();">닫기</button>
			</div>
		</div>
	</div>
</div>