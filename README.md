## Table of Contents
1. [문서 개요](#1)
     * [1.1. 목적](#2)
     * [1.2. 범위](#3)
     * [1.3. 참고 자료](#4)
2. [Node.js API 미터링 개발가이드](#5)
     * [2.1. 개요](#6)
3. [개발](#7)
     * [3.1. Node.js Express애플리케이션 생성](#8)
     * [3.2. Node.js 샘플 애플리케이션](#9)
     * [3.3. 애플리케이션 환경설정](#10)
     * [3.4. VCAP_SERVICES 환경설정 정보](#11)
     * [3.5. Mysql 연동](#12)
     * [3.6. Cubrid 연동](#13)
     * [3.7. MongoDB 연동](#14)
     * [3.8. Redis 연동](#15)
     * [3.9. RabbitMQ연동](#16)
     * [3.10. GlusterFS 연동](#17)
4. [배포](#18)
     * [4.1. 개방형 플랫폼 로그인](#19)
     * [4.2. 서비스 생성](#20)
     * [4.3. 애플리케이션 배포](#21)
     * [4.4. 애플리케이션, 서비스 연결](#22)
     * [4.5. 애플리케이션 실행](#23)
5. [테스트](#24)



<div id='1'></div>
# 1. 문서 개요

<div id='2'></div>
### 1.1 목적

본 문서(node.js API 서비스 미터링 애플리케이션 개발 가이드)는 파스-타 플랫폼 프로젝트의 미터링 플러그인과 Node.js API 애플리케이션을 연동시켜 API 서비스를 미터링하는 방법에 대해 기술 하였다.

<div id='3'></div>
### 1.2 범위
본 문서의 범위는 파스-타 플랫폼 프로젝트의 Node.js API 서비스 애플리케이션 개발과 CF-Abacus 연동에 대한 내용으로 한정되어 있다.

<div id='4'></div>
### 1.3 참고자료
**<https://docs.cloudfoundry.org/devguide/>**  
**<https://docs.cloudfoundry.org/buildpacks/node/node-tips.html>**  
**<https://nodejs.org/>**  
**<http://expressjs.com/ko/>**  
**<https://github.com/cloudfoundry-incubator/cf-abacus>**  


<div id='5'></div>
# 2. Node.js API 미터링 개발가이드

### 2.1 개요

API 서비스 및 해당 API 서비스를 사용하는 애플리케이션을 Node.js 언어로 작성 한다. API 서비스를 사용하는 애플리케이션과 API 서비스를 바인딩하고 해당 애플리케이션에 바인딩된 환경정보(VCAP\_SERVICES)를 이용해 각 서비스별 접속정보를 획득하여 애플리케이션에 적용하여 API 서비스를 호출하는 애플리케이션을 작성 한다. 또한 API 서비스는 서비스 요청을 처리함과 동시에 API 사용 내역을 CF-ABACUS에 전송하는 애플리케이션을 작성 한다.


<imgage>

\

기능

설명

Runtime

미터링/등급/과금 정책

API 서비스 제공자가 제공하는 서비스에 대한 각종 정책 정의 정보. JSON
형식으로 되었으며, 해당 정책을 CF-ABACUS에 등록하면 정책에 정의한 내용에
따라 API 사용량을 집계 한다.

정책은 서비스 제공자가 정의해야 하며, JSON 스키마는 다음을 참조한다.

[https://github.com/cloudfoundry-incubator/cf-abacus/blob/master/doc/api.md](https://github.com/cloudfoundry-incubator/cf-abacus/blob/master/doc/api.md)

서비스 브로커 API

Cloud Controller와 Service Broker 사이의 규약으로써 서비스 브로커 API
개발에 대해서는 다음을 참조한다.

[https://github.com/OpenPaaSRnD/Documents/blob/master/Development-Guide/ServicePack\_develope\_guide.md\#11](https://github.com/OpenPaaSRnD/Documents/blob/master/Development-Guide/ServicePack_develope_guide.md#11)

서비스 API

서비스 제공자가 제공하는 API 서비스 기능 및 API 사용량을 CF-ABACUS에
전송하는 기능으로 구성되었다.

대시보드

서비스를 제공하기 위한 인증, 서비스 모니터링 등을 위한 대시보드 기능으로
서비스 제공자가 개발해야 한다.

CF-ABACUS

CF-ABACUS 핵심 기능으로써 수집한 사용량 정보를 집계한다.

CF-ABACUS은 CF 설치 후, CF에 마이크로 서비스 형태로 설치한다. 자세한
사항은 다음을 참조한다.

[https://github.com/cloudfoundry-incubator/cf-abacus](https://github.com/cloudfoundry-incubator/cf-abacus)

\

※ 본 개발 가이드는 **API****서비스** 개발에 대해서만 기술하며, 다른
컴포넌트의 개발 또는 설치에 대해서 링크한 사이트를 참조한다.

\

2.  개발환경 구성 {.번호대제목-cjk}
    =============

Node.js 애플리케이션 개발을 위해 다음과 같은 환경으로 개발환경을 구성
한다.

\

-   CF release: v226 이상

-   nodejs\_buildpack: nodejs\_buildpack-cached-v1.5.18.zip 이상

-   Node.js: v5.11.1

-   npm: v3.8.6

\

1.  Node.js 및 npm 설치 {.번호대제목-cjk}
    ===================

1.  Node.js 및 npm 설치

\

  --------------------------------------------------------------------
  \$ sudo apt-get install curl

  \

  **\#\# Node.js version 6.x****를 설치할 경우**

  **\#\# Source Repository****등록**

  \$ curl -sL https://deb.nodesource.com/setup\_6.x | sudo -E bash –

  \

  **\#\# Node.js & Npm****설치**

  \$ sudo apt-get install -y nodejs

  \

  **\#\# Node.js & Nmp****설치 확인**

  \$ node -v

  \$ npm -v
  --------------------------------------------------------------------

\

※ Windows 용은 다음 사이트에서 Node.js를 다운 받는다.

[https://nodejs.org/ko/download/](https://nodejs.org/ko/download/)

\

※ 개발도구

Node.js는 javascript기반의 언어로 Notepad++, Sublim Text, EditPlus등
문서편집기를 개발도구로 사용할 수 있다. 또한 Eclipse의 플러그인
Nodeclipse를 설치하여 사용할 수 있다. 그리고 상용 개발 도구로써는
WebStome 등이 있다.

\

2.  CF-Abacus 설치 {.번호대제목-cjk}
    ==============

별도 제공하는 Abacus 설치 가이드를 참고하여 CF-Abacus를 설치한다.

\

3.  샘플 API 서비스 브로커 및 대시보드 개발 {.번호대제목-cjk}
    =======================================

별도 제공하는 서비스 브로커 개발 가이드를 참고하여 서비스 브로커 및
대시보드를 개발한다.

[https://github.com/OpenPaaSRnD/Documents/blob/master/Development-Guide/ServicePack\_develope\_guide.md\#6](https://github.com/OpenPaaSRnD/Documents/blob/master/Development-Guide/ServicePack_develope_guide.md#6)

\

4.  샘플 API 서비스 개발 {.번호대제목-cjk}
    ====================

샘플 api 서비스는 서비스 요청이 있는 경우, 해당 요청에 대한 응답 처리와
api 서비스 요청에 대한 미터링 정보를 CF-ABACUS에 전송하는 처리를 한다.

\

1.  샘플 API 서비스 형상

  -----------------------------
  sample\_api\_node\_service/

  ├── .apprc

  ├── .gitignore

  ├── manifest.yml

  ├── .npmrc

  ├── package.json

  ├── sampleApiService

  └── src

  ├── app.js

  └── test

  └── test.js
  -----------------------------

\

  ----------------------- ----------------------------------------------------------------------------------------------------------------
  **파일****/****폴더**   **목적**

  .apprc                  앱 실행 환경 설정 파일

  .gitignore              Git을 통한 형상 관리 시, 형상 관리를 할 필요가 없는 파일 또는 디렉토리를 설정한다.

  manifest.yml            애플리케이션을 파스-타 플랫폼에 배포 시 적용하는 애플리케이션에 대한 환경 설정 정보
                          
                          애플리케이션의 이름, 배포 경로, 인스턴스 수 등을 정의할 수 있다.

  .npmrc                  Npm 실행 환경 설정 파일

  package.json            node.js 어플리케이션에 필요한 npm의 의존성 정보를 기술하는데 사용 한다.
                          
                          npm install 명령을 실행시 install 뒤에 아무런 정보를 입력하지 않으면 이 파일의 정보를 이용하여 npm을 설치한다.

  sampleApiService        서비스 앱 실행 스크립트

  app.js                  서비스 앱
                          
                          서비스 요청에 대한 라우팅 정보와 미터링 정보 전송 처리를 정의한다.

  test.js                 서비스 앱 단위 테스트 모듈
                          
                          mocha를 통한 서비스 앱의 단위 테스트를 정의한다.
  ----------------------- ----------------------------------------------------------------------------------------------------------------

\

\

1.  샘플 API 서비스 애플리케이션 코드 구현 {.번호대제목-cjk}
    ======================================

\

1.  Package.json

샘플 애플리케이션의 코드 구성에 대해 기술한다.

  ------------------------------------------------------------------------------------------------------------
  {

  "name": "sample-api-node-service", \#\# 앱 명

  "version": "0.0.1", \#\# 앱 버전

  "description": "CF API Usage Service Metering Sample",

  "main": "lib/app.js", \#\# 개발 소스의 메인

  "bin": {

  "sampleApiService": "./sampleApiService"

  },

  "files": [ \#\# 형상 관리 대상의 파일 또는 디렉토리를 기술

  ".apprc",

  ".npmrc",

  "manifest.yml",

  "src/",

  "sampleApiService"

  ],

  "scripts": { \#\# npm run 명령어 또는 npm 명령어로 실행

  "start": "./sampleApiService start", \#\# npm start: 앱 실행

  "stop": "./sampleApiService stop", \#\# npm stop: 앱 중지

  "babel": "babel", \#\# npm run babel: 개발 소스를 컴파일

  "test": "eslint && mocha", \#\# npm test: 개발 소스를 테스트

  "lint": "eslint", \#\# npm run lint: 개발 소스 체크

  "pub": "publish", \#\# npm run publish: 개발 소스를 퍼블리시

  "cfpack": "cfpack", \#\# npm run cfpack: 컴파일한 개발 소스를 패키지화

  "cfpush": "cfpush" \#\# npm run cfpush: 패키지화 한 개발 소스를 cf에 push

  },

  "author": "PAASTA",

  "license": "Apache-2.0", \#\# 라이선스 선언

  "dependencies": {

  "body-parser": "\^1.15.2", \#\# json parser 모듈

  "cors": "\^2.8.1", \#\# cross domain request 허용 모듈

  "express": "\^4.14.0", \#\# node.js 웹 프레임워크

  "abacus-oauth": "\^0.0.6-dev.8", \#\# Secured Abacus와 통신을 위한 Oauth 모듈

  "request": "\^2.74.0", \#\# request 모듈

  "babel-preset-es2015": "\^6.6.0", \#\# ECMA5를 ECMA6으로 변환하기 위한 모듈

  "commander": "\^2.8.1", \#\# 명령어 실행 모듈

  "underscore": "\^1.8.3" \#\# javascript에 사용할 수 있는 함수가 정의된 모듈

  },

  "devDependencies": { \#\# 개발 환경에서 의존하는 패키지

  "abacus-babel": "file:../../tools/babel", \#\# 개발 소스를 ECMA5 -\> ECMA6으로 변환

  "abacus-cfpack": "file:../../tools/cfpack", \#\# 개발 소스를 cf에 push 할 수 있도록 패키지화 하는 패키지

  "abacus-cfpush": "file:../../tools/cfpush", \#\# 개발 소스를 cf에 push하는 패키지

  "abacus-coverage": "file:../../tools/coverage", \#\# 개발 소스에 대해 테스트 소스의 coverage율 체크 패키지

  "abacus-eslint": "file:../../tools/eslint", \#\# 개발 소스 코드 체크 패키지

  "abacus-mocha": "file:../../tools/mocha", \#\# mocha 테스트 실행 패키지

  "abacus-publish": "file:../../tools/publish" \#\# 개발 소스 퍼블리시 패키지

  },

  "engines": {

  "node": "\>=5.11.1", \#\# nodejs 버전

  "npm": "\>=3.8.6" \#\# npm 버전

  }

  }
  ------------------------------------------------------------------------------------------------------------

\

2.  Manifest.yml

앱을 CF에 배포할 때 필요한 설정 정보 및 앱 실행 환경에 필요한 설정
정보를 기술한다.

  ------------------------------------------------------------------------------------------
  ---

  ---

  applications:

  - name: sample-api-node-service \# 애플리케이션 이름

  host: sample-api-node-service \# 애플리케이션 호스트명

  memory: 512M \# 애플리케이션 메모리 사이즈

  disk\_quota: 512M \# 애플리케이션 디스크 사이즈

  instances: 1 \# 애플리케이션 인스턴스 개수

  command: npm start \# CF에서의 애플리케이션 시작 명령어

  path: ./.cfpack/app.zip \# 배포될 애플리케이션의 위치

  env:

  CONF: default \# 명령어 실행 환경 설정 정보

  DEBUG: s\* \# 디버그 출력 대상 설정

  NODE\_TLS\_REJECT\_UNAUTHORIZED: 0 \# SSL flag off

  API: [https://api.bosh-lite.com](https://api.bosh-lite.com) \# CF API 서비스 엔드포인트

  COLLECTOR: https://localhost/v1/metering/collected/usage \# api 사용량 전송 엔드포인드

  SECURED: true \# Secured Abacus 설정: false or true

  AUTH\_SERVER: https://api.bosh-lite.com:443 \# oauth 서비스 엔드포인트

  CLIENT\_ID: abacus \# oauth 권한 id

  CLIENT\_SECRET: secret \# oauth id 비밀번호

  JWTKEY: |+ \# 앱을 secured mode로 서비스 하기 위해 유효성 체크를 위한 인증 서비스 공개키

  -----BEGIN PUBLIC KEY-----

  MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDHFr+KICms+tuT1OXJwhCUmR2d

  KVy7psa8xzElSyzqx7oJyfJ1JZyOzToj9T5SfTIq396agbHJWVfYphNahvZ/7uMX

  qHxf+ZH9BL1gk9Y6kCnbM5R60gfwjyW1/dQPjOzn9N394zd2FJoFHwdq9Qs0wBug

  spULZVNRxq7veq/fzwIDAQAB

  -----END PUBLIC KEY-----

  JWTALGO: RS256
  ------------------------------------------------------------------------------------------

\

-   ENV 항목

    -   아래에 기술한 항목 이외에 서비스에 필요한 항목을 추가할 수 있다.

  --------------------------------- ------------------------------------------------------------------
  ENV 항목                          설명

  DEBUG                             애플리케이션 디버그 로그 출력 대상 설정

  NODE\_TLS\_REJECT\_UNAUTHORIZED   -

  API                               CF API URL
                                    
                                    https://api.\<파스-타 도메인\>

  COLLECTOR                         Abacus Collector 앱의 사용량 수집 서비스 URL
                                    
                                    https://\<abacus-collector 도메인\> /v1/metering/collected/usage

  SECURED                           Secured abacus를 운용할 경우, 반드시 true를 설정한다.

  AUTH\_SERVER                      SECURED가 true인 경우 설정한다.
                                    
                                    - CF UAA를 Auth\_server로 설정할 경우,
                                    
                                    https://api.\<파스-타 도메인\>
                                    
                                    - Abacus의 AuthServer를 Auth\_server로 설정할 경우,
                                    
                                    abacus-authserver-plugin

  CLIENT\_ID                        SECURED가 true인 경우 설정한다.
                                    
                                    Abacus.usage 권한 id

  CLIENT\_SECRET                    SECURED가 true인 경우 설정한다.
                                    
                                    Abacus.usage 권한 비밀번호

  JWTKEY                            SECURED가 true인 경우 설정한다.
                                    
                                    - CF UAA를 Auth\_server로 설정할 경우,
                                    
                                    CF 배포 manifest의 properties.jwt.verification\_key 값을 설정
                                    
                                    - Abacus의 AuthServer를 Auth\_server로 설정할 경우,
                                    
                                    Key 값을 설정

  JWTALGO                           SECURED가 true인 경우 설정한다.
                                    
                                    - CF UAA를 Auth\_server로 설정할 경우,
                                    
                                    RS256
                                    
                                    - Abacus의 AuthServer를 Auth\_server로 설정할 경우,
                                    
                                    HS256
  --------------------------------- ------------------------------------------------------------------

\

3.  App.js

애플리케이션의 기능 및 미터링 정보 전송 기능을 구현한다.

샘플 애플리케이션은 다음과 같이 기능을 구현하였다.

\

-   의존 모듈 선언

  --------------------------------------------------------------------------------------------
  'use strict';

  \

  // Implemented in ES5 for now

  /\* eslint no-var: 0 \*/ // ECMA5로 개발할 경우, eslint 체크에서 var 사용 제한을 풀어준다.

  \

  var express = require('express'); // 웹 프레임워크 모듈

  var request = require('request'); // request 모듈

  var bodyParser = require('body-parser'); // request 정보를 json으로 변환하는 모듈

  var cp = require('child\_process');

  var commander = require('commander'); // command 사용 가능 모듈

  var oauth = require('abacus-oauth'); // oauth 모듈
  --------------------------------------------------------------------------------------------

\

-   변수 선언

  ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  // Create router

  var routes = express.Router(); // 앱 서비스 엔드포인트를 설정 할 미들웨어

  \

  // Abacus Collector App's URL

  var abacusCollectorUrl = process.env.COLLECTOR; // api 사용량 전송 url (abacus)

  \

  // Abacus System Token Scope

  var scope = 'abacus.usage.write abacus.usage.read'; // abacus를 secured로 설정할 경우, api 사용량 정보의 전송을 위한 token scope 설정 (scope 설정에 대해서는 abacus 설치 가이드 참조)

  \

  // 크로스 도메인 허용을 위한 헤더 설정

  var accessControlAllowHeader = 'Origin,X-Requested-With,Content-Type,Accept';

  \

  // 아래의 항목은 더미로 설정한 미터링 대상 정보이다.

  // 실제 서비스를 구현할 경우, 해당 항목을 제공하는 API 호출이나 프로퍼티 값 등을 통해

  // 설정한다.

  var resourceId = 'object-storage';

  var measure1 = 'storage';

  var measure2 = 'light\_api\_calls';

  var measure3 = 'heavy\_api\_calls';

  var serviceKey = '[cloudfoundry]';

  \
  ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

\

-   Secure Abacus 보안 구현

  -----------------------------------------------------------------------------------------------------
  // abacus 토큰 초기화

  var abacusToken = void 0;

  \

  // Secure 설정

  var secured = function secured() {

  return process.env.SECURED === 'true' ? true : false;

  };

  \

  // abacus에 api usage를 전송할 때, request header 설정

  var authHeader = function authHeader(token) {

  return token ? { authorization: token() } : {};

  };

  \

  … 중략 …

  \

  var sampleApiService = function sampleApiService() {

  \

  var app = express();

  \

  // Secured abacue의 경우, 앱 서비스를 시작할 때, abacus 토큰을 구한다.

  if (secured()) {

  /\*

  AUTH\_SERVER: 인증 서버 엔드포인트 https://hostname:port or

  https://hostname

  CLIENT\_ID: 인증 서버 권한 아이디

  CLIENT\_SECRET: 인증 서버 권한 비밀번호

  SCOPE: 토큰 scope

  \*/

  \

  abacusToken = oauth.cache(process.env.AUTH\_SERVER, process.env.CLIENT\_ID,

  process.env.CLIENT\_SECRET, scope);

  \

  // abacus Token을 주기적으로 갱신한다.

  abacusToken.start();

  };

  \

  // api 서비스 또한 secured 모드로 실행 할 수 있다.

  // secured 모드로 실행할 경우, route에 등록한 모든 서비스에 대해 유효성 체크를 실행하도록 구현한다.

  // 본 샘플에서는 abacus의 oauth 서비스의 유효성 체크를 사용하였다.

  // if (secured())

  // app.use(/\^\\/plan[0-9]/,

  // oauth.validator(process.env.JWTKEY, process.env.JWTALGO));

  \

  app.use(routes);

  \

  return app;

  };

  \

  … 후략 …

  \
  -----------------------------------------------------------------------------------------------------

\

-   COR 설정

  ----------------------------------------------------------------------------------------------------------------------------
  // api 서비스는 요청한 서비스의 응답 처리 이외에 abacus에 대해 request 처리가 추가로 필요하므로 크로스 도메인을 설정 한다.

  routes.use(function(req, res, next) {

  res.header('Access-Control-Allow-Origin', '\*');

  res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE,OPTIONS');

  res.header('Access-Control-Allow-Headers', accessControlAllowHeader);

  next();

  });

  \
  ----------------------------------------------------------------------------------------------------------------------------

\

-   서비스 API 구현

  --------------------------------------------------------------------------------------------
  /\*

  Sample API 'Plan1'

  ​1. Caller의 요청에 API 서비스를 처리 응답 (Sample에서는 생략)

  ​2. abacus에 api usage 전송

  \*/

  routes.post('/plan1', function(req, res, next) {

  \

  // api 요청 시각 설정

  var d = new Date();

  var eventTime = Date.parse(d);

  \

  // 사용량 미터링에 필요한 메타 정보 설정

  var orgid = req.body.organization\_id;

  var spaceid = req.body.space\_id;

  var instanceid = reqs.body.instance\_id ? reqs.body.instance\_id : reqs.body.consumer\_id;

  var appid = req.body.consumer\_id;

  var planid = req.body.plan\_id;

  var credential = req.body.credential;

  \

  // 실제 서비스에 실행에 필요한 메타 정보는 inputs에 설정

  // var inputs = req.body.inputs;

  \

  // 서비스 이용 권한 체크, 해당 체크 내용은 제공할 서비스에 맞게 변형한다.

  if (credential.serviceKey != serviceKey)

  return res.status(401).send();

  \

  // 필수 항목 체크

  if (!orgid || !spaceid || !appid)

  return res.status(400).send();

  \

  // abacus에 리포팅할 api 사용량 정보를 작성한다. (JSON 형식)

  var usage =

  buildAppUsage(orgid, spaceid, appid, instanceid, planid, eventTime);

  \

  // api 사용량 정보 체크

  if (usage.usage === null) return res.status(400).send();

  \

  // api 사용량 전송을 위한 요청 정보 설정

  var options = {

  uri: abacusCollectorUrl,

  headers: authHeader(abacusToken),

  json: usage.usage

  };

  \

  // api usage를 abacus에 전송

  request.post(options, function(error, response, body) {

  \

  // abacus 전송 처리 판정

  if (error) console.log(error);

  else if (response.statusCode === 201 || response.statusCode === 200) {

  // console.log('Successfully reported usage %j with headers %j',

  // usage, response.headers);

  res.status(201).send(response.body);

  return;

  }

  // abacus에 api 사용량을 중복 송신한 경우 발생

  else if (response.statusCode === 409) {

  // console.log('Conflicting usage %j. Response: %j',

  // usage, response);

  res.sendStatus(409);

  return;

  }

  // 기타 400 / 500 계열의 오류 체크

  else {

  // console.log('failed report usage %j with headers %j',

  // usage, response.headers);

  res.sendStatus(response.statusCode);

  return;

  }

  });

  return res;

  });
  --------------------------------------------------------------------------------------------

\

-   Abacus 전송 Json 생성

  ---------------------------------------------------------------------------
  \

  // abacus로 전송할 데이터 포맷을 만든다.

  var buildAppUsage =

  function buildAppUsage(orgid, spaceid, appid, insid, planid, eventTime) {

  \

  var appUsage = { usage: null };

  \

  // sample api plan id가 'standard'

  if (planid == 'standard')

  appUsage = {

  usage: {

  start: eventTime,

  end: eventTime,

  organization\_id: orgid,

  space\_id: spaceid,

  consumer\_id: 'app:' + appid,

  resource\_id: resourceId,

  plan\_id: planid,

  resource\_instance\_id: insid,

  measured\_usage: [

  {

  measure: measure1,

  quantity: 1073741824

  },

  {

  measure: measure2,

  quantity: 1000

  },

  {

  measure: measure3,

  quantity: 0

  }

  ]

  }

  };

  \

  return appUsage;

  };
  ---------------------------------------------------------------------------

\

-   기타

  -----------------------------------------------------------------------
  // 앱 서비스를 위한 설정 정보

  // PORT, HOST명 등을 설정한다.

  var conf = function conf() {

  process.env.PORT = commander.port || process.env.PORT || 9602;

  };

  \

  // Command line 인터페이스 설정

  // package에 기술한 start, stop 스크립트를 실행할 수 있다.

  // 앱 시작: npm start

  // 앱 중지: npm stop

  \

  var runCLI = function runCLI() {

  \

  commander

  .option('-p, --port \<port\>', 'port number [9602]')

  .option('start', 'start the server')

  .option('stop', 'stop the server')

  .parse(process.argv);

  \

  // Start API server

  if (commander.start) {

  conf();

  \

  // Create app and listen on the configured port

  var app = sampleApiService();

  \

  app.listen({

  port: parseInt(process.env.PORT)

  });

  }

  else if (commander.stop)

  \

  // Stop API App server

  cp.exec(

  'pkill -f "node ./sampleApiService"', function(err, stdout, stderr) {

  if (err) console.log('Stop error %o', err);

  });

  };

  \

  // Export our public functions

  module.exports = sampleApiService;

  module.exports.runCLI = runCLI;

  \
  -----------------------------------------------------------------------

\

1.  샘플 API 서비스 애플리케이션 미터링 연동 항목 {.번호대제목-cjk}
    =============================================

1.  미터링 정보 전송 API 엔드포인트

  -----------------------------------
  POST /v1/metering/collected/usage
  -----------------------------------

\

2.  API 서비스 미터링 전송 항목

  ------------------------ ---------------- ------------------------------------ -----------------------------------------------
  항목명                   유형             설명                                 예시
  start                    UNIX Timestamp   API처리 시작 시각                    1396421450000
  end                      UNIX Timestamp   API처리 응답 시각                    1396421451000
  organization\_id         String           API를 호출한 앱의 조직 ID            us-south:54257f98-83f0-4eca-ae04-9ea35277a538
  space\_id                String           API를 호출한 앱의 영역 ID            d98b5916-3c77-44b9-ac12-04456df23eae
  consumer\_id             String           API를 호출한 앱 ID                   App: d98b5916-3c77-44b9-ac12-04d61c7a4eae
  resource\_id             String           API 자원 ID                          sample\_api
  plan\_id                 String           API 미터링 Plan ID                   basic
  resource\_instance\_id   String           API를 호출한 앱 ID                   d98b5916-3c77-44b9-ac12-04d61c7a4eae
  measured\_usage          Array            미터링 항목                          -
  measure                  String           미터링 대상 명                       api\_calls
  quantity                 Number           해당 API 요청에 대한 API 처리 횟수   10
  ------------------------ ---------------- ------------------------------------ -----------------------------------------------

\

3.  API 서비스 미터링 전송 항목 예제

  ----------------------------------------------------------------------
  {

  "start": 1396421450000,

  "end": 1396421451000,

  "organization\_id": "us-south:54257f98-83f0-4eca-ae04-9ea35277a538",

  "space\_id": "d98b5916-3c77-44b9-ac12-04456df23eae",

  "consumer\_id": "app:d98b5916-3c77-44b9-ac12-045678edabae",

  "resource\_id": "sample\_api",

  "plan\_id": "basic",

  "resource\_instance\_id": "d98b5916-3c77-44b9-ac12-04d61c7a4eae",

  "measured\_usage": [

  {

  "measure": "api\_calls",

  "quantity": 10

  }

  ]

  }
  ----------------------------------------------------------------------

\

참고:
[https://github.com/cloudfoundry-incubator/cf-abacus/blob/master/doc/api.md](https://github.com/cloudfoundry-incubator/cf-abacus/blob/master/doc/api.md)

\

5.  API 서비스 연동 샘플 애플리케이션 개발 {.번호대제목-cjk}
    ======================================

Api 서비스를 이용하는 애플리케이션으로 본 샘플은 웹 화면을 통행 단순히
api 서비스를 요청하는 기능만을 구현 하였다.

\

1.  API 서비스 연동 샘플 애플리케이션 형상

  ----------------------------
  sample\_api\_node\_caller/

  ├── .apprc

  ├── cfpush.sh

  ├── .eslintignore

  ├── .gitignore

  ├── manifest.yml

  ├── .npmrc

  ├── package.json

  ├── sampleApiCaller

  ├── src

  │   ├── app.js

  │   ├── bower\_components

  │   └── test

  │   └── test.js

  └── views

  ├── apiCaller.handlebars

  └── layouts

  └── main.handlebars
  ----------------------------

\

  ----------------------- ------------------------------------------------------------------------------------------------------------------------
  **파일****/****폴더**   **목적**

  .apprc                  앱 실행 환경 설정 파일

  cfpush.sh               Manifest의 org\_id를 현재 target 설정된 org의 guid로 수정하고 패키지한 앱을 cf에 push 한다.

  .eslintignore           Eslint 실행 시, 체크 제외 대상의 파일 및 디렉토리를 설정한다.

  .gitignore              Git을 통한 형상 관리 시, 형상 관리를 할 필요가 없는 파일 또는 디렉토리를 설정한다.

  manifest.yml            파스-타 플랫폼에 배포시 애플리케이션에 대한 설정이다. 애플리케이션의 이름, 배포 경로, 인스턴스 수 등을 정의할 수 있다.

  .npmrc                  Npm 실행 환경 설정 파일

  package.json            node.js 어플리케이션에 필요한 npm의 의존성 정보를 기술하는데 사용 한다.
                          
                          npm install 명령을 실행시 install 뒤에 아무런 정보를 입력하지 않으면 이 파일의 정보를 이용하여 npm을 설치한다.

  sampleApiCaller         서비스 호출 앱 실행 스크립트

  app.js                  서비스 호출 앱
                          
                          웹 서비스 및 bind 한 api 서비스 호출에 대한 라우팅 정보를 정의한다.

  bower\_components       프론트엔드 라이브러리 디렉토리

  test.js                 서비스 앱 단위 테스트 모듈
                          
                          mocha를 통한 서비스 앱의 단위 테스트를 정의한다.

  views                   샘플 API 서비스 호출 데모 화면
  ----------------------- ------------------------------------------------------------------------------------------------------------------------

\

1.  API 서비스 연동 샘플 애플리케이션 코드 {.번호대제목-cjk}
    ======================================

\

1.  Package.json

샘플 애플리케이션의 코드 구성에 대해 기술한다.

  ---------------------------------------------------
  {

  "name": "sample-api-node-caller",

  "version": "0.0.1",

  "description": "CF API Metering Caller Sample",

  "main": "lib/app.js",

  "bin": {

  "sampleApiCaller": "./sampleApiCaller"

  },

  "files": [

  ".apprc",

  ".eslintignore",

  ".npmrc",

  "cfpush.sh",

  "manifest.yml",

  "src/",

  "views/"

  ],

  "scripts": {

  "start": "./sampleApiCaller start",

  "stop": "./sampleApiCaller stop",

  "babel": "babel",

  "test": "eslint && mocha",

  "lint": "eslint",

  "pub": "publish",

  "cfpack": "cfpack",

  "cfpush": "./cfpush.sh"

  },

  "author": "PAASTA",

  "license": "Apache-2.0",

  "dependencies": {

  "body-parser": "\^1.15.2",

  "cors": "\^2.8.1",

  "express": "\^4.14.0",

  "express-handlebars": "\^3.0.0", \#\# 핸들러 모듈

  "babel-preset-es2015": "\^6.6.0",

  "request": "\^2.74.0",

  "commander": "\^2.8.1",

  "underscore": "\^1.8.3"

  },

  "devDependencies": {

  "abacus-babel": "file:../../tools/babel",

  "abacus-cfpack": "file:../../tools/cfpack",

  "abacus-cfpush": "file:../../tools/cfpush",

  "abacus-coverage": "file:../../tools/coverage",

  "abacus-eslint": "file:../../tools/eslint",

  "abacus-mocha": "file:../../tools/mocha",

  "abacus-publish": "file:../../tools/publish"

  },

  "engines": {

  "node": "\>=5.11.1",

  "npm": "\>=3.8.6"

  }

  }

  \
  ---------------------------------------------------

\

2.  Manifest.yml

앱을 CF에 배포할 때 필요한 설정 정보 및 앱 실행 환경에 필요한 설정
정보를 기술한다.

  ---------------------------------------------------------
  \

  ---

  applications:

  - name: sample-api-node-caller \# 애플리케이션 이름

  memory: 512M \# 애플리케이션 메모리 사이즈

  disk\_quota: 512M

  instances: 1 \# 애플리케이션 인스턴스 개수

  path: ./.cfpack/app.zip \# 배포될 애플리케이션의 위치

  command: npm start \# CF에서의 애플리케이션 시작 명령어

  env:

  DEBUG: a\*

  ORG\_ID: d6ce3670-ab9c-4453-b993-f2821f54846b

  SECURED: false

  \#AUTH\_SERVER: https://api.bosh-lite.com:443

  \#CLIENT\_ID: abacus

  \#CLIENT\_SECRET: secret
  ---------------------------------------------------------

\

-   ENV 항목

    -   아래에 기술한 항목 이외에 서비스에 필요한 항목을 추가할 수 있다.

  ---------------- -------------------------------------------------------------
  ENV 항목         설명

  DEBUG            애플리케이션 디버그 로그 출력 대상 설정

  ORG\_ID          Caller 애플리케이션을 배포할 조직 ID

  SECURED          API 서비스를 Secured로 운용할 경우, 반드시 true를 설정한다.

  AUTH\_SERVER     SECURED가 true인 경우 설정한다.
                   
                   - CF UAA를 Auth\_server로 설정할 경우,
                   
                   https://api.\<파스-타 도메인\>
                   
                   - Abacus의 AuthServer를 Auth\_server로 설정할 경우,
                   
                   abacus-authserver-plugin

  CLIENT\_ID       SECURED가 true인 경우 설정한다.
                   
                   Abacus.usage 권한 id

  CLIENT\_SECRET   SECURED가 true인 경우 설정한다.
                   
                   Abacus.usage 권한 비밀번호
  ---------------- -------------------------------------------------------------

\

3.  App.js

Api 서비스를 요청하는 애플리케이션을 구현한다.

\

-   의존 모듈 선언

  ------------------------------------------------------------------------------------------------------------
  'use strict';

  \

  // Implemented in ES5 for now

  /\* eslint no-var: 0 \*/

  \

  var express = require('express');

  var request = require('request');

  var bodyParser = require('body-parser');

  var cp = require('child\_process');

  var commander = require('commander');

  var handlebars = require('express-handlebars').create({ defaultLayout:'main' }); // 웹 서비스 뷰 처리 모듈
  ------------------------------------------------------------------------------------------------------------

\

-   변수 선언

  -------------------------------------------------------------------------------
  // 크로스 도메인 요청 헤더

  var accessControlAllowHeader = 'Origin,X-Requested-With,Content-Type,Accept';

  \

  var vcapApp = undefined;

  var vcapService = undefined;

  var dummyOrgId = undefined;

  var vcapBindServices = undefined;

  \

  // vcap\_application 정보 설정

  vcapApp = JSON.parse(process.env.VCAP\_APPLICATION);

  \

  // vcap\_service 정보 설정

  vcapService = JSON.parse(process.env.VCAP\_SERVICES);

  \

  // 조직 guid 설정

  dummyOrgId = process.env.ORG\_ID;
  -------------------------------------------------------------------------------

\

-   Secure Abacus 보안 구현

  -------------------------------------------------------------------------------------------
  // api 서비스를 참고 하여 api service 요청 시, header에 token을 설정하는 처리를 구현한다.
  -------------------------------------------------------------------------------------------

\

-   COR 설정

  ----------------------------------------------------------------------------------------------------------------------------
  // api 서비스는 요청한 서비스의 응답 처리 이외에 abacus에 대해 request 처리가 추가로 필요하므로 크로스 도메인을 설정 한다.

  routes.use(function(req, res, next) {

  res.header('Access-Control-Allow-Origin', '\*');

  res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE,OPTIONS');

  res.header('Access-Control-Allow-Headers', accessControlAllowHeader);

  next();

  });

  \
  ----------------------------------------------------------------------------------------------------------------------------

\

-   서비스 API 구현

  ----------------------------------------------------------------------------
  // VCAP\_SERVICE에서 앱과 바인드한 서비스 정보를 가져온다.

  // 복수의 서비스가 앱에 바인드 될 수 있다.

  vcapBindServices = vcapService[Object.keys(vcapService)[0]];

  \

  var sampleApiCaller = function sampleApiCaller() {

  \

  var app = express();

  \

  // 웹 서비스 뷰 처리

  app.engine('handlebars', handlebars.engine);

  app.set('view engine', 'handlebars');

  \

  // 크로스 도메인 처리

  app.use(function(req, res, next) {

  res.header('Access-Control-Allow-Origin', '\*');

  res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE,OPTIONS');

  res.header('Access-Control-Allow-Headers', accessControlAllowHeader);

  next();

  });

  \

  // 뷰에서 참조하는 js 모듈을 미들웨어에 마운트 한다.

  app.use('/bower\_components',

  express.static(\_\_dirname + '/bower\_components'));

  \

  // 웹 서비스 메인을 미들웨어에 마운트 한다.

  app.get('/', function(req, res) {

  res.type('text/html');

  res.render('apiCaller'); // 뷰 생성

  });

  \

  // to support JSON-encoded bodies

  app.use(bodyParser.json());

  \

  // to support URL-encoded bodies

  app.use(bodyParser.urlencoded({

  extended: true

  }));

  \

  /\*

  Sample Web Service

  ​1. API 서비스를 요청

  \*/

  app.post('/sampleApiSerivceCall', function(req, res, next) {

  \

  // 앱에 Bind한 서비스가 없는 경우

  if (typeof vcapBindServices !== 'object') {

  return res.status(502).send('unbind service called');

  next();

  };

  \

  // API Service와 연동 할 서비스를 구한다.

  // 바인드된 복수의 서비스 중에서 연동 할 서비스 정보를 구한다.

  var bindApiService = vcapBindServices[0];

  \

  // 위에서 구한 서비스 정보에서 서비스 url를 구한다.

  var serviceUrl = bindApiService.credentials.uri ?

  bindApiService.credentials.uri : bindApiService.credentials.url;

  \

  // API Service에 요청 할 JSON 을 작성한다.

  var callEvent = buildSendData(req.body, bindApiService);

  \

  // Set request options

  var options = {

  uri: serviceUrl,

  json: callEvent

  };

  \

  // api Service에 요청하고 결과를 응답한다.

  request.post(options, function(error, response, body) {

  \

  if (error) console.log(error);

  else if (response.statusCode === 201 || response.statusCode === 200) {

  // console.log('Successfully reported usage %j with headers %j',

  // usage, response.headers);

  res.status(201).send(response.body);

  return;

  }

  else {

  // console.log('failed report usage %j with headers %j',

  // usage, response.headers);

  res.sendStatus(response.statusCode);

  return;

  }

  });

  return res;

  });

  \

  // Not found

  app.use(function(req, res) {

  res.type('text/plain');

  res.status(404);

  res.send('404 not found');

  });

  \

  return app;

  };
  ----------------------------------------------------------------------------

\

-   Abacus 전송 Json 생성

  --------------------------------------------------------------------
  // 서비스로 보낼 데이터를 JSON 형식으로 작성한다.

  var buildSendData = function buildSendData(args, bindApiService) {

  \

  return {

  organization\_id: dummyOrgId,

  space\_id: vcapApp.space\_id,

  consumer\_id: vcapApp.application\_id,

  instance\_id: vcapApp.instance\_id ?

  vcapApp.instance\_id : vcapApp.application\_id,

  plan\_id: bindApiService.plan,

  credential: bindApiService.credentials,

  inputs: args

  };

  };
  --------------------------------------------------------------------

\

-   기타

  ----------------------------------------------------------------------
  // 앱 서비스를 위한 설정 정보

  // PORT, HOST명 등을 설정한다.

  var conf = function conf() {

  process.env.PORT = commander.port || process.env.PORT || 9601;

  };

  \

  // Command line interface

  var runCLI = function runCLI() {

  \

  commander

  .option('-p, --port \<port\>', 'port number [9601]')

  .option('start', 'start the server')

  .option('stop', 'stop the server')

  .parse(process.argv);

  \

  // Start Caller server

  if (commander.start) {

  conf();

  \

  // Create app and listen on the configured port

  var app = sampleApiCaller();

  \

  app.listen({

  port: parseInt(process.env.PORT)

  });

  }

  else if (commander.stop)

  \

  // Stop Caller App server

  cp.exec(

  'pkill -f "node ./sampleApiCaller"', function(err, stdout, stderr) {

  if (err) console.log('Stop error %o', err);

  });

  };

  \

  // Export our public functions

  module.exports = sampleApiCaller;

  module.exports.runCLI = runCLI;
  ----------------------------------------------------------------------

\

4.  Views/apiCaller.handlebars

Api 서비스를 요청하는 웹 화면

\

  -------------------------------------------------------------------------
  \<!DOCTYPE html\>

  \<html\>

  \<head\>

  \<meta charset="utf-8"\>

  \<script src="bower\_components/jquery/dist/jquery.min.js"\>\</script\>

  \<script type="text/javascript"\>

  \

  \$(document).ready(function() {

  \

  \$('\#result\_div').html('');

  \

  var appUrl = document.URL;

  // 요청 서비스 입력 항목 설정

  var input\_param1 = '서울';

  var input\_param2 = '무교동';

  \

  \$('\#send\_btn').click(function(){

  \

  \$('\#result\_div').html('');

  \

  var data = sendData( input\_param1, input\_param2 );

  \

  \$.ajax({

  url: appUrl + 'sampleApiSerivceCall',

  type:"POST",

  data:data,

  success:function(data)

  {

  \$('\#result\_div').html("posted.");

  },

  error:function(jqXHR,textStatus,errorThrown)

  {

  \$('\#result\_div').html(errorThrown);

  }

  });

  \

  });

  });

  \

  // 요청 서비스 입력 항목 설정, JSON 형식으로 작성한다.

  var sendData = function buildJSON( input\_param1, input\_param2 ) {

  \

  return {

  input\_param1: input\_param1,

  input\_param2: input\_param2

  };

  };

  \

  \</script\>

  \</head\>

  \<body\>

  \<form id="form"\>

  \</form\>

  \

  \<h3\>CF REMOTE SERVICE API CALL TEST\</h3\>

  \

  \<button id="send\_btn"\>SEND SERVICE API CALL\</button\>

  \<br\>

  \<div id="result\_div"\>\</div\>

  \

  \<!--\<h4\>{{vcapService}}\</h4\>--\>

  \

  \</body\>

  \</html\>
  -------------------------------------------------------------------------

\

\

1.  API 서비스 연동 샘플 애플리케이션 인터페이스 항목 {.번호대제목-cjk}
    =================================================

1.  API 서비스 엔드포인트

  ------------------------------------------------------
  GET|POST|PUT|DELETE *\<api\_service\_restful\_api\>*
  ------------------------------------------------------

\

2.  API 서비스 미터링 전송 항목

  -------------- -------- -------------------------------------------------- --------------------------------------
  항목명         유형     설명                                               예시

  org\_id        String   API 서비스를 요청한 앱의 조직 ID                   54257f98-83f0-4eca-ae04-9ea35277a538

  space\_id      String   API 서비스를 요청한 앱의 영역 ID                   d98b5916-3c77-44b9-ac12-04456df23eae

  consumer\_id   String   API 서비스를 요청한 앱 ID                          d98b5916-3c77-44b9-ac12-045678edabae

  instance\_id   String   API 서비스를 요청한 앱의 자원 인스턴스 ID          d98b5916-3c77-44b9-ac12-045678edabad

  plan\_id       String   앱의 요청한 API 서비스의 plan ID                   basic

  credentials    JSON     서비스 요청에 필요한 credential 항목을 설정한다.   credentials: {
                                                                             
                                                                             key: value,
                                                                             
                                                                             …
                                                                             
                                                                             }

  inputs         JSON     서비스 요청에 필요한 입력 정보를 설정한다.         inputs: {
                                                                             
                                                                             key: value,
                                                                             
                                                                             ...
                                                                             
                                                                             }
  -------------- -------- -------------------------------------------------- --------------------------------------

\

3.  API 서비스 미터링 전송 항목 예제

  -----------------------------------------------------------
  {

  organization\_id: 'd6ce3670-ab9c-4453-b993-f2821f54846b',

  space\_id: 'ab63eaed-7932-4f24-804d-dccb40a68752',

  consumer\_id: 'ff7476f9-f5b6-420c-96f0-ac39be43de8c',

  instance\_id: 'ff7476f9-f5b6-420c-96f0-ac39be43de8c',

  plan\_id: 'standard',

  credential: {

  'serviceKey': '[cloudfoundry]',

  'url': 'http://localhost:9602/plan1'

  },

  inputs: {

  key1: 'val1',

  key2: 'val2'

  }

  }
  -----------------------------------------------------------

\

3.  VCAP\_SERVICES 환경설정 정보 {.번호대제목-cjk}
    ============================

파스-타 플랫폼에 배포되는 애플리케이션이 바인딩된 서비스별 접속 정보를
얻기 위해서는 애플리케이션별로 등록되어있는 VCAP\_SERVICES 환경설정
정보를 읽어들여 정보를 획득 할 수 있다.

\

1.  파스-타 플랫폼의 애플리케이션 환경정보

-   서비스를 바인딩하면 JSON 형태로 환경설정 정보가 애플리케이션 별로
    등록된다.

  --------------------------------------------------------------------
  {

  "VCAP\_SERVICES": {

  "sample\_api\_node\_service": [

  {

  "credentials": {

  "documentUrl": "http://sample-api-node-service.bosh-lite.com/doc",

  "serviceKey": "UxJWlzVP0VJquACPD+FohQ==",

  "url": "http://sample-api-node-service.bosh-lite.com/"

  },

  "label": "sample\_api\_node\_service",

  "name": "sampleNodeApi",

  "plan": "basic",

  "provider": null,

  "syslog\_drain\_url": "PASTA\_SERVICE\_METERING",

  "tags": [

  "Sample API Service"

  ],

  "volume\_mounts": []

  }

  ]

  }

  }

  \

  {

  "VCAP\_APPLICATION": {

  "application\_id": "58872d8a-edfc-44df-97f0-df67cf9033a7",

  "application\_name": "sample-api-node-caller",

  "application\_uris": [

  "sample-api-node-caller.bosh-lite.com"

  ],

  "application\_version": "55678102-584c-4fca-8304-82f727506b1d",

  "limits": {

  "disk": 512,

  "fds": 16384,

  "mem": 512

  },

  "name": "sample-api-node-caller",

  "space\_id": "2ce08996-f463-406c-a971-adbbaf4e4ca5",

  "space\_name": "ops",

  "uris": [

  "sample-api-node-caller.bosh-lite.com"

  ],

  "users": null,

  "version": "55678102-584c-4fca-8304-82f727506b1d"

  }

  }
  --------------------------------------------------------------------

\

2.  Node.js에서 파스-타 플랫폼의 애플리케이션 환경정보에 접근하는 방법

-   시스템 환경변수의 VCAP\_SERVICES값을 읽어서 접근 할 수 있다.

  ----------------------------
  process.env.VCAP\_SERVICES
  ----------------------------

\

6.  미터링/등급/과금 정책 {.번호대제목-cjk}
    =====================

서비스, 그리고 서비스 제공자 마다 미터링/등급/과금 정책 다르기 때문에 본
가이드에서는 정책의 개발 예제를 다루지는 않는다. 다만 CF-ABACUS에 적용할
수 있는 형식에 대해 설명한다.

\

1.  미터링 정책 {.번호대제목-cjk}
    ===========

미터링 정책이란 수집한 미터링 정보에서 미터링 대상의 지정 및 집계 방식을
정의한 JSON 형식의 오브젝트이다. 서비스 제공자는 미터링 정책 스키마에
맞춰 서비스에 대한 정책을 개발한다.

\

1.  미터링 정책 스키마

  ------------ -------- ----------- --------------------------------------------------------------
  항목명       유형     필수        설명
  plan\_id     String   O           API 미터링 Plan ID
  measures     Array    최소 하나   API 미터링 정보 수집 대상 정의
  Name         String   O           미터링 정보 수집 대상 명
  Unit         String   O           미터링 정보 수집 대상 단위
  metrics      Array    최소하나    API 미터링 집계 방식 정의
  Name         String   O           미터링 정보 수집 대상 명
  unit         String   O           미터링 정보 수집 대상 단위
  meter        String   X           미터링 정보에 대해서 수집 단계에 적용하는 계산식 또는 변환식
  accumulate   String   X           미터링 정보에 대해서 누적 단계에 적용하는 계산식 또는 변환식
  aggregate    String   X           미터링 정보에 대해서 집계 단계에 적용하는 계산식 또는 변환식
  summarize    String   X           미터링 정보를 보고할 때 적용하는 계산식 또는 변환식
  title        String   X           API 미터링 제목
  ------------ -------- ----------- --------------------------------------------------------------

\

2.  미터링 정책 예제

  -------------------------------------------------
  {

  "plan\_id": "basic-object-storage",

  "measures": [

  {

  "name": "storage",

  "unit": "BYTE"

  },

  {

  "name": "api\_calls",

  "units": "CALL"

  }

  ],

  "metrics": [

  {

  "name": "storage",

  "unit": "GIGABYTE",

  "meter": "(m) =\> m.storage / 1073741824",

  "accumulate": "(a, qty) =\> Math.max(a, qty)"

  },

  {

  "name": "thousand\_api\_calls",

  "unit": "THOUSAND\_CALLS",

  "meter": "(m) =\> m.light\_api\_calls / 1000",

  "accumulate": "(a, qty) =\> a ? a + qty : qty",

  "aggregate": "(a, qty) =\> a ? a + qty : qty",

  "summarize": "(t, qty) =\> qty"

  }

  ]

  }
  -------------------------------------------------

\

2.  등급 정책 {.번호대제목-cjk}
    =========

등급 정책이란 각 서비스의 사용 가중치를 정의한 JSON 형식의 오브젝트이다.
서비스 제공자는 등급 정책 스키마에 맞춰 서비스에 대한 정책을 개발한다.

\

1.  등급 정책 스키마

  ---------- -------- ---------- ---------------------------------------
  항목명     유형     필수       설명
  plan\_id   String   O          API 등급 Plan ID
  metrics    Array    최소하나   등급 정책 목록
  Name       String   O          등급 정의 대상 명
  rate       String   X          가중치 계산식 또는 변환식
  charge     String   X          사용량에 대한 과금 계산식 또는 변환식
  title      String   X          등급 정책 명
  ---------- -------- ---------- ---------------------------------------

\

2.  등급 정책 예제

  ------------------------------------------
  {

  "plan\_id": "object-rating-plan",

  "metrics": [

  {

  "name": "storage"

  },

  {

  "name": "thousand\_api\_calls",

  "rate": "(p, qty) =\> p ? p \* qty : 0",

  "charge": "(t, cost) =\> cost"

  }

  ]

  }
  ------------------------------------------

\

\

3.  과금 정책 {.번호대제목-cjk}
    =========

과금 정책이란 각 서비스에 대한 사용 단가를 정의한 JSON 형식의
오브젝트이다. 서비스 제공자는 과금 정책 스키마에 맞춰 서비스에 대한
정책을 개발한다.

\

1.  과금 정책 스키마

  ---------- -------- ---------- --------------------------------
  항목명     유형     필수       설명
  plan\_id   String   O          API 과금 Plan ID
  Metrics    Array    최소하나   과금 정책 목록
  Name       String   O          과금 대상 명
  Price      Array    최소하나   과금 정책 상세
  Country    String   O          서비스 사용 단가에 적용할 통화
  Price      Number   O          서비스 사용 단가
  title      String   X          과금 정책 제목
  ---------- -------- ---------- --------------------------------

\

2.  과금 정책 예제

  -------------------------------------
  {

  "plan\_id": "object-pricing-basic",

  "metrics": [

  {

  "name": "storage",

  "prices": [

  {

  "country": "USA",

  "price": 1

  },

  {

  "country": "EUR",

  "price": 0.7523

  },

  {

  "country": "CAN",

  "price": 1.06

  }

  ]

  },

  {

  "name": "thousand\_api\_calls",

  "prices": [

  {

  "country": "USA",

  "price": 0.03

  },

  {

  "country": "EUR",

  "price": 0.0226

  },

  {

  "country": "CAN",

  "price": 0.0317

  }

  ]

  }

  ]

  }
  -------------------------------------

\

4.  정책 등록 {.번호대제목-cjk}
    =========

정책은 2가지 방식 중 하나의 방법으로 CF-ABACUS에 등록할 수 있다.

\

1.  js 파일을 등록하는 방식

작성한 정책을 다음의 디렉토리에 저장한 후, CF에 CF-ABACUS를 배포 또는
재배포 한다.

\

-   미터링 정책의 경우

  -------------------------------------------------------
  cf-abacus/lib/plugins/provisioning/src/plans/metering
  -------------------------------------------------------

\

-   등급 정책의 경우

  ------------------------------------------------------
  cf-abacus/lib/plugins/provisioning/src/plans/pricing
  ------------------------------------------------------

\

-   과금 정책의 경우

  -----------------------------------------------------
  cf-abacus/lib/plugins/provisioning/src/plans/rating
  -----------------------------------------------------

\

2.  DB에 등록하는 방식

작성한 정책을 curl 등을 이용해 DB에 저장하는 방식으로 CF-ABACUS를
재배포할 필요는 없다. 정책 등록 시, 정책 ID는 고유해야 한다.

\

-   미터링 정책의 경우

  ---------------------------------------------
  POST /v1/metering/plans/:metering\_plan\_id
  ---------------------------------------------

\

  --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  \# 예제

  \$ curl -k -X POST 'http://abacus-provisioning-plugin.bosh-lite.com/v1/metering/plans/sample-linux-container' \\

  -H "Content-Type: application/json" \\

  -d '{"plan\_id":"sample-linux-container","measures":[{"name":"current\_instance\_memory","unit":"GIGABYTE"},{"name":"current\_running\_instances","unit":"NUMBER"},{"name":"previous\_instance\_memory","unit":"GIGABYTE"},{"name":"previous\_running\_instances","unit":"NUMBER"}],"metrics":[{"name":"memory","unit":"GIGABYTE","type":"time-based","meter":"((m)=\>({previous\_consuming:newBigNumber(m.previous\_instance\_memory||0).div(1073741824).mul(m.previous\_running\_instances||0).mul(-1).toNumber(),consuming:newBigNumber(m.current\_instance\_memory||0).div(1073741824).mul(m.current\_running\_instances||0).toNumber()})).toString()","accumulate":"((a,qty,start,end,from,to,twCell)=\>{if(end\<from||end\>=to)returnnull;constpast=from-start;constfuture=to-start;consttd=past+future;return{consuming:a&&a.since\>start?a.consuming:qty.consuming,consumed:newBigNumber(qty.consuming).mul(td).add(newBigNumber(qty.previous\_consuming).mul(td)).add(a?a.consumed:0).toNumber(),since:a&&a.since\>start?a.since:start};}).toString()","aggregate":"((a,prev,curr,aggTwCell,accTwCell)=\>{if(!curr)returna;constconsuming=newBigNumber(curr.consuming).sub(prev?prev.consuming:0);constconsumed=newBigNumber(curr.consumed).sub(prev?prev.consumed:0);return{consuming:consuming.add(a?a.consuming:0).toNumber(),consumed:consumed.add(a?a.consumed:0).toNumber()};}).toString()","summarize":"((t,qty,from,to)=\>{if(!qty)return0;constrt=Math.min(t,to?to:t);constpast=from-rt;constfuture=to-rt;consttd=past+future;constconsumed=newBigNumber(qty.consuming).mul(-1).mul(td).toNumber();returnnewBigNumber(qty.consumed).add(consumed).div(2).div(3600000).toNumber();}).toString()"}]}' \\

  -H "Authorization: \$(cf oauth-token | grep bearer)"
  --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

\

-   등급 정책의 경우

  -----------------------------------------
  POST /v1/rating/plans/:rating\_plan\_id
  -----------------------------------------

\

  ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  \#\# 예제

  \$ curl -k -X POST 'http://abacus-provisioning-plugin.bosh-lite.com/v1/rating/plans/linux-rating-sample' \\

  -H "Content-Type: application/json" \\

  -d '{"plan\_id":"linux-rating-sample","metrics":[{"name":"memory","rate":"((price,qty)=\>({price:price,consuming:qty.consuming,consumed:qty.consumed})).toString(),charge:((t,qty,from,to)=\>{if(!qty)return0;constrt=Math.min(t,to?to:t);constpast=from-rt;constfuture=to-rt;consttd=past+future;constconsumed=newBigNumber(qty.consuming).mul(-1).mul(td).toNumber();constgbhour=newBigNumber(qty.consumed).add(consumed).div(2).div(3600000).toNumber();returnnewBigNumber(gbhour).mul(qty.price).toNumber();}).toString()"}]}' \\

  -H "Authorization: \$(cf oauth-token | grep bearer)"
  ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

\

-   과금 정책의 경우

  -------------------------------------------
  POST /v1/pricing/plans/:pricing\_plan\_id
  -------------------------------------------

\

  ------------------------------------------------------------------------------------------------------------------------
  \# 예제

  \$ curl -k -X POST 'http://abacus-provisioning-plugin.bosh-lite.com/v1/pricing/plans/linux-pricing-sample' \\

  -H "Content-Type: application/json" \\

  -d '{"plan\_id":"linux-pricing-sample","metrics":[{"name":"memory","prices":[{"country":"USA","price":0.00014}]}]}' \\

  -H "Authorization: \$(cf oauth-token | grep bearer)"
  ------------------------------------------------------------------------------------------------------------------------

\

7.  배포 {.번호대제목-cjk}
    ====

파스-타 플랫폼에 애플리케이션을 배포하면 배포한 애플리케이션과 파스-타
플랫폼이 제공하는 서비스를 연결하여 사용할 수 있다. 파스-타 플랫폼상에서
실행을 해야만 파스-타 플랫폼의 애플리케이션 환경변수에 접근하여 서비스에
접속할 수 있다.

\

1.  파스-타 플랫폼 로그인 {.번호대제목-cjk}
    =====================

아래의 과정을 수행하기 위해서 파스-타 플랫폼에 로그인

  ------------------------------------------------------------------------------------------------------------------------------------
  \$ cf api --skip-ssl-validation **https://api.*****\<****파스**-**타 도메인**\>***\#****파스****-****타 플랫폼****TARGET****지정**

  \

  \$ cf login -u *\<****user name****\>* -o *\<****org name****\>* -s *\<****space name****\>***\#****로그인 요청**
  ------------------------------------------------------------------------------------------------------------------------------------

\

2.  API 서비스 브로커 생성 {.번호대제목-cjk}
    ======================

애플리케이션에서 사용할 서비스를 파스-타 플랫폼을 통하여 생성한다.
별도의 서비스 설치과정 없이 생성할 수 있으며, 애플리케이션과
바인딩과정을 통해 접속정보를 얻을 수있다.

-   서비스 생성 (cf marketplace 명령을 통해 서비스 목록과 각 서비스의
    플랜을 조회할 수 있다.)

-   Gradle 버전: 2.2

  -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  **\#\#****서비스 브로커****CF****배포**

  \$ cd *\<**샘플 서비스 브로커 경로**\>*/sample\_api\_java\_broker

  \$ gradle build -x test

  :compileJava

  Note: \~/PAASTA-API-METERING-SAMPLE/lib/sample\_api\_java\_broker/src/main/java/org/openpaas/servicebroker/api/config/CatalogConfig.java uses unchecked or unsafe operations.

  Note: Recompile with -Xlint:unchecked for details.

  :processResources

  :classes

  :findMainClass

  :jar

  :bootRepackage

  :assemble

  :check

  :build

  \

  BUILD SUCCESSFUL

  \

  Total time: 28.905 secs

  \

  This build could be faster, please consider using the Gradle Daemon: https://docs.gradle.org/2.14.1/userguide/gradle\_daemon.html

  \

  \$ cf push

  \

  **\#\#****서비스 브로커 생성**

  \$ cf create-service-broker *\<**서비스 브로커 명**\> \<**인증**ID\> \<**인증**Password\> \<**서비스 브로커 주소**\>*

  \

  예)

  \$ cf create-service-broker sample-api-broker admin cloudfoundry http://sample-api-java-broker.bosh-lite.com

  \

  **\#\#****서비스 브로커 확인**

  \$ cf service-brokers

  Getting service brokers as admin...

  \

  name url

  sample-api-broker http://sample-api-java-broker.bosh-lite.com

  \

  **\#\#****서비스 카탈로그 확인**

  \$ cf service-access

  Getting service access as admin...

  broker: sample-api-broker

  service plan access orgs

  standard\_obejct\_storage\_light\_api\_calls standard none

  standard\_obejct\_storage\_heavy\_api\_calls basic none

  \

  **\#\#****등록한 서비스 접근 허용**

  \$ cf enable-service-access *\<**서비스명**\>* -p *\<**플랜명**\>*

  \

  예)

  \$ cf enable-service-access standard\_obejct\_storage\_light\_api\_calls -p standard
  -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

\

3.  API 서비스 애플리케이션 배포 및 서비스 등록 {.번호대제목-cjk}
    ===========================================

API 서비스 애플리케이션을 파스-타 플랫폼에 배포한다. 서비스 등록한 API는
다른 애플리케이션과 바인드하여 API 서비스를 할 수 있다.

\

1.  애플리케이션 배포

-   cf push 명령으로 배포한다. 별도의 값을 넣지않으면 manifest.yml의
    설정을 사용한다.

  -----------------------------------------------------------------------------------------------------
  **\#\# API****서비스 배포**

  \$ cd *\<**샘플**api**서비스 경로**\>*/sample\_api\_node\_service

  \$ npm install && npm run babel && npm run cfpack && cf push

  \

  **\#\#****서비스 생성**

  \$ cf create-service *\<**서비스명**\> \<**플랜명**\> \<**서비스 인스턴스명**\>*

  예)

  \$ cf create-service standard\_obejct\_storage\_light\_api\_calls standard sampleNodejslightCallApi

  \

  **\#\#****서비스 확인**

  \$ cf services

  Getting services in org real / space ops as admin...

  OK

  \

  name service plan bound apps last operation

  sampleNodejslightCallApi standard\_obejct\_storage\_light\_api\_calls standard create succeeded
  -----------------------------------------------------------------------------------------------------

\

\

4.  API 서비스 연동 샘플 애플리케이션 배포 및 서비스 연결 {.번호대제목-cjk}
    =====================================================

애플리케이션과 서비스를 연결하는 과정을 '바인드(bind)라고 하며, 이
과정을 통해 서비스에 접근할 수 있는 접속정보를 생성한다.

-   애플리케이션과 서비스 연결

  ------------------------------------------------------------------------------------------------------------------------
  **\#\# API****서비스 연동 샘플 애플리케이션 배포**

  \$ cd *\<**샘플 애플리케이션 경로**\>*/sample\_api\_node\_caller

  \$ npm install && npm run babel && npm run cfpack && ./cfpush.sh

  \

  **\#\#****서비스 바인드**

  \$ cf bind-service *\<APP\_NAME\> \<SERVICE\_INSTANCE\>* -c *\<PARAMETERS\_AS\_JSON\>*

  \

  예)

  \$ cf bind-service sample-api-node-caller sampleNodejslightCallApi -c '{"serviceKey": "[cloudfoundry]"}'

  \

  **\#\#****서비스 연결 확인**

  \$ cf services

  Getting services in org real / space ops as admin...

  OK

  \

  name service plan bound apps last operation

  sampleNodejslightCallApi standard\_obejct\_storage\_light\_api\_calls standard sample-api-node-caller create succeeded

  \

  **\#\#****애플리케이션 실행**

  \$ cf start *\<APP\_NAME\>*

  \

  *예**)*

  \$ cf start sample-api-node-caller

  \

  **\#\#****형상 확인**

  \$ cf a

  Getting apps in org real / space ops as admin...

  OK

  \

  name requested state instances memory disk urls

  sample-api-node-service started 1/1 512M 512M sample-api-node-service.bosh-lite.com

  sample-api-java-broker started 1/1 512M 1G sample-api-java-broker.bosh-lite.com

  sample-api-node-caller started 1/1 512M 512M sample-api-node-caller.bosh-lite.com
  ------------------------------------------------------------------------------------------------------------------------

\

8.  테스트 {.번호대제목-cjk}
    ======

샘플 애플리케이션은 REST 서비스로 구현되어 있으며, 코드 체크, 테스트 및
커버리스 체크를 위해서 eslint/mocha/Istanbul 모듈을 사용하였다. 테스트를
진행하기 위해서는 mocha 모듈을 포함한 package.json 안의
devDependencies모듈이 설치 되어 있어야한다. (npm install)

\

1.  코드 테스트를 위한 개발 환경 구성

\

-   테스트를 위한 형상

  ---------------------------------------------------------------------------------------------------------------
  \<앱\>/

  ├── .eslint // 코드 체크 대상 설정

  ├── .eslintignore // 코드 체크에서 제외할 파일 또는 디렉토리 정의

  ├── package.json // 앱 구성 요소 정의

  ├── lib // npm run babel을 실행하면 자동으로 생성된다. cfpack 및 mocha 테스트 는 lib에 있는 모듈을 대상 한다.

  │   ├── app.js // ECMA5로 변환된 app.js

  │   ├── bower\_components

  │   └── test

  │   └── test.js

  ├── src // 코드 변환 및 코드 체크 대상 디렉토리

  │   ├── app.js

  │   ├── bower\_components

  │   └── test // mocha 테스트 모듈은 반드시 src/test 디렉토리에 있어야 한다.

  │   └── test.js

  └── views // src/lib 이외에 위치한 디렉토리와 파일은 체크 및 mocha 테스트 대상이 아니다.

  ├── apiCaller.handlebars

  └── layouts

  └── main.handlebars
  ---------------------------------------------------------------------------------------------------------------

\

-   테스트를 위한 package.json 구성

  -----------------------------------------------------------------
  {

  ... 중략

  테

  "scripts": {

  "babel": "babel", // 코드 변환

  "test": "eslint && mocha", // 코드 체크 및 테스트

  "lint": "eslint" // 코드 체크

  },

  \

  ... 중략

  "devDependencies": {

  "abacus-babel": "\^0.0.6-dev.8", // 코드 변환 (ECMA6 -\> ECMA5)

  "abacus-coverage": "\^0.0.6-dev.8", // 테스트 커버리지 체크

  "abacus-eslint": "\^0.0.6-dev.8", // 코드 체크

  "abacus-mocha": "\^0.0.6-dev.8" // 코드 테스트

  },

  ... 후략

  }
  -----------------------------------------------------------------

\

-   Eslint 설정

  -----------------------------------------------------------------------------------------------------------
  // false / 0 / disable을 설정한 항목에 대해서만 체크를 생략한다.

  // 규칙에 대한 자세한 내용은 [http://eslint.org/docs/rules/](http://eslint.org/docs/rules/) 을 참고 한다.

  ---

  parser: espree

  \

  env:

  browser: true

  node: true

  es6: false

  jasmine: true

  mocha: true

  \

  globals:

  \_\_DEV\_\_: true

  jest: true

  sinon: true

  chai: true

  spy: true

  stub: true

  \

  parserOptions:

  sourceType: "module"

  \

  rules:

  \# ERRORS

  space-before-blocks: 2 // 공백 문자가 2개 이상 연속

  indent: [2, 2, { "SwitchCase": 1 }] // 들여쓰기 형식 설정 (공백문자 2개만 허용)

  \#strict: [2, "global"]

  semi: [2, "always"] // ‘;’ 누락

  comma-dangle: [2, "never"]

  no-unused-expressions: 2

  block-scoped-var: 2

  dot-notation: 2

  consistent-return: 2 // 함수의 응답 결과 형식 불일치

  no-unused-vars: [2, args: none] // 소스내 참조되지 않는 변수 선언

  quotes: [2, 'single'] // 문자열에 ‘“’ 사용 금지

  space-infix-ops: 2

  no-else-return: 2

  no-extra-parens: 2

  no-eq-null: 2

  no-floating-decimal: 2

  no-param-reassign: 2

  no-self-compare: 2

  wrap-iife: [2, "inside"]

  brace-style: [2, "stroustrup", { "allowSingleLine": false }]

  object-curly-spacing: [1, "always"]

  func-style: [2, "expression"]

  no-lonely-if: 2

  space-in-parens: [2, "never"]

  space-before-function-paren: [2, "never"]

  generator-star-spacing: [2, "before"]

  spaced-comment: [2, "always"]

  eol-last: 2 // 소스 코드의 마지막에 개행문자 없음

  no-multi-spaces: 2

  curly: [2, "multi"]

  camelcase: [2, {properties: "never"}] // 변수명에 ‘\_’ 사용 허용

  no-eval: 2

  \#require-yield: 2

  no-var: 2 // 변수 선언에 var 금지 (ECMA6)

  max-len: [2, 80] // 한줄에 80자까지 허용

  complexity: [2, 6]

  arrow-parens: [2, "always"]

  \

  \# WARNINGS

  \# We use this for functions that reference each other

  no-use-before-define: 1

  \

  \# WISHLIST.

  \# valid-jsdoc: 1

  \

  \# DISABLED. These aren't compatible with our style

  \# We use this for private/internal variables

  no-underscore-dangle: 0

  \# We pass constructors around / access them from members

  new-cap: 0

  \# We do this in a few places to align values

  key-spacing: 0

  \# We do this a lot

  space-after-keywords: 0

  \# We do this mostly for callbacks

  no-shadow: 0

  \# We do not use spaces in brackets but use spaces in braces

  space-in-brackets: 0
  -----------------------------------------------------------------------------------------------------------

\

2.  테스트 실행

-   Test 디렉토리 아래에 있는 모듈을 테스트 한다.

\

  --------------------------------------------------------------------------------------------------------------------------
  \$ cd *\<**샘플 애플리케이션 경로**\>/\<**앱**\>*

  \$ npm install && npm run babel && npm test

  \

  \$ npm install && npm run babel && npm test

  sample-api-node-service@0.0.1 \~/PAASTA-API-METERING-SAMPLE/lib/sample\_api\_node\_service

  ├─┬ abacus-babel@0.0.6-dev.8

  ... 설치 패키지 트리 출력 ...

  \

  npm WARN sample-api-node-service@0.0.1 No repository field.

  \

  \> sample-api-node-service@0.0.1 babel /home/cloud4u/workspace/PAASTA-API-METERING-SAMPLE/lib/sample\_api\_node\_service

  \> babel

  \

  src/app.js -\> lib/app.js

  src/test/test.js -\> lib/test/test.js

  \

  \> sample-api-node-service@0.0.1 test /home/cloud4u/workspace/PAASTA-API-METERING-SAMPLE/lib/sample\_api\_node\_service

  \> eslint && mocha

  \

  \

  Testing...

  Running Istanbul instrumentation on node\_modules/abacus-oauth/lib/index.js

  Running Istanbul instrumentation on node\_modules/abacus-request/lib/index.js

  Running Istanbul instrumentation on node\_modules/abacus-transform/lib/index.js

  Running Istanbul instrumentation on node\_modules/abacus-yieldable/lib/index.js

  Running Istanbul instrumentation on node\_modules/abacus-debug/lib/index.js

  Running Istanbul instrumentation on node\_modules/abacus-events/lib/index.js

  Running Istanbul instrumentation on node\_modules/abacus-lock/lib/index.js

  Running Istanbul instrumentation on node\_modules/abacus-lrucache/lib/index.js

  Running Istanbul instrumentation on node\_modules/abacus-retry/lib/index.js

  \

  \

  sample-api-node-service

  Running Istanbul instrumentation on lib/app.js

  ✓ Send API usage data to Abacus (59ms)

  ✓ Missing metering parameter

  ✓ Not serviced plan

  ✓ Missing credentials

  ✓ Send duplicated data to Abacus

  ✓ Abacus is not serviced

  \

  \

  6 passing (159ms)

  \

  Source lib/app.js

  'use strict';

  \

  // Implemented in ES5 for now

  /\* eslint no-var: 0 \*/

  \

  ... 중략 ...

  \

  // Export our public functions

  module.exports = sampleApiService;

  module.exports.runCLI = runCLI;

  \

  Coverage lines 88.89% statements 86.9% lib/app.js

  \

  Run time 686ms
  --------------------------------------------------------------------------------------------------------------------------

\

9.  API 및 CF-Abacus 연동 테스트 {.번호대제목-cjk}
    ============================

API 연동 샘플 애플리케이션의 url을 통해 웹 브라우저에서 접속하면 API
연동 및 API 사용량에 대한 CF-Abacus 연동 테스트를 진행 할 수 있다.

\

1.  CF-Abacus 연동 확인

  -----------------------------------------------------------------------------------------------------------------------------------------------------------
  **\#\#****조직****guid****확인**

  \$ cf org *\<**샘플 애플리케이션을 배포한 조직**\>* --guid

  \

  예)

  \$ cf org real --guid

  ***877d01b2-d177-4209-95b0-00de794d9bba***

  \

  **\#\#****샘플 애플리케이션****guid****확인**

  \$ cf env *\<**샘플 애플리케이션 명**\>*

  예)

  \$ cf env sample-api-node-caller

  Getting env variables for app sample-api-node-caller in org real / space ops as admin...

  OK

  \

  \<\<중략\>\>

  {

  "VCAP\_APPLICATION": {

  "application\_id": "***58872d8a-edfc-44df-97f0-df67cf9033a7***",

  "application\_name": "sample-api-node-caller",

  "application\_uris": [

  "sample-api-node-caller.bosh-lite.com"

  ],

  "application\_version": "55678102-584c-4fca-8304-82f727506b1d",

  "limits": {

  "disk": 512,

  "fds": 16384,

  "mem": 512

  },

  "name": "sample-api-node-caller",

  "space\_id": "2ce08996-f463-406c-a971-adbbaf4e4ca5",

  "space\_name": "ops",

  "uris": [

  "sample-api-node-caller.bosh-lite.com"

  ],

  "users": null,

  "version": "55678102-584c-4fca-8304-82f727506b1d"

  }

  }

  \

  \<\<후략\>\>

  \

  **\#\# API****사용량 확인**

  \$ curl 'http://abacus-usage-reporting.*\<**파스**-**타 도메인**\>*/v1/metering/organizations/*\<**샘플 애플리케이션을 배포한 조직**\>*/aggregated/usage'

  \

  예)

  \$ curl 'http://abacus-usage-reporting.bosh-lite.com/v1/metering/organizations/877d01b2-d177-4209-95b0-00de794d9bba/aggregated/usage'
  -----------------------------------------------------------------------------------------------------------------------------------------------------------

\

[1](#sdfootnote1anc)^^ 변경 내용: 변경이 발생되는 위치와 변경 내용을
자세히 기록(장/절과 변경 내용을 기술한다.)

\

Page 37 / 37

\

[2-1-0]:/Sample-App-Guide/image/nodejs/2-2-1-0.png
