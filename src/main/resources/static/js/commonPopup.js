/**
 * 공통 confirm 팝업
 */
function commonCongirmPop(popTitle, popContents, buttonText ){
	w2popup.open({
		title: '<b>' + popTitle + '</b>',
		body : popContents,
		buttons : '<a href="" class="btn btn-primary"><span>' + buttonText + '</span></a>'
	});
}

/**
 * 공통 confirm 팝업
 */
function commonRegistPop(popTitle, popContents, regBtnText, regEvent, closeBtnText, closeEvent ){
	
	if( !closeEvent ) closeEvent = 'w2popup.close';
	
	w2popup.open({
		title: '<b>' + popTitle + '</b>',
		body : popContents,
		  buttons   : '<button class="btn" onclick="javascript:'+regEvent+'();">' + regBtnText + '</button> '+
		  '<button class="btn" onclick="javascript:'+closeEvent+'();">' + closeBtnText + '</button> '
	});
}