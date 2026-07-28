package com.demo.demo.config;

import com.demo.demo.security.AuthMode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "auth")
public class SecurityProperties {

    private AuthMode mode = AuthMode.JWT;

    private String preAuthKey;
}