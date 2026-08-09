package com.demo.demo.config;

import com.demo.demo.security.AuthMode;
import com.demo.demo.security.SecurityConstants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private final SecurityProperties securityProperties;

    @Bean
    public OpenAPI bankingOpenAPI() {

        final String jwtSchemeName = "Bearer Authentication";
        final String preAuthKeySchemeName = "Pre-Auth Key";
        final String userEmailSchemeName = "User Email";

        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Banking Management System API")
                        .description("REST APIs for Banking Management System")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Prasad Khose"))
                        .license(new License()
                                .name("MIT License")))
                .components(new Components());

        if (securityProperties.getMode() == AuthMode.JWT) {

            openAPI
                    .addSecurityItem(
                            new SecurityRequirement()
                                    .addList(jwtSchemeName))
                    .getComponents()
                    .addSecuritySchemes(
                            jwtSchemeName,
                            new SecurityScheme()
                                    .name("Authorization")
                                    .type(SecurityScheme.Type.HTTP)
                                    .scheme("bearer")
                                    .bearerFormat("JWT")
                    );

        } else if (securityProperties.getMode() == AuthMode.PREAUTH) {

            openAPI
                    .addSecurityItem(
                            new SecurityRequirement()
                                    .addList(preAuthKeySchemeName)
                                    .addList(userEmailSchemeName))
                    .getComponents()
                    .addSecuritySchemes(
                            preAuthKeySchemeName,
                            new SecurityScheme()
                                    .name(SecurityConstants.PRE_AUTH_KEY_HEADER)
                                    .type(SecurityScheme.Type.APIKEY)
                                    .in(SecurityScheme.In.HEADER)
                    )
                    .addSecuritySchemes(
                            userEmailSchemeName,
                            new SecurityScheme()
                                    .name(SecurityConstants.PRE_AUTH_HEADER)
                                    .type(SecurityScheme.Type.APIKEY)
                                    .in(SecurityScheme.In.HEADER)
                    );
        }

        return openAPI;
    }
}
