package com.groupHi.groupHi.global.exception.error

import org.springframework.http.HttpStatus

enum class ApiError(
    val statusCode: HttpStatus,
    val code: String,
    val message: String
) {

    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E000", "Internal Server Error"),
}
