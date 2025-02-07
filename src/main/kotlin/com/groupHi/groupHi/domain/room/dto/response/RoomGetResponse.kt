package com.groupHi.groupHi.domain.room.dto.response

import com.groupHi.groupHi.domain.game.dto.response.GameGetResponse
import com.groupHi.groupHi.domain.room.entity.RoomStatus
import com.groupHi.groupHi.domain.room.repository.PlayerResponse

data class RoomGetResponse(
    val id: String,
    val status: RoomStatus,
    val game: GameGetResponse,
    val hostName: String?,
    val players: List<PlayerResponse>
)
