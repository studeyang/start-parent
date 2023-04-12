# 介绍

本工程基于 https://github.com/spring-io/start.spring.io 开发的，增加了分层应用架构，整合了公司自己的组件库，并且新开发了「创建工程」功能。界面预览图：

### 【主界面】

![主界面](docs/主界面.png)

### 【依赖管理】

![](docs/依赖管理.gif)

### 【代码预览】

![](docs/代码预览.gif)

### 【创建工程】

![](docs/创建工程.gif)



# 快速体验

```shell
# 下载镜像
docker pull studeyang/start-site:0.0.2
# 运行容器
docker run -d \
  -p 8080:8080 \
  -e security.client-id='326e1886e1af3ddc904233a559f5005cb75a4608277c806b0c7ca93ef6b331fa' \
  -e security.client-secret='aa03373ee6224f2c493d45167a2ac942957d4d4111f39edf2d14f57aadac9b3c' \
  --name=start-site \
  studeyang/start-site:0.0.2
```

浏览器打开地址`http://localhost:8080`即可访问。

# 环境搭建

详细文档：[环境搭建](./docs/环境搭建.md)

# 设计实现

详细文档：[设计实现](./docs/设计实现.md)

# 联系作者

![image-20221127122036490](docs/contact.png)
