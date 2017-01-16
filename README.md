 | 항목명                 |유형             | 설명                                          | 예시                                           |
 |-----------------------|-----------------|----------------------------------------------|----------------------------------------------|
 |   start               | UNIX Timestamp  |바인드 언바인드 처리 시작 시각                   |1396421450000                                    |
 |   end                 | UNIX Timestamp  |  바인드 언바인드 처리 응답 시각                 |1396421451000                                    |
 |  organization_id      | String          | 바인드 요청을 호출한 앱의 조직 ID               | us-south:54257f98-83f0-4eca-ae04-9ea35277a538   |
 |   space_id            |String           | 서비스 바인드 요청을 호출한 앱의 영역 ID         |d98b5916-3c77-44b9-ac12-04456df23eae             |
 |  consumer_id          | String          |서비스 바인드 요청을 호출한 앱 ID                | App: d98b5916-3c77-44b9-ac12-04d61c7a4eae       |
 |  resource_id          |String           |서비스 자원 ID                                 |linux-container                                 |
 |  plan_id              |String           | 서비스 미터링 Plan ID                         |standard                                        |
 |  resource_instance_id | String          |바인드 요청을 호출한 앱 ID                      | d98b5916-3c77-44b9-ac12-04d61c7a4eae            |
 |  measured_usage       | Array           | 미터링 항목                                   | ㅁㄴㅇㅁ                                        |    
 |   measure             | String          | 미터링 대상 명                                |sample_service_usage_param1                     |
 |  quantity             |Number           |  서비스 사용량 예제는 메모리 사용량 (byte)      |1000000000                                        |
