package com.dxu.sso.course.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseApplicationDecisionRequest {
    private boolean approve;
    private String comment;
}

