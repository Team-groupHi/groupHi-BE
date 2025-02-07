package com.groupHi.groupHi.domain.room.entity

import com.groupHi.groupHi.domain.room.repository.RoomStatus

data class Room(
    val id: String,
    val status: RoomStatus,
    val gameId: String,
    val hostName: String? = null,
    val players: MutableList<Player> = mutableListOf()
)
