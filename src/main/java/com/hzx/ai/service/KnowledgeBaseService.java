package com.hzx.ai.service;


import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zexiao.huang
 * @since 2025/7/5 21:46
 */

@Component // 交给 Spring 管理，方便注入
public class KnowledgeBaseService {

    @Resource
    private EmbeddingModel embeddingModel;
    @Resource
    private RedisEmbeddingStore redisEmbeddingStore;

    /**
     * 重建知识库（先删后建）
     */
    public EmbeddingStore<?> rebuildKnowledgeBase() {
        // 1. 删除原有知识库（根据实际 Redis 操作，这里假设 RedisEmbeddingStore 有清空方法）
        redisEmbeddingStore.removeAll();

        // 2. 重新加载文档
        List<Document> documents = ClassPathDocumentLoader.loadDocuments(
                "content/pdf",
                new ApachePdfBoxDocumentParser()
        );

        // 3. 重新构建向量数据库
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(500, 100))
                .embeddingModel(embeddingModel)
                .embeddingStore(redisEmbeddingStore)
                .build();
        ingestor.ingest(documents);

        return redisEmbeddingStore;
    }

    /**
     * 重建知识库（先删后建）
     */
    public EmbeddingStore<?> removeKnowledgeBase() {
        // 1. 删除原有知识库
        redisEmbeddingStore.removeAll();
        return redisEmbeddingStore;
    }
}
