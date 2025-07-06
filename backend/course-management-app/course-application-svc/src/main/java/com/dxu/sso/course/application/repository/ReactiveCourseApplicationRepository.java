package com.dxu.sso.course.application.repository;

import com.dxu.sso.common.exception.SsoApplicationException;
import com.dxu.sso.common.model.CourseApplicationStatus;
import com.dxu.sso.common.model.courseapp.CourseApplication;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ReactiveCourseApplicationRepository {

    private final CourseApplicationRepository repository;

    public Mono<CourseApplication> findById(Long id) {
        return Mono.fromCallable(() -> repository.findById(id)
                        .orElseThrow(() -> new SsoApplicationException(
                                HttpStatus.BAD_REQUEST.value(), "Application not found")))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Boolean> existsByStudentIdAndCourseIdAndStatusIn(Long studentId, Long courseId, List<CourseApplicationStatus> statuses) {
        return Mono.fromCallable(() -> repository.existsByStudentIdAndCourseIdAndStatusIn(studentId, courseId, statuses))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<CourseApplication> findByCourseIdAndStatus(Long courseId, CourseApplicationStatus status) {
        return Mono.fromCallable(() -> repository.findByCourseIdAndStatus(courseId, status))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);
    }

    public Flux<CourseApplication> findByStudentIdAndCourseId(Long studentId, Long courseId) {
        return Mono.fromCallable(() -> repository.findByStudentIdAndCourseId(studentId, courseId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);
    }

    public Flux<CourseApplication> findByStudentId(Long studentId) {
        return Mono.fromCallable(() -> repository.findByStudentId(studentId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);
    }

    public Mono<CourseApplication> save(CourseApplication entity) {
        return Mono.fromCallable(() -> repository.save(entity))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
