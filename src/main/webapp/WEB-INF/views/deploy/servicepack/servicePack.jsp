<%
/* =================================================================
 * 작성일 : 2016.10
 * 작성자 : 이동현
 * 상세설명 : 서비스팩 설치
 * =================================================================
 * 수정일         작성자             내용     
 * ------------------------------------------------------------------
 * 2016.12       이동현          서비스팩 설치 코드 버그 수정 
 * =================================================================
 */ 
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<script>
	var deployment = ""; 
	var servicePack = [];
	var deploymentFiles = [];
	var servicePackInfo = "";
	var selectIaas = "";
	var deploymentId = "";
	var deploymentFile = "";
	var iaasType = "";
	var installStatus = "";
	var bdefaultDirector = "";
	var deleteClient = "";
	
	$(function() {
	 	/************************************************
	 	 * 설명: 기본 설치 관리자 정보 조회
	 	************************************************/
	 	bdefaultDirector = getDefaultDirector("<c:url value='/common/use/director'/>");
			 	
	 	$('#w2ui_servicePackGrid').w2grid({
			name: 'w2ui_servicePackGrid',
			method: 'GET',
			header: '<b>서비스팩 목록</b>',
			method: 'GET',
			multiSelect: false,
			show: {	
					selectColumn: true,
					footer: true},
			style: 'text-align: center',
			columns	: [
						 {field: 'recid', 	caption: 'recid', hidden: true}
						,{field: 'deployStatus', caption: '배포 상태', size: '130px', style: 'text-align:center',
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
					   , {field: 'iaas', caption: 'IaaS', size: '140px', style: 'text-align:center'
						   , render: function(record) {
								return record.iaas.toLowerCase();
							}
					   }
					   , {field: 'deploymentName', caption: '배포명', size: '200px', style: 'text-align:center'}
				       , {field: 'createDate', caption: '최초 배포 일자', size: '180px', style: 'text-align:center'}
				       ,{field: 'updateDate', caption: '배포 수정 일자', size: '180px', style: 'text-align:center'}
				       ,{field: 'deploymentFile', caption: '배포 파일 명', size: '170px', style: 'text-align:center'}

			       	],
	       	onSelect: function(event) {
				event.onComplete = function() {
					$('#deleteServicePack').attr('disabled', false);
					return;
				}
			},
			onUnselect: function(event) {
				event.onComplete = function() {
					$('#deleteServicePack').attr('disabled', true);
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
		 * 설명			: ServicePack 버튼클릭
		 *********************************************************/
		$('#installServicePack').on('click',function(){
			if($("#installServicePack").attr('disabled') == "disabled") return;
			var directorName = $("#directorName").text().toUpperCase();
			 if (directorName.indexOf("AWS") > 0) {
				 	selectIaas = "AWS";
				} else if (directorName.indexOf("OPENSTACK") > 0) {
					selectIaas = "OPENSTACk";
				} else if (directorName.indexOf("VSPHERE") > 0)  {
					selectIaas = "VSPHERE";
				}
			var selected = w2ui['w2ui_servicePackGrid'].getSelection();
			var record = w2ui['w2ui_servicePackGrid'].get(selected);
			
			if(record!=null){
				/* servicePack.push(record); */
				saveServicePackInfo(record);
			}else{
				saveServicepackPopup();
			}
		});
	 	
	 	/************************************************
	 	 * 설명: 서비스팩 삭제
	 	************************************************/
		$("#deleteServicePack").click(
				function() {
					if ($("#deleteServicePack").attr('disabled') == "disabled") return;
					var selected = w2ui['w2ui_servicePackGrid'].getSelection();
					var record = w2ui['w2ui_servicePackGrid'].get(selected);
					var message = "";
					if (record.deploymentName)
						message = "서비스팩 (배포명 :" + record.deploymentName + ")를 삭제하시겠습니까?";
					else
						message = "선택된 서비스팩을 삭제하시겠습니까?";
					w2confirm({
						title :"서비스팩 삭제",
						msg :message,
						yes_text :"확인",
						yes_callBack :function(event) {
							deletePopup(record);
							initSetting();
						},
						no_text :"취소",
						no_callBack : function(event){
							gridReload();
						}
					});
		});
	 	
		 doAllSearch();
 	});
	
	/********************************************************
	 * 설명			: ServicePack 팝업
	 * Function	: saveServicePackPopup
	 *********************************************************/
	function saveServicepackPopup(){
		$("#saveServicePackDiv").w2popup({
			width : 1200,
			height :800,
			onClose :function(event){
				event.onComplete = function(){				
					initSetting();
				}
			},
			modal	: true,
			onOpen:function(event){
				event.onComplete = function(){				
					getManifestName();
				}
			},
		});
	}
	 function doAllSearch(){
			var directorName = $("#directorName").text().toUpperCase();
			if( directorName.indexOf("_CPI") > 0  ) {
					var start = directorName.indexOf("(");
					var end = directorName.indexOf("_CPI)", start+1);
					selectIaas = directorName.substring(start+1, end)
		 w2ui['w2ui_servicePackGrid'].load("<c:url value='/deploy/servicePack/list/'/>"+selectIaas);
					
			 }
		doButtonStyle();
	 }
	
	
	/********************************************************
	 * 설명			: 업로드 된 Manifest 명
	 * Function	: getManifestName
	 *********************************************************/
	function getManifestName(){
		$.ajax({
			type : "GET",
			url : "/deploy/servicePack/list/manifest",
			contentType : "application/json",
			async : true,
			success : function(data, status) {
				deploymentFiles = new Array();
				var result = "";
				deploymentFiles.push(data);
				var directorName = $("#directorName").text().toUpperCase();
				if( directorName.indexOf("_CPI") > 0  ) {
						var start = directorName.indexOf("(");
						var end = directorName.indexOf("_CPI)", start+1);
						iaasType = directorName.substring(start+1, end)
				}
				if(data.records != null){
					result += "<table id='manifestTable' class='table table-hover'>"
					for(var i=0;i<data.records.length;i++){
						if(data.records[i].useYn != "Y" && data.records[i].iaas == iaasType.toLowerCase()){
						result += '<tbody><tr><td style="width:5.16%"><input type="radio" onchange="setServicePackInfo(this.value);" name="selectManifest" value="'+data.records[i].id+'" </td>';
						result += "<td style='width:30%'>"+data.records[i].deploymentName+"</td>";
						result += "<td style='width:30%''>"+data.records[i].fileName+"</td>";
						result += "<td style='width:30%''>"+data.records[i].description+"</td>";
						result += "</tr></tbody>";
						}
					}
					$('.manifestList').html(result);
				}
			},
			error : function( e, status ) {
				w2alert("Manifest를 가져오는데 실패하였습니다.", "서비스팩 설치");
			}
		});
	}
	
	function setServicePackInfo(data){
		for(var i=0;i<deploymentFiles[0].records.length;i++){
			if(deploymentFiles[0].records[i].id==data){
				$(".w2ui-msg-body input[name='manifestId']").val(deploymentFiles[0].records[i].id);
				$(".w2ui-msg-body input[name='deploymentName']").val(deploymentFiles[0].records[i].deploymentName);
				$(".w2ui-msg-body input[name='deploymentFile']").val(deploymentFiles[0].records[i].fileName);
			}
		}
	}
	
	/********************************************************
	 * 설명			: 서비스 팩 기본 정보 저장
	 * Function	: saveServicePackInfo
	 *********************************************************/
	var deploymentName="";
	function saveServicePackInfo(record){
		if(record != null){
			servicePackInfo = {
					iaas : selectIaas,
					id: record.id,
					manifestIdx:record.manifestIdx,
					deploymentName : record.deploymentName,
					deploymentFile :record.deploymentFile,
			}; 
		}else{
			servicePackInfo = {
					iaas : selectIaas,
					manifestIdx: $(".w2ui-msg-body input[name='manifestId']").val(),
					deploymentName : $(".w2ui-msg-body input[name='deploymentName']").val(),
					deploymentFile : $(".w2ui-msg-body input[name='deploymentFile']").val(),
			}; 
		}
		$.ajax({
			type : "POST",
			url : "/deploy/servicePack/install/saveServicePackinfo/N",
			contentType : "application/json",
			data:  JSON.stringify(servicePackInfo), 
			async : true,
			success : function(data, status) {
				iaasType = data.iaas;
				deploymentId = data.id;
				deploymentFile = data.deploymentFile;
				deploymentName = data.deploymentName;
				createSettingFile(data.id, data.deploymentFile);
			},
			error : function(request, status, error ) {
				w2alert("서비스 팩 정보 저장에 실패하였습니다.", "서비스팩 설치");
				servicePackInfo = [];
			}
		});
	}
	
	/********************************************************
	 * 설명			:  배포 파일 파일 생성
	 * Function	: createSettingFile
	 *********************************************************/
	function createSettingFile(id, deploymentFile){
		$.ajax({
			type : "POST",
			url : "/deploy/servicePack/install/createSettingFile/"+id +"/N",
			contentType : "application/json",
			async : true,
			success : function(status) {
				deployPopup(deploymentFile);
			},
			error :function(request, status, error) {
				w2alert("서비스팩 배포 파일 생성 실패", "서비스팩  배포 파일 생성");
			}
		});
	}
	
	/********************************************************
	 * 설명			:  서비스팩 Deploy POPUP
	 * Function	: deployPopup
	 *********************************************************/
	function deployPopup(deploymentFile) {
		$("#deployDiv").w2popup({
			width : 720,
			height : 490,
			modal	: true,
			showMax : true,
			onClose :initSetting,
			onOpen :function(event) {
				event.onComplete = function() {
					serviceDeploy(deploymentFile);
				}
			}
		});
	}
	
	/********************************************************
	 * 설명			:  서비스팩 Deploy Confirm
	 * Function	: serviceDeploy
	 *********************************************************/
	function serviceDeploy(type) {
		w2confirm({
			msg :"서비스팩을 설치 하시겠습니까?",
			title :w2utils.lang('서비스팩 설치'),
			yes_text :"예",
			no_text :"아니오",
			yes_callBack :installPopup,
			no_callBack :function(envent){
				w2popup.close();
				initSetting();
			},
		});
	}

	/********************************************************
	 * 설명			:  서비스팩 Install Popup
	 * Function	: installPopup
	 *********************************************************/
	var installClient = "";
	function installPopup(){
		var message = "서비스 팩 설치(배포명:" + deploymentName +  ") ";
		var requestParameter = {
				id :deploymentId,
				iaas:iaasType
		};
		
		$("#installDiv").w2popup({
			width : 720,
			height : 490,
			modal	: true,
			showMax : true,
			onOpen :function(event){
				event.onComplete = function(){
					//deployFileName
					var socket = new SockJS('/deploy/servicePack/install/servicepackInstall');
					installClient = Stomp.over(socket); 
					installClient.connect({}, function(frame) {
				        installClient.subscribe('/user/deploy/servicePack/install/logs', function(data){
				        	
				        	var installLogs = $(".w2ui-msg-body #installLogs");
				        	
				        	var response = JSON.parse(data.body);
				        	
				        	if ( response.messages != null ) {
						       	for ( var i=0; i < response.messages.length; i++) {
						        	installLogs.append(response.messages[i] + "\n").scrollTop( installLogs[0].scrollHeight );
						       	}
						       	
						       	if ( response.state.toLowerCase() != "started" ) {
						            if ( response.state.toLowerCase() == "done" )	message = message + " 설치가 완료되었습니다."; 
						    		if ( response.state.toLowerCase() == "error" ) message = message + " 설치 중 오류가 발생하였습니다.";
						    		if ( response.state.toLowerCase() == "cancelled" ) message = message + " 설치 중 취소되었습니다.";
						    		
						    		installStatus = response.state.toLowerCase();
						    		$('.w2ui-msg-buttons #deployPopupBtn').prop("disabled", false);
						    		if(installClient!=""){
						    			installClient.disconnect();
						    			installClient = "";
						    		}
									w2alert(message, "서비스팩 설치");
						       	}
				        	}

				        });
				        installClient.send('/send/deploy/servicePack/install/servicepackInstall', {}, JSON.stringify(requestParameter));
				    });
				}
			}, onClose : function(event){
				$("textarea").text("");
				installClient.disconnect();
				installClient = "";
				initSetting();
			}
		});
	}
	
	/********************************************************
	 * 설명			:  서비스팩 삭제
	 * Function	: deletePopup
	 *********************************************************/
	function deletePopup(record){
		var requestParameter = {
				iaas:record.iaas, 
				id:record.id
		};
		
		if ( record.deployStatus == null || record.deployStatus == '' ) {
			// 단순 레코드 삭제
			var url = "/deploy/servicePack/delete/data";
			$.ajax({
				type :"DELETE",
				url :url,
				data :JSON.stringify(requestParameter),
				contentType :"application/json",
				success :function(data, status) {
					doAllSearch();
				},
				error :function(request, status, error) {
					var errorResult = JSON.parse(request.responseText);
					w2alert(errorResult.message, "서비스 팩 삭제");
				}
			});
			
		} else {
			var message = "";
			var body = '<textarea id="deleteLogs" style="width:95%;height:90%;overflow-y:visible;resize:none;background-color:#FFF; margin:2%" readonly="readonly"></textarea>';
			
			w2popup.open({
				width :700,
				height :500,
				title :"<b>서비스팩 삭제</b>",
				body  :body,
				buttons :'<button class="btn" onclick="popupComplete();">닫기</button>',
				showMax :true,
				onOpen :function(event){
					event.onComplete = function(){
						var socket = new SockJS('/deploy/servicePack/delete/instance');
						deleteClient = Stomp.over(socket); 
						deleteClient.connect({}, function(frame) {
							deleteClient.subscribe('/user/deploy/servicePack/delete/logs', function(data){
								
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
							    		if(deleteClient != ""){
							    			deleteClient.disconnect();
							    			deleteClient = "";
							    		}
										w2alert(message, "서비스팩 삭제");
							       	}
					        	}
					        	
					        });
							deleteClient.send('/send/deploy/servicePack/delete/instance', {}, JSON.stringify(requestParameter));
					    });
					}
				}, onClose :function (event){
					event.onComplete= function(){
						$("textarea").text("");
						if( deleteClient != "" ){
							deleteClient.disconnect();
						}
						initSetting();
					}
				}
			});
		}		
	}
	
	/********************************************************
	 * 설명			:  Install/Delete 팝업 종료시 Event
	 * Function	: popupComplete
	 *********************************************************/
	function popupComplete(){
		var msg;
		if(installStatus == "done" || installStatus == 'error' || installStatus == 'cancelled'){
			w2popup.close();
		}else{
			msg = $(".w2ui-msg-title b").text() + " 화면을 닫으시겠습니까?<BR>(닫은 후에도 완료되지 않는 설치 또는 삭제 작업은 계속 진행됩니다.)";
			w2confirm({
				title 	: $(".w2ui-msg-title b").text(),
				msg		: msg,
				yes_text:"확인",
				yes_callBack :function(envent){
					w2popup.close();
					initSetting();
				},
				no_text :"취소"
			});
		}
		
	}
	
	/********************************************************
	 * 설명			:  Manifest 파일 검색 Ajax
	 * Function	: searchBtn
	 *********************************************************/
	function searchBtn(){
		var searchVal = $(".w2ui-msg-body input[name='search']").val();
		if(searchVal=="" || searchVal == null || searchVal.replace(/\s/g,"") == ""){
			searchVal = "ALL";
		}
		var url = "/deploy/servicePack/list/manifest/search/"+searchVal+"";
		$.ajax({
			type :"GET",
			url :url,
			contentType :"application/json",
			success :function(data, status) {
				deploymentFiles = new Array();
				var result = "";
				deploymentFiles.push(data);
				if(data.records.length != 0){
					result += "<table id='manifestTable' class='table table-hover'>"
					for(var i=0;i<data.records.length;i++){
						if(data.records[i].useYn != "Y"){
						result += '<tbody><tr><td style="width:5.16%"><input type="radio" onchange="setServicePackInfo(this.value);" name="selectManifest" value="'+data.records[i].id+'" </td>';
						result += "<td style='width:30%'>"+data.records[i].deploymentName+"</td>";
						result += "<td style='width:30%''>"+data.records[i].fileName+"</td>";
						result += "<td style='width:30%''>"+data.records[i].description+"</td>";
						result += "</tr></tbody>";
						}
					}
					$('.manifestList').html(result);
				}else if(data.records.length == 0){
					w2alert("해당하는 Manifest 정보를 찾을 수 없습니다!");
					$('.manifestList').html("");
				}
			},
			error :function(request, status, error) {
				var errorResult = JSON.parse(request.responseText);
				w2alert(errorResult.message, "서비스 팩 검색");
			}
		});
	}

	
	/********************************************************
	 * 설명			: 전역 변수 초기화
	 * Function	: initSetting
	 *********************************************************/
	function initSetting(){
		deployment = ""; 
		servicePack = [];
		deploymentFiles = [];
		servicePackInfo = "";
		selectIaas = "";
		deploymentId = "";
		deploymentFile = "";
		iaasType = "";
		installClient = "";
		deploymentName = "";
		gridReload();
	}


	/********************************************************
	 * 설명			: 목록 재조회
	 * Function	: gridReload
	 *********************************************************/
	function gridReload() {
		w2ui['w2ui_servicePackGrid'].clear();
		doAllSearch();
	}
	
	/********************************************************
	 * 설명			: 다른페이지 이동시 호출
	 * Function	: clearMainPage
	 *********************************************************/
	function clearMainPage() {
		$().w2destroy('w2ui_servicePackGrid');
	}
	
	 /********************************************************
	 * 설명		: 버튼 스타일 변경
	 * Function	: doButtonStyle
	 *********************************************************/
	function doButtonStyle() {
		if ( !bdefaultDirector ) {
			$('#installServicePack').attr('disabled', true);
			$('#deleteServicePack').attr('disabled', true);
		} 
		else {
			$('#installServicePack').attr('disabled', false);
			$('#deleteServicePack').attr('disabled', true);
		}
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
 	input[type="radio"] {width:23px;height:16px;vertical-align:text-top}
	
  #manifestTable th,#manifestTable td {
    border: 1px solid #bcbcbc;
  }
 </style>
 
 <div id="main">
	<div class="page_site">정보조회 > <strong>서비스팩 설치</strong></div>
	<!-- 설치 관리자 -->
	<div id="isDefaultDirector"></div>
	<div class="pdt20"> 
		<div class="title fl">서비스팩 목록</div>
		<div class="fr"> 
			<!-- Btn -->
			<sec:authorize access="hasAuthority('DEPLOY_SERVICEPACK_INSTALL')">
			<span class="btn btn-primary" style="width:120px" id="installServicePack">설치</span>&nbsp; 
			</sec:authorize>
			<sec:authorize access="hasAuthority('DEPLOY_SERVICEPACK_DELETE')">
			<span class="btn btn-danger" style="width:120px" id="deleteServicePack">삭제</span>
			</sec:authorize>
			<!-- //Btn -->
		</div>
		<div id="w2ui_servicePackGrid" style="width:100%; height:610px"></div>	
	</div>
	
	<!-- 서비스팩 기본 정보 DIV -->
<div id="saveServicePackDiv" style="width:100%;height:100%; " hidden="true"  >
	<div rel="title"><b>서비스팩 설치</b></div>
	<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto; overflow-x:hidden;overflow-y:hidden">
        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
			<div class="panel panel-info">	
				<div class="panel-heading"><b>Manifest 정보</b></div>
				<div class="panel-body" style="height:600px; padding:5px 5% 10px 5%;">
					<div style="float: right; margin-top: 55px;">
						<label style="margin-right: 10px;">검색</label>
						<input type="text" name="search" style="width:270px;" required placeholder="키워드를 입력하세요. 공백 일 시 전체 검색" >
						<button class="btn"  onclick="searchBtn();" >검색</button>
					</div>
					<div  style="width:100%; height:34px; float:left; position:relative;  top:11px; ">
						<table id='manifestTable' class='table table-hover'>
							<thead>
								<tr class='active'>
								<th style='width:5.4%'></th>
								<th style='width:50px;'>배포 명</th>
								<th style='width:50px;'>파일 명</th>
								<th style='width:50px;'>설명</th></tr>
							</thead>
						</table>
					</div>
					<input type="hidden" name="deploymentName">
					<input type="hidden" name="deploymentFile">
					<input type="hidden" name="manifestId">
					<div class="manifestList" style="margin-top: 127px; height:470px; overflow:auto">
					</div>		
				</div>
			</div>
	    </div>
		<br/>
	    <div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn"  onclick="saveServicePackInfo();" >설치</button>
			<button class="btn"  onclick="w2popup.close();" >닫기</button>
	    </div>
	</div>
</div>

<!-- 설치화면 -->
<div id="installDiv" style="width:100%; height:100%;" hidden="true">
	<div rel="title">서비스팩 설치</div>
	<div rel="body" style="width:100%;height:100%;padding:15px 5px 0 5px;margin:0 auto;">
		<div style="width:95%;height:84%;float:left;display:inline-block;margin-top: 10px;">
			<textarea id="installLogs" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color:#FFF;margin-left:1%" readonly="readonly"></textarea>
		</div>
	</div>
	<div class="w2ui-buttons" rel="buttons" hidden="true">
		<button class="btn"  onclick="popupComplete();">닫기</button>
	</div>
</div>
<!-- End Popup -->
</div>