package com.groupHi.groupHi.global.exception.handler

import com.groupHi.groupHi.global.dto.response.ErrorResponse
import com.groupHi.groupHi.global.exception.error.ErrorCode
import com.groupHi.groupHi.global.exception.exception.ApiException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ApiExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(ApiException::class)
    protected fun handleApiException(e: ApiException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(e.statusCode).body(e.response)
    }

    @ExceptionHandler(Exception::class)
    protected fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        println("🚨 $e")
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.from(ErrorCode.INTERNAL_SERVER_ERROR))
    }
}
