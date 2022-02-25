# MQTT, Elasticsearch 연동 샘플 프로젝트

### 본 프로젝트를 구동하기 위해서는 Docker 환경이 필요합니다.

- docker run -d --name emqx -p 1883:1883 -p 8081:8081 -p 8083:8083 -p 8084:8084 -p 8883:8883 -p 18083:18083 emqx/emqx:4.4.1


- docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" elasticsearch:7.17.0