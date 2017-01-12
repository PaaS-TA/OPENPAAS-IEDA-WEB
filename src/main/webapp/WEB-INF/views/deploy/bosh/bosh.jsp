<%
/* =================================================================
 * 작성일 : 
 * 작성자 : 
 * 상세설명 : Bosh 설치
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016.10       이동현           화면 수정 및 vSphere 클라우드 기능 추가
 * 2016.12       이동현           Bosh 목록과 팝업 화면 .jsp 분리 및 설치 버그 수정 
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<script type="text/javascript">
//private var
var iaas = "";
var bDefaultDirector; 
var boshDeploymentName = new Array();

$(function(){
	//기본 설치 관리자
 	bDefaultDirector = getDefaultDirector("<c:url value='/common/use/director'/>");
	
 	/* ****************** BOSH 목록 Grid ****************** */
	$('#config_boshGrid').w2grid({
		name: 'config_boshGrid',
		header: '<b>Bosh 목록</b>',
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
			, {field: 'taskId', caption: 'TASK ID', size: '100px', hidden: true}
			, {field: 'deploymentName', caption: '배포명', size: '100px',
				render : function(record){
					boshDeploymentName.push(record.deploymentName);
				return record.deploymentName;
			}}
			, {field: 'iaas', caption: 'IaaS', size: '100px'
				, render: function(record) {
					return record.iaas.toLowerCase();
				}
			}
			, {field: 'directorUuid', caption: '설치관리자 UUID', size: '220px'}
			, {field: 'releaseVersion', caption: 'BOSH 릴리즈', size: '100px'}
			, {field: 'publicStaticIp', caption: '디렉터 공인 IP', size: '100px',
				render: function(recode){
					if(recode.publicStaticIp != null && recode.publicStaticIp != ""){
						return recode.publicStaticIp;
					}else{
						return '&ndash;';
					}
				}}
			, {field: 'subnetRange', caption: '서브넷 범위', size: '100px'}
			, {field: 'subnetStaticIp', caption: '할당 IP대역', size: '240px'}
			, {field: 'subnetGateway', caption: '게이트웨이', size: '100px'}
			, {field: 'subnetDns', caption: 'DNS', size: '100px'}
			, {field: 'subnetId', caption: '서브넷 ID(NET ID)', size: '100px'}
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
		},
       	onLoad:function(event){
			if(event.xhr.status == 403){
				location.href = "/abuse";
				event.preventDefault();
			}
		}, onError:function(evnet){
		}
	});
	
	/********************************************************
	 * 설명 :  BOSH 설치 버튼
	 *********************************************************/
	$("#installBtn").click(function(){
		if($("#installBtn").attr('disabled') == "disabled") return;
		var directorName = $("#directorName").text().toUpperCase();
		if( directorName.indexOf("_CPI") > 0  ) {
			var start = directorName.indexOf("(");
			var end = directorName.indexOf("_CPI)", start+1);
			iaas = directorName.substring(start+1, end)
		}
 		 if (iaas == "AWS") { 
			$("#boshPopupDiv").load("/deploy/bosh/install/boshPopup",function(event){
				iaas = "AWS";
				awsPopup();
			});
 		} else if (iaas == "OPENSTACK") {
 			$("#boshPopupDiv").load("/deploy/bosh/install/boshPopup",function(event){
				iaas = "OPENSTACK";
				openstackPopup();
 			});
		} else if(iaas == "VSPHERE"){
			$("#boshPopupDiv").load("/deploy/bosh/install/boshPopup",function(event){
				iaas = "VSPHERE";
				vSpherePopup();
			});
		}
	});
	
	/********************************************************
	 * 설명 :  BOSH 수정 버튼
	 *********************************************************/
	$("#modifyBtn").click(function(){
		if($("#modifyBtn").attr('disabled') == "disabled") return;
		
		var selected = w2ui['config_boshGrid'].getSelection();
		var record = w2ui['config_boshGrid'].get(selected);
		var message = "";
		
		if ( record.deploymentName ){
			message = "BOSH (배포명 : " + record.deploymentName + ")를 수정하시겠습니까?";
		} else{
			message = "선택된 BOSH를 수정하시겠습니까?";
		}
		
		w2confirm({
			title 	: "BOSH 수정",
			msg		: message,
			yes_text: "확인",
			yes_callBack : function(event){
				var selected = w2ui['config_boshGrid'].getSelection();

				if( selected.length == 0 ){
					w2alert("선택된 정보가 없습니다.", "BOSH 수정");
					return;
				} else{
					var record = w2ui['config_boshGrid'].get(selected);
					$("#boshPopupDiv").load("/deploy/bosh/install/boshPopup",function(event){
						getBoshData(record);
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
	 * 설명 :  BOSH 삭제
	 *********************************************************/
	$("#deleteBtn").click(function(){
		if($("#deleteBtn").attr('disabled') == "disabled") return;
		
		var selected = w2ui['config_boshGrid'].getSelection();
		var record = w2ui['config_boshGrid'].get(selected);
		
		var message = "";
		
		if ( record.deploymentName ){
			message = "BOSH (배포명 : " + record.deploymentName + ")를 삭제하시겠습니까?";
		} else{
			message = "선택된 BOSH를 삭제하시겠습니까?";
		}
		w2confirm({
			title 	: "BOSH 삭제",
			msg		: message,
			yes_text: "확인",
			yes_callBack : function(event){
				$("#boshPopupDiv").load("/deploy/bosh/install/boshPopup",function(event){
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
 * 설명 :  BOSH 조회
 * Function : doSearch
 *********************************************************/
function doSearch() {
	var directorName = $("#directorName").text().toUpperCase();
	//IaaS 추출
	if( directorName.indexOf("_CPI") > 0  ) {
			var start = directorName.indexOf("(");
			var end = directorName.indexOf("_CPI)", start+1);
			iaas = directorName.substring(start+1, end);
	}
	if(iaas != ""){
	w2ui['config_boshGrid'].load("<c:url value='/deploy/bosh/list/"+iaas+"'/>",
			function (){ doButtonStyle(); });
	}else{
		doButtonStyle();
	}
}

/********************************************************
 * 설명 :  버튼 스타일 제어
 * Function : doButtonStyle
 *********************************************************/
function doButtonStyle(){
	//Button Style init
	if ( !bDefaultDirector ) {
		$('#installBtn').attr('disabled', true);
		$('#modifyBtn').attr('disabled', true);
		$('#deleteBtn').attr('disabled', true);
	} 
	else {
		$('#installBtn').removeAttr("disabled"); 
		$('#modifyBtn').attr('disabled', true);
		$('#deleteBtn').attr('disabled', true);
	}
}

/********************************************************
 * 설명 :  grid Reload
 * Function : gridReload
 *********************************************************/
function gridReload(){
	w2ui['config_boshGrid'].clear();
	doSearch();
}

//다른페이지 이동시 호출
function clearMainPage() {
	$().w2destroy('config_boshGrid');
}

//화면 리사이즈시 호출
$( window ).resize(function() {
	setLayoutContainerHeight();
});
</script>

<div id="main">
	<div class="page_site">플랫폼 설치 > <strong>BOSH 설치</strong></div>
	<!-- 설치 관리자 -->
	<div id="isDefaultDirector"></div>
	
	<!-- Bosh 목록-->
	<div class="pdt20"> 
		<div class="title fl">BOSH 목록</div>
		<div class="fr"> 
			<!-- Btn -->
			<sec:authorize access="hasAuthority('DEPLOY_BOSH_INSTALL')">
			<span id="installBtn" class="btn btn-primary" style="width:120px">설&nbsp;&nbsp;치</span>&nbsp;
			</sec:authorize>
			<sec:authorize access="hasAuthority('DEPLOY_BOSH_INSTALL')">
			<span id="modifyBtn" class="btn btn-info" style="width:120px">수&nbsp;&nbsp;정</span>&nbsp;
			</sec:authorize>
			<sec:authorize access="hasAuthority('DEPLOY_BOSH_DELETE')">
			<span id="deleteBtn" class="btn btn-danger" style="width:120px">삭&nbsp;&nbsp;제</span>
			</sec:authorize>
			<!-- //Btn -->
	    </div>
	</div>
	<div id="config_boshGrid" style="width:100%; height:610px"></div>
</div>

<!-- IaaS 설정 DIV -->
<div id="iaasSelectBody" style="width:100%; height: 80px;" hidden="true">
	<div class="w2ui-lefted" style="text-align: center;">
		BOSH를 설치할 클라우드 환경을 선택하세요<br /><br />
	</div>
	<div class="col-sm-9" style="width:100%">
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
<div id="boshPopupDiv"></div>	
