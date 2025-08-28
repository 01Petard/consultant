package com.hzx.ai.controller;

import com.hzx.ai.service.ai.ConsultantService;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * 对话管理
 * @author hzx
 */
@RestController
@CrossOrigin
public class ChatController {
    @Resource
    private ConsultantService consultantService;

//    @GetMapping(value = "/chat", produces = "text/html;charset=utf-8")
    @GetMapping(value = "/chat", produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> chat(String memoryId, String message) {
        return consultantService.chat(memoryId, message);
    }

    @GetMapping("/chat/block")
    public String chat2(String message) {
        return consultantService.chat(message);
    }

    @Resource
    private OpenAiChatModel model;

    @GetMapping("/chat/model")
    public String chat3(String message) {//浏览器传递的用户问题
        return model.chat(message);
    }
}
