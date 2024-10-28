package com.groupHi.groupHi.domain.game.balanceGame.dto.response

data class BalanceGameResultGetResponse(
    val turn: Int,
    val question: String,
    val a: List<String>,
    val b: List<String>
)
