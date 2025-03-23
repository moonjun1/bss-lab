package com.bsslab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableJpaAuditing
public class BsslabApplication {

	public static void main(String[] args) {
		SpringApplication.run(BsslabApplication.class, args);
	}

	// 이 메소드는 Swagger 경로를 정상적으로 인식하기 위해 추가합니다
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				registry.addResourceHandler("/swagger-ui/**")
						.addResourceLocations("classpath:/META-INF/resources/webjars/springdoc-openapi-ui/")
						.resourceChain(false);
			}
		};
	}
}