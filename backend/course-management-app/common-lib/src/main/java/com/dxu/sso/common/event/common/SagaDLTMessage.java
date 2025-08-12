package com.dxu.sso.common.event.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SagaDLTMessage {
    private SagaEvent originalEvent;
    private String errorMessage;
    private String stackTrace;
}
