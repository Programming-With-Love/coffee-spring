# 概述

与spring boot完整集成的common包，约定优于配置，所有的项目的基础脚手架依赖包。

## 优势

提供通用自动配置，完全零配置并提供高度扩展能力

* rest配置，能够正确的返回`site.zido.pojo.Result`封装的json，提供`OriginalResponse`注解以帮助返回原值。直接返回的方式可以让controller层更薄，更易于编写。

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

...

完毕

## 注解式限流

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

## TODO


* 注解式防重放攻击解决方案

* 权限管理框架集成（小程序和web通用的效率更高的解决方案），注解式鉴权，随时获取用户信息等功能


