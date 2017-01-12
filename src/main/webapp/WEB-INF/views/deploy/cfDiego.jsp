<%
/* =================================================================
 * 작성일 : 2016.10
 * 작성자 : 지향은
 * 상세설명 : CF & Diego 통합 설치
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016.12       지향은           CF & Diego 통합 설치 버그 수정 
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
    
<script type="text/javascript">
var iaas = "";
var bDefaultDirector = "";
var diegoUse="";
var deploymentName = [];
var deigoDeploymentName = new Array();
var installStep = 0;
var menu = "";
var cfInfo = "";
var cfId  = "";
$(function() {
	
	/********************************************************
	 * 설명 :  기본 설치 관리자 정보 조회
	 *********************************************************/
	bDefaultDirector = getDefaultDirector("<c:url value='/common/use/director'/>");
	
	/********************************************************
	 * 설명 :  CF & Diego 통합 정보 그리드
	 *********************************************************/
	$('#config_cfDiegoGrid').w2grid({
		name: 'config_cfDiegoGrid',
		header: '<b>CF & Diego 통합 설치 목록</b>',
		method: 'GET',
 		multiSelect: false,
		show: {	
				selectColumn: true,
				footer: true},
		style: 'text-align: center',
		columnGroups: [
		              { caption: '기본 정보', span:5 },
		              { caption: 'CF', span: 15 },
		              { caption: 'Diego', span: 13 }
		],
		columns:[
		    // 1. CF & Diego
		      {field: 'recid', 	caption: 'recid', hidden: true}
			, {field: 'id', caption: 'id', hidden: true}
			, {field: 'cfVo.id', caption: 'cf 아이디',  hidden: true}
			, {field: 'diegoVo.id', caption: 'diego 아이디', hidden: true}
			, {field: 'deployStatus', caption: '배포상태', size: '114px', 
				render: function(record) {
						if ( record.deployStatus ){
							if ( (record.deployStatus).indexOf("오류") > -1 )
			    				return '<span class="btn btn-danger" style="width: 85%;font-size: 12px;">'+ record.deployStatus + '</span>';
		    				else 
		    					return '<span class="btn btn-primary" style="width: 85%;font-size: 12px;">'+ record.deployStatus + '</span>';
						} else {
							return '&ndash;';
						}
		    	   }
				}
			, {field: 'iaasType', caption: 'IaaS', size: '100px'
				, render: function(record) {
					return record.iaasType.toLowerCase();
				}
			}
			, {field: 'cfVo.directorUuid', caption: '설치관리자 UUID', size: '280px'}
			, {field: 'createDate', caption: '생성일자', size: '100px'}
			, {field: 'updateDate', caption: '수정일자', size: '100px'}
			//2. CF
			, {field: 'cfVo.deploymentName', caption: '배포명', size: '100px' }
			, {field: 'cfVo.release', caption: 'CF 릴리즈', size: '100px'
				, render:function(record){
						if( record.cfVo.releaseName && record.cfVo.releaseVersion ){
							return record.cfVo.releaseName +"/"+ record.cfVo.releaseVersion;
						}
					}
				}
			, {field: 'cfVo.domain', caption: '도메인', size: '180px'}
			, {field: 'cfVo.description', caption: '도메인 설명', size: '220px'}
			, {field: 'cfVo.domainOrganization', caption: '도메인 그룹', size: '120px'}
			// 2.1 HA Proxy Info
			, {field: 'cfVo.proxyStaticIps', caption: 'HAProxy 공인 IP', size: '120px'}
			// 2.2 Network Info
			, {field: 'cfVo.network.cloudSecurityGroups', caption: '시큐리티 그룹', size: '140px'}			
			, {field: 'cfVo.network.subnetRange', caption: '서브넷 범위', size: '100px'}
			, {field: 'cfVo.network.subnetGateway', caption: '게이트웨이', size: '100px'}
			, {field: 'cfVo.network.subnetDns', caption: 'DNS', size: '100px'}
			, {field: 'cfVo.network.subnetReservedFrom', caption: 'IP 할당 제외 대역', size: '200px' }
			, {field: 'cfVo.network.subnetStaticFrom', caption: 'IP 할당 대역', size: '200px'	}
			, {field: 'cfVo.network.subnetId', caption: '네트워크 ID', size: '235px'}
			//2.3 Resource Info
			, {field: 'cfVo.resource.stemcellName', caption: '스템셀', size: '280px'
				, render:function(record){
						if( record.cfVo.resource.stemcellName && record.cfVo.resource.stemcellVersion ){
							return record.cfVo.resource.stemcellName +"/"+ record.cfVo.resource.stemcellVersion;
						}
					}
				}
			, {field: 'cfVo.deploymentFile', caption: '배포파일명', size: '180px',
					render: function(record) {
						if ( record.cfVo.deploymentFile != null ) {
		       				return '<a style="color:#333;" href="/common/deploy/download/manifest/' + record.cfVo.deploymentFile +'" onclick="window.open(this.href); return false;">' + record.cfVo.deploymentFile + '</a>';
						} else {
		    				return '&ndash;';
						}
					}
				}
			//3. Diego
			, {field: 'diegoVo.deploymentName', caption:'배포명', size:'100px' }
			, {field:'diegoVo.diegoRelease', caption:'Diego 릴리즈', size:'100px'
				, render :function(record){
					if( !checkEmpty(record.diegoVo.diegoReleaseName) && !checkEmpty(record.diegoVo.diegoReleaseVersion) ){
						return record.diegoVo.diegoReleaseName +"/"+ record.diegoVo.diegoReleaseVersion;
					}
					else{
						return "&ndash;"
					}
				}}
			, {field:'diegoVo.cflinuxfs2rootfsrelease', caption:'cflinuxfs2rootfs 릴리즈', size:'123px'
				, render :function(record){
					if( !checkEmpty(record.diegoVo.cflinuxfs2rootfsreleaseName) && !checkEmpty(record.diegoVo.cflinuxfs2rootfsreleaseVersion) ){
						return record.diegoVo.cflinuxfs2rootfsreleaseName +"/"+ record.diegoVo.cflinuxfs2rootfsreleaseVersion;
					}
					else{
						return "&ndash;"
					}
				}}
			, {field:'diegoVo.gardenRelease', caption:'Garden Linux 릴리즈', size:'130px'
				, render :function(record){
					if( !checkEmpty(record.diegoVo.gardenReleaseName) && !checkEmpty(record.diegoVo.gardenReleaseVersion) ){
						return record.diegoVo.gardenReleaseName +"/"+ record.diegoVo.gardenReleaseVersion;
					}else{
						return "&ndash;"
					}
				}}
			, {field:'diegoVo.etcdRelease', caption:'ETCD 릴리즈', size:'100px'
				, render :function(record){
					if( !checkEmpty(record.diegoVo.etcdReleaseName) && !checkEmpty(record.diegoVo.etcdReleaseVersion) ){
						return record.diegoVo.etcdReleaseName +"/"+ record.diegoVo.etcdReleaseVersion;
					}else{
						return "&ndash;"
					}
				}}
			// 3.1  network info
			, {field: 'diegoVo.network.cloudSecurityGroups', caption: '시큐리티 그룹', size: '140px'}			
			, {field: 'diegoVo.network.subnetRange', caption: '서브넷 범위', size: '100px'}
			, {field: 'diegoVo.network.subnetGateway', caption: '게이트웨이', size: '100px'}
			, {field: 'diegoVo.network.subnetDns', caption: 'DNS', size: '100px'}
			, {field: 'diegoVo.network.subnetReservedFrom', caption: 'IP 할당 제외 대역', size: '200px' }
			, {field: 'diegoVo.network.subnetStaticFrom', caption: 'IP 할당 대역', size: '200px'	}
			, {field: 'diegoVo.network.subnetId', caption: '네트워크 ID', size: '235px'}
			//3.2 resource info
			, {field: 'diegoVo.resource.stemcellName', caption: '스템셀', size: '280px'
				, render:function(record){
						if( record.cfVo.resource.stemcellName && record.cfVo.resource.stemcellVersion ){
							return record.cfVo.resource.stemcellName +"/"+ record.cfVo.resource.stemcellVersion;
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
	 * 설명 :  CF & Diego 통합 설치 버튼
	 *********************************************************/
	$("#installBtn").click(function(){
		if ($("#installBtn").attr('disabled') == "disabled") return;
		if( iaas == "") {
 			selectIaas(); return;
 		}
		//1.1 save CF & Diego info 
		$("#cfDiegoPopupDiv").load("/deploy/cfDiego/install/cfPopup",function(event){
			//DIego 사용 여부
			diegoUse = "true";
			installStep = 7;
			menu ="cfDiego";
			defaultInfoPopup();
			return;
		});
	});
	
	/********************************************************
	 * 설명 :  CF & Diego 통합 설치 수정 버튼
	 *********************************************************/
	$("#modifyBtn").click(function() {
		if ($("#modifyBtn").attr('disabled') == "disabled")
			return;
		
		var selected = w2ui['config_cfDiegoGrid'].getSelection();
		var record = w2ui['config_cfDiegoGrid'].get(selected);
		
		var message = "";

		if (record.deploymentName)
			message = "CF & DIEGO (배포명 :" + record.deploymentName + ")를 수정하시겠습니까?";
		else
			message = "선택된 CF & DIEGO를 수정하시겠습니까?";
		
		w2confirm({
			title : "CF & Diego 통합 설치 수정",
			msg : message,
			yes_text : "확인",
			yes_callBack : function(event) {
				setModifyPopup();
			},
			no_text : "취소",
			no_callBack : function(event){
				gridReload();
			}
		});
	});
	
	/********************************************************
	 * 설명 :  CF & Diego 삭제 버튼
	 *********************************************************/
	$("#deleteBtn").click( function() {
		if ($("#deleteBtn").attr('disabled') == "disabled")
			return;
		var selected = w2ui['config_cfDiegoGrid'].getSelection();
		var record = w2ui['config_cfDiegoGrid'].get(selected);
		
		var message = "선택된 CF & DIEGO를 삭제하시겠습니까?";
		
		w2confirm({
			title :"CF & Diego 통합 설치 삭제",
			msg :message,
			yes_text :"확인",
			yes_callBack :function(event) {
				var platform = "";
				if( record.diegoVo.deploymentName != "" && record.diegoVo.deploymentName != null ){
					platform ="diego";
				}else if( record.cfVo.deploymentName != ""&& record.cfVo.deploymentName != null  ){
					platform = "cf";
				}else{
					platform = "";
				}
				cfDiegoDeletePopup(record, platform);
				return;
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
 * 설명 :  CF & Diego 조회
 * Function : doSearch
 *********************************************************/
function doSearch() {
	//iaas 추출
	var directorName = $("#directorName").text().toUpperCase();
	if( directorName.indexOf("_CPI") > 0  ) {
			var start = directorName.indexOf("(");
			var end = directorName.indexOf("_CPI)", start+1);
			iaas = directorName.substring(start+1, end);
	}
	if(iaas != ""){
	w2ui['config_cfDiegoGrid'].load("<c:url value='/deploy/cfDiego/list/"+iaas+"'/>",
			function() { doButtonStyle(); });
	}else{
		doButtonStyle();
	}
}

/********************************************************
 * 설명 : CF & Diego 수정 화면 호출
 * Function : setModifyPopup
 *********************************************************/
function setModifyPopup(){
	var selected = w2ui['config_cfDiegoGrid'].getSelection();

	if (selected.length == 0) {
		w2alert("선택된 정보가 없습니다.", "CF & Diego 통합 설치 수정");
		return;
	} else {
		var record = w2ui['config_cfDiegoGrid'].get(selected);
		$("#cfDiegoPopupDiv").load("/deploy/cfDiego/install/cfPopup",function(event){
			installStep = 7;
			menu="cfDiego";
			getCfData(record); return;
		});
	}
}

/********************************************************
 * 설명 : Diego Popup 화면 호출
 * Function : diegoPopup
 *********************************************************/
function setDiegoPopup(history){
	 var selected = w2ui['config_cfDiegoGrid'].getSelection();
	 var record = w2ui['config_cfDiegoGrid'].get(selected);
	 $("#cfDiegoPopupDiv").load("/deploy/cfDiego/install/diegoPopup",function(event){ 
		 menu="cfDiego";
	 	 if( (record == null && history == 'after') ||  (record.diegoVo.id == null &&  history == 'after' )){
	 		defaultPopup();
	 	 } else if( (record.diegoVo.id != null  ||  record.diegoVo.id != "") || history == 'back' ){
	 		cfId = record.cfVo.id;
	 		getDiegoData(record); return;	 
	 	 }
	 });
}

/********************************************************
 * 설명		:  CF Install
 * Function	: cfInstallPopup
 *********************************************************/
var installClient="";
function cfInstallPopup( cfDeploymentName, diegoDeployment ){
	var deploymentName =  cfDeploymentName;
	var message = "CF(배포명:" + deploymentName +  ") ";
	
	var requestParameter = {
			id 				: cfId,
			iaas			: iaas,
			platform		: "cf"
	};
	
	$("#cfInstallDiv").w2popup({
		width : 850,
		height : 550,
		modal	: true,
		showMax : true,
		onOpen : function(event){
			event.onComplete = function(){
				//deployFileName
				var socket = new SockJS('/deploy/cfDiego/install/cfInstall');
				installClient = Stomp.over(socket); 
				installClient.connect({}, function(frame) {
			        installClient.subscribe('/user/deploy/cfDiego/install/cfLogs', function(data){
			        	
			        	var installLogs = $(".w2ui-msg-body #cfInstallLogs");
			        	var response = JSON.parse(data.body);
			        	
			        	if ( response.messages != null ) {
					       	for ( var i=0; i < response.messages.length; i++) {
					        	installLogs.append(response.messages[i] + "\n").scrollTop( installLogs[0].scrollHeight );
					       	}
					       	
					       	if ( response.state.toLowerCase() != "started" ) {
					    		if ( response.state.toLowerCase() == "error" ) message = message + " 설치 중 오류가 발생하였습니다.";
					    		if ( response.state.toLowerCase() == "cancelled" ) message = message + " 설치 중 취소되었습니다.";
					    		if ( response.state.toLowerCase() == "done" )	message = message + " 설치가 완료되었습니다."; 
					    		
					    		installStatus = response.state.toLowerCase();
					    		$('.w2ui-msg-buttons #cfDeployPopupBtn').prop("disabled", false);
					    		
					    		w2alert(message, "CF 설치");
					    		installClient.disconnect(diegoInstallPopup(diegoDeployment));
					    		
					       	}
			        	}
			        });
			        installClient.send('/send/deploy/cfDiego/install/cfInstall', {}, JSON.stringify(requestParameter));
			    });
			}
		}, onClose : function (event){
			event.onComplete= function(){
				$("textarea").text("");
				w2ui['config_cfDiegoGrid'].reset();
				if( installClient  != ""){
					installClient.disconnect();
					installClient = "";
				}
				cfDiegoInitSetting();
				doSearch();
			}
		}
	});
}

/********************************************************
 * 설명		:  Diego Install Popup 
 * Function	: diegoInstallPopup
 *********************************************************/
function diegoInstallPopup( diegoDeploymentName ){
	 if(installStatus != "done") return;

	 var deploymentName = diegoDeploymentName;
	var message = "DIEGO(배포명:" + deploymentName +  ") ";
	installClient="";
	
	var requestParameter = {
			id 				: diegoId,
			iaas			: iaas,
			platform		: "diego"
	};
	
	$("#diegoInstallDiv").w2popup({
		width 	: 950,
		height 	: 520,
		modal	:true,
		showMax :true,
		onOpen :function(event){
			event.onComplete = function(){
				//deployFileName
				var socket = new SockJS('/deploy/cfDiego/install/diegoInstall');
				installClient = Stomp.over(socket); 
				installClient.connect({}, function(frame) {
			        installClient.subscribe('/user/deploy/cfDiego/install/diegoLogs', function(data){
			        	
			        	var installLogs = $(".w2ui-msg-body #diegoInstallLogs");
			        	
			        	var response = JSON.parse(data.body);
			        	
			        	if ( response.messages != null ) {
					       	for ( var i=0; i < response.messages.length; i++) {
					        	installLogs.append(response.messages[i] + "\n").scrollTop( installLogs[0].scrollHeight );
					       	}
					       	
					       	if ( response.state.toLowerCase() != "started" ) {
					            if ( response.state.toLowerCase() == "done" )	message = message + " 설치가 완료되었습니다."; 
					    		if ( response.state.toLowerCase() == "error" ) {
					    			message = message + " 설치 중 오류가 발생하였습니다.";
					    			installClient ="";
					    		}
					    		if ( response.state.toLowerCase() == "cancelled" ){
					    			message = message + " 설치 중 취소되었습니다.";
					    			installClient ="";
					    		}
					    		
					    		installStatus = response.state.toLowerCase();
					    		$('.w2ui-msg-buttons #deployPopupBtn').prop("disabled", false);
					    		
					    		installClient.disconnect();
								w2alert(message, "DIEGO 설치");
					       	}
			        	}

			        });
			        installClient.send('/send/deploy/cfDiego/install/diegoInstall', {}, JSON.stringify(requestParameter));
			    });
			}
		}, onClose : function (event){
			event.onComplete= function(){
				$("textarea").text("");
				w2ui['config_cfDiegoGrid'].reset();
				if( installClient  != ""){
					installClient.disconnect();
					installClient = "";
				}
				cfDiegoInitSetting();
				doSearch();
			}
		}
	});
}

/********************************************************
 * 설명		:  CF & Diego 삭제
 * Function	: deletePopup
 *********************************************************/
var deleteClient = "";
function cfDiegoDeletePopup(record, type){
	var requestParameter = {
			iaas		: (record.iaas) ? record.iaas : record.iaasType, 
			id			: (type=="diego")?  record.diegoVo.id : record.cfVo.id,
			platform	: type
	};
	if ( record.deployStatus == null || record.deployStatus == "" ) {
		// 단순 레코드 삭제
		var url = "/deploy/cfDiego/delete/data";
		$.ajax({
			type : "DELETE",
			url : url,
			data : JSON.stringify(requestParameter),
			contentType : "application/json",
			success : function(data, status) {
				if(  type == "diego"){
					cfDiegoDeletePopup(record, "cf");
					doSearch();
				}else{
					doSearch();
				}
			},
			error : function(request, status, error) {
				w2alert( JSON.parse(request.responseText).message, "CF Diego 삭제");
			}
		});
	} else{
		var message = "";
		var body = '<textarea id="deleteLogs" style="width:95%;height:90%;overflow-y:visible;resize:none;background-color: #FFF; margin:2%" readonly="readonly"></textarea>';
		w2popup.open({
			width : 700,
			height : 500,
			title : "<b>"+ requestParameter.platform.toUpperCase() + "삭제</b>",
			body  : body,
			buttons : '<button class="btn" style="float:right; padding-right:15%;" onclick="popupComplete();">닫기</button>',
			showMax : true,
			onOpen : function(event){
				event.onComplete = function(){
					var socket = new SockJS('/deploy/cfDiego/delete/instance');
					deleteClient = Stomp.over(socket); 
					deleteClient.connect({}, function(frame) {
						deleteClient.subscribe('/user/deploy/cfDiego/delete/logs', function(data){
				        	var deleteLogs = $(".w2ui-msg-body #deleteLogs");
				        	var response = JSON.parse(data.body);
				        	
				        	if ( response.messages != null ) {
						       	for ( var i=0; i < response.messages.length; i++) {
						       		deleteLogs.append(response.messages[i] + "\n").scrollTop( deleteLogs[0].scrollHeight );
						       	}
						       	if ( response.state.toLowerCase() != "started" ) {
						            if ( response.state.toLowerCase() == "done" )	message = message + " 삭제가 완료되었습니다."; 
						    		if ( response.state.toLowerCase() == "error" ) message = message + " 삭제 중 오류가 발생하였습니다.";
						    		if ( response.state.toLowerCase() == "cancelled" ) message = message + " 삭제 중 취소되었습니다.";
						    			
						    		installStatus = response.state.toLowerCase();
						    		deleteClient.disconnect();
									if ( type == "diego" && installStatus == "done" ) {
										w2alert(message, "Diego 설치");
										deleteClient = "";
										w2popup.close();
										cfDiegoDeletePopup(record, "cf" );
									}else{
										w2alert(message, "CF 설치");
									}
						       	}
				        	}					        	
				        });
						deleteClient.send('/send/deploy/cfDiego/delete/instance', {}, JSON.stringify(requestParameter));
				    });
				}
			}, onClose : function (event){
				event.onComplete= function(){
					$("textarea").text("");
					w2ui['config_cfDiegoGrid'].reset();
					if(  deleteClient != "" ){
						deleteClient.disconnect();
						deleteClient = "";
					}
					cfDiegoInitSetting();
					doSearch();
				}
			}
		});
	}		
}

/********************************************************
 * 설명		: Install/Delete 팝업 종료시 이벤트
 * Function	: popupComplete
 *********************************************************/
function popupComplete(){
	var msg;
	if(installStatus="done" ||installStatus == "error" ){
		msg = $(".w2ui-msg-title b").text() + " 화면을 닫으시겠습니까?";
	}else if( installStatus != "done" && installStatus != "error" ){
		msg = $(".w2ui-msg-title b").text() + " 화면을 닫으시겠습니까?<BR>(닫은 후에도 완료되지 않는 설치 또는 삭제 작업은 계속 진행됩니다.)";
	}
	w2confirm({
		title 	: $(".w2ui-msg-title b").text(),
		msg		: msg,
		yes_text: "확인",
		yes_callBack : function(envent){
			w2popup.close();
			cfDiegoInitSetting();
		},
		no_text : "취소"
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
		$('#installBtn').removeAttr("disabled"); 
		$('#modifyBtn').attr('disabled', true);
		$('#deleteBtn').attr('disabled', true);
	}
}

/********************************************************
 * 설명		: 변수 초기 셋팅
 * Function	: cfDiegoInitSetting
 *********************************************************/
function cfDiegoInitSetting(){
	iaas = "";
	bDefaultDirector = "";
	diegoUse="";
	deploymentName = [];
	deigoDeploymentName = new Array();
	installStep = 0;
	menu = "";
	cfInfo = "";
	cfId  = "";
}

/********************************************************
 * 설명		: 그리드 재조회
 * Function	: gridReload
 *********************************************************/
function gridReload() {
	w2ui['config_cfDiegoGrid'].clear();
	doSearch();
}

/********************************************************
 * 설명		: 다른페이지 이동시 호출
 * Function	: clearMainPage
 *********************************************************/
function clearMainPage() {
	$().w2destroy('config_cfDiegoGrid');
}

/********************************************************
 * 설명		: 화면 리사이즈시 호출
 * Function	: clearMainPage
 *********************************************************/
$(window).resize(function() {
	setLayoutContainerHeight();
});
</script>
<div id="main2">
	<div class="page_site">플랫폼 설치 > <strong>CF & Diego 통합 설치</strong></div>
	<!-- 설치 관리자 -->
	<div id="isDefaultDirector"></div>
	<!-- CF & Diego 통합 설치 목록 -->
	<div class="pdt20">
		<div class="title fl">CF & Diego 통합 설치 목록</div>
		<div class="fr">
			<!-- Btn -->
			<sec:authorize access="hasAuthority('DEPLOY_CF_DIEGO_INSTALL')">
			<span id="installBtn" class="btn btn-primary" style="width: 120px">설&nbsp;&nbsp;치</span>&nbsp; 
			</sec:authorize>
			<sec:authorize access="hasAuthority('DEPLOY_CF_DIEGO_INSTALL')">
				<span id="modifyBtn" class="btn btn-info" style="width: 120px">수&nbsp;&nbsp;정</span>&nbsp; 
			</sec:authorize>
			<sec:authorize access="hasAuthority('DEPLOY_CF_DIEGO_DELETE')">
				<span id="deleteBtn" class="btn btn-danger" style="width: 120px">삭&nbsp;&nbsp;제</span>
			</sec:authorize>
			<!-- //Btn -->
		</div>
	</div>
	<div id="config_cfDiegoGrid" style="width: 100%; height: 610px"></div>
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
<div id="cfDiegoPopupDiv"></div>

<!-- cf 설치화면 -->
<div id="cfInstallDiv" style="width:100%; height:100%;" hidden="true">
	<div rel="title">CF & DIEGO 통합 설치</div>
	<div rel="body" style="width:100%;height:100%;padding:15px 5px 0 5px;margin:0 auto;">
		<div style="margin-left:2%;display:inline-block;width:97%;">
			<ul class="progressStep_7">
				<li class="active">CF 설치</li>
				<li class="before">Diego 설치</li>
			</ul>
		</div>
		<div style="width:95%;height:84%;float:left;display:inline-block;margin-top: 10px;">
			<textarea id="cfInstallLogs" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color:#FFF;margin-left:1%" readonly="readonly"></textarea>
		</div>
	</div>
	<div class="w2ui-buttons" rel="buttons" hidden="true">
		<button class="btn" id="cfDeployPopupBtn" style="float:left;" onclick="deployPopup()" disabled>이전</button>
		<button class="btn" style="float:right; padding-right:15%" onclick="popupComplete();">닫기</button>
	</div>
</div>

<!-- diego 설치화면 -->
<div id="diegoInstallDiv" style="width:100%; height:100%;" hidden="true">
	<div rel="title"><b>CF & DIEGO 통합 설치</b></div>
	<div rel="body" style="width:100%;height:100%;padding:15px 5px 0 5px;margin:0 auto;">
		<div style="margin-left:2%;display:inline-block;width:97%;">
			<ul class="progressStep_7">
				<li class="pass">CF 설치</li>
				<li class="active">Diego 설치</li>
			</ul>
		</div>
		<div style="width:95%;height:84%;float:left;display:inline-block;margin-top: 10px;">
			<textarea id="diegoInstallLogs" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color:#FFF;margin-left:1%" readonly="readonly"></textarea>
		</div>
	</div>
	<div class="w2ui-buttons" rel="buttons" hidden="true">
		<button class="btn" id="diegoDeployPopupBtn" style="float:left;" onclick="deployPopup()" disabled>이전</button>
		<button class="btn" style="float:right; padding-right:15%" onclick="popupComplete();">닫기</button>
	</div>
</div>
