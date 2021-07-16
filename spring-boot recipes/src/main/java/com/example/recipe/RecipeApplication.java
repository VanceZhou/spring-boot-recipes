package com.example.recipe;

import com.example.recipe.Config.SwaggerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
//import springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration;

@SpringBootApplication
@EnableAsync
@Import(SwaggerConfig.class)
public class RecipeApplication {
	public static void main(String[] args) {
		SpringApplication.run(RecipeApplication.class, args);
	}

}
