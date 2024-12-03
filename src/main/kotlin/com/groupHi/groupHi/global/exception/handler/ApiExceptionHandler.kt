package com.groupHi.groupHi.global.exception.handler

import com.groupHi.groupHi.global.dto.response.ApiErrorResponse
import com.groupHi.groupHi.global.exception.error.ApiError
import com.groupHi.groupHi.global.exception.exception.ApiException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ApiExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(ApiException::class)
    protected fun handleApiException(e: ApiException): ResponseEntity<ApiErrorResponse> {
        return ResponseEntity.status(e.statusCode).body(e.response)
    }

    @ExceptionHandler(Exception::class)
    protected fun handleException(e: Exception): ResponseEntity<ApiErrorResponse> {
        print("🚨 $e")
        val e = ApiError.INTERNAL_SERVER_ERROR
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiErrorResponse.from(e))
    }
}
