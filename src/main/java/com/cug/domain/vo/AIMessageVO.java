package com.cug.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIMessageVO {
    private String role;
    private String content;
    private Long timestamp;
}
