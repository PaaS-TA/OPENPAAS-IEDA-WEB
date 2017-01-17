/*******************************************************************************
 * 설명 : 기본설치관리자 정보 조회
 ******************************************************************************/
function getDefaultDirector(url) {
	var isOk = true;

	var directorInfoDiv = '<div class="title">설치 관리자</div>';
	directorInfoDiv += '<table class="tbl1" border="1" cellspacing="0">';
	directorInfoDiv += '<tr><th width="18%" class="th_fb">관리자 이름</th><td class="td_fb"><b id="directorName"></b></td>';
	directorInfoDiv += '<th width="18%" class="th_fb">관리자 계정</th><td class="td_fb"><b id="userId"></b></td></tr>';
	directorInfoDiv += '<tr><th width="18%" >관리자 URL</th><td><b id="directorUrl"></b></td>';
	directorInfoDiv += '<th width="18%" >관리자 UUID</th><td ><b id="directorUuid"></b></td></tr></table>';

		$.ajax({
				type : "get",
				url : url,
				async : false,
				error : function(request, status, error) {
					if (error == "Forbidden") {
						location.href = "/abuse";
					}
				},
				success : function(data) {
					if (!checkEmpty(data)) {
						$("#isDefaultDirector").html(directorInfoDiv);
						setDefaultDirectorInfo(data);
						isOk = data.connect;
					} else {
						isOk = false;
						var message = "기본 설치관리자가 존재하지 않습니다. 플랫폼설치 -> BOOSTRAP설치 메뉴를 이용해서 BOOTSTRAP 설치 후 설치관리자를 등록하세요.";
						var errorDirectorDiv = '<div class="alert alert-danger" style="font-size:15px;text-align:center;"><strong>'
								+ message + '</strong></div>';
						$("#isDefaultDirector").html(errorDirectorDiv);
					}
				}
			});
	return isOk;
}

/*******************************************************************************
 * 설명 : 기본설치관리자 설정 
 * Function : setDefaultDirectorInfo
 ******************************************************************************/
function setDefaultDirectorInfo(data) {
	$('#directorName').text(data.directorName + '(' + data.directorCpi + ')');
	$('#userId').text(data.userId);

	var diretorUrl = "https://" + data.directorUrl + ":" + data.directorPort;

	$('#directorUrl').text(diretorUrl);
	$('#directorUuid').text(data.directorUuid);
}

/*******************************************************************************
 * 설명 : Lock 파일 설정 
 * Function : commonLockFile
 ******************************************************************************/
function commonLockFile(url, message) {

	var lock = true;
	$.ajax({
		type : "get",
		url : url,
		async : false,
		error : function(request, status, error) {
			if (error == "Forbidden") {
				location.href = "/abuse";
			}
			lock = false;
		},
		success : function(data) {
			if (!data) {
				w2alert(message);
			}
			lock = data;
		}
	});
	return lock;
}

/*******************************************************************************
 * 설명 : 배포명 중복 체크
 * Function : checkDeploymentNameDuplicate
 ******************************************************************************/
function checkDeploymentNameDuplicate( platform, deploymentName, iaas ) {
	var check = true;
	$.ajax({
		type : "get",
		url : "/common/deploy/deployments/"+platform + "/" +iaas,
		async : false,
		success : function(data) {
			data.map(function(obj) {
				if( deploymentName == obj){
					check = false;
				}
			});
		}
	});
	return check;
}

/*******************************************************************************
 * 설명 : 버전 비교
 * Function : compare
 ******************************************************************************/
function compare(a, b) {
    if (a === b) {
       return 0;
    }
    var a_components = a.split(".");
    var b_components = b.split(".");

    var len = Math.min(a_components.length, b_components.length);

    // loop while the components are equal
    for (var i = 0; i < len; i++) {
        // A bigger than B
        if (parseInt(a_components[i]) > parseInt(b_components[i])) {
            return 1;
        }

        // B bigger than A
        if (parseInt(a_components[i]) < parseInt(b_components[i])) {
            return -1;
        }
    }

    // If one's a prefix of the other, the longer one is greater.
    if (a_components.length > b_components.length) {
        return 1;
    }
    if (a_components.length < b_components.length) {
        return -1;
    }
    // Otherwise they are the same.
    return 0;
}



/*******************************************************************************
 * 설명 : 특수문자 유효성 
 * Function : Keycode
 ******************************************************************************/
function Keycode(e) {
	var code = (window.event) ? event.keyCode : e.which; // IE : FF - Chrome
	// both
	if (code > 32 && code < 48)
		nAllow(e);
	if (code > 57 && code < 65)
		nAllow(e);
	if (code > 90 && code < 97)
		nAllow(e);
	if (code > 122 && code < 127)
		nAllow(e);
}

/*******************************************************************************
 * 설명 : 특수문자 유효성 메시지 
 * Function : nAllow
 ******************************************************************************/
function nAllow(e) {
	setGuideMessage($(".w2ui-msg-body #codeValueSuccMsg"), "",
			$(".w2ui-msg-body #codeValueErrMsg"), "특수문자는 사용할 수 없습니다.");

	if (navigator.appName != "Netscape") { // for not returning keycode value
		event.returnValue = false; // IE , - Chrome both
	} else {
		e.preventDefault(); // FF , - Chrome both
	}
}

/*******************************************************************************
 * 설명 : 숫자만 입력
 *  Function : onlyNumber
 ******************************************************************************/
function onlyNumber(event) {
	event = event || window.event;
	var keyID = (event.which) ? event.which : event.keyCode;
	if ((keyID >= 48 && keyID <= 57) || (keyID >= 96 && keyID <= 105)
			|| keyID == 8 || keyID == 9 || keyID == 46 || keyID == 37
			|| keyID == 39)
		return;
	else
		return false;
}

/*******************************************************************************
 * 설명 : 포트 숫자만 입력
 *  Function : onlyNumberPort
 ******************************************************************************/
function onlyNumberPort(val){
	regNumber = /^[0-9]*$/;
	if (regNumber.test(val) == true) {
		return true;
	} else {
		return false;
	}
}

/*******************************************************************************
 * 설명 : _만 입력 
 * Function : onlyNumberSpecialChar
 ******************************************************************************/
function onlyNumberSpecialChar(event) {
	var special_pattern = /[_]/gi;
	if (special_pattern.test(event.value) == true) {
		return true;

	} else {
		return false;
	}
}

/*******************************************************************************
 * 설명 : 문자 remove 
 * Function : removeChar
 ******************************************************************************/
function removeChar(event) {
	event = event || window.event;
	var keyID = (event.which) ? event.which : event.keyCode;
	if (keyID == 8 || keyID == 46 || keyID == 37 || keyID == 39)
		return;
	else
		event.target.value = event.target.value.replace(/[^0-9_]/g, "");
}

/*******************************************************************************
 * 설명 : 한글만 입력 
 * Function : fn_press_han
 ******************************************************************************/
function fn_press_han(event, obj) {
	// 좌우 방향키, 백스페이스, 딜리트, 탭키에 대한 예외
	event = event || window.event;
	if (event.keyCode == 8 || event.keyCode == 9 || event.keyCode == 37
			|| event.keyCode == 39 || event.keyCode == 46)
		return;

	obj.value = obj.value.replace(/[\ㄱ-ㅎㅏ-ㅣ가-힣]/g, '');
}

/*******************************************************************************
 * 설명 : 입력값 오류 경고 
 * Function : setGuideMessage
 ******************************************************************************/
function setGuideMessage(successObject, successMessage, errorObject,
		errorMessage) {
	if (successMessage == "")
		successObject.html(successMessage);
	else {
		errorObject.css("color", "grey");
		successObject.html(successMessage).show().fadeOut(300);
	}
	alert(errorMessage);
}

/*******************************************************************************
 * 설명 : 빈값 체크 
 * Function : checkEmpty
 ******************************************************************************/
function checkEmpty(value) {
	return (value == null || value == "") ? true : false;
}

/*******************************************************************************
 * 설명 : URL 확인 
 * Function : validateIP
 ******************************************************************************/
function validateIP(input) {
	if (/^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/
			.test(input))
		return true;
	else
		return false;
}

/*******************************************************************************
 * 설명 : 이메일 유효성 
 * Function : emailValidation
 ******************************************************************************/
function emailValidation(email) {
	var email = email;
	var regex = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/;
	if (regex.test(email) == false)
		return false;
	else
		return true;
}

/*******************************************************************************
 * 설명 : 특수 문자 유효성
 * Function : specialCharacterValidation
 ******************************************************************************/
function specialCharacterValidation(input) {
	var txt = input;
	var regex = /[\{\}\[\]\/?.,;:|\)*~`!^\-+<>@\#$%&\\\=\(\'\"]/i;
	if (regex.test(txt)) {
		return true;
	} else {
		return false;
	}
}

/*******************************************************************************
 * 설명 : 문자 길이 문자 유효성 
 * Function : textLengthValidation
 ******************************************************************************/
function textLengthValidation(input) {
	var txt = input;
	var pattern = /^[\w\Wㄱ-ㅎㅏ-ㅣ가-힣]{2,15}$/;
	if (pattern.test(txt)) {
		return true;
	} else {
		return false;
	}
}


/*******************************************************************************
 * Popup ValidationCheck
 ******************************************************************************/
function popupValidation() {
	var elements = $(".w2ui-box1 .w2ui-msg-body .w2ui-field input:visible, textarea:visible");
	var checkValidation = true;
	var emptyFieldLabels = null;
	var emailFieldLabels = null;
	if (elements.length > 0) {
		emptyFieldLabels = new Array();
		emailFieldLabels = new Array();
		elements.each(function(obj) {
					var tagType = $(this).get(0).tagName;
					var inputType = $(this).attr('type');
					var elementName = $(this).attr('name');
					var elementValue = $(this).val().trim();
					var label = "";
					// 빈값일 경우
					if (elementName && !elementValue) {
						if (tagType.toLowerCase() == "input") {
							if (inputType == 'text') {
								if ($(this).attr('name') == "subnetStaticFrom" || $(this).attr('name') == "subnetStaticTo") {
									label = "Static Ip";
									$(this).css({"border-color" : "red"}).parent().parent().find(".isMessage").text(label + "를(을) 입력하세요").css({"color" : "red"})
								
								} else if ($(this).attr('name') == "subnetReservedFrom" || $(this).attr('name') == "subnetReservedTo") {
									label = "Reserved Range";
									$(this).css({ "border-color" : "red"}).parent().parent().find(".isMessage").text(label + "를(을) 입력하세요").css({"color" : "red"});
								
								} else if ($(this).attr('name') == "releasePathFile" || $(this).attr('name') == "releaseUrl") {
									label = "release path";
									$(this).css({ "border-color" : "red" }).parent().find(".isMessage").text( label + "를(을) 입력하세요").css({"color" : "red"});
								
								} else if ($(this).attr('name') == "deploymentName") {
									checkValidation = true;
								
								} else if ($(this).attr('name') == "stemcellPathVersion" || $(this).attr('name') == "stemcellPathUrl" || $(this).attr('name') == "stemcellPathFileName"){
									if($(".w2ui-msg-body input[name=stemcellPathFileName]").val() != "" ||  $(".w2ui-msg-body input[name='stemcellPathUrl']").val() != "" 
											|| $(".w2ui-msg-body input[name='stemcellPathVersion']").val() != ""){
										checkValidation = true;
									}else{
										label = "시스템 다운 유형";
										$(this).css({"border-color" : "red"});
									}
								} else if( $(this).attr('name') == 'appSshFingerprint'){
									checkValidation = true;
								} else if( $(this).attr('name') == 'cadvisorDriverIp' || $(this).attr('name') == 'cadvisorDriverPort' 
									|| $(this).attr('name') == 'ingestorIp' || $(this).attr('name') == 'ingestorPort') {
									if( $("input[name=paastaMonitoring]:checkbox:checked").length == 0){
										checkValidation = true;
									}else{
										label = $(this).parent().parent().find("label").text();
										$(this).css({"border-color" : "red"}).parent().find(".isMessage").text(label + "를(을) 입력하세요").css({"color" : "red"});
									}
								}else {
									if ($(this).attr('name') != "deploymentName" ||  $(this).attr('name') != "stemcellPathVersion" || 
											$(this).attr('name') != "stemcellPathFileName" || $(this).attr('name') != "stemcellPathUrl") {
										label = $(this).parent().parent().find("label").text();
										$(this).css({"border-color" : "red"}).parent().find(".isMessage").text(label + "를(을) 입력하세요").css({"color" : "red"});
									}
									
								}
							}else if (inputType == 'password') {
								label = $(this).parent().parent().find("label").text();
								$(this).css({"border-color" : "red"}).parent().find(".isMessage").text(label + "를(을) 입력하세요").css({"color" : "red"});

							} else if (inputType == 'url') {
								label = $(this).parent().parent().find("label").text();
								$(this).css({"border-color" : "red"}).parent().find(".isMessage").text(label + "를(을) 입력하세요").css({"color" : "red"});

							} else if (inputType == 'list') {
								if ($(this).attr('name') == "keyPathList") {// 예외
									label = "Private Key File";
								} else {
									label = $(this).parent().parent().find("label").text();
								}
							} else if (inputType == 'file') {
								if ($(this).attr('name') == "keyPathFile") {
									label = "Private Key File";
									$(this).css({"border-color" : "red"});
								} else if ($(this).attr('name') == "releasePathFile") {
									label = "release path File";
									$(this).css({"border-color" : "red"});
								}
							}
							if ($(this).attr('name') != "deploymentName" &&  $(this).attr('name') != "stemcellPathVersion" && 
									$(this).attr('name') != "stemcellPathFileName" && $(this).attr('name') != "stemcellPathUrl") {
								$(this).css({"border-color" : "red"});
							}
						} else if (tagType.toLowerCase() == "textarea") {
							label = $(this).parent().parent().find('label').text();
							$(this).css({
								"border-color" : "red"
							});
						}
						if (label)
							emptyFieldLabels.push(label);
					}

					// 값이 있을 경우
					else if (elementName && elementValue) {
						if (tagType.toLowerCase() == "input") {
							if (inputType.toLowerCase() == "text") {
								if ($(this).attr('name') == 'email') {
									if (emailValidation($(this).val())) {
										$(this).css({"border" : "1px solid #bbb"}).parent().find(".isMessage").text("");
									} else {
										emailFieldLabels.push($(this));
									}
								} else if ($(this).attr('name') == 'userId' || $(this).attr('name') == 'roleName'
										|| $(this).attr('name') == 'userName' || $(this).attr('name') == 'codeName'
										|| $(this).attr('name') == 'subCodeName' || $(this).attr('name') == 'subCodeNameKR') {
									
									if (!specialCharacterValidation($(this).val())) {
										$(this).css({"border" : "1px solid #bbb"}).parent().find(".isMessage").text("");
										if ($(this).attr('name') == 'roleName') {
											if( textLengthValidation($(this).val()) ) {
												$(this).css({"border" : "1px solid #bbb"}).parent().find(".isMessage").text("");
											} else {
												label = $(this).parent().parent().find("label").text();
												$(this).css({"border-color" : "red"}).parent().find(".isMessage").text("2~15자 사이로 입력 해주세요.").css({"color" : "red"});
												k.push(label);
											}
										}
									} else {
										label = $(this).parent().parent().find("label").text();
										$(this).css({"border-color" : "red"}).parent().find(".isMessage").text(label + " 특수 문자 입력 불가").css({"color" : "red"});
										emptyFieldLabels.push(label);
									}
								} else if($(this).attr('name') == "stemcellPathFileName"){
									if($(".w2ui-msg-body input[name=stemcellPathFileName]").val().indexOf($(".w2ui-msg-body input[name='iaasList']").val().toLowerCase())==-1){
										label = "IaaS 유형";
										emptyFieldLabels.push(label);
									}
									if($(".w2ui-msg-body input[name=stemcellPathFileName]").val().indexOf($(".w2ui-msg-body input[name='osList']").val().toLowerCase())==-1){
										label = "OS 유형";
										emptyFieldLabels.push(label);
									}
									var osVersion = "";
									if($(".w2ui-msg-body input[name='osVersionList']").val() == "7.X"){
										osVersion = "7";
									}else{
										osVersion = $(".w2ui-msg-body input[name='osVersionList']").val();
									}
									if($(".w2ui-msg-body input[name=stemcellPathFileName]").val().indexOf(osVersion.toLowerCase())==-1){
										label = "OS 버전";
										emptyFieldLabels.push(label);
									}
								} else if($(this).attr('name') == "stemcellPathUrl"){
									if($(".w2ui-msg-body input[name=stemcellPathUrl]").val().indexOf($(".w2ui-msg-body input[name='iaasList']").val().toLowerCase())==-1){
										label = "IaaS 유형";
										emptyFieldLabels.push(label);
									}
									if($(".w2ui-msg-body input[name=stemcellPathUrl]").val().indexOf($(".w2ui-msg-body input[name='osList']").val().toLowerCase())==-1){
										label = "OS 유형";
										emptyFieldLabels.push(label);
									}
									var osVersion = "";
									if($(".w2ui-msg-body input[name='osVersionList']").val() == "7.X"){
										osVersion = "7";
									}else{
										osVersion = $(".w2ui-msg-body input[name='osVersionList']").val();
									}
									if($(".w2ui-msg-body input[name=stemcellPathUrl]").val().indexOf(osVersion.toLowerCase())==-1){
										label = "OS 버전";
										emptyFieldLabels.push(label);
									}
								} else if($(this).attr('name') == "cadvisorDriverPort" || $(this).attr('name') == 'ingestorPort'){
									if(!onlyNumberPort(elementValue)){
										label = "포트 번호";
										$(this).css({"border-color" : "red"}).parent().find(".isMessage").text(label + "를(을) 확인하세요").css({"color" : "red"});
										emptyFieldLabels.push(label);
									}else{
										$(this).css({"border" : "1px solid #bbb"}).parent().find(".isMessage").text("");
									}
								} else if($(this).attr('name') == "cadvisorDriverIp" || $(this).attr('name') == 'ingestorIp'){
									if(!validateIP(elementValue)){
										label = "서버 IP"; 
										$(this).css({"border-color" : "red"}).parent().find(".isMessage").text(label + "를(을) 확인하세요").css({"color" : "red"});
										emptyFieldLabels.push(label);
									}else {
										$(this).css({"border" : "1px solid #bbb"}).parent().find(".isMessage").text("");
									}
								}
								
							} else if (inputType.toLowerCase() == "list") {
								$(this).css({"border" : "1px solid #bbb"});// .parent().find(".isMessage").text("");
							} else if (inputType.toLowerCase() == "url") {
								if (validateIP(elementValue)) {
									$(this).css({"border" : "1px solid #bbb"}).parent().find(".isMessage").text("");
								} else {
									label = $(this).parent().parent().find("label").text();
									$(this).css({"border-color" : "red"}).parent().find(".isMessage").text(label + "를(을) 입력하세요").css({"color" : "red"});
									emptyFieldLabels.push(label);
								}
							} else if (inputType == 'file') {
								if ($(this).attr('name') == "keyPathFile") {
									if ($.inArray($(this).val().split('.').pop().toLowerCase(), [ 'pem' ]) == -1) {
										emptyFieldLabels.push("Empty Key File");
									}
								}
							}
						} else if (tagType.toLowerCase() == "textarea") {
							$(this).css({"border" : "1px solid #bbb"});// .parent().find(".isMessage").text("");
						}
					}
				});
	}
	if (emptyFieldLabels.length > 0) {
		checkValidation = false;
		if (emptyFieldLabels[0] == "Empty Key File") {
			w2alert("KeyPath File은 .pem 파일만 등록 가능합니다.", $(".w2ui-msg-title b")
					.text());
			return;
		} else if (emptyFieldLabels[0] == "아이디") {
			w2alert("아이디 특수문자 사용 불가", $(".w2ui-msg-title b").text());
			return;
		}
		w2alert(emptyFieldLabels[0] + "을(를) 확인하세요.", $(".w2ui-msg-title b").text());

	} else if (emailFieldLabels.length > 0) {
		checkValidation = false;
		if (emailFieldLabels[0].attr('name') == 'email') {
			w2alert("이메일을(를) 확인하세요.", $(".w2ui-msg-title b").text());
			emailFieldLabels[0].css({"border-color" : "red"}).parent().find(".isMessage").text("이메일을(를) 입력하세요").css({"color" : "red"});
			return;
		}
	} else {
		checkValidation = true;
	}

	return checkValidation;
}

/*******************************************************************************
 * Popup NetworkValidation for vSphere
 ******************************************************************************/
function popupNetworkValidation() {
	var elements = $(".w2ui-box1 .w2ui-msg-body .w2ui-field input:visible, textarea:visible");
	var checkValidation = true;
	var emptyFieldLabels = null;
	var check = "";
	if (elements.length > 0) {
		emptyFieldLabels = new Array();
		
		elements.each(function(obj) {
					var tagType = $(this).get(0).tagName;
					var inputType = $(this).attr('type');
					var elementName = $(this).attr('name');
					var elementValue = $(this).val().trim();
					var label = "";
					if (elementName && !elementValue) {
						if (tagType.toLowerCase() == "input") {
							if (inputType == 'text') {
								if ($(this).attr('name') == "publicStaticIp") {
									check = "true";
									return;
								} else if (check == "true") {
									if ($(this).attr('name') == "publicSubnetId"
											|| $(this).attr('name') == "publicSubnetRange"
											|| $(this).attr('name') == "publicSubnetGateway"
											|| $(this).attr('name') == "publicSubnetDns"
											|| $(this).attr('name') == "publicNtp") {
										return true;
									}
								} else {
									// 일반
									check = "";
									label = $(this).parent().parent().find("label").text();
									$(this).css({"border-color" : "red"});
									$(this).parent().parent()
											.find(".isMessage").text(label + "를(을) 입력하세요").css({"color" : "red"});

									$(this).on("change", function() {
										if (!checkEmpty($(this).val())) {
											$(this).css({"border-color" : "grey"}).parent().parent().find(".isMessage").text("OK").show().fadeOut();
										}
									});
								}
							}
						}
						if (label) emptyFieldLabels.push(label);
					}

					// 값이 있을 경우
					else if (elementName && elementValue) {
						if (tagType.toLowerCase() == "input") {
							if (inputType.toLowerCase() == "text") {
								if ($(this).attr('name') == "publicSubnetId"
										|| $(this).attr('name') == "publicSubnetRange"
										|| $(this).attr('name') == "publicSubnetGateway"
										|| $(this).attr('name') == "publicSubnetDns"
										|| $(this).attr('name') == "publicNtp") {

									if ($(".w2ui-msg-body input[name='publicStaticIp']").val().trim() == "" 
											|| $(".w2ui-msg-body input[name='publicStaticIp']").val() == null) {
										
										// 디렉터공인 ip
										var label = "디렉터 공인 IP ";
										$(".w2ui-msg-body input[name='publicStaticIp']").css({"border-color" : "red"}).parent().find(".isMessage")
												.text(label + "를(을) 입력하세요").css({"color" : "red"});
										emptyFieldLabels.push(label);
									}
								} else {
									$(this).css({"border-color" : "1px solid #bbb"}).parent().find(".isMessage").text("");
								}
							}
						}
					}
				});
	}
	if (emptyFieldLabels.length > 0) {
		checkValidation = false;
		w2alert(emptyFieldLabels[0] + "을(를) 확인하세요.", $(".w2ui-msg-title b").text());
	} else {
		checkValidation = true;
	}
	return checkValidation;
}
