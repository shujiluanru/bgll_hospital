package com.cug.service.serviceImpl;

import com.cug.config.CustomJdbcChatMemoryRepository;
import com.cug.constant.AIConstant;
import com.cug.domain.dto.UserChatDTO;
import com.cug.domain.pojo.R;
import com.cug.domain.pojo.User;
import com.cug.domain.vo.AIMessageVO;
import com.cug.mapper.UserChatMapper;
import com.cug.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    private ChatClient chatClient;
    @Autowired
    private CustomJdbcChatMemoryRepository chatMemoryRepository;
    @Autowired
    private UserChatMapper userChatMapper;
    @Override
    public R getChatHistory(String userId) {
        //通过userId获取conversation_id
        String conversationId = userChatMapper.getConversationId(userId);
        // 2. 校验 conversationId 是否存在
        if (conversationId == null || conversationId.isEmpty()) {
            return R.ok(List.of());  // 没有会话记录，返回空列表
        }
        // 3. 查询消息历史
        List<Message> messages = chatMemoryRepository.findByConversationId(conversationId);
        // 4. 过滤并转换为 VO
        List<AIMessageVO> res = messages.stream()
                .filter(msg -> msg instanceof UserMessage || msg instanceof AssistantMessage)
                .map(this::toAIMessageVO)
                .collect(Collectors.toList());

        return R.ok(res);

    }
    @Override
    public R chat(UserChatDTO userChatDTO) {
        String conversationId = userChatMapper.getConversationId(userChatDTO.getUserId());
        if (conversationId == null || conversationId.isEmpty()) {
            conversationId = "conversation_" + userChatDTO.getUserId();
            userChatMapper.insertChatIdForUser(conversationId,userChatDTO.getUserId());
        }
        final String finalConversationId=conversationId;
        //交给LLM处理
        String content = chatClient.prompt()
                .user(userChatDTO.getContent())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, finalConversationId))
                .call()
                .content();
        //转化为AIMessageVO
        AIMessageVO aiMessageVO = new AIMessageVO("assistant", content, System.currentTimeMillis());
        // 保存消息,自动实现
        chatMemoryRepository.saveAll(finalConversationId, List.of(new UserMessage(userChatDTO.getContent()), new AssistantMessage(content)));
        return R.ok(List.of(aiMessageVO));
    }

    @Override
    public R newChat(String userId) {
        // 清除该用户的会话记录，不创建新的id
        //获取该用户的conversation_id
        String conversationId = userChatMapper.getConversationId(userId);
        if (conversationId != null && !conversationId.isEmpty()) {
            conversationId = "conversation_" + userId;
            userChatMapper.insertChatIdForUser(conversationId,userId);
        }
        // 清除该用户的会话记录
        chatMemoryRepository.deleteByConversationId(conversationId);
        //给前端返回一个包含了Assistant初始消息的列表，并插入到数据库中
        chatMemoryRepository.saveAll(conversationId, List.of(new AssistantMessage(AIConstant.AI_INITIAL_MESSAGE)));
        return R.ok(List.of(new AIMessageVO("assistant", AIConstant.AI_INITIAL_MESSAGE, System.currentTimeMillis())));
    }

    private AIMessageVO toAIMessageVO(Message message) {
        AIMessageVO vo = new AIMessageVO();

        // 设置角色和内容
        if (message instanceof UserMessage userMessage) {
            vo.setRole("user");
            vo.setContent(userMessage.getText());
        } else if (message instanceof AssistantMessage assistantMessage) {
            vo.setRole("assistant");
            vo.setContent(assistantMessage.getText());
        }

        // 从 metadata 中取出时间戳（在 CustomJdbcChatMemoryRepository 中已放入）
        Object timestampObj = message.getMetadata().get("timestamp");
        if (timestampObj instanceof LocalDateTime) {
            // LocalDateTime 转毫秒时间戳
            long timestamp = ((LocalDateTime) timestampObj).toEpochSecond(ZoneOffset.UTC) * 1000;
            vo.setTimestamp(timestamp);
        } else if (timestampObj instanceof Long) {
            vo.setTimestamp((Long) timestampObj);
        } else {
            // 兜底：使用当前时间
            vo.setTimestamp(System.currentTimeMillis());
        }

        return vo;
    }

}
