package com.groupHi.groupHi.global.exception.exception

import com.groupHi.groupHi.global.dto.response.MessageErrorResponse
import com.groupHi.groupHi.global.exception.error.MessageError

class MessageException(private val e: MessageError) : RuntimeException(e.message) {
        val response: MessageErrorResponse get() = MessageErrorResponse.from(e)
}
