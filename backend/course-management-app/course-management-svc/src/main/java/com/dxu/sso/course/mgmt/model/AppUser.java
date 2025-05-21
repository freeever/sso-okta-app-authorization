package com.dxu.sso.course.mgmt.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AppUser {
    private String email;
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;
    private String role;
}

