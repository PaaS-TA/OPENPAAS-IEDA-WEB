
function getDefaultDirector(url) {
	
	jQuery.ajax({
		type: "get",
		url: url,
		async : false,
		error: function(xhr, status) {
			ajaxErrorMsg(xhr);
		},
		success: function(data) {
			if ( data == null || data == "" ) return;
			
			$('#directorName').text(data.directorName);
			$('#userId').text(data.userId);
			
			diretorUrl = "https://" + data.directorUrl + ":" + data.directorPort;
			
			$('#directorUrl').text(diretorUrl);
			$('#directorUuid').text(data.directorUuid);
		}

	});
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

/*function setCommonCode(url, id, value) {
	jQuery.ajax({
		type: "get",
		url: url,
		async : false,
		error: function(xhr, status) {
			ajaxErrorMsg(xhr);
		},
		success: function(data) {
			
			var $object = jQuery("#"+id);
			
			var optionString = $object.html() + '\n';
			for ( i=0; i < data.length; i++ ) {
				optionString += "<option ";
				optionString += "value='" + data[i].codeIdx + "'>";
				optionString += data[i].codeName;
				optionString += "</option>\n";
			}
			alert(optionString);
			$object.html(optionString);
		}
	});
}
*/