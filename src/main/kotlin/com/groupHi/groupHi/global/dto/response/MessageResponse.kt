package com.groupHi.groupHi.global.dto.response

import com.groupHi.groupHi.global.dto.MessageType
import com.groupHi.groupHi.global.exception.error.ErrorCode

data class MessageResponse(
    val type: MessageType,
    val sender: String? = "System",
    val content: Any? = null
) {

    companion object {
        fun error(e: ErrorCode): MessageResponse {
            return MessageResponse(
                type = MessageType.ERROR,
                content = ErrorResponse.from(e)
            )
        }
    }
}
