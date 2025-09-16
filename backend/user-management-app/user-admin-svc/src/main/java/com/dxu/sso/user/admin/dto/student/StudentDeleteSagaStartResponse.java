package com.dxu.sso.user.admin.dto.student;

import com.dxu.sso.user.admin.saga.common.SagaStatus;

import java.net.URI;
import java.util.UUID;

public record StudentDeleteSagaStartResponse(
        UUID sagaId,
        SagaStatus status,
        Long studentId,
        URI statusUrl) {
}
