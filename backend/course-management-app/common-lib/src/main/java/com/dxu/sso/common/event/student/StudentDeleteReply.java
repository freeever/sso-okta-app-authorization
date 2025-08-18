package com.dxu.sso.common.event.student;

import com.dxu.sso.common.event.common.SagaEvent;

import java.time.Instant;
import java.util.UUID;

public record StudentDeleteReply(
        UUID sagaId,
        Instant timestamp,
        Long studentId,
        StudentDeleteReplyType type,
        String reason  // nullable
) implements SagaEvent { }
