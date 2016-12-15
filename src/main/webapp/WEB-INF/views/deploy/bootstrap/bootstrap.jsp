<%
/* =================================================================
 * 작성일 : 
 * 작성자 : 
 * 상세설명 : Bootstrap 설치
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016.07       지향은           화면 수정 및 vSphere 클라우드 기능 추가
 * 2016.12       지향은           Bootstrap 목록과 팝업 화면 .jsp 분리 및 설치 버그 수정 
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<script type="text/javascript">

/******************************************************************
 * 설명 :	변수 설정
 ***************************************************************** */
var iaas ="";
var bootStrapDeploymentName = new Array();
 $(function() {	
	/********************************************************
	 * 설명 :  bootstrap 목록 설정
	 *********************************************************/
 	$('#config_bootstrapGrid').w2grid({
		name: 'config_bootstrapGrid',
		header: '<b>BOOTSTRAP 목록</b>',
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
					if ( record.deployStatus == 'DEPLOY_STATUS_PROCESSING' )
    					return '<span class="btn btn-primary" style="width:60px">배포중</span>';
					else if ( record.deployStatus == 'DEPLOY_STATUS_DONE' )
	    				return '<span class="btn btn-primary" style="width:60px">성공</span>';
    				else	if ( record.deployStatus == 'DEPLOY_STATUS_CANCELLED' )
	    				return '<span class="btn btn-danger" style="width:60px">취소</span>';
    				else	if ( record.deployStatus == 'DEPLOY_STATUS_FAILED' )
	    				return '<span class="btn btn-danger" style="width:60px">실패</span>';
	    			else	if ( record.deployStatus == 'DEPLOY_STATUS_DELETING' )
	    				return '<span class="btn btn-primary" style="width:60px">삭제중</span>';
					else
	    				return '&ndash;';
	    	   		}
				}
			, {field: 'deployLog', caption: '배포로그', size: '100px',
				render: function(record) {
						if ( (record.deployStatus == 'DEPLOY_STATUS_DONE' || record.deployStatus == 'DEPLOY_STATUS_FAILED') && record.deployLog != null ) {
		       				return '<span id="" class="btn btn-primary" style="width:60px" onClick="getDeployLogMsg( \''+record.id+'\');">로그보기</span>';
						} else {
		    				return '&ndash;';
						}
					}
				}
			, {field: 'deploymentName', caption: '배포명', size: '120px', render : function(record){
				bootStrapDeploymentName.push(record.deploymentName);
				return record.deploymentName;
			}}
			
			, {field: 'directorName', caption: '디렉터 명', size: '100px'}
			, {field: 'iaas', caption: 'IaaS', size: '100px'
				, render: function(record) {
					return record.iaas.toLowerCase();
				}
			}
			, {field: 'boshRelease', caption: 'BOSH 릴리즈', size: '100px'}
			, {field: 'boshCpiRelease', caption: 'BOSH CPI 릴리즈', size: '200px'}
			, {field: 'subnetId', caption: '네트워크 ID', size: '200px'}
			, {field: 'subnetRange', caption: '서브넷 범위', size: '100px'}
			, {field: 'publicStaticIp', caption: '디렉터 공인 IP', size: '100px'}
			, {field: 'privateStaticIp', caption: '디렉터 내부 IP', size: '100px'}
			, {field: 'subnetGateway', caption: '게이트웨이', size: '100px'}
			, {field: 'subnetDns', caption: 'DNS', size: '100px'}
			, {field: 'ntp', caption: 'NTP', size: '100px'}
			, {field: 'stemcell', caption: '스템셀', size: '320px'}
			, {field: 'instanceType', caption: '인스턴스 유형', size: '100px'}
			, {field: 'boshPassword', caption: 'VM 비밀번호', size: '100px'}
			, {field: 'deploymentFile', caption: '배포파일명', size: '180px',
					render: function(record) {
						if ( record.deploymentFile != null ) {
		       				var deplymentParam = {
		       						  service	: "bootstrap"
		       						, iaas		: record.iaas
		       						, id		: record.id
		       				} 
		       				var fileName = record.deploymentFile;
		       				return '<a style="color:#333;" href="/common/deploy/download/manifest/' + fileName +'" onclick="window.open(this.href); return false;">' + record.deploymentFile + '</a>';
						}
		    			else {
		    				return '&ndash;';
						}
					}
				}
			, {field: 'createdDate', caption: '생성일자', size: '100px', hidden: true}
			, {field: 'updatedDate', caption: '수정일자', size: '100px', hidden: true}
			
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
 	
	/******************************************************************
	 * 설명 : BootStrap 설치 버튼
	 ***************************************************************** */
 	$("#installBtn").click(function(){
 		iaasSelectPopup();
 	});
 	
 	/******************************************************************
	 * 설명 : BootStrap 수정 버튼
	 ***************************************************************** */
	$("#modifyBtn").click(function(){
		if($("#modifyBtn").attr('disabled') == "disabled") return;
		var selected = w2ui['config_bootstrapGrid'].getSelection();
		if( selected.length == 0 ){
			w2alert("선택된 정보가 없습니다.", "BOOTSTRAP 수정");
			return;
		}
		var record = w2ui['config_bootstrapGrid'].get(selected);
		if ( record.deploymentName )
			message = "BOOTSTRAP (배포명 : " + record.deploymentName + ")를 수정하시겠습니까?";
		else
			message = "선택된 BOOTSTRAP을 수정하시겠습니까?";
			
		w2confirm({
			title 	: "BOOTSTRAP 수정",
			msg		: message,
			yes_text: "확인",
			yes_callBack : function(event){
				$("#bootstrapPopupDiv").load("/deploy/bootstrap/install/bootstrapPopup",function(event){
					iaas = record.iaas.toLowerCase();
					getBootstrapData(record);
				});
			},
			no_text : "취소",
			no_callBack : function(event){
				gridReload();
			}
		});
 	});
 	
 	/******************************************************************
	 * 설명 : BootStrap 삭제 버튼
	 ***************************************************************** */
	$("#deleteBtn").click(function(){
		if($("#deleteBtn").attr('disabled') == "disabled") return;
		
		var selected = w2ui['config_bootstrapGrid'].getSelection();
		var record = w2ui['config_bootstrapGrid'].get(selected);
		var message = "";
		
		if ( record.deploymentName )
			message = "BOOTSTRAP (배포명 : " + record.deploymentName + ")를 삭제하시겠습니까?";
		else
			message = "선택된 BOOTSTRAP을 삭제하시겠습니까?";
		
		w2confirm({
			title 	: "BOOTSTRAP 삭제",
			msg		: message,
			yes_text: "확인",
			yes_callBack : function(event){
				$("#bootstrapPopupDiv").load("/deploy/bootstrap/install/bootstrapPopup",function(event){
					deletePop(record);
				});
			},
			no_text : "취소",
			no_callBack : function(event){
				gridReload();
			}
		});
 	});
 	
 	//조회
	doSearch();
});


/******************************************************************
 * Function : doSearch
 * 설명		 : Bootstrap 목록 조회
 ***************************************************************** */
function doSearch() {
	//doButtonStyle();
	w2ui['config_bootstrapGrid'].load("<c:url value='/deploy/bootstrap/list'/>",
			function (){ doButtonStyle(); });
}

/******************************************************************
 * Function : iaasSelectPopup
 * 설명		 : Bootstrap Iaas 선택 팝업
 ***************************************************************** */
function iaasSelectPopup() {
	w2confirm({
		width 			: 550,
		height 			: 180,
		title 				: '<b>BOOTSTRAP 설치</b>',
		msg 				: $("#bootSelectBody").html(),
		modal			: true,
		yes_text 		: "확인",
		no_text 			: "취소",
		yes_callBack 	: function(){
			iaas = $(".w2ui-msg-body input:radio[name='iaas']:checked").val();
			if(iaas){
				$("#bootstrapPopupDiv").load("/deploy/bootstrap/install/bootstrapPopup",function(event){
					if( iaas.toUpperCase() == "AWS") {
						awsPopup();
					} else if( iaas.toUpperCase() == 'OPENSTACK'){
						openstackPopup();
					} else if( iaas.toUpperCase() == "VSPHERE"){
						vSpherePopup();
					} 
				});
			}else{
				w2alert("BOOTSTRAP을 설치할 클라우드 환경을 선택하세요");
			}
		},no_callBack : function(event){
			w2ui['config_bootstrapGrid'].clear();
			doSearch();
		}
	});
}

 /******************************************************************
  * Function : doButtonStyle
  * 설명		  : Button 제어
  ***************************************************************** */
function doButtonStyle(){
	//Button Style init
	$('#modifyBtn').attr('disabled', true);
	$('#deleteBtn').attr('disabled', true);
}

 /******************************************************************
  * Function : clearMainPage
  * 설명		  : 다른페이지 이동시 Bootstrap Grid clear
  ***************************************************************** */
function clearMainPage() {
	$().w2destroy('config_bootstrapGrid');
}

 /******************************************************************
  * 설명		  : 화면 리사이즈시 호출 
  ***************************************************************** */
$( window ).resize(function() {
	setLayoutContainerHeight();
});
</script>

<div id="main">
	<div class="page_site">플랫폼 설치 > <strong>BOOTSTRAP 설치</strong></div>
	<!-- BOOTSTRAP 목록-->
	<div class="pdt20"> 
		<div class="title fl">BOOTSTRAP 목록</div>
		<div class="fr"> 
			<!-- Btn -->
			<sec:authorize access="hasAuthority('DEPLOY_BOOTSTRAP_INSTALL')">
			<span id="installBtn" class="btn btn-primary"  style="width:120px">설&nbsp;&nbsp;치</span>
			</sec:authorize>
			&nbsp;
			<sec:authorize access="hasAuthority('DEPLOY_BOOTSTRAP_INSTALL')">
			<span id="modifyBtn" class="btn btn-info" style="width:120px">수&nbsp;&nbsp;정</span>
			</sec:authorize>
			&nbsp;
			<sec:authorize access="hasAuthority('DEPLOY_BOOTSTRAP_DELETE')">
			<span id="deleteBtn" class="btn btn-danger" style="width:120px">삭&nbsp;&nbsp;제</span>
			</sec:authorize>
			<!-- //Btn -->
	    </div>
	</div>
	<div id="config_bootstrapGrid" style="width:100%; height:758px"></div>	
</div>

<!-- Infrastructure  설정 DIV -->
<div id="bootSelectBody" style="width:100%; height: 80px;" hidden="true">
	<div class="w2ui-lefted" style="text-align: left;">
		BOOTSTRAP을 설치할 클라우드 환경을 선택하세요<br />
		<br />
	</div>
	<div>
		<div class="btn-group" data-toggle="buttons" >
			<label style="width: 100px; ">
				<input type="radio" name="iaas" id="type1" value="AWS" checked="checked"  />
				&nbsp;AWS
			</label>
			<label style="width: 120px;margin-left:30px;">
				<input type="radio" name="iaas" id="type2" value="OPENSTACK"  />
				&nbsp;OPENSTACK
			</label>
			<label style="width: 100px;margin-left:30px;">
				<input type="radio" name="iaas" id="type2" value="VSPHERE"  />
				&nbsp;VSPHERE
			</label>
		</div>
	</div>
</div>

<div id="bootstrapPopupDiv"></div>