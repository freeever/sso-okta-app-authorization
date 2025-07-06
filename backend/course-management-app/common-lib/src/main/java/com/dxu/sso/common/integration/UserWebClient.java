package com.dxu.sso.common.integration;

import com.dxu.sso.common.dto.user.AppUserDto;
import com.dxu.sso.common.security.ReactorRequestContextUtil;
import com.dxu.sso.common.security.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class UserWebClient {

    @Value("${url.user.profile:}")
    private String userProfileUrl;

    @Value("${url.user.admin:}")
    private String userAdminUrl;

    private final ReactorRequestContextUtil securityUtil;
    private final WebClient.Builder webClientBuilder;
    private final UserContext userContext;

    public UserWebClient(ReactorRequestContextUtil securityUtil,
                         @Qualifier("userWebClientBuilder") WebClient.Builder webClientBuilder,
                         UserContext userContext) {
        this.securityUtil = securityUtil;
        this.webClientBuilder = webClientBuilder;
        this.userContext = userContext;
    }

    /**
     * Note: Cannot change it to reactive method because this is used in the Aspect which will cause runtime error:
     *
     *
     *
     * Based on the access token, call the user profile service to get the user information
     * @return user information
     */
    public Mono<AppUserDto> getUserProfile(String authHeader) {
        log.info("Fetching user profile");

        // ✅ If user is already cached, return it wrapped in a Mono
        if (userContext.getAppUser() != null) {
            return Mono.just(userContext.getAppUser());
        }

        // ✅ Fetch user profile reactively
        return webClientBuilder.build()
                .get()
                .uri(userProfileUrl)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.empty())
                .bodyToMono(AppUserDto.class)
                .doOnNext(userContext::setAppUser); // ✅ Cache after receiving
    }

    public Mono<AppUserDto> getUserById(Long id) {
        log.info("Fetching user by id: {}", id);

        return securityUtil.getAuthHeader()
                .flatMap(authHeader -> webClientBuilder.build()
                                .get()
                                .uri(userAdminUrl + "/" + id)
                                .header(HttpHeaders.AUTHORIZATION, authHeader)
                                .retrieve()
                                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                                    log.warn("User by ID {} not found (4xx)", id);
                                    return Mono.empty();
                                })
                                .bodyToMono(AppUserDto.class)
                        );
    }

    public Flux<AppUserDto> getUsersByIds(List<Long> userIds) {
        log.info("Fetching users by IDs: {}", userIds);
        if (userIds == null || userIds.isEmpty()) {
            return Flux.empty();
        }

        return securityUtil.getAuthHeader()
                .flatMapMany(authHeader -> webClientBuilder.build()
                                .post()
                                .uri(userAdminUrl + "/batch")
                                .header(HttpHeaders.AUTHORIZATION, authHeader)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(userIds)
                                .retrieve()
                                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                                    log.warn("Batch user fetch returned {}", response.statusCode());
                                    return Mono.empty();
                                })
                                .bodyToFlux(AppUserDto.class)
                            );
    }
}
