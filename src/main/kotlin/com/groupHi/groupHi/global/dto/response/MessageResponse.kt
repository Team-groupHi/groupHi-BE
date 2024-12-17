package com.groupHi.groupHi.global.dto.response

import com.groupHi.groupHi.global.dto.MessageType
import com.groupHi.groupHi.global.exception.error.MessageError

data class MessageResponse(
    val type: MessageType,
    val sender: String? = "System",
    val content: Any? = null
) {

    companion object {
        fun error(error: MessageError): MessageResponse {
            return MessageResponse(
                type = MessageType.ERROR,
                content = MessageErrorResponse.from(error)
            )
        }
    }
}
