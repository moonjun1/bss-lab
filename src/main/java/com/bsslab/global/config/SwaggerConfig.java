package com.bsslab.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        return new OpenAPI()
                .info(apiInfo())
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", securityScheme))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }

    private Info apiInfo() {
        return new Info()
                .title("BSS-Lab API")
                .description("BSS-Lab 소개 페이지 백엔드 API입니다.\n\n" +
                        "## 인증 방법\n" +
                        "1. `/auth/signup` API로 회원가입\n" +
                        "2. `/auth/login` API로 로그인하여 JWT 토큰 받기\n" +
                        "3. 오른쪽 상단의 'Authorize' 버튼 클릭\n" +
                        "4. 'Bearer [토큰값]' 형식으로 토큰 입력 (예: Bearer eyJhbGciOiJIUzI1...)\n" +
                        "5. 이제 인증이 필요한 API를 테스트할 수 있습니다.\n\n" +
                        "API를 테스트하려면 각 API의 'Try it out' 버튼을 클릭하세요.")
                .version("1.0.0")
                .contact(new Contact()
                        .name("BSS-Lab")
                        .email("admin@bsslab.com")
                        .url("https://www.bsslab.com"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("http://www.apache.org/licenses/LICENSE-2.0.html"));
    }

    @Bean
    public OperationCustomizer customGlobalHeaders() {
        return (operation, handlerMethod) -> {
            Parameter authorizationHeader = new Parameter()
                    .in("header")
                    .name("Authorization")
                    .description("JWT 토큰 (Bearer 접두사 포함)")
                    .example("Bearer eyJhbGciOiJIUzUxMiJ9...")
                    .schema(new io.swagger.v3.oas.models.media.StringSchema())
                    .required(false);

            operation.addParametersItem(authorizationHeader);
            return operation;
        };
    }
}