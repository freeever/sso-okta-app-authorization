package com.dxu.sso.user.admin.service;

import com.dxu.sso.common.constant.Role;
import com.dxu.sso.common.exception.SsoApplicationException;
import com.dxu.sso.common.model.user.AppUser;
import com.dxu.sso.user.admin.dto.student.StudentDeleteSagaStartResponse;
import com.dxu.sso.user.admin.dto.student.StudentDeleteSagaStatusDto;
import com.dxu.sso.user.admin.model.common.SagaStatus;
import com.dxu.sso.user.admin.model.student.StudentDeleteSaga;
import com.dxu.sso.user.admin.repository.StudentDeleteSagaRepository;
import com.dxu.sso.user.admin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class StudentService {

    private final UserRepository userRepository;
    private final StudentDeleteOrchestrator deleteOrchestrator;
    private final StudentDeleteSagaRepository deleteSagaRepository;

    /**
     * Start the student delete saga, return the immediate status with the URL to retrieve the updated status
     * @param studentId student id
     * @return the student delete saga information
     */
    public StudentDeleteSagaStartResponse deleteStudent(Long studentId) {
        AppUser student = userRepository.findByIdAndRole(studentId, Role.STUDENT.name());
        if (student == null) {
            throw new SsoApplicationException(HttpStatus.BAD_REQUEST.value(), "Student not found:" + studentId);
        }

        UUID sagaId = deleteOrchestrator.startDeleteStudent(studentId);
        URI statusUrl = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/sagas/{sagaId}")
                .buildAndExpand(sagaId)
                .toUri();

        return new StudentDeleteSagaStartResponse(sagaId, SagaStatus.FORWARDING, studentId, statusUrl);
    }

    /**
     * Fetch the student delete saga information
     * @param sagaId the Id of the student delete saga
     * @param studentId the student user id
     * @return the student delete saga information
     */
    public StudentDeleteSagaStatusDto getSaga(UUID sagaId, Long studentId) {
        log.info("fetching saga by id: {}", sagaId);

        StudentDeleteSaga saga = deleteSagaRepository.findByIdAndStudentId(sagaId, studentId)
                .orElseThrow(() -> new SsoApplicationException(HttpStatus.NOT_FOUND.value(), "Saga not found"));
        return StudentDeleteSagaStatusDto.from(saga);
    }

    /**
     * Get the latest active student deletion status by the given student id
     * Active status: one of the following statuses:
     *  - STARTED
     *  - FORWARDING
     *  - COMPENSATING
     * @param studentId student id
     * @return the latest active student deletion status
     */
    public StudentDeleteSagaStatusDto getActiveDelStatusByStudentId(Long studentId) {
        log.info("fetching latest del status by studentId: {}", studentId);

        return deleteSagaRepository.findFirstByStudentIdAndStatusIn(studentId,
                List.of(SagaStatus.STARTED, SagaStatus.FORWARDING, SagaStatus.COMPENSATING))
                .map(StudentDeleteSagaStatusDto::from)
                .orElse(null);
    }
}
