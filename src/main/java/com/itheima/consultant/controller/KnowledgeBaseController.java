package com.itheima.consultant.controller;

import com.itheima.consultant.service.KnowledgeBaseService;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zexiao.huang
 * @since 2025/7/5 21:48
 */
@RestController
@CrossOrigin
public class KnowledgeBaseController {

    @Resource
    private KnowledgeBaseService knowledgeBaseService;

    /**
     * 重建知识库
     */
    @PostMapping(value = "/rebuild")
    public String rebuildKnowledgeBase() {
        try {
            EmbeddingStore<?> store = knowledgeBaseService.rebuildKnowledgeBase();
            // 返回简单响应，也可根据需求返回更详细的信息（如向量数量等）
            return "知识库重建成功！当前向量数量：" + store.toString().length();
        } catch (Exception e) {
            // 捕获异常，返回错误信息
            return "知识库重建失败：" + e.getMessage();
        }
    }

    /**
     * 删除知识库
     */
    @PostMapping(value = "/remove")
    public String removeKnowledgeBase() {
        try {
            EmbeddingStore<?> store = knowledgeBaseService.removeKnowledgeBase();
            // 返回简单响应，也可根据需求返回更详细的信息（如向量数量等）
            return "知识库删除成功！当前向量数量：" + store.toString().length();
        } catch (Exception e) {
            // 捕获异常，返回错误信息
            return "知识库删除失败：" + e.getMessage();
        }
    }
}