| 　　|유형 | 필수|
|---------|---|----|
|   수정      |build.gradle   |  빌드 설정 파일<br>미터링 사용량 객체 생성에 필요한 dependency 를 추가 한다.|     
|   수정      | application-mvc.properties  | 서비스 바인딩 request 의 정보들을 매핑한다.<br>미터링 서비스를 구현하기 위해 바인딩 되는 애플리케이션의 환경정보 필드를 추가 한다.|     
|   수정      | datasource.properties   | Mongo-db 서비스 정보   |     
|   수정     | MongoServiceInstanceBindingService  |service broker binding request parameter 로 입력 받은 미터링 정보를 ServiceInstanceBinding 에 매핑하는 프로세스를 추가 한다.    |     
|   추가      | SampleMeteringReportServiceImpl  | SampleMeteringReportService 를 구현 한다.   |     
|   추가     |SampleMeteringOAuthServiceImpl   | SampleMeteringOAuthService 를 구현 한다.   |     
|   수정     |Manifest.yml   | 앱을 CF에 배포할 때 필요한 설정 정보 및 앱 실행 환경에 필요한 설정 정보를 기술한다.   |
