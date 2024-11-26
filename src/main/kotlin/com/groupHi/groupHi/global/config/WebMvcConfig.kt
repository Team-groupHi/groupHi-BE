package com.groupHi.groupHi.global.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig: WebMvcConfigurer {

    override fun addCorsMappings(registry: org.springframework.web.servlet.config.annotation.CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
    }
}
