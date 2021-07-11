package com.sayedbaladoh.ecommerce.config.documentation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {
	
	@Value("${info.app.name}")
	private String title;
	@Value("${info.app.description}")
	private String description;
	@Value("${info.app.version}")
	private String version;

	@Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(getApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.sayedbaladoh.ecommerce"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo getApiInfo() {
        Contact contact = new Contact("Sayed Baladoh", "https://www.linkedin.com/in/SayedBaladoh/",
				"sayedbaladoh@yahoo.com");
        return new ApiInfoBuilder()
                .title(title.toUpperCase())
                .description(description)
                .version(version)
                .license("Apache License Version 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
                .contact(contact)
                .build();
    }
}
