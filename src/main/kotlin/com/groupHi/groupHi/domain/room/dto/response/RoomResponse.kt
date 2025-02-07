package com.groupHi.groupHi.domain.room.dto.response

import com.groupHi.groupHi.domain.room.entity.RoomStatus
import com.groupHi.groupHi.domain.room.repository.PlayerResponse

data class RoomResponse(
    val id: String,
    val status: RoomStatus,
    val gameId: String,
    val hostName: String?,
    val players: List<PlayerResponse>
)
