<%
/* =================================================================
 * 작성일 : 
 * 작성자 : 
 * 상세설명 : Diego 설치
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016.10       이동현           화면 수정 및 vSphere 클라우드 기능 추가
 * 2016.12       이동현           Diego 목록과 팝업 화면 .jsp 분리 및 설치 버그 수정 
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<script type="text/javascript">
//private var
var iaas = "";//iaas
var bDefaultDirector = "";
var menu = "";
var cfId = "";
$(function() {
	/********************************************************
	 * 설명 :  기본 설치 관리자 정보 조회
	 *********************************************************/
	 bDefaultDirector = getDefaultDirector("<c:url value='/common/use/director'/>");

	/********************************************************
	 * 설명 :  Diego 배포 목록 Grid
	 *********************************************************/
	$('#config_diegoGrid').w2grid({
		name:'config_diegoGrid',
		header:'<b>DIEGO 목록</b>',
		method:'GET',
 		multiSelect:false,
		show:{	
				selectColumn:true,
				footer:true},
		style:'text-align:center',
		columns:[
		      {field:'recid', 	caption:'recid', hidden:true}
			, {field:'id', caption:'ID', hidden:true}
			, {field:'deployStatus', caption:'배포상태', size:'80px', 
				render:function(record) {
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
			, {field:'deploymentName', caption:'배포명', size:'100px'}
			, {field:'iaas', caption:'IaaS', size:'100px'
				, render: function(record) {
					return record.iaas.toLowerCase();
				}
			}
			, {field:'directorUuid', caption:'설치관리자 UUID', size:'220px'}
			, {field:'diegoRelease', caption:'Diego 릴리즈', size:'100px'
				, render :function(record){
					if( !checkEmpty(record.diegoReleaseName) && !checkEmpty(record.diegoReleaseVersion) ){
						return record.diegoReleaseName +"/"+ record.diegoReleaseVersion;
					}
					else{
						return "&ndash;"
					}
				}}
			, {field:'cflinuxfs2rootfsrelease', caption:'cflinuxfs2rootfs 릴리즈', size:'123px'
				, render :function(record){
					if( !checkEmpty(record.cflinuxfs2rootfsreleaseName) && !checkEmpty(record.cflinuxfs2rootfsreleaseVersion) ){
						return record.cflinuxfs2rootfsreleaseName +"/"+ record.cflinuxfs2rootfsreleaseVersion;
					}
					else{
						return "&ndash;"
					}
				}}
			, {field:'gardenRelease', caption:'Garden Linux 릴리즈', size:'130px'
				, render :function(record){
					if( !checkEmpty(record.gardenReleaseName) && !checkEmpty(record.gardenReleaseVersion) ){
						return record.gardenReleaseName +"/"+ record.gardenReleaseVersion;
					}else{
						return "&ndash;"
					}
				}}
			, {field:'etcdRelease', caption:'ETCD 릴리즈', size:'100px'
				, render :function(record){
					if( !checkEmpty(record.etcdReleaseName) && !checkEmpty(record.etcdReleaseVersion) ){
						return record.etcdReleaseName +"/"+ record.etcdReleaseVersion;
					}else{
						return "&ndash;"
					}
				}}
			, {field:'subnetStaticIp', caption:'VM할당 IP대역', size:'130px' }
			, {field:'subnetReservedIp', caption:'할당 제외 IP대역', size:'130px' }
			, {field:'subnetRange', caption:'서브넷 범위', size:'100px' }
			, {field:'subnetGateway', caption:'게이트웨이', size:'100px' }
			, {field:'subnetDns', caption:'DNS', size:'100px' }
			, {field:'subnetId', caption:'서브넷 ID(NET ID)', size:'100px',
				 render:function(record){
					 	var subnetIds = record.subnetId;
						if( subnetIds.indexOf("Internal") < 0){
							return record.subnetId;

						}else {
							return '&ndash;';
	                    }
					}
				}
			, {field:'stemcell', caption:'스템셀', size:'240px'
				, render:function(record){
						if( !checkEmpty(record.stemcellName) && !checkEmpty(record.stemcellVersion) ){
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
                                  service   : "bootstrap"
                                , iaas      : record.iaas
                                , id        : record.id
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
		}, onLoad:function(event){
			if(event.xhr.status == 403){
				location.href = "/abuse";
				event.preventDefault();
			}
			
		}, onError:function(evnet){
	
		}
	});
	
	/********************************************************
	 * 설명 :  Diego 설치 버튼
	 *********************************************************/
	$("#installBtn").click(function() {
		if ($("#installBtn").attr('disabled') == "disabled") return;
		var directorName = $("#directorName").text().toUpperCase();
		if( iaas == "") {
			selectIaas(); return;
		}
		//diego popup load
		$("#diegoPopupDiv").load("/deploy/diego/install/diegoPopup",function(event){
			menu = "diego";
			defaultPopup();
		});
	});

	/********************************************************
	 * 설명 :  Diego 수정 버튼
	 *********************************************************/
	$("#modifyBtn").click(function() {
		if ($("#modifyBtn").attr('disabled') == "disabled")
			return;
		var selected = w2ui['config_diegoGrid'].getSelection();
		var record = w2ui['config_diegoGrid'].get(selected);

		var message = "";

		if (record.deploymentName)
			message = "DIEGO (배포명 :" + record.deploymentName + ")를 수정하시겠습니까?";
		else
			message = "선택된 DIEGO를 수정하시겠습니까?";
		modifyNetWork ="modify";
		w2confirm({
			title :"DIEGO 설치",
			msg :message,
			yes_text :"확인",
			yes_callBack :function(event) {
				var selected = w2ui['config_diegoGrid'].getSelection();
				if (selected.length == 0) {
					w2alert("선택된 정보가 없습니다.", "DIEGO 설치");
					return;
				} else {
					var record = w2ui['config_diegoGrid'].get(selected);
					$("#diegoPopupDiv").load("/deploy/diego/install/diegoPopup",function(event){
						menu = "diego";
						getDiegoData(record);
						return;
					});
				}
			},
			no_text :"취소",
			no_callBack : function(event){
				gridReload();
			}
		});
	});

	/********************************************************
	 * 설명 :  Diego 삭제 버튼
	 *********************************************************/
	$("#deleteBtn").click( function() {
		if ($("#deleteBtn").attr('disabled') == "disabled")
			return;
		
		var selected = w2ui['config_diegoGrid'].getSelection();
		var record = w2ui['config_diegoGrid'].get(selected);

		var message = "";

		if (record.deploymentName)
			message = "DIEGO (배포명 :" + record.deploymentName + ")를 삭제하시겠습니까?";
		else
			message = "선택된 DIEGO를 삭제하시겠습니까?";

		w2confirm({
			title :"DIEGO 삭제",
			msg :message,
			yes_text :"확인",
			yes_callBack :function(event) {
				$("#diegoPopupDiv").load("/deploy/diego/install/diegoPopup",function(event){
					menu = "diego";
					diegoDeletePopup(record);
					return;
				});
				
			},
			no_text :"취소",
			no_callBack : function(event){
				gridReload();
			}
		});
	});
	
	doSearch();
});

/********************************************************
 * 설명 		:  Diego 조회
 * Function : Diego 조회
 *********************************************************/
function doSearch() {
	//iaas 추출
	var directorName = $("#directorName").text().toUpperCase();
	if( directorName.indexOf("_CPI") > 0  ) {
			var start = directorName.indexOf("(");
			var end = directorName.indexOf("_CPI)", start+1);
			iaas = directorName.substring(start+1, end);
	}
	if(iaas!=""){
	w2ui['config_diegoGrid'].load("<c:url value='/deploy/diego/list/"+iaas+"'/>",
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
	//Bootstrap 
	w2confirm({
		width :500,
		height :180,
		title :'<b>DIEGO 설치</b>',
		msg : $("#bootSelectBody").html(),
		modal :true,
		yes_text :"확인",
		no_text :"취소",
		yes_callBack :function() {
			iaas = $(".w2ui-msg-body input:radio[name='structureType']:checked").val();
			if (iaas) {
				defaultPopup();
			} else {
				w2alert("DIEGO를 설치할 클라우드 환경을 선택하세요");
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
	$().w2destroy('config_diegoGrid');
}

/********************************************************
 * 설명 : 화면 리사이즈시 호출
 *********************************************************/
$(window).resize(function() {
	setLayoutContainerHeight();
});

</script>

<div id="main">
	<div class="page_site">플랫폼 설치 > <strong>DIEGO 설치</strong></div>

	<!-- 설치 관리자 -->
	<div id="isDefaultDirector"></div>

	<!-- Diego 목록-->
	<div class="pdt20">
		<div class="title fl">DIEGO 목록</div>
		<div class="fr">
			<!-- Btn -->
			<sec:authorize access="hasAuthority('DEPLOY_DIEGO_INSTALL')">
				<span id="installBtn" class="btn btn-primary" style="width:120px">설&nbsp;&nbsp;치</span>&nbsp; 
			</sec:authorize>
			<sec:authorize access="hasAuthority('DEPLOY_DIEGO_INSTALL')">
				<span id="modifyBtn" class="btn btn-info" style="width:120px">수&nbsp;&nbsp;정</span>&nbsp; 
			</sec:authorize>
			<sec:authorize access="hasAuthority('DEPLOY_DIEGO_DELETE')">
				<span id="deleteBtn" class="btn btn-danger" style="width:120px">삭&nbsp;&nbsp;제</span>
			</sec:authorize>
			<!-- //Btn -->
		</div>
	</div>
	<div id="config_diegoGrid" style="width:100%; height:610px"></div>
</div>

<!-- IaaS 설정 DIV -->
<div id="bootSelectBody" style="width:100%; height:80px;"
	hidden="true">
	<div class="w2ui-lefted" style="text-align:left;">IaaS를 선택하세요</div>
	<div class="col-sm-9">
		<div class="btn-group" data-toggle="buttons">
			<label style="width:100px; margin-left:40px;">
				<input type="radio" name="structureType" id="type1" value="AWS" checked="checked" tabindex="1" />
				&nbsp;AWS
			</label>
			<label style="width:130px; margin-left:50px;">
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

<!--  diego div -->
<div id="diegoPopupDiv"></div>