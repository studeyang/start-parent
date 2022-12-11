FROM casstime/alpine-glibc-server-jre-8:latest

# 设定工作目录，后续操作都会在这个目录下（除非指定绝对路径）
WORKDIR /opt

# 向容器中添加目录或文件
ADD target/start-site-exec.jar start-site-exec.jar

RUN sed -i 's/dl-cdn.alpinelinux.org/repo.huaweicloud.com/g' /etc/apk/repositories && \
    apk add git=2.20.4-r0 && \
    git config --global user.email "start@yourcompany.com" && \
    git config --global user.name "start.yourcompany.com" && \
    git config --list

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "start-site-exec.jar"]