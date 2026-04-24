package com.cug.service;

import com.cug.domain.pojo.ReleaseMessage;

public interface ResourceRabbitMQService {
    void release(ReleaseMessage releaseMessage);
}
