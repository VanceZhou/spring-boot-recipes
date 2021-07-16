package com.example.recipe.Config;

import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket recipeApi(){
        return new Docket(DocumentationType.SWAGGER_2)
//                .host("http://localhost:8080")
                .select()
                .apis((Predicate<RequestHandler>) RequestHandlerSelectors.any())
                .paths((Predicate<String>) PathSelectors.any())
                .build()
                .apiInfo(getApoInfo());


    }

    private ApiInfo getApoInfo() {
        return new ApiInfoBuilder()
                .title("recipe API")
                .version("1.0")
                .description("API for recipe App")

                .license("Apache License Version 2.0")
                .build();
    }
}
