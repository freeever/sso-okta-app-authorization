package com.dxu.sso.user.profile.service;

import com.dxu.sso.common.exception.SsoApplicationException;
import com.dxu.sso.common.model.AppUser;
import com.dxu.sso.user.profile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService extends OidcUserService {

    private final UserRepository userRepository;

    public AppUser findByEmail(String email) throws SsoApplicationException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new SsoApplicationException(HttpStatus.NOT_FOUND.value(), "User profile not found"));
    }

    public AppUser updateByEmail(String email, AppUser update) throws SsoApplicationException {
        AppUser existing = findByEmail(email);

        existing.setFirstName(update.getFirstName());
        existing.setLastName(update.getLastName());
        existing.setGender(update.getGender());
        existing.setDateOfBirth(update.getDateOfBirth());
        existing.setRole(update.getRole());

        return userRepository.save(existing);
    }

    public AppUser create(String oktaUserId, String email, String firstName, String lastName) throws SsoApplicationException {
        AppUser user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            return user;
        }

        return userRepository.save(AppUser.builder()
                .email(email)
                .oktaUserId(oktaUserId)
                .firstName(firstName)
                .lastName(lastName)
                .build());
    }
}
