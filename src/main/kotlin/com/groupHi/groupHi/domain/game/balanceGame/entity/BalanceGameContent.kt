package com.groupHi.groupHi.domain.game.balanceGame.entity

import com.groupHi.groupHi.domain.game.balanceGame.BalanceGameTheme
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "balanceGameContents")
class BalanceGameContent(
    @Id val id: String,
    val theme: BalanceGameTheme,
    val q: String,
    val a: String,
    val b: String
)
