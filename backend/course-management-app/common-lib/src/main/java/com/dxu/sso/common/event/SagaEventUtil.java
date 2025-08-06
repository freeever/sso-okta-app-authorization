package com.dxu.sso.common.event;

public class SagaEventUtil {

    public static String buildCourseApplicationEvtKey(Long courseId, Long studentId) {
        return String.format("course-%d|stu-%d", courseId, studentId);
    }
}
