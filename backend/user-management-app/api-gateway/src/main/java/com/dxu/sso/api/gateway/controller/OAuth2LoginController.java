package com.dxu.sso.api.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/custom-login")
public class OAuth2LoginController {

    @GetMapping("/{client}")
    public Mono<Void> loginWithRedirect(@PathVariable("client") String client,
                                        @RequestParam(required = false) String redirectTo,
                                        ServerWebExchange exchange) {
        return exchange.getSession().flatMap(session -> {
            if (redirectTo != null) {
                session.getAttributes().put("redirectTo", redirectTo);
            }

            String loginUri = "/oauth2/authorization/" + client;

            return Mono.defer(() -> {
                exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                exchange.getResponse().getHeaders().setLocation(URI.create(loginUri));
                return exchange.getResponse().setComplete();
            });
        });
    }
}
