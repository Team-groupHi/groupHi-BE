package com.groupHi.groupHi.domain.game.balanceGame.dto.request

import com.groupHi.groupHi.domain.game.balanceGame.BalanceGameTheme

data class BalanceGameStartRequest(
    val theme: BalanceGameTheme,
    val totalRounds: Int
)
