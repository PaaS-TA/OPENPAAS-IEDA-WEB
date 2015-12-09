
function getDefaultDirector(url) {
	var isOk = true;
	
	jQuery.ajax({
		type: "get",
		url: url,
		async : false,
		error: function(request, status, error) {
			var errorResult = JSON.parse(request.responseText);
			w2alert(errorResult.message, "알림");
			isOk = false;
		},
		success: function(data) {
			if ( data == null || data == "" )
				isOk = false;
			else {
				$('#directorName').text(data.directorName);
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
