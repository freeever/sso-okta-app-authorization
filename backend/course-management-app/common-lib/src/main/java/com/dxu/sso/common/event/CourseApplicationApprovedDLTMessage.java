package com.dxu.sso.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseApplicationApprovedDLTMessage {
    private CourseApplicationApprovedEvent originalEvent;
    private String errorMessage;
    private String stackTrace;
}
