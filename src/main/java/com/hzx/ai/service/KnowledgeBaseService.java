package com.hzx.ai.service;


import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 知识库管理
 * @author zexiao.huang
 * @since 2025/7/5 21:46
 */
@Slf4j
@Component
public class KnowledgeBaseService {

    private static final String LAST_REBUILD_KEY = "knowledge:lastRebuildTime";

    @Resource
    private EmbeddingModel embeddingModel;
    @Resource
    private RedisEmbeddingStore redisEmbeddingStore;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 系统启动时检测知识库状态，构建向量数据库操作对象
     * 注册为Bean后会自动进行加载，推荐第一次启用，或通过接口来控制，不然数据库会被塞爆的
     * embeddingStore的对象, 这个对象的名字不能重复,所以这里使用store
     */
    @PostConstruct
    public void init() {
        log.info("【知识库检测】开始检查知识库状态...");
        try {
            Set<String> keys = stringRedisTemplate.keys("embedding:*");
            boolean hasKnowledgeBase = keys != null && !keys.isEmpty();
            log.info("【知识库检测】知识库是否存在：{}", hasKnowledgeBase);
            LocalDateTime lastRebuildTime = getLastRebuildTime();
            if (!hasKnowledgeBase) {
                // 重建：知识库不存在
                log.info("【知识库检测】未发现知识库，正在初始化...");
                rebuildKnowledgeBase();
            } else if (lastRebuildTime == null || Duration.between(lastRebuildTime, LocalDateTime.now()).toDays() >= 7) {
                // 重建：知识库存在，但是已超过7天
                log.info("【知识库检测】已存在，但上次重建超过 7 天，正在重建...");
                rebuildKnowledgeBase();
            } else {
                // 不重建：知识库存在，且不超过7天
                log.info("【知识库检测】存在且未超过 7 天，无需重建。");
            }
        } catch (Exception e) {
            log.error("【知识库检测】初始化失败！请检查 Redis 或重建逻辑", e);
            // 如果有必要，可以记录报警或者发送通知
        } finally {
            log.info("【知识库检测】重建检测结束。");
        }
    }


    /**
     * 重建知识库（先删后建）
     */
    public EmbeddingStore<?> rebuildKnowledgeBase() {
        // 1. 删除原有知识库
        redisEmbeddingStore.removeAll();

        // 2. 重新加载文档
        List<Document> documents = ClassPathDocumentLoader.loadDocuments(
                "content/pdf", new ApachePdfBoxDocumentParser()
        );

        // 3. 重新构建向量数据库
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(500, 100))
                .embeddingModel(embeddingModel)
                .embeddingStore(redisEmbeddingStore)
                .build();
        ingestor.ingest(documents);

        // 4. 更新重建时间
        updateLastRebuildTime(LocalDateTime.now());

        return redisEmbeddingStore;
    }

    /**
     * 删除知识库
     */
    public EmbeddingStore<?> removeKnowledgeBase() {
        redisEmbeddingStore.removeAll();
        return redisEmbeddingStore;
    }

    // ------------------ 工具方法 ------------------

    private void updateLastRebuildTime(LocalDateTime time) {
        stringRedisTemplate.opsForValue().set(LAST_REBUILD_KEY, time.toString());
    }

    private LocalDateTime getLastRebuildTime() {
        String value = stringRedisTemplate.opsForValue().get(LAST_REBUILD_KEY);
        if (value == null) return null;
        return LocalDateTime.parse(value);
    }
}
