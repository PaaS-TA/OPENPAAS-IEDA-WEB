<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript">

$(function() {

  	$('#config_directorGrid').w2grid({
		name: 'config_directorGrid',
		header: '<b>설치관리자 목록</b>',
 		method: 'GET',
		show: {	lineNumbers: true,
				//selectColumn: true,
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
			,{field: 'defaultYn', caption: '기본관리자 여부', size: '10%'}
			,{field: 'directorName', caption: '관리자 이름', size: '15%'}
			,{field: 'userId', caption: '관리자 계정', size: '15%'}
			,{field: 'directorUrl', caption: '관리자 URL', size: '35%',
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
 	getDefaultDirector("<c:url value='/directors/default'/>");
 	doSearch();
 	
 	$("#setDefaultDirector").click(function() {
 		var selected = w2ui['config_directorGrid'].getSelection();
 		alert(selected);
 		
 		var result = confirm("기본관리자로 설정하시겠습니까?");
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
		<span class="boardBtn"><a href="#" class="btn btn-primary" style="width:130px"><span>설정 추가</span></a></span>
		<span class="boardBtn"><a href="#" class="btn btn-danger" style="width:130px"><span>설정 삭제</span></a></span>
		<!-- //Btn -->
	    </div>
	</div>
	
	<div id="hMargin"/>
	
	<div id="config_directorGrid" style="width:100%; height:500px"/>	
	
</div>