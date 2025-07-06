package com.dxu.sso.common.security;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Component
public class ReactorRequestContextUtil {
    public static final String AUTH_HEADER_KEY = "Authorization";

    public Context withAuthHeader(String headerValue) {
        return Context.of(AUTH_HEADER_KEY, headerValue);
    }

    public Mono<String> getAuthHeader() {
        return Mono.deferContextual(ctx -> {
            if (ctx.hasKey(AUTH_HEADER_KEY)) {
                return Mono.just(ctx.get(AUTH_HEADER_KEY));
            } else {
                return Mono.empty();
            }
        });
    }
}
