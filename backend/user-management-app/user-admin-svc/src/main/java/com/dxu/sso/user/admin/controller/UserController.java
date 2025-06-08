package com.dxu.sso.user.admin.controller;

import com.dxu.sso.common.exception.SsoApplicationException;
import com.dxu.sso.common.model.AppUser;
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
    public ResponseEntity<AppUser> findById(@PathVariable Long id) throws SsoApplicationException {
        log.info("get user by id: {}", id);

        AppUser user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @RequireRoles({"ADMIN", "TEACHER"})
    @GetMapping
    public List<AppUser> findAll() {
        log.info("find all users");

        return userService.findAll();
    }

    @RequireRoles({"ADMIN"})
    @PutMapping("/{id}")
    public ResponseEntity<AppUser> update(@PathVariable Long id, @Valid @RequestBody AppUser update)
            throws SsoApplicationException {
        log.info("update user by id: {}", id);

        AppUser user = userService.updateUser(id, update);
        return ResponseEntity.ok(user);
    }

    @RequireRoles({"ADMIN"})
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("delete users by id");

        userService.deleteById(id);
    }
}
