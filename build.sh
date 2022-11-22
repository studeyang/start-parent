#!/bin/sh

# 打包 initializr
cd initializr
sudo sh ../mvnw clean install --settings /etc/maven/settings.xml -Dmaven.test.skip=true
if [ $? != 0 ]; then
  curl -d '{"msgtype": "text", "text": {"content": "[1] package initialize fail!"}}' -H 'Content-Type: application/json' -X POST 'https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=857730edxx'
  exit 1
fi

# 打包 start-site
# ./mvnw clean install --settings D:/dev/apache-maven-3.5.0/conf/settings.xml -Dmaven.test.skip=true
cd ../start-site
sudo sh ../mvnw clean install --settings /etc/maven/settings.xml -Dmaven.test.skip=true
if [ $? != 0 ]; then
  curl -d '{"msgtype": "text", "text": {"content": "[2] package start-site fail!"}}' -H 'Content-Type: application/json' -X POST 'https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=857730edxx'
  exit 2
fi

# 打镜像
sudo docker rmi swr.cn-south-1.myhuaweicloud.com/yourcompany/start-site:0.0.2
sudo docker build -t swr.cn-south-1.myhuaweicloud.com/yourcompany/start-site:0.0.2 .
sudo docker login -u {usr} -p {pwd} swr.cn-south-1.myhuaweicloud.com
sudo docker push swr.cn-south-1.myhuaweicloud.com/yourcompany/start-site:0.0.2

if [ $? != 0 ]; then
  curl -d '{"msgtype": "text", "text": {"content": "[3] build docker fail!"}}' -H 'Content-Type: application/json' -X POST 'https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=857730edxx'
  exit 3
fi

# 机器人通知
curl -d '{"msgtype": "text", "text": {"content": "build success!"}}' -H 'Content-Type: application/json' -X POST 'https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=857730edxx'