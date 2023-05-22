FROM casstime/alpine-glibc-server-jre-8:latest

# 设定工作目录，后续操作都会在这个目录下（除非指定绝对路径）
WORKDIR /opt

# 引用构建参数
ARG APP_NAME

# 向容器中添加目录或文件
ADD target/${APP_NAME}.jar ${APP_NAME}.jar
