package com.cug.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class BgllLogAspect {
    @Pointcut("@annotation(com.cug.annotation.BgllLog)")//作用在注解上
    public void logPointCut(){}
    @Around("logPointCut()")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        String name = joinPoint.getSignature().getName();
        System.out.println("方法"+name+"调用,参数:"+ Arrays.toString(joinPoint.getArgs()));
        return joinPoint.proceed();
    }

}
