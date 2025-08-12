package com.dxu.sso.common.event.common;

import java.time.Instant;
import java.util.UUID;

/**
 * Base envelope for traceability
 */
public interface SagaEvent {
    UUID sagaId();       // same ID for all events in one saga instance
    Instant timestamp();
}
