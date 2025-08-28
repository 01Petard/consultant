package com.hzx.ai.config.repository;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

/**
 * 向量数据库存储对象
 * @author hzx
 */
@Repository
public class RedisChatMemoryStore implements ChatMemoryStore {

    @Resource
    private StringRedisTemplate ragRedisTemplate;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        //获取会话消息
        String json = ragRedisTemplate.opsForValue().get(memoryId);
        //把json字符串转化成List<ChatMessage>
        return ChatMessageDeserializer.messagesFromJson(json);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> list) {
        //更新会话消息
        //1.把list转换成json数据
        String json = ChatMessageSerializer.messagesToJson(list);
        //2.把json数据存储到redis中
        ragRedisTemplate.opsForValue().set(memoryId.toString(), json, Duration.ofDays(1));
    }

    @Override
    public void deleteMessages(Object memoryId) {
        ragRedisTemplate.delete(memoryId.toString());
    }
}
