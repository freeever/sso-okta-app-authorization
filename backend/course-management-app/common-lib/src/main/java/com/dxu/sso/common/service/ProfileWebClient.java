package com.dxu.sso.common.service;

import com.dxu.sso.common.dto.user.AppUserDto;
import com.dxu.sso.common.security.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ProfileWebClient {

    @Value("${user-profile.url:}")
    private String userProfileUrl;

    private final WebClient.Builder webClientBuilder;
    private final UserContext userContext;

    public ProfileWebClient(@Qualifier("commonWebClientBuilder") WebClient.Builder webClientBuilder,
                            UserContext userContext) {
        this.webClientBuilder = webClientBuilder;
        this.userContext = userContext;
    }

    /**
     * Based on the access token, call the user profile service to get the user information
     * @return user information
     */
    public AppUserDto getUserProfile() {
        log.info("Fetching user profile");

        // Return cached user if already fetched
        if (userContext.getAppUser() != null) {
            return userContext.getAppUser();
        }

        String authHeader = getAuthHeader();
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;

        AppUserDto user = webClientBuilder.build()
                .get()
                .uri(userProfileUrl)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.empty())
                .bodyToMono(AppUserDto.class)
                .block();

        userContext.setAppUser(user); // ✅ cache it
        return user;
    }

    private static String getAuthHeader() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return null;

        return  attrs.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
    }

}
