package com.cug.service.serviceImpl;

import com.cug.context.UserContext;
import com.cug.domain.dto.UserInfoDTO;
import com.cug.domain.pojo.R;
import com.cug.domain.vo.ReservationForUserVO;
import com.cug.mapper.ReservationMapper;
import com.cug.mapper.UserMapper;
import com.cug.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final ReservationMapper reservationMapper;
    @Autowired
    public UserServiceImpl(UserMapper userMapper, ReservationMapper reservationMapper) {
        this.userMapper = userMapper;
        this.reservationMapper=reservationMapper;
    }
    @Override
    public R updateUserInfo(UserInfoDTO userInfoDTO) {
        userMapper.updateUserById(userInfoDTO);
        log.info("updateUserInfo cug");
        return R.ok();
    }

    @Override
    public R getReservations() {
        List<ReservationForUserVO> reservations = reservationMapper.getReservationsForUser(UserContext.getUserId());
        return R.ok(reservations);
    }
}
