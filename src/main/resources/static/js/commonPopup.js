/**
 * 공통 confirm 팝업
 */
function commonCongirmPop( popTitle, popContents, buttonText, buttonEvent ){
	if( !buttonEvent || buttonEvent = ''  )	buttonEvent = 'w2popup.close';
	
	w2popup.open({
		title: '<b>' + popTitle + '</b>',
		body : popContents,
		buttons : '<button class="btn" onclick="javascript:' + buttonEvent + '();">' + buttonText + '</button> '
	});
}

/**
 * 공통 Regist 팝업
 */
function commonRegistPop( popTitle, popContents, regBtnText, regEvent, closeBtnText, closeEvent ){
	if( !regEvent || regEvent = ''  )	regEvent = 'w2popup.close';
	if( !closeEvent || closeEvent = '' ) closeEvent = 'w2popup.close';
	
	w2popup.open({
		title: '<b>' + popTitle + '</b>',
		body : popContents,
		buttons   : '<button class="btn" onclick="javascript:'+regEvent+'();">' + regBtnText + '</button> '+
					'<button class="btn" onclick="javascript:'+closeEvent+'();">' + closeBtnText + '</button> '
	});
}

/**
 * 공통 프로세스 팝업 
 */
function commonProcessPop(processArray){
	var buttons = '';
	w2popup.open({
		title: '<b>' + popTitle + '</b>',
		body : popContents,
		buttons   : '<button class="btn" onclick="javascript:'+regEvent+'();">' + regBtnText + '</button> '+
					'<button class="btn" onclick="javascript:'+closeEvent+'();">' + closeBtnText + '</button> '
	});
}
