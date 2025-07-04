# SIP配置使用说明

## 概述

本项目提供了完整的SIP配置管理功能，包括GB28181配置和配置验证。主要包含以下组件：

- `Gb28181Properties`: GB28181协议相关配置
- `SipConfigValidator`: SIP配置验证器
- `SipConfigurationManager`: 统一配置管理器

## 配置类说明

### 1. Gb28181Properties

GB28181协议配置类，支持外部化配置。

**配置前缀**: `sip.gb28181`

**主要配置项**:

- `server`: 服务器配置（IP、端口、最大设备数等）
- `client`: 客户端配置（心跳间隔、重试次数等）
- `performance`: 性能配置（线程池、队列大小等）
- `cache`: 缓存配置（设备缓存、订阅缓存等）

### 2. SipConfigValidator

SIP配置验证器，用于验证配置参数的合理性和有效性。

**验证内容**:

- 异步配置验证（线程池参数、超时时间等）
- 连接池配置验证（连接数、超时时间等）
- 配置兼容性检查

### 3. SipConfigurationManager

统一配置管理器，整合所有SIP相关配置。

**功能**:

- 统一访问所有配置
- 配置摘要生成
- 配置重新验证
- 启动时自动验证和打印配置

## 使用方法

### 1. 基本配置

在 `application.yml` 中添加配置：

```yaml
sip:
  gb28181:
    server:
      ip: "0.0.0.0"
      port: 5060
      maxDevices: 10000
    client:
      clientId: "34020000001320000001"
      username: "admin"
      password: "123456"
    performance:
      enableAsync: true
      threadPoolSize: 200
  async:
    enabled: true
    corePoolSize: 4
    maxPoolSize: 8
  pool:
    enabled: true
    maxConnections: 100
```

### 2. 在代码中使用配置

```java
@Autowired
private SipConfigurationManager configManager;

@PostConstruct
public void init() {
    // 获取GB28181配置
    Gb28181Properties gbConfig = configManager.getGb28181Properties();

    // 获取异步配置
    SipAsyncConfig asyncConfig = configManager.getAsyncConfig();

    // 检查配置是否有效
    if (configManager.isConfigurationValid()) {
        log.info("配置验证通过");
    }

    // 打印配置摘要
    log.info(configManager.getConfigurationSummary());
}
```

### 3. 配置验证

配置验证会在应用启动时自动执行，如果发现配置问题会抛出异常：

```java
// 手动重新验证配置
configManager.revalidateAllConfigs();
```

## 配置验证规则

### GB28181配置验证

- 服务器端口必须在1-65535范围内
- 最大设备数必须大于0
- 重试次数不能为负数
- 线程池大小必须大于0
- 消息队列大小必须大于0
- 缓存大小必须大于0

### 异步配置验证

- 核心线程数必须大于0
- 最大线程数不能小于核心线程数
- 队列容量必须大于0
- 线程存活时间必须大于0
- 异步处理超时时间必须大于0
- 线程名前缀不能为空

### 连接池配置验证

- 最大连接数必须大于0
- 核心连接数不能小于0且不能大于最大连接数
- 最大空闲连接数必须大于0
- 各种超时时间必须大于0
- 验证查询不能为空

## 启动日志示例

应用启动时会自动打印配置摘要：

```
2024-01-01 10:00:00 [main] INFO  SipConfigurationManager - SIP配置管理器初始化完成
2024-01-01 10:00:00 [main] INFO  SipConfigurationManager - === SIP配置摘要 ===
GB28181配置:
  服务器: 0.0.0.0:5060
  客户端ID: 34020000001320000001
  异步处理: 启用
  监控: 启用

异步配置:
  异步处理: 启用 (核心线程=4, 最大线程=8, 队列=1000)
  连接池: 启用 (最大连接=100, 核心连接=10, 最大空闲=5)
```

## 注意事项

1. **配置优先级**: 外部配置文件 > 默认值
2. **验证时机**: 应用启动时自动验证，配置错误会导致启动失败
3. **线程安全**: 配置类在启动后是只读的，线程安全
4. **热更新**: 当前版本不支持配置热更新，需要重启应用

## 故障排除

### 常见配置错误

1. **端口冲突**: 确保配置的端口未被其他服务占用
2. **内存不足**: 根据服务器资源调整线程池和连接池大小
3. **超时设置**: 根据网络环境调整各种超时时间

### 调试配置

启用DEBUG日志查看详细配置信息：

```yaml
logging:
  level:
    io.github.lunasaw.sip: DEBUG
    io.github.lunasaw.gb28181: DEBUG
```