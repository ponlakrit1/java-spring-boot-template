package com.nctine.template.template.component;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Data
@Component
@Scope(
    value = "request",
    proxyMode = ScopedProxyMode.TARGET_CLASS
)
public class UserComponent {
    private Integer userId;

    private String username;

    private String email;
}
