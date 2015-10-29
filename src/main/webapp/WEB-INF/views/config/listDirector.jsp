<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript">

$(function() {

  	$('#config_directorGrid').w2grid({
		name: 'config_directorGrid',
		header: '<b>설치관리자 목록</b>',
 		method: 'GET',
 		multiSelect: false,
		show: {	lineNumbers: true,
				selectColumn: true,
				footer: true},
//         show: {
//             header         : true,
//             toolbar     : true,
//             footer        : true,
//             lineNumbers    : true,
//             selectColumn: true,
//             expandColumn: true
//         },	
		//multiSelect: false,
		style: 'text-align: center',
		columns:[
			 {field: 'iedaDirectorConfigSeq', caption: '레코드키', hidden: true}
			,{field: 'defaultYn', caption: '기본관리자 여부', size: '15%'}
			,{field: 'directorName', caption: '관리자 이름', size: '10%'}
			,{field: 'userId', caption: '관리자 계정', size: '15%'}
			,{field: 'directorUrl', caption: '관리자 URL', size: '30%',
				render: function(record) {
					return 'https://' + record.directorUrl + ':' + record.directorPort;
				}
			}
			,{field: 'directorUUID', caption: '관리자 UUID', size: '35%'}
		],
		onError: function(event) {
			this.unlock();
			gridErrorMsg(event);
		}
	});

 	// 기본 설치 관리자 정보 조회
 	//getDefaultDirector("<c:url value='/directors/default'/>");
 	doSearch();
 	
 	//기본관리자 설정 버튼
 	$("#setDefaultDirector").click(function() {
 		var selected = w2ui['config_directorGrid'].getSelection();
 		
 		if( selected.length == 0 ){
 			alert("선택된 정보가 없습니다.");
 			return;
 		}
 		else if ( selected.length > 1 ){
 			alert("기본관리자 설정은 하나만 선택 가능합니다.");
 			return;
 		}
 		
 		var result = confirm("기본관리자로 설정하시겠습니까?");
 		if ( result ) {
 			//alert("set");
 		} else {
 			//alert("do nothing")
 		}
 	});
 	
 	//설정 추가 버튼
 	$("#addSetting").click(function() {
 		w2popup.open({
			title 	: "<b>설치관리자 설정추가</b>",
			width 	: 600,
			height	: 250,
			body	: $("#regPopupDiv").html(),
			buttons : $("#regPopupBtnDiv").html()
		});
 	});
 	
 	//설정 삭제 버튼
 	$("#deleteSetting").click(function() {
 		var selected = w2ui['config_directorGrid'].getSelection();
 		
 		if( selected.length == 0 ){
 			alert("선택된 정보가 없습니다.");
 			return;
 		}
 		
 		var result = confirm("선택한 정보를 삭제하시겠습니까?");
 		if ( result ) {
 			//alert("set");
 		} else {
 			//alert("do nothing")
 		}
 	});
	
});

function doSearch() {
	w2ui['config_directorGrid'].load("<c:url value='/directors'/>");
}


//다른페이지 이동시 호출
function clearMainPage() {
	$().w2destroy('config_directorGrid');
}


function lock (msg) {
    w2popup.lock(msg, true);
    registSetting();
}


//등록처리
function registSetting(){
	$.ajax({
		type : "POST",
		url : "/directors",
		contentType : "application/json",
		//dataType: "json",
		async : true,
		data : JSON.stringify({
			userId : $("[name='userId']").val(),
			userPassword : $("[name='userPassword']").val(),
			directorUrl : $("[name='directorUrl']").val(),
			directorPort : parseInt($("[name='directorPort']").val())
		}),
		success : function(data, status) {
			// ajax가 성공할때 처리...
			w2popup.unlock();
			w2popup.close();
			doSearch();
		},
		error : function(e ) {
			// ajax가 실패할때 처리...
			alert(e);
			//alert("등록 중 오류가 발생하였습니다." );
			w2popup.unlock();
		}
	});
}
</script>

<div id="main">
	<div class="page_site">설치관리자 환경설정 > <strong>설치관리자 설정</strong></div>
	
	<!-- 설치 관리자 -->
	<div class="title">설치 관리자</div>
	
	<table class="tbl1" border="1" cellspacing="0">
	<tr>
		<th width="18%" class="th_fb">관리자 이름</th><td class="td_fb"><b id="directorName"/></td>
		<th width="18%" class="th_fb">관리자 계정</th><td class="td_fb"><b id="userId"/></td>
	</tr>
	<tr>
		<th width="18%" >관리자 URL</th><td><b id="directorUrl"/></td>
		<th width="18%" >관리자 UUID</th><td ><b id="directorUuid"/></td>
	</tr>
	</table>
	
	<!-- 설치관리자 목록-->
	<div class="pdt20">
		<div class="title fl">설치관리자 목록</div>
		<div class="fr"> 
		<!-- Btn -->
		<span class="boardBtn" id="setDefaultDirector"><a href="#" class="btn btn-primary" style="width:150px"><span>기본관리자로 설정</span></a></span>
		<span class="boardBtn" id="addSetting"><a href="#" class="btn btn-primary" style="width:130px"><span>설정 추가</span></a></span>
		<span class="boardBtn" id="deleteSetting"><a href="#" class="btn btn-danger" style="width:130px"><span>설정 삭제</span></a></span>
		<!-- //Btn -->
	    </div>
	</div>
	
	<div id="hMargin"/>
	
	<div id="config_directorGrid" style="width:100%; height:500px"/>	
</div>
<div id="regPopupDiv" hidden="true">
	<form id="addSettingForm" action="POST">
		<div class="w2ui-page page-0" style="width: 100%">
			<label>●&nbsp;설치관리자 설정 정보</label>
			<div class="w2ui-field">
				<label style="width:30%;text-align: left;padding-left: 20px;">관리자 계정명</label>
				<div style="width: 70%">
					<input name="userId" type="text" maxlength="100" style="width: 250px" required="required" value="test_id"/>
				</div>
			</div>
			<div class="w2ui-field">
				<label style="width:30%;text-align: left;padding-left: 20px;">관리자 계정 비밀번호</label>
				<div style="width: 70%;">
					<input name="userPassword" type="password" maxlength="100" style="width: 250px" required="required" value="testPass"/>
				</div>
			</div>
			<div class="w2ui-field">
				<label style="width:30%;text-align: left;padding-left: 20px;">디텍터 Url</label>
				<div style="width: 70%;">
					<input name="directorUrl" type="text" maxlength="100" style="width: 250px" required="required" value="52.21.37.184"/>
				</div>
			</div>
			<div class="w2ui-field">
				<label style="width:30%;text-align: left;padding-left: 20px;">디텍터 Port</label>
				<div style="width: 70%;">
					<input name="directorPort" type="number" maxlength="100" style="width: 250px" required="required" value="25555"/>
				</div>
			</div>
		</div>
	</form>	
</div>
<div id="regPopupBtnDiv" hidden="true">
	<button class="btn" onclick="lock( '등록 중입니다.', true);">설정</button>
	<button class="btn" onclick="w2popup.close();">취소</button>
</div>