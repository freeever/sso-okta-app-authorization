package com.dxu.sso.common.integration;

import com.dxu.sso.common.security.ReactorRequestContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthHeaderWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null) {
            return chain.filter(exchange)
                    .contextWrite(ctx -> ctx.put(ReactorRequestContextUtil.AUTH_HEADER_KEY, authHeader));
        }
        return chain.filter(exchange);
    }
}
