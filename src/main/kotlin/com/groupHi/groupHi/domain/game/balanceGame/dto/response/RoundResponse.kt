package com.groupHi.groupHi.domain.game.balanceGame.dto.response

import java.time.LocalDateTime

data class RoundResponse(
    val totalRounds: Int,
    val currentRound: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val q: String,
    val a: String,
    val b: String
)
