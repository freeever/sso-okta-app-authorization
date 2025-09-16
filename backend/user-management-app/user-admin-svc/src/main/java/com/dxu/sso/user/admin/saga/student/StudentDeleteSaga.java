package com.dxu.sso.user.admin.saga.student;

import com.dxu.sso.user.admin.saga.common.SagaStatus;
import com.dxu.sso.user.admin.saga.common.StepStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "student_delete_saga")
public class StudentDeleteSaga {

    @Id
    private UUID id;

    @Column(nullable = false)
    private Long studentId;

    @Enumerated(EnumType.STRING)
    private SagaStatus status; // STARTED, FORWARDING, COMPLETED, COMPENSATING, FAILED

    @Enumerated(EnumType.STRING)
    private StepStatus enrollments;  // PENDING, DONE, FAILED, COMP_DONE, COMP_FAILED
    @Enumerated(EnumType.STRING)
    private StepStatus applications; // PENDING, DONE, FAILED, COMP_DONE, COMP_FAILED

    private Instant startedAt;
    private Instant updatedAt;

    @Version
    private Long version;
}
