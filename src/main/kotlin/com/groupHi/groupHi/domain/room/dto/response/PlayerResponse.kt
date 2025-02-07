package com.groupHi.groupHi.domain.room.dto.response

import com.groupHi.groupHi.domain.room.entity.Player

data class PlayerResponse(
    val name: String,
    val avatar: String,
    val isHost: Boolean,
    val isReady: Boolean,
) {

    companion object {
        fun from(player: Player): PlayerResponse {
            return PlayerResponse(
                name = player.name,
                avatar = player.avatar,
                isHost = player.isHost,
                isReady = player.isReady
            )
        }
    }
}
