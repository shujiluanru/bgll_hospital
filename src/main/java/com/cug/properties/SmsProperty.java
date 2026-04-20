package com.cug.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class SmsProperty {
    @Value("${bgll.sms.appcode}")
    private String appcode;
    @Value("${bgll.sms.templateId}")
    private String templateId;
    @Value("${bgll.sms.host}")
    private String host;
    @Value("${bgll.sms.path}")
    private String path;
}
