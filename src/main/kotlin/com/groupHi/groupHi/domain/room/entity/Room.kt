package com.groupHi.groupHi.domain.room.entity

data class Room(
    val id: String,
    val status: RoomStatus,
    val gameId: String,
    val hostName: String? = null, //TODO: 삭제
)
