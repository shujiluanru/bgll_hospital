package com.cug.service;

import com.cug.domain.dto.UserChatDTO;
import com.cug.domain.pojo.R;
import com.cug.domain.vo.AIMessageVO;
import org.springframework.ai.chat.messages.Message;

public interface ChatService {
    R getChatHistory(String userId);

    R chat(UserChatDTO userChatDTO);

    R newChat(String userId);
}
