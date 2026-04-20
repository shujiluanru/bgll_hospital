package com.cug.service.serviceImpl;

import cn.hutool.system.UserInfo;
import com.cug.annotation.BgllLog;
import com.cug.constant.SmsCacheConstant;
import com.cug.domain.dto.UserLoginDTO;
import com.cug.domain.dto.UserRegisterDTO;
import com.cug.domain.pojo.R;
import com.cug.domain.pojo.User;
import com.cug.domain.vo.UserInfoVO;
import com.cug.domain.vo.UserLoginVO;
import com.cug.exception.ParamException;
import com.cug.exception.SmsException;
import com.cug.mapper.UserAuthMapper;
import com.cug.properties.JwtUserProperty;
import com.cug.properties.SmsProperty;
import com.cug.service.UserAuthService;
import com.cug.utils.JwtUtil;
import com.cug.utils.SmsUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserAuthServiceImpl implements UserAuthService {
    /**
     * 登录
     * @param userLoginDTO
     * @return
     */
    private final UserAuthMapper userAuthMapper;
    private final JwtUserProperty jwtUserProperty;
    private final SmsProperty smsProperty;
    private final StringRedisTemplate stringRedisTemplate;


    public UserAuthServiceImpl(UserAuthMapper userAuthMapper, JwtUserProperty jwtUserProperty, SmsProperty smsProperty, StringRedisTemplate stringRedisTemplate)
    {
        this.userAuthMapper = userAuthMapper;
        this.jwtUserProperty = jwtUserProperty;
        this.smsProperty = smsProperty;
        this.stringRedisTemplate = stringRedisTemplate;
    }
    @Override
    @BgllLog
    public R login(UserLoginDTO userLoginDTO) {
        //判断登录方式
        if (userLoginDTO.getPhone() == null) {
            throw new ParamException("手机号不能为空");
        }
        //查询手机号是否存在
        User user = userAuthMapper.getUserByPhone(userLoginDTO.getPhone());
        if (user == null) {
            throw new ParamException("手机号不存在");
        }
        if(userLoginDTO.getPassword()== null||userLoginDTO.getPassword().isBlank())
        {
            //验证码登录，从redis中获取验证码
            String key=SmsCacheConstant.SEND_SMS_CODE_USER+userLoginDTO.getPhone();
            String code=stringRedisTemplate.opsForValue().get(key);
            if(code==null)
                throw new ParamException("验证码已过期");
            if(!code.equals(userLoginDTO.getCode()))
                throw new ParamException("验证码错误");
            //验证码正确,删除验证码
            stringRedisTemplate.delete(SmsCacheConstant.SEND_SMS_CODE_USER+userLoginDTO.getPhone());
        }
        else
        {
            //密码登录
            if(!userLoginDTO.getPassword().equals(user.getPassword()))
                throw new ParamException("密码错误");
        }
        //组装返回的数据
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user,userInfoVO);
        UserLoginVO userLoginVO = new UserLoginVO();
        userLoginVO.setUserInfo(userInfoVO);
        //组装Claims
        Claims claims= Jwts.claims();
        claims.put("id",user.getId());
        userLoginVO.setToken(JwtUtil.generateToken(claims, jwtUserProperty.getSecret(), jwtUserProperty.getExpiration()));
        return R.ok(userLoginVO);
    }

    @Override
    public R sendSmsCode(String phone) {
        String code= SmsUtil.generateCode();
        try {
            SmsUtil.getSmsCode(phone, smsProperty.getHost(), smsProperty.getPath(), smsProperty.getAppcode(), smsProperty.getTemplateId(), code);
        }catch(Exception e)
            {
            throw new SmsException("发送验证码失败");
        }
        stringRedisTemplate.opsForValue().set(SmsCacheConstant.SEND_SMS_CODE_USER+phone,code,SmsCacheConstant.SEND_SMS_CODE_EXPIRE, TimeUnit.SECONDS);
        return R.ok();
    }

    @Override
    public R register(UserRegisterDTO userRegisterDTO) {
        userAuthMapper.getUserByPhone(userRegisterDTO.getPhone());
        if(userAuthMapper.getUserByPhone(userRegisterDTO.getPhone())!=null)
            throw new ParamException("手机号已存在");
        if(stringRedisTemplate.opsForValue().get(SmsCacheConstant.SEND_SMS_CODE_USER+userRegisterDTO.getPhone())==null)
            throw new ParamException("验证码已过期");
        if(!stringRedisTemplate.opsForValue().get(SmsCacheConstant.SEND_SMS_CODE_USER+userRegisterDTO.getPhone()).equals(userRegisterDTO.getCode()))
            throw new ParamException("验证码错误");
        User user=new User();
        BeanUtils.copyProperties(userRegisterDTO,user);
        userAuthMapper.insertNewUser(user);
        return R.ok();
    }
}
