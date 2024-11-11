package com.groupHi.groupHi.domain.game.balanceGame.dto.response

data class BalanceGameResultGetResponse(
    val round: Int,
    val q: String,
    val a: String,
    val b: String,
    val result: BalanceGameSelectionsResponse
)

data class BalanceGameSelectionsResponse(
    val a: List<String>,
    val b: List<String>,
    val c: List<String>
)
