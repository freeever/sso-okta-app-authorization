package com.dxu.sso.user.admin.service;

import com.dxu.sso.common.exception.SsoApplicationException;
import com.dxu.sso.common.model.AppUser;
import com.dxu.sso.user.admin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public List<AppUser> findAll() {
        log.info("find all users");
        return userRepository.findAll();
    }

    public AppUser findById(Long id) throws SsoApplicationException {
        log.info("find user by id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new SsoApplicationException(HttpStatus.BAD_REQUEST.value(), "User not found"));
    }

    public AppUser updateUser(Long id, AppUser update) throws SsoApplicationException {
        AppUser existing = userRepository.findById(id).orElse(null);
        if (existing == null) {
            throw new SsoApplicationException(HttpStatus.BAD_REQUEST.value(), "User not found");
        }

        existing.setFirstName(update.getFirstName());
        existing.setLastName(update.getLastName());
        existing.setGender(update.getGender());
        existing.setDateOfBirth(update.getDateOfBirth());
        existing.setRole(update.getRole());

        return userRepository.save(existing);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
