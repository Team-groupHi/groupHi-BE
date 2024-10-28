package com.groupHi.groupHi.domain.game.balanceGame.controller

import com.groupHi.groupHi.domain.game.balanceGame.dto.request.BalanceGameResultCreateRequest
import com.groupHi.groupHi.domain.game.balanceGame.dto.response.BalanceGameResultGetResponse
import com.groupHi.groupHi.domain.game.balanceGame.service.BalanceGameService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/games")
class BalanceGameController(private val balanceGameService: BalanceGameService) {

    @PostMapping("/balance-game/results")
    fun createBalanceGameResult(@RequestBody request: BalanceGameResultCreateRequest): BalanceGameResultGetResponse {
        return balanceGameService.createBalanceGameResult(request)
    }

    @GetMapping("/balance-game/results")
    fun getBalanceGameResults(@RequestParam roomId: String): List<BalanceGameResultGetResponse> {
        return balanceGameService.getBalanceGameResults(roomId)
    }
}
