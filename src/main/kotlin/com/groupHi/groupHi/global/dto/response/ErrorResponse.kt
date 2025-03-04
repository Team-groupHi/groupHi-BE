package com.groupHi.groupHi.global.dto.response

import com.groupHi.groupHi.global.exception.error.ErrorCode

data class ErrorResponse(
    val code: String,
    val message: String
) {

    companion object {
        fun from(e: ErrorCode): ErrorResponse {
            return ErrorResponse(e.code, e.message)
        }
    }
}
