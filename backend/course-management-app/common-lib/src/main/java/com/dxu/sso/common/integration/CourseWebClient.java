package com.dxu.sso.common.integration;

import com.dxu.sso.common.dto.course.CourseDto;
import com.dxu.sso.common.dto.course.CourseQueryRequest;
import com.dxu.sso.common.security.ReactorRequestContextUtil;
import com.dxu.sso.common.security.SecurityUtil;
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
public class CourseWebClient {

    @Value("${url.course.query:}")
    private String courseQueryUrl;

    private final ReactorRequestContextUtil securityUtil;
    private final WebClient.Builder webClientBuilder;

    public CourseWebClient(@Qualifier("courseWebClientBuilder") WebClient.Builder webClientBuilder,
                           ReactorRequestContextUtil securityUtil) {
        this.webClientBuilder = webClientBuilder;
        this.securityUtil = securityUtil;
    }

    public Flux<CourseDto> findCoursesByIds(List<Long> courseIds) {
        if (courseIds == null || courseIds.isEmpty()) {
            return Flux.empty();
        }

        return securityUtil.getAuthHeader()
                .flatMapMany(authHeader -> webClientBuilder.build()
                                .post()
                                .uri(courseQueryUrl + "/list")
                                .header(HttpHeaders.AUTHORIZATION, authHeader)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new CourseQueryRequest(courseIds))
                                .retrieve()
                                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                                    log.warn("Batch course fetch returned {}", response.statusCode());
                                    return Mono.empty();
                                })
                                .bodyToFlux(CourseDto.class)
                            );
    }
}
