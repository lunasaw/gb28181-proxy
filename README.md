# gb28181-proxy

[![Maven Central](https://img.shields.io/maven-central/v/io.github.lunasaw/luna-gb28181-proxy)](https://mvnrepository.com/artifact/io.github.lunasaw/gb28181-proxy-common)
[![GitHub license](https://img.shields.io/badge/MIT_License-blue.svg)](https://raw.githubusercontent.com/lunasaw/luna-gb28181-proxy/master/LICENSE)
[![Build Status](https://github.com/lunasaw/gb28181-proxy/actions/workflows/maven-publish.yml/badge.svg?branch=master)](https://github.com/lunasaw/gb28181-proxy/actions)

[www.isluna.ml](http://lunasaw.github.io)

基于sip实现gb28181的通信框架，区分client和server。以便于快速构建发起SIP请求和处理响应。项目不仅限于gb28181协议。也可以利用封装的SIP方法处理其他协议。

## 实现功能

- [x] SIP 通用请求构建
- [ ] springboot starter封装 
    - [x] 端口监听
        - [x] UDP 监听
        - [x] TCP 监听
    - [x] 基于javax的xml转化，写对象的方式写xml
- [x] Server
    - [x] GB28181
        - [x] 设备注册
        - [x] 设备认证
        - [x] 设备控制(PTZ)
            - [x] 云台控制
            - [x] 安放告警
            - [x] 设备查询
        - [ ] 实时点播
        - [ ] 视频回放点播
        - [ ] 设备移动订阅
- [x] Client
    - [x] 设备注册
    - [ ] 点播响应
    - [x] 设备移动
    - [x] 告警上报
    - [ ] 视频回放点播


# how to use

<a href="https://lunasaw.github.io/luna-gb28181-proxy/docs/" target="_blank">文档链接</a>

```xml
全量包
<dependency>
    <groupId>io.github.lunasaw</groupId>
    <artifactId>gb28181-proxy</artifactId>
    <version>${last.version}</version>
</dependency>

按需引入

基于sip的请求封装包
<dependency>
    <groupId>io.github.lunasaw</groupId>
    <artifactId>sip-common</artifactId>
    <version>${last.version}</version>
</dependency>

gb28181设备模拟client
<dependency>
    <groupId>io.github.lunasaw</groupId>
    <artifactId>gb28181-client</artifactId>
    <version>${last.version}</version>
</dependency>


sip服务器server
<dependency>
    <groupId>io.github.lunasaw</groupId>
    <artifactId>gb28181-server</artifactId>
    <version>${last.version}</version>
</dependency>
```

# 代码规范

- 后端使用同一份代码格式化膜模板ali-code-style.xml，ecplise直接导入使用，idea使用Eclipse Code Formatter插件配置xml后使用。
- 前端代码使用vs插件的Beautify格式化，缩进使用TAB
- 后端代码非特殊情况准守P3C插件规范
- 注释要尽可能完整明晰，提交的代码必须要先格式化
- xml文件和前端一样，使用TAB缩进
