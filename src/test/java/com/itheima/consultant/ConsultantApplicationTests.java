package com.itheima.consultant;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ConsultantApplicationTests {

    @Test
    void contextLoads() {
        // 这个测试确保Spring上下文能够正常加载
        // 这是一个基础的集成测试
    }

    @Test
    void applicationStartsSuccessfully() {
        // 验证应用能够成功启动
        // 这个测试会验证所有的Bean都能正确创建
    }
}
