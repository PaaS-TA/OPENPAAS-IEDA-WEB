<%
/* =================================================================
 * 작성일 : 2016-06
 * 작성자 : 지향은
 * 상세설명 : 릴리즈 조회 및 등록
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016-12       지향은      릴리즈 관리 코드 버그 수정
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

var releaseTyps = "";
var completeButton = '<div><div class="btn btn-success btn-xs" style="width:100px; padding:3px;">Downloaded</div></div>';
var downloadingButton = '<div class="btn btn-info btn-xs" style="width:100px;">Downloading</div>';
var progressBarDiv = '<div class="progress">';
	progressBarDiv += '<div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" >';
	progressBarDiv += '</div></div>';
	
$(function() {
	
	/********************************************************
	 * 설명 :  릴리즈 목록 조회 Grid 생성
	 *********************************************************/
	$('#config_releaseGrid').w2grid({
		name: 'config_releaseGrid',
		header: '<b>릴리즈 등록</b>',
		method: 'GET',
		multiSelect: false,
		show: {	selectColumn: true, footer: true},
		style: 'text-align:center',
		columns:[
			 {field: 'recid', caption: 'recid', hidden: true}
			,{field: 'id', caption: 'id', hidden: true}
			,{field: 'releaseName', caption: '릴리즈 명', size: '15%', style:'text-align:left; padding-left:10px' }			
			,{field: 'releaseType', caption: '릴리즈 유형', size: '10%'}
			,{field: 'releaseFileName', caption: '릴리즈 파일명', size: '20%', style:'text-align:left;  padding-left:10px'}
			,{field: 'releaseSize', caption: '릴리즈 파일 크기', size: '7%'}
			,{field: 'downloadStatus', caption: '다운로드 여부', size: '10%',
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
		onSelect : function(event) {
			event.onComplete = function() {
				$('#doDelete').attr('disabled', false);
				return;
			}
		},
		onUnselect: function(event) {
			event.onComplete = function() {
				$('#doDelete').attr('disabled', true);
			}
		},onLoad:function(event){
			if(event.xhr.status == 403){
				location.href = "/abuse";
				event.preventDefault();
			}
		},
		onError : function(event) {
		}
	});
	
	/********************************************************
	 * 설명 :  릴리즈 등록 팝업
	 *********************************************************/
 	$("#doRegist").click(function(){
 		w2popup.open({
 			title 	: "<b>릴리즈 등록</b>",
 			width 	: 600,
 			height	: 385,
 			modal	: true,
 			body	: $("#regPopupDiv").html(),
 			buttons : $("#regPopupBtnDiv").html(),
 			onClose : function(event){
 				doSearch();
 			}
 		});
 		$('.w2ui-msg-body input:radio[name=fileType]:input[value=url]').attr("checked", true);
 		//릴리즈 유형 조회
 		$('[data-toggle="popover"]').popover();
 	 	//스템셀 버전 정보
 	 	$(".release-info").attr('data-content', "http://bosh.io/releases");
 		getReleaseTyps();
 		changReleasePathType("url");
 		
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
	
	/********************************************************
	 * 설명 :  릴리즈 삭제 팝업
	 *********************************************************/
	$("#doDelete").click(function(){
		if($("#deleteBtn").attr('disabled') == "disabled") return;
		var selected = w2ui['config_releaseGrid'].getSelection();
		var record = w2ui['config_releaseGrid'].get(selected);
		var message = "";
		
		if ( record.releaseFileName )
			message = "릴리즈 (파일명 : " + record.releaseFileName + ")를 삭제하시겠습니까?";
		else
			message = "선택된 릴리즈를 삭제하시겠습니까?";
		
		w2confirm({
			title 	: "릴리즈 삭제",
			msg		: message,
			yes_text: "확인",
			yes_callBack : function(event){
					deletePop(record);						
			},
			no_text : "취소",
			no_callBack	: function(){
				initView();
			}
		});
	});
	
 	initView();
	
});


/******************************************************************
 * Function : getReleaseTyps
 * 설명		  : 릴리즈 유형 조회
 ***************************************************************** */
function getReleaseTyps() {
	$.ajax({
		type : "GET",
		url : "/config/systemRelease/list/releaseType",
		contentType : "application/json",
		async : true,
		success : function(data, status) {
			releaseTyps = new Array();
			if( data != null){
				data.map(function(obj) {
					releaseTyps.push(obj);
				});
			}
			$(".w2ui-msg-body input[name='releaseType']").w2field('list', {items : releaseTyps,maxDropHeight : 200,width : 250});
		},
		error : function(e, status) {
			w2popup.unlock();
			w2alert("Release 유형을 가져오는데 실패하였습니다.", "릴리즈 유형");
		}
	});
}

/********************************************************
 * 설명		: 화면 초기화
 * Function	: initView
 *********************************************************/
function initView() {
	 downloadStatus = "";
	doSearch();
	$('#doDelete').attr('disabled', true);
}

/********************************************************
 * 설명		: 릴리즈 목록 조회
 * Function	: doSearch
 *********************************************************/
function doSearch() {
	w2ui['config_releaseGrid'].load('/config/systemRelease/list');
}

/********************************************************
 * 설명		: 릴리즈 Path 선택
 * Function	: changReleasePathType
 *********************************************************/
function changReleasePathType(type){
	
	var keyPathDiv = $('.w2ui-msg-body #releasePathDiv');
	
	//릴리즈 파일 업로드
	var fileUploadInput = '<span><input type="file" name="releasePathFile[]" id="releasePathFile" onchange="setReleaseFilePath(this);"   hidden="true"/>';
	fileUploadInput += '<input type="text" id="releasePathFileName" name="releasePathFileName" style="width:70%;" readonly  onClick="openBrowse();" placeholder="업로드할 release 파일을 선택하세요."/>';
	fileUploadInput += '<a href="#" id="browse" onClick="openBrowse();">Browse </a></span>';
	fileUploadInput += '<div class="isMessage"></div>';
	
	//릴리즈 url 입력
	var selectInput = '<input type="text" name="releaseUrl" style="float:left; width:250px" onblur="setReleasePath(this.value);" onfocus="setReleasePath(this.value);" placeholder="release url을 입력하세요."/>';
	selectInput += '<div class="isMessage"></div>';
	//목록에서 선택
	if(type == "url") {
		keyPathDiv.html(selectInput);		
	}else{
		//파일업로드
		keyPathDiv.html(fileUploadInput);
		$(".w2ui-msg-body input[name='releasePathFile[]']").hide();		
	}
}

/********************************************************
 * 설명		: Release 브라우저 선택
 * Function	: openBrowse
 *********************************************************/
function openBrowse(){
	$(".w2ui-msg-body input[name='releasePathFile[]']").click();
}

/********************************************************
 * 설명		: Release Url 정보
 * Function	: setReleasePath
 *********************************************************/
function setReleasePath(value){
	$(".w2ui-msg-body input[name=releasePath]").val(value);
}

/********************************************************
 * 설명		: 릴리즈 파일 정보
 * Function	: setReleaseFilePath
 *********************************************************/
function setReleaseFilePath(fileInput){
	console.log(fileInput);
	var file = fileInput.files;
	console.log(file);
	var files = $('#releasePathFile')[0].files;
	console.log(files);
	$(".w2ui-msg-body input[name='releaseSize']").val(files[0].size);
	$(".w2ui-msg-body input[name=releasePath]").val(file[0].name);
	$(".w2ui-msg-body #releasePathFileName").val(file[0].name);
	
}

/********************************************************
 * 설명		: 릴리즈 등록 확인 버튼
 * Function	: releaseRegist
 *********************************************************/
function releaseRegist(){
	var releaseInfo = {
			id							: $(".w2ui-msg-body input[name='id']").val(),
			releaseName 		: $(".w2ui-msg-body input[name='releaseName']").val(),
			releaseType 			: $(".w2ui-msg-body input[name='releaseType']").val(),
			releaseFileName 	: $(".w2ui-msg-body input[name='releasePath']").val(),
			fileType	 				: $(".w2ui-msg-body :radio[name='fileType']:checked").val(),
			overlayCheck 			: $(".w2ui-msg-body :checkbox[name='overlay']").is(':checked'),
			releaseSize			: $(".w2ui-msg-body input[name='releaseSize']").val(),
			downloadStatus		: ""
	}
	if(releaseInfo.fileType == "file"){
		var files = $('#releasePathFile')[0].files;
		if(files[0].size == 0){
	 		w2alert("릴리즈 파일을 찾을 수 없습니다. 확인해주세요.", "릴리즈 파일 업로드");
	 		return false;
	 	}
		//file upload 하기 전 lock 파일 검사
		if(!lockFileSet(files[0].name)){
			return;
		}
	}

	// 데이터 저장 후 릴리즈 다운로드/업로드 
	releaseInfoSave(releaseInfo);
}

/********************************************************
 * 설명 		: lock 검사
 * Function 	: lockFileSet
 *********************************************************/
var lockFile = false;
function lockFileSet(releaseFile){
	var fileName  ="";
	if( releaseFile.indexOf(".") > -1 ){
		var pathHeader = releaseFile;
	 	var pathMiddle = releaseFile.lastIndexOf('.');
		var pathEnd = releaseFile.length;
		fileName = releaseFile.substring(pathHeader,pathMiddle)+"-download";
	}
	var message = "현재 다른 플랫폼 설치 관리자가 동일한 릴리즈를 등록 중 입니다."
	lockFile = commonLockFile("<c:url value='/common/deploy/lockFile/"+fileName+"'/>", message);
	return lockFile;
}


/********************************************************
 * 설명		: 공통 릴리즈 정보 저장
 * Function	: releaseInfoSave
 *********************************************************/
function releaseInfoSave(releaseInfo){
	lock( '등록 중입니다.', true);
	//유효성 검사
	if (popupValidation()){
		$.ajax({
			type : "POST",
			url : "/config/systemRelease/regist/N",
			contentType : "application/json",
			async : true,
			data : JSON.stringify(releaseInfo),
			success : function(data, status) {
				w2popup.close();
				releaseInfo.id = data.id; 
				releaseInfo.downloadStatus = data.downloadStatus;
				initView();//재조회
				if(releaseInfo.fileType == 'file'){
					releaseFileUpload(releaseInfo);			
				}else{
					socketDwonload(releaseInfo);	
				}
			}, error : function(request, status, error) {
				w2popup.unlock();
				var errorResult = JSON.parse(request.responseText);
				w2alert(errorResult.message);
			}
		});
	}
}


/********************************************************
 * 설명			: 릴리즈 다운로드
 * Function	: socketDwonload
 *********************************************************/
var fail_count = 0;
function socketDwonload(releaseInfo){
	lock( '다운로드 중입니다.', true);
	
	var socket = new SockJS("<c:url value='/config/systemRelease/regist/download/releaseDownloading'/>");
	downloadClient = Stomp.over(socket); 
	var status = 0;
	
	var downloadPercentage = 0;
	downloadClient.heartbeat.outgoing = 50000;
	downloadClient.heartbeat.incoming = 0;
	downloadClient.connect({}, function(frame) {
		downloadClient.subscribe('/user/config/systemRelease/regist/download/logs', function(data){
			w2popup.unlock();
	 		
			status = data.body.split('/')[1]; //recid/percent 중 percent
        	id = data.body.split('/')[0]; //recid/percent 중 recid
        	
        	if(  releaseInfo.downloadStatus == 'DOWNLOADING' &&  downloadStatus == ""){
        		downloadStatus ="PROCESSING";
				$("#downloading_"+ id).wrap('<div class="btn" id="isExisted_'+ id+'" style="position: relative;width:100px;"></div>');
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
		    	if(downloadClient != ""){
		    		downloadClient.disconnect();
		    		downloadClient = "";
		    	}
		    	doSearch();
		    }      		
		});
		downloadClient.send("<c:url value='/send/config/systemRelease/regist/download/releaseDownloading'/>", {}, JSON.stringify(releaseInfo));
	}, function(frame){
		fail_count ++;
		console.log("request reConnecting.... fail_count: " + fail_count);
		downloadClient.disconnect();
		if( fail_count < 10 ){
			socketDwonload(releaseInfo);	
		}else{
			w2alert("시스템 릴리즈 다운로드에 실패하였습니다. ", "시스템 릴리즈 다운로드")
			fail_count = 0;
			var requestParameter = {
					id : releaseInfo.id,
					releaseFileName : releaseInfo.releaseFileName
				};
				deleteRelease(requestParameter);
		}
	});
}

/********************************************************
 * 설명		: 릴리즈 파일 업로드
 * Function	: releaseFileUpload
 *********************************************************/
function releaseFileUpload(releaseInfo){
	var form = $(".w2ui-msg-body #settingForm");
	var formData = new FormData(form);

	var files = $('#releasePathFile')[0].files;
	formData.append("file", files[0]);
	formData.append("overlay", releaseInfo.overlayCheck);
	formData.append("id", releaseInfo.id);
	formData.append("fileSize", files[0].size);
	
	if(files[0].size == 0){
 		w2alert("릴리즈 파일을 찾을 수 없습니다. 확인해주세요.", "릴리즈 파일 업로드");
 		return false;
 	}
	
	$.ajax({
        type:'POST',
        url: '/config/systemRelease/regist/upload',
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
				        	if(releaseInfo.downloadStatus == "DOWNLOADED"  ){
				        		$("#downloaded_"+ releaseInfo.id).wrap('<div class="btn" id="isExisted_'+releaseInfo.id+'" style="position: relative;width:100px;"></div>');
				        		$("div").remove(releaseInfo.id);
							} else if(  releaseInfo.downloadStatus == 'DOWNLOADING'  ){
								$("#downloading_"+ releaseInfo.id).wrap('<div class="btn" id="isExisted_'+releaseInfo.id+'" style="position: relative;width:100px;"></div>');
				    			$("div").remove(releaseInfo.id);
							}
				        	$("#isExisted_" + releaseInfo.id).html(progressBarDiv);
				        	
						} else if (Percentage == 100){ 
								Percentage = 99;
							};
						$("#isExisted_"+ releaseInfo.id + " .progress .progress-bar")
								.css({ "width" : Percentage + "%", "padding-top" : "5px", "text-align" : "center"}).text(Percentage + "%");
						
					}
				}
				return myXhr;
			},
			cache : false,
			contentType : false,
			processData : false,
			success : function(data) {
				$("#isExisted_" + releaseInfo.id + " .progress .progress-bar")
						.css({ "width" : "100%", "padding-top" : "5px", "text-align" : "center" }).text("100%");
				doSearch();
			},
			error : function(data) {
			}
		});
	}

	 /********************************************************
	 * 설명		: 릴리즈 삭제 데이터 셋팅
	 * Function	: deletePop
	 *********************************************************/
	function deletePop(record) {
		var requestParameter = {
			id : record.id,
			releaseFileName : record.releaseFileName
		};
		deleteRelease(requestParameter);
	}
	
	 /********************************************************
	 * 설명		: 릴리즈 삭제 요청
	 * Function	: deleteRelease
	 *********************************************************/
	function deleteRelease(requestParameter){
		$.ajax({
			type : "DELETE",
			url : "/config/systemRelease/delete",
			data : JSON.stringify(requestParameter),
			contentType : "application/json",
			success : function(data, status) {
				if(  downloadClient != "") {
					downloadClient.disconnect();
					downloadClient ="";
				}
				initView();
			},
			error : function(request, status, error) {
				var errorResult = JSON.parse(request.responseText);
				w2alert(errorResult.message, "릴리즈 삭제");
				doSearch();
			}
		});
	}

	 /********************************************************
	 * 설명		: 그리드 재조회
	 * Function	: gridReload
	 *********************************************************/
	function gridReload() {
		w2ui['config_releaseGrid'].reset();
		doSearch();
	}

	 /********************************************************
	 * 설명		: 팝업 창을 닫을 경우
	 * Function	: initSetting
	 *********************************************************/
	function initSetting() {
		 if(  downloadClient != "") {
				downloadClient.disconnect();
				downloadClient ="";
		}
		 doSearch(); 
	}

	 /********************************************************
	 * 설명		: 다른 페이지 이동 시 호출
	 * Function	: clearMainPage
	 *********************************************************/
	function clearMainPage() {
		$().w2destroy('config_releaseGrid');
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
</script>
<div id="main">
	<div class="page_site">환경설정 및 환리 > <strong>릴리즈 관리</strong></div>
	
	<!-- OpenPaaS 릴리즈 목록-->
	<div class="pdt20">
		<div class="title fl">릴리즈 목록</div>
		<div class="fr"> 
			<!-- Btn -->
			<sec:authorize access="hasAuthority('CONFIG_RELEASE_REGIST')">
			<span id="doRegist" class="btn btn-primary" style="width:120px" >등록</span>
			</sec:authorize>
			<sec:authorize access="hasAuthority('CONFIG_RELEASE_DELETE')">
			<span id="doDelete" class="btn btn-danger" style="width:120px" >삭제</span>
			</sec:authorize>
			<!-- //Btn -->
		</div>
	</div>
	<!-- 릴리즈 grid -->
	<div id="config_releaseGrid" style="width:100%; height:718px"></div>	
		
	<!-- 릴리즈 등록 팝업 -->
	<form id="settingForm" action="POST" style="padding:5px 0 5px 0;margin:0;">
		<div id="regPopupDiv" hidden="true">
			<input name="releaseSize" type="hidden" />
			<input name="id" type="hidden" />
			<div class="panel panel-info" >	
				<div class="panel-heading"><b>릴리즈 등록</b></div>
				<div class="panel-body" style="padding:5px 5% 10px 5%;height:210px;">
					<div class="w2ui-field">
						<label style="width:30%;text-align: left;padding-left: 20px;">릴리즈 명</label>
						<div style="width: 70%;">
							<input name="releaseName" type="text" maxlength="100" style="width: 250px" required="required" />
							<div class="isMessage"></div>
						</div>
					</div>
					<div class="w2ui-field">
							<label style="width:30%;text-align: left;padding-left: 20px;">릴리즈 유형</label>
							<div style="width: 70%">
								<input name="releaseType" type="list" style="float: left; width: 60%;" required placeholder="선택하세요." />
							</div>
					</div>
					<div class="w2ui-field">
						<label style="width:30%;text-align: left;padding-left: 20px;">파	&nbsp;&nbsp;&nbsp;&nbsp;일</label>
						<img alt="release-help-info" class="release-info" style="width:18px; position:absolute; left:20%; margin-top:6px" data-toggle="popover" title="공개 릴리즈 참조 사이트"  data-html="true" src="../images/help-Info-icon.png">	
						<div style="width:70%">
							<span onclick="changReleasePathType('file');" style="width:30%;"><label><input type="radio" name="fileType" value="file" />&nbsp;로컬에서 선택</label></span>
							&nbsp;&nbsp;
							<span onclick="changReleasePathType('url');" style="width:30%;"><label><input type="radio" name="fileType" value="url" />&nbsp;릴리즈 URL</label></span>
							&nbsp;&nbsp;
							<label><input name="overlay" type="checkbox" value="overlay" checked />&nbsp;파일 덮어쓰기</label>
						</div>
					</div>
					<div class="w2ui-field">			         	
		                <input name="releasePath" type="text" style="width:250px;" hidden="true"/>
					    <label style="text-align: left;width:30%;font-size:11px;" class="control-label"></label>
						<div id="releasePathDiv" style="width:70%"></div>
			        </div>
				</div>
			</div>
		</div>
		<div id="regPopupBtnDiv" hidden="true">
			<button class="btn" id="registBtn" onclick="releaseRegist()">등록</button>
			<button class="btn" id="popClose"  onclick="w2popup.close();">취소</button>
		</div>
	<!-- //릴리즈 등록 팝업 -->
	</form>	
</div>