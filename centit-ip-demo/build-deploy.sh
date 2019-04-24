#!/bin/sh
#编译
mvn clean package -DskipTests=true
#构建镜像
TIME='date +%Y%m%d%H%M'
GIT_REVISION='git log -1 --pretty=format:"%h"'
IMAGE_NAME=172.29.0.13:8082/framework:${TIME}_${GIT_REVISION}
docker build -t ${IMAGE_NAME} .
#上传nexus
cd
docker login -u developer -p centit 172.29.0.13:8082
docker push ${IMAGE_NAME}
#运行
docker run -d --network host -p 11000:8080 --name framework ${IMAGE_NAME}