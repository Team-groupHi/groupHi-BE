package com.groupHi.groupHi.global.dto.response

import com.groupHi.groupHi.global.dto.MessageType

data class MessageResponse(
    val type: MessageType,
    val sender: String,
    val content: String?
)
