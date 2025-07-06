package com.dxu.sso.common.security;

import com.dxu.sso.common.dto.user.AppUserDto;
import com.dxu.sso.common.exception.SsoApplicationException;
import com.dxu.sso.common.integration.UserWebClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
public class SecurityAspect {

    private final UserWebClient userWebClient;

    @Around("@annotation(requireUserProfile)")
    public Object checkUserProfile(ProceedingJoinPoint joinPoint, RequireUserProfile requireUserProfile)
            throws Throwable {
        // Extract the auth header directly from request
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new SsoApplicationException(HttpStatus.FORBIDDEN.value(), "Missing or invalid Authorization header");
        }

        AppUserDto user = userWebClient.getUserProfile(authHeader).block(); // ⚠️ blocking, but now in request thread

        if (user == null) {
            throw new SsoApplicationException(HttpStatus.FORBIDDEN.value(), "User profile not found");
        }
        return joinPoint.proceed();
    }

    @Around("@annotation(requireRoles)")
    public Object checkUserRole(ProceedingJoinPoint joinPoint, RequireRoles requireRoles) throws Throwable {
        // Extract the auth header directly from request
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new SsoApplicationException(HttpStatus.FORBIDDEN.value(), "Missing or invalid Authorization header");
        }

        AppUserDto user = userWebClient.getUserProfile(authHeader).block(); // ⚠️ blocking, but now in request thread

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
