package com.itheima.consultant.aiservice;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,//手动装配
        chatModel = "openAiChatModel",//指定模型
        streamingChatModel = "openAiStreamingChatModel",
        //chatMemory = "chatMemory",//配置会话记忆对象
        chatMemoryProvider = "chatMemoryProvider",//配置会话记忆提供者对象
        contentRetriever = "contentRetriever",//配置向量数据库检索对象
        tools = "reservationTool"
)
//@AiService
public interface ConsultantService {
    //用于聊天的方法
    //public String chat(String message);
    //@SystemMessage("你是东哥的助手小月月,人美心善又多金!")
    @SystemMessage(fromResource = "system.txt")
    //@UserMessage("你是东哥的助手小月月,人美心善又多金!{{it}}")
    //@UserMessage("你是东哥的助手小月月,人美心善又多金!{{msg}}")
    public Flux<String> chat(/*@V("msg")*/@MemoryId String memoryId, @UserMessage String message);
}
