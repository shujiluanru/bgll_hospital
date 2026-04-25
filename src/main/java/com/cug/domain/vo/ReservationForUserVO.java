package com.cug.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationForUserVO {
    private String reservationId;
    private String doctorName;
    private String doctorPhone;
    private String doctorEmail;
    private String doctorDepartmentName;
    private String doctorDescription;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    private String status;
}
