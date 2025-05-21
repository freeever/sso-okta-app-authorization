package com.dxu.sso.common.service;

import com.dxu.sso.common.model.AppUser;
import com.dxu.sso.common.security.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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
    public AppUser getUserProfile() {
        log.info("Fetching user profile");

        // Return cached user if already fetched
        if (userContext.getAppUser() != null) {
            return userContext.getAppUser();
        }

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return null;

        String authHeader = attrs.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;

        AppUser user = webClientBuilder.build()
                .get()
                .uri(userProfileUrl)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> Mono.empty())
                .bodyToMono(AppUser.class)
                .block();

        userContext.setAppUser(user); // ✅ cache it
        return user;
    }

}
