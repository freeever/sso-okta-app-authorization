package com.dxu.sso.user.admin.controller;

import com.dxu.sso.common.dto.user.AppUserDto;
import com.dxu.sso.common.exception.SsoApplicationException;
import com.dxu.sso.common.security.RequireRoles;
import com.dxu.sso.user.admin.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @RequireRoles({"ADMIN", "TEACHER"})
    @GetMapping("/{id}")
    public ResponseEntity<AppUserDto> findById(@PathVariable Long id) throws SsoApplicationException {
        log.info("get user by id: {}", id);

        AppUserDto user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @RequireRoles({"ADMIN", "TEACHER"})
    @GetMapping
    public List<AppUserDto> findAll() {
        log.info("find all users");

        return userService.findAll();
    }

    @RequireRoles({"ADMIN"})
    @PutMapping("/{id}")
    public ResponseEntity<AppUserDto> update(@PathVariable Long id, @Valid @RequestBody AppUserDto update)
            throws SsoApplicationException {
        log.info("update user by id: {}", id);

        AppUserDto user = userService.updateUser(id, update);
        return ResponseEntity.ok(user);
    }

    @RequireRoles({"ADMIN"})
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("delete users by id");

        userService.deleteById(id);
    }
}
