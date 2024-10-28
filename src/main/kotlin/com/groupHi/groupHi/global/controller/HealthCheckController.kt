package com.groupHi.groupHi.global.controller

import io.swagger.v3.oas.annotations.Hidden
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Hidden
@RestController
class HealthCheckController {

    @GetMapping
    fun healthCheck(): String {
        return "Hello, World!"
    }
}
