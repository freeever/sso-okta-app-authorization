package com.dxu.sso.user.profile.controller;

import com.dxu.sso.common.dto.user.AppUserDto;
import com.dxu.sso.common.exception.SsoApplicationException;
import com.dxu.sso.common.security.RequireUserProfile;
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

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/profile/me")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping()
    public ResponseEntity<AppUserDto> getProfile(@AuthenticationPrincipal Jwt jwt)
            throws SsoApplicationException {
        log.info("Fetching profile");

        String email = jwt.getClaimAsString("email");
        AppUserDto user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }

    @RequireUserProfile
    @PutMapping()
    public ResponseEntity<AppUserDto> updateProfile(@AuthenticationPrincipal Jwt jwt,
                                                    @Valid @RequestBody AppUserDto update)
            throws SsoApplicationException {
        log.info("Updating profile");

        String email = jwt.getClaimAsString("email");
        AppUserDto user = userService.updateByEmail(email, update);
        return ResponseEntity.ok(user);
    }

    /**
     * This is called when users log in to the system for the first time and there is not profile created yet.
     *
     * @param jwt Jwt token
     * @return the created user profile
     */
    @PostMapping
    public ResponseEntity<AppUserDto> create(@AuthenticationPrincipal Jwt jwt) throws SsoApplicationException {
        log.info("Creating profile for current user");

        AppUserDto user = userService.create(
                jwt.getClaimAsString("sub"),
                jwt.getClaimAsString("email"),
                jwt.getClaimAsString("firstName"),
                jwt.getClaimAsString("lastName"));

        return ResponseEntity.ok(user);
    }

    @GetMapping("token")
    public Map<String, Object> getTokenClaims(@AuthenticationPrincipal Jwt jwt) {
        return jwt.getClaims();
    }
}
