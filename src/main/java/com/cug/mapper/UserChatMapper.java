package com.cug.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserChatMapper {
    @Select("select conversation_id from user where id=#{userId}")
    String getConversationId(String userId);
    @Update("update user set conversation_id=#{conversationId} where id=#{userId}")
    void insertChatIdForUser(String conversationId,String userId);
}
