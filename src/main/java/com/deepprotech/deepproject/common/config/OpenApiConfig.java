package com.deepprotech.deepproject.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String SECURITY_SCHEME = "keycloak_oauth";

    @Bean
    public OpenAPI deepProjectOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Deep Project API")
                        .description("REST API for Deep Project task management platform")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME, new SecurityScheme()
                                .name(SECURITY_SCHEME)
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new OAuthFlows()
                                        .authorizationCode(new OAuthFlow()
                                                .authorizationUrl("http://localhost:8090/realms/deepproject/protocol/openid-connect/auth")
                                                .tokenUrl("http://localhost:8090/realms/deepproject/protocol/openid-connect/token")
                                                .scopes(new Scopes().addString("openid", "OpenID Connect"))))));
    }
}