package com.dxu.sso.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseApplicationApprovedEvent {
    private Long courseId;
    private Long studentId;
    private LocalDateTime approvedAt;
}
