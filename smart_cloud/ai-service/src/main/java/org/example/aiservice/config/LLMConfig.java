package org.example.aiservice.config;

import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

@Configuration
public class LLMConfig {

    @Value("${langchain4j.open-ai.chat-model.api-key}")
    private String apiKey;

    @Value("${langchain4j.open-ai.chat-model.base-url}")
    private String baseUrl;

    @Value("${langchain4j.open-ai.chat-model.model-name}")
    private String modelName;

    @Value("${langchain4j.open-ai.chat-model.temperature:0.1}")
    private Double temperature;

    @Bean
    @Primary
    public OpenAiChatModel chatClient() {
        System.out.println("========================================");
        System.out.println("🔧 OpenAiChatModel Bean 创建中...");
        System.out.println("Base URL: " + baseUrl);
        System.out.println("Model: " + modelName);
        System.out.println("Temperature: " + temperature);
        System.out.println("API Key: " + (apiKey != null && !apiKey.isEmpty() ? "✅ 已配置" : "❌ 未配置！"));
        System.out.println("========================================");

        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(modelName)
                .temperature(temperature)
                .maxTokens(2048)
                .timeout(Duration.ofSeconds(120))
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    // 🔥 新增：嵌入模型（文本转向量）
    @Bean
    public EmbeddingModel embeddingModel() {
        System.out.println("========================================");
        System.out.println("🔧 EmbeddingModel Bean 创建中...");
        System.out.println("使用模型: AllMiniLmL6V2EmbeddingModel");
        System.out.println("========================================");
        return new AllMiniLmL6V2EmbeddingModel();
    }
}