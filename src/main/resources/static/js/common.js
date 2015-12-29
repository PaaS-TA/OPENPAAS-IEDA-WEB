
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

//팝업 INPUT Validation
function popupValidation(){
	var emptyFields = new Array();
	var checkValidation = true;
	var textInputs 	= $(".w2ui-msg-body :input[type=text]:visible");
	var listInputs 	= $(".w2ui-msg-body :input[type=list]:visible");
	var urlInputs 	= $(".w2ui-msg-body :input[type=url]:visible");
	
	if( textInputs.length > 0 ){
		textInputs.each(function(obj){
			if( checkEmpty( $(this).val()) &&  $(this).attr('name') ){
				console.log("#### : NAME :" + $(this).attr('name'));
				emptyFields.push($(this ).attr("name"));
				var label = $(this).parent().parent().find("label").text();
				$(this).css({"border-color":"red"}).parent().find(".isMessage").text(label + "를(을) 입력하세요").css({"color":"red"});
			}
			else{
				$(this).css({"border":"1px solid #bbb"}).parent().find(".isMessage").text("");
			}
		});
	}
	if( listInputs.length > 0 ){
		listInputs.each(function(obj){
			//console.log("#### : NAME :" +$(this).attr('name'));
			if( checkEmpty( $(this).val()) &&  $(this).attr('name') ){
				console.log("#### : NAME :" + $(this).attr('name'));
				emptyFields.push($(this).attr("name"));
				$(this).css({"border-color":"red"});
			}
			else{
				$(this).css({"border":"1px solid #bbb"});
			}
		});
	}
	
	if( urlInputs.length > 0 ){
		urlInputs.each(function(obj){
			//console.log("#### : NAME :" +$(this).attr('name'));
			if( checkEmpty( $(this).val()) &&  $(this).attr('name') ){
				console.log("#### : NAME :" + $(this).attr('name'));
				emptyFields.push($(this ).attr("name"));
				var label = $(this).parent().parent().find("label").text();
				$(this).css({"border-color":"red"}).parent().find(".isMessage").text(label + "를(을) 입력하세요").css({"color":"red"});
			}
			else{
				$(this).css({"border":"1px solid #bbb"}).parent().find(".isMessage").text("");
			}
		});
	}
	
	console.log("EMPTY : " + emptyFields.length);
	//ALert 메세지
	if(emptyFields.length != 0 ){
		emptyFields.forEach(function(obj){
			console.log("## Input Name :: " + obj);
		});
		
		checkValidation = false;
		var label = $(".w2ui-msg-body input[name="+emptyFields[0]+"]").parent().parent().find("label").text();
		w2alert(label + "을(를) 입력하세요.");
	}
	else{
		checkValidation = true;
	}
	
	console.log(":: valid ::" + checkValidation);
	return checkValidation;
}

