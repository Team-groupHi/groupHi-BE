package com.groupHi.groupHi.global.dto.response

import com.groupHi.groupHi.global.exception.error.ApiError

data class ApiErrorResponse(
    val code: String,
    val message: String
) {

    companion object {
        fun from(e: ApiError): ApiErrorResponse {
            return ApiErrorResponse(e.code, e.message)
        }
    }
}
