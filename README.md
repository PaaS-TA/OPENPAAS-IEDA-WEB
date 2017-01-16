
```yml

  applications:
  - name: paasta-usage-reporting
  memory: 1024M
  disk\_quota: 512M
  instances: 1
  command: node app.js \# 애플리케이션 실행 명령어
  path: ./ \# 배포될 애플리케이션의 위치
  env: \# [[6.2.](#_Abacus와_연동할_DB)[abacus manifest.yml](#_Abacus와_연동할_DB)]참조
    DEBUG: a\*
    API: https://api.*\<CF**도메인**\>*
    CF\_CLIENT\_ID: abacus-cf-bridge
    CF\_CLIENT\_SECRET: secret
    ABACUS\_REPORT\_SERVER: http://abacus-usage-reporting.*\<CF**도메인**\>*
    NODE\_TLS\_REJECT\_UNAUTHORIZED: 0
    NODE\_MODULES\_CACHE: false
    SECURED: false \# abacus-usage-reporting의 SECURED 설정이 true인 경우, true 설정
    AUTH\_SERVER: https://api.*\<CF**도메인**\>*
    CLIENT\_ID: abacus
    CLIENT\_SECRET: secret
    JWTKEY: |+
      -----BEGIN PUBLIC KEY-----
      -----END PUBLIC KEY-----
    JWTALGO: RS256
```
