package com.cug.config;

import com.cug.domain.vo.DoctorInfoForAI;
import com.cug.mapper.DoctorMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class AIChatTools {
    private final DoctorMapper doctorMapper;
    public AIChatTools(DoctorMapper doctorMapper) {
        this.doctorMapper = doctorMapper;
    }
    @Tool(description = "获取当前的日期和时间")
    public String getTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    /**
     * 搜索医生
     *
     * @param keyName 搜索关键词（姓名）
     * @return List<DoctorInfoForAI>医生列表，每个医生包含：
     *         - name: 医生姓名
     *         - departmentName: 科室名称
     *         - resourceNum: 剩余号源数
     *         - phone: 联系方式
     *         - resourceNum: 剩余号源数
     */
    @Tool(description = "获取医生号源数量,可能会有多个医生")
    public List<DoctorInfoForAI> getResourceNum(@ToolParam(description="医生姓名")String keyName)
    {
        return doctorMapper.searchDoctor(keyName);
    }


}
