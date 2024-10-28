package com.groupHi.groupHi.domain.game.balanceGame.service

import com.groupHi.groupHi.domain.game.balanceGame.dto.request.BalanceGameResultCreateRequest
import com.groupHi.groupHi.domain.game.balanceGame.dto.response.BalanceGameResultGetResponse
import org.springframework.stereotype.Service

@Service
class BalanceGameService(private val balanceGameCacheService: BalanceGameCacheService) {

    fun createBalanceGameResult(request: BalanceGameResultCreateRequest): BalanceGameResultGetResponse {
        balanceGameCacheService.createBalanceGameResult(request)
        return balanceGameCacheService.getBalanceGameResult(request.roomId, request.turn)
    }

    fun getBalanceGameResults(roomId: String): List<BalanceGameResultGetResponse> {
        return balanceGameCacheService.getBalanceGameResults(roomId)
    }
}
