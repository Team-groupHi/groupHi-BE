package com.groupHi.groupHi.domain.game.balanceGame.dto.request

data class BalanceGameResultCreateRequest(
    val roomId: String,
    val turn: Int,
    val question: String,
    val a: List<String>,
    val b: List<String>
)
