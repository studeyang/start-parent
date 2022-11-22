# 一、简介

本工程基于 https://github.com/spring-io/start.spring.io 开发的（下面简称 start.io）。与 start.spring.io 的主要区别是，start.io 增加了分层应用架构，整合了公司自己的组件库，并且新开发了【一键运行】功能。

界面预览图：

【主界面】

![image-20221020143215030](https://technotes.oss-cn-shenzhen.aliyuncs.com/2022/image-20221020143215030.png)

【依赖管理】

![image-20221020143308373](https://technotes.oss-cn-shenzhen.aliyuncs.com/2022/image-20221020143308373.png)

【代码预览】

![image-20221020143350663](https://technotes.oss-cn-shenzhen.aliyuncs.com/2022/image-20221020143350663.png)

# 二、构建

**步骤一：mvn deploy start-client**

由于 jenkins 服务器在 mvn install start-client 的时候有问题，需要先把 start-client.jar 打包并推到 maven 仓库。

> 但我本地环境可以打包。
>
> jenkins 服务器提示 node 版本要高于 v12，升级后仍然不行。怀疑是 python 的版本问题。

**步骤二：git push**

添加了 push hooks, push 代码是自动触发打包镜像脚本。

**步骤三：kubernetes 重启 start-site 服务**

# 三、设计与实现

## 3.1 工程结构

```
start-parent
  |- initializr                    代码生成
    |- initializr-actuator
    |- initializr-bom
    |- initializr-docs
    |- initializr-generator         生成基础工程代码
    |- initializr-generator-spring  生成 spring 工程代码
    |- initializr-generator-test    单元测试的封装
    |- initializr-generator-zebra   生成 zebra 分层架构
    |- initializr-metadata          工程元数据（pom 相关定义）
    |- initializr-parent
    |- initializr-service-sample
    |- initializr-version-resolver  版本解析
    |- initializr-web
  |- start-client                   start.io 前端
  |- start-site                     start.io 后端
```

工程依赖关系图：

![image-20221020152301967](https://technotes.oss-cn-shenzhen.aliyuncs.com/2022/image-20221020152301967.png)

## 3.2 功能剖析：【获取代码】

```
接口：/starter.zip

ProjectGenerationController # springZip()
-> DefaultProjectAssetGenerator # generate() L56
```

**问1：buildItemResolver 这个 bean 是从哪里注入的？**

![image-20221020155357604](https://technotes.oss-cn-shenzhen.aliyuncs.com/2022/image-20221020155357604.png)

答：

![image-20221020155426170](https://technotes.oss-cn-shenzhen.aliyuncs.com/2022/image-20221020155426170.png)

**问2：buildCustomizers 有哪些？**

```
0 = {SimpleBuildCustomizer@12094} 
1 = {ReactorTestBuildCustomizer@12095} 
2 = {SpringKafkaBuildCustomizer@12096} 
3 = {ObservabilityBuildCustomizer@12097} 
4 = {SpringCloudFunctionBuildCustomizer@12098} 
5 = {SpringCloudStreamBuildCustomizer@12099} 
6 = {SpringCloudCircuitBreakerBuildCustomizer@12100} 
7 = {SpringBootVersionRepositoriesBuildCustomizer@12101} 
8 = {ZebraProjectGenerationConfiguration$lambda@12102} 
9 = {ParentPomBuildCustomizer@12103} 
10 = {AnnotationProcessorExclusionBuildCustomizer@12104} 
11 = {DependencyManagementBuildCustomizer@12105} 
12 = {DefaultStarterBuildCustomizer@12106} 
```

**问3：分层架构的 contributor 有哪些？**

```
0 = {ZebraServiceStructureProjectContributor@11717} 
1 = {WebApplicationCodeProjectContributor@11718} 
2 = {OrderAppServiceCodeProjectContributor@11719} 
3 = {OrderBizServiceCodeProjectContributor@11720} 
4 = {OrderEntityCodeProjectContributor@11721} 
5 = {DockerfileProjectContributor@11722} 
6 = {OrderDtoCodeProjectContributor@11723} 
7 = {ModifyOrderRequestCodeProjectContributor@11724} 
8 = {MavenWrapperContributor@11725} 
9 = {HelpDocumentProjectContributor@11726} 
10 = {GitIgnoreContributor@11727} 
11 = {ZebraRootPomProjectContributor@11728} 
12 = {ApplicationYmlProjectContributor@11729} 
13 = {ZebraSpiStructureProjectContributor@11730} 
14 = {BuildDockerGroovyProjectContributor@11731} 
15 = {BuildProjectGroovyProjectContributor@11732} 
```

## 3.3 功能剖析：【一键运行】

**前端界面怎么实现的？**

框架：抖音 Semi

**gitlab 登录授权怎么实现的？**

![image-20221020171907903](https://technotes.oss-cn-shenzhen.aliyuncs.com/2022/image-20221020171907903.png)

**一键运行后端流程**

![image-20221020173624403](https://technotes.oss-cn-shenzhen.aliyuncs.com/2022/image-20221020173624403.png)

`createGitlabProjectProcessor`业务处理：

- 完成 `gitlab`工程的创建；
- 生成脚手架工程，并上传至`gitlab`；

`createDevopsProcessor`业务处理：

- 生成并上传工程服务部署模板；

`cicdTriggerProcessor`业务处理：

- 触发`PRECI`操作（后续操作由`jenkins`回调衔接）；

## 3.4 如何添加新依赖？

```yaml
initializr:
  dependencies:
    - name: 基础组件库
      bom: infra
      repository: my-rep
      content:
        - name: Example
          id: example
          groupId: com.dbses.open
          artifactId: example-spring-boot-starter
          description: 示例组件说明
          starter: true
          links:
            - rel: guide
              href: {用户手册}
              description: Example 快速开始
            - rel: reference
              href: {参考文档}
```

## 3.5 易用性优化

- 点击一键运行，Gitlab 授权后会中断该操作；
- 选择好依赖后，点击一键运行，会丢失所选依赖；
- 将创建的 Gitlab 工程同步到 Gerrit；
- 如果选择了 Panda 配置中心，可以创建相应配置；
- start-site 只能单节点部署；





