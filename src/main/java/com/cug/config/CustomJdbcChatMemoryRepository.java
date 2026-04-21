package com.cug.config;

import com.cug.domain.pojo.ChatMemoryEntity;
import com.cug.mapper.ChatMemoryMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CustomJdbcChatMemoryRepository implements ChatMemoryRepository {

    @Autowired
    private ChatMemoryMapper chatMemoryMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Message> findByConversationId(String conversationId) {
        List<ChatMemoryEntity> entities = chatMemoryMapper.findByConversationId(conversationId);

        return entities.stream()
                .map(this::convertToMessage)
                .collect(Collectors.toList());
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < messages.size(); i++) {
            Message msg = messages.get(i);
            ChatMemoryEntity entity = new ChatMemoryEntity();
            entity.setConversationId(conversationId);
            entity.setType(msg.getMessageType().getValue());
            entity.setContent(serializeMessage(msg));
            entity.setTimestamp(now.plusSeconds(i));
            chatMemoryMapper.insert(entity);
        }
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        chatMemoryMapper.deleteByConversationId(conversationId);
    }

    @Override
    public List<String> findConversationIds() {
        return chatMemoryMapper.findAllConversationIds();
    }

    private String serializeMessage(Message message) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("text", message.getText());
            data.put("metadata", message.getMetadata());
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            log.warn("序列化消息失败: {}", e.getMessage());
            return message.getText();
        }
    }

    private Message convertToMessage(ChatMemoryEntity entity) {
        String type = entity.getType();
        String content = entity.getContent();
        LocalDateTime timestamp = entity.getTimestamp();

        String text = content;
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("timestamp", timestamp);  // 时间戳放进 metadata

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = objectMapper.readValue(content, Map.class);
            text = (String) data.getOrDefault("text", content);
            Object metaObj = data.get("metadata");
            if (metaObj instanceof Map) {
                metadata.putAll((Map<String, Object>) metaObj);
                // 从metadata中获取messageType，如果存在则覆盖type
                Object messageTypeObj = ((Map<String, Object>) metaObj).get("messageType");
                if (messageTypeObj != null) {
                    type = messageTypeObj.toString();
                }
            }
        } catch (Exception e) {
            // 不是 JSON，直接用原内容
        }

        if ("USER".equals(type)) {
            return UserMessage.builder().text(text).metadata(metadata).build();
        } else if ("ASSISTANT".equals(type)) {
            return new AssistantMessage(text, metadata);
        } else if ("SYSTEM".equals(type)) {
            return SystemMessage.builder().text(text).metadata(metadata).build();
        } else if ("TOOL".equals(type)) {
            return new ToolResponseMessage(
                    List.of(new ToolResponseMessage.ToolResponse("", "", text)),
                    metadata
            );
        }

        return UserMessage.builder().text(text).metadata(metadata).build();
    }
}