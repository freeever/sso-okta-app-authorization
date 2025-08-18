package com.dxu.sso.user.admin.controller;

import com.dxu.sso.common.security.RequireRoles;
import com.dxu.sso.user.admin.dto.student.StudentDeleteSagaStartResponse;
import com.dxu.sso.user.admin.dto.student.StudentDeleteSagaStatusDto;
import com.dxu.sso.user.admin.repository.StudentDeleteSagaRepository;
import com.dxu.sso.user.admin.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/students")
public class StudentController {

    private final StudentService service;
    private final StudentDeleteSagaRepository sagaRepo;

    @RequireRoles({"ADMIN"})
    @DeleteMapping("/{id}")
    public ResponseEntity<StudentDeleteSagaStartResponse> deleteStudent(@PathVariable Long id) {
        log.info("start deleting student: {}", id);

        StudentDeleteSagaStartResponse response = service.deleteStudent(id);
        return ResponseEntity.accepted()
                .location(response.statusUrl())
                .body(response);
    }

    @RequireRoles({"ADMIN"})
    @GetMapping("/{studentId}/sagas/{sagaId}")
    public StudentDeleteSagaStatusDto getSaga(@PathVariable Long studentId, @PathVariable UUID sagaId) {
        log.info("Get saga by id: {}", sagaId);

        return service.getSaga(sagaId, studentId);
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
    @RequireRoles({"ADMIN"})
    @GetMapping("/{studentId}/deletion-status")
    public ResponseEntity<StudentDeleteSagaStatusDto> getActiveDelStatusByStudentId(@PathVariable Long studentId) {
        log.info("Get deletion status of student: {}", studentId);

        StudentDeleteSagaStatusDto status = service.getActiveDelStatusByStudentId(studentId);
        return status != null ? ResponseEntity.ok(status) : ResponseEntity.noContent().build();
    }
}
