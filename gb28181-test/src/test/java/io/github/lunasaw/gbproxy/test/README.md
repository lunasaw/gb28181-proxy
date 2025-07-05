# SIP代理测试重构说明

## 测试结构重组

由于客户端和服务端功能混合，现将测试类拆分成独立的客户端和服务端测试，每个测试类专注于测试特定的命令交互。

## 目录结构

```
gb28181-test/src/test/java/io/github/lunasaw/gbproxy/test/
├── client/                          # 客户端测试类
│   ├── ClientRegisterTest.java      # 设备注册测试
│   ├── ClientHeartbeatTest.java     # 设备心跳测试
│   ├── ClientCatalogTest.java       # 设备目录查询测试
│   ├── ClientControlTest.java       # 设备控制响应测试
│   └── ClientInviteTest.java        # 设备点播响应测试
├── server/                          # 服务端测试类
│   ├── ServerRegisterTest.java      # 设备注册管理测试
│   ├── ServerHeartbeatTest.java     # 设备心跳监控测试
│   ├── ServerCatalogTest.java       # 设备目录查询测试
│   ├── ServerControlTest.java       # 设备控制命令测试
│   └── ServerInviteTest.java        # 设备点播命令测试
├── config/                          # 测试配置
├── register/                        # 原有注册测试（保留）
├── invite/                          # 原有点播测试（保留）
├── subscribe/                       # 原有订阅测试（保留）
└── README.md                        # 本说明文件
```

## 测试分类原则

### 客户端测试特点

- 模拟设备端行为
- 被动响应服务端命令
- 主动发送注册、心跳等消息
- 验证响应处理逻辑

### 服务端测试特点

- 模拟平台端行为
- 主动发送控制命令
- 被动接收设备响应
- 验证设备管理逻辑

## 测试执行顺序

### 客户端测试

1. `ClientRegisterTest` - 设备注册功能
2. `ClientHeartbeatTest` - 设备心跳功能
3. `ClientCatalogTest` - 设备目录查询功能
4. `ClientControlTest` - 设备控制响应功能
5. `ClientInviteTest` - 设备点播响应功能

### 服务端测试

1. `ServerRegisterTest` - 设备注册管理功能
2. `ServerHeartbeatTest` - 设备心跳监控功能
3. `ServerCatalogTest` - 设备目录查询功能
4. `ServerControlTest` - 设备控制命令功能
5. `ServerInviteTest` - 设备点播命令功能

## 配置说明

### 客户端测试配置

- 使用客户端相关配置参数
- 模拟设备端行为
- 验证设备端功能

### 服务端测试配置

- 使用服务端相关配置参数
- 模拟平台端行为
- 验证平台端功能

## 注意事项

1. 每个测试类都是独立的，可以单独运行
2. 测试类之间没有依赖关系
3. 使用 `@TestMethodOrder` 确保测试方法按顺序执行
4. 每个测试类都有完整的初始化和清理逻辑
5. 测试结果独立验证，便于问题定位