# AI志愿填报顾问系统 - 部署指南

## 🚀 GitHub Actions 自动部署

### 1. 准备工作

#### 1.1 创建GitHub仓库
```bash
# 初始化Git仓库
git init
git add .
git commit -m "Initial commit: AI志愿填报顾问系统"

# 添加远程仓库
git remote add origin https://github.com/your-username/ai-consultant.git
git branch -M main
git push -u origin main
```

#### 1.2 配置GitHub Secrets
在GitHub仓库的 `Settings > Secrets and variables > Actions` 中添加以下secrets：

**Docker相关:**
- `DOCKER_USERNAME`: Docker Hub用户名
- `DOCKER_PASSWORD`: Docker Hub密码或访问令牌

**API密钥:**
- `OPENAI_API_KEY`: 阿里云百炼API密钥

**Staging环境:**
- `STAGING_HOST`: 测试服务器IP地址
- `STAGING_USER`: 测试服务器用户名
- `STAGING_SSH_KEY`: 测试服务器SSH私钥
- `STAGING_DB_URL`: 测试环境数据库连接URL
- `STAGING_DB_USER`: 测试环境数据库用户名
- `STAGING_DB_PASSWORD`: 测试环境数据库密码
- `STAGING_REDIS_HOST`: 测试环境Redis主机
- `STAGING_REDIS_PASSWORD`: 测试环境Redis密码

**Production环境:**
- `PROD_HOST`: 生产服务器IP地址
- `PROD_USER`: 生产服务器用户名
- `PROD_SSH_KEY`: 生产服务器SSH私钥
- `PROD_DB_URL`: 生产环境数据库连接URL
- `PROD_DB_USER`: 生产环境数据库用户名
- `PROD_DB_PASSWORD`: 生产环境数据库密码
- `PROD_REDIS_HOST`: 生产环境Redis主机
- `PROD_REDIS_PASSWORD`: 生产环境Redis密码

### 2. 自动部署流程

#### 2.1 触发条件
- 推送到 `main` 或 `master` 分支
- 创建Pull Request到 `main` 或 `master` 分支

#### 2.2 部署阶段
1. **测试阶段**: 运行单元测试和集成测试
2. **构建阶段**: 编译Java代码，打包JAR文件
3. **Docker构建**: 构建Docker镜像并推送到Docker Hub
4. **Staging部署**: 自动部署到测试环境
5. **Production部署**: 手动确认后部署到生产环境

### 3. 环境配置

#### 3.1 GitHub Environments
在GitHub仓库中配置两个环境：
- `staging`: 测试环境，自动部署
- `production`: 生产环境，需要手动确认

#### 3.2 服务器准备
确保目标服务器已安装：
- Docker
- Docker Compose
- 必要的防火墙规则

## 🐳 Docker 本地部署

### 1. 快速开始
```bash
# 设置API密钥
export OPENAI_API_KEY="your-api-key-here"

# 一键部署
./deploy.sh deploy
```

### 2. 分步部署
```bash
# 1. 构建应用
./deploy.sh build

# 2. 启动服务
docker-compose up -d

# 3. 查看状态
docker-compose ps

# 4. 查看日志
./deploy.sh logs
```

### 3. 管理命令
```bash
# 停止服务
./deploy.sh stop

# 重启服务
./deploy.sh restart

# 清理资源
./deploy.sh cleanup
```

## 🔧 手动部署

### 1. 环境要求
- JDK 17+
- Maven 3.8.1+
- MySQL 8.0+
- Redis 6.0+

### 2. 构建应用
```bash
mvn clean package -DskipTests
```

### 3. 配置数据库
```sql
CREATE DATABASE volunteer CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 4. 启动应用
```bash
java -jar target/consultant-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --spring.datasource.url=jdbc:mysql://localhost:3306/volunteer \
  --spring.datasource.username=root \
  --spring.datasource.password=your_password \
  --spring.data.redis.host=localhost \
  --langchain4j.open-ai.chat-model.api-key=your_api_key
```

## 🌐 Nginx 反向代理

### 1. 安装Nginx
```bash
# Ubuntu/Debian
sudo apt update && sudo apt install nginx

# CentOS/RHEL
sudo yum install nginx
```

### 2. 配置Nginx
```bash
# 复制配置文件
sudo cp nginx.conf /etc/nginx/sites-available/ai-consultant
sudo ln -s /etc/nginx/sites-available/ai-consultant /etc/nginx/sites-enabled/

# 测试配置
sudo nginx -t

# 重启Nginx
sudo systemctl restart nginx
```

## 📊 监控和日志

### 1. 应用监控
- 健康检查: `http://your-domain/hello`
- 指标监控: `http://your-domain:8080/actuator/metrics`
- Prometheus: `http://your-domain:8080/actuator/prometheus`

### 2. 日志查看
```bash
# Docker环境
docker-compose logs -f app

# 本地环境
tail -f logs/app.log
```

### 3. 性能监控
建议集成以下监控工具：
- Prometheus + Grafana
- ELK Stack (Elasticsearch + Logstash + Kibana)
- APM工具 (如New Relic, Datadog)

## 🔒 安全配置

### 1. 环境变量
不要在代码中硬编码敏感信息，使用环境变量：
```bash
export SPRING_DATASOURCE_PASSWORD="your_secure_password"
export LANGCHAIN4J_OPENAI_CHATMODEL_APIKEY="your_api_key"
```

### 2. 防火墙配置
```bash
# 只开放必要端口
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 22/tcp
sudo ufw enable
```

### 3. SSL证书
使用Let's Encrypt获取免费SSL证书：
```bash
sudo certbot --nginx -d your-domain.com
```

## 🚨 故障排除

### 1. 常见问题
- **端口冲突**: 检查8080端口是否被占用
- **数据库连接失败**: 验证数据库配置和网络连接
- **Redis连接失败**: 检查Redis服务状态
- **API密钥错误**: 验证阿里云百炼API密钥

### 2. 日志分析
```bash
# 查看应用启动日志
docker-compose logs app | grep -i error

# 查看数据库连接日志
docker-compose logs mysql

# 查看Redis日志
docker-compose logs redis
```

### 3. 性能调优
```bash
# 调整JVM参数
export JAVA_OPTS="-Xmx2048m -Xms1024m -XX:+UseG1GC"

# 调整数据库连接池
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

## 📞 技术支持

如果遇到部署问题，请：
1. 查看应用日志
2. 检查GitHub Actions构建日志
3. 验证服务器环境配置
4. 提交Issue到GitHub仓库
