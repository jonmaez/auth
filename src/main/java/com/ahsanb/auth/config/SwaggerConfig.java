package com.ahsanb.auth.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@Configuration
@EnableSwagger2WebMvc
//@Import(SpringDataRestConfiguration.class)
public class SwaggerConfig {
    @Bean
    public Docket authApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("users")
                .apiInfo(apiInfo())
                .host("localhost:8300")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.ahsanb.auth.controllers"))
                .build();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Auth")
                .description(
                        "The Auth API is a Create, Read, Update, and Delete (CRUD) API used to manage users, login and logout")
                .contact(new Contact("Ahsan Bhai", null, null))
                .license("Copyright (c) Ahsan Bhai - All Rights Reserved")
                .version("v1")
                .build();

    }
}
