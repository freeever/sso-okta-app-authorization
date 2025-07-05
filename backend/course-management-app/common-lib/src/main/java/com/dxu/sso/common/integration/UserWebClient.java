package com.dxu.sso.common.integration;

import com.dxu.sso.common.dto.user.AppUserDto;
import com.dxu.sso.common.security.SecurityUtil;
import com.dxu.sso.common.security.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class UserWebClient {

    @Value("${url.user.profile:}")
    private String userProfileUrl;

    @Value("${url.user.admin:}")
    private String userAdminUrl;

    private final SecurityUtil securityUtil;
    private final WebClient.Builder webClientBuilder;
    private final UserContext userContext;

    public UserWebClient(SecurityUtil securityUtil,
                         @Qualifier("userWebClientBuilder") WebClient.Builder webClientBuilder,
                         UserContext userContext) {
        this.securityUtil = securityUtil;
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

        AppUserDto user = webClientBuilder.build()
                .get()
                .uri(userProfileUrl)
                .header(HttpHeaders.AUTHORIZATION, securityUtil.getAuthHeader())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.empty())
                .bodyToMono(AppUserDto.class)
                .block();

        userContext.setAppUser(user); // ✅ cache it
        return user;
    }

    public AppUserDto getUserById(Long id) {
        log.info("Fetching user by id: {}", id);

        return webClientBuilder.build()
                .get()
                .uri(userAdminUrl + "/" + id)
                .header(HttpHeaders.AUTHORIZATION, securityUtil.getAuthHeader())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.empty())
                .bodyToMono(AppUserDto.class)
                .block();
    }

    public List<AppUserDto> getUsersByIds(List<Long> ids) {
        log.info("Fetching users by IDs: {}", ids);

        return webClientBuilder.build()
                .post()
                .uri(userAdminUrl + "/batch")
                .header(HttpHeaders.AUTHORIZATION, securityUtil.getAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ids)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.empty())
                .bodyToFlux(AppUserDto.class)
                .collectList()
                .block();
    }
}
