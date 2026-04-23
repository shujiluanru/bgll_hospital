package com.cug.service.serviceImpl;

import cn.hutool.core.bean.BeanUtil;
import com.cug.constant.CommonConstant;
import com.cug.domain.dto.DoctorLoginDTO;
import com.cug.domain.pojo.Doctor;
import com.cug.domain.pojo.R;
import com.cug.domain.vo.DoctorInfoVO;
import com.cug.domain.vo.DoctorLoginVO;
import com.cug.exception.ParamException;
import com.cug.mapper.DoctorAuthMapper;
import com.cug.properties.JwtDoctorProperty;
import com.cug.service.DoctorAuthService;
import com.cug.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

@Service
public class DoctorAuthServiceImpl implements DoctorAuthService {
    private final JwtDoctorProperty jwtDoctorProperty;
    private final DoctorAuthMapper doctorAuthMapper;
    public DoctorAuthServiceImpl(JwtDoctorProperty jwtDoctorProperty, DoctorAuthMapper doctorAuthMapper) {
        this.jwtDoctorProperty = jwtDoctorProperty;
        this.doctorAuthMapper = doctorAuthMapper;
    }
    @Override
    public R login(DoctorLoginDTO doctorLoginDTO) {
        if(doctorLoginDTO.getPhone() == null || doctorLoginDTO.getPassword() == null)
            throw  new ParamException(CommonConstant.PARAM_ERROR);
        Doctor doctor = doctorAuthMapper.getDoctorByPhone(doctorLoginDTO.getPhone());
        if(doctor == null)
            throw new ParamException("用户不存在");
        if(!doctor.getPassword().equals(doctorLoginDTO.getPassword()))
            throw new ParamException("密码错误");
        DoctorLoginVO doctorLoginVO = new DoctorLoginVO();
        DoctorInfoVO doctorInfoVO = new DoctorInfoVO();
        BeanUtil.copyProperties(doctor,doctorInfoVO);
        doctorLoginVO.setDoctorInfo(doctorInfoVO);
        Claims claims= Jwts.claims();
        claims.put("id",doctor.getId());
        doctorLoginVO.setToken(JwtUtil.generateToken(claims,jwtDoctorProperty.getSecret(),jwtDoctorProperty.getExpiration()));
        return R.ok(doctorLoginVO);
    }
}
