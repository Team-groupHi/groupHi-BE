package com.groupHi.groupHi.domain.game.balanceGame.repository

import com.groupHi.groupHi.domain.game.balanceGame.BalanceGameTheme
import com.groupHi.groupHi.domain.game.balanceGame.entity.BalanceGameContent
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface BalanceGameContentRepository : MongoRepository<BalanceGameContent, String> {

    fun findByTheme(theme: BalanceGameTheme): List<BalanceGameContent>
}
