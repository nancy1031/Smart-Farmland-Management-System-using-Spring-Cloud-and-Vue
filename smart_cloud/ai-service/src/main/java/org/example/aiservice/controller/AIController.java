package org.example.aiservice.controller;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import org.example.aiservice.dto.ChatRequest;
import org.example.aiservice.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:8089")
@RestController
@RequestMapping("/api/ai/chat")
public class AIController {
    @Autowired
    private AIService aiService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired(required = false)
    private OpenAiChatModel chatClient;

    @GetMapping("/test")
    public Map<String, Object> testAi() {
        Map<String, Object> result = new HashMap<>();
        try {
            System.out.println("========== 开始测试AI调用 ==========");
            System.out.println("chatClient是否为null: " + (chatClient == null));

            if (chatClient == null) {
                result.put("code", 500);
                result.put("message", "OpenAiChatModel Bean未注入！请检查配置");
                result.put("data", null);
                return result;
            }

            UserMessage userMessage = UserMessage.from("你好，请用一句话介绍你自己");
            Response<AiMessage> response = chatClient.generate(userMessage);
            String reply = response.content().text();

            System.out.println("AI返回：" + reply);
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", reply);
        } catch (Exception e) {
            System.err.println("AI测试失败：" + e.getMessage());
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "AI调用失败：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }

    @PostMapping("/context")
    public Map<String, Object> chat(@RequestBody ChatRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            System.out.println("========== 收到对话请求 ==========");
            System.out.println("sessionId: " + request.getSessionId());
            System.out.println("message: " + request.getMessage());

            String reply = aiService.chatWithMemory(request.getSessionId(), request.getMessage());

            result.put("code", 200);
            result.put("message", "success");
            result.put("data", reply);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "网络请求失败，无法获取AI回答：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }

    @PostMapping("/analyse")
    public Map<String, Object> analyseFarmData() {
        Map<String, Object> result = new HashMap<>();
        try {
            System.out.println("========== 开始执行数据分析 ==========");

            String sql = "SELECT CONCAT('农田总块数：',COUNT(id),'，种植作物：',COALESCE(GROUP_CONCAT(DISTINCT crop),'暂无种植作物')) FROM statistic";
            String data = jdbcTemplate.queryForObject(sql, String.class);
            System.out.println("📊 数据库查询结果：" + data);

            if (data == null || data.isEmpty() || data.contains("暂无种植作物")) {
                result.put("code", 400);
                result.put("message", "数据库没有数据可供分析，请先添加农田数据");
                result.put("data", null);
                return result;
            }

            System.out.println("🔄 开始调用AI分析...");
            String report = aiService.analyseBusinessData(data);
            System.out.println("✅ AI分析完成");

            result.put("code", 200);
            result.put("message", "success");
            result.put("data", report);
        } catch (Exception e) {
            System.err.println("❌ 数据分析失败：" + e.getMessage());
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "数据分析失败：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }

    // 🔥 新增：RAG知识库问答接口
    @PostMapping("/rag")
    public Map<String, Object> ragChat(@RequestBody ChatRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            System.out.println("========== RAG知识库问答 ==========");
            System.out.println("消息：" + request.getMessage());

            String reply = aiService.chatWithRag(request.getMessage());

            result.put("code", 200);
            result.put("message", "success");
            result.put("data", reply);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "RAG问答失败：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }
}