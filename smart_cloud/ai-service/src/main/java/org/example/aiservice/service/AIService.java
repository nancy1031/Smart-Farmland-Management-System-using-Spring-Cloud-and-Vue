package org.example.aiservice.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.example.aiservice.dto.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;  // 🔥 改为 jakarta

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AIService {
    @Autowired(required = false)
    private OpenAiChatModel chatClient;

    @Autowired(required = false)
    private EmbeddingModel embeddingModel;

    @Value("${rag.knowledge.path:./knowledge/农业知识库.txt}")
    private String knowledgePath;

    @Value("${rag.max-results:3}")
    private int maxResults;

    private final Map<String, List<ChatMessage>> chatMemory = new ConcurrentHashMap<>();

    private InMemoryEmbeddingStore<TextSegment> embeddingStore;
    private boolean vectorStoreReady = false;

    @PostConstruct
    public void initVectorStore() {
        try {
            log.info("========== 初始化向量存储 ==========");
            log.info("知识库路径: {}", knowledgePath);

            if (embeddingModel == null) {
                log.warn("⚠️ EmbeddingModel 未注入，向量检索功能不可用");
                return;
            }

            embeddingStore = new InMemoryEmbeddingStore<>();

            Document document = FileSystemDocumentLoader.loadDocument(
                    knowledgePath,
                    new TextDocumentParser()
            );

            EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                    .embeddingStore(embeddingStore)
                    .embeddingModel(embeddingModel)
                    .build();

            ingestor.ingest(document);

            vectorStoreReady = true;
            log.info("✅ 知识库向量化完成");
        } catch (Exception e) {
            log.warn("⚠️ 知识库加载失败，向量检索功能不可用: {}", e.getMessage());
            embeddingStore = null;
            vectorStoreReady = false;
        }
    }

    public String chat(String message) {
        try {
            if (chatClient == null) {
                return "错误：OpenAiChatModel Bean未注入！";
            }

            log.info("===== 开始调用AI =====");
            log.info("消息内容：{}", message);

            UserMessage userMessage = UserMessage.from(message);
            Response<AiMessage> response = chatClient.generate(userMessage);

            String result = response.content().text();
            log.info("AI返回内容：{}", result);
            return result;
        } catch (Exception e) {
            log.error("AI调用失败", e);
            return "AI调用失败：" + e.getMessage();
        }
    }

    public String generateRecommendation(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return "暂无商品推荐";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("根据下面农资商品，生成简短营销文案，50字以内：\n");
        for (int i = 0; i < Math.min(products.size(), 5); i++) {
            Product p = products.get(i);
            sb.append(p.getName()).append("，售价").append(p.getPrice()).append("元\n");
        }
        return chat(sb.toString());
    }

    public String chatWithMemory(String sessionId, String message) {
        try {
            if (chatClient == null) {
                return "错误：OpenAiChatModel Bean未注入！";
            }

            List<ChatMessage> history = chatMemory.computeIfAbsent(sessionId, k -> new ArrayList<>());
            history.add(UserMessage.from(message));
            while (history.size() > 10) {
                history.remove(0);
            }

            Response<AiMessage> response = chatClient.generate(history);
            return response.content().text();
        } catch (Exception e) {
            log.error("带记忆的对话失败", e);
            return "AI服务暂时不可用：" + e.getMessage();
        }
    }

    public String analyseBusinessData(String businessData) {
        try {
            if (chatClient == null) {
                return "错误：OpenAiChatModel Bean未注入！请检查配置";
            }

            log.info("===== 开始业务数据分析 =====");
            log.info("输入数据：{}", businessData);

            String prompt = "根据农田业务数据，给出种植经营分析建议。要求：\n" +
                    "1. 不要使用任何Markdown格式（不要用#、**、*、-等符号）\n" +
                    "2. 使用纯文本，用换行和空格来分段\n" +
                    "3. 用数字1、2、3来列举要点\n" +
                    "4. 内容要专业、实用、通俗易懂\n\n" +
                    "农田数据：" + businessData;

            UserMessage userMessage = UserMessage.from(prompt);
            Response<AiMessage> response = chatClient.generate(userMessage);

            String result = response.content().text();
            log.info("AI分析结果：{}", result);
            return result;
        } catch (Exception e) {
            log.error("业务数据分析失败", e);
            return "数据分析失败：" + e.getMessage();
        }
    }

    public String chatWithRag(String message) {
        try {
            if (chatClient == null) {
                return "错误：OpenAiChatModel Bean未注入！";
            }

            if (!vectorStoreReady || embeddingStore == null) {
                log.warn("⚠️ 向量存储不可用，降级为普通对话");
                return chat(message);
            }

            log.info("===== 开始RAG对话 =====");
            log.info("用户问题：{}", message);

            Embedding queryEmbedding = embeddingModel.embed(message).content();

            List<EmbeddingMatch<TextSegment>> matches = embeddingStore.findRelevant(queryEmbedding, maxResults);

            log.info("✅ 找到 {} 条相关知识", matches.size());

            List<TextSegment> relevantSegments = matches.stream()
                    .map(EmbeddingMatch::embedded)
                    .collect(Collectors.toList());

            StringBuilder context = new StringBuilder();
            context.append("请根据以下知识库内容回答用户的问题。\n\n");
            context.append("【知识库内容】\n");
            for (int i = 0; i < relevantSegments.size(); i++) {
                context.append("--- 参考资料 ").append(i + 1).append(" ---\n");
                context.append(relevantSegments.get(i).text()).append("\n\n");
            }
            context.append("【用户问题】\n");
            context.append(message);

            UserMessage userMessage = UserMessage.from(context.toString());
            Response<AiMessage> response = chatClient.generate(userMessage);

            String result = response.content().text();
            log.info("AI返回内容：{}", result);
            return result;
        } catch (Exception e) {
            log.error("RAG对话失败，降级为普通对话", e);
            return chat(message);
        }
    }
}