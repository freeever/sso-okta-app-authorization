package com.dxu.sso.common.security;

import com.dxu.sso.common.dto.user.AppUserDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.annotation.SessionScope;

@SessionScope
@Component
@Getter
@Setter
public class UserContext {
    private AppUserDto appUser;
}
