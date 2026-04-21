package com.cug.controller;

import com.cug.domain.dto.UserChatDTO;
import com.cug.domain.pojo.R;
import com.cug.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user/api/ai")
public class ChatController {
    private final ChatService chatService;
    public ChatController(ChatService chatService) {
       this.chatService=chatService;
    }
    @GetMapping("/chat/history")
    public R getChatHistory(String userId)
    {
        log.info("getChatHistory");
        return chatService.getChatHistory(userId);
    }
    @PostMapping("/chat/new")
    public R newChat(@RequestBody Map<String,String> data)
    {
        return chatService.newChat(data.get("userId"));
    }
    @PostMapping("/chat")
    public R chat(@RequestBody UserChatDTO userChatDTO)
    {
        return chatService.chat(userChatDTO);
    }

}
