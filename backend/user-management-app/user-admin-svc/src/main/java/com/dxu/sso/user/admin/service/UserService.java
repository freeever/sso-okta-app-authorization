package com.dxu.sso.user.admin.service;

import com.dxu.sso.common.dto.mapper.AppUserMapper;
import com.dxu.sso.common.dto.user.AppUserDto;
import com.dxu.sso.common.exception.SsoApplicationException;
import com.dxu.sso.common.model.user.AppUser;
import com.dxu.sso.user.admin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final AppUserMapper userMapper;

    public List<AppUserDto> findAll() {
        log.info("find all users");
        List<AppUser> users = userRepository.findAll();
        return users.stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    public AppUserDto findById(Long id) throws SsoApplicationException {
        log.info("find user by id: {}", id);
        AppUser appUser = userRepository.findById(id)
                .orElseThrow(() -> new SsoApplicationException(HttpStatus.BAD_REQUEST.value(), "User not found"));
        return appUser == null ? null : userMapper.toDto(appUser);
    }

    public AppUserDto updateUser(Long id, AppUserDto updateDto) throws SsoApplicationException {
        AppUser userEntity = userRepository.findById(id)
                .orElseThrow(() -> new SsoApplicationException(HttpStatus.NOT_FOUND.value(), "User profile not found"));

        userEntity.setFirstName(updateDto.getFirstName());
        userEntity.setLastName(updateDto.getLastName());
        userEntity.setGender(updateDto.getGender());
        userEntity.setDateOfBirth(updateDto.getDateOfBirth());
        userEntity.setRole(updateDto.getRole());

        AppUser updated = userRepository.save(userEntity);
        return userMapper.toDto(updated);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
