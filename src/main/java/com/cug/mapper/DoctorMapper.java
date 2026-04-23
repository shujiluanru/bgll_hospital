package com.cug.mapper;

import com.cug.domain.pojo.ReservationResourceData;
import org.apache.ibatis.annotations.Mapper;
import com.cug.domain.pojo.ReleaseLog;

import java.util.List;
import java.util.Map;

@Mapper
public interface DoctorMapper {
    int updateDescription(Long id, String description);

    List<ReleaseLog> getReleaseLog(Long doctorId);
    List<ReservationResourceData> getResourceNum();

    void release(Long doctorId, int releaseNum);
}
