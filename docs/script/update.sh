#!/bin/bash

# 后端项目自动化部署脚本
# 应用名: baer-mes

# 定义颜色变量，用于日志输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

# 应用名称和 JAR 包路径
APP_NAME="baer-mes"
PROJECT_DIR="/var/www/${APP_NAME}"
JAR_NAME="${APP_NAME}-1.0.jar"
JAR_PATH="${PROJECT_DIR}/target/${JAR_NAME}"

echo -e "${YELLOW}===== 开始后端项目部署流程 =====${NC}"
echo -e "${YELLOW}项目目录: ${PROJECT_DIR}${NC}"
echo ""

# 步骤1: 拉取最新代码
echo -e "${YELLOW}--- 步骤1: 拉取最新代码 ---${NC}"
cd "$PROJECT_DIR"
if [ $? -ne 0 ]; then
    echo -e "${RED}错误: 无法切换到项目目录 ${PROJECT_DIR}${NC}"
    exit 1
fi

git pull
if [ $? -ne 0 ]; then
    echo -e "${RED}错误: git pull 执行失败，请检查网络或Git配置${NC}"
    exit 1
fi
echo -e "${GREEN}代码更新完成${NC}"
echo ""

# 步骤2: 使用 Maven 打包项目 (dev 环境)
echo -e "${YELLOW}--- 步骤2: 打包项目 (Maven -Pdev) ---${NC}"
mvn clean package -Pdev
if [ $? -ne 0 ]; then
    echo -e "${RED}错误: Maven 打包失败，请检查项目构建配置${NC}"
    exit 1
fi
echo -e "${GREEN}项目打包完成${NC}"
echo ""

# 步骤3: 停止正在运行的应用实例
echo -e "${YELLOW}--- 步骤3: 停止正在运行的实例 ---${NC}"
PID=$(ps -ef | grep java | grep ${APP_NAME} | grep -v grep | awk '{print $2}')
if [ -n "$PID" ]; then
    echo -e "${YELLOW}检测到运行中的实例，PID: $PID，尝试优雅终止...${NC}"
    kill -15 $PID
    sleep 10
else
    echo -e "${GREEN}没有正在运行的实例${NC}"
fi

# 步骤4: 强制清理残留进程（如有）
echo -e "${YELLOW}--- 步骤4: 清理残留进程 ---${NC}"
PID=$(ps -ef | grep java | grep ${APP_NAME} | grep -v grep | awk '{print $2}')
if [ -n "$PID" ]; then
    echo -e "${YELLOW}残留进程仍在运行，执行强制杀进程，PID: $PID${NC}"
    kill -9 $PID
    echo -e "${GREEN}残留进程已清理${NC}"
else
    echo -e "${GREEN}无残留进程${NC}"
fi
echo ""

# 步骤5: 启动新版本应用
echo -e "${YELLOW}--- 步骤5: 启动新版本应用 ---${NC}"
if [ ! -f "$JAR_PATH" ]; then
    echo -e "${RED}错误: 未找到Jar包: ${JAR_PATH}${NC}"
    exit 1
fi

echo "执行命令: nohup java -Xmx1g -Xms1g -XX:+UseG1GC -jar $JAR_PATH &"
nohup java -Xmx1g -Xms1g -XX:+UseG1GC -jar "$JAR_PATH" > /dev/null 2>&1 &
sleep 5

# 步骤6: 验证应用是否启动成功
echo -e "${YELLOW}--- 步骤6: 验证启动结果 ---${NC}"
PID=$(ps -ef | grep java | grep ${APP_NAME} | grep -v grep | awk '{print $2}')
if [ -n "$PID" ]; then
    echo -e "${GREEN}${APP_NAME} 启动成功，PID: $PID${NC}"
else
    echo -e "${RED}错误: ${APP_NAME} 启动失败，请检查日志文件${NC}"
    exit 1
fi

echo ""
echo -e "${YELLOW}===== 部署流程结束 =====${NC}"