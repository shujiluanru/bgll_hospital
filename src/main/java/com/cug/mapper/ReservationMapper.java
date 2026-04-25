package com.cug.mapper;

import com.cug.domain.pojo.Reservation;
import com.cug.domain.vo.ReservationForDoctorVO;
import com.cug.domain.vo.ReservationForUserVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
@Mapper
public interface ReservationMapper {
    List<Reservation> getReservations();

    List<ReservationForUserVO> getReservationsForUser(Long userId);

    List<ReservationForDoctorVO> getReservationsForDoctor(Long doctorId);

    Reservation getReservationById(String reservationId);

    int confirmReservation(String reservationId);

    List<Reservation> getConfirmedReservations();

    int deleteBatchById(List<String> overtimeId);
}
