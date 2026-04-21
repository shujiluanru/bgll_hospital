package com.cug.config;

import com.cug.constant.AIConstant;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepositoryDialect;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestClient;

import javax.sql.DataSource;
import java.time.Duration;
import java.util.List;

@Configuration
public class AIChatConfig {
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;
    @Value("${bgll.ai.rag.pdf.category.path}")
    private String pdfCategoryPath;
    @Value("${bgll.ai.rag.pdf.introduction.path}")
    private String pdfIntroductionPath;
    @Bean
    public ChatClient chatClient(OpenAiChatModel openAiChatModel)
    {
        return ChatClient.builder(openAiChatModel)
                .defaultSystem(AIConstant.AI_PROMPT)
                .build();
    }
    @Bean
    public ChatMemory chatMemory(CustomJdbcChatMemoryRepository repository)
    {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(10)
                .build();
    }
    @Bean
    public OpenAiApi openAiApi() {
        // 1. 创建 RequestFactory 并设置超时（关键修正）
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000);   // 连接超时 30 秒（单位：毫秒）
        factory.setReadTimeout(180000);     // 读取超时 180 秒（3分钟）

        // 2. 构建 RestClient 并应用这个 factory
        RestClient.Builder restClientBuilder = RestClient.builder()
                .requestFactory(factory);

        // 3. 构建 OpenAiApi
        return OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .restClientBuilder(restClientBuilder)
                .build();
    }

    @Bean
    public OpenAiChatModel openAiChatModel(OpenAiApi openAiApi,
                                           @Value("${spring.ai.openai.chat.options.model}") String model,
                                           @Value("${spring.ai.openai.chat.options.temperature}") Double temperature) {
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(model)
                        .temperature(temperature)
                        .build())
                .build();
    }
    @Bean
    public VectorStore vectorStore(OpenAiEmbeddingModel openAiEmbeddingModel) {
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(openAiEmbeddingModel).build();
        //将文件添加到向量库中
        Resource resource = new FileSystemResource(pdfCategoryPath);
        PagePdfDocumentReader reader = new PagePdfDocumentReader(resource,
                PdfDocumentReaderConfig.defaultConfig()
        );
        List<Document> documents = reader.read();
        vectorStore.doAdd(documents);
        //添加介绍文件
        Resource introductionResource = new FileSystemResource(pdfIntroductionPath);
        PagePdfDocumentReader introductionReader = new PagePdfDocumentReader(introductionResource,
                PdfDocumentReaderConfig.defaultConfig()
        );
        List<Document> introductionDocuments = introductionReader.read();
        vectorStore.doAdd(introductionDocuments);
        return vectorStore;
    }
}


