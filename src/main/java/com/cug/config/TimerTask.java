package com.cug.config;

import com.cug.domain.pojo.Reservation;
import com.cug.mapper.ReservationMapper;
import com.cug.mapper.UserMapper;
import com.cug.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TimerTask {
    private final ReservationMapper reservationMapper;
    public TimerTask(ReservationMapper reservationMapper) {
        this.reservationMapper = reservationMapper;

    }
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanConfirmedReservation() {
        // 获取已确认的预约信息
        List<Reservation> reservations = reservationMapper.getConfirmedReservations();
        List<String> overtimeId=reservations.stream()
                        .filter(r->r.getCreateTime().plusDays(2).isBefore(LocalDateTime.now()))
                .map(Reservation::getId)  // 获取ID
                .collect(Collectors.toList());

        int i = reservationMapper.deleteBatchById(overtimeId);
        log.info(LocalDateTime.now()+"已清理确认过期的{}条预约信息",i);
    }

}
