# SipSender 重构说明

## 概述

`SipSender` 类已经进行了全面重构，采用了策略模式和建造者模式，提高了代码的抽象性和复用性。

## 重构内容

### 1. 策略模式

- 创建了 `SipRequestStrategy` 接口定义通用的请求发送模式
- 实现了各种具体的策略类：`MessageRequestStrategy`、`InviteRequestStrategy` 等
- 使用 `SipRequestStrategyFactory` 工厂类管理策略

### 2. 建造者模式

- 提供了 `SipRequestBuilder` 内部类，支持流式API
- 简化了请求构建和发送的过程

### 3. 职责分离

- `SipMessageTransmitter`：负责消息传输和事件订阅管理
- `SipTransactionManager`：负责事务管理
- `SipSender`：提供统一的API接口

## 使用方式

### 1. 新的流式API（推荐）

```java
// 发送MESSAGE请求
String callId = SipSender.request(fromDevice, toDevice, "MESSAGE")
                .content("Hello World")
                .errorEvent(errorEvent)
                .okEvent(okEvent)
                .send();

// 发送INVITE请求
String callId = SipSender.request(fromDevice, toDevice, "INVITE")
        .content(sdpContent)
        .subject("实时点播")
        .errorEvent(errorEvent)
        .okEvent(okEvent)
        .send();

// 发送SUBSCRIBE请求
String callId = SipSender.request(fromDevice, toDevice, "SUBSCRIBE")
        .content(content)
        .subscribeInfo(subscribeInfo)
        .errorEvent(errorEvent)
        .okEvent(okEvent)
        .send();

// 发送REGISTER请求
String callId = SipSender.request(fromDevice, toDevice, "REGISTER")
        .expires(3600)
        .errorEvent(errorEvent)
        .okEvent(okEvent)
        .send();
```

### 2. 兼容性API（保持向后兼容）

```java
// 原有的方法仍然可用
String callId = SipSender.doMessageRequest(fromDevice, toDevice, "Hello World");
String callId = SipSender.doInviteRequest(fromDevice, toDevice, sdpContent, "实时点播");
String callId = SipSender.doSubscribeRequest(fromDevice, toDevice, content, subscribeInfo);
String callId = SipSender.doRegisterRequest(fromDevice, toDevice, 3600);
```

### 3. 消息传输

```java
// 直接传输消息
SipSender.transmitRequest(ip, message);
SipSender.

transmitRequest(ip, message, errorEvent, okEvent);

// 或者使用新的传输器
SipMessageTransmitter.

transmitMessage(ip, message);
SipMessageTransmitter.

transmitMessage(ip, message, errorEvent, okEvent);
```

### 4. 事务管理

```java
// 获取服务器事务
ServerTransaction transaction = SipSender.getServerTransaction(request);
ServerTransaction transaction = SipTransactionManager.getServerTransaction(request, ip);
```

## 架构优势

### 1. 代码复用

- 通过策略模式，不同的请求类型共享相同的发送逻辑
- 减少了代码重复，提高了维护性

### 2. 扩展性

- 新增SIP方法只需要实现对应的策略类
- 支持自定义策略的注册和管理

### 3. 可读性

- 流式API使代码更加清晰易读
- 职责分离使每个类的功能更加明确

### 4. 向后兼容

- 保留了原有的所有公共方法
- 现有代码无需修改即可继续使用

## 策略扩展

### 添加新的SIP方法支持

```java
// 1. 实现策略接口
public class CustomRequestStrategy extends AbstractSipRequestStrategy {
    @Override
    protected Request buildRequest(FromDevice fromDevice, ToDevice toDevice, String content, String callId) {
        return SipRequestBuilderFactory.createCustomRequest(fromDevice, toDevice, content, callId);
    }
}

// 2. 注册策略
SipRequestStrategyFactory.

registerStrategy("CUSTOM",new CustomRequestStrategy());

// 3. 使用
String callId = SipSender.request(fromDevice, toDevice, "CUSTOM")
        .content(content)
        .send();
```

## 注意事项

1. 新的流式API提供了更好的类型安全和代码提示
2. 策略模式使得添加新的SIP方法变得简单
3. 所有原有功能都保持向后兼容
4. 建议在新代码中使用流式API，在维护现有代码时可以使用兼容性API