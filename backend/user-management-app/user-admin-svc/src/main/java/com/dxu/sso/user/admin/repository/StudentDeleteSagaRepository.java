package com.dxu.sso.user.admin.repository;

import com.dxu.sso.user.admin.model.common.SagaStatus;
import com.dxu.sso.user.admin.model.student.StudentDeleteSaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface StudentDeleteSagaRepository extends JpaRepository<StudentDeleteSaga, UUID> {

    Optional<StudentDeleteSaga> findFirstByStudentIdAndStatusIn(Long studentId, Collection<SagaStatus> statuses);

    Optional<StudentDeleteSaga> findByIdAndStudentId(UUID sagaId, Long studentId);
}
