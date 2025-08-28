package com.hzx.ai.config;

import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 对话记忆存储配置
 * @author hzx
 */
@Configuration
public class ChatMemoryConfig {


//    @Resource
//    private OpenAiChatModel model;
//
//    @Bean
//    public ConsultantService consultantService(){
//        return AiServices.builder(ConsultantService.class)
//                .chatModel(model)
//                .build();
//    }


    /**
     * 构建会话记忆对象
     * @return
     */
    @Bean
    public ChatMemory chatMemory(){
        return MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();
    }


    @Resource
    private ChatMemoryStore redisChatMemoryStore;

    /**
     * 构建ChatMemoryProvider对象
     * @return
     */
    @Bean
    public ChatMemoryProvider chatMemoryProvider(){
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(20)
                .chatMemoryStore(redisChatMemoryStore)
                .build();
    }

    @Resource
    private RedisEmbeddingStore redisEmbeddingStore;
    @Resource
    private EmbeddingModel embeddingModel;

    /**
     * 构建向量数据库检索对象
     * @return
     */
    @Bean
    public ContentRetriever contentRetriever() {
        return EmbeddingStoreContentRetriever.builder()
                .minScore(0.5)
                .maxResults(3)
                .embeddingModel(embeddingModel)
                .embeddingStore(redisEmbeddingStore)
                .build();
    }
}
