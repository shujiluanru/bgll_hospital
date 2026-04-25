package com.cug.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationForDoctorVO {
    private String reservationId;
    private String username;
    private String userPhone;
    private String userEmail;
    private String sex;
    private Long age;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    private String status;
}
