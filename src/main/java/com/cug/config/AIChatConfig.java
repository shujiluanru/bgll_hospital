package com.cug.config;

import com.cug.constant.AIConstant;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.util.List;

@Configuration
public class AIChatConfig {
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;
    @Bean
    public ChatClient chatClient(OpenAiChatModel openAiChatModel,VectorStore vectorStore)
    {
        QuestionAnswerAdvisor build = QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder().topK(4).similarityThreshold(0.7).build())
                .build();//基于RAG构建Advisor
        return ChatClient.builder(openAiChatModel)
                .defaultSystem(AIConstant.AI_PROMPT)
                .defaultAdvisors(build)
                .build();
    }
    /**
     * 创建 ChatMemory
     *
     * @param repository
     * @return
     */
    @Bean
    public ChatMemory chatMemory(CustomJdbcChatMemoryRepository repository)
    {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(10)
                .build();
    }
    /**
     * 创建 OpenAiApi
     *
     * @return
     */
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
    /*
    * 模型配置
    *
    * */

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
    /*
    *
    * 配置向量库
    *
    * */
    @Bean
    public VectorStore vectorStore(OpenAiEmbeddingModel openAiEmbeddingModel) {
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(openAiEmbeddingModel).build();
        //将文件添加到向量库中
        Resource resource = new ClassPathResource("pdfs/bg_hospital_category.pdf");
        PagePdfDocumentReader reader = new PagePdfDocumentReader(resource,
                PdfDocumentReaderConfig.defaultConfig()
        );
        List<Document> documents = reader.read();
        vectorStore.doAdd(documents);
        //添加介绍文件
        Resource introductionResource = new ClassPathResource("pdfs/bg_hospital_introduction.pdf");
        PagePdfDocumentReader introductionReader = new PagePdfDocumentReader(introductionResource,
                PdfDocumentReaderConfig.defaultConfig()
        );
        List<Document> introductionDocuments = introductionReader.read();
        vectorStore.doAdd(introductionDocuments);
        return vectorStore;
    }
}


