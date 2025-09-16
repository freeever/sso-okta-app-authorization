package com.dxu.sso.user.admin.dto.student;

import com.dxu.sso.user.admin.saga.student.StudentDeleteSaga;

import java.time.Instant;
import java.util.UUID;

public record StudentDeleteSagaStatusDto (
        UUID sagaId,
        Long studentId,
        String sagaStatus,
        String enrollmentsStep,
        String applicationsStep,
        Instant startedAt,
        Instant updatedAt) {

    public static StudentDeleteSagaStatusDto from(StudentDeleteSaga studentDeleteSaga) {
        return new StudentDeleteSagaStatusDto(
                studentDeleteSaga.getId(),
                studentDeleteSaga.getStudentId(),
                studentDeleteSaga.getStatus().name(),
                studentDeleteSaga.getEnrollments().name(),
                studentDeleteSaga.getApplications().name(),
                studentDeleteSaga.getStartedAt(),
                studentDeleteSaga.getUpdatedAt());
    }
}
