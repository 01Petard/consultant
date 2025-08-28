#!/bin/bash

# AI志愿填报顾问系统部署脚本

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查环境
check_requirements() {
    log_info "检查部署环境..."
    
    # 检查Docker
    if ! command -v docker &> /dev/null; then
        log_error "Docker未安装，请先安装Docker"
        exit 1
    fi
    
    # 检查Docker Compose
    if ! command -v docker-compose &> /dev/null; then
        log_error "Docker Compose未安装，请先安装Docker Compose"
        exit 1
    fi
    
    # 检查环境变量
    if [ -z "$OPENAI_API_KEY" ]; then
        log_warn "OPENAI_API_KEY环境变量未设置"
        read -p "请输入你的OpenAI API Key: " OPENAI_API_KEY
        export OPENAI_API_KEY
    fi
    
    log_info "环境检查完成"
}

# 构建应用
build_app() {
    log_info "构建Spring Boot应用..."
    
    # 清理并构建
    mvn clean package -DskipTests
    
    if [ $? -eq 0 ]; then
        log_info "应用构建成功"
    else
        log_error "应用构建失败"
        exit 1
    fi
}

# 部署应用
deploy_app() {
    log_info "部署应用..."
    
    # 停止现有服务
    docker-compose down
    
    # 构建并启动服务
    docker-compose up --build -d
    
    if [ $? -eq 0 ]; then
        log_info "应用部署成功"
    else
        log_error "应用部署失败"
        exit 1
    fi
}

# 健康检查
health_check() {
    log_info "执行健康检查..."
    
    # 等待服务启动
    sleep 30
    
    # 检查应用健康状态
    for i in {1..10}; do
        if curl -f http://localhost:8080/hello > /dev/null 2>&1; then
            log_info "应用健康检查通过"
            return 0
        fi
        log_warn "等待应用启动... ($i/10)"
        sleep 10
    done
    
    log_error "应用健康检查失败"
    return 1
}

# 显示部署信息
show_info() {
    log_info "部署完成！"
    echo ""
    echo "应用访问地址:"
    echo "  - 主页: http://localhost"
    echo "  - API: http://localhost/chat"
    echo "  - 健康检查: http://localhost:8080/hello"
    echo ""
    echo "服务状态:"
    docker-compose ps
    echo ""
    echo "查看日志:"
    echo "  docker-compose logs -f app"
}

# 清理函数
cleanup() {
    log_info "清理资源..."
    docker-compose down
    docker system prune -f
}

# 主函数
main() {
    case "$1" in
        "build")
            check_requirements
            build_app
            ;;
        "deploy")
            check_requirements
            build_app
            deploy_app
            health_check
            show_info
            ;;
        "stop")
            docker-compose down
            log_info "应用已停止"
            ;;
        "restart")
            docker-compose restart
            health_check
            log_info "应用已重启"
            ;;
        "logs")
            docker-compose logs -f app
            ;;
        "cleanup")
            cleanup
            ;;
        *)
            echo "使用方法: $0 {build|deploy|stop|restart|logs|cleanup}"
            echo ""
            echo "命令说明:"
            echo "  build   - 仅构建应用"
            echo "  deploy  - 完整部署（构建+部署+健康检查）"
            echo "  stop    - 停止应用"
            echo "  restart - 重启应用"
            echo "  logs    - 查看应用日志"
            echo "  cleanup - 清理Docker资源"
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@"
