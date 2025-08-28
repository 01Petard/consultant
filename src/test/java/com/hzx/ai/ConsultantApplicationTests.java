package com.hzx.ai;

import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;

@SpringBootTest(classes = ConsultantApplication.class)
@Slf4j
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


    @Resource
    private RedisEmbeddingStore redisEmbeddingStore;

    @Test
    void test2() {
        int length = redisEmbeddingStore.toString().length();
        System.out.println(length);
    }

    @Resource
    private StringRedisTemplate ragRedisTemplate;

    @Test
    void test3() {
        Set<String> keys = ragRedisTemplate.keys("embedding:*");
        boolean hasKnowledgeBase = keys != null && !keys.isEmpty();
        System.out.println(hasKnowledgeBase);

    }
}
