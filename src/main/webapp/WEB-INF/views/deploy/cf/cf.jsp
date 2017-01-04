<%
/* =================================================================
 * 작성일 : 
 * 작성자 : 
 * 상세설명 : CF 설치
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016.10       지향은           화면 수정 및 vSphere 클라우드 기능 추가
 * 2016.12       지향은           CF 목록과 팝업 화면 .jsp 분리 및 설치 버그 수정 
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<script type="text/javascript">
/******************************************************************
 * 										CF Init Setting
 ***************************************************************** */
var iaas = "";//iaas
var bDefaultDirector = ""; //기본 설치 관리자
var diegoUse=""; //diego 사용 유무
var installStep = 0;
var menu = "";
$(function() {
	
	/********************************************************
	 * 설명 :  기본 설치 관리자 정보 조회
	 *********************************************************/
	bDefaultDirector = getDefaultDirector("<c:url value='/common/use/director'/>");
	
	/********************************************************
	 * 설명 :  CF 목록 셋팅
	 *********************************************************/
	$('#config_cfGrid').w2grid({
		name: 'config_cfGrid',
		header: '<b>CF 목록</b>',
		method: 'GET',
 		multiSelect: false,
		show: {	
				selectColumn: true,
				footer: true},
		style: 'text-align: center',
		columns:[
		      {field: 'recid', 	caption: 'recid', hidden: true}
			, {field: 'id', caption: 'ID', hidden: true}
			, {field: 'deployStatus', caption: '배포상태', size: '80px', 
				render: function(record) {
		    			if ( record.deployStatus == 'DEPLOY_STATUS_DONE' )
		    				return '<span class="btn btn-primary" style="width:60px">성공</span>';
		    			else	if ( record.deployStatus == 'DEPLOY_STATUS_FAILED' )
		    				return '<span class="btn btn-danger" style="width:60px">오류</span>';
		    			else	if ( record.deployStatus == 'DEPLOY_STATUS_CANCELLED' )
		    				return '<span class="btn btn-primary" style="width:60px">취소</span>';
		    			else	if ( record.deployStatus == 'DEPLOY_STATUS_PROCESSING' )
		    				return '<span class="btn btn-primary" style="width:60px">배포중</span>';
	    				else	if ( record.deployStatus == 'DEPLOY_STATUS_DELETING' )
		    				return '<span class="btn btn-primary" style="width:60px">삭제중</span>';
						else
		    				return '&ndash;';
		    	   }
				}
			, {field: 'taskId', caption: 'TASK ID', size: '100px', hidden: true}
			//1.1 Deployment 정보
			, {field: 'deploymentName', caption: '배포명', size: '100px'}
			, {field: 'iaas', caption: 'IaaS', size: '100px'
				, render: function(record) {
					return record.iaas.toLowerCase();
				}
			}
			, {field: 'directorUuid', caption: '설치관리자 UUID', size: '280px'}
			, {field: 'release', caption: 'CF 릴리즈', size: '100px'
				, render:function(record){
						if( record.releaseName && record.releaseVersion ){
							return record.releaseName +"/"+ record.releaseVersion;
						}
					}
				}
			, {field: 'appSshFingerprint', caption: 'SSH 핑거프린트', size: '240px'}
			// 1.2 기본정보
			, {field: 'domain', caption: '도메인', size: '180px'}
			, {field: 'description', caption: '도메인 설명', size: '220px'}
			, {field: 'domainOrganization', caption: '도메인 그룹', size: '120px'}
			// 1.3 HA프록시 정보
			, {field: 'proxyStaticIps', caption: 'HAProxy 공인 IP', size: '120px'}
			// 4. 네트워크 정보
			, {field: 'subnetRange', caption: '서브넷 범위', size: '180px'}
			, {field: 'subnetGateway', caption: '게이트웨이', size: '100px'}
			, {field: 'subnetDns', caption: 'DNS', size: '100px'}
			, {field: 'subnetReservedIp', caption: '할당된 IP 대역', size: '240px' }
			, {field: 'subnetStaticIp', caption: 'VM 할당 IP대역', size: '240px'	}
			, {field: 'subnetId', caption: '네트워크 ID(포트 그룹명)', size: '140px'}
			, {field: 'cloudSecurityGroups', caption: '시큐리티 그룹명', size: '100px'}
			
			, {field: 'stemcell', caption: '스템셀', size: '240px'
				, render:function(record){
						if( record.stemcellName && record.stemcellVersion ){
							return record.stemcellName +"/"+ record.stemcellVersion;
						}
					}
				}
			, {field: 'createdDate', caption: '생성일자', size: '100px', hidden: true}
			, {field: 'updatedDate', caption: '수정일자', size: '100px', hidden: true}
			, {field: 'deploymentFile', caption: '배포파일명', size: '180px',
					render: function(record) {
						if ( record.deploymentFile != null ) {
		       				return '<a style="color:#333;" href="/common/deploy/download/manifest/' + record.deploymentFile +'" onclick="window.open(this.href); return false;">' + record.deploymentFile + '</a>';
						} else {
		    				return '&ndash;';
						}
					}
				}
			],
		onSelect : function(event) {
			event.onComplete = function() {
				if ( bDefaultDirector  != null) {
					$('#modifyBtn').attr('disabled', false);
					$('#deleteBtn').attr('disabled', false);
					return;
				}
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
	 * 설명 :  CF 설치 버튼
	 *********************************************************/
	$("#installBtn").click(function() {
		if($("#installBtn").attr('disabled') == "disabled") return;
		w2confirm({
			width 			: 550,
			height 			: 180,
			title 			: '<b>DIEGO 사용여부</b>',
			msg 			: $("#diegoSelectDiv").html(),
			modal			: true,
			yes_text 		: "확인",
			no_text 		: "취소",
			yes_callBack 	: function(){
 				//DIego 사용 여부
				diegoUse = $(".w2ui-msg-body input:radio[name='diegoSelect']:checked").val();
				if( iaas == "") {
					selectIaas(); return;
				}
				//cf Popup load
				$("#cfPopupDiv").load("/deploy/cf/install/cfPopup",function(event){
	 				installStep = 8;
	 				menu = "cf";
	 				defaultInfoPopup();
	 				return;
				});
			},
			no_callBack : function(event){
				gridReload();
			}
		});
	});
	
	/********************************************************
	 * 설명 :  Cf 수정 버튼
	 *********************************************************/
	$("#modifyBtn").click(function() {
		if($("#modifyBtn").attr('disabled') == "disabled") return;
		
		//cf & diego 통합 설치 사용 여부
		var selected = w2ui['config_cfGrid'].getSelection();
		var record = w2ui['config_cfGrid'].get(selected);
		if( record.cfDiegoInstall == 'Y' ){
			w2alert("CF & Diego 통합 설치 메뉴에서 사용중입니다. ","CF & Diego 통합 설치");
			return;
		}
		
		var message = "";

		if (record.deploymentName)
			message = "CF (배포명 : " + record.deploymentName + ")를 수정하시겠습니까?";
		else
			message = "선택된 CF를 수정하시겠습니까?";
		
		w2confirm({
			title : "CF 설치",
			msg : message,
			yes_text : "확인",
			yes_callBack : function(event) {
				var selected = w2ui['config_cfGrid'].getSelection();
				if (selected.length == 0) {
					w2alert("선택된 정보가 없습니다.", "CF 설치");
					return;
				} else {
					var record = w2ui['config_cfGrid'].get(selected);
					$("#cfPopupDiv").load("/deploy/cf/install/cfPopup",function(event){
						installStep = 8;
						menu ="cf";
						getCfData(record);
						return;
					});
				}
			},
			no_text : "취소",
			no_callBack : function(event){
				gridReload();
			}
		});
	});
	
	/********************************************************
	 * 설명 :  Cf 삭제 버튼
	 *********************************************************/
	$("#deleteBtn").click( function() {
		if($("#deleteBtn").attr('disabled') == "disabled") return;
		
		var selected = w2ui['config_cfGrid'].getSelection();
		var record = w2ui['config_cfGrid'].get(selected);

		var message = "";

		if (record.deploymentName)
			message = "CF (배포명 : " + record.deploymentName + ")를 삭제하시겠습니까?";
		else
			message = "선택된 CF를 삭제하시겠습니까?";

		w2confirm({
			title : "CF 삭제",
			msg : message,
			yes_text : "확인",
			yes_callBack : function(event) {
				$("#cfPopupDiv").load("/deploy/cf/install/cfPopup",function(event){
					deletePopup(record);
					return;
				});
			},
			no_text : "취소",
			no_callBack : function(event){
				gridReload();
			}
		});
	});
	doSearch();
});


/********************************************************
 * 설명 :  CF 조회
 * Function : doSearch
 *********************************************************/
function doSearch() {
	//iaas 추출
	var directorName = $("#directorName").text().toUpperCase();
	if( directorName.indexOf("_CPI") > 0  ) {
			var start = directorName.indexOf("(");
			var end = directorName.indexOf("_CPI)", start+1);
			iaas = directorName.substring(start+1, end)
	}
	if(iaas != ""){
	w2ui['config_cfGrid'].load("<c:url value='/deploy/cf/list/"+iaas+"'/>",
			function() { doButtonStyle(); });
	}else{
		doButtonStyle();
	}
}

/********************************************************
 * 설명 		:  Iaas Select Confirm
 * Function : selectIaas
 *********************************************************/
function selectIaas() {
	w2confirm({
		width : 500,
		height : 180,
		title : '<b>CF 설치</b>',
		msg : $("#bootSelectBody").html(),
		modal : true,
		yes_text : "확인",
		no_text : "취소",
		yes_callBack : function() {
			iaas = $(".w2ui-msg-body input:radio[name='structureType']:checked").val();
			if (iaas) {
				defaultInfoPopup();
			} else {
				w2alert("CF를 설치할 클라우드 환경을 선택하세요");
			}
		}
	});
}

/********************************************************
 * 설명		: 버튼 스타일 변경
 * Function	: doButtonStyle
 *********************************************************/
function doButtonStyle() {
	if ( !bDefaultDirector ) {
		$('#installBtn').attr('disabled', true);
		$('#modifyBtn').attr('disabled', true);
		$('#deleteBtn').attr('disabled', true);
	} 
	else {
		$('#installBtn').attr('disabled', false);
		$('#modifyBtn').attr('disabled', true);
		$('#deleteBtn').attr('disabled', true);
	}
}


/********************************************************
 * 설명		: 다른페이지 이동시 호출
 * Function	: clearMainPage
 *********************************************************/
function clearMainPage() {
	$().w2destroy('config_cfGrid');
}

/********************************************************
 * 설명		: 화면 리사이즈시 호출
 * Function	: clearMainPage
 *********************************************************/
$(window).resize(function() {
	setLayoutContainerHeight();
});

</script>

<div id="main">
	<div class="page_site">플랫폼 설치 > <strong>CF 설치</strong></div>
	<!-- 설치 관리자 -->
	<div id="isDefaultDirector"></div>
	<!-- Cf 목록-->
	<div class="pdt20">
		<div class="title fl">CF 목록</div>
		<div class="fr">
			<!-- Btn -->
			<sec:authorize access="hasAuthority('DEPLOY_CF_INSTALL')">
			<span id="installBtn" class="btn btn-primary" style="width: 120px">설&nbsp;&nbsp;치</span>&nbsp;
			</sec:authorize>
			<sec:authorize access="hasAuthority('DEPLOY_CF_INSTALL')">
			 <span id="modifyBtn" class="btn btn-info" style="width: 120px">수&nbsp;&nbsp;정</span>&nbsp;
			</sec:authorize>
			<sec:authorize access="hasAuthority('DEPLOY_CF_DELETE')">
			 <span id="deleteBtn" class="btn btn-danger" style="width: 120px">삭&nbsp;&nbsp;제</span>
			</sec:authorize>
			<!-- //Btn -->
		</div>
	</div>
	<div id="config_cfGrid" style="width: 100%; height: 610px"></div>
</div>

<!--  Diego 설정 유무-->
<div id="diegoSelectDiv" style="width:100%; height: 80px;" hidden="true">
		<div class="w2ui-lefted" style="text-align: center;">
			DIEGO를 사용 하시겠습니까?<br />
			<br/>
			<div >
				<label style="width: 100px; ">
					<input type="radio" name="diegoSelect" id="type1" value="true" checked="checked"  />
					&nbsp;예
				</label>
				<label style="width: 120px;margin-left:30px;">
					<input type="radio" name="diegoSelect" id="type2" value="false"  />
					&nbsp;아니요
				</label>
			</div>
		</div>
</div>

<!-- IaaS 설정 DIV -->
<div id="bootSelectBody" style="width: 100%; height: 80px;" hidden="true">
	<div class="w2ui-lefted" style="text-align: left;">IaaS를 선택하세요</div>
	<div class="col-sm-9">
		<div class="btn-group" data-toggle="buttons">
			<label style="width: 100px; margin-left: 40px;">
				<input type="radio" name="structureType" id="type1" value="AWS" checked="checked" tabindex="1" />
				&nbsp;AWS
			</label>
			<label style="width: 130px; margin-left: 50px;">
				<input type="radio" name="structureType" id="type2" value="OPENSTACK" tabindex="2" />
				 &nbsp;OPENSTACK
			</label>
			<label style="width: 130px; margin-left: 40px;">
				<input type="radio" name="structureType" id="type3" value="VSPHERE" tabindex="3" />
				 &nbsp;VSPHERE
			</label>
		</div>
	</div>
</div>

<!--  cf Popup Div -->
<div id="cfPopupDiv"></div>
