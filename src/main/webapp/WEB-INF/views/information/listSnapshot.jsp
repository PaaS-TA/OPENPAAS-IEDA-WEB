<%
/* =================================================================
 * 작성일 : 2016.09
 * 작성자 : 지향은
 * 상세설명 : 스냅샷 관리
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016.12       이동현        목록 화면 개선 및 코드 버그 수정
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<script type="text/javascript">
var bDefaultDirector;
$(function() {
	/************************************************
 	 * 설명: 기본 설치 관리자 정보 조회
 	************************************************/
 	bDefaultDirector = getDefaultDirector("<c:url value='/common/use/director'/>");
	
 	/************************************************
 	 * 설명: 스냅샷 조회 그리드
 	************************************************/
 	$('#us_snapshotGrid').w2grid({
		name: 'us_snapshotGrid',
		header: '<b>스냅샷 조회</b>',
		style	: 'text-align:center',
		method	: 'GET',
		msgAJAXerror : '스냅샷 조회 실패',
		multiSelect: false,
		show: {	
			selectColumn: true,
			footer: true},
		style: 'text-align: center',
		columns	: [
					 {field: 'recid', 	caption: 'recid', hidden: true}
				   , {fiello: 'deploymentName', caption: 'deploymentName', hidden: true}
		       	   , {field: 'job', caption: 'JobName', size: '180px', style: 'text-align:center'}
		       	   , {field: 'uuid', caption: 'Uuid', size: '350', style: 'text-align:center'}
		       	   , {field: 'snapshot_cid', caption: 'SnapshotCid', size: '240px', style: 'text-align:center'}
		       	   , {field: 'created_at', caption: 'CreatedAt', size: '180px', style: 'text-align:center'}
		       	   , {field: 'clean', caption: 'Clean', size: '120px', style: 'text-align:center'}
		       	],
       	onSelect: function(event) {
			var grid = this;
			event.onComplete = function() {
				$('#deleteSnapshot').attr('disabled', false);
				$('#deleteAllSnapshot').attr('disabled', false);
			}
		},
		onUnselect: function(event) {
			event.onComplete = function() {
				$('#deleteAllSnapshot').attr('disabled', false);
				$('#deleteSnapshot').attr('disabled', true);
			}
		},
		onLoad:function(event){
			if(event.xhr.status == 403){
				location.href = "/abuse";
				event.preventDefault();
			}
			if(event.status=="nocontent"){
				$('#deleteAllSnapshot').attr('disabled', true);
				return;
			}
			event.onComplete = function() {
				$('#deleteAllSnapshot').attr('disabled', false);
			}
		}, onError:function(evnet){
		}
	});
 	//초기 설정
 	initView(bDefaultDirector);
 	
 	/************************************************
 	 * 설명: 스냅샷 조회
 	************************************************/
 	$("#doSearch").click(function(){
 		doSearch($("#deployments").val());
 	});
});

/********************************************************
 * 설명			: 초기 설정
 * Function	: initView
 *********************************************************/
function initView(bDefaultDirector) {
	 if ( bDefaultDirector ) {
		getDeploymentList();
	}else{
		$("#deployments").html("<option selected='selected' disabled='disabled' value='all' style='color:red'>기본 설치자가 존재 하지 않습니다.</option>");
	}
	$('#deleteAllSnapshot').attr('disabled', true);
	$('#deleteSnapshot').attr('disabled', true);
}

/********************************************************
 * 설명			: 배포 조회
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
				optionString = "<option selected='selected' disabled='disabled'  value='all' style='color:gray'>조회할 배포명을 선택하세요.(필수)</option>";
				for (i = 0; i < data.contents.length; i++) {
					optionString += "<option value='" + data.contents[i].name + "' >";
					optionString += data.contents[i].name;
					optionString += "</option>\n";
				}
			}else{
				optionString = optionString = "<option selected='selected' disabled='disabled' value='' style='color:red'>조회할 배포명이 없습니다.</option>";
			}
			$object.html(optionString);
		}
	});
}
/********************************************************
 * 설명			: 스냅샷 조회
 * Function	: doSearch
 *********************************************************/
function doSearch(deployment) {
	if( deployment != null ){
		w2ui['us_snapshotGrid'].load("<c:url value='/info/snapshot/list/'/>"+deployment);	
	}else{
 		if(!bDefaultDirector){
			w2alert("기본 설치 관리자를 등록해 주세요. ","스냅샷 조회");
		}else{
			w2alert("배포명을 선택해주세요. ","스냅샷 조회");
		}
	}
}

/********************************************************
 * 설명			: 스냅샷 삭제 정보
 * Function	: deleteSnapshotInfo
 *********************************************************/
function deleteSnapshotInfo(type){
	 if( type == "part" && $("#deleteSnapshot").attr('disabled') == "disabled" ){
		 return;
	 } else if($("#deleteAllSnapshot").attr('disabled') == "disabled") {
		 return;
	 }
	 
	 w2ui['us_snapshotGrid'].lock("스냅샷 삭제 중입니다.", {
			spinner: true, opacity : 1
		});
	 var snaspshotParam = "";
	 if( type == 'part' ){
		 var selected = w2ui['us_snapshotGrid'].getSelection();
		 if ( selected == "" || selected == null) return;
		 var record = w2ui['us_snapshotGrid'].get(selected);
		 if ( record == "" || record == null) return;
		 snapshotParam = {
				deploymentName: record.deploymentName,
				snapshot_cid	: record.snapshot_cid
		 }
	 }else{
		 snapshotParam = {
				deploymentName: $("#deployments").val()
		 }
	 }
	 deleteSnapshot(type, snapshotParam );
}

/********************************************************
 * 설명			: 스냅샷 삭제
 * Function	: deleteSnapshot
 *********************************************************/
function deleteSnapshot(type, snapshotParam){
	$.ajax({
		type : "DELETE",
		url : "/info/snapshot/delete/"+ type,
		data : JSON.stringify(snapshotParam),
		contentType : "application/json",
		success : function(data, status) {
			w2alert("스냅샷을 삭제하였습니다.");
			w2ui['us_snapshotGrid'].clear();
			doSearch(snapshotParam.deploymentName);
			if(type == "all"){
				$('#deleteAllSnapshot').attr('disabled', true);
				$('#deleteSnapshot').attr('disabled', true);
			}else{
				$('#deleteSnapshot').attr('disabled', true);
			}
		},
		error : function(request, status, error) {
			w2popup.unlock();
			var errorResult = JSON.parse(request.responseText);
			w2alert(errorResult.message, "스냅샷 삭제");
		}
		
	});
}
/********************************************************
 * 설명			: 다른페이지 이동시 호출
 * Function	: clearMainPage
 *********************************************************/
function clearMainPage() {
	$().w2destroy('us_snapshotGrid');
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
	<div class="page_site">정보조회 > <strong>스냅샷 정보 조회</strong></div>

	<!-- 설치 관리자 -->
	<div id="isDefaultDirector"></div>
	
	<div class="pdt20"> 
		<div class="search_box" align="left" style="padding-left:10px;">
			<label  style="font-size:11px">배포명</label>
			&nbsp;&nbsp;&nbsp;
			<select name="select" id="deployments"  class="select" style="width:300px">
			</select>
			&nbsp;&nbsp;&nbsp;
			<span id="doSearch" class="btn btn-info" style="width:50px" >조회</span>
		</div>
		
		<div class="title fl">스냅샷 목록</div>
		<div class="fr"> 
			<!-- Btn -->
			<sec:authorize access="hasAuthority('INFO_SNAPSHOT_DELETE')">
			<span class="btn btn-primary" style="width:120px" id="deleteSnapshot" onclick="deleteSnapshotInfo('part');">스냅샷 삭제</span>
			</sec:authorize>
			<sec:authorize access="hasAuthority('INFO_SNAPSHOT_DELETE')">
			<span class="btn btn-danger" style="width:120px" id="deleteAllSnapshot" onclick="deleteSnapshotInfo('all');">스냅샷 전체 삭제</span>
			</sec:authorize>
			<!-- //Btn -->
		</div>
		<div id="us_snapshotGrid" style="width:100%; height:533px"></div>	
	</div>
</div>
