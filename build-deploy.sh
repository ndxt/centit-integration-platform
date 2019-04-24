#!/bin/sh
DEPLOYMENT=framework
MOUDLE=centit-ip-demo
TIME='date +%Y%m%d%H%M'
GIT_REVISION='git log -1 --pretty=format:"%h"'
IMAGE_NAME=172.29.0.13:8082/${DEPLOYMENT}:${TIME}_${GIT_REVISION}
#编译
cd ./${MODULE}/
mvn clean package -DskipTests=true
#写入dockerfile文件
cat >./${MODULE}/dockerfile <<EOF
FROM tomcat
MAINTAINER hzf "hzf@centit.com"
ADD target/*.war /usr/local/tomcat/webapps/{DEPLOYMENT}.war
EXPOSE 8080
CMD /usr/local/tomcat/bin/startup.sh && tail -f /usr/local/tomcat/logs/catalina.out
EOF
#构建镜像
cd ./${MODULE}/
docker build -t ${IMAGE_NAME} .
#上传nexus
docker login -u developer -p centit 172.29.0.13:8082
docker push ${IMAGE_NAME}
#运行
docker run -d --network host -p 12000:8080 --name ${DEPLOYMENT} ${IMAGE_NAME}