# AI志愿填报顾问系统

## 项目简介
基于大语言模型的智能志愿填报咨询系统，集成RAG技术，提供以下功能：
- 🏫 院校简介查询
- 📋 录取规则查询
- 💰 奖学金设置查询
- 🏠 食宿条件查询
- 📞 招生联系方式查询
- 📊 2024年专业录取情况查询
- 🔥 热门专业查询
- ⚠️ 天坑专业查询
- 🎯 分数推荐学校专业（冲、稳、保策略）

## 技术栈
### 核心框架
- Spring Boot: 3.5.0
- LangChain4j: 1.0.1-beta6
- Java: 17

### AI相关技术
- LangChain4j OpenAI Spring Boot Starter
- LangChain4j Spring Boot Starter
- LangChain4j Reactor
- LangChain4j Easy RAG
- LangChain4j Apache PDFBox

### 数据存储
- MySQL: 关系型数据库
- Redis: 缓存和向量数据库
- LangChain4j Redis Community

### 其他技术
- MyBatis: 3.0.3
- Spring WebFlux
- Lombok
- Maven: 3.8.1

### AI模型
- 通义千问 (Qwen-Plus)
- Text-Embedding-V3

## 环境要求
### 开发环境
- JDK: 17+
- Maven: 3.8.1+
- MySQL: 8.0+
- Redis: 6.0+

### 系统兼容性
- macOS (M1/M2/M3芯片)
- Linux
- Windows

## 快速开始
### 1. 环境准备
安装JDK 17、Maven 3.8.1+、MySQL 8.0+、Redis 6.0+

### 2. 数据库配置
```sql
CREATE DATABASE volunteer CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 配置文件
修改`application-dev.yml`：
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/volunteer?serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: your_username
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password

langchain4j:
  open-ai:
    chat-model:
      base-url: https://dashscope.aliyuncs.com/compatible-mode/v1
      api-key: your_api_key
      model-name: qwen-plus
    embedding-model:
      base-url: https://dashscope.aliyuncs.com/compatible-mode/v1
      api-key: your_api_key
      model-name: text-embedding-v3
  community:
    redis:
      host: localhost
      port: 6379
```

### 4. 获取API密钥
1. 访问[阿里云百炼平台](https://dashscope.aliyun.com/)
2. 注册并获取API密钥
3. 配置到`application-dev.yml`

### 5. 启动项目
```bash
git clone <repository-url>
cd consultant
mvn clean compile
mvn spring-boot:run
```

### 6. 访问应用
- 应用地址: http://localhost:8080
- 聊天界面: http://localhost:8080/index.html
- API接口: http://localhost:8080/chat

## 项目结构
```
consultant/
├── src/main/java/com/itheima/consultant/
│   ├── ConsultantApplication.java          # 启动类
│   ├── aiservice/
│   │   └── ConsultantService.java          # AI服务接口
│   ├── config/
│   │   └── CommonConfig.java               # 配置类
│   ├── controller/
│   │   └── ChatController.java             # 控制器
│   ├── mapper/                             # MyBatis映射器
│   ├── pojo/                               # 实体类
│   ├── repository/                         # 数据仓库
│   ├── service/                            # 业务服务
│   └── tools/
│       └── ReservationTool.java            # 工具类
├── src/main/resources/
│   ├── content/                            # 知识库文档
│   │   ├── pdf/                            # PDF文档
│   │   └── txt/                            # 文本文档
│   ├── static/
│   │   └── index.html                      # 前端页面
│   ├── application.yml                     # 主配置文件
│   ├── application-dev.yml                 # 开发环境配置
│   └── system.txt                          # 系统提示词
└── pom.xml                                 # Maven配置
```

## 核心功能
### 1. 智能对话
- 通义千问大语言模型
- 流式响应支持
- 上下文记忆能力

### 2. 知识检索 (RAG)
- 向量存储院校信息
- 智能检索相关内容
- 提高回答准确性

### 3. 专业推荐
- 历年录取数据支撑
- 冲稳保策略推荐
- 个性化志愿方案

### 4. 多格式文档支持
- PDF文档解析
- 文本文档处理
- 自动切分与向量化

## API接口
### 聊天接口
```http
GET /chat?memoryId={memoryId}&message={message}
```
- `memoryId`: 会话ID，维持上下文
- `message`: 用户消息内容

### 健康检查
```http
GET /hello
```

## 部署说明
### 开发环境
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```

### 生产环境
```bash
mvn clean package -Pprod
java -jar target/consultant-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Docker部署
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/consultant-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 配置说明
### 模型配置
- 自定义模型API地址
- 可配置不同embedding模型
- 请求响应日志记录

### 内存配置
- Redis持久化会话记忆
- 会话窗口大小配置
- 多用户会话隔离

### 向量数据库配置
- Redis向量存储
- 相似度阈值配置
- 检索结果数量调整

## 常见问题
### Q: 如何添加新的知识文档？
A: 将文档放入`src/main/resources/content/`目录，系统自动处理。

### Q: 如何更换AI模型？
A: 修改配置文件中的`model-name`和`base-url`参数。

### Q: 如何调整回答质量？
A: 调整`minScore`和`maxResults`参数优化检索效果。

## 更新日志
### v0.0.1-SNAPSHOT
- 初始版本发布
- 基础AI对话功能
- RAG知识检索功能
- 志愿填报推荐功能