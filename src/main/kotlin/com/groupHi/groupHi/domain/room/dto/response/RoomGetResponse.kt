package com.groupHi.groupHi.domain.room.dto.response

import com.groupHi.groupHi.domain.game.dto.response.GameGetResponse
import com.groupHi.groupHi.domain.room.RoomStatus
import com.groupHi.groupHi.domain.room.service.PlayerResponse

data class RoomGetResponse(
    val id: String,
    val status: RoomStatus,
    val hostName: String?,
    val players: List<PlayerResponse>,
    val game: GameGetResponse
)
