package com.groupHi.groupHi.domain.room.dto.response

import com.groupHi.groupHi.domain.game.dto.response.GameGetResponse
import com.groupHi.groupHi.domain.room.service.PlayerResponse
import com.groupHi.groupHi.domain.room.service.RoomStatus

data class RoomGetResponse(
    val id: String,
    val status: RoomStatus,
    val game: GameGetResponse,
    val hostName: String?,
    val players: List<PlayerResponse>
)
