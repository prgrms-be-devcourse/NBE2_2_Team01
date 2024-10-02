package me.seunghui.springbootdeveloper.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
//http://localhost:8080/swagger-ui/index.html#/
@OpenAPIDefinition(
        info=@Info(
                title = "REST API",
                version = "ver 0.1",
                description = "RESTful API Documentation"
        ),
        servers = {@Server(
                description = "Prod ENV",
                url = "http://localhost:8080/"
        ),@Server(
                description = "Staging ENV",
                url = "http://localhost:8080/staging"
        )
        }
)

public class SwaggerOpenAPIConfig {
}
//Swagger는 RESTful API를 문서화하고 테스트할 수 있는 도구