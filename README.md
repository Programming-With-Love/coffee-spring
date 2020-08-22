# 概述

与spring boot完整集成的common包，约定优于配置，所有的项目的基础脚手架依赖包。

本项目的主要目标是：

* 提供*restful web*应用默认规则，主要以json作为序列化/反序列化方式
* 开箱即用
* 从框架层培养开发习惯，通过各种注解式工具在spring boot基础上使代码更加高内聚，低耦合
* 继承spring boot理念：完全没有代码生成，也不需要XML配置。

在使用本项目时，你应该同意以下观点：

* **习惯约定+注解**优于提供一堆工具类
* **可扩展性**优于不必要的效率优化（例如选择jackson而不是fastjson做为序列化工具)
* **restful** api接口优于后端（java）模板渲染
* [关于rest api请求响应的相关规则](./docs/rest.md)

## 优势

提供通用自动配置，完全零配置并提供高度扩展能力

* 自动配置的认证框架，包含登录/鉴权功能

* 为model层添加基础类，帮助model层开发

* json配置，自动根据profile配置json序列化方式，具体表现为`prod`环境下，null不参与序列化,属性不匹配会失败，节省带宽，提高返回速度
，其他环境下返回全属性以帮助更好的提示前端各种属性

* 全局异常自动捕捉并返回Result结果。业务异常务必继承`CommonBussinessException`，给予前端更好的提示处理

* 全局请求日志,帮助快速定位问题所在,日志示例：`Before request [uri=/test3/a?x=1&y=2;client=0:0:0:0:0:0:0:1;headers={host=[localhost:8080], user-agent=[curl/7.64.1], accept=[*/*]}]`

* 定义更多的校验注解，例如：`@Phone`

* 一些其他常用工具：分布式id生成器，分布式锁等

## 环境要求

* java 8+
* maven 3.3+
* gradle 5.x / 6.x

## 使用

为使框架尽可能轻量化，项目采用分包开发，尽可能进行单一功能的增强，鉴于开发者可能对某些模块的定制并不满意，你完全可以选择不引入相应功能。

为了保持Spring Boot使用习惯，本项目提供site.zido:coffee-spring-boot-parent包，放置于pom文件中的parent标签下，
它继承自Spring Boot Starter Parent主要用于维护版本及能够沿用它的相关插件设置，在其基础上增加coffee项目的版本依赖管理。
```xml
<parent>
    <groupId>site.zido</groupId>
    <artifactId>coffee-spring-boot-parent</artifactId>
    <version>{version}</version>
</parent>
```
当然，你也可以使用dependencyManager方式引入site.zido:coffee-dependencies包，用于管理依赖版本。

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <!-- Import dependency management from Spring Boot -->
            <groupId>site.zido</groupId>
            <artifactId>coffee-dependencies</artifactId>
            <version>${version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

当在需要引入项目时，可能会遇到两种情况：

#### 你完全认同本框架对于Spring的相应扩展能力（推荐）

直接引入`coffee-spring-boot-starter`模块。本模块完全采用探测型功能增强，
也即是说，它会在你引入相应模块时才为之生效，否则不产生任何副作用。示例如下：
```xml
<project>
    <parent>
        <groupId>site.zido</groupId>
        <artifactId>coffee-spring-boot-parent</artifactId>
        <version>{version}</version>
    </parent>
    <dependencies>
        <dependency>
            <groupId>site.zido</groupId>
            <artifactId>coffee-spring-boot-starter</artifactId>
            <version>{version}</version>
        </dependency>
        <!--增强Spring Mvc,添加全局统一返回，异常处理等逻辑-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!--增强Spring Security，增加Restful Api支持，增加jwt支持，添加手机号登陆等功能支持-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
    </dependencies>
</project>
```
#### 你认同部分扩展能力 （推荐）
可以分别使用各个模块为你提供相关的功能

| 模块名               | 描述                                                                                                  |
| -------------------- | ----------------------------------------------------------------------------------------------------- |
| coffee-starter-web        | 基本通用包，包含异常封装，响应体封装，json自动配置，请求日志自动配置等，其他模块均会自动引入该模块    |
| coffee-starter-rest-security | rest api认证框架，基于spring security封装，在spring security基础上提供rest api支持，默认使用jwt token |
| coffee-starter-extra         | 提供诸多常用注解式开发工具，包含注解式分布式锁/限流器/防重放等功能                                    |

将`spring-boot-starter-xxx`替换为`coffee-starter-xxx`即可，**不需要再添加对应spring boot模块**，这些模块本身附带有对应依赖。
```xml
<project>
    <parent>
        <groupId>site.zido</groupId>
        <artifactId>coffee-spring-boot-parent</artifactId>
        <version>{version}</version>
    </parent>
    <dependencies>
        <!--增强Spring Mvc,添加全局统一返回，异常处理等逻辑-->
        <dependency>
            <groupId>site.zido</groupId>
            <artifactId>coffee-starter-web</artifactId>
            <version>{version}</version>
        </dependency>
        <!--增强Spring Security，增加Restful Api支持，增加jwt支持，添加手机号登陆等功能支持-->
        <dependency>
            <groupId>site.zido</groupId>
            <artifactId>coffee-starter-rest-security</artifactId>
            <version>{version}</version>
        </dependency>
        <!--额外开发利器-->
        <dependency>
            <groupId>site.zido</groupId>
            <artifactId>coffee-starter-extra</artifactId>
            <version>{version}</version>
        </dependency>
    </dependencies>
</project>
```

## 认证模块 (coffee-auth)

可实现几乎0配置的自动认证模块，基于spring security的自动化配置，
并添加了spring security曾经不（完全）具有的rest能力，使用jwt作为token规范，
可自行扩展使用其他token规则

自带登录模块：

用户名密码登录(spring security自带，并加入rest相关自动配置)
手机号验证码登录(需自行配置)

### 使用

与spring security完全相同的配置，只需要将spring security的@EnableWebSecurity注解换成@EnableRestSecurity
即可，不改变其他任何使用方式。

如果需要自定义配置，你需要继承RestSecurityConfigurationAdapter
而不是Spring security的WebSecurityConfigurerAdapter

RestSecurityConfigurationAdapter去除了其他rest不需要的配置，新增了适合rest风格的自动配置

除此之外，其他使用与[spring security官方文档](https://spring.io/projects/spring-security)完全相同

## 注解式限流 (coffee-extra)

如需启用需使用 @EnableLimiter 注解

使用场景例如：手机号发送短信验证码一分钟不能超过一次(建议时间设置比实际情况略小

在需要限流的方法上加入注解@Limiter

参数如下：

* key: 限流的key，支持spel表达式
* timeout: 超时时间，在超时时间内不允许同key第二次调用
* unit: 超时时间单位


## 可配置扩展点

### 禁用自动切换json序列化

```properties
site.zido.json.auto-switch.enable=false
```

### 自动配置限流器

使用redis进行限流处理

默认key前缀为`coffee:limiter:`

如需要定制前缀，在`application.properties`配置文件中使用`coffee.limiter.prefix=xxx`

在需要使用限流的方法上使用@Limiter注解。
...

待完成

