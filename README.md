# Table of Contents
1. [문서 개요](#1)
	* [목적](#2)
	* [범위](#3)
	* [참고자료](#4)
2. [플랫폼 설치 자동화 메뉴얼](#5)
	* [플랫폼 설치 자동화 화면 설명](#6)
		* [로그인](#7)
		* [환경설정 및 관리 -> 설치관리자 설정](#8)
		* [환경설정 및 관리 -> 스템셀 관리](#9)
		* [환경설정 및 관리 -> 릴리즈 관리](#10)
		* [플랫폼 설치 자동화 관리 -> 코드 관리](#11)
		* [플랫폼 설치 자동화 관리 -> 권한 관리](#12)
		* [플랫폼 설치 자동화 관리 -> 사용자 관리](#13)
		* [플랫폼 설치 -> BOOTSTRAP 설치](#14)
		* [플랫폼 설치 -> BOSH 설치](#15)
		* [플랫폼 설치 -> CF 설치](#16)
		* [플랫폼 설치 -> DEIGO 설치](#17)
		* [플랫폼 설치 -> CF & DEIGO 통합 설차](#18)
		* [플랫폼 설치 -> 서비스팩 설치](#19)
		* [정보조회 -> 스템셀 업로드](#20)
		* [정보조회 -> 릴리즈 업로드](#21)
		* [정보조회 -> 배포 정보](#22)
		* [정보조회 -> Task 정보](#23)
		* [정보조회 -> VM 관리](#24)
		* [정보조회 -> Property 관리](#25)
		* [정보조회 -> 스냅샷 관리](#26)
		* [정보조회 -> Manifest 관리](#27)
3. [플랫폼 설치 자동화 활용](#28)
		* [플랫폼 설치 자동화 파일 관리](#28)
		* [스템셀 과 릴리즈](#29)
	* [BOOTSTRAP 설치 하기](#30)
		* [스템셀 다운로드](#31)
		* [릴리즈 다운로드](#32)
		* [BOOTSTRAP 설치](#33)
		* [설치 관리자 설정](#34)
	* [BOSH 설치 하기](#35)
		* [스템셀 업로드](#36)
		* [릴리즈 업로드](#37)
		
 		
#<div id='1'/>1.  문서 개요 

##<div id='2'/>1.1.  목적

본 문서는 플랫폼 설치 자동화 시스템의 사용 절차에 대해 기술하였다.

##<div id='3'/>1.2.  범위

본 문서에서는 Linux 환경(Ubuntu 14.04)을 기준으로 플랫폼 설치 자동화를
사용하는 방법에 대해 작성되었다.

##<div id='4'/>1.3.  참고자료

본 문서는 Cloud Foundry의 Document를 참고로 작성하였다.<br>
BOSH Document: **[http://bosh.io](http://bosh.io)**<br>
CF & Diego Document:
[http://docs.cloudfoundry.org/](http://docs.cloudfoundry.org/)


#<div id='5'/>2.  플랫폼 설치 자동화 매뉴얼

플랫폼 설치 관리자는 설치관리자 등록정보 관리 및 기본 설치관리자를
지정하는 환경 설정하는 부분과 기본 설치관리자로부터 필요한 정보를
조회/업로드를 수행하는 부분 그리고 설치관리자를 이용해서 PaaS-TA를
설치하는 부분으로 구성되어 있다.

<table>
  <tr>
    <th>분류</th>
    <th>메뉴</th>
    <th>설명</th>
  </tr>
  <tr>
    <td rowspan="3">환경설정 및 관리</td>
    <td>설치관리자 설정</td>
    <td>BOSH 디렉터(설치관리자) 정보를 관리하는 화면</td>
  </tr>
  <tr>
    <td>스템셀 관리</td>
    <td>BOSH Public 스템셀 등록/삭제하는 화면</td>
  </tr>
  <tr>
    <td>릴리즈 관리</td>
    <td>릴리즈 등록/삭제하는 화면</td>
  </tr>
  <tr>
    <td rowspan="3">플랫폼 설치 자동화 관리</td>
    <td>코드 관리</td>
    <td>공통 코드를 등록/수정/삭제 등 관리하는 화면</td>
  </tr>
  <tr>
    <td>권한 관리</td>
    <td>권한 정보를 등록/수정/삭제 등 관리하는 화면</td>
  </tr>
  <tr>
    <td>사용자 관리</td>
    <td>사용자 정보를 등록/수정/삭제 등 관리하는 화면</td>
  </tr>
  <tr>
    <td rowspan="6">플랫폼 설치</td>
    <td>BOOTSTRAP 설치</td>
    <td>BOOTSTRAP를 설치하는 화면</td>
  </tr>
  <tr>
    <td>BOSH 설치</td>
    <td>BOSH를 설치하는 화면</td>
  </tr>
  <tr>
    <td>CF 설치</td>
    <td>CF를 설치하는 화면</td>
  </tr>
  <tr>
    <td>Diego 설치</td>
    <td>Diego를 설치하는 화면</td>
  </tr>
  <tr>
    <td>CF 및 Diego 설치</td>
    <td>CF 및 Diego를 설치하는 화면</td>
  </tr>
  <tr>
    <td>서비스팩 설치</td>
    <td>서비스팩을 설치하는 화면</td>
  </tr>
  <tr>
    <td rowspan="8">배포 정보 조회 및 관리</td>
    <td>스템셀 업로드</td>
    <td>기본 설치관리자에 스템셀 업로드 및 삭제하는 화면</td>
  </tr>
  <tr>
    <td>릴리즈 업로드</td>
    <td>기본 설치관리자에 릴리즈 업로드 및 삭제하는 화면</td>
  </tr>
  <tr>
    <td>배포 정보</td>
    <td>기본 설치관리자에 배포된 배포목록을 확인하는 화면</td>
  </tr>
  <tr>
    <td>Task 정보</td>
    <td>기본 설치관리자가 수행한 Task 정보를 확인하는 화면</td>
  </tr>
  <tr>
    <td>VM 관리</td>
    <td>기본 설치관리자에 배포된 배포의 VM을 관리하는 화면</td>
  </tr>
  <tr>
    <td>Property 관리</td>
    <td>기본 설치관리자에 배포된 배포의 Property를 관리하는 화면</td>
  </tr>
  <tr>
    <td>스냅샷 관리</td>
    <td>기본 설치관리자에 배포된 배포의 스냅샷을 관리하는 화면</td>
  </tr>
  <tr>
    <td>Manifest 관리</td>
    <td>서비스팩 설치에 필요한 Manifest를 관리하는 화면</td>
  </tr>
  
</table>

##<div id='6'/>2.1.  플랫폼 설치 자동화 화면 설명

본 장에서는 플랫폼 설치 자동화를 구성하는 20개의 메뉴 및 로그인에 대한
설명을 기술한다.

###<div id='7'/>2.1.1. ***로그인***

“로그인” 화면은 플랫폼 설치 자동화 관리자가 로그인하는 화면으로 사용자
정보 생성 후 최초 로그인을 했을 경우 비밀번호 변경 화면을 통해 비밀번호
정보를 변경한다.


#####1.  로그인

-   플랫폼 설치 관리자는 로그인 첫 아이디와 비밀번호를(admin/admin) 입력 후 로그인 버튼을 클릭한다.

![PaaSTa_Platform_Use_Guide_Image01]

#####2.  비밀번호 변경

-   비밀번호 변경 화면을 통해 비밀번호를 수정할 수 있다.

![PaaSTa_Platform_Use_Guide_Image02]

#####3.  플랫폼 설치 자동화 접속

![PaaSTa_Platform_Use_Guide_Image03]

###<div id='8'/>2.1.2. ***환경설정 및 관리 -> 설치관리자 설정***

“설치관리자 설정” 화면은 BOSH의 디렉터 정보 관리 및 설정하는 화면으로
BOOTSTRAP(Microbosh) 또는 BOSH의 디렉터 정보를 관리하는 화면이다.

※ 설치 관리자에서 설정을 추가하기 위해서는 먼저 BOOTSTRAP을 설치
해야한다.

![PaaSTa_Platform_Use_Guide_Image04]

#####1.  설정 추가

-   설치 관리자 정보를 등록하는 기능으로 BOSH 디렉터의 IP, 포트번호,계정, 비밀번호 입력 후 확인 버튼을 클릭한다.

![PaaSTa_Platform_Use_Guide_Image05]

#####2.  설정 수정

-   설치 관리자 등록 목록에서 선택된 설치 관리자 정보를 수정하는 기능으로 계정과 비밀번호를 수정할 수 있다.

#####3.  설정 삭제

-   설치 관리자 등록 목록에서 선택된 설치 관리자 정보를 삭제하는 기능

#####4.  기본 설치 관리자로 설정

-   설치 관리자 등록 목록에서 선택된 설치 관리자를 기본 설치 관리자로 설정하는 기능

#####5.  설치 관리자 목록

-   등록된 설치 관리자 목록을 보여준다.

#####6.  설치 관리자

-   기본 설치 관리자로 설정된 설치 관리자 정보를 보여준다.


###<div id='9'/>2.1.3.  ***환경설정 및 관리 -> 스템셀 관리***

다운로드한 스템셀 목록을 조회하고, 필요한 스템셀을 등록 및 삭제 할 수
있는 화면이다.

![PaaSTa_Platform_Use_Guide_Image06]

#####1.  등록

-   등록할 스템셀 정보를 입력하여 스템셀 정보를 저장하고 스템셀 파일을 플랫폼 설치 자동화의 스템셀 디렉토리(\~/.bosh\_plugin/stemcell)로 다운로드를 수행한다.

#####2.  삭제

-   플랫폼 설치 자동화에 다운로드 된 스템셀을 삭제하는 기능을 수행한다.

###<div id='10'/>2.1.4.  환경설정 및 관리 -> 릴리즈 관리

다운로드한 릴리즈 목록을 조회하고, 필요한 릴리즈를 등록/삭제 할 수 있는
화면이다.

![PaaSTa_Platform_Use_Guide_Image07]

#####1.  등록

-   등록할 릴리즈 정보를 입력하여 릴리즈 정보를 저장 하고 플랫폼 설치 자동화의 릴리즈 디렉토리(\~/.bosh\_plugin/release)로 다운로드를 수행한다.

#####2.  삭제

-   플랫폼 설치 자동화에 등록된 릴리즈를 삭제하는 기능을 수행한다.


###<div id='11'/>2.1.5.  ***플랫폼 설치 자동화 관리 -> 코드 관리***

코드 관리 코드 그룹, 코드 목록을 조회하고, 필요한 코드 그룹을 등록,
수정, 삭제 할 수 있고 해당 코드 그룹의 하위 코드를 등록, 수정, 삭제 할
수 있는 화면이다.

![PaaSTa_Platform_Use_Guide_Image08]

#####1.  코드 그룹 조회

-   플랫폼 설치 자동화에 등록 된 상위 공통 코드를 조회 한다.

#####2.  코드 조회

-   선택 한 코드 그룹에 해당 하는 플랫폼 설치 자동화에 등록 된 하위 공통 코드를 조회 한다.

#####3.  코드 그룹 등록

-   코드 그룹 정보를 등록 하는 기능으로 코드 그룹 명, 코드 그룹 값, 설명을 입력 하고 확인 버튼을 클릭한다.

#####4.  코드 그룹 수정

-   코드 그룹 목록에서 선택 된 코드 그룹 정보를 수정하는 기능으로 코드 그룹 명과 설명을 수정 할 수 있다.

#####5.  코드 그룹 삭제

-   코드 그룹 목록에서 선택 된 코드 그룹을 삭제 하는 기능

#####6.  코드 등록

-   코드를 등록 하는 기능으로 하위 그룹, 코드명(영문), 코드명(한글), 코드 값, 설명을 입력 하고 확인 버튼을 클릭 한다.

#####7.  코드 수정

-   코드 목록에서 선택 된 코드 정보를 수정하는 기능으로 하위 그룹, 코드명(영문), 코드명(한글), 설명을 수정 할 수 있다.

#####8.  코드 삭제

-   코드 목록에서 선택 된 코드를 삭제 하는 기능


###<div id='12'/>2.1.6.  ***플랫폼 설치 자동화 관리 -> 권한 관리***

권한 관리 권한 그룹, 권한 목록을 조회하고, 필요한 권한 그룹을 등록,
수정, 삭제 할 수 있고 해당 권한 그룹의 상세 권한을 등록할 수 있는
화면이다.

![PaaSTa_Platform_Use_Guide_Image09]

#####1.  권한 그룹 조회

-   플랫폼 설치 자동화에 등록 된 권한 그룹을 조회 한다.

#####2.  상세 권한 조회

-   선택 한 권한 그룹에 해당 하는 플랫폼 설치 자동화에 등록 된 상세 권한을 조회 한다.

#####3.  권한 그룹 등록

-   권한 그룹 정보를 등록 하는 기능으로 권한 그룹 명, 설명을 입력 하고 확인 버튼을 클릭 한다.

#####4.  권한 그룹 수정

-   권한 그룹 목록에서 선택 된 권한 그룹 정보를 수정하는 기능으로 권한 그룹 명과 설명을 수정 할 수 있다.

#####5.  권한 그룹 삭제

-   권한 그룹 목록에서 선택 된 권한 그룹을 삭제 하는 기능

#####6.  상세 권한 등록

-   권한 그룹 목록에서 선택 된 권한 그룹에 해당하는 상세 권한 목록을 등록 하는 기능으로 권한 설정 허용/거부를 입력 후 확인 버튼을 클릭 한다.



###<div id='13'/>2.1.7.  ***플랫폼 설치 자동화 관리 -> 사용자 관리***

사용자 관리 사용자 목록을 조회하고 사용자를 등록, 수정, 삭제 할 수 있는
화면이다.

![PaaSTa_Platform_Use_Guide_Image10]

#####1.  사용자 조회

-   플랫폼 설치 자동화에 등록 된 사용자 목록을 조회 한다.

#####2.  사용자 등록

-   사용자 정보를 등록 하는 기능으로 사용자 아이디, 이름, Email, 권한 그룹을 입력하고 확인 버튼을 클릭 한다.

#####3.  사용자 수정

-   사용자 목록에서 선택 된 사용자 정보를 수정 하는 기능으로 비밀번호, 이름, Email, 권한을 수정 할 수 있다.

#####4.  사용자 삭제

-   사용자 목록에서 선택 된 사용자 정보를 삭제 하는 기능이다.

###<div id='14'/>2.1.8.  ***플랫폼 설치 -> BOOTSTRAP 설치***

클라우드 환경에 BOOTSTRAP(Microbosh)를 설치하는 화면으로 상단의 버튼을
이용해서 설치/수정/삭제 기능을 제공한다.

![PaaSTa_Platform_Use_Guide_Image11]

#####1.  설치

-   BOOTSTRAP 설치할 수 있는 기능을 수행한다.

#####2.  수정

-   BOOTSTRAP 목록에서 선택된 BOOTSTRAP 정보 확인 및 수정 후 재설치하는 기능을 수행한다.

#####3.  삭제

-   BOOTSTRAP 목록에서 선택된 BOOTSRAP을 삭제하는 기능을 수행한다.


###<div id='15'/>2.1.9.  ***플랫폼 설치 -> BOSH 설치***

클라우드 환경에 BOSH를 설치하는 화면으로 상단의 버튼을 이용해서
설치/수정/삭제 기능을 제공한다.

![PaaSTa_Platform_Use_Guide_Image12]

#####1.  설치

-   BOSH를 설치할 수 있는 기능을 수행한다.

#####2.  수정

-   BOSH 목록에서 선택된 BOSH 정보 확인 및 수정 후 재설치하는 기능을 수행한다.

#####3.  삭제

-   BOSH 목록에서 선택된 BOSH을 삭제하는 기능을 수행한다.

#####4.  설치 관리자

-   기본 설치 관리자로 설정된 설치 관리자 정보를 보여준다.

#####5.  BOSH 목록

-   BOSH 설치 목록을 조회한다.


###<div id='16'/>2.1.10. ***플랫폼 설치 -> CF 설치***

설치 관리자(BOOTSTRAP 또는 BOSH)를 이용해서 PaaS-TA Controller인 CF를
설치하는 화면으로 상단의 버튼을 이용해서 설치/수정/삭제 기능을 제공한다.

![PaaSTa_Platform_Use_Guide_Image13]

#####1.  설치

-   CF를 설치할 수 있는 기능을 수행한다.

#####2.  수정

-   CF 목록에서 선택된 CF 정보 확인 및 수정 후 재설치하는 기능을 수행한다.

#####3.  삭제

-   CF 목록에서 선택된 CF를 삭제하는 기능을 수행한다.

#####4.  설치 관리자

-   기본 설치 관리자로 설정된 설치 관리자 정보를 보여준다.

#####5.  CF 목록

-   CF 설치 목록을 조회한다.


###<div id='17'/>2.1.11. ***플랫폼 설치 -> DIEGO 설치***

설치 관리자(BOOTSTRAP 또는 BOSH)를 이용해서 PaaS-TA Container인 DIEGO를
설치하는 화면으로 상단의 버튼을 이용해서 설치/수정/삭제 기능을 제공한다.

![PaaSTa_Platform_Use_Guide_Image14]

#####1.  설치

-   DIEGO를 설치할 수 있는 기능을 수행한다.

#####2.  수정

-   DIEGO 목록에서 선택된 DIEGO 정보 확인 및 수정 후 재설치하는 기능을 수행한다.

#####3.  삭제

-   DIEGO 목록에서 선택된 DIEGO를 삭제하는 기능을 수행한다.

#####4.  설치 관리자

-   기본 설치 관리자로 설정된 설치 관리자 정보를 보여준다.

#####5.  DIEGO 목록

-   DIEGO 설치 목록을 조회한다.


###<div id='18'/>2.1.12. ***플랫폼 설치 -> CF & DIEGO 통합 설치***

설치 관리자(BOOTSTRAP 또는 BOSH)를 이용해서 PaaS-TA Controller인 CF와
PaaS-TA Container인 Diego를 통합 설치하는 화면으로 상단의 버튼을
이용해서 설치/수정/삭제 기능을 제공한다.

![PaaSTa_Platform_Use_Guide_Image15]

#####1.  설치

-   CF 및 Diego를 통합 설치할 수 있는 기능을 수행한다.

#####2.  수정

-   CF & Diego 통합 설치 목록에서 선택된 CF & Diego 정보 확인 및 수정 후 재설치하는 기능을 수행한다.

#####3.  삭제

-   CF & Diego 통합 설치 목록에서 선택된 CF & Diego를 삭제하는 기능을 수행한다.

#####4.  설치 관리자

-   기본 설치 관리자로 설정된 설치 관리자 정보를 보여준다.

#####5.  CF & Diego 통합 설치 목록

-   CF & Diego 통합 설치 목록을 조회한다.

###<div id='19'/>2.1.13. ***플랫폼 설치 -> 서비스팩 설치***

설치 관리자(BOOTSTRAP 또는 BOSH)를 이용해서 PaaS-TA Controller인 CF에
서비스팩을 설치하는 화면으로 상단의 버튼을 이용해서 설치/삭제 기능을
제공한다.

![PaaSTa_Platform_Use_Guide_Image16]

#####1.  설치

-   CF에 서비스팩을 설치할 수 있는 기능을 수행한다.

#####2.  삭제

-   서비스팩 목록에서 선택된 서비스팩을 삭제하는 기능을 수행한다.

#####3.  설치 관리자

-   기본 설치 관리자로 설정된 설치 관리자 정보를 보여준다.

#####4.  서비스팩 목록

-   서비스팩 설치 목록을 조회한다.

###<div id='20'/>2.1.14. ***정보조회 -> 스템셀 업로드***

설치 관리자로부터 스템셀 정보를 조회/업로드/삭제할 수 있는 기능을
제공하는 화면이다.

![PaaSTa_Platform_Use_Guide_Image17]

#####1.  설치 관리자

-   기본 설치 관리자로 설정된 설치 관리자 정보를 보여준다.

#####2.  스템셀 삭제

-   설치 관리자에 업로드 된 스템셀을 삭제하는 기능을 제공한다.

#####.  업로드 된 스템셀 목록

-   설치 관리자에 업로드 된 스템셀 목록을 보여준다.

#####4.  스템셀 업로드

-   설치 관리자로 스템셀을 업로드할 수 있는 기능을 제공한다.

#####5.  다운로드 된 스템셀 목록

-   플랫폼 설치 자동화에 다운로드 된 스템셀을 목록을 보여준다.


###<div id='21'/>2.1.15. ***정보조회 -> 릴리즈 업로드***

설치 관리자로부터 릴리즈 정보를 조회/업로드/삭제할 수 있는 기능을
제공하는 화면이다.

![PaaSTa_Platform_Use_Guide_Image18]

#####1.  설치 관리자

-   기본 설치 관리자로 설정된 설치 관리자 정보를 보여준다.

#####2.  업로드 된 릴리즈 목록

-   설치 관리자에 업로드 된 업로드 된 릴리즈 목록을 보여준다.

#####3.  다운로드 된 릴리즈 목록

-   플랫폼 설치 자동화에 다운로드 된 릴리즈 목록을 보여준다.

#####4.  릴리즈 삭제

-   설치 관리자에 업로드 된 업로드 된 릴리즈를 삭제 하는 기능을 제공
    한다.

#####5.  릴리즈 업로드

-   설치 관리자로 릴리즈 업로드할 수 있는 기능을 제공한다.


###<div id='22'/>2.1.16. ***정보조회 -> 배포정보***

설치 관리자로부터 배포된 배포 정보를 조회하는 기능을 제공하는 화면이다.

![PaaSTa_Platform_Use_Guide_Image19]

#####1.  설치 관리자

-   기본 설치 관리자로 설정된 설치 관리자 정보를 보여준다.

#####2.  설치 목록

-   설치 관리자를 이용해서 배포된 배포 목록 정보를 보여준다.

###<div id='23'/>2.1.17. ***정보조회 -> Task 정보***

설치 관리자가 수행한 Task 작업들에 대한 목록 조회 및 상세 로그 정보를
확인하는 기능을 제공하는 화면이다.

![PaaSTa_Platform_Use_Guide_Image20]

#####1.  설치 관리자

-   기본 설치 관리자로 설정된 설치 관리자 정보를 보여준다.

#####2.  Task 실행 이력

-   설치 관리자가 수행한 Task의 작업 목록을 보여준다.

#####3.  디버그 로그

-   선택된 Task 작업에 대한 디버그 로그를 보여준다.

#####4.  이벤트 로그

-   선택된 Task 작업에 대한 이벤트 로그를 보여준다.


###<div id='24'/>2.1.18. ***정보조회 -> VM 관리***

VM 관리 기본 설치 관리자를 통해 배포한 VM을 조회, 관리 하는 기능을 제공
하는 화면 이다.

![PaaSTa_Platform_Use_Guide_Image21]

#####1.  설치 관리자

-   기본 설치 관리자로 설정된 설치 관리자 정보를 보여준다.

#####2.  배포 명 목록

-   기본 설치 관리자가 배포한 VM의 배포명 목록을 보여준다.

#####3.  VM 목록

-   VM의 상세 명칭, 상태 값, Type, AZ, IPs, Load, Cpu 타입 등을 보여 준다.

#####4.  배포 명 조회

-   배포명 목록에서 선택 된 배포명을 통해 해당 배포명을 갖는 VM의 상세 목록을 조회하는 기능

#####5.  로그 다운로드

-   VM 목록에서 선택 된 VM의 Agent 로그, Job 로그를 선택 하여 다운로드 할 수 있는 기능

#####6.  Job 시작

-   VM 목록에서 선택 된 중지 상태 중인 VM을 시작하는 기능

#####7.  Job 중지

-   VM 목록에서 선택 된 시작 상태 중인 VM을 중지하는 기능

#####8.  Job 재시작

-   VM 목록에서 선택 된 VM을 재시작 하는 기능

#####9.  Job 재생성

-   VM 목록에서 선택 된 VM을 재생성 하는 기능


###<div id='25'/>2.1.19. 정보조회 -> Property 관리

Property 관리 설치 관리자가 배포한 VM 정보의 Property를 조회, 생성,
수정, 삭제, 상세보기 할 수 있는 기능을 제공하는 화면

![PaaSTa_Platform_Use_Guide_Image22]

#####1.  설치 관리자

-   기본 설치 관리자로 설정된 설치 관리자 정보를 보여준다.

#####2.  배포 명 목록

-   기본 설치 관리자가 배포한 VM의 배포명 목록을 보여준다.

#####3.  배포 명 조회 기능

-   배포명 목록에서 선택 된 배포명을 통해 해당 배포명을 갖는 Property의 상세 목록을 조회하는 기능

#####4.  Property 목록 조회

-   배포 명을 통해 조회 된 해당 배포 정보의 Property 명, Property 값을 보여 준다.

#####5.  Property 생성

-   Property 정보를 저장하는 기능으로 Property 명, Property 값을 입력 하고 저장 버튼을 클릭 한다.

#####6.  Property 수정

-   선택 된 Property 정보를 수정하는 기능으로 Property 값을 수정하고 수정 버튼을 클릭 한다.

#####7.  Property 삭제

-   선택 된 Property 정보를 삭제하는 기능

#####8.  Property 상세 보기

-   선택 된 Property를 상세 보기 할 수 있는 기능

###<div id='26'/>***2.1.20. 정보조회 -> 스냅샷 관리***

스냅샷 관리 설치 관리자가 배포한 VM 정보의 스냅샷을 조회, 삭제, 전체
삭제 할 수 있는 기능을 제공하는 화면

![PaaSTa_Platform_Use_Guide_Image23]

#####1.  설치 관리자

-   기본 설치 관리자로 설정된 설치 관리자 정보를 보여준다.

#####2.  배포 명 목록

-   기본 설치 관리자가 배포한 VM의 배포명 목록을 보여준다.

#####3.  배포 명 조회 기능

-   배포명 목록에서 선택 된 배포명을 통해 해당 배포명을 갖는 스냅샷의 상세 목록을 조회하는 기능

#####4.  스냅샷 목록 조회

-   배포 명을 통해 조회 된 해당 배포 정보의 JobName, Uuid, SnapshotCid 등 스냅샷 상세 정보를 보여 준다

#####5.  스냅샷 삭제

-   선택 된 스냅샷을 삭제 하는 기능

#####6.  스냅샷 전체 삭제

-   조회 된 스냅샷을 전체 삭제 하는 기능.


###<div id='27'/>***2.1.21. 정보조회 -> Manifest 관리***

Manifest 관리 서비스팩 설치에 필요한 Manifest를 플랫폼 설치 자동화에
업로드, 수정, 삭제, 업로드 된 Manifest 파일을 로컬에 다운로드 할 수 있는
기능을 제공 하는 화면

![PaaSTa_Platform_Use_Guide_Image24]

#####1.  Manifest 목록

-   플랫폼 설치 자동화에 업로드 된 Manifest 파일 목록을 보여준다.

#####2.  Manifest 업로드

-   로컬에 있는 Manifest 파일을 플랫폼 설치 자동화의 Manifest 관리 디렉토리(\~/.bosh\_plugin/deployment/manifest)로 업로드를 실행하는 기능.<br>
IaaS, 설명, 파일을 입력 후 업로드 버튼을 클릭 한다.

#####3.  Manifest 다운로드

-   선택 한 업로드 된 Manifest 파일을 로컬 다운로드 폴더 경로로 다운로드 하는 기능

#####4.  Manifest 수정

-   선택 한 업로드 된 Manifest 파일을 상세 보기하여 수정 할 수 있는 기능

#####5.  Manifest 삭제

-   선택 한 업로드 된 Manifest 파일을 삭제 하는 기능


[PaaSTa_Platform_Use_Guide_Image01]:/images/PaaSTa_Platform_Use_Guide/login.png
[PaaSTa_Platform_Use_Guide_Image02]:/images/PaaSTa_Platform_Use_Guide/passwordChange.png
[PaaSTa_Platform_Use_Guide_Image03]:/images/PaaSTa_Platform_Use_Guide/Dashboard.png
[PaaSTa_Platform_Use_Guide_Image04]:/images/PaaSTa_Platform_Use_Guide/DirectorConfig.png
[PaaSTa_Platform_Use_Guide_Image05]:/images/PaaSTa_Platform_Use_Guide/DirectorConfigAdd.png
[PaaSTa_Platform_Use_Guide_Image06]:/images/PaaSTa_Platform_Use_Guide/StemcellConfig.png
[PaaSTa_Platform_Use_Guide_Image07]:/images/PaaSTa_Platform_Use_Guide/ReleaseConfig.png
[PaaSTa_Platform_Use_Guide_Image08]:/images/PaaSTa_Platform_Use_Guide/CodeManagement.png
[PaaSTa_Platform_Use_Guide_Image09]:/images/PaaSTa_Platform_Use_Guide/AuthManagement.png
[PaaSTa_Platform_Use_Guide_Image10]:/images/PaaSTa_Platform_Use_Guide/UseManagement.png
[PaaSTa_Platform_Use_Guide_Image11]:/images/PaaSTa_Platform_Use_Guide/BootStrapInstall.png
[PaaSTa_Platform_Use_Guide_Image12]:/images/PaaSTa_Platform_Use_Guide/BoshInstall.png
[PaaSTa_Platform_Use_Guide_Image13]:/images/PaaSTa_Platform_Use_Guide/CfInstall.png
[PaaSTa_Platform_Use_Guide_Image14]:/images/PaaSTa_Platform_Use_Guide/DiegoInstall.png
[PaaSTa_Platform_Use_Guide_Image15]:/images/PaaSTa_Platform_Use_Guide/Cf_DiegoInstall.png
[PaaSTa_Platform_Use_Guide_Image16]:/images/PaaSTa_Platform_Use_Guide/ServicePackInstall.png
[PaaSTa_Platform_Use_Guide_Image17]:/images/PaaSTa_Platform_Use_Guide/StemcellUpload.png
[PaaSTa_Platform_Use_Guide_Image18]:/images/PaaSTa_Platform_Use_Guide/ReleaseUpload.png
[PaaSTa_Platform_Use_Guide_Image19]:/images/PaaSTa_Platform_Use_Guide/DeploymentInfo.png
[PaaSTa_Platform_Use_Guide_Image20]:/images/PaaSTa_Platform_Use_Guide/TaskInfo.png
[PaaSTa_Platform_Use_Guide_Image21]:/images/PaaSTa_Platform_Use_Guide/VmInfo.png
[PaaSTa_Platform_Use_Guide_Image22]:/images/PaaSTa_Platform_Use_Guide/PropertyInfo.png
[PaaSTa_Platform_Use_Guide_Image23]:/images/PaaSTa_Platform_Use_Guide/SnapshotInfo.png
[PaaSTa_Platform_Use_Guide_Image24]:/images/PaaSTa_Platform_Use_Guide/ManifestInfo.png
