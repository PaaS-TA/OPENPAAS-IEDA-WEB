<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
	.ui-progressbar {
	  position: relative;
	}
	.progress-label {
	  position: absolute;
	  left: 50%;
	  top: 4px;	  
	  font-weight: bold;
	  text-shadow: 1px 1px 0 #58ACFA;
	}
	.ui-progressbar {
		height: 1em;
		text-align: left;
		overflow: hidden;
	}
	.ui-progressbar .ui-progressbar-value {
		margin: -1px;
		height: 50%;
	}
</style>
<script type="text/javascript" src="<c:url value='/js/sockjs-0.3.4.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/stomp.js'/>"></script>
<script type="text/javascript">

$(function() {
 	$('#config_opStemcellsGrid').w2grid({
		name: 'config_opStemcellsGrid',
		show: {selectColumn: true, footer: true},

		multiSelect: false,
		method: 'GET',
		style: 'text-align:center',
		columns:[
			 {field: 'recid', caption: '운영체계', hidden: true}
			,{field: 'os', caption: '운영체계', size: '10%'}
			,{field: 'osVersion', caption: '버전', size: '10%'}
			,{field: 'iaas', caption: 'IaaS', size: '10%', sortable: true}
			,{field: 'stemcellVersion', caption: '스템셀버전', size: '10%'}
			,{field: 'stemcellFileName', caption: '파일명', size: '40%', style: 'text-align:left'}
			,{field: 'isExisted', caption: '다운로드 여부', size: '10%',
				render: function(record) {
					if ( record.isExisted == 'Y'){
						return '<div class="btn btn-success btn-xs" style="width:70px;">' + '완료 ' + '</div>';
					}
					else{
						return '<div id="isExisted_'+record.recid+'"><div class="progress-label"></div></div>';
					}	
				}
			}
		],
		onClick: function(event) {
			var grid = this;
			event.onComplete = function() {
				var sel = grid.getSelection();
				if ( sel == null || sel == "") {
					$('#doDownload').attr('disabled', true);
					$('#doDelete').attr('disabled', true);
					return;
				}
				
				var record = grid.get(sel);
				if ( record.isExisted == 'Y' ) {
					// 다운로드 버튼 Disable
					$('#doDownload').attr('disabled', true);
					// 삭제 버튼 Enable
					$('#doDelete').attr('disabled', false);
				}
				else {
					// 다운로드 버튼 Enable
					$('#doDownload').attr('disabled', false);
					// 삭제 버튼 Disable
					$('#doDelete').attr('disabled', true);
				}
			}
		}
		
	});

 	//  화면 초기화에 필요한 데이터 요청
 	initView();
 	
 	// 목록조회
 	$("#doSearch").click(function(){
 		doSearch();
    });
 	
 	//  스템셀 다운로드
 	$("#doDownload").click(function(){
    	doDownload();
    });
 	
 	//	스템셀 삭제
 	$("#doDelete").click(function(){
 		doDelete();
    });
 	
 	// OS구분 코드 변경 시  OS버전 구분코드 조회
 	$("#os").change(function() {
 		// OS버전 코드  (OS구분과 연관된 하위코드로 선택된 OS구분 코드의 값)
 		setCommonCode('<c:url value="/codes/child/"/>' + $("#os option:selected").val(), 'osVersion');
 	});

});

function initView() {
	//  기본 설치관리자 정보 조회
	getDefaultDirector();

	// OS구분 코드 (코드값 : '2')
	setCommonCode('<c:url value="/codes/child/"/>'+'2', 'os');
	
	// OS버전 코드  (OS구분과 연관된 하위코드로 선택된 OS구분 코드의 값)
	setCommonCode('<c:url value="/codes/child/"/>' + $("#os option:selected").val(), 'osVersion');
	
	// IaaS 코드 (코드값 : '1')
	setCommonCode('<c:url value="/codes/child/"/>' + '1', 'iaas');
	
	// 스템셀 목록 조회
	doSearch();
	
	// 다운로드 & 삭제버튼 Disable
	$('#doDownload').attr('disabled', true);
	$('#doDelete').attr('disabled', true);
}

// OS버전 Option 목록 조회
function changeOS() {
	// OS버전 코드  (OS구분과 연관된 하위코드로 선택된 OS구분 코드의 값)
	setCommonCode('<c:url value="/codes/child/"/>' + $("#os option:selected").val(), 'osVersion');
}

//스템셀 목록 조회
function doSearch() {
	var requestParam = "?os=" + $("#os option:selected").text();
	requestParam += "&osVersion=" + $("#osVersion option:selected").text();
	requestParam += "&iaas=" + $("#iaas option:selected").text();

	w2ui['config_opStemcellsGrid'].load("<c:url value='/publicStemcells'/>"
			+ requestParam);
}

// 스템셀 다운로드
function doDownload() {
	var selected = w2ui['config_opStemcellsGrid'].getSelection();

	var record = w2ui['config_opStemcellsGrid'].get(selected);

	var requestParameter = {
			recid : record.recid,
			key : record.key,
			fileName : record.stemcellFileName,
			fileSize : record.size
		};

	$.ajax({
		method : 'post',
		type : "json",
		url : "/downloadPublicStemcell",
		contentType : "application/json",
		data : JSON.stringify(requestParameter),
		beforeSend : function(){
			//progressbar 생성
			progressGrow(requestParameter, requestParameter.recid);
		},
		success : function(data, status) {
			//w2alert("status : " + status, "StemcellFile Download Complete!");
		},
		error : function(e) {
			w2alert("오류가 발생하였습니다.");
		}
	});
}

//ProgressBar Create
function progressGrow(param, recid){
	var progressbar = $("#isExisted_" + param.recid);
	var progressLabel = $( ".progress-label" );
	
	progressbar.progressbar({
	      value: false,
	      change: function() {
	        progressLabel.text( progressbar.progressbar( "value" ) + "%" );
	      },
	      complete: function() {
	        progressLabel.text( "Complete!" );
	      }
	});
	
	getDowloadStatus(param, recid);
}

//다운로드 상태 확인
function getDowloadStatus(param, recid){
	if(param.recid == recid){
		$.ajax({
			method : 'post',
			type : "json",
			//url : "/downloadPublicStemcell",
			url : "/stemcellDownloadStatus",
			contentType : "application/json",
			data : JSON.stringify(param),
			success : function(data, status) {
				console.log("!!!!! STATUS ::: "+data.status);
				//if( data.status != 100){
					//getDowloadStatus(param);
					progressPress(data);
				//}
			},
			error : function(e, e1) {
				w2alert("오류가 발생하였습니다.");
			}
		});
	}
}

//ProgressBar Status Update
function progressPress(param) {
	var progressbar = $("#isExisted_" + param.recid);
	var progressLabel = $( ".progress-label" );
	console.log("###progress Status ::: " + param.status );
    var val = param.status;//progressbar.progressbar( "value" ) || 0 ;

    //progressbar.progressbar( "value", val );
    $("#isExisted_" + param.recid + " .progress-label" ).text(val+"%");
    if ( val < 100 ) {
      	setTimeout( getDowloadStatus(param), 4000 );
    }
    else if(val == 100) {
    	progressbar.parent().html('<div class="btn btn-success btn-xs" style="width:70px;">' + '완료 ' + '</div>');
    }
}

// 스템셀 삭제
function doDelete() {
	var selected = w2ui['config_opStemcellsGrid'].getSelection();
	var record = w2ui['config_opStemcellsGrid'].get(selected);
	
	var requestParameter = { stemcellFileName: record.stemcellFileName };
	
	w2confirm( { msg : '선택된 스템셀 ' + record.stemcellFileName + '을 삭제하시겠습니까?'
		, title : '스템셀 삭제'
		, yes_text:'확인'
		, no_text:'취소'
		})
		.yes(function() {
			$.ajax({
				method : 'delete',
				type : "json",
				url : "/deletePublicStemcell",
				contentType : "application/json",
				data : JSON.stringify(requestParameter),
				success : function(data, status) {
					record.isExisted = 'N';
					w2ui['config_opStemcellsGrid'].reload();
					$('#doDelete').attr('disabled', true);
					w2ui['config_opStemcellsGrid'].selectNone();
					w2alert("삭제 처리가 완료되었습니다.", "스템셀  삭제");
				},
				error : function(e) {
					w2alert("오류가 발생하였습니다.");
				}
			});
		})
		.no(function() {
			// do nothing
		});
}

//다른페이지 이동시 호출
function clearMainPage() {
	$().w2destroy('config_opStemcellsGrid');
}

//화면 리사이즈시 호출
$(window).resize(function() {
	setLayoutContainerHeight();
});
</script>

<div id="main">
	<div class="page_site">설치관리자 환경설정 > <strong>스템셀 관리</strong></div>
	
	<!-- OpenPaaS 스템셀 목록-->
	<div class="title">스템셀 목록</div>
	
 	<div class="search_box" align="center">
		<span class="search_li">OS</span>&nbsp;&nbsp;&nbsp;
		<!-- OS구분 -->
		<select name="select" id="os" class="select" style="width:120px">
		</select>
		<span class="search_li">OS버전</span>&nbsp;&nbsp;&nbsp;
		<!-- OS버전구분 -->
		<select name="select" id="osVersion" class="select" style="width:120px">
		</select>
		<span class="search_li">IaaS</span>&nbsp;&nbsp;&nbsp;
		<!-- IaaS구분 -->
		<select name="select" id="iaas" class="select" style="width:120px">
		</select>
		&nbsp;&nbsp;&nbsp;
		
		<button type="button" class="btn btn-info !important" style="width:100px" id="doSearch">조회</button>
		
		<button type="button" class="btn btn-primary" style="width:100px" id="doDownload">다운로드</button>
		<button type="button" class="btn btn-danger" style="width:100px" id="doDelete">삭제</button>

	</div>
	
	<!-- 그리드 영역 -->
	<div id="config_opStemcellsGrid" style="width:100%; height:500px"></div>	
	
</div>