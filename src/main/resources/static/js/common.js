
function getDefaultDirector(url) {
	var isOk = true;
	
	jQuery.ajax({
		type: "get",
		url: url,
		async : false,
		error: function(request, status, error) {
			console.log(request.responseText);
			var errorResult = JSON.parse(request.responseText);
			w2alert(errorResult.message, "알림");
			isOk = false;
		},
		success: function(data) {
			if ( data == null || data == "" )
				isOk = false;
			else {
				$('#directorName').text(data.directorName + '(' + data.directorCpi + ')' );
				$('#userId').text(data.userId);
				
				diretorUrl = "https://" + data.directorUrl + ":" + data.directorPort;
				
				$('#directorUrl').text(diretorUrl);
				$('#directorUuid').text(data.directorUuid);
			}
		}

	});
	
	return isOk;
}

function setCommonCode(url, id) {
	jQuery.ajax({
		type: "get",
		url: url,
		async : false,
		error: function(xhr, status) {
			ajaxErrorMsg(xhr);
		},
		success: function(data) {
			
			var $object = jQuery("#"+id);
			
			var optionString  = "";
			for ( i=0; i < data.length; i++ ) {
				if ( i == 0 ) {
					optionString += "<option selected='selected' ";
					optionString += "value='" + data[i].codeIdx + "' >";
					optionString += data[i].codeName;
					optionString += "</option>\n";
				}
				else {
					optionString += "<option ";
					optionString += "value='" + data[i].codeIdx + "'>";
					optionString += data[i].codeName;
					optionString += "</option>\n";
				}
			}
			//alert(optionString);
			$object.html(optionString);
		}
	});
}

//빈값 체크
function checkEmpty(value){
	return (value == null || value == "") ? true: false;
}

//URL 체크
function validateIP(input){
	if (/^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(input))  
		return true;
	else
		return false;
}

//Popup ValidationCheck
function popupValidation(){
	var elements = $(".w2ui-box1 .w2ui-msg-body .w2ui-field input:visible, textarea:visible");
	var checkValidation = true;
	var emptyFieldLabels = null;
	
	if( elements.length > 0){
		emptyFieldLabels = new Array();
		elements.each(function(obj){
			var tagType = $(this).get(0).tagName;
			var inputType = $(this).attr('type');
			var elementName = $(this).attr('name');
			var elementValue = $(this).val();
			var label = "";
			//빈값일 경우
			if( elementName && !elementValue ){
				
				if( tagType.toLowerCase() == "input" ){
					
					if( inputType == 'text'){
						//예외
						if($(this).attr('name') == "subnetStaticFrom" || $(this).attr('name') == "subnetStaticTo"){
							label = "Static Ip";
							$(this).css({"border-color":"red"})
								.parent().parent().find(".isMessage").text(label + "를(을) 입력하세요").css({"color":"red"})
								.on("change", function(){
									if( !checkEmpty($(this).val()) ){
										$(this).css({"border-color":"grey"})
										.parent().parent().find(".isMessage").text("OK").show().fadeOut();
									}
								});
						}
						else if($(this).attr('name') == "subnetReservedFrom" || $(this).attr('name') == "subnetReservedTo" ){
							label = "Reserved Range";
							$(this).css({"border-color":"red"})
								.parent().parent().find(".isMessage").text(label + "를(을) 입력하세요").css({"color":"red"});
						}
						else{
							//일반
							label = $(this).parent().parent().find("label").text();
							$(this).css({"border-color":"red"})
								.parent().find(".isMessage").text(label + "를(을) 입력하세요").css({"color":"red"});
						}
					}
					else if( inputType == 'url'){
						label = $(this).parent().parent().find("label").text();
						$(this).css({"border-color":"red"}).parent().find(".isMessage").text(label + "를(을) 입력하세요").css({"color":"red"});
					}
					else if( inputType == 'list'){
						if($(this).attr('name') == "keyPathList"){//예외
							label = "Private Key File";
						}
						else{
							label = $(this).parent().parent().find("label").text();
						}
					}
					else if( inputType == 'file'){
						if($(this).attr('name') == "keyPathFile"){
							label = "Private Key File";
							$(this).css({"border-color":"red"});
						}
					}
					
					$(this).css({"border-color":"red"});
				}
				else if( tagType.toLowerCase() == "textarea" ){
					label = $(this).parent().parent().find('label').text();
					$(this).css({"border-color":"red"}); //.parent().find(".isMessage").text(label + "를(을) 입력하세요").css({"color":"red"});
				}
				
				if(label) emptyFieldLabels.push(label);
			}
			//값이 있을 경우
			else if( elementName && elementValue  ){
				if( tagType.toLowerCase() == "input" ){
					if( inputType.toLowerCase() == "text"){
						$(this).css({"border":"1px solid #bbb"}).parent().find(".isMessage").text("");
					}
					else if( inputType.toLowerCase() == "list"){
						$(this).css({"border":"1px solid #bbb"});//.parent().find(".isMessage").text("");
					}
					else if( inputType.toLowerCase() == "url"){
						if(validateIP(elementValue)){
							$(this).css({"border":"1px solid #bbb"}).parent().find(".isMessage").text("");
						}
						else{
							label = $(this).parent().parent().find("label").text();
							$(this).css({"border-color":"red"}).parent().find(".isMessage").text(label + "를(을) 입력하세요").css({"color":"red"});
							emptyFieldLabels.push(label);
						}
					}
					else if( inputType == 'file'){
						console("keyPathFilekeyPathFile");
						if($(this).attr('name') == "keyPathFile"){
							console("PPPPPPPPPPPPPPPPPPPP");
							if($.inArray($(this).val().split('.').pop().toLowerCase(), ['pem']) == -1) {
								console("YYYYYYYY");
								emptyFieldLabels.push("Empty Key File");
							}
						}
					}
				}
				else if( tagType.toLowerCase() == "textarea" ){
					$(this).css({"border":"1px solid #bbb"});//.parent().find(".isMessage").text("");
				}
			}
		});
	}

	if(emptyFieldLabels.length > 0){
		checkValidation = false;
		if( emptyFieldLabels[0] == "Empty Key File"){
			w2alert("KeyPath File은 .pem 파일만 등록 가능합니다.", $(".w2ui-msg-title b").text());
			return;
		}
		
		w2alert(emptyFieldLabels[0] + "을(를) 필드값을 확인하세요.", $(".w2ui-msg-title b").text() );
	}
	else{
		checkValidation = true;
	}
	
	return checkValidation;
}


//Deploy Log 
function getDeployLogMsg(service, iaas, id){
	var getParam = {
			  service	: service
			, iaas		: iaas
			, id		: id
	}
	
	$.ajax({
		type : "POST",
		url : "/common/getDeployLogMsg",
		contentType : "application/json",
		data : JSON.stringify(getParam),
		success : function(data, status) {
			if(!checkEmpty(data)){
				deployLogMsgPopup(service, iaas, data);
			}
			else{
				w2alert("배포 로그가 존재 하지 않습니다.", service + " DEPLOY LOG");
			}
		},
		error : function(request, status, error) {
			var errorResult = JSON.parse(request.responseText);
			w2alert(errorResult.message, service + " DEPLOY LOG");
		}
	});	
}

function deployLogMsgPopup(service, iaas, msg){
	var body = '<textarea id="deployLogMsg" style="margin-left:2%;width:95%;height:93%;overflow-y:visible;resize:none;background-color: #FFF; margin:2%" readonly="readonly"></textarea>';
	
	w2popup.open({
		width : 800,
		height : 700,
		title : "<b>"+service.toUpperCase()+"Deploy Log</b>",
		body  : body,
		buttons : '<button class="btn" style="float: right; padding-right: 15%;" onclick="w2popup.close();">닫기</button>',
		showMax : true,
		onOpen : function(event){
			event.onComplete = function(){
				$("#deployLogMsg").text(msg);
			}
		}
	});	
}

//한글입력 방지
//var popupBodySelector = document.getElement('w2ui-msg-body');
