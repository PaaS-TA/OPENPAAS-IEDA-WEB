| **기능**                       |**설명**                       |                                 |
|--------------------------------|------------------------------|---------------------------------|
| Runtime                        |       미터링/등급/과금 정책    |API 서비스 제공자가 제공하는 서비스에 대한 각종 정책 정의 정보. JSON 형식으로 되었으며, 해당 정책을 CF-ABACUS에 등록하면 정책에 정의한 내용에 따라 API 사용량을 집계 한다.<br>
정책은 서비스 제공자가 정의해야 하며, JSON 스키마는 다음을 참조한다.<br>
https://github.com/cloudfoundry-incubator/cf-abacus/blob/master/doc/api.md
                                 |
||       서비스 브로커 API        |     Cloud Controller와 Service Broker 사이의 규약으로써 서비스 브로커 API 개발에 대해서는 다음을 참조한다
https://github.com/OpenPaaSRnD/Documents/blob/master/Development-Guide/ServicePack_develope_guide.md#11
                            |                                  
||        서비스 API             |     서비스 제공자가 제공하는 API 서비스 기능 및 API 사용량을 CF-ABACUS에 전송하는 기능으로 구성되었다.                            |
||        대시보드               |     서비스를 제공하기 위한 인증, 서비스 모니터링 등을 위한 대시보드 기능으로 서비스 제공자가 개발해야 한다.                            |
| CF-ABACUS                      ||       CF-ABACUS 핵심 기능으로써 수집한 사용량 정보를 집계한다.<br>
CF-ABACUS은 CF 설치 후, CF에 마이크로 서비스 형태로 설치한다. 자세한 사항은 다음을 참조한다.<br>
https://github.com/cloudfoundry-incubator/cf-abacus<br>
                      |
