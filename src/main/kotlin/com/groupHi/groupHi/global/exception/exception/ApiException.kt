package com.groupHi.groupHi.global.exception.exception

import com.groupHi.groupHi.global.dto.response.ApiErrorResponse
import com.groupHi.groupHi.global.exception.error.ApiError

class ApiException(private val e: ApiError) : RuntimeException(e.message) {

    val statusCode: Int get() = e.statusCode.value()
    val response: ApiErrorResponse get() = ApiErrorResponse.from(e)
}
