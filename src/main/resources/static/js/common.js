
function getDefaultDirector(url) {
	
	jQuery.ajax({
		type: "get",
		url: url,
		async : false,
		error: function(xhr, status) {
			ajaxErrorMsg(xhr);
		},
		success: function(data) {
			$('#directorName').text(data.directorName);
			$('#userId').text(data.userId);
			
			diretorUrl = "https://" + data.directorUrl + ":" + data.directorPort;
			
			$('#directorUrl').text(diretorUrl);
			$('#directorUuid').text(data.directorUuid);
		}

	});
}