# Coffee-Spring-Boot

<p align="center">
  <a href="https://github.com/zidoshare/coffee-spring-boot-builder"><img alt="Travis Build Status" src="https://travis-ci.org/zidoshare/coffee-spring-boot-builder.svg?branch=master"></a>
  <a href="https://github.com/zidoshare/coffee-spring-boot-builder/blob/master/LICENSE"><img alt="MIT LICENCE" src="https://img.shields.io/cocoapods/l/Kingfisher.svg?style=flat"></a>
  <a href="https://github.com/zidoshare/coffee-spring-boot-builder"><img alt="LANGUAGE JAVA" src="https://img.shields.io/badge/language-java-orange.svg"></a>
</p>
<p align="center">
  <a href="https://github.com/zidoshare/coffee-spring-boot-builder"><img alt="JDK 1.8+" src="https://img.shields.io/badge/JDK-1.8+-green.svg"></a>
  <a href="https://github.com/zidoshare/coffee-spring-boot-builder"><img alt="JDK 1.8+" src="https://img.shields.io/badge/Spring%20Boot-2.2.1.RELEASE-blue.svg"></a>
</p>

> spring 5.x / spring boot 2.x的restful启动器，节省一杯咖啡的时间，更多的约定，更快的开发

它承袭Spring/Spring Boot的开发理念，在原Spring Boot基础上做更进一步的约定但又尽可能的不为开发者带来配置负担。

旨在提升小型团队的开发输出能力，**不依赖模板代码**，**不依赖代码生成**的脚手架。

## 优势

* 提供*restful web*应用默认规则

* 开箱即用

* 从框架层培养开发习惯，通过各种注解式工具在spring boot基础上使代码更加高内聚，低耦合

* 继承spring boot理念：基于自动配置，完全没有代码生成，也不需要XML配置。

* 自动配置的认证框架，包含账号密码（手机号验证码）登录/鉴权（全注解鉴权）。

* 全局异常自动捕捉并返回约定响应结果。

* 全局请求日志,帮助快速定位问题所在。

* 定义更多的校验注解，例如：`@Phone`

* 其他常用工具集成

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

## WebMvc模块

为spring web mvc 提供了一系列定制，并为其他模块提供自适应的相关配置。

主要包括：

### 全局统一返回对象

不需要为返回结果添加统一工具类，形如`Result.success(data)`形式。

不需要为统一异常封装而发愁，自行封装还可能漏掉某些异常，导致前端一脸懵逼。

如果未使用自动配置，则可以使用`@EnableGlobalResult`注解标记到Configuration中，从而开启全局统一返回处理,自动封装你的响应数据，
否则在自动配置的规则中，全局统一封装会自动开启，可以通过`spring.coffee.web.global-result=false`来关闭。

从此你的controller方法只需要直接返回业务数据，不需要再进行任何封装，并且可以统一直接修改响应类型。

全局统一返回对象默认为`site.zido.coffee.mvc.rest.Result`。

在开启全局统一返回后，框架会做出如下自适应：

* 封装RestController注解和ResponseBody的返回结果，自动包装为统一返回对象
* 封装异常响应结果，注意对于字段校验异常，只返回遇到的第一个异常封装字段
* 如果你的返回类型已经是封装对象，不做任何封装
* 有时可能会需要返回原对象，可以使用@OriginalResponse注解，如果作用于类上，则所有返回加过不封装。

如果需要返回错误，那么你可以根据异常进行分类，框架提供`CommonBusinessException`代表业务异常，继承它，能得到更加完善的结果并自动加入业务日志中。

```java
public class UnknownException extends CommonBusinessException {
    public UnknownException() {
        super(400, "发生意料之外的错误");
    }
}
```

如果你对返回结果有定制需求，可以实现`HttpResponseBodyFactory`,如下：

```java
public class DefaultHttpResponseBodyFactory implements HttpResponseBodyFactory {
    @Override
    public boolean isExceptedClass(Class<?> clazz) {
        return Result.class.isAssignableFrom(clazz);
    }

    @Override
    public Object success(Object data) {
        return Result.success(data);
    }

    @Override
    public Object error(int code, String message, Object data) {
        return Result.error(code, message, data);
    }
}
```

并作为Bean返回，框架会自动迁移到对应的factory中。

### 全局统一异常处理

全部统一异常处理会使用`HttpResponseBodyFactory`包装异常返回，它跟随全局统一返回处理一起开启。

对于校验类型的异常，默认只显示第一个

### 全局日志



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