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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript">
var diegoId = "";
var networkId = "";
var defaultInfo = "";	
var cfInfo = "";
var diegoInfo = "";
var etcdInfo = "";
var peerInfo = "";
var networkInfo = new Array();
var publicStaticIp = "";
var internalCnt=1;
var resourceInfo = "";
var diegoReleases = new Array();
var cfReleases = new Array();
var gardenReleaseName = new Array();
var etcdReleases = new Array();
var cflinuxfs2rootfsrelease = new Array();
var cfInfo = new Array();
var stemcells = new Array();
var deploymentFile = "";
var installStatus = "";
var installClient ="";
var modifyNetWork = "";
var cfInfoYn = false;
var diegoKeyFile = "";

$(function() {
	//릴리즈 정보 popup over
 	$('[data-toggle="popover"]').popover();
 	//Diego Release Info
 	getReleaseVersionList();
 	
 	$(".gardenRelease-info").attr('data-content', "https://github.com/cloudfoundry/diego-cf-compatibility");
 	$(".cflinux-info").attr('data-content', "https://github.com/cloudfoundry/diego-cf-compatibility");
 	$(".etcd-info").attr('data-content', "https://github.com/cloudfoundry/diego-cf-compatibility");
 	
 	//Diego 릴리즈 popover가 나타난 후
 	$('.diego-info').on('show.bs.popover', function () {
 		$(".gardenRelease-info").popover('hide')
 		$(".cflinux-info").popover('hide')
 	 	$(".etcd-info").popover('hide')
 	 })
 	 
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
 * 설명 :  Diego 릴리즈 설치 지원 버전 목록 조회
 * Function : getReleaseVersionList
 *********************************************************/
function getReleaseVersionList(){
	 var contents = "";
	$.ajax({
		type :"GET",
		url :"/common/deploy/list/releaseInfo/diego/"+iaas, 
		contentType :"application/json",
		success :function(data, status) {
			if (data != null && data != "") {
				contents = "<table id='popoverTable'><tr><th>IaaS 유형</th><th>릴리즈 버전</th></tr>";
				data.map(function(obj) {
					contents += "<tr><td>" + obj.iaasType+ "</td><td>" +  obj.minReleaseVersion +"</td></tr>";
				});
				contents += "</table>";
				$('.diego-info').attr('data-content', contents);
			}
		},
		error :function(request, status, error) {
			var errorResult = JSON.parse(request.responseText);
			w2alert(errorResult.message, "DIEGO 릴리즈 정보 목록 조회");
		}
	});
}

/********************************************************
 * 설명 :  Diego 수정 - 데이터 조회
 * Function : getDiegoData
 *********************************************************/
function getDiegoData(record) {
	var url = "/deploy/"+menu+"/install/detail/" + record.id;
	$.ajax({
		type :"GET",
		url :url,
		contentType :"application/json",
		success :function(data, status) {
			if (data != null && data != "") {
				setDiegoData(data.content);
				defaultPopup();
			}
		},
		error :function(request, status, error) {
			var errorResult = JSON.parse(request.responseText);
			w2alert(errorResult.message, "DIEGO 수정");
		}
	});
}

/********************************************************
 * 설명 :  Diego Data Setting
 * Function : setCfData
 *********************************************************/
function setDiegoData(contents) {
	 if( menu == "cfDiego" ) {
		 contents = contents.diegoVo;
	 }
	diegoId = contents.id;
	iaas = contents.iaasType;
	if( contents.networks.length > 1){
		internalCnt = contents.networks.length-1;
		if( menu == "cfDiego" ) internalCnt = contents.networks.length;
	}
	 //기본 정보 설정
	 diegoKeyFile = contents.keyFile
	 defaultInfo = {
			 iaas										: contents.iaas,
			 cfId										: cfId,
			deploymentName 					: contents.deploymentName,
			directorUuid 							: contents.directorUuid,
			diegoReleaseName 				: contents.diegoReleaseName,
			diegoReleaseVersion 				: contents.diegoReleaseVersion,
			gardenReleaseName 				: contents.gardenReleaseName,
			gardenReleaseVersion 			: contents.gardenReleaseVersion,
			etcdReleaseName 					: contents.etcdReleaseName,
			etcdReleaseVersion 				: contents.etcdReleaseVersion,
			cfDeploymentName 					: contents.cfName,
			cflinuxfs2rootfsreleaseName 	: contents.cflinuxfs2rootfsreleaseName,
			cflinuxfs2rootfsreleaseVersion	: contents.cflinuxfs2rootfsreleaseVersion,
		}
	
		//네트워크 정보 설정
		for(var i=0; i<contents.networks.length; i++){
		 	var arr = {
		 		id								: contents.id,
				deployType				: contents.networks[i].deployType,
				seq							: i,
				net							: contents.networks[i].net,
				publicStaticIp				: contents.networks[i].publicStaticIp,
				subnetRange 				: contents.networks[i].subnetRange,
				subnetGateway 			: contents.networks[i].subnetGateway,
				subnetDns 				: contents.networks[i].subnetDns,
				subnetReservedFrom 	: contents.networks[i].subnetReservedFrom,
				subnetReservedTo 		: contents.networks[i].subnetReservedTo,
				subnetStaticFrom 		: contents.networks[i].subnetStaticFrom,
				subnetStaticTo 			: contents.networks[i].subnetStaticTo,
				subnetId 					: contents.networks[i].subnetId,
				cloudSecurityGroups 	: contents.networks[i].cloudSecurityGroups
			}
		 	networkInfo.push(arr);
		 	
		}
		internalCnt = networkInfo.length;
		//리소스 정보 설정
		if(contents.resource != null && contents.resource != ""){
			resourceInfo = {
					id 									: contents.id,
					stemcellName 				: contents.resource.stemcellName,
					stemcellVersion 			: contents.resource.stemcellVersion,
					boshPassword 				: contents.resource.boshPassword,
					smallFlavor					: contents.resource.smallFlavor,
					mediumFlavor				: contents.resource.mediumFlavor,
					largeFlavor					: contents.resource.largeFlavor,
					runnerFlavor					: contents.resource.runnerFlavor,
					smallRam						: contents.resource.smallRam,
					smallDisk						: contents.resource.smallDisk,
					smallCpu						: contents.resource.smallCpu,
					mediumRam					: contents.resource.mediumRam,
					mediumDisk					: contents.resource.mediumDisk,
					mediumCpu					: contents.resource.mediumCpu,
					largeRam						: contents.resource.largeRam						,
					largeDisk						: contents.resource.largeDisk,
					largeCpu						: contents.resource.largeCpu,
					runnerRam					: contents.resource.runnerRam,
					runnerDisk					: contents.resource.runnerDisk,
					runnerCpu						: contents.resource.runnerCpu
				}
		}
}

/********************************************************
 * 설명		:  기본정보 팝업
 * Function	: defaultPopup
 *********************************************************/
function defaultPopup() {
	$("#defaultInfoDiv").w2popup({
		width : 750,
		height :500,
		modal :true,
		showMax :false,
		onOpen :function(event) {
			event.onComplete = function() {
				if( menu == "cfDiego") {
					$('.w2ui-msg-buttons #defaultPopupBtn').show();
					$('.w2ui-msg-body #keyBtn').css("display","none");
				}
				if (defaultInfo != "") {
					$(".w2ui-msg-body input[name='deploymentName']").val(defaultInfo.deploymentName);
					$(".w2ui-msg-body input[name='directorUuid']").val(defaultInfo.directorUuid);
					$(".w2ui-msg-body input[name='cfId']").val(cfId);
				}else{
					if( !checkEmpty($("#directorUuid").text()) ){
						$(".w2ui-msg-body input[name='directorUuid']").val($("#directorUuid").text());
					}
				}
				getReleases();//릴리즈 조회	
			}
		},
		onClose :function(event) {
			event.onComplete = function() {
				initSetting();
			}
		}
	});
}

/********************************************************
 * 설명		: Diego 설치 릴리즈 목록 조회 
 * Function	: getReleases
 *********************************************************/
function getReleases(){
	cfInfo = new Array(); //CF  릴리즈
	etcdReleases = new Array(); //ETCD 릴리즈
	gardenReleaseName = new Array(); //Garden-Linux 릴리즈
	diegoReleases = new Array(); //DIEGO 릴리즈
	stemcells = new Array(); //STEMCELL
	//화면 LOCK
	w2popup.lock("릴리즈를 조회 중입니다.", true);
	getCfRelease(); //Diego 릴리즈 조회
}

/********************************************************
 * 설명		: Diego 릴리즈 조회
 * Function	: getDiegoRelease
 *********************************************************/
function getDiegoRelease() {
	$.ajax({
		type :"GET",
		url :"/common/deploy/release/list/diego",
		contentType :"application/json",
		async :true,
		success :function(data, status) {
			diegoReleases = new Array();
			if(data.records != null){
				data.records.map(function(obj) {
					diegoReleases.push(obj.name + "/" + obj.version);
					
				});
			}
			getcflinuxfs2RootfsRelease();
		},
		error :function(e, status) {
			w2alert("Diego Release List 를 가져오는데 실패하였습니다.", "DIEGO 설치");
		}
	});
}

/********************************************************
 * 설명		: cflinuxfs2Rootfs 릴리즈 조회 
 * Function	: getcflinuxfs2RootfsRelease
 *********************************************************/
function getcflinuxfs2RootfsRelease(){
	$.ajax({
		type :"GET",
		url :"/common/deploy/release/list/cflinuxfs2-rootfs",
		contentType :"application/json",
		async :true,
		success :function(data, status) {
			cflinuxfs2rootfsrelease = new Array();
			if(data.records!=null){
				data.records.map(function(obj) {
					cflinuxfs2rootfsrelease.push(obj.name + "/" + obj.version);
				});
			}
			getgardenRelease();
		},
		error :function(e, status) {
			w2popup.unlock();
			w2alert("getcflinuxfs2RootfsRelease List 를 가져오는데 실패하였습니다.", "DIEGO 설치");
		}
	});
}


/********************************************************
 * 설명		: CF 정보 조회
 * Function	: arrayCFInfoJSON
 *********************************************************/
var arrayCFInfoJSON = [];
function getCfRelease() {
	$.ajax({
		type :"GET",
		url :"/deploy/"+menu+"/list/cf/"+iaas+"",
		contentType :"application/json",
		async :true,
		success :function(data, status) {
			if(data.records.length==0){
				var getCfInfo =  $(".w2ui-msg-body #getCfInfo");
				var input = '<input name="cfInfo" type="text" style="float:left; width:60%;" readonly onchange="setCfDeployFile(this.value);"  required placeholder="Diego와 연동하여 배포 할 CF정보가 없습니다." />';
				getCfInfo.html(input);
				cfInfoYn = true;
			}
			cfInfo = new Array();
			if(data.records != null){
				data.records.map(function(obj) {
					var getCfInfo =  $(".w2ui-msg-body #getCfInfo");
					if( menu == "cfDiego"  ){
						if(  cfId == obj.id) {
							var input = '<input name="cfInfo" type="text" style="float:left; width:60%;" readonly onchange="setCfDeployFile(this.value);"  required placeholder="Diego와 연동하여 배포 할 CF정보를 선택 하세요." />';
							getCfInfo.html(input);
							$(".w2ui-msg-body input[name='cfInfo']").val(obj.deploymentName);
							 $(".w2ui-msg-body input[name='cfId']").val(obj.id);
							$(".w2ui-msg-body input[name='deploymentName']").val(obj.deploymentName+"-diego");
						}
					} else{
						if(obj.diegoYn=="true") cfInfo.push(obj.deploymentName);	
						var listInput = '<input name="cfInfo" type="list" onchange="setCfDeployFile(this.value);" style="float:left; width:60%;" required placeholder="Diego와 연동하여 배포 할 CF정보를 선택 하세요." />';
						getCfInfo.html(listInput);
						$(".w2ui-msg-body input[name='cfInfo']").w2field('list', {items :cfInfo,maxDropHeight :200,width :250});
						arrayCFInfoJSON=data;
					}
				});
			}
			getDiegoRelease();
		},
		error :function(e, status) {
			w2alert("CF 정보를 가져오는데 실패하였습니다.", "DIEGO 설치");
		}
	});
}


/********************************************************
 * 설명		: CF 배포 파일 설정
 * Function	: setCfDeployFile
 *********************************************************/
function setCfDeployFile(value){
	var cf_id;
	var cfDeploymentFile;
 	for(var i=0;i<arrayCFInfoJSON.records.length;i++){
		if(value==arrayCFInfoJSON.records[i].deploymentName){
			cf_id = arrayCFInfoJSON.records[i].id;
			cfDeploymentFile = arrayCFInfoJSON.records[i].deploymentFile;
			break;
		}
	}
 	$(".w2ui-msg-body input[name='deploymentName']").val(value+"-diego");
 	$(".w2ui-msg-body input[name='cfId']").val(cf_id);
 	$(".w2ui-msg-body input[name='cfDeploymentFile']").val(cfDeploymentFile);
}

/********************************************************
 * 설명		: garden-Linux 릴리즈 조회
 * Function	: getgardenRelease
 *********************************************************/
function getgardenRelease() {
	$.ajax({
		type :"GET",
		url :"/common/deploy/release/list/garden-linux",
		contentType :"application/json",
		async :true,
		success :function(data, status) {
			gardenReleaseName = new Array();
			if(data.records != null){
				data.records.map(function(obj) {
					gardenReleaseName.push(obj.name + "/" + obj.version);
				});
			}
			getEtcdRelease();
		},
		error :function(e, status) {
			w2alert("Garden Linux Release List 를 가져오는데 실패하였습니다.", "DIEGO 설치");
		}
	});
}

/********************************************************
 * 설명		: ETCD 릴리즈 조회
 * Function	: getEtcdRelease
 *********************************************************/
function getEtcdRelease() {
	$.ajax({
		type :"GET",
		url :"/common/deploy/release/list/etcd",
		contentType :"application/json",
		async :true,
		success :function(data, status) {
			etcdReleases = new Array();
			if(data.records != null){
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

/********************************************************
 * 설명		: Release List W2Field 적용
 * Function	: setReleaseList
 *********************************************************/
function setReleaseList(){
	$(".w2ui-msg-body input[name='diegoReleases']").w2field('list', {items :diegoReleases,maxDropHeight :200,width :250});
	$(".w2ui-msg-body input[name='gardenReleaseName']").w2field('list', {items :gardenReleaseName,maxDropHeight :200,width :250});
	$(".w2ui-msg-body input[name='etcdReleases']").w2field('list', {items :etcdReleases,maxDropHeight :200,width :250});
	if(  menu != "cfDiego"){
		$(".w2ui-msg-body input[name='cfInfo']").w2field('list', {items :cfInfo,maxDropHeight :200,width :250});
	}
	$(".w2ui-msg-body input[name='cflinuxfs2rootfsrelease']").w2field('list', {items :cflinuxfs2rootfsrelease,maxDropHeight :200,width :250});
	setReleaseData();
}

/********************************************************
 * 설명		: 릴리즈 버전에 따른 cflinuxfs2rootfsrelease 화면 설정
 * Function	: setcflinuxDisplay
 *********************************************************/
function setcflinuxDisplay(val){
	var diegoReleaseVersion = val.split("/")[1];
	if( compare( diegoReleaseVersion, "0.1463.0" ) > 0 ){
		$('.w2ui-msg-body #cflinux').css('display','block');
	} else{
		$('.w2ui-msg-body #cflinux').css('display','none');
		$(".w2ui-msg-body input[name='cflinuxfs2rootfsrelease']").val("");
	}
}

/********************************************************
 * 설명		: Release Data 세팅
 * Function	: setReleaseData
 *********************************************************/
function setReleaseData(){
	if( !checkEmpty(defaultInfo.diegoReleaseName) && !checkEmpty(defaultInfo.diegoReleaseVersion) ){//diego 릴리즈
		$(".w2ui-msg-body input[name='diegoReleases']").data('selected',{text :defaultInfo.diegoReleaseName + "/"+ defaultInfo.diegoReleaseVersion});
	}
	if( !checkEmpty(defaultInfo.gardenReleaseName) &&  !checkEmpty(defaultInfo.gardenReleaseVersion) ){//Garden-Linux 릴리즈
		$(".w2ui-msg-body input[name='gardenReleaseName']").data('selected',{text :defaultInfo.gardenReleaseName + "/"+ defaultInfo.gardenReleaseVersion});
	}
	if( !checkEmpty(defaultInfo.etcdReleaseName) &&  !checkEmpty(defaultInfo.etcdReleaseVersion) ){//ETCD 릴리즈
		$(".w2ui-msg-body input[name='etcdReleases']").data('selected',{text :defaultInfo.etcdReleaseName + "/"+ defaultInfo.etcdReleaseVersion});
	}
	if( !checkEmpty(defaultInfo.cfDeploymentName)){//DIEGO와 연동할 CF 배포명
		$(".w2ui-msg-body input[name='cfInfo']").data('selected',{text :defaultInfo.cfDeploymentName });
	}
	if( !checkEmpty(defaultInfo.cflinuxfs2rootfsreleaseVersion) &&  !checkEmpty(defaultInfo.cflinuxfs2rootfsreleaseName)){ //cflinuxfs2rootfsrelease
		$('.w2ui-msg-body #cflinux').css('display','block');
		$(".w2ui-msg-body input[name='cflinuxfs2rootfsrelease']").data('selected',{text :defaultInfo.cflinuxfs2rootfsreleaseName + "/"+ defaultInfo.cflinuxfs2rootfsreleaseVersion});
	}
	w2popup.unlock();
}


/********************************************************
 * 설명		: Key 생성 확인
 * Function	: createKeyConfirm
 *********************************************************/
function createKeyConfirm(){
	 
	 var message = "";
	 if( !checkEmpty(diegoKeyFile) ){//이미 key가 생성됐으면,
		 message = "Key를 재 생성하시겠습니다.?";
	 }else{
		message ="Key를 생성하시겠습니까?"; 
	 }
	 
	 w2confirm({
		width 			: 350,
		height 			: 180,
		title 				: '<b>Key 생성 여부</b>',
		msg 				: message,
		modal			: true,
		yes_text 		: "확인",
		no_text 		: "취소",
		yes_callBack 	: function(){
			createKey();
		},
		no_callBack : function(event){
		}
	});
}

/********************************************************
 * 설명		:  diego 키 생성
 * Function	: createKey
 *********************************************************/
function createKey(){
	w2popup.lock("Key 생성 중입니다.", true);
	keyInfo = {
			id                      		: diegoId,
			iaas 							: iaas.toLowerCase(),
			platform					: "diego", //cf -> 1, diego -> 2, cf&diego -> 3
	}
	$.ajax({
		type : "POST",
		url : "/common/deploy/key/createKey",
		contentType : "application/json",
		data : JSON.stringify(keyInfo),
		async : true,
		success : function(data, status) {
			w2popup.unlock();
			w2popup.message({
			    width  : 500,
			    height : 120,
			    html   : '<div>' + 
						    	'<div style="padding: 10px; font-size:11px; text-align:center">'+ 
						    		"Key 생성에 성공하였습니다. <br/> 아래 SSH 핑거프린트를 복사하여 CF SSH 핑거프린트 항목에 입력 후 CF를 재 설치해주세요. <br/><br/>" +
									"ssh-key-fingerprint: <b>" +data.fingerprint + '</b>'+ 
								'</div>'+
								'<div rel="buttons" style="text-align:center;"><button class="btn" onclick="w2popup.message()">닫기</button><div>'+
							'</div>'
			});
			diegoKeyFile = data.keyFile;
			
		},
		error :function(request, status, error) {
			w2popup.unlock();
			var errorResult = JSON.parse(request.responseText);
			w2alert(errorResult.message, "Diego Key 생성");
		}
	});
}


/********************************************************
 * 설명		:  기본정보 저장
 * Function	: saveDefaultInfo
 *********************************************************/
function saveDefaultInfo(type) {
	if(cfInfoYn==true){
		w2alert("Diego와 연동 할 CF를 설치해 주세요.", "DIEGO 설치");
		return;
	}
	// 배포명 중복 검사
	if( !checkDeploymentNameDuplicate("diego", $(".w2ui-msg-body input[name='deploymentName']").val() ) 
			&& defaultInfo.deploymentName !=  $(".w2ui-msg-body input[name='deploymentName']").val() ){
		w2alert(  "입력한 배포명 (" + $(".w2ui-msg-body input[name='deploymentName']").val()  + ") 은 이미 존재합니다.","Diego 설치");
		return;
	}
	
   //key 생성하지 하지 않았을 경우
  	if( checkEmpty(diegoKeyFile) && menu =='diego' ){
  		w2alert("Diego Key를 먼저 생성해주세요.", "DIEGO 설치");
		return;
  	}
  	
	var diegoRelease = $(".w2ui-msg-body input[name='diegoReleases']").val();
	var gardenRelease = $(".w2ui-msg-body input[name='gardenReleaseName']").val();
	var etcdRelease = $(".w2ui-msg-body input[name='etcdReleases']").val();
	var cflinuxfs2rootfsrelease = $(".w2ui-msg-body input[name='cflinuxfs2rootfsrelease']").val();
	var cfName = $(".w2ui-msg-body input[name='cfInfo']").val();
	
	defaultInfo = {
				id 											: (diegoId) ? diegoId :"",
				iaas 										: iaas.toUpperCase(),
				platform									: "diego",
				deploymentName 					: $(".w2ui-msg-body input[name='deploymentName']").val(),
				directorUuid 							: $(".w2ui-msg-body input[name='directorUuid']").val(),
				diegoReleaseName 				: diegoRelease.split("/")[0],
				diegoReleaseVersion				: diegoRelease.split("/")[1],
				cfDeploymentName 				: cfName,
				cfId 										: $(".w2ui-msg-body input[name='cfId']").val(),
				cfDeployment							: $(".w2ui-msg-body input[name='cfDeployment']").val(),
				gardenReleaseName 				: gardenRelease.split("/")[0],
				gardenReleaseVersion			: gardenRelease.split("/")[1],
				etcdReleaseName 					: etcdRelease.split("/")[0],
				etcdReleaseVersion				: etcdRelease.split("/")[1],
				cflinuxfs2rootfsreleaseName 	: cflinuxfs2rootfsrelease.split("/")[0],
				cflinuxfs2rootfsreleaseVersion	: cflinuxfs2rootfsrelease.split("/")[1],
				keyFile										: diegoKeyFile
	}
	if( type == 'after'){
		if (popupValidation()) {
			$.ajax({
				type :"PUT",
				url :"/deploy/"+menu+"/install/saveDefaultInfo/N",
				contentType :"application/json",
				data :JSON.stringify(defaultInfo),
				success :function(data, status) {
					if( menu == "cfDiego" ){
						diegoId = data.content.diegoVo.id;
					}else{
						diegoId = data.content.id;
					}
					if(iaas.toUpperCase() == "VSPHERE"){
						vSphereNetworkPopup();
					}else{
						networkPopup();
					}
				},
				error :function(request, status, error) {
					var errorResult = JSON.parse(request.responseText);
					w2alert(errorResult.message, "diego  기본정보 등록");
					return;
				}
			});
		}
	}else{
		w2popup.clear();
		setModifyPopup();
	}
}

/********************************************************
 * 설명		: 네트워크 정보 팝업(openstack/aws)
 * Function	: networkPopup
 *********************************************************/
function networkPopup(){
	$("#networkInfoDiv").w2popup({
		width  : 750,
		height 	:700,
		modal 	:true,
		showMax :false,
		onOpen 	:function(event) {
			event.onComplete = function() {
				$(".addInternal").show();
				$(".delInternal").hide();
				if (networkInfo.length > 0) {
					networkId = networkInfo[0].id;
					for(var i=0; i <networkInfo.length; i++){
						var cnt = i;
						if(cnt > 0)  settingNetwork(cnt);
						$(".w2ui-msg-body input[name='subnetRange']").eq(cnt).val(networkInfo[i].subnetRange); 
						$(".w2ui-msg-body input[name='subnetGateway']").eq(cnt).val(networkInfo[i].subnetGateway);
						$(".w2ui-msg-body input[name='subnetDns']").eq(cnt).val(networkInfo[i].subnetDns);
						$(".w2ui-msg-body input[name='subnetReservedFrom']").eq(cnt).val(networkInfo[i].subnetReservedFrom);
						$(".w2ui-msg-body input[name='subnetReservedTo']").eq(cnt).val(networkInfo[i].subnetReservedTo);
						$(".w2ui-msg-body input[name='subnetStaticFrom']").eq(cnt).val(networkInfo[i].subnetStaticFrom);
						$(".w2ui-msg-body input[name='subnetStaticTo']").eq(cnt).val(networkInfo[i].subnetStaticTo);
						$(".w2ui-msg-body input[name='subnetId']").eq(cnt).val(networkInfo[i].subnetId);
						$(".w2ui-msg-body input[name='cloudSecurityGroups']").eq(cnt).val(networkInfo[i].cloudSecurityGroups);
					}
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
 
 /********************************************************
  * 설명		: 네트워크 정보 팝업(vSphere)
  * Function	: vSphereNetworkInfoDiv
  *********************************************************/
 function vSphereNetworkPopup(){
 		$("#VsphereNetworkInfoDiv").w2popup({
 			width : 750,
 			height : 500,
 			modal : true,
 			showMax : false,
 			onOpen : function(event) {
 				event.onComplete = function() {
 					$(".addInternal").show();
 					$(".delInternal").hide();
 					if (networkInfo.length > 0) {
 						networkId = networkInfo[0].id;
 						for(var i=0; i <networkInfo.length; i++){
							var cnt = i;
							if(cnt > 0)  settingNetwork(cnt);
 							$(".w2ui-msg-body input[name='subnetRange']").eq(cnt).val(networkInfo[i].subnetRange); 
 							$(".w2ui-msg-body input[name='subnetGateway']").eq(cnt).val(networkInfo[i].subnetGateway);
	 						$(".w2ui-msg-body input[name='subnetDns']").eq(cnt).val(networkInfo[i].subnetDns);
	 						$(".w2ui-msg-body input[name='subnetReservedFrom']").eq(cnt).val(networkInfo[i].subnetReservedFrom);
	 						$(".w2ui-msg-body input[name='subnetReservedTo']").eq(cnt).val(networkInfo[i].subnetReservedTo);
							$(".w2ui-msg-body input[name='subnetStaticFrom']").eq(cnt).val(networkInfo[i].subnetStaticFrom);
 							$(".w2ui-msg-body input[name='subnetStaticTo']").eq(cnt).val(networkInfo[i].subnetStaticTo);
	 						$(".w2ui-msg-body input[name='subnetId']").eq(cnt).val(networkInfo[i].subnetId);
 						}
 					}
 				}
 			},
 			onClose : function(event) {
 				event.onComplete = function() {
 					initSetting();
 				}
 			}
 		});
 }
  
/********************************************************
 * 설명		: 네트워크 화면 설정
 * Function	: settingNetwork
 *********************************************************/
 function settingNetwork( i ){
	  var cnt =1;
	  var panelBody = "#panel-body";
	  var networkDiv = "#networkInfoDiv_";
	  if( iaas.toLowerCase() == "vsphere" ){
		  networkDiv = "#VsphereNetworkInfoDiv_";
		  panelBody = "#panel-body2";
	  }
	  if( i != 0 && i != undefined ) cnt =  i;
	  $(".w2ui-msg-body "+networkDiv + i).addClass("panel panel-info");
	  $(".w2ui-msg-body "+networkDiv + i).html($(panelBody).html()).show();
	  if( i == 1){
		  $(".addInternal").hide(); 
		  $(networkDiv+"1" +" .addInternal").show();
		  $(networkDiv+"1" +" .delInternal").show();
	  } else if ( i > 1 ){
		  $(".addInternal").hide(); 			
		  $(networkDiv+"1" +" .delInternal").show();
		  $(networkDiv+"2" +" .delInternal").show();
	  }
 }

/********************************************************
 * 설명		: 네트워크 입력 추가
 * Function	: addNetwork
 *********************************************************/
 function addNetwork( ){
	var cnt = internalCnt;
	var networkDiv = "#networkInfoDiv_";
	var panelBody = "#panel-body";
	if (popupValidation()) {
		if( iaas.toLowerCase() == "vsphere" ){
			networkDiv = "#VsphereNetworkInfoDiv_";
			panelBody = "#panel-body2";
		}
		$(".w2ui-msg-body " +networkDiv +cnt).addClass("panel panel-info");
		$(".w2ui-msg-body " +networkDiv +cnt).html($(panelBody).html()).show(); 
		
		internalCnt ++;
		if(internalCnt == 2){
			$(".addInternal").hide(); 
			$(networkDiv +"1" +" .addInternal").show();
			$(networkDiv +"1" +" .delInternal").show();
		}else if ( internalCnt > 2 ){
			$(".addInternal").hide();
			$(networkDiv+"1" +" .delInternal").show();
			$(networkDiv+"2" +" .delInternal").show();
		}
	}
}

/********************************************************
 * 설명		: 네트워크 입력 삭제
 * Function	: delNetwork
 *********************************************************/
function delNetwork(value){

	var networkDiv = "#networkInfoDiv_";
	if( iaas.toLowerCase() == "vsphere" ){
		networkDiv = "#VsphereNetworkInfoDiv_";
	}
	var doc =  $(value).parent().parent().parent();
	var cnt = doc.attr("id").split("_")[1];
	$(".w2ui-msg-body #" + doc.attr("id")).removeClass("panel");
	$(".w2ui-msg-body #" + doc.attr("id")).removeClass("panel-info");
	if( internalCnt >2  && cnt == 1 ){
		$(".w2ui-msg-body #"+doc.attr("id")).html('');
		$(".w2ui-msg-body #VsphereNetworkInfoDiv_2").attr("id", "VsphereNetworkInfoDiv_1"); 
	}else{
		$(".w2ui-msg-body  #"+doc.attr("id")).html('');
	}
		
	internalCnt --;
	if( internalCnt == 1 ){ 
		$(".addInternal").show();
	}else if( internalCnt == 2  ){
		$(".addInternal").hide();
		$(networkDiv +"1" + " .addInternal").show();
		$(networkDiv +"1" + " .delInternal").show();
	}else{
		$(".addInternal").show();
		$("#networkInfoDiv .addInternal").hide();
		$("#networkInfoDiv .delInternal").hide();
	}
}

/********************************************************
 * 설명		:  Network 정보 저장
 * Function	: saveNetworkInfo
 *********************************************************/
function saveNetworkInfo(type) {
	networkInfo = [];
	 var subnetStaticFrom = "";
	 if( iaas.toUpperCase() == "VSPHERE" ){
		 subnetStaticFrom = $(".w2ui-msg-body input[name='publicStaticFrom']").val();
	 }else{
		 subnetStaticFrom = $(".w2ui-msg-body input[name='publicStaticIp']").val();
	 }
	 publicStaticIp = subnetStaticFrom;
	 //Internal
	 for(var i=0; i < $(".w2ui-msg-body input[name='subnetReservedFrom']").length; i++){
		var  InternalArr = {
				diegoId 								: diegoId,
					id									: networkId,
					iaas								: iaas.toUpperCase(),
					deployType					: 1400,
					net								: "Internal",
					seq								: i,
					subnetRange					: $(".w2ui-msg-body input[name='subnetRange']").eq(i).val(),
					subnetGateway				: $(".w2ui-msg-body input[name='subnetGateway']").eq(i).val(),
					subnetDns						: $(".w2ui-msg-body input[name='subnetDns']").eq(i).val(),
					subnetReservedFrom		: $(".w2ui-msg-body input[name='subnetReservedFrom']").eq(i).val(),
					subnetReservedTo			: $(".w2ui-msg-body input[name='subnetReservedTo']").eq(i).val(),
					subnetStaticFrom			: $(".w2ui-msg-body input[name='subnetStaticFrom']").eq(i).val(),
					subnetStaticTo				: $(".w2ui-msg-body input[name='subnetStaticTo']").eq(i).val(),
					subnetId						: $(".w2ui-msg-body input[name='subnetId']").eq(i).val(),
					cloudSecurityGroups		: $(".w2ui-msg-body input[name='cloudSecurityGroups']").eq(i).val()
			}
		 networkInfo.push(InternalArr);
	 }

	if (type == 'after') {
		if (popupValidation()) {
			//Server send Diego Info
			$.ajax({
				type :"PUT",
				url :"/deploy/"+menu+"/install/saveNetworkInfo",
				contentType :"application/json",
				async :true,
				data : JSON.stringify(networkInfo),
				success :function(data, status) {
					w2popup.clear();
					if( iaas.toLowerCase() == 'vsphere'){
						vSphereResourceInfoPopup();	
					}else{
						resourcePopup();
					}
				},
				error :function(e, status) {
					w2alert("Diego (OPENSTACK) Network 등록에 실패 하였습니다.", "Diego 설치");
				}
			});
		}
	} else if (type == 'before') {
		w2popup.clear();
		defaultPopup();
	}
}


/********************************************************
 * 설명		:  Resource 정보 팝업
 * Function	: Resource
 *********************************************************/
function resourcePopup() {
	$("#resourceInfoDiv").w2popup({
		width  : 750,
		height	:800,
		modal 	:true,
		showMax :false,
		onOpen :function(event) {
			event.onComplete = function() {
				if (resourceInfo != "") {
					$(".w2ui-msg-body input[name='boshPassword']").val(resourceInfo.boshPassword);
					$(".w2ui-msg-body input[name='smallFlavor']").val(resourceInfo.smallFlavor);
					$(".w2ui-msg-body input[name='mediumFlavor']").val(resourceInfo.mediumFlavor);
					$(".w2ui-msg-body input[name='largeFlavor']").val(resourceInfo.largeFlavor);
					$(".w2ui-msg-body input[name='runnerFlavor']").val(resourceInfo.runnerFlavor);
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

/********************************************************
 * 설명		: 리소스 정보 팝업(vSphere)
 * Function	: vSphereResourceInfoPopup
 *********************************************************/
function vSphereResourceInfoPopup() {
	$("#vSphereResourceInfoDiv").w2popup({
		width : 750,
		height : 800,
		modal : true,
		showMax : false,
		onOpen : function(event) {
			event.onComplete = function() {
				if (resourceInfo != "") {
					$(".w2ui-msg-body input[name='boshPassword']").val(resourceInfo.boshPassword);
					$(".w2ui-msg-body input[name='smallFlavorRam']").val(resourceInfo.smallRam);
					$(".w2ui-msg-body input[name='smallFlavorDisk']").val(resourceInfo.smallDisk);
					$(".w2ui-msg-body input[name='smallFlavorCpu']").val(resourceInfo.smallCpu);
					$(".w2ui-msg-body input[name='mediumFlavorRam']").val(resourceInfo.mediumRam);
					$(".w2ui-msg-body input[name='mediumFlavorDisk']").val(resourceInfo.mediumDisk);
					$(".w2ui-msg-body input[name='mediumFlavorCpu']").val(resourceInfo.mediumCpu);
					$(".w2ui-msg-body input[name='largeFlavorRam']").val(resourceInfo.largeRam);
					$(".w2ui-msg-body input[name='largeFlavorDisk']").val(resourceInfo.largeDisk);
					$(".w2ui-msg-body input[name='largeFlavorCpu']").val(resourceInfo.largeCpu);
					$(".w2ui-msg-body input[name='runnerFlavorRam']").val(resourceInfo.runnerRam);
					$(".w2ui-msg-body input[name='runnerFlavorDisk']").val(resourceInfo.runnerDisk);
					$(".w2ui-msg-body input[name='runnerFlavorCpu']").val(resourceInfo.runnerCpu);
				}
				w2popup.lock("스템셀을 조회 중입니다.", true);
				getStamcellList();
			}
		},
		onClose : function(event) {
			event.onComplete = function() {
				initSetting();
			}
		}
	});
}
 

 /********************************************************
  * 설명		: Diego 설치 스템셀 조회 
  * Function	: getStamcellList
  *********************************************************/
 function getStamcellList() {
	$.ajax({
		type : "GET",
		url : "/common/deploy/stemcell/list/diego/openstack",
		contentType : "application/json",
		success : function(data, status) {
			stemcells = new Array();
			if(data.records != null ){
				data.records.map(function(obj) {
					stemcells.push(obj.stemcellFileName+"/"+obj.stemcellVersion);
				});
			}
			$(".w2ui-msg-body input[name='stemcells']").w2field('list', {items : stemcells,maxDropHeight : 200,width : 250});
			setStemcellData();
		},
		error : function(e, status) {
			w2popup.unlock();
			w2alert("Stemcell List 를 가져오는데 실패하였습니다.", "CF 설치");
		}
	});
}


/********************************************************
 * 설명		:  리소스 정보 저장
 * Function	: saveResourceInfo
 *********************************************************/
function saveResourceInfo(type) {
	var stemcellInfos = $(".w2ui-msg-body input[name='stemcells']").val().split("/");
	resourceInfo = {
			id 								: diegoId,
			iaas							: iaas.toUpperCase(),
			platform						: "diego",
			stemcellName 			: stemcellInfos[0],
			stemcellVersion 			: stemcellInfos[1],
			boshPassword 			: $(".w2ui-msg-body input[name='boshPassword']").val(),
			smallFlavor				: $(".w2ui-msg-body input[name='smallFlavor']").val(),
			smallCpu					: $(".w2ui-msg-body input[name='smallFlavorCpu']").val(),
			smallRam					: $(".w2ui-msg-body input[name='smallFlavorRam']").val(),
			smallDisk					: $(".w2ui-msg-body input[name='smallFlavorDisk']").val(),
			mediumFlavor				: $(".w2ui-msg-body input[name='mediumFlavor']").val(),
			mediumCpu				: $(".w2ui-msg-body input[name='mediumFlavorCpu']").val(),
			mediumRam				: $(".w2ui-msg-body input[name='mediumFlavorRam']").val(),
			mediumDisk				: $(".w2ui-msg-body input[name='mediumFlavorDisk']").val(),
			largeFlavor					: $(".w2ui-msg-body input[name='largeFlavor']").val(),
			largeCpu					: $(".w2ui-msg-body input[name='largeFlavorCpu']").val(),
			largeRam					: $(".w2ui-msg-body input[name='largeFlavorRam']").val(),
			largeDisk					: $(".w2ui-msg-body input[name='largeFlavorDisk']").val(),
			runnerFlavor				: $(".w2ui-msg-body input[name='runnerFlavor']").val(),
			runnerCpu					: $(".w2ui-msg-body input[name='runnerFlavorCpu']").val(),
			runnerRam					: $(".w2ui-msg-body input[name='runnerFlavorRam']").val(),
			runnerDisk					: $(".w2ui-msg-body input[name='runnerFlavorDisk']").val()
	}

	if (type == 'after') {
		if(popupValidation()){	
			var url = "/deploy/diego/install/saveResourceInfo/N";
			//Server send Diego Info
			$.ajax({
				type 		:"PUT",
				url 		:url,
				contentType :"application/json",
				async 		:true,
				data 		:JSON.stringify(resourceInfo),
				success 	:function(data, status) {
					w2popup.clear();
					deploymentFile = data.deploymentFile;
					createSettingFile(data.id, deploymentFile);
				},
				error :function(e, status) {
					w2alert("Diego ("+iaas.toUpperCase()+") Resource 등록에 실패 하였습니다.", "Diego 설치");
				}
			});
		}
	} else if (type == 'before') {
			w2popup.clear();
			if( iaas.toLowerCase() == 'vsphere'){
				vSphereNetworkPopup();	
			}else{
				networkPopup();	
			}
			
	}
}

/********************************************************
 * 설명			:  Manifest 파일 생성
 * Function	: deployPopup
 *********************************************************/
function createSettingFile(id, deploymentFile){
	var settingFile = {
			id				: id,
			platform		: "diego"	
	}
	$.ajax({
		type : "POST",
		url : "/deploy/"+menu+"/install/createSettingFile/N",
		contentType : "application/json",
		data : JSON.stringify(settingFile),
		async : true,
		success : function(status) {
			deployPopup();
		},
		error :function(request, status, error) {
			var errorResult = JSON.parse(request.responseText);
			w2alert(errorResult.message, "diego  배포 파일 생성");
			if(iaas=="VSPHERE"){
				vSphereResourceInfoPopup();
			}else{
				resourcePopup();
			}
		}
	});
}
	

/********************************************************
 * 설명		:  배포 정보 팝업
 * Function	: deployPopup
 *********************************************************/
function deployPopup() {
	$("#deployDiv").w2popup({
		width : 750,
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

/********************************************************
 * 설명		:  배포 정보 조회
 * Function	: deployPopup
 *********************************************************/
function getDeployInfo() {
	var url = "/common/use/deployment/"+deploymentFile+"";
	$.ajax({
		type :"GET",
		url :url,
		contentType :"application/json",
		async :true,
		success :function(data, status) {
			if (status == "success") {
				$(".w2ui-msg-body #deployInfo").text(data);
			} else if (status == "204") {
				w2alert("배포파일이 존재하지 않습니다.", "DIEGO 설치");
				if(iaas == "VSPHERE"){
					vSphereResourceInfoPopup();	
				}else{
					resourcePopup();
				}
			}
		},
		error :function(e, status) {
			w2alert("Temp 파일을 가져오는 중 오류가 발생하였습니다. ", "DIEGO 설치");
			if(iaas.toLowerCase() == "VSPHERE"){
				vSphereResourceInfoPopup();	
			}else{
				resourcePopup();
			}
		}
	});
}

/********************************************************
 * 설명		:  배포 확인창 뒤로가기
 * Function	: resourcePopupSel
 *********************************************************/
function resourcePopupSel(){
	if(iaas == "VSPHERE"){
		vSphereResourceInfoPopup();	
	}else{
		resourcePopup();
	}
}

/********************************************************
 * 설명		:  Diego 설치 확인 팝업
 * Function	: diegoDeploy
 *********************************************************/
function diegoDeploy(type) {
	w2confirm({
		msg :"설치하시겠습니까?",
		title :w2utils.lang( menu.toUpperCase() +' 설치'),
		yes_text :"예",
		no_text :"아니오",
		yes_callBack :function(event) {
			if( menu != "cfDiego" ){
				installPopup();	
			}else{
				var selected = w2ui['config_cfDiegoGrid'].getSelection();
				var record = w2ui['config_cfDiegoGrid'].get(selected);
				//cf & diego 수정일 경우
				if( !checkEmpty(record) ) {
					 if( record.cfVo.deployStatus == 'CF 성공' ||  record.diegoVo.deployStatus == 'DIEGO 취소' ||  record.diegoVo.deployStatus == 'DIEGO 오류' ){
						 diegoInstallPopup(defaultInfo.deploymentName);
					 }else { 
						 cfInstallPopup(defaultInfo.cfDeploymentName, defaultInfo.deploymentName); 
					 }
				}else{
					cfInstallPopup(defaultInfo.cfDeploymentName);
				}
			}
		}
	});
}

/********************************************************
 * 설명		:  Diego Install Popup 
 * Function	: installPopup
 *********************************************************/
function installPopup(){
	var deploymentName = defaultInfo.deploymentName;
	var message = "DIEGO(배포명:" + deploymentName +  ") ";
	
	var requestParameter = {
			id 				: diegoId,
			iaas			: iaas,
			platform	: "diego"
	};
	
	$("#installDiv").w2popup({
		width 	: 750,
		height 	: 520,
		modal	:true,
		showMax :true,
		onOpen :function(event){
			event.onComplete = function(){
				//deployFileName
				var socket = new SockJS('/deploy/diego/install/diegoInstall');
				installClient = Stomp.over(socket); 
				installClient.connect({}, function(frame) {
			        installClient.subscribe('/user/deploy/diego/install/logs', function(data){
			        	
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
					    		
					    		installClient.disconnect();
								w2alert(message, "DIEGO 설치");
					       	}
			        	}

			        });
			        installClient.send('/send/deploy/diego/install/diegoInstall', {}, JSON.stringify(requestParameter));
			    });
			}
		}, onClose : function (event){
			event.onComplete= function(){
				$("textarea").text("");
				if( installClient != ""){
					installClient.disconnect();
				}
				initSetting();
			}
		}
	});
}

/********************************************************
 * 설명		:  조회한 스템셀 정보 설정
 * Function	: getStamcellList
 *********************************************************/
function setStemcellData(){
	if( !checkEmpty(resourceInfo.stemcellName) && !checkEmpty(resourceInfo.stemcellVersion) ){
		$(".w2ui-msg-body input[name='stemcells']").data('selected',{text : resourceInfo.stemcellName + "/"+ resourceInfo.stemcellVersion});
	}
	w2popup.unlock();
}

/********************************************************
 * 설명 :  Diego 삭제
 * Function : deletePopup
 *********************************************************/
function diegoDeletePopup(record){
	var requestParameter = {
			iaas			: (record.iaas) ? record.iaas : record.iaasType, 
			id				: record.id,
			platform		: "diego"
	};
	
	if ( record.deployStatus == null || record.deployStatus == '' ) {
		if( menu != "cfDiego" ){
			// 단순 레코드 삭제
			var url = "/deploy/diego/delete/data";
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
		}else{
			doSearch();
		}
	} else {
		var message = "";
		var body = '<textarea id="deleteLogs" style="width:95%;height:90%;overflow-y:visible;resize:none;background-color:#FFF; margin:2%" readonly="readonly"></textarea>';
		
		w2popup.open({
			width :700,
			height :500,
			title :"<b>DIEGO 삭제</b>",
			body  :body,
			buttons :'<button class="btn" style="float:right; padding-right:15%;" onclick="popupComplete();">닫기</button>',
			showMax :true,
			onOpen :function(event){
				event.onComplete = function(){
					var socket = new SockJS('/deploy/diego/delete/instance');
					deleteClient = Stomp.over(socket); 
					deleteClient.connect({}, function(frame) {
						deleteClient.subscribe('/user/deploy/diego/delete/logs', function(data){
							
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
									w2alert(message, "DIEGO 삭제");
						       	}
				        	}
				        	
				        });
						deleteClient.send('/send/deploy/diego/delete/instance', {}, JSON.stringify(requestParameter));
				    });
				}
			},
			onClose :function (event){
				event.onComplete= function(){
					$("textarea").text("");
					if( deleteClient != ""){
						deleteClient.disconnect();
					}
					initSetting();
				}
			}
		});
	}		
}

 /********************************************************
  * 설명		: 전역변수 초기화 
  * Function	: initSetting
  *********************************************************/
function initSetting() {
	//private var
	diegoId = "";
	defaultInfo = "";	
	cfInfo = "";
	diegoInfo = "";
	etcdInfo = "";
	peerInfo = "";
	networkInfo = [];
	publicStaticIp = "";
	internalCnt=0;
	resourceInfo = "";
	internalCnt =1;
	diegoReleases = "";
	cfReleases = "";
	gardenReleaseName = "";
	etcdReleases = "";
	cflinuxfs2rootfsrelease = "";
	stemcells = "";
	deploymentFile = "";
	defaultDirector = "";
	installClient = "";
	installStatus = "";
	modifyNetWork = "";
	cfInfoYn= false;
	diegoKeyFile ="";
	//grid Reload
	gridReload();
}

 /********************************************************
  * 설명		: Install/Delete 팝업 종료시 Event 
  * Function	: popupComplete
  *********************************************************/
function popupComplete(){
	var msg;
	if(installStatus == "done" || installStatus == 'error'){
		msg = $(".w2ui-msg-title b").text() + " 화면을 닫으시겠습니까?";
	}else{
		msg = $(".w2ui-msg-title b").text() + " 화면을 닫으시겠습니까?<BR>(닫은 후에도 완료되지 않는 설치 또는 삭제 작업은 계속 진행됩니다.)";
	}
	w2confirm({
		title 	: $(".w2ui-msg-title b").text(),
		msg		: msg,
		yes_text:"확인",
		yes_callBack :function(envent){
			w2popup.close();
			//params init
			initSetting();
		},
		no_text :"취소"
	});
}
 
/********************************************************
 * 설명		: 그리드 재조회
 * Function	: gridReload
 *********************************************************/
function gridReload() {
	 if( menu == "diego" ){
		 w2ui['config_diegoGrid'].reset();
		 doSearch();
	 }else if( menu =="cfDiego" ){
		 w2ui['config_cfDiegoGrid'].load("<c:url value='/deploy/cfDiego/list/"+iaas+"'/>",
					function() { doButtonStyle(); });
	 }
}
</script>

<!-- 기본 정보 설정 DIV -->
<div id="defaultInfoDiv" style="width:100%; height:100%;" hidden="true">
	<div rel="title"><b>DIEGO 설치</b></div>
	<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
		<div style="margin-left:2%;display:inline-block;width:97%;">
			<ul class="progressStep_5">
				<li class="active">기본 정보</li>
				<li class="before">네트워크 정보</li>
				<li class="before">리소스 정보</li>
				<li class="before">배포파일 정보</li>
				<li class="before">설치</li>
			</ul>
		</div>
		<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
			<div class="panel panel-info">	
				<div class="panel-heading"><b>기본 정보</b></div>
				<div class="panel-body" style="padding:5px 5% 10px 5%;">
					<div class="w2ui-field" >
						<label style="text-align:left; width:40%; font-size:11px;">설치관리자 UUID</label>
						<div>
							<input name="directorUuid" type="text" style="float:left; width:60%;" required placeholder="설치관리자 UUID를 입력하세요." readonly="readonly"/>
							<div class="isMessage"></div>
						</div>
					</div>
					<div class="w2ui-field" >
						<label style="text-align:left; width:40%; font-size:11px;">배포 명</label>
						<div>
							<input name="deploymentName" type="text" style="float:left; width:60%;" required placeholder="" readonly="readonly"/>
							<div class="isMessage"></div>
						</div>
					</div>
					<div class="w2ui-field" >
						<label style="text-align:left; width:40%; font-size:11px;">DIEGO 릴리즈</label>
						<img alt="diego-help-info"  src="../images/help-Info-icon.png" class="diego-info" style="width:18px; position:absolute; left:20%; margin-top:3px"  data-toggle="popover"  data-trigger="hover" data-html="true" title="Diego 릴리즈 설치 지원 버전 목록"/>
						<div>
							<input name="diegoReleases" onchange='setcflinuxDisplay(this.value);' type="list" style="float:left; width:60%;" required placeholder="DIEGO 릴리즈를 선택하세요." />
						</div>
					</div>
					<div class="w2ui-field" id="cflinux" style="display:none">
						<label style="text-align:left; width:40%; font-size:11px;">Cflinuxfs2-Rootfs 릴리즈</label>
						<img alt="cflinux-help-info" class="cflinux-info"  style="width:18px; position:absolute; left:27%; margin-top:3px"  data-toggle="popover"  data-html="true" title="cflinuxfs2Root 릴리즈 호환성 정보 참조 사이트"  src="../images/help-Info-icon.png"/>
						<div>
							<input name="cflinuxfs2rootfsrelease" type="list" style="float:left; width:60%;" required placeholder="cflinuxfs2Root 릴리즈를 선택하세요." />
						</div>
					</div>
					<div class="w2ui-field" >
						<label style="text-align:left; width:40%; font-size:11px;">Garden-Linux 릴리즈</label>
						<img alt="gardenRelease-help-info" class="gardenRelease-info" style="width:18px; position:absolute; left:25%; margin-top:3px"   data-toggle="popover"  title="Garden-Linux 릴리즈 호환성 정보 참조 사이트 "  data-html="true" src="../images/help-Info-icon.png"/>
						<div>
							<input name="gardenReleaseName" type="list" style="float:left; width:60%;" required placeholder="Garden-Linux 릴리즈를 선택하세요." />
						</div>
					</div>
					<div class="w2ui-field" >
						<label style="text-align:left; width:40%; font-size:11px;">ETCD 릴리즈</label>
						<img alt="etcdReleases-help-info" class="etcd-info" style="width:18px; position:absolute; left:18%; margin-top:3px"  data-toggle="popover" title="ETCD 릴리즈 호환성 정보 참조 사이트 "  data-html="true" src="../images/help-Info-icon.png">
						<div>
							<input name="etcdReleases" type="list" style="float:left; width:60%;" required placeholder="ETCD 릴리즈를 선택하세요." />
						</div>
					</div>
					<div class="w2ui-field" >
						<label style="text-align:left; width:40%; font-size:11px;">DIEGO와 연동할 CF 배포명</label>
						<div id="getCfInfo"></div>
						<input name="cfId" type="hidden"/>
						<input name="cfDeploymentFile" type="hidden"/>
					</div>
				</div>
				<button class="btn" style="float: right; margin-top:10px;" id="keyBtn" onclick="createKeyConfirm();" >Key 생성</button>
			</div>
		</div>
		<br />
		<div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" id="defaultPopupBtn" style="float:left; display:none" onclick="saveDefaultInfo('before');">이전</button>
			<button class="btn" style="float:right; padding-right:15%" onclick="saveDefaultInfo('after');">다음>></button>
		</div>
	</div>
</div>

<!-- network 정보 -->
<div id="networkInfoDiv" style="width:100%; height:100%;" hidden="true">
	<div rel="title">DIEGO 설치</div>
	<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
		<div style="margin-left:2%;display:inline-block;width:97%;">
			<ul class="progressStep_5">
				<li class="pass">기본 정보</li>
				<li class="active">네트워크 정보</li>
				<li class="before">리소스 정보</li>
				<li class="before">배포파일 정보</li>
				<li class="before">설치</li>
			</ul>
		</div>
		<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
			<div class="panel panel-info">	
				<div class="panel-heading"><b>네트워크 정보</b></div>
				<div class="panel-body">
					<div class="panel panel-info" id="panel-body">	
						<div  class="panel-heading" style="padding:5px 5% 17px 5%;">
							<b>Internal</b>
							<div style="float:right">
								<button class="btn addInternal" onclick="addNetwork();">추가</button>
								<button class="btn delInternal" onclick="delNetwork(this);">삭제</button> 
							</div>
						</div>
						<div class="panel-body">
							<div class="w2ui-field">
								<label style="text-align: left; width: 40%; font-size: 11px;">시큐리티 그룹</label>
								<div>
									<input name="cloudSecurityGroups" type="text" style="float: left; width: 60%;" required placeholder="예) diego-security" />
									<div class="isMessage"></div>
								</div>
							</div>
					    	<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">네트워크 ID</label>
								<div>
									<input name="subnetId" type="text"  style="float:left;width:60%;" required placeholder="네트워크 ID를 입력하세요."/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">서브넷 범위</label>
								<div>
									<input name="subnetRange" type="text"  style="float:left;width:60%;"  required placeholder="예) 10.0.0.0/24"/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">게이트웨이</label>
								<div>
									<input name="subnetGateway" type="text"  style="float:left;width:60%;"  required placeholder="예) 10.0.0.1"/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">DNS</label>
								<div>
									<input name="subnetDns" type="text"  style="float:left;width:60%;" required placeholder="예) 8.8.8.8"/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left; width: 40%; font-size: 11px;">IP할당 제외 대역</label>
								<div>
									<input name="subnetReservedFrom" id="subnetStaticFrom" type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.100" />
									<span style="float: left; width: 6%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
									<input name="subnetReservedTo" id="subnetStaticTo" type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.106" />
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left; width: 40%; font-size: 11px;">IP할당 대역(최소 15개)</label>
								<div>
									<input name="subnetStaticFrom" type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.100" />
									<span style="float: left; width: 6%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
									<input name="subnetStaticTo" type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.106" />
									<div class="isMessage"></div>
								</div>
							</div>
						</div>
					</div>
					<!-- 추가 네트워크 div_1 -->
					<div  id="networkInfoDiv_1" ></div>
					<!-- 추가 네트워크 div_2 -->
					<div  id="networkInfoDiv_2"></div>
				</div>
			</div>
	    </div>
		<br/>
		<div class="w2ui-buttons" rel="buttons" hidden="true">
			<button class="btn" style="float:left;" onclick="saveNetworkInfo('before');">이전</button>
			<button class="btn" style="float:right; padding-right:15%" onclick="saveNetworkInfo('after');">다음>></button>
		</div>
	</div>
</div>

<!-- vSphere Network -->
<div id="VsphereNetworkInfoDiv" style="width:100%;height:100%;" hidden="true">
	<div rel="title">DIEGO 설치</div>
	<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
		<div style="margin-left:2%;display:inline-block;width:97%;">
			<ul class="progressStep_5">
				<li class="pass">기본 정보</li>
				<li class="active">네트워크 정보</li>
				<li class="before">리소스 정보</li>
				<li class="before">배포파일 정보</li>
				<li class="before">설치</li>
			</ul>
		</div>
        <div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
					<!-- Internal -->
					<div class="panel panel-info"  id="panel-body2">
						<div  class="panel-heading" style="padding:5px 5% 17px 5%;">
							<b>Internal</b>
							<div style="float:right">
								<button class="btn addInternal" onclick="addNetwork();">추가</button>
								<button class="btn delInternal" onclick="delNetwork(this);">삭제</button>
							</div>
						</div>
						<div class="panel-body">
					    	<div class="w2ui-field">
								<label class="subnetId" style="text-align: left;width:40%;font-size:11px;">포트 그룹명</label>
								<div>
									<input name="subnetId" type="text"  style="float:left;width:60%;" required placeholder="포트 그룹명을 입력하세요."/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">서브넷 범위</label>
								<div>
									<input name="subnetRange" type="text"  style="float:left;width:60%;"  required placeholder="예) 10.0.0.0/24"/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">게이트웨이</label>
								<div>
									<input name="subnetGateway" type="text"  style="float:left;width:60%;"  required placeholder="예) 10.0.0.1"/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left;width:40%;font-size:11px;">DNS</label>
								<div>
									<input name="subnetDns" type="text"  style="float:left;width:60%;" required placeholder="예) 8.8.8.8"/>
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left; width: 40%; font-size: 11px;">IP할당 제외 대역</label>
								<div>
									<input name="subnetReservedFrom" id="subnetStaticFrom" type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.100" />
									<span style="float: left; width: 6%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
									<input name="subnetReservedTo" id="subnetStaticTo" type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.106" />
									<div class="isMessage"></div>
								</div>
							</div>
							<div class="w2ui-field">
								<label style="text-align: left; width: 40%; font-size: 11px;">IP할당 대역(최소 15개)</label>
								<div>
									<input name="subnetStaticFrom" type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.100" />
									<span style="float: left; width: 6%; text-align: center;">&nbsp;&ndash; &nbsp;</span>
									<input name="subnetStaticTo" type="url" style="float:left;width:27%;" placeholder="예) 10.0.0.106" />
									<div class="isMessage"></div>
								</div>
							</div>
						</div>
					</div>
					<!-- 추가 네트워크 div_1 -->
					<div  id="VsphereNetworkInfoDiv_1" ></div>
					<div  id="VsphereNetworkInfoDiv_2" ></div>
				</div><br/>
		    <div class="w2ui-buttons" rel="buttons" hidden="true">
		        <button class="btn" style="float: left;" onclick="saveNetworkInfo('before');" >이전</button>
				<button class="btn" style="float: right; padding-right: 15%" onclick="saveNetworkInfo('after');" >다음>></button>
		    </div>
		</div>
	  </div>


<!-- Resource  설정 DIV -->
<div id="resourceInfoDiv" style="width:100%; height:100%;" hidden="true">
	<div rel="title"><b>DIEGO 설치</b></div>
	<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
		<div style="margin-left:2%;display:inline-block;width:97%;">
			<ul class="progressStep_5">
				<li class="pass">기본 정보</li>
				<li class="pass">네트워크 정보</li>
				<li class="active">리소스 정보</li>
				<li class="before">배포파일 정보</li>
				<li class="before">설치</li>
			</ul>
		</div>
		<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
			<div class="panel panel-info">	
				<div class="panel-heading"><b>리소스 정보</b></div>
				<div class="panel-body" style="padding:5px 5% 10px 5%;">
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">Stemcell</label>
						<div>
							<div>
								<input type="list" name="stemcells" style="float: left; width:60%; margin-top: 1.5px;" placeholder="스템셀을 선택하세요.">
							</div>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">VM 비밀번호</label>
						<div>
							<input name="boshPassword" type="text" style="float: left; width: 60%;" required placeholder="VM 비밀번호를 입력하세요." />
							<div class="isMessage"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="panel panel-info">	
				<div class="panel-heading"><b>Small Instance Type</b></div>
				<div class="panel-body"  style="padding:5px 5% 10px 5%;">
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">Flavor</label>
						<div>
							<input name="smallFlavor" type="text" style="float: left; width: 60%;" required placeholder="Small Instance Type을 입력하세요."  />
							<div class="isMessage"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="panel panel-info">	
				<div class="panel-heading"><b>Medium Instance Type</b></div>
				<div class="panel-body"  style="padding:5px 5% 10px 5%;" >
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">Flavor</label>
						<div>
							<input name="mediumFlavor" type="text" style="float: left; width: 60%;" required placeholder="Medium Instance Type을 입력하세요."  />
							<div class="isMessage"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="panel panel-info">	
				<div class="panel-heading"><b>Large Instance Type</b></div>
				<div class="panel-body"  style="padding:5px 5% 10px 5%;">
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">Flavor</label>
						<div>
							<input name="largeFlavor" type="text" style="float: left; width: 60%;" required placeholder="Large Instance Type을 입력하세요."  />
							<div class="isMessage"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="panel panel-info">	
				<div class="panel-heading"><b>Cell Instance Type</b></div>
				<div class="panel-body"  style="padding:5px 5% 10px 5%;">
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">Flavor</label>
						<div>
							<input name="runnerFlavor" type="text" style="float: left; width: 60%;" required placeholder="Cell Instance Type을 입력하세요."  />
							<div class="isMessage"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="w2ui-buttons" rel="buttons" hidden="true">
		<button class="btn" style="float:left;" onclick="saveResourceInfo('before');">이전</button>
		<button class="btn" style="float:right; padding-right:15%" onclick="saveResourceInfo('after');">다음>></button>
	</div>
</div>

<!--  vSphere Resource -->
<div id="vSphereResourceInfoDiv" style="width: 100%; height: 100%;" hidden="true">
	<div rel="title"><b>DIEGO 설치</b></div>
	<div rel="body" style="width: 100%; height: 100%; padding: 15px 5px 0 5px; margin: 0 auto;">
		<div style="margin-left: 2%;display:inline-block;width: 98%;">
			<ul class="progressStep_5">
				<li class="pass">기본 정보</li>
				<li class="pass">네트워크 정보</li>
				<li class="active">리소스 정보</li>
				<li class="before">배포파일 정보</li>
				<li class="before">설치</li>
			</ul>
		</div>
		<div class="w2ui-page page-0" style="margin-top:15px;padding:0 3%;">
			<div class="panel panel-info">	
				<div class="panel-heading"><b>리소스 정보</b></div>
				<div class="panel-body" style="padding:5px 5% 10px 5%;">
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">Stemcell</label>
						<div>
							<div>
								<input type="list" name="stemcells" style="float: left; width:60%; margin-top: 1.5px;" placeholder="스템셀을 선택하세요.">
							</div>
						</div>
					</div>
					<div class="w2ui-field">
						<label style="text-align: left; width: 40%; font-size: 11px;">VM 비밀번호</label>
						<div>
							<input name="boshPassword" type="text" style="float: left; width: 60%;"  required placeholder="VM 비밀번호를 입력하세요." />
							<div class="isMessage"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="panel panel-info">	
				<div class="panel-heading"><b>Small Resource Type</b></div>
				<div class="panel-body"  style="padding:5px 5% 10px 5%;">
					<div class="w2ui-field">
						<label style="text-align: left;  width: 40%;">Small Type Ram</label>
						<div>
							<input name="smallFlavorRam" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Ram을 입력하세요. 예) 1024"  />
							<div class="isMessage "></div>
						</div>
					</div>
					<div class="w2ui-field">	
						<label style="text-align: left;  width: 40%; ">Small Type Disk</label>
						<div>
							<input name="smallFlavorDisk" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Disk를 입력하세요. 예) 4096"  />
							<div class="isMessage "></div>
						</div>
					</div>
					<div class="w2ui-field">	
						<label style="text-align: left;  width: 40%;">Small Type Cpu</label>
						<div>
							<input name="smallFlavorCpu" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Cpu를 입력하세요. 예) 1"  />
							<div class="isMessage "></div>
						</div>
					</div>
				</div>
			</div>
			<div class="panel panel-info">	
				<div class="panel-heading"><b>Medium Resource Type</b></div>
				<div class="panel-body"  style="padding:5px 5% 10px 5%;" >
					<div class="w2ui-field">
						<label style="text-align: left;  width: 40%;">Medium Type Ram</label>
						<div>
							<input name="mediumFlavorRam" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Ram을 입력하세요. 예) 1024"  />
							<div class="isMessage "></div>
						</div>
					</div>
					<div class="w2ui-field">	
						<label style="text-align: left;  width: 40%;">Medium Type Disk</label>
						<div>
							<input name="mediumFlavorDisk" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Disk를 입력하세요. 예) 4096"  />
							<br/><div class="isMessage "></div>
						</div>
					</div>
					<div class="w2ui-field">	
						<label style="text-align: left;  width: 40%;">Medium Type Cpu</label>
						<div>
							<input name="mediumFlavorCpu" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Cpu를 입력하세요. 예) 1"  />
							<div class="isMessage "></div>
						</div>
					</div>
				</div>
			</div>
			<div class="panel panel-info">	
				<div class="panel-heading"><b>Large Resource Type</b></div>
				<div class="panel-body"  style="padding:5px 5% 10px 5%;">
					<div class="w2ui-field">
						<label style="text-align: left;  width: 40%;">Large Type Ram</label>
						<div>
							<input name="largeFlavorRam" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Ram을 입력하세요. 예) 1024"  />
							<div class="isMessage"></div>
						</div>
					</div>
					<div class="w2ui-field">	
						<label style="text-align: left;  width: 40%;">Large Type Disk</label>
						<div>
							<input name="largeFlavorDisk" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Disk를 입력하세요. 예) 10240 "  />
							<div class="isMessag"></div>
						</div>
					</div>
					<div class="w2ui-field">	
						<label style="text-align: left;  width: 40%;">Large Type Cpu</label>
						<div>
							<input name="largeFlavorCpu" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Cpu를 입력하세요. 예) 1"  />
							<div class="isMessage"></div>
						</div>
					</div>
					</div>
				</div>
			<div class="panel panel-info">	
				<div class="panel-heading"><b>Cell Resource Type</b></div>
				<div class="panel-body"  style="padding:5px 5% 10px 5%;">
					<div class="w2ui-field">
						<label style="text-align: left;  width: 40%;">Runner Type Ram</label>
						<div>
							<input name="runnerFlavorRam" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Ram을 입력하세요. 예) 16384"  />
							<div class="isMessage"></div>
						</div>
					</div>
					<div class="w2ui-field">	
						<label style="text-align: left;  width: 40%;">Runner Type Disk</label>
						<div>
							<input name="runnerFlavorDisk" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Disk를 입력하세요. 예) 32768"  />
							<div class="isMessage"></div>
						</div>
					</div>
					<div class="w2ui-field">	
						<label style="text-align: left;  width: 40%;">Runner Type Cpu</label>
						<div>
							<input name="runnerFlavorCpu" type="text" style="float: left; width: 60%;"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' style='ime-mode:disabled;'  required placeholder="Cpu를 입력하세요. 예) 2"  />
							<div class="isMessage"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
</div>
	<div class="w2ui-buttons" rel="buttons" hidden="true">
		<button class="btn" style="float: left;" onclick="saveResourceInfo('before');">이전</button>
		<button class="btn" style="float: right; padding-right: 15%" onclick="saveResourceInfo('after');">다음>></button>
	</div>
</div>

<!-- 배포파일 정보 -->
<div id="deployDiv" style="width: 100%; height: 100%;" hidden="true">
	<div rel="title"><b>DIEGO 설치</b></div>
	<div rel="body" style="width:100%; height:100%; padding:15px 5px 0 5px; margin:0 auto;">
		<div style="margin-left:2%;display:inline-block;width:97%;">
			<ul class="progressStep_5">
				<li class="pass">기본 정보</li>
				<li class="pass">네트워크 정보</li>
				<li class="pass">리소스 정보</li>
				<li class="active">배포파일 정보</li>
				<li class="before">설치</li>
			</ul>
		</div>
		<div style="width:95%;height:82%;float:left;display: inline-block;margin-top:1%;">
			<textarea id="deployInfo" style="width:100%;height:99%;overflow-y:visible;resize:none;background-color: #FFF;margin-left:3%;" readonly="readonly"></textarea>
		</div>
	</div>
	<div class="w2ui-buttons" rel="buttons" hidden="true">
		<button class="btn" style="float: left;" onclick="resourcePopupSel();">이전</button>
		<button class="btn" style="float: right; padding-right: 15%" onclick="diegoDeploy('after');">다음>></button>
	</div>
</div>

<!-- diego 설치화면 -->
<div id="installDiv" style="width:100%; height:100%;" hidden="true">
	<div rel="title">DIEGO 설치</div>
	<div rel="body" style="width:100%;height:100%;padding:15px 5px 0 5px;margin:0 auto;">
		<div style="margin-left:2%;display:inline-block;width:97%;">
			<ul class="progressStep_5">
				<li class="pass">기본 정보</li>
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
		<button class="btn" id="deployPopupBtn" style="float:left;" onclick="deployPopup()" disabled>이전</button>
		<button class="btn" style="float:right; padding-right:15%" onclick="popupComplete();">닫기</button>
	</div>
</div>
