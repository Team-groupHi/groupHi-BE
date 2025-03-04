package com.groupHi.groupHi.global.exception.exception

import com.groupHi.groupHi.global.dto.response.ErrorResponse
import com.groupHi.groupHi.global.exception.error.ErrorCode

class MessageException(private val e: ErrorCode) : RuntimeException(e.message), BaseException {
    override val code: String get() = e.code
    override val message: String get() = e.message
    override val response: ErrorResponse get() = ErrorResponse.from(e)
}
