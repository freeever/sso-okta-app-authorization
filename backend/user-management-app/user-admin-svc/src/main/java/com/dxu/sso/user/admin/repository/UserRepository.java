package com.dxu.sso.user.admin.repository;

import com.dxu.sso.common.model.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    List<AppUser> findByIdIn(List<Long> ids);
}
