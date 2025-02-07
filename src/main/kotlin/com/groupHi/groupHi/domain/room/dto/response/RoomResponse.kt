package com.groupHi.groupHi.domain.room.dto.response

import com.groupHi.groupHi.domain.game.dto.response.GameGetResponse
import com.groupHi.groupHi.domain.room.entity.RoomStatus

data class RoomResponse(
    val id: String,
    val status: RoomStatus,
    val hostName: String?, //TODO: 삭제
    val game: GameGetResponse,
    val players: List<PlayerResponse>
)
