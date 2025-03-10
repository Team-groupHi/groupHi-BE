package com.groupHi.groupHi.domain.game.balanceGame.dto.response

import java.time.Instant

data class BalanceGameRoundResponse(
    val totalRounds: Int,
    val currentRound: Int,
    val startTime: Instant,
    val endTime: Instant,
    val q: String,
    val a: String,
    val b: String
)
