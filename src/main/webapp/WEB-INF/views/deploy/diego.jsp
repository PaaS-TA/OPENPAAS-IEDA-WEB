<!--
Diego
-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<link rel="stylesheet" type="text/css" href="<c:url value='/css/progress-step.css'/>"/>
<style type="text/css">
.w2ui-popup .w2ui-msg-body {
	background-color:#FFF;
}
.w2ui-popup div.w2ui-field{
	display:inline-block;
	text-align:left;
	width:100%;
}
</style>

<script type="text/javascript" src="<c:url value='/js/sockjs-0.3.4.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/stomp.js'/>"></script>

<script type="text/javascript">
	//private var
	var iaas = "";
	var diegoId = "";
	
	var defaultInfo = "";	
	var cfInfo = "";
	var diegoInfo = "";
	var etcdInfo = "";
	var networkInfo = "";
	var resourceInfo = "";
	
	var diegoReleases = "";
	var cfReleases = "";
	var gardenLinuxReleases = "";
	var etcdReleases = "";
	
	var stemcells = "";
	var deploymentFile = "";

	//Main View Event
	$(function() {

		// 기본 설치 관리자 정보 조회
		var bDefaultDirector = getDefaultDirector("<c:url value='/directors/default'/>");

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
			    			if ( record.deployStatus == 'done' )
			    				return '<span class="btn btn-primary" style="width:60px">성공</span>';
			    			else	if ( record.deployStatus == 'error' )
			    				return '<span class="btn btn-primary" style="width:60px">오류</span>';
			    			else	if ( record.deployStatus == 'cancelled' )
			    				return '<span class="btn btn-primary" style="width:60px">취소</span>';
			    			else	if ( record.deployStatus == 'deploying' )
			    				return '<span class="btn btn-primary" style="width:60px">배포중</span>';
							else
			    				return 'N/A';
			    	   }
					}
				, {field: 'taskId', caption: 'TASK ID', size: '100px', hidden: true}
				, {field:'deploymentName', caption:'배포명', size:'100px'}
				, {field:'iaas', caption:'IaaS', size:'100px'}
				, {field:'directorUuid', caption:'설치관리자 UUID', size:'220px'}
				, {field:'diegoRelease', caption:'Diego 릴리즈', size:'100px'
					, render :function(record){
						if( !checkEmpty(record.diegoReleaseName) && !checkEmpty(record.diegoReleaseVersion) ){
							return record.diegoReleaseName +"/"+ record.diegoReleaseVersion;
						}
					}}
				, {field:'cfRelease', caption:'CF 릴리즈', size:'100px'
					, render :function(record){
						if( !checkEmpty(record.cfReleaseName) && !checkEmpty(record.cfReleaseVersion) ){
							return record.cfReleaseName +"/"+ record.cfReleaseVersion;
						}
					}}
				, {field:'gardenLinuxRelease', caption:'Garden Linux 릴리즈', size:'130px'
					, render :function(record){
						if( !checkEmpty(record.gardenLinuxReleaseName) && !checkEmpty(record.gardenLinuxReleaseVersion) ){
							return record.gardenLinuxReleaseName +"/"+ record.gardenLinuxReleaseVersion;
						}
					}}
				, {field:'etcdRelease', caption:'ETCD 릴리즈', size:'100px'
					, render :function(record){
						if( !checkEmpty(record.etcdReleaseName) && !checkEmpty(record.etcdReleaseVersion) ){
							return record.etcdReleaseName +"/"+ record.etcdReleaseVersion;
						}
					}}
				, {field:'domain', caption:'도메인', size:'100px'}
				, {field:'deployment', caption:'CF 배포명', size:'100px'}
				, {field:'etcdMachines', caption:'ETCD 서버 IPS', size:'100px'}
				, {field:'natsMachines', caption:'NATS 서버 IPS', size:'100px'}
				, {field:'consulServersLan', caption:'CONSUL 서버 IPS', size:'120px'}
				, {field:'subnetStatic', caption:'VM할당 IP대역', size:'120px'
					, render :function(record){
						if( !checkEmpty(record.subnetStaticFrom) && !checkEmpty(record.subnetStaticTo) ){
							return record.subnetStaticFrom + " - " + record.subnetStaticTo;
						}
					}}
				, {field:'subnetReserved', caption:'할당된 IP대역', size:'120px'
					, render :function(record){
						if( !checkEmpty(record.subnetReservedFrom) && !checkEmpty(record.subnetReservedTo) ){
							return record.subnetReservedFrom + " - " + record.subnetReservedTo;
						}
					}}
				, {field:'subnetRange', caption:'서브넷 범위', size:'100px'}
				, {field:'subnetGateway', caption:'게이트웨이', size:'100px'}
				, {field:'subnetDns', caption:'DNS', size:'100px'}
				, {field:'subnetId', caption:'서브넷 ID(NET ID)', size:'100px'}
				, {field:'diegoServers', caption:'SSH 서버 IP', size:'100px'}
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
			       				//return '<a style="color:#333;" onClick="getDownloadDeploymentFile(\''+record.deploymentFile+'\')">' + record.deploymentFile + '</a>';
			       				//return '<a style="color:#333;" href="/common/downloadDeploymentFile/'+record.deploymentFile+'">' + record.deploymentFile + '</a>';
								return record.deploymentFile;
							}
			    			else {
			    				return 'N/A';
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
			}
		});

		$("#installBtn").click(function() {
			var directorName = $("#directorName").text().toUpperCase();
			
			if (directorName.indexOf("AWS") > 0) {
				iaas = "OPENSTACk";
				//iaas = "AWS";
				defaultPopup();
			} else if (directorName.indexOf("OPENSTACK") > 0) {
				iaas = "OPENSTACk";
				defaultPopup();
			}
			else{
				selectIaas();
			}
		});

		//Diego 수정
		$("#modifyBtn").click(function() {
			if ($("#modifyBtn").attr('disabled') == "disabled")
				return;
			
			w2confirm({
				title :"DIEGO 설치",
				msg :"DIEGO설치 정보를 수정하시겠습니까?",
				yes_text :"확인",
				yes_callBack :function(event) {
					var selected = w2ui['config_diegoGrid'].getSelection();

					if (selected.length == 0) {
						w2alert("선택된 정보가 없습니다.", "DIEGO 설치");
						return;
					} else {
						var record = w2ui['config_diegoGrid'].get(selected);
						iaas = record.iaas;
						getDiegoData(record);
					}
				},
				no_text :"취소"
			});
		});

		//Diego 삭제
		$("#deleteBtn").click(
				function() {
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
							deletePopup(record);
						},
						no_text :"취소"
					});
		});
		
		doSearch();
	});

	//***** Start Main View Event Function ****/

	//조회기능
	function doSearch() {
		w2ui['config_diegoGrid'].load("<c:url value='/deploy/diegoList'/>",
			function() {
				doButtonStyle();
			});
	}

	//Iaas Select Confirm
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

	//수정 기능 - 데이터 조회
	function getDiegoData(record) {
		console.log("@@@" + record.iaas.toLowerCase());
		var url = "/diego/" + record.iaas.toLowerCase() + "/" + record.id;
		$.ajax({
			type :"GET",
			url :url,
			contentType :"application/json",
			success :function(data, status) {
				if (data != null && data != "") {
					initSetting();
					if (record.iaas.toUpperCase() == "AWS"){
						//iaas = "AWS";
						setAwsData(data.contents);
					}
					else if (record.iaas.toUpperCase() == "OPENSTACK"){
						//iaas = "OPENSTACK";
						setOpenstackData(data.contents);
					}
				}
			},
			error :function(request, status, error) {
				var errorResult = JSON.parse(request.responseText);
				w2alert(errorResult.message, "DIEGO 수정");
			}
		});
	}

	//삭제 팝업
	function deletePopup(record){
	
		var requestParameter = {iaas:record.iaas, id:record.id};
		
		if ( record.deployStatus == null || record.deployStatus == '' ) {
			// 단순 레코드 삭제
			var url = "/diego/delete";
			$.ajax({
				type :"DELETE",
				url :url,
				data :JSON.stringify(requestParameter),
				contentType :"application/json",
				success :function(data, status) {
					doSearch();
				},
				error :function(request, status, error) {
					var errorResult = JSON.parse(request.responseText);
					w2alert(errorResult.message, "DIEGO 삭제");
				}
			});
			
		} else {
			var message = "";
			var body = '<textarea id="deleteLogs" style="width:95%;height:90%;overflow-y:visible;resize:none;background-color:#FFF; margin:2%" readonly="readonly"></textarea>';
			
			w2popup.open({
				width :610,
				height :500,
				title :"<b>DIEGO 삭제</b>",
				body  :body,
				buttons :'<button class="btn" style="float:right; padding-right:15%;" onclick="popupComplete();">닫기</button>',
				showMax :true,
				onOpen :function(event){
					event.onComplete = function(){
						var socket = new SockJS('/diegoDelete');
						deleteClient = Stomp.over(socket); 
						deleteClient.connect({}, function(frame) {
							deleteClient.subscribe('/diego/diegoDelete', function(data){
								
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
							    		
							    		
							    		
							    		deleteClient.disconnect();
										w2alert(message, "DIEGO 삭제");
							       	}
					        	}
					        	
					        });
							deleteClient.send('/send/diegoDelete', {}, JSON.stringify(requestParameter));
					    });
					}
				},
				onClose :function (event){
					event.onComplete= function(){
						$("textarea").text("");
						w2ui['config_diegoGrid'].reset();
						deleteClient.disconnect();
						deleteClient = "";
						doSearch();
					}
				}
			});
		}		
	}

	/***************************************
	 **********     Aws Process    **********
	 ***************************************/
	 
	// AWS AwsPopup Data Setting
	function setAwsData(contents) {
		diegoId = contents.id;
		iaas = "AWS";
		defaultInfo = {
			iaas						:contents.iaas,
			deploymentName 				:contents.deploymentName,
			directorUuid 				:contents.directorUuid,
			diegoReleaseName 			:contents.diegoReleaseName,
			diegoReleaseVersion 		:contents.diegoReleaseVersion,
			cfReleaseName 				:contents.cfReleaseName,
			cfReleaseVersion 			:contents.cfReleaseVersion,
			gardenLinuxReleaseName 		:contents.gardenLinuxReleaseName,
			gardenLinuxReleaseVersion 	:contents.gardenLinuxReleaseVersion,
			etcdReleaseName 			:contents.etcdReleaseName,
			etcdReleaseVersion 			:contents.etcdReleaseVersion,
		}
		
		cfInfo = {
			iaas				:contents.iaas,			
			domain 				:contents.domain,
			deployment			:contents.deployment,
			secret 				:contents.secret,			
			etcdMachines 		:contents.etcdMachines,
			natsMachines 		:contents.natsMachines,
			consulServersLan 	:contents.consulServersLan,
			consulAgentCert 	:contents.consulAgentCert,
			consulAgentKey 		:contents.consulAgentKey,
			consulCaCert 		:contents.consulCaCert,
			consulEncryptKeys 	:contents.consulEncryptKeys,
			consulServerCert 	:contents.consulServerCert,
			consulServerKey 	:contents.consulServerKey,
		}
		
		diegoInfo = {
			id 					:contents.id,
			iaas				:contents.iaas,
			diegoCaCert			:contents.diegoCaCert,
			diegoClientCert		:contents.diegoClientCert,
			diegoClientKey		:contents.diegoClientKey,
			diegoEncryptionKeys	:contents.diegoEncryptionKeys,
			diegoServerCert		:contents.diegoServerCert,
			diegoServerKey		:contents.diegoServerKey,
			diegoHostKey 		:contents.diegoHostKey,
		}
		
		etcdInfo = {
			id 					:contents.id,
			iaas				:contents.iaas,
			etcdClientCert		:contents.etcdClientCert,
			etcdClientKey		:contents.etcdClientKey,
			etcdPeerCaCert		:contents.etcdPeerCaCert,
			etcdPeerCert		:contents.etcdPeerCert,
			etcdPeerKey			:contents.etcdPeerKey,
			etcdServerCert		:contents.etcdServerCert,
			etcdServerKey		:contents.etcdServerKey
			
		}
		
		networkInfo = {
			id 					:contents.id,
			subnetRange 		:contents.subnetRange,
			subnetGateway 		:contents.subnetGateway,
			subnetDns 			:contents.subnetDns,
			subnetReservedFrom 	:contents.subnetReservedFrom,
			subnetReservedTo 	:contents.subnetReservedTo,
			subnetStaticFrom 	:contents.subnetStaticFrom,
			subnetStaticTo 		:contents.subnetStaticTo,
			subnetId 			:contents.subnetId,
			cloudSecurityGroups :contents.cloudSecurityGroups,
		}
	
		resourceInfo = {
			id 					:contents.id,
			stemcellName 		:contents.stemcellName,
			stemcellVersion 	:contents.stemcellVersion,
			boshPassword 		:contents.boshPassword,
			deploymentFile 		:contents.deploymentFile,
			deployStatus 		:contents.deployStatus,
		}
		
		defaultPopup();
	}
	 
	//기본정보 팝업
	function defaultPopup() {
		//var defaultIaas = (checkEmpty(iaas)) ? defaultInfo.iaas.toUpperCase():iaas.toUpperCase(); 
		$("#defaultInfoDiv").w2popup({
			width : 950,
			height :490,
			title :"DIEGO 설치 (" + iaas + ")",
			modal :true,
			showMax :false,
			onOpen :function(event) {
				event.onComplete = function() {
					if (defaultInfo != "") {
						$(".w2ui-msg-body input[name='deploymentName']").val(defaultInfo.deploymentName);
						$(".w2ui-msg-body input[name='directorUuid']").val(defaultInfo.directorUuid);
					}
					else{
						if( !checkEmpty($("#directorUuid").text()) ){
							$(".w2ui-msg-body input[name='directorUuid']").val($("#directorUuid").text());
						}
					}
					
					getReleases();	
				}
			},
			onClose :function(event) {
				event.onComplete = function() {
					initSetting();
				}
			}
		});
	}

	//기본정보 저장
	function saveDefaultInfo() {
		var diegoRelease = $(".w2ui-msg-body input[name='diegoReleases']").val();
		var cfRelease = $(".w2ui-msg-body input[name='cfReleases']").val();
		var gardenLinuxRelease = $(".w2ui-msg-body input[name='gardenLinuxReleases']").val();
		var etcdRelease = $(".w2ui-msg-body input[name='etcdReleases']").val();
		
		defaultInfo = {
					id 							: (diegoId) ? diegoId :"",
					iaas 						: iaas.toUpperCase(),
					deploymentName 				: $(".w2ui-msg-body input[name='deploymentName']").val(),
					directorUuid 				: $(".w2ui-msg-body input[name='directorUuid']").val(),
					diegoReleaseName 			: diegoRelease.split("/")[0],
					diegoReleaseVersion			: diegoRelease.split("/")[1],
					cfReleaseName 				: cfRelease.split("/")[0],
					cfReleaseVersion			: cfRelease.split("/")[1],
					gardenLinuxReleaseName 		: gardenLinuxRelease.split("/")[0],
					gardenLinuxReleaseVersion	: gardenLinuxRelease.split("/")[1],
					etcdReleaseName 			: etcdRelease.split("/")[0],
					etcdReleaseVersion			: etcdRelease.split("/")[1],
		}
		
		if (popupValidation()) {
			$.ajax({
				type :"PUT",
				url :(iaas.toUpperCase() =="AWS") ? "/diego/saveAws":"/diego/saveOpenstack",
				contentType :"application/json",
				data :JSON.stringify(defaultInfo),
				success :function(data, status) {
					diegoId = data.id;
					cfPopup();
				},
				error :function(e, status) {
					w2alert("Diego ("+iaas.toUpperCase()+") 설정 등록에 실패 하였습니다.", "DIEGO 설치");
				}
			});
		}
	}

	//CF 팝업
	function cfPopup() {
		$("#cfInfoDiv").w2popup({
			width  : 950,
			height	:800,
			title 	:"DIEGO 설치 (" + iaas.toUpperCase() + ")",
			modal 	:true,
			showMax :false,
			onOpen 	:function(event) {
				event.onComplete = function() {
					if ( cfInfo != "") {
						$(".w2ui-msg-body input[name='domain']").val(cfInfo.domain);
						$(".w2ui-msg-body input[name='deployment']").val(cfInfo.deployment);
						$(".w2ui-msg-body input[name='secret']").val(cfInfo.secret);
						$(".w2ui-msg-body input[name='etcdMachines']").val(cfInfo.etcdMachines);
						$(".w2ui-msg-body input[name='natsMachines']").val(cfInfo.natsMachines);
						$(".w2ui-msg-body input[name='consulServersLan']").val(cfInfo.consulServersLan);
						$(".w2ui-msg-body textarea[name='consulAgentCert']").val(cfInfo.consulAgentCert);
						$(".w2ui-msg-body textarea[name='consulAgentKey']").val(cfInfo.consulAgentKey);
						$(".w2ui-msg-body textarea[name='consulCaCert']").val(cfInfo.consulCaCert);
						$(".w2ui-msg-body input[name='consulEncryptKeys']").val(cfInfo.consulEncryptKeys);
						$(".w2ui-msg-body textarea[name='consulServerCert']").val(cfInfo.consulServerCert);
						$(".w2ui-msg-body textarea[name='consulServerKey']").val(cfInfo.consulServerKey);
					}
				}
			},
			onClose :function(event) {
				event.onComplete = function() {
					initSetting();
				}
			}
		});
	}

	//CF 정보 저장
	function saveCfInfo(type) {
		cfInfo = {
					id 					: (diegoId) ? diegoId :"",
					iaas 				: iaas.toUpperCase(),
					domain				: $(".w2ui-msg-body input[name='domain']").val(),
					deployment			: $(".w2ui-msg-body input[name='deployment']").val(),
					secret				: $(".w2ui-msg-body input[name='secret']").val(),
					etcdMachines		: $(".w2ui-msg-body input[name='etcdMachines']").val(),
					natsMachines		: $(".w2ui-msg-body input[name='natsMachines']").val(),
					consulServersLan	: $(".w2ui-msg-body input[name='consulServersLan']").val(),
					consulAgentCert		: $(".w2ui-msg-body textarea[name='consulAgentCert']").val(),
					consulAgentKey		: $(".w2ui-msg-body textarea[name='consulAgentKey']").val(),
					consulCaCert		: $(".w2ui-msg-body textarea[name='consulCaCert']").val(),
					consulEncryptKeys	: $(".w2ui-msg-body input[name='consulEncryptKeys']").val(),
					consulServerCert	: $(".w2ui-msg-body textarea[name='consulServerCert']").val(),
					consulServerKey		: $(".w2ui-msg-body textarea[name='consulServerKey']").val()
		}
		
		if( type == 'after'){
			if (popupValidation()) {
				$.ajax({
					type :"PUT",
					url :( iaas.toUpperCase() == "AWS") ? "/diego/saveAwsCf":"/diego/saveOpenstackCf",
					contentType :"application/json",
					data :JSON.stringify(cfInfo),
					success :function(data, status) {
						diegoId = data.id;
						diegoPopup();
					},
					error :function(e, status) {
						w2alert("Diego ("+iaas.toUpperCase()+") 설정 등록에 실패 하였습니다.", "DIEGO 설치");
					}
				});
			}
		}
		else{
			defaultPopup();
		}
	}
	
	//Diego 팝업
	function diegoPopup(){
		$("#diegoInfoDiv").w2popup({
			width  : 950,
			height 	:780,
			title 	:"DIEGO 설치 ("+iaas.toUpperCase()+")",
			modal 	:true,
			showMax :false,
			onOpen :function(event) {
				event.onComplete = function() {
					if (diegoInfo != "") {
						//2.1 Diego 정보	
						$(".w2ui-msg-body textarea[name='diegoCaCert']").val(diegoInfo.diegoCaCert);
						$(".w2ui-msg-body textarea[name='diegoClientCert']").val(diegoInfo.diegoClientCert);
						$(".w2ui-msg-body textarea[name='diegoClientKey']").val(diegoInfo.diegoClientKey);
						$(".w2ui-msg-body input[name='diegoEncryptionKeys']").val(diegoInfo.diegoEncryptionKeys);
						$(".w2ui-msg-body textarea[name='diegoServerCert']").val(diegoInfo.diegoServerCert);
						$(".w2ui-msg-body textarea[name='diegoServerKey']").val(diegoInfo.diegoServerKey);
						$(".w2ui-msg-body textarea[name='diegoHostKey']").val(diegoInfo.diegoHostKey);
					}	
				}
			},
			onClose :function(event) {
				event.onComplete = function() {
					initSetting();
				}
			}
		});
	}
	
	//Diego 정보 저장
	function saveDiegoInfo(type){
		diegoInfo = {
				id 					: diegoId,
				iaas				: iaas.toUpperCase(),
				diegoCaCert			: $(".w2ui-msg-body textarea[name='diegoCaCert']").val(),
				diegoClientCert		: $(".w2ui-msg-body textarea[name='diegoClientCert']").val(),
				diegoClientKey		: $(".w2ui-msg-body textarea[name='diegoClientKey']").val(),
				diegoEncryptionKeys : $(".w2ui-msg-body input[name='diegoEncryptionKeys']").val(),
				diegoServerCert		: $(".w2ui-msg-body textarea[name='diegoServerCert']").val(),
				diegoCaCert			: $(".w2ui-msg-body textarea[name='diegoCaCert']").val(),
				diegoServerKey 		: $(".w2ui-msg-body textarea[name='diegoServerKey']").val(),
				diegoHostKey		: $(".w2ui-msg-body textarea[name='diegoHostKey']").val()
		}
		
		if( type == 'after'){
			if (popupValidation()) {
				//ajax AwsInfo Save
				$.ajax({
					type :"PUT",
					url :(iaas.toUpperCase() == "AWS" ) ? "/diego/saveAwsDiego":"/diego/saveOpenstackDiego",
					contentType :"application/json",
					data :JSON.stringify(diegoInfo),
					success :function(data, status) {
						etcdPopup();
					},
					error :function(e, status) {
						w2alert("Diego ("+iaas.toUpperCase()+") 등록에 실패 하였습니다.", "DIEGO 설치");
					}
				});
			}
		}
		else{
			cfPopup();
		}
	}
	
	//ETCD 팝업
	function etcdPopup(){
		$("#etcdInfoDiv").w2popup({
			width  : 950,
			height 	:750,
			title 	:"DIEGO 설치 ("+iaas.toUpperCase()+")",
			modal 	:false,
			showMax :false,
			onOpen 	:function(event) {
				event.onComplete = function() {
					if (etcdInfo != "") {
						$(".w2ui-msg-body textarea[name='etcdClientCert']").val(etcdInfo.etcdClientCert);
						$(".w2ui-msg-body textarea[name='etcdClientKey']").val(etcdInfo.etcdClientKey);
						$(".w2ui-msg-body textarea[name='etcdPeerCaCert']").val(etcdInfo.etcdPeerCaCert);
						$(".w2ui-msg-body textarea[name='etcdPeerCert']").val(etcdInfo.etcdPeerCert);
						$(".w2ui-msg-body textarea[name='etcdPeerKey']").val(etcdInfo.etcdPeerKey);
						$(".w2ui-msg-body textarea[name='etcdServerCert']").val(etcdInfo.etcdServerCert);
						$(".w2ui-msg-body textarea[name='etcdServerKey']").val(etcdInfo.etcdServerKey);
					}					
				}
			},
			onClose :function(event) {
				event.onComplete = function() {
					initSetting();
				}
			}
		});
	}
	
	//ETCD 정보 저장
	function saveEtcdInfo(type){
		etcdInfo = {
				id 					: diegoId,
				iaas				: iaas.toUpperCase(),
				etcdClientCert		: $(".w2ui-msg-body textarea[name='etcdClientCert']").val(),
				etcdClientKey		: $(".w2ui-msg-body textarea[name='etcdClientKey']").val(),
				etcdPeerCaCert		: $(".w2ui-msg-body textarea[name='etcdPeerCaCert']").val(),
				etcdPeerCert		: $(".w2ui-msg-body textarea[name='etcdPeerCert']").val(),
				etcdPeerKey			: $(".w2ui-msg-body textarea[name='etcdPeerKey']").val(),
				etcdServerCert		: $(".w2ui-msg-body textarea[name='etcdServerCert']").val(),
				etcdServerKey		: $(".w2ui-msg-body textarea[name='etcdServerKey']").val(),
		}
		
		if( type == 'after'){
			if (popupValidation()) {
				$.ajax({
					type	:"PUT",
					url 	:(iaas.toUpperCase() == "AWS") ?"/diego/saveAwsEtcd":"/diego/saveOpenstackEtcd",
					contentType :"application/json",
					data :JSON.stringify(etcdInfo),
					success :function(data, status) {
						if(iaas.toUpperCase() == "AWS"){
							awsNetworkPopup();
						}
						else{
							openstackNetworkPopup();
						}
					},
					error :function(e, status) {
						w2alert("ETCD 정보 등록에 실패 하였습니다.", "DIEGO 설치");
					}
				});
			}
		}
		else{
			diegoPopup();
		}
	}
	
	//AWS NETWORK 팝업
	function awsNetworkPopup(){
		$("#awsNetworkInfoDiv").w2popup({
			width  : 950,
			height 	:500,
			title 	:"DIEGO 설치 (AWS)",
			modal 	:true,
			showMax :false,
			onOpen 	:function(event) {
				event.onComplete = function() {
					if (networkInfo != "") {
						$(".w2ui-msg-body input[name='subnetRange']").val(networkInfo.subnetRange);
						$(".w2ui-msg-body input[name='subnetGateway']").val(networkInfo.subnetGateway);
						$(".w2ui-msg-body input[name='subnetDns']").val(networkInfo.subnetDns);
						$(".w2ui-msg-body input[name='subnetReservedFrom']").val(networkInfo.subnetReservedFrom);
						$(".w2ui-msg-body input[name='subnetReservedTo']").val(networkInfo.subnetReservedTo);
						$(".w2ui-msg-body input[name='subnetStaticFrom']").val(networkInfo.subnetStaticFrom);
						$(".w2ui-msg-body input[name='subnetStaticTo']").val(networkInfo.subnetStaticTo);
						$(".w2ui-msg-body input[name='subnetId']").val(networkInfo.subnetId);
						$(".w2ui-msg-body input[name='cloudSecurityGroups']").val(networkInfo.cloudSecurityGroups);
					}					
				}
			},
			onClose :function(event) {
				event.onComplete = function() {
					initSetting();
				}
			}
		});
	}
	
	// AWS NETWORK save Aws Network
	function saveAwsNetworkInfo(type) {
		networkInfo = {
				id 					: diegoId,
				iaas				: iaas.toUpperCase(),
				subnetStaticFrom	: $(".w2ui-msg-body input[name='subnetStaticFrom']").val(),
				subnetStaticTo		: $(".w2ui-msg-body input[name='subnetStaticTo']").val(),
				subnetReservedFrom	: $(".w2ui-msg-body input[name='subnetReservedFrom']").val(),
				subnetReservedTo	: $(".w2ui-msg-body input[name='subnetReservedTo']").val(),
				
				subnetRange			: $(".w2ui-msg-body input[name='subnetRange']").val(),
				subnetGateway		: $(".w2ui-msg-body input[name='subnetGateway']").val(),
				subnetDns			: $(".w2ui-msg-body input[name='subnetDns']").val(),
				subnetId			: $(".w2ui-msg-body input[name='subnetId']").val(),
				cloudSecurityGroups	: $(".w2ui-msg-body input[name='cloudSecurityGroups']").val(),
		}

		if (type == 'after') {
			if (popupValidation()) {
				$.ajax({
					type :"PUT",
					url :"/diego/saveAwsNetwork",
					contentType :"application/json",
					async :true,
					data :JSON.stringify(networkInfo),
					success :function(data, status) {
						resourcePopup();
					},
					error :function(e, status) {
						w2alert("Diego Network 등록에 실패 하였습니다.", "Diego 설치");
					}
				});
			}
		} else if (type == 'before') {
			etcdPopup();
		}
	}

	//RESOURCE 팝업
	function resourcePopup() {
		$("#resourceInfoDiv").w2popup({
			width  : 950,
			height	:370,
			title 	:"DIEGO 설치 ("+iaas.toUpperCase()+")",
			modal 	:true,
			showMax :false,
			onOpen :function(event) {
				event.onComplete = function() {
					if (resourceInfo != "") {
						$(".w2ui-msg-body input[name='boshPassword']").val(resourceInfo.boshPassword);
					}
					w2popup.lock("스템셀을 조회 중입니다.", true);
					getStamcellList();
				}
			},
			onClose :function(event) {
				event.onComplete = function() {
					initSetting();
				}
			}
		});
	}

	//Resource 정보 저장
	function saveResourceInfo(type) {
		var stemcellInfos = $(".w2ui-msg-body input[name='stemcells']").val().split("/");
		resourceInfo = {
				id 					:diegoId,
				stemcellName 		:stemcellInfos[0],
				stemcellVersion 	:stemcellInfos[1],
				boshPassword 		: $(".w2ui-msg-body input[name='boshPassword']").val()
		}

		if (type == 'after') {
			if(popupValidation()){	
				var url = (iaas.toUpperCase() == "AWS") ? "/diego/saveAwsResource":"/diego/saveOpenstackResource";
				//Server send Diego Info
				$.ajax({
					type 		:"PUT",
					url 		:url,
					contentType :"application/json",
					async 		:true,
					data 		:JSON.stringify(resourceInfo),
					success 	:function(data, status) {
						deploymentFile = data.deploymentFile;
						deployPopup();
					},
					error :function(e, status) {
						w2alert("Diego ("+iaas.toUpperCase()+") Resource 등록에 실패 하였습니다.", "Diego 설치");
					}
				});
			}
		} else if (type == 'before') {
			if( iaas.toUpperCase() == "AWS") 
				awsNetworkPopup();
			else if( iaas.toUpperCase() == "OPENSTACK")
				openstackNetworkPopup();
		}
	}
	/********************************* AWS END ********************************************/
	
	
	/***************************************
	*******     Openstack Process    *******
	***************************************/
	//OPENSTACK 조회정보 세팅
	function setOpenstackData(contents) {
		diegoId = contents.id;
		iaas = "OPENSTACK";
		defaultInfo = {
			iaas						:contents.iaas,
			deploymentName 				:contents.deploymentName,
			directorUuid 				:contents.directorUuid,
			diegoReleaseName 			:contents.diegoReleaseName,
			diegoReleaseVersion 		:contents.diegoReleaseVersion,
			cfReleaseName 				:contents.cfReleaseName,
			cfReleaseVersion 			:contents.cfReleaseVersion,
			gardenLinuxReleaseName 		:contents.gardenLinuxReleaseName,
			gardenLinuxReleaseVersion 	:contents.gardenLinuxReleaseVersion,
			etcdReleaseName 			:contents.etcdReleaseName,
			etcdReleaseVersion 			:contents.etcdReleaseVersion,
		}
		
		cfInfo = {
			iaas				:contents.iaas,			
			domain 				:contents.domain,
			deployment 			:contents.deployment,
			secret 				:contents.secret,			
			etcdMachines 		:contents.etcdMachines,
			natsMachines 		:contents.natsMachines,
			consulServersLan 	:contents.consulServersLan,
			consulAgentCert 	:contents.consulAgentCert,
			consulAgentKey 		:contents.consulAgentKey,
			consulCaCert 		:contents.consulCaCert,
			consulEncryptKeys 	:contents.consulEncryptKeys,
			consulServerCert 	:contents.consulServerCert,
			consulServerKey 	:contents.consulServerKey,
			
		}
		
		diegoInfo = {
			id 					:contents.id,
			iaas				:contents.iaas,
			diegoCaCert			:contents.diegoCaCert,
			diegoClientCert		:contents.diegoClientCert,
			diegoClientKey		:contents.diegoClientKey,
			diegoEncryptionKeys	:contents.diegoEncryptionKeys,
			diegoServerCert		:contents.diegoServerCert,
			diegoServerKey		:contents.diegoServerKey,
			diegoHostKey 		:contents.diegoHostKey,
		}
		
		etcdInfo = {
			id 					:contents.id,
			iaas				:contents.iaas,
			etcdClientCert		:contents.etcdClientCert,
			etcdClientKey		:contents.etcdClientKey,
			etcdPeerCaCert		:contents.etcdPeerCaCert,
			etcdPeerCert		:contents.etcdPeerCert,
			etcdPeerKey			:contents.etcdPeerKey,
			etcdServerCert		:contents.etcdServerCert,
			etcdServerKey		:contents.etcdServerKey
			
		}
		
		networkInfo = {
			id 					:contents.id,
			subnetRange 		:contents.subnetRange,
			subnetGateway 		:contents.subnetGateway,
			subnetDns 			:contents.subnetDns,
			subnetReservedFrom 	:contents.subnetReservedFrom,
			subnetReservedTo 	:contents.subnetReservedTo,
			subnetStaticFrom 	:contents.subnetStaticFrom,
			subnetStaticTo 		:contents.subnetStaticTo,
			cloudNetId 			:contents.cloudNetId,
			cloudSecurityGroups :contents.cloudSecurityGroups,
			
		}
	
		resourceInfo = {
			id 					:contents.id,
			stemcellName 		:contents.stemcellName,
			stemcellVersion 	:contents.stemcellVersion,
			boshPassword 		:contents.boshPassword,
			deploymentFile 		:contents.deploymentFile,
			deployStatus 		:contents.deployStatus,
		}
		
		defaultPopup();
	}
	
	//OPENSTACK NETWORK 팝업
	function openstackNetworkPopup(){
		$("#openstackNetworkInfoDiv").w2popup({
			width  : 950,
			height 	:500,
			title 	:"DIEGO 설치 (OPENSTACK)",
			modal 	:true,
			showMax :false,
			onOpen 	:function(event) {
				event.onComplete = function() {
					if (networkInfo != "") {
						$(".w2ui-msg-body input[name='subnetStaticFrom']").val(networkInfo.subnetStaticFrom);
						$(".w2ui-msg-body input[name='subnetStaticTo']").val(networkInfo.subnetStaticTo);
						$(".w2ui-msg-body input[name='subnetReservedFrom']").val(networkInfo.subnetReservedFrom);
						$(".w2ui-msg-body input[name='subnetReservedTo']").val(networkInfo.subnetReservedTo);
						$(".w2ui-msg-body input[name='subnetRange']").val(networkInfo.subnetRange);
						$(".w2ui-msg-body input[name='subnetGateway']").val(networkInfo.subnetGateway);
						$(".w2ui-msg-body input[name='subnetDns']").val(networkInfo.subnetDns);
						$(".w2ui-msg-body input[name='cloudNetId']").val(networkInfo.cloudNetId);
						$(".w2ui-msg-body input[name='cloudSecurityGroups']").val(networkInfo.cloudSecurityGroups);
					}					
				}
			},
			onClose :function(event) {
				event.onComplete = function() {
					initSetting();
				}
			}
		});
	}
	
	//오픈스텍 NETWORK 정보 저장
	function saveOpenstackNetworkInfo(type) {
		networkInfo = {
				id 					: diegoId,
				iaas				: iaas,
				subnetRange			: $(".w2ui-msg-body input[name='subnetRange']").val(),
				subnetGateway		: $(".w2ui-msg-body input[name='subnetGateway']").val(),
				subnetDns			: $(".w2ui-msg-body input[name='subnetDns']").val(),
				subnetReservedFrom	: $(".w2ui-msg-body input[name='subnetReservedFrom']").val(),
				subnetReservedTo	: $(".w2ui-msg-body input[name='subnetReservedTo']").val(),
				subnetStaticFrom	: $(".w2ui-msg-body input[name='subnetStaticFrom']").val(),
				subnetStaticTo		: $(".w2ui-msg-body input[name='subnetStaticTo']").val(),
				cloudNetId			: $(".w2ui-msg-body input[name='cloudNetId']").val(),
				cloudSecurityGroups	: $(".w2ui-msg-body input[name='cloudSecurityGroups']").val(),
		}
	
		if (type == 'after') {
			if (popupValidation()) {
				//Server send Diego Info
				$.ajax({
					type :"PUT",
					url :"/diego/saveOpenstackNetwork",
					contentType :"application/json",
					async :true,
					data :JSON.stringify(networkInfo),
					success :function(data, status) {
						resourcePopup();
					},
					error :function(e, status) {
						w2alert("Diego (OPENSTACK) Network 등록에 실패 하였습니다.", "Diego 설치");
					}
				});
			}
		} else if (type == 'before') {
			etcdPopup();
		}
	}
	
	/*******************************  OPENSTACK END  ****************************************/
	
	// DEPLOY Confirm
	function diegoDeploy(type) {
		//Deploy 단에서 저장할 데이터가 있는지 확인 필요
		//Confirm 설치하시겠습니까?
		if (type == 'before' && iaas == "AWS") {
			resourcePopup();
			return;
		} else if (type == 'before' && iaas == "OPENSTACK") {
			resourcePopup();
			return;
		}

		w2confirm({
			msg :"설치하시겠습니까?",
			title :w2utils.lang('DIEGO 설치'),
			yes_text :"예",
			no_text :"아니오",
			yes_callBack :installPopup
		});
	}
	
	// DEPLOY POPUP
	function deployPopup() {
		var deployDiv = $("#deployDiv");
		deployDiv.w2popup({
			width : 950,
			height :520,
			modal :true,
			showMax :true,
			onClose :initSetting,
			onOpen :function(event) {
				event.onComplete = function() {
					getDeployInfo();
				}
			}
		});
	}

	// DEPLOY Get Deployment File
	function getDeployInfo() {
		$.ajax({
			type :"POST",
			url :"/common/getDeployInfo",
			contentType :"application/json",
			async :true,
			data :deploymentFile,
			success :function(data, status) {
				if (status == "success") {
					$(".w2ui-msg-body #deployInfo").text(data);
				} else if (status == "204") {
					w2alert("배포파일이 존재하지 않습니다.", "DIEGO 설치");
				}
			},
			error :function(e, status) {
				w2alert("Temp 파일을 가져오는 중 오류가 발생하였습니다. ", "DIEGO 설치");
			}
		});
	}

	// INSTALL POPUP
	function installPopup(){
		
		var deploymentName = defaultInfo.deploymentName;
		var message = "DIEGO(배포명:" + deploymentName +  ") ";
		
		var requestParameter = {
				id :diegoId,
				iaas:iaas
		};
		
		$("#installDiv").w2popup({
			width 	: 950,
			height 	: 520,
			modal	:true,
			showMax :true,
			onOpen :function(event){
				event.onComplete = function(){
					//deployFileName
					var socket = new SockJS('/diegoInstall');
					installClient = Stomp.over(socket); 
					installClient.connect({}, function(frame) {
						console.log('Connected Frame :' + frame);
				        installClient.subscribe('/diego/diegoInstall', function(data){
				        	
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
						    		
						    		$('.w2ui-msg-buttons #deployPopupBtn').prop("disabled", false);
						    		
						    		installClient.disconnect();
									w2alert(message, "DIEGO 설치");
						       	}
				        	}

				        });
				        installClient.send('/send/diegoInstall', {}, JSON.stringify(requestParameter));
				    });
				}
			}
		});
	}

	function getReleases(){
		//화면 LOCK
		w2popup.lock("릴리즈를 조회 중입니다.", true);
		getDiegoRelease(); //순차적으로 조회
	}
	
	//DIEGO 릴리즈 조회
	function getDiegoRelease() {
		
		$.ajax({
			type :"GET",
			url :"/release/getReleaseList/diego",
			contentType :"application/json",
			async :true,
			success :function(data, status) {
				console.log("DIEGO Releases List");
				diegoReleases = new Array();
				data.records.map(function(obj) {
					diegoReleases.push(obj.name + "/" + obj.version);
				});
				
				getCfRelease();
			},
			error :function(e, status) {
				w2alert("Diego Release List 를 가져오는데 실패하였습니다.", "DIEGO 설치");
			}
		});
	}
	
	//CF 릴리즈 조회
	function getCfRelease() {
		$.ajax({
			type :"GET",
			url :"/release/getReleaseList/cf",
			contentType :"application/json",
			async :true,
			success :function(data, status) {
				console.log("Cf Releases List");
				cfReleases = new Array();
				data.records.map(function(obj) {
					cfReleases.push(obj.name + "/" + obj.version);
				});
				
				getGardenLinuxRelease();
			},
			error :function(e, status) {
				w2alert("Diego Release List 를 가져오는데 실패하였습니다.", "DIEGO 설치");
			}
		});
	}
	
	//gardenLinux 릴리즈 조회
	function getGardenLinuxRelease() {
		$.ajax({
			type :"GET",
			url :"/release/getReleaseList/garden-linux",
			contentType :"application/json",
			async :true,
			success :function(data, status) {
				console.log("VirtualLinux Releases List");
				gardenLinuxReleases = new Array();
				data.records.map(function(obj) {
					gardenLinuxReleases.push(obj.name + "/" + obj.version);
				});
				
				getEtcdRelease();
			},
			error :function(e, status) {
				w2alert("Garden Linux Release List 를 가져오는데 실패하였습니다.", "DIEGO 설치");
			}
		});
	}
	
	//ETCD 릴리즈 조회
	function getEtcdRelease() {
		$.ajax({
			type :"GET",
			url :"/release/getReleaseList/etcd",
			contentType :"application/json",
			async :true,
			success :function(data, status) {
				console.log("ETCD Releases List");
				etcdReleases = new Array();
				if(data.records){
					data.records.map(function(obj) {
						etcdReleases.push(obj.name + "/" + obj.version);
					});
				}
				setReleaseList();
			},
			error :function(e, status) {
				w2popup.unlock();
				w2alert("ETCD Release List 를 가져오는데 실패하였습니다.", "DIEGO 설치");
			}
		});
	}
	
	//Release List W2Field 적용
	function setReleaseList(){
		
		$(".w2ui-msg-body input[name='diegoReleases']").w2field('list', {items :diegoReleases,maxDropHeight :200,width :250});
		$(".w2ui-msg-body input[name='cfReleases']").w2field('list', {items :cfReleases,maxDropHeight :200,width :250});
		$(".w2ui-msg-body input[name='gardenLinuxReleases']").w2field('list', {items :gardenLinuxReleases,maxDropHeight :200,width :250});
		$(".w2ui-msg-body input[name='etcdReleases']").w2field('list', {items :etcdReleases,maxDropHeight :200,width :250});
		setReleaseData();
	}
	
	//Release Data 세팅
	function setReleaseData(){
		if( !checkEmpty(defaultInfo.diegoReleaseName) && !checkEmpty(defaultInfo.diegoReleaseVersion) ){
			$(".w2ui-msg-body input[name='diegoReleases']").data('selected',{text :defaultInfo.diegoReleaseName + "/"+ defaultInfo.diegoReleaseVersion});
		}
		if( !checkEmpty(defaultInfo.cfReleaseName) &&  !checkEmpty(defaultInfo.cfReleaseVersion) ){
			$(".w2ui-msg-body input[name='cfReleases']").data('selected',{text :defaultInfo.cfReleaseName + "/"+ defaultInfo.cfReleaseVersion});
		}
		if( !checkEmpty(defaultInfo.gardenLinuxReleaseName) &&  !checkEmpty(defaultInfo.gardenLinuxReleaseVersion) ){
			$(".w2ui-msg-body input[name='gardenLinuxReleases']").data('selected',{text :defaultInfo.gardenLinuxReleaseName + "/"+ defaultInfo.gardenLinuxReleaseVersion});
		}
		if( !checkEmpty(defaultInfo.etcdReleaseName) &&  !checkEmpty(defaultInfo.etcdReleaseVersion) ){
			$(".w2ui-msg-body input[name='etcdReleases']").data('selected',{text :defaultInfo.etcdReleaseName + "/"+ defaultInfo.etcdReleaseVersion});
		}
		w2popup.unlock();
	}
	
	// 스템셀 조회
	function getStamcellList() {
		$.ajax({
			type :"GET",
			url :"/stemcells",
			contentType :"application/json",
			success :function(data, status) {
				console.log("Stemcell List");
				stemcells = new Array();
				if(data.records){
					data.records.map(function(obj) {
						stemcells.push(obj.name + "/" + obj.version);
					});
				}
				setStemcellList();
			},
			error :function(e, status) {
				w2popup.unlock();
				w2alert("Stemcell List 를 가져오는데 실패하였습니다.", "DIEGO 설치");
			}
		});
	}
	//스템셀 List W2Field 적용
	function setStemcellList(){
		$(".w2ui-msg-body input[name='stemcells']").w2field('list', {items : stemcells,maxDropHeight : 200,width : 250});
		setStemcellData();
	}
	
	// 스템셀 release value setgting
	function setStemcellData(){
		if( !checkEmpty(resourceInfo.stemcellName) && !checkEmpty(resourceInfo.stemcellVersion) ){
			$(".w2ui-msg-body input[name='stemcells']").data('selected',{text : resourceInfo.stemcellName + "/"+ resourceInfo.stemcellVersion});
		}
		w2popup.unlock();
	}
	
	//전역변수 초기화
	function initSetting() {
		//private var
		iaas = "";
		diegoId = "";
		
		defaultInfo = "";	
		cfInfo = "";
		diegoInfo = "";
		etcdInfo = "";
		networkInfo = "";
		resourceInfo = "";
		
		diegoReleases = "";
		cfReleases = "";
		gardenLinuxReleases = "";
		etcdReleases = "";
		
		stemcells = "";
		deploymentFile = "";
		//grid Reload
		gridReload();
	}

	//Install/Delete 팝업 종료시 이벤트
	function popupComplete(){
		w2confirm({
			title 	: $(".w2ui-msg-title b").text(),
			msg		: $(".w2ui-msg-title b").text() + " 화면을 닫으시겠습니까?<BR>(닫은 후에도 완료되지 않는 설치 또는 삭제 작업은 계속 진행됩니다.)",
			yes_text:"확인",
			yes_callBack :function(envent){
				w2popup.close();
				//params init
				initSetting();
			},
			no_text :"취소"
		});
	}

	//버튼 스타일 변경
	function doButtonStyle() {
		//Button Style init
		$('#modifyBtn').attr('disabled', true);
		$('#deleteBtn').attr('disabled', true);
	}

	// 그리드 재조회
	function gridReload() {
		//console.log("delete complete!");
		w2ui['config_diegoGrid'].clear();
		doSearch();
	}

	//다른페이지 이동시 호출
	function clearMainPage() {
		$().w2destroy('config_diegoGrid');
	}

	//화면 리사이즈시 호출
	$(window).resize(function() {
		setLayoutContainerHeight();
	});

	//w2Overay
	function overlay(text){
		var over = text.replace( "/\r\n/g", "\n" );
		
		$(this).w2overlay(over);
	}
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
				<span id="installBtn" class="btn btn-primary" style="width:120px">설&nbsp;&nbsp;치</span>
				&nbsp; <span id="modifyBtn" class="btn btn-info" style="width:120px">수&nbsp;&nbsp;정</span>
				&nbsp; <span id="deleteBtn" class="btn btn-danger" style="width:120px">삭&nbsp;&nbsp;제</span>
				<!-- //Btn -->
			</div>
		</div>
		<div id="config_diegoGrid" style="width:100%; height:500px"></div>
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
			</div>
		</div>
	</div>

	<!-- Start AWS Popup  -->

	<!-- 기본 정보 설정 DIV -->
	<div id="defaultInfoDiv" style="width:100%; height:100%;" hidden="true">
		<div rel="title"><b>DIEGO 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:2%;display:inline-block;width:97%;">
				<ul class="progressStep_8">
					<li class="active">기본 정보</li>
					<li class="before">CF 정보</li>
					<li class="before">DIEGO 정보</li>
					<li class="before">ETCD 정보</li>
					<li class="before">네트워크 정보</li>
					<li class="before">리소스 정보</li>
					<li class="before">배포파일 정보</li>
					<li class="before">설치</li>
				</ul>
			</div>
			<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
				<div class="panel panel-info">	
					<div class="panel-heading"><b>기본 정보</b></div>
					<div class="panel-body" style="padding:0 5%;">
						<div class="w2ui-field" >
							<label style="text-align:left; width:40%; font-size:11px;">설치관리자 UUID</label>
							<div>
								<input name="directorUuid" type="text" style="float:left; width:60%;" required placeholder="설치관리자 UUID를 입력하세요." />
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field" >
							<label style="text-align:left; width:40%; font-size:11px;">배포 명</label>
							<div>
								<input name="deploymentName" type="text" style="float:left; width:60%;" required placeholder="배포 명을 입력하세요." />
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field" >
							<label style="text-align:left; width:40%; font-size:11px;">DIEGO 릴리즈</label>
							<div>
								<input name="diegoReleases" type="list" style="float:left; width:60%;" required placeholder="DIEGO 릴리즈를 선택하세요." />
							</div>
						</div>
						
						<div class="w2ui-field" >
							<label style="text-align:left; width:40%; font-size:11px;">CF 릴리즈</label>
							<div>
								<input name="cfReleases" type="list" style="float:left; width:60%;" required placeholder="CF 릴리즈를 선택하세요." />
							</div>
						</div>
						<div class="w2ui-field" >
							<label style="text-align:left; width:40%; font-size:11px;">Garden-Linux 릴리즈</label>
							<div>
								<input name="gardenLinuxReleases" type="list" style="float:left; width:60%;" required placeholder="Garden-Linux 릴리즈를 선택하세요." />
							</div>
						</div>
						<div class="w2ui-field" >
							<label style="text-align:left; width:40%; font-size:11px;">ETCD 릴리즈</label>
							<div>
								<input name="etcdReleases" type="list" style="float:left; width:60%;" required placeholder="ETCD 릴리즈를 선택하세요." />
							</div>
						</div>
					</div>
				</div>
			</div>
			<br />
			<div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float:right; padding-right:15%" onclick="saveDefaultInfo();">다음>></button>
			</div>
		</div>
	</div>
	
	<!-- CF 정보 설정 DIV -->
	<div id="cfInfoDiv" style="width:100%; height:100%;" hidden="true">
		<div rel="title"><b>DIEGO 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:2%;display:inline-block;width:97%;">
				<ul class="progressStep_8">
					<li class="pass">기본 정보</li>
					<li class="active">CF 정보</li>
					<li class="before">DIEGO 정보</li>
					<li class="before">ETCD 정보</li>
					<li class="before">네트워크 정보</li>
					<li class="before">리소스 정보</li>
					<li class="before">배포파일 정보</li>
					<li class="before">설치</li>
				</ul>
			</div>
			<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
				<div class="panel panel-info">	
					<div class="panel-heading"><b>CF 정보</b></div>
					<div class="panel-body" style="padding:0 5%;">
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">도메인</label>
							<div>
								<input name="domain" type="text" style="float:left; width:60%;" required placeholder="도메인을 입력하세요. 예)diegodoamin.com" />
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">CF 배포명</label>
							<div>
								<input name="deployment" type="text" style="float:left; width:60%;" required placeholder="CF 배포명을 입력하세요." />
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">CF 비밀번호</label>
							<div>
								<input name="secret" type="text" style="float:left; width:60%;" required placeholder="CF 비밀번호을 입력하세요." />
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">ETCD 서버 IP</label>
							<div>
								<input name="etcdMachines" type="text" style="float:left; width:60%;" required placeholder="ETCD 서버 IP를 입력하세요." />
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">NATS 서버 IP</label>
							<div>
								<input name="natsMachines" type="text" style="float:left; width:60%;" required placeholder="NATS 서버 IP를 입력하세요." />
								<div class="isMessage"></div>
							</div>
						</div>
					</div>
				</div>
				<div class="panel panel-info">	
					<div class="panel-heading"><b>CONSUL 정보</b></div>
					<div class="panel-body" style="padding:0 5%;">
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">서버 IP</label>
							<div>
								<input name="consulServersLan" type="text" style="float:left; width:60%;" required placeholder="CONSUL 서버 IP를 입력하세요." />
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">암호화키</label>
							<div>
								<input name="consulEncryptKeys" type="text" style="float:left; width:60%;" required placeholder="CONSUL 암호화키를 입력하세요." />
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">에이전트 인증서</label>
							<div>
								<textarea name="consulAgentCert" style="float:left; width:60%; height:50px;margin-bottom:10px; overflow-y:visible; resize:none; background-color:#FFF;"
									required placeholder="CONSUL 에이전트 인증서를 입력하세요." onblur="overlay($(this).val());"></textarea>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">에이전트 개인키</label>
							<div>
								<textarea name="consulAgentKey" style="float:left; width:60%; height:50px;margin-bottom:10px; overflow-y:visible; resize:none; background-color:#FFF;"
									required placeholder="CONSUL 에이전트 개인키(Agent Private Key)를 입력하세요." onblur="overlay($(this).val());"></textarea>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">서버 CA 인증서</label>
							<div>
								<textarea name="consulCaCert" style="float:left; width:60%; height:50px;margin-bottom:10px; overflow-y:visible; resize:none; background-color:#FFF;"
									required placeholder="서버 CA 인증서(Server CA Certificate)를 입력하세요." onblur="overlay($(this).val());"></textarea>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">서버 인증서</label>
							<div>
								<textarea name="consulServerCert" style="float:left; width:60%; height:50px;margin-bottom:10px; overflow-y:visible; resize:none; background-color:#FFF;"
									required placeholder="CONSUL 서버 인증서(Server Certificate)를 입력하세요." onblur="overlay($(this).val());"></textarea>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">서버 개인키</label>
							<div>
								<textarea name="consulServerKey" style="float:left; width:60%; height:50px;margin-bottom:10px; overflow-y:visible; resize:none; background-color:#FFF;"
									required placeholder="CONSUL 서버 개인키(Server Private Key)를 입력하세요." onblur="overlay($(this).val());"></textarea>
							</div>
						</div>
					</div>
				</div>
			</div>
			<br />
			<div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float:left;" onclick="saveCfInfo('before');">이전</button>
				<button class="btn" style="float:right; padding-right:15%" onclick="saveCfInfo('after');">다음>></button>
			</div>
		</div>
	</div>
	
	<!-- Diego 정보 설정 DIV -->
	<div id="diegoInfoDiv" style="width:100%; height:100%;" hidden="true">
		<div rel="title"><b>DIEGO 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:2%;display:inline-block;width:97%;">
				<ul class="progressStep_8">
					<li class="pass">기본 정보</li>
					<li class="pass">CF 정보</li>
					<li class="active">DIEGO 정보</li>
					<li class="before">ETCD 정보</li>
					<li class="before">네트워크 정보</li>
					<li class="before">리소스 정보</li>
					<li class="before">배포파일 정보</li>
					<li class="before">설치</li>
				</ul>
			</div>
			<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
				<div class="panel panel-info">	
					<div class="panel-heading"><b>DIEGO 인증정보</b></div>
					<div class="panel-body" style="padding:0 5%;">
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">CA 인증서</label>
							<div>
								<textarea name="diegoCaCert" style="float:left; width:60%; height:50px;margin-bottom:10px; overflow-y:visible; resize:none; background-color:#FFF;"
									required placeholder="CA 인증서를 입력하세요." onblur="overlay($(this).val());"></textarea>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">SSH Proxy 개인키</label>
							<div>
								<textarea name="diegoHostKey" style="float:left; width:60%; height:50px;margin-bottom:10px; overflow-y:visible; resize:none; background-color:#FFF;"
									required placeholder="개인키를 입력하세요." onblur="overlay($(this).val());"></textarea>
							</div>
						</div>
					</div>
				</div>
				<div class="panel panel-info">	
					<div class="panel-heading"><b>BBS 인증정보</b></div>
					<div class="panel-body" style="padding:0 5%;">
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">암호화키</label>
							<div>
								<input name="diegoEncryptionKeys" type="text" style="float:left; width:60%;" required placeholder="암호화키를 입력하세요." />
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">클라이언트 인증서</label>
							<div>
								<textarea name="diegoClientCert" style="float:left; width:60%; height:50px;margin-bottom:10px; overflow-y:visible; resize:none; background-color:#FFF;"
									required placeholder="클라이언트 인증서를 입력하세요." onblur="overlay($(this).val());"></textarea>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">클라이언트 개인키</label>
							<div>
								<textarea name="diegoClientKey" style="float:left; width:60%; height:50px;margin-bottom:10px; overflow-y:visible; resize:none; background-color:#FFF;"
									required placeholder="클라이언트 개인키를 입력하세요." onblur="overlay($(this).val());"></textarea>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">서버 인증서</label>
							<div>
								<textarea name="diegoServerCert" style="float:left; width:60%; height:50px;margin-bottom:10px; overflow-y:visible; resize:none; background-color:#FFF;"
									required placeholder="서버 인증서를 입력하세요." onblur="overlay($(this).val());"></textarea>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">서버 공개키</label>
							<div>
								<textarea name="diegoServerKey" style="float:left; width:60%; height:50px;margin-bottom:10px; overflow-y:visible; resize:none; background-color:#FFF;"
									required placeholder="서버 공개키를 입력하세요." onblur="overlay($(this).val());"></textarea>
							</div>
						</div>
					</div>
				</div>
			</div>
			<br />
			<div class="w2ui-buttons" rel="buttons" hidden="true"> 
				<button class="btn" style="float:left;" onclick="saveDiegoInfo('before');">이전</button>
				<button class="btn" style="float:right; padding-right:15%" onclick="saveDiegoInfo('after');">다음>></button>
			</div>
		</div>
	</div>
	
	<!-- ETCD 설정 DIV -->
	<div id="etcdInfoDiv" style="width:100%; height:100%;" hidden="true">
		<div rel="title"><b>DIEGO 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:2%;display:inline-block;width:97%;">
				<ul class="progressStep_8">
					<li class="pass">기본 정보</li>
					<li class="pass">CF 정보</li>
					<li class="pass">DIEGO 정보</li>
					<li class="active">ETCD 정보</li>
					<li class="before">네트워크 정보</li>
					<li class="before">리소스 정보</li>
					<li class="before">배포파일 정보</li>
					<li class="before">설치</li>
				</ul>
			</div>
			<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
				<div class="panel panel-info">	
					<div class="panel-heading"><b>ETCD 인증정보</b></div>
					<div class="panel-body" style="padding:0 5%;">
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">클라이언트 인증서</label>
							<div>
								<textarea name="etcdClientCert" style="float:left; width:60%; height:50px;margin-bottom:10px; overflow-y:visible; resize:none; background-color:#FFF;"
									required placeholder="클라이언트 인증서를 입력하세요." onblur="overlay($(this).val());"></textarea>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">클라이언트 개인키</label>
							<div>
								<textarea name="etcdClientKey" style="float:left; width:60%; height:50px;margin-bottom:10px; overflow-y:visible; resize:none; background-color:#FFF;"
									required placeholder="클라이언트 개인키를 입력하세요." onblur="overlay($(this).val());"></textarea>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">서버 인증서</label>
							<div>
								<textarea name="etcdServerCert" style="float:left; width:60%; height:50px;margin-bottom:10px; overflow-y:visible; resize:none; background-color:#FFF;"
									required placeholder="서버 인증서를 입력하세요." onblur="overlay($(this).val());"></textarea>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">서버 공개키</label>
							<div>
								<textarea name="etcdServerKey" style="float:left; width:60%; height:50px;margin-bottom:10px; overflow-y:visible; resize:none; background-color:#FFF;"
									required placeholder="서버 공개키를 입력하세요." onblur="overlay($(this).val());"></textarea>
							</div>
						</div>
					</div>
				</div>
				<div class="panel panel-info">	
					<div class="panel-heading"><b>PEER 인증정보</b></div>
					<div class="panel-body" style="padding:0 5%;">
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">CA 인증서</label>
							<div>
								<textarea name="etcdPeerCaCert" style="float:left; width:60%; height:50px;margin-bottom:10px; overflow-y:visible; resize:none; background-color:#FFF;"
									required placeholder="CA 인증서를 입력하세요." onblur="overlay($(this).val());"></textarea>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">인증서</label>
							<div>
								<textarea name="etcdPeerCert" style="float:left; width:60%; height:50px;margin-bottom:10px; overflow-y:visible; resize:none; background-color:#FFF;"
									required placeholder="인증서를 입력하세요." onblur="overlay($(this).val());"></textarea>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">개인키</label>
							<div>
								<textarea name="etcdPeerKey" style="float:left; width:60%; height:50px;margin-bottom:10px; overflow-y:visible; resize:none; background-color:#FFF;"
									required placeholder="개인키를 입력하세요." onblur="overlay($(this).val());"></textarea>
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<br />
			<div class="w2ui-buttons" rel="buttons" hidden="true"> 
				<button class="btn" style="float:left;" onclick="saveEtcdInfo('before');">이전</button>
				<button class="btn" style="float:right; padding-right:15%" onclick="saveEtcdInfo('after');">다음>></button>
			</div>
		</div>
	</div>
	
	<!-- AWS Network 설정 DIV -->
	<div id="awsNetworkInfoDiv" style="width:100%; height:100%;" hidden="true">
		<div rel="title"><b>DIEGO 설치</b></div>AWS
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:2%;display:inline-block;width:97%;">
				<ul class="progressStep_8">
					<li class="pass">기본 정보</li>
					<li class="pass">CF 정보</li>
					<li class="pass">DIEGO 정보</li>
					<li class="pass">ETCD 정보</li>
					<li class="active">네트워크 정보</li>
					<li class="before">리소스 정보</li>
					<li class="before">배포파일 정보</li>
					<li class="before">설치</li>
				</ul>
			</div>
			<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
				<div class="panel panel-info">	
					<div class="panel-heading"><b>네트워크 정보</b></div>
					<div class="panel-body" style="padding:0 5%;">
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">시큐리티 그룹명</label>
							<div>
								<input name="cloudSecurityGroups" type="text" style="float:left; width:60%;" required placeholder="예) diego-security" />
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">서브넷 ID</label>
							<div>
								<input name="subnetId" type="text" style="float:left; width:60%;" required placeholder="예) subnet-XXXXXX" />
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">서브넷 범위</label>
							<div>
								<input name="subnetRange" type="text" style="float:left; width:60%;" required placeholder="예) 10.0.0.0/24" />
								<div class="isMessage"></div>
							</div>
						</div>
						
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">게이트웨이</label>
							<div>
								<input name="subnetGateway" type="url" style="float:left; width:60%;" required placeholder="예) 10.0.0.1" />
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">DNS</label>
							<div>
								<input name="subnetDns" type="text" style="float:left; width:60%;" required placeholder="예) 8.8.8.8" />
								<div class="isMessage"></div>
							</div>
						</div>			
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">IP할당 제외 대역</label>
							<div>
								<div style="display:inline-block; width:60%;">
									<span style="float:left; width:45%;">
										<input name="subnetReservedFrom" id="subnetStaticFrom" type="url" style="float:left;width:100%;" placeholder="예) 10.0.0.100" />
									</span> 
									<span style="float:left; width:10%; text-align:center;">&nbsp;&ndash; &nbsp;</span>
									<span style="float:left; width:45%;">
										<input name="subnetReservedTo" id="subnetStaticTo" type="url" style="float:left;width:100%;" placeholder="예) 10.0.0.106" />
									</span>
								</div>
								<div class="isMessage"></div>
							</div>
						</div>				
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">IP할당 대역(최소 6개 이상)</label>
							<div>
								<div style="display:inline-block; width:60%;">
									<span style="float:left; width:45%;">
										<input name="subnetStaticFrom" type="url" style="float:left;width:100%;" placeholder="예) 10.0.0.100" />
									</span> 
									<span style="float:left; width:10%; text-align:center;">&nbsp;&ndash; &nbsp;</span>
									<span style="float:left; width:45%;">
										<input name="subnetStaticTo" type="url" style="float:left;width:100%;" placeholder="예) 10.0.0.106" />
									</span>
								</div>
								<div class="isMessage"></div>
							</div>
						</div>
					</div>
				</div>				
			</div>
			<br />
			<div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float:left;" onclick="saveAwsNetworkInfo('before');">이전</button>
				<button class="btn" style="float:right; padding-right:15%" onclick="saveAwsNetworkInfo('after');">다음>></button>
			</div>
		</div>
	</div>
	
	<!-- Aws Resource  설정 DIV -->
	<div id="resourceInfoDiv" style="width:100%; height:100%;" hidden="true">
		<div rel="title"><b>DIEGO 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:2%;display:inline-block;width:97%;">
				<ul class="progressStep_8">
					<li class="pass">기본 정보</li>
					<li class="pass">CF 정보</li>
					<li class="pass">DIEGO 정보</li>
					<li class="pass">ETCD 정보</li>
					<li class="pass">네트워크 정보</li>
					<li class="active">리소스 정보</li>
					<li class="before">배포파일 정보</li>
					<li class="before">설치</li>
				</ul>
			</div>
			<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
				<div class="panel panel-info">	
					<div class="panel-heading"><b>리소스 정보</b></div>
					<div class="panel-body" style="padding:0 5%;">
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">스템셀</label>
							<div>
								<div>
									<input type="list" name="stemcells" style="float:left; width:60%; margin-top:1.5px;" placeholder="스템셀을 선택하세요.">
								</div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">VM 비밀번호</label>
							<div>
								<input name="boshPassword" type="text" style="float:left; width:60%;" required placeholder="VM 비밀번호를 입력하세요." />
								<div class="isMessage"></div>
							</div>
						</div>
						<br/><br/>
					</div>
				</div>
			</div>
			<div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float:left;" onclick="saveResourceInfo('before');">이전</button>
				<button class="btn" style="float:right; padding-right:15%" onclick="saveResourceInfo('after');">다음>></button>
			</div>
		</div>
	</div>
	
	<!-- 배포파일 정보 -->
	<div id="deployDiv" style="width:100%; height:100%;" hidden="true">
		<div rel="title"><b>DIEGO 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:2%;display:inline-block;width:97%;">
				<ul class="progressStep_8">
					<li class="pass">기본 정보</li>
					<li class="pass">CF 정보</li>
					<li class="pass">DIEGO 정보</li>
					<li class="pass">ETCD 정보</li>
					<li class="pass">네트워크 정보</li>
					<li class="pass">리소스 정보</li>
					<li class="active">배포파일 정보</li>
					<li class="before">설치</li>
				</ul>
			</div>
			<div style="width:95%;height:84%;float:left;display:inline-block;margin-top: 10px;">
				<textarea id="deployInfo" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color:#FFF;margin-left:2%" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" style="float:left;" onclick="resourcePopup();">이전</button>
			<button class="btn" style="float:right; padding-right:15%" onclick="diegoDeploy('after');">다음>></button>
		</div>
	</div>
	
	<!-- 설치화면 -->
	<div id="installDiv" style="width:100%; height:100%;" hidden="true">
		<div rel="title"><b>DIEGO 설치</b></div>
		<div rel="body" style="width:100%;height:100%;padding:15px 5px 0 5px;margin:0 auto;">
			<div style="margin-left:2%;display:inline-block;width:97%;">
				<ul class="progressStep_8">
					<li class="pass">기본 정보</li>
					<li class="pass">CF 정보</li>
					<li class="pass">DIEGO 정보</li>
					<li class="pass">ETCD 정보</li>
					<li class="pass">네트워크 정보</li>
					<li class="pass">리소스 정보</li>
					<li class="pass">배포파일 정보</li>
					<li class="active">설치</li>
				</ul>
			</div>
			<div style="width:95%;height:84%;float:left;display:inline-block;margin-top: 10px;">
				<textarea id="installLogs" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color:#FFF;margin-left:1%" readonly="readonly"></textarea>
			</div>
		</div>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" style="float:left;" onclick="deployPopup()">이전</button>
			<button class="btn" style="float:right; padding-right:15%" onclick="popupComplete();">닫기</button>
		</div>
	</div>
	<!-- End AWS Popup -->

	<!-- Start Diego OPENSTACK POP -->	
	<!-- 오픈스텍 네트워크 정보 -->
	<div id="openstackNetworkInfoDiv" style="width:100%; height:100%;" hidden="true">
		<div rel="title"><b>DIEGO 설치</b></div>
		<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
			<div style="margin-left:2%;display:inline-block;width:97%;">
				<ul class="progressStep_8">
					<li class="pass">기본 정보</li>
					<li class="pass">CF 정보</li>
					<li class="pass">DIEGO 정보</li>
					<li class="pass">ETCD 정보</li>
					<li class="active">네트워크 정보</li>
					<li class="before">리소스 정보</li>
					<li class="before">배포파일 정보</li>
					<li class="before">설치</li>
				</ul>
			</div>
			<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
				<div class="panel panel-info">	
					<div class="panel-heading"><b>네트워크 정보</b></div>
					<div class="panel-body" style="padding:0 5%;">
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">시큐리티 그룹명</label>
							<div>
								<input name="cloudSecurityGroups" type="text" style="float:left; width:60%;" required placeholder="예) diego-security" />
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">네트워크 ID</label>
							<div>
								<input name="cloudNetId" type="text" style="float:left; width:60%;" required placeholder="예) subnet-XXXXXX" />
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">서브넷 범위</label>
							<div>
								<input name="subnetRange" type="text" style="float:left; width:60%;" required placeholder="예) 10.0.0.0/24" />
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">게이트웨이</label>
							<div>
								<input name="subnetGateway" type="url" style="float:left; width:60%;" required placeholder="예) 10.0.0.1" />
								<div class="isMessage"></div>
							</div>
						</div>
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">DNS</label>
							<div>
								<input name="subnetDns" type="text" style="float:left; width:60%;" required placeholder="예) 8.8.8.8" />
								<div class="isMessage"></div>
							</div>
						</div>			
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">IP할당 제외 대역</label>
							<div>
								<div style="display:inline-block; width:60%;">
									<span style="float:left; width:45%;">
										<input name="subnetReservedFrom" id="subnetStaticFrom" type="url" style="float:left;width:100%;" placeholder="예) 10.0.0.100" />
									</span> 
									<span style="float:left; width:10%; text-align:center;">&nbsp;&ndash; &nbsp;</span>
									<span style="float:left; width:45%;">
										<input name="subnetReservedTo" id="subnetStaticTo" type="url" style="float:left;width:100%;" placeholder="예) 10.0.0.106" />
									</span>
								</div>
								<div class="isMessage"></div>
							</div>
						</div>
						
						<div class="w2ui-field">
							<label style="text-align:left; width:40%; font-size:11px;">IP할당 대역(최소 6개)</label>
							<div>
								<div style="display:inline-block; width:60%;">
									<span style="float:left; width:45%;">
										<input name="subnetStaticFrom" type="url" style="float:left;width:100%;" placeholder="예) 10.0.0.100" />
									</span> 
									<span style="float:left; width:10%; text-align:center;">&nbsp;&ndash; &nbsp;</span>
									<span style="float:left; width:45%;">
										<input name="subnetStaticTo" type="url" style="float:left;width:100%;" placeholder="예) 10.0.0.106" />
									</span>
								</div>
								<div class="isMessage"></div>
							</div>
						</div>
					</div>
				</div>				
			</div>
			<br />
			<div class="w2ui-buttons" rel="buttons" hidden="true">
				<button class="btn" style="float:left;" onclick="saveOpenstackNetworkInfo('before');">이전</button>
				<button class="btn" style="float:right; padding-right:15%" onclick="saveOpenstackNetworkInfo('after');">다음>></button>
			</div>
		</div>
	</div>
