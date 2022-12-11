#!/bin/sh

# 打包 initializr
cd initializr
sudo sh ../mvnw clean install --settings ../.mvn/settings.xml -Dmaven.test.skip=true
if [ $? != 0 ]; then
  curl -d '{"msgtype": "text", "text": {"content": "[1] package initialize fail!"}}' -H 'Content-Type: application/json' -X POST 'https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=857730edxx'
  exit 1
fi

# 打包 start-site
# ./mvnw clean install --settings D:/dev/apache-maven-3.5.0/conf/settings.xml -Dmaven.test.skip=true
cd ../start-site
sudo sh ../mvnw clean install --settings ../.mvn/settings.xml -Dmaven.test.skip=true
if [ $? != 0 ]; then
  curl -d '{"msgtype": "text", "text": {"content": "[2] package start-site fail!"}}' -H 'Content-Type: application/json' -X POST 'https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=857730edxx'
  exit 2
fi

# 打镜像
docker rmi studeyang/start-site:0.0.2
docker build -t studeyang/start-site:0.0.2 .
docker login -u {usr} -p {pwd} swr.cn-south-1.myhuaweicloud.com
docker push studeyang/start-site:0.0.2

# run
docker run -d \
  -p 8080:8080 \
  -e security.client-id='326e1886e1af3ddc904233a559f5005cb75a4608277c806b0c7ca93ef6b331fa' \
  -e security.client-secret='aa03373ee6224f2c493d45167a2ac942957d4d4111f39edf2d14f57aadac9b3c' \
  --name=start-site \
  studeyang/start-site:0.0.2

if [ $? != 0 ]; then
  curl -d '{"msgtype": "text", "text": {"content": "[3] build docker fail!"}}' -H 'Content-Type: application/json' -X POST 'https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=857730edxx'
  exit 3
fi

# 机器人通知
curl -d '{"msgtype": "text", "text": {"content": "build success!"}}' -H 'Content-Type: application/json' -X POST 'https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=857730edxx'