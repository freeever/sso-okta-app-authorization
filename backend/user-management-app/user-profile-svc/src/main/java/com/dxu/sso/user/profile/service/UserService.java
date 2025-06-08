package com.dxu.sso.user.profile.service;

import com.dxu.sso.common.dto.mapper.AppUserMapper;
import com.dxu.sso.common.dto.mapper.CourseMapper;
import com.dxu.sso.common.dto.user.AppUserDto;
import com.dxu.sso.common.exception.SsoApplicationException;
import com.dxu.sso.common.model.user.AppUser;
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
    private final AppUserMapper userMapper;

    public AppUserDto findByEmail(String email) throws SsoApplicationException {
        AppUser appUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new SsoApplicationException(HttpStatus.NOT_FOUND.value(), "User profile not found"));
        return appUser == null ? null : userMapper.toDto(appUser);
    }

    public AppUserDto updateByEmail(String email, AppUserDto updateDto) throws SsoApplicationException {
        AppUser userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new SsoApplicationException(HttpStatus.NOT_FOUND.value(), "User profile not found"));

        userEntity.setFirstName(updateDto.getFirstName());
        userEntity.setLastName(updateDto.getLastName());
        userEntity.setGender(updateDto.getGender());
        userEntity.setDateOfBirth(updateDto.getDateOfBirth());
        userEntity.setRole(updateDto.getRole());

        AppUser updated = userRepository.save(userEntity);
        return userMapper.toDto(updated);
    }

    public AppUserDto create(String oktaUserId, String email, String firstName, String lastName) throws SsoApplicationException {
        AppUser user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            return userMapper.toDto(user);
        }

        AppUser newUser = userRepository.save(AppUser.builder()
                .email(email)
                .oktaUserId(oktaUserId)
                .firstName(firstName)
                .lastName(lastName)
                .build());
        return userMapper.toDto(newUser);
    }
}
