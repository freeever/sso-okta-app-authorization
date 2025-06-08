package com.dxu.sso.common.security;

import com.dxu.sso.common.dto.user.AppUserDto;
import com.dxu.sso.common.exception.SsoApplicationException;
import com.dxu.sso.common.service.ProfileWebClient;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
public class SecurityAspect {

    private final ProfileWebClient profileWebClient;

    @Around("@annotation(requireUserProfile)")
    public Object checkUserProfile(ProceedingJoinPoint joinPoint, RequireUserProfile requireUserProfile)
            throws Throwable {
        AppUserDto user = profileWebClient.getUserProfile();
        if (user == null) {
            throw new SsoApplicationException(HttpStatus.FORBIDDEN.value(), "User profile not found");
        }
        return joinPoint.proceed();
    }

    @Around("@annotation(requireRoles)")
    public Object checkUserRole(ProceedingJoinPoint joinPoint, RequireRoles requireRoles) throws Throwable {
        AppUserDto user = profileWebClient.getUserProfile();
        if (user == null) {
            throw new SsoApplicationException(HttpStatus.FORBIDDEN.value(), "User profile not found");
        }

        List<String> allowedRoles = Arrays.asList(requireRoles.value());
        if (!allowedRoles.contains(user.getRole())) {
            throw new SsoApplicationException(HttpStatus.FORBIDDEN.value(), "User role not permitted");
        }

        return joinPoint.proceed();
    }
}
