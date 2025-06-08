package com.dxu.sso.common.model.user;

import com.dxu.sso.common.model.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "app_user", schema = "ssouser", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "oktaUserId")
})
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String oktaUserId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    @Builder.Default
    private String role = Role.NONE.name();

    private String gender;

    private LocalDate dateOfBirth;
}
