package com.groupHi.groupHi.domain.room.dto.response

import com.groupHi.groupHi.domain.room.controller.RoomResponseMessageType

data class RoomMessageResponse(
    val type: RoomResponseMessageType,
    val sender: String,
    val content: String?
)
