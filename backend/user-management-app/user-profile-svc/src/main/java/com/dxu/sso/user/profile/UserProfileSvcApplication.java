package com.dxu.sso.user.profile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EntityScan(basePackages = {
        "com.dxu.sso.common.model",       // include shared Course entity
        "com.dxu.sso.user.profile.model"  // if you have your own entities
})
@ComponentScan(basePackages = {
        "com.dxu.sso.user.profile",
        "com.dxu.sso.common"           // shared library "common-lib"
})
public class UserProfileSvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserProfileSvcApplication.class, args);
    }

}
