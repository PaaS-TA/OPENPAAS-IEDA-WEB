<%
/* =================================================================
 * 작성일 : 
 * 작성자 : 
 * 상세설명 : 스템셀 조회 및 다운로드
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016-12       지향은      스템셀 다운로드 기능 개선
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<script type="text/javascript">

//private common variable
var downloadClient = "";
var downloadStatus = "";
var OS_TYPE_CODE = '200';
var IAAS_TYPE_CODE = '100';
var osVersionArray = [];
var stemcellArray = [];
var completeButton = '<div><div class="btn btn-success btn-xs" style="width:100px; padding:3px;">Downloaded</div></div>';
var downloadingButton = '<div class="btn btn-info btn-xs" style="width:100px;">Downloading</div>';
var progressBarDiv = '<div class="progress">';
	progressBarDiv += '<div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" >';
	progressBarDiv += '</div></div>';
$(function() {
	/********************************************************
	 * 설명 :  스템셀 목록 조회 Grid 생성
	 *********************************************************/
 	$('#config_opStemcellsGrid').w2grid({
		name: 'config_opStemcellsGrid',
		show: {selectColumn: true, footer: true},
		multiSelect: false,
		msgAJAXerror : '스템셀 조회 실패',
		method: 'GET',
		style: 'text-align:center',
		columns:[
			 {field: 'recid', caption: 'recid', hidden: true}
			,{field: 'id', caption: '아이디', hidden: true} 
			,{field: 'stemcellName', caption: '스템셀 명', size: '15%'}
			,{field: 'os', caption: 'Os 유형', size: '10%'}			
			,{field: 'osVersion', caption: 'Os 버전', size: '10%'}
			,{field: 'iaas', caption: 'IaaS', size: '10%', sortable: true}
			,{field: 'stemcellFileName', caption: '스템셀 파일명', size: '45%', style: 'text-align:left'}
			,{field: 'stemcellVersion', caption: '스템셀 버전', size: '10%'}
			,{field: 'size', caption: '파일 크기', size: '10%'}
			,{field: 'isExisted', caption: '다운로드 여부', size: '20%',
				render: function(record) {
					if ( record.downloadStatus == 'DOWNLOADED'  ){
						return '<div class="btn btn-success btn-xs" id= "downloaded_'+record.id+'" style="width:100px;">Downloaded</div>';
					}else if(record.downloadStatus == 'DOWNLOADING'){ //다른 사용자가다운로드 중일 경우
						return '<div class="btn btn-info btn-xs" id= "downloading_'+record.id+'" style="width:100px;">Downloading</div>';
					} else{
						return '<div class="btn" id="isExisted_'+record.id+'" style="position: relative;width:100px;"></div>';
					}
				}
			}
		],
		onSelect: function(event) {
			event.onComplete = function() {
					$('#doregist').attr('disabled', false);
					$('#doDelete').attr('disabled', false);
			}
		},
		onUnselect: function(event) {
			var grid = this;
			event.onComplete = function() {
				$('#doRegist').attr('disabled', false);
				$('#doDelete').attr('disabled', true);
			}
		}, onLoad:function(event){
			if(event.xhr.status == 403){
				location.href = "/abuse";
				event.preventDefault();
			}
			
		}, onError:function(evnet){
	
		}
	});
	
 	/**************************************************************
 	 * 설명 스템셀 등록 버튼 클릭
 	 **************************************************************/
 	$("#doRegist").click(function(){
 		w2popup.open({
 			title 	: "<b>스템셀 등록</b>",
 			width 	: 675,
 			height	: 590,
 			modal	: true,
 			body	: $("#regPopupDiv").html(),
 			buttons : $("#regPopupBtnDiv").html(),
 			onClose : function(event){
 				w2ui['config_opStemcellsGrid'].clear();
 				initView();
 			}
 		});
 		setCommonCode('<c:url value="/common/deploy/codes/parent/"/>'+ OS_TYPE_CODE, 'os');
 		setCommonCode('<c:url value="/common/deploy/codes/parent/"/>' + IAAS_TYPE_CODE, 'iaas');
 		$('.w2ui-msg-body input:radio[name=fileType]:input[value=version]').attr("checked", true);	
 		$('.w2ui-msg-body input:text[name=stemcellPathVersion]').attr("readonly", false);
 		$('[data-toggle="popover"]').popover();
 	 	//스템셀 버전 정보
 	 	$(".stemcell-info").attr('data-content', "http://bosh.cloudfoundry.org/stemcells/");
 	 	
 	 	 //다른 곳 클릭 시 popover hide 이벤트
 	 	$('.w2ui-popup').on('click', function (e) {
 	 	    $('[data-toggle="popover"]').each(function () {
 	 	        //the 'is' for buttons that trigger popups
 	 	        //the 'has' for icons within a button that triggers a popup
 	 	        if (!$(this).is(e.target) && $(this).has(e.target).length === 0 && $('.popover').has(e.target).length === 0) {
 	 	            $(this).popover('hide');
 	 	        }
 	 	    });
 	 	});
 	});
 	
 	/**************************************************************z
 	 * 설명 스템셀 삭제 버튼 클릭
 	 **************************************************************/
 	$('#doDelete').click(function(){
		if($("#doDelete").attr('disabled') == "disabled") return;
		var selected = w2ui['config_opStemcellsGrid'].getSelection();
		var record = w2ui['config_opStemcellsGrid'].get(selected);
		var message = "";
		
		if ( record.stemcellFileName )
			message = "스템셀 (파일명 : " + record.stemcellFileName + ")를 삭제하시겠습니까?";
		else
			message = "선택된 스템셀을 삭제하시겠습니까?";
		w2confirm({
			title 	: "스템셀 삭제",
			msg		: message,
			yes_text: "확인",
			yes_callBack : function(event){
				deletePop(record);						
			},
			no_text : "취소",
			no_callBack	: function(){
				w2ui['config_opStemcellsGrid'].clear();
				initView();
			}
		});
 	})
 	
 	//  화면 초기화에 필요한 데이터 요청
 	initView();
});

/**************************************************************
 * 설명 공통코드 설정
 * Function : setCommonCode
 **************************************************************/
function setCommonCode(url, id) {
	jQuery.ajax({
		type : "get",
		url : url,
		async : false,
		error : function(xhr, status) {
			if(xhr.status==403){
				location.href = "/abuse";
			}else{
				var errorResult = JSON.parse(request.responseText);
				w2alert(errorResult, "스템셀 조회");
			}
		},
		success : function(data) {
			stemcellArray = new Array();
			if(data[0].parentCode == 200 && data[0].subGroupCode == null){
				data.map(function(obj) {
					stemcellArray.push(obj.codeName);
					osVersionArray.push(obj);
				});
				$(".w2ui-msg-body input[name='osList']").w2field('list', {items : stemcellArray,maxDropHeight : 200,width : 250});
			}else if(data[0].parentCode == 100){
				data.map(function(obj) {
					stemcellArray.push(obj.codeName);
				});
				$(".w2ui-msg-body input[name='iaasList']").w2field('list', {items : stemcellArray,maxDropHeight : 200,width : 250});
			}else if(data[0].parentCode == 200 && data[0].subGroupCode != null){
				
				data.map(function(obj) {
				stemcellArray.push(obj.codeName);
				});
				$('.w2ui-msg-body #osVersionList').removeAttr('disabled');
				$('.w2ui-msg-body #osVersionList').attr("placeholder","선택하세요.");
				$(".w2ui-msg-body input[name='osVersionList']").w2field('list', {items : stemcellArray,maxDropHeight : 200,width : 250});
			}
		}
	});
}

/**************************************************************
 * 설명 IaaS 유형 change Event
 * Function : setAwsType
 **************************************************************/
function setAwsType(value){
	if(value == "AWS"){
		if($('.w2ui-msg-body input:radio[name=fileType]:input[value=version]').is(':checked')==true){
			$('.w2ui-msg-body input:checkbox[name=awsLight]').attr("disabled", false);
		}
	}else {
		$('.w2ui-msg-body input:checkbox[name=awsLight]').attr("disabled", true);
		$('.w2ui-msg-body input:checkbox[name=awsLight]').attr('checked',false)
	}
}

/**************************************************************
 * 설명 Os 유형 change Event
 * Function : setOsVersion
 **************************************************************/
function setOsVersion(value){
	var subCodeValue = 0;
 	for(var i=0;i<osVersionArray.length;i++){
		if(value == osVersionArray[i].codeName){
			subCodeValue = osVersionArray[i].codeValue;
		}
	}
	setCommonCode('<c:url value="/common/deploy/codes/parent/"/>' + OS_TYPE_CODE + '<c:url value="/subcode/"/>' + subCodeValue, 'osVersion');
}

/**************************************************************
 * 설명 스템셀 다운 유형 change Event
 * Function : setRegistType
 **************************************************************/
function setRegistType(value){
	if(value == "file"){
		$('.w2ui-msg-body #browser').attr("disabled", false);
		$('.w2ui-msg-body input:text[name=stemcellPathUrl]').attr("readonly", true);
		$('.w2ui-msg-body input:text[name=stemcellPathVersion]').attr("readonly", true);
		$('.w2ui-msg-body input:checkbox[name=awsLight]').attr("disabled", true);
		$('.w2ui-msg-body input:text[name=stemcellPathUrl]').val("");
		$('.w2ui-msg-body input:text[name=stemcellPathVersion]').val("");
		$('.w2ui-msg-body input:checkbox[name=awsLight]').attr('checked',false)
	}else if(value == "url"){
		$('.w2ui-msg-body input:text[name=stemcellPathUrl]').attr("readonly", false);
		$('.w2ui-msg-body #browser').attr("disabled", true);
		$('.w2ui-msg-body input:text[name=stemcellPathVersion]').attr("readonly", true);
		$('.w2ui-msg-body input:checkbox[name=awsLight]').attr("disabled", true);
		$('.w2ui-msg-body input:text[name=stemcellPathVersion]').val("");
		$('.w2ui-msg-body input:checkbox[name=awsLight]').attr('checked',false)
		$('.w2ui-msg-body input:text[name=stemcellPathFileName]').val("");
	}else if(value == "version"){
			$('.w2ui-msg-body input:text[name=stemcellPathVersion]').attr("readonly", false);
			$('.w2ui-msg-body #browser').attr("disabled", true);
			$('.w2ui-msg-body input:text[name=stemcellPathUrl]').attr("readonly", true);
			$('.w2ui-msg-body input:checkbox[name=awsLight]').attr("disabled", true);
			$('.w2ui-msg-body input:text[name=stemcellPathUrl]').val("");
			$('.w2ui-msg-body input:text[name=stemcellPathFileName]').val("");
			if($(".w2ui-msg-body input[name='iaasList']").val()=="AWS"){
				$('.w2ui-msg-body input:checkbox[name=awsLight]').attr("disabled", false);
			}
	}else{
		w2alert(errorResult, "잘못 된 요청 입니다.");
	}
}

/**************************************************************
 * 설명 초기 스템셀 조회
 * Function : doSearch
 **************************************************************/
function doSearch(){
	w2ui['config_opStemcellsGrid'].load('/config/stemcell/publicStemcells');
}

/********************************************************
 * 설명		: 스템셀 파일 정보
 * Function	: setstemcellFilePath
 *********************************************************/
function setstemcellFilePath(fileInput){
	var file = fileInput.files;
	var files = $('.w2ui-msg-body #stemcellPathFile')[0].files;
	$(".w2ui-msg-body input[name='stemcellSize']").val(files[0].size);
	$(".w2ui-msg-body input[name=stemcellPath]").val(files[0].name);
	$(".w2ui-msg-body #stemcellPathFileName").val(files[0].name);
	
}

/********************************************************
 * 설명		: 스템셀 브라우저 선택
 * Function	: openBrowse
 *********************************************************/
function openBrowse(){
	if($('.w2ui-msg-body #browser').attr('disabled') == "disabled") return;	
	$(".w2ui-msg-body input[name='stemcellPathFile[]']").click();
}

/**************************************************************
 * 설명 스템셀 정보 저장 Array 설정
 * Function : stemcellRegist
 **************************************************************/
function stemcellRegist(){
	var stemcellInfo = {
			id			   : $(".w2ui-msg-body input[name='id']").val(),
			stemcellName   : $(".w2ui-msg-body input[name='stemcellName']").val(),
			stemcellFileName : $(".w2ui-msg-body input[name=stemcellPathFileName]").val(),
			stemcellUrl   : $(".w2ui-msg-body input[name='stemcellPathUrl']").val(),
			stemcellVersion   : $(".w2ui-msg-body input[name='stemcellPathVersion']").val(),
			osName         : $(".w2ui-msg-body input[name='osList']").val(),
			osVersion      : $(".w2ui-msg-body input[name='osVersionList']").val(),
			iaasType       : $(".w2ui-msg-body input[name='iaasList']").val(),
			fileType       : $(".w2ui-msg-body :radio[name='fileType']:checked").val(), 
			overlayCheck   : $(".w2ui-msg-body :checkbox[name='overlay']").is(':checked'),
			stemcellSize    : $(".w2ui-msg-body input[name='stemcellSize']").val(),
			awsLight       : $(".w2ui-msg-body :checkbox[name='awsLight']").is(':checked'),
			downloadStatus : ""
	}
	
	if(stemcellInfo.fileType == "file"){
		if($(".w2ui-msg-body input[name='stemcellSize']").val() == 0){
	 		w2alert("스템셀 파일을 찾을 수 없습니다. 확인해주세요.", "스템셀 파일 업로드");
	 		return false;
	 	}
	}
	stemcellInfoSave(stemcellInfo);
}

/**************************************************************
 * 설명 스템셀 정보 저장
 * Function : stemcellInfoSave
 **************************************************************/
function stemcellInfoSave(stemcellInfo){
	lock( '등록 중입니다.', true);
	if(popupValidation()){
		$.ajax({
			type : "POST",
			url : "/config/stemcell/regist/savestemcell/N",
			contentType : "application/json",
			async : true,
			data : JSON.stringify(stemcellInfo),
			success : function(data, status) {
				w2popup.close();
				stemcellInfo.id = data.id;
				stemcellInfo.downloadStatus = data.downloadStatus;
				stemcellInfo.stemcellFileName = data.stemcellFileName;
				initView();//재조회
				if(stemcellInfo.fileType == "file"){
					stemcellFileUpload(stemcellInfo);		
				}else if(stemcellInfo.fileType == "url"){
					stemcellFileDownload(stemcellInfo);
				}else if (stemcellInfo.fileType == "version"){
					stemcellFileDownload(stemcellInfo);
				}else{
					w2alert("잘못된 스템셀 등록 방식 입니다.");
				}
			}, error : function(request, status, error) {
				w2popup.unlock();
				var errorResult = JSON.parse(request.responseText);
				w2alert(errorResult.message);
			}
		});
	}
}

/**************************************************************
 * 설명 로컬에 있는 스템셀 업로드
 * Function : stemcellFileUpload
 **************************************************************/
function stemcellFileUpload(stemcellInfo){
	var form = $(".w2ui-msg-body #settingForm");
	var formData = new FormData(form);
	var files = $('.w2ui-msg-body #stemcellPathFile')[0].files;
	formData.append("file", files[0]);
	formData.append("overlay", stemcellInfo.overlayCheck);
	formData.append("id", stemcellInfo.id);
	formData.append("fileSize", files[0].size);
	
	if(stemcellInfo.id == 'undefined' || stemcellInfo.id==null || stemcellInfo.id=="" ){
		alert(stemcellInfo.id);
		return;
	}
	
	if(files[0].size == 0){
 		w2alert("스템셀 파일을 찾을 수 없습니다. 확인해주세요.", "스템셀 파일 업로드");
 		return false;
 	}
	
	$.ajax({
        type:'POST',
        url: '/config/stemcell/regist/upload',
        enctype : 'multipart/form-data',
 		dataType: "text",
 		async : true,
        data:formData,
        xhr: function() {
				var myXhr = $.ajaxSettings.xhr();
				myXhr.onreadystatechange = function () {}
				myXhr.upload.onprogress = function(e) {
					
					if (e.lengthComputable) {
						var max = e.total;
						var current = e.loaded;
		
						var Percentage = parseInt((current * 100) / max);
						if (Percentage == 1) {
				        	if(stemcellInfo.downloadStatus == "DOWNLOADED"  ){
				        		$("#downloaded_"+ stemcellInfo.id).wrap('<div class="btn" id="isExisted_'+stemcellInfo.id+'" style="position: relative;width:100px;"></div>');
				        		$("div").remove(stemcellInfo.id);
							} else if(  stemcellInfo.downloadStatus == 'DOWNLOADING'  ){
								$("#downloading_"+ stemcellInfo.id).wrap('<div class="btn" id="isExisted_'+stemcellInfo.id+'" style="position: relative;width:100px;"></div>');
				    			$("div").remove(stemcellInfo.id);
							}
				        	$("#isExisted_" + stemcellInfo.id).html(progressBarDiv);
				        	
						} else if (Percentage == 100){ 
								Percentage = 99;
							};
						$("#isExisted_"+ stemcellInfo.id + " .progress .progress-bar")
								.css({ "width" : Percentage + "%", "padding-top" : "5px", "text-align" : "center"}).text(Percentage + "%");
					}
				}
				return myXhr;
			},
			cache : false,
			contentType : false,
			processData : false,
			success : function(data) {
				$("#isExisted_" + stemcellInfo.id + " .progress .progress-bar")
						.css({ "width" : "100%", "padding-top" : "5px", "text-align" : "center" }).text("100%");
				doSearch();
			},
			error : function(data) {
			}
		});
}

/**************************************************************
 * 설명 원격지에 있는 스템셀 다운로드
 * Function : stemcellFileDownload
 **************************************************************/
var fail_count = 0;
function stemcellFileDownload(stemcellInfo){
	lock( '다운로드 중입니다.', true);
	var socket = new SockJS("<c:url value='/config/stemcell/regist/stemcellDownloading'/>");
	downloadClient = Stomp.over(socket); 
	var status = 0;
	
	var downloadPercentage = 0;
	downloadClient.heartbeat.outgoing = 50000;
	downloadClient.heartbeat.incoming = 0;
	downloadClient.connect({}, function(frame) {
		downloadClient.subscribe('/user/config/stemcell/regist/download/logs', function(data){
			w2popup.unlock();
			status = data.body.split('/')[1]; //recid/percent 중 percent
        	id = data.body.split('/')[0]; //recid/percent 중 recid
        	if(  stemcellInfo.downloadStatus == 'DOWNLOADING' &&  downloadStatus == ""){
        		downloadStatus ="PROCESSING";
				$("#downloading_"+id).wrap('<div class="btn" id="isExisted_'+ id+'" style="position: relative;width:100px;"></div>');
				$("#downloading_"+id).remove();
				$("#isExisted_" + id).html(progressBarDiv);
			}
        	
        	console.log("### Download Status ::: " + status.split("%")[0]);
        	
        	if ( Number(status.split("%")[0]) < 100 ) {
       		 $("#isExisted_" + id+ " .progress .progress-bar")
       		 	.css({"width": status
       		 		, "padding-top": "5px"
       		 		, "text-align": "center"	
       		 	}).text( status );
		    }else if( status == "done") {
		    	downloadStatus = '';
		    	$("#isExisted_" + id).parent().html(completeButton);
		    	downloadClient.disconnect();
		    	doSearch();
		    }
		});
		downloadClient.send("<c:url value='/send/config/stemcell/regist/stemcellDownloading'/>", {}, JSON.stringify(stemcellInfo));
	}, function(frame){
		fail_count ++;
		console.log("request reConnecting.... fail_count: " + fail_count);
		downloadClient.disconnect();
		if( fail_count < 10 ){
			socketDwonload(stemcellInfo);	
		}else{
			w2alert("스템셀 다운로드에 실패하였습니다. ", "스템셀 다운로드")
			fail_count = 0;
			var requestParameter = {
					id : stemcellInfo.id,
					stemcellFileName : stemcellInfo.stemcelleFileName
				};
		}
	});
}

/**************************************************************
 * 설명 스템셀 삭제 버튼 클릭 후 확인 팝업 화면
 * Function : deletePop
 **************************************************************/
function deletePop(record){
	var requestParameter = {
			id : record.id,
			stemcellFileName : record.stemcellFileName
		};
	$.ajax({
		type : "DELETE",
		url : "/config/stemcell/deletePublicStemcell",
		contentType : "application/json",
		async : true,
		data : JSON.stringify(requestParameter),
		success : function(data, status) {
			w2ui['config_opStemcellsGrid'].clear();
			initView();
		}, error : function(request, status, error) {
			w2popup.unlock();
			var errorResult = JSON.parse(request.responseText);
			w2alert(errorResult.message);
			w2ui['config_opStemcellsGrid'].clear();
			doSearch();
		}
	});
}


/********************************************************
 * 설명		: 그리드 재조회
 * Function	: gridReload
 *********************************************************/
function gridReload() {
	w2ui['config_opStemcellsGrid'].reset();
	doSearch();
}

/********************************************************
 * 설명		: 다른 페이지 이동 시 호출
 * Function	: clearMainPage
 *********************************************************/
function clearMainPage() {
	$().w2destroy('config_opStemcellsGrid');
}

 /********************************************************
 * 설명		: Lock 실행
 * Function	: clearMainPage
 *********************************************************/
function lock(msg) {
	w2popup.lock(msg, true);
}

/********************************************************
 * 설명 :  화면 변환 시
 *********************************************************/
$(window).resize(function() {
	setLayoutContainerHeight();
});


/**************************************************************
 * 설명 : 정보 조회
 * Function : initView
 **************************************************************/
function initView() {
	$('#doDelete').attr('disabled', true);
	doSearch();
}
</script>
<style type="text/css">
#stemcellPathFile { display:none; } 
</style>
<div id="main">
	<div class="page_site">환경설정 및 관리 > <strong>스템셀 관리</strong></div>
	
	<!-- OpenPaaS 스템셀 목록-->
	
	<div class="pdt20">
		<div class="title fl">스템셀 목록</div>
		<div class="fr"> 
			<!-- Btn -->
			<sec:authorize access="hasAuthority('CONFIG_STEMCELL_REGIST')">
			<span id="doRegist" class="btn btn-primary" style="width:120px" >등록</span>
			</sec:authorize>
			<sec:authorize access="hasAuthority('CONFIG_STEMCELL_DELETE')">
			<span id="doDelete" class="btn btn-danger" style="width:120px" >삭제</span>
			</sec:authorize>
			<!-- //Btn -->
		</div>
	</div>
	
	<!-- 그리드 영역 -->
	<div id="config_opStemcellsGrid" style="width:100%; height:718px"></div>
	
	<!-- 스템셀 등록 팝업 -->
	<form id="settingForm" action="POST" style="padding:5px 0 5px 0;margin:0;">
		<div id="regPopupDiv" hidden="true">
			<input name="stemcellSize" type="hidden" />
			<input name="id" type="hidden" />
			<div class="panel panel-info" style ="margin-top:20px;">	
				<div class="panel-heading"><b>스템셀 기본 정보</b></div>
				<div class="panel-body" style="padding:5px 5% 10px 5%;height:145px;">
					<div class="w2ui-field">
						<label style="width:30%;text-align: left;padding-left: 20px;">스템셀 명</label>
						<div style="width: 70%;">
							<input name="stemcellName" type="text" maxlength="100" style="width: 250px" required placeholder="스템셀 명을 입력 하세요."  />
						</div>
					</div>
					<div class="w2ui-field" >
							<label style="width:30%;text-align: left;padding-left: 20px;">IaaS 유형</label>
							<div style="width: 70%">
								<input name="iaasList" onchange='setAwsType(this.value);' type="list" style="float: left; width: 60%;" required placeholder="선택하세요." />
							</div>
					</div>
					<div class="w2ui-field">
							<label style="width:30%;text-align: left;padding-left: 20px;">OS 유형</label>
							<div style="width: 70%">
								<input name="osList" onchange='setOsVersion(this.value);' type="list" style="float: left; width: 60%;" required placeholder="선택하세요." />
							</div>
					</div>
					<div class="w2ui-field">
							<label style="width:30%;text-align:left;padding-left: 20px;">OS 버전</label>
							<div style="width: 70%">
								<input name="osVersionList" id="osVersionList"  disabled="disabled" type="list" style="float: left; width: 60%;" required placeholder="OS 유형을 선택하세요." />
							</div>
					</div>
				</div>
			</div>
			<input name="id" type="hidden" />
			<div class="panel panel-info" >	
				<div class="panel-heading"><b>스템셀 다운 유형</b></div>
				<div class="panel-body" style="padding:5px 5% 10px 5%;height:210px;">
					<div class="w2ui-field" style="margin: 8px 0px 0px 0px;" >
			        <label style="width:30%;text-align: left;padding-left: 15px;"><input type="radio" name="fileType" value="file" onchange='setRegistType(this.value);'/>&nbsp;&nbsp;로컬에서 선택</label>
			        	<div>
				        	<span>
				        	<input type="file" name="stemcellPathFile[]" id="stemcellPathFile" onchange="setstemcellFilePath(this);" hidden="true"/>
				        	<input type="text" id="stemcellPathFileName" name="stemcellPathFileName" style="width:53%;" readonly  onClick="openBrowse();" placeholder="업로드할 stemcell 파일을 선택하세요."/>
				        	<span class="btn btn-primary" id = "browser" onClick="openBrowse();" disabled style="height: 25px; padding: 1px 7px 7px 6px;">Browse </span>&nbsp;&nbsp;&nbsp;
				        	</span>
			        	</div>
			        </div>
			         <div class="w2ui-field" style="margin: 8px 0px 0px 0px;">
			          <img alt="stemcellVersion-help-info" class="stemcell-info" style="width:18px; position:absolute; left:25%; margin-top:10px" data-toggle="popover" title="공개 스템셀 참조 사이트"  data-html="true" src="../images/help-Info-icon.png">	
			         <label style="width:30%;text-align: left;padding-left: 15px;"><input type="radio" name="fileType" value="url" onchange='setRegistType(this.value);'/>&nbsp;&nbsp;스템셀 Url</label>
			        	<div>
			        		<input type="text" id="stemcellPathUrl" name="stemcellPathUrl" style="width:53%;" readonly   placeholder="스템셀 다운로드 Url을 입력 하세요."/>
						</div>			        
			        </div>
			         <div class="w2ui-field" style="margin: 8px 0px 0px 0px;">
			         <label style="width:30%;text-align: left;padding-left: 15px;"><input type="radio" name="fileType" value="version" onchange='setRegistType(this.value);' />&nbsp;&nbsp;스템셀 Version</label>
			        	<div>
			        		<input type="text" id="stemcellPathVersion" name="stemcellPathVersion" style="width:53%;" readonly   placeholder="스템셀 다운로드 버전을 입력 하세요."/>
			        	</div>
			        	<div>
			        		<input name="awsLight" type="checkbox" value="awsLight" disabled/>&nbsp;Lite 유형
			        	</div>
			        </div>
			         <div class="w2ui-field" style="margin: 8px 0px 0px 0px;">
			          <label style="width:30%;text-align: left;padding-left: 15px;">파일 덮어 쓰기</label>
			         	 <div>
			        		<input name="overlay" type="checkbox" value="overlay" checked/>&nbsp;
			        	</div>
			         </div>
				</div>
			</div>
		</div>
		<div id="regPopupBtnDiv" hidden="true">
			<button class="btn" id="registBtn" onclick="stemcellRegist()">등록</button>
			<button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
		</div>
	<!-- //스템셀 등록 팝업 -->
	</form>	
	
	
</div>