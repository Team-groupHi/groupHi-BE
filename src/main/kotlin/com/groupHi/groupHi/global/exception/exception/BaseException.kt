package com.groupHi.groupHi.global.exception.exception

import com.groupHi.groupHi.global.dto.response.ErrorResponse

interface BaseException {
    val code: String
    val message: String
    val response: ErrorResponse
}
