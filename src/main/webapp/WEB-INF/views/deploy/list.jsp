<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript">

$(function() {
	
 	// 기본 설치 관리자 정보 조회
 	getDefaultDirector("<c:url value='/directors/default'/>");	
	
	$('#deploy_deploymentsGrid').w2grid({
		name: 'deploy_deploymentsGrid',
		show: {selectColumn: true, footer: true},
		multiSelect: false,
		style: 'text-align:center',
		columns:[
		     {field: 'recid', caption: 'ID', hidden: true}
			,{field: 'name', caption: '배포 이름', size: '20%', sortable: true}
			,{field: 'releases', caption: '릴리즈 목록', size: '40%', sortable: true}
			,{field: 'stemcells', caption: '스템셀 목록', size: '40%', sortable: true}
		],
		onAdd: function(event) {
			w2alert('add');
		},
		onEdit: function(event) {
			w2alert('edit');
		},
		onDelete: function(event) {
			w2alert('delete');
		},
        records: [
                  { recid: 1, name: 'John', releases: 'doe', stemcells: 'jdoe@gmail.com'},
                  { recid: 2, name: 'Stuart', releases: 'Motzart', stemcells: 'jdoe@gmail.com'},
                  { recid: 3, name: 'Jin', releases: 'Franson', stemcells: 'jdoe@gmail.com'},
                  { recid: 4, name: 'Susan', releases: 'Ottie', stemcells: 'jdoe@gmail.com'},
                  { recid: 5, name: 'Kelly', releases: 'Silver', stemcells: 'jdoe@gmail.com'},
                  { recid: 6, name: 'Francis', releases: 'Gatos', stemcells: 'jdoe@gmail.com'},
                  { recid: 7, name: 'Mark', releases: 'Welldo', stemcells: 'jdoe@gmail.com'},
                  { recid: 8, name: 'Thomas', releases: 'Bahh', stemcells: 'jdoe@gmail.com'}
              ]		
	});


});

// 서비스 설치 팝업
function deployPopup() {
	var inData = "";
	$.ajax({ 
		type:"GET", 
		url : "<c:url value='/deploy/deployPopup'/>",
		data : inData,
		success:function(res) { 
			w2popup.open({
 				title: '<b>서비스 설치</b>',
				body: res,
				height : 560,
				width : 700,
				showMax : true,
				modal : true,
				style: 'padding: 5px 5px 5px 5px; border: 0px; background-color: #FFFFFFF;',
				onClose : function(event){
					clearPopupPage();
					try {
						//doSearch();  
					}catch(e){};
				},
				buttons : '<a href="" class="btn btn-primary"><span>닫기</span></a>'
			});
		}, 
		error : function(xhr, status) {
			ajaxErrorMsg(xhr);
		}
	});	

}

//다른페이지 이동시 호출
function clearMainPage() {
	$().w2destroy('deploy_deploymentsGrid');
}

//화면 리사이즈시 호출
$( window ).resize(function() {
	setLayoutContainerHeight();
});

</script>

<div id="main">
	<div class="page_site">서비스 설치관리 > <strong>서비스 설치</strong></div>
	
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
	
<!-- 	<div id="hMargin"/> -->
	
	<!-- 설치 목록 -->
	<div class="pdt20"> 
		<div class="title fl">설치 목록</div>
		<div class="fr"> 
		<!-- Btn -->
		<span class="boardBtn"><a href="javascript:deployPopup();" class="btn btn-primary" style="width:130px"><span>서비스 설치</span></a></span>
		<span class="boardBtn"><a href="#" class="btn btn-primary" style="width:130px"><span>서비스 재설치</span></a></span>
		<span class="boardBtn"><a href="#" class="btn btn-danger" style="width:130px"><span>서비스 설치 삭제</span></a></span>
		<!-- //Btn -->
	    </div>
	</div> 	
	<div id="deploy_deploymentsGrid" style="width:100%; height:500px"/>	
	
</div>