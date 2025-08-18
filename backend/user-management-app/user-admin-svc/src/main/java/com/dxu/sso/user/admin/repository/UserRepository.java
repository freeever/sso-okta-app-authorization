package com.dxu.sso.user.admin.repository;

import com.dxu.sso.common.model.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    List<AppUser> findByIdIn(List<Long> ids);

    AppUser findByIdAndRole(Long id, String role);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update AppUser u set u.deleted = :deleted where u.id = :id")
    void markDeleted(@Param("id") Long id, @Param("deleted") boolean deleted);
}
