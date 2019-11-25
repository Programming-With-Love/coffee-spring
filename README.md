# 概述

与spring boot完整集成的common包，约定优于配置，所有的项目的基础脚手架依赖包。

本项目主要是目标是在靠近**spring**技术栈的初衷下，
尽力以spring的行为方式用更少的依赖集成一些**小**项目的日常使用工具，
以帮助我们日常的小项目更快的开发。

在使用本项目时，你应该同意以下观点：

* **习惯约定+注解**优于提供一堆工具类
* **可扩展性**优于不必要的效率优化（例如选择jackson而不是fastjson做为序列化工具)
* **restful** api接口优于后端（java）模板渲染
* [关于rest api请求响应的相关规则](./docs/rest.md)

## 优势

提供通用自动配置，完全零配置并提供高度扩展能力

* 自动配置的认证框架，包含登录/鉴权功能

* json配置，自动根据profile配置json序列化方式，具体表现为`prod`环境下，null不参与序列化,属性不匹配会失败，节省带宽，提高返回速度
，其他环境下返回全属性以帮助更好的提示前端各种属性

* 全局异常自动捕捉并返回Result结果。业务异常务必继承`CommonBussinessException`，给予前端更好的提示处理

* 全局请求日志,帮助快速定位问题所在,日志示例：`Before request [uri=/test3/a?x=1&y=2;client=0:0:0:0:0:0:0:1;headers={host=[localhost:8080], user-agent=[curl/7.64.1], accept=[*/*]}]`

* 定义更多的校验注解，例如：`@Mobile`

一些其他常用工具：分布式id生成器，分布式锁等

## 使用

maven 加入依赖

```xml
<dependency>
    <groupId>site.zido</groupId>
    <artifactId>coffee-spring-boot-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>

```

可以分别使用各个模块为你提供相关的功能

| 模块名               | 描述                                                                                                  |
| -------------------- | ----------------------------------------------------------------------------------------------------- |
| coffee-common        | 基本通用包，包含异常封装，响应体封装，json自动配置，请求日志自动配置等，其他模块均会自动引入该模块    |
| coffee-rest-security | rest api认证框架，基于spring security封装，在spring security基础上提供rest api支持，默认使用jwt token |
| coffee-extra         | 提供诸多常用注解式开发工具，包含注解式分布式锁/限流器/防重放等功能                                    |

...

完毕

## 认证模块 (coffee-auth)

可实现几乎0配置的自动认证模块，基于spring security的自动化配置，
并添加了spring security曾经不（完全）具有的rest能力，使用jwt作为token规范，
可自行扩展使用其他token规则

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

