package com.groupHi.groupHi.global.dto.response

import com.groupHi.groupHi.global.exception.error.MessageError

data class MessageErrorResponse(
    val code: String,
    val message: String
) {

    companion object {
        fun from(e: MessageError): MessageErrorResponse {
            return MessageErrorResponse(e.code, e.message)
        }
    }
}
