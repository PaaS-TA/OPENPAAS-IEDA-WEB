/**
 * 공통 confirm 팝업
 * ex) commonRegistPop( '타이틀', '<b>컨텐츠</b>', '확인', 'checkSetting("파라메터")');
 */
function commonCongirmPop( popTitle, popContents, buttonText, buttonEvent ){
	if( !buttonEvent ){	buttonEvent = 'w2popup.close();';}
	
	w2popup.open({
		title: '<b>' + popTitle + '</b>',
		body : popContents,
		buttons : '<button class="btn" onclick="javascript:' + buttonEvent + '">' + buttonText + '</button> '
	});
}

/**
 * 공통 Regist 팝업
 * ex) commonRegistPop( '타이틀', '<b>컨텐츠</b>', '저장', 'regSetting("파라메터")', '닫기', 'clseSetting("파라메터")' );
 */
function commonRegistPop( popTitle, popContents, regBtnText, regEvent, closeBtnText, closeEvent ){
	if( !regEvent ){	regEvent = 'w2popup.close();';}
	if( !closeEvent ){ closeEvent = 'w2popup.close();';}
	
	w2popup.open({
		title: '<b>' + popTitle + '</b>',
		body : popContents,
		buttons   : '<button class="btn" onclick="javascript:' + regEvent + '">' + regBtnText + '</button> '+
					'<button class="btn" onclick="javascript:' + closeEvent+'">' + closeBtnText + '</button> '
	});
}
