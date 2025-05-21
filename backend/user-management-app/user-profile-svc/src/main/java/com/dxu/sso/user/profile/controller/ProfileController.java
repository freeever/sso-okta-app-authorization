package com.dxu.sso.user.profile.controller;

import com.dxu.sso.common.exception.SsoApplicationException;
import com.dxu.sso.common.model.AppUser;
import com.dxu.sso.user.profile.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<AppUser> getProfile(@AuthenticationPrincipal Jwt jwt)
            throws SsoApplicationException {
        log.info("Getting profile for user");
        // Log all claims
        jwt.getClaims().forEach((k, v) -> log.debug("{} : {}", k, v));

        String email = jwt.getClaimAsString("email");
        AppUser user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<AppUser> updateProfile(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody AppUser update)
            throws SsoApplicationException {
        String email = jwt.getClaimAsString("email");
        AppUser user = userService.updateByEmail(email, update);
        return ResponseEntity.ok(user);
    }

    /**
     * This is called when users log in to the system for the first time and there is not profile created yet.
     * @param jwt Jwt token
     * @return the created user profile
     */
    @PostMapping
    public ResponseEntity<AppUser> create(@AuthenticationPrincipal Jwt jwt) {
        log.info("Creating profile for current user");

        AppUser user = userService.create(
                jwt.getClaimAsString("username"),
                jwt.getClaimAsString("email"),
                jwt.getClaimAsString("firstName"),
                jwt.getClaimAsString("lastName"));

        return ResponseEntity.ok(user);
    }
}
