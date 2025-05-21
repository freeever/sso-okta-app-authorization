package com.dxu.sso.course.query;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EntityScan(basePackages = {
        "com.dxu.sso.common.model",       // 👈 include shared Course entity
        "com.dxu.sso.course.query.model"  // if you have your own entities
})
@ComponentScan(basePackages = {
        "com.dxu.sso.course.query",
        "com.dxu.sso.common"           // shared library "common-lib"
})
public class CourseQuerySvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseQuerySvcApplication.class, args);
    }

}
