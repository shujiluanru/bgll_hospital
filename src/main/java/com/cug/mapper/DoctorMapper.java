package com.cug.mapper;

import com.cug.domain.pojo.*;
import com.cug.domain.vo.DoctorInfoForAI;
import com.cug.domain.vo.ReservationForDoctorVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DoctorMapper {
    int updateDescription(Long id, String description);

    List<ReleaseLog> getReleaseLog(Long doctorId);
    List<ReservationResourceData> getResourceNum();

    void release(Long doctorId, int releaseNum);
    void addReleaseLog(ReleaseMessage releaseMessage);

    List<Doctor> getDoctorList();

    void addReservation(Reservation reservation);

    List<DoctorInfoForAI> searchDoctor(String keyName);
}
