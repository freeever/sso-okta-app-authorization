package com.dxu.sso.common.dto.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseApplicationDto {
    private Long id;
    private Long courseId;
    private String courseName;

    private Long studentId;
    private String studentFirstName;
    private String studentLastName;
    private String studentEmail;

    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime reviewStartedAt;

    private Long reviewerId;
    private String reviewerFirstName;
    private String reviewerLastName;
    private String reviewerEmail;

    private LocalDateTime reviewedAt;
    private String decisionComment;
}

