package com.cug.mapper;

import com.cug.domain.pojo.ChatMemoryEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatMemoryMapper {

    @Select("SELECT * FROM SPRING_AI_CHAT_MEMORY " +
            "WHERE conversation_id = #{conversationId} " +
            "ORDER BY timestamp ASC")
    List<ChatMemoryEntity> findByConversationId(@Param("conversationId") String conversationId);

    @Insert("INSERT INTO SPRING_AI_CHAT_MEMORY " +
            "(conversation_id, content, type, timestamp) " +
            "VALUES (#{conversationId}, #{content}, #{type}, #{timestamp})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ChatMemoryEntity entity);

    @Delete("DELETE FROM SPRING_AI_CHAT_MEMORY WHERE conversation_id = #{conversationId}")
    int deleteByConversationId(@Param("conversationId") String conversationId);

    @Select("SELECT DISTINCT conversation_id FROM SPRING_AI_CHAT_MEMORY " +
            "ORDER BY MAX(timestamp) DESC")
    List<String> findAllConversationIds();
}