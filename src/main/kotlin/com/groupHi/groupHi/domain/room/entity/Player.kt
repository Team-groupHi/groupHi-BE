package com.groupHi.groupHi.domain.room.entity

data class Player(
//    val id: String,
    val name: String,
    var avatar: String? = null,
    val isHost: Boolean,
    val isReady: Boolean
)
