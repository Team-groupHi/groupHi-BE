package com.groupHi.groupHi.global.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun swaggerApi(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("그루파이 API")
                    .description("그루파이 API 문서입니다.")
                    .version("1.0.0")
            )
            .servers(
                listOf(
                    Server()
                        .url("/")
                        .description("Current Server")
                )
            )
    }
}
