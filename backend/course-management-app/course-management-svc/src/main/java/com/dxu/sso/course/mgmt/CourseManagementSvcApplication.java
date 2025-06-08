package com.dxu.sso.course.mgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EntityScan(basePackages = {
        "com.dxu.sso.common.model.course",   // 👈 include shared Course entity
        "com.dxu.sso.course.mgmt.model"      // if you have your own entities
})
@ComponentScan(basePackages = {
        "com.dxu.sso.course.mgmt",
        "com.dxu.sso.common"           // shared library "common-lib"
})
public class CourseManagementSvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseManagementSvcApplication.class, args);
    }

}
