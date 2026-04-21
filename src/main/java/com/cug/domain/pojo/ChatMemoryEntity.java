package com.cug.domain.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChatMemoryEntity {
    private Long id;
    private String conversationId;
    private String content;
    private String type;      // USER, ASSISTANT, SYSTEM, TOOL
    private LocalDateTime timestamp;
}