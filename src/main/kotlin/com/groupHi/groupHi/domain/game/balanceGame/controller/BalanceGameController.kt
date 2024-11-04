package com.groupHi.groupHi.domain.game.balanceGame.controller

import com.groupHi.groupHi.domain.game.balanceGame.dto.response.BalanceGameResultGetResponse
import com.groupHi.groupHi.domain.game.balanceGame.service.BalanceGameService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "BalanceGame")
@RestController
@RequestMapping("/api/v1/games")
class BalanceGameController(private val balanceGameService: BalanceGameService) {

    @Operation(summary = "밸런스 게임 결과 조회")
    @GetMapping("/balance-game/results")
    fun getBalanceGameResults(
        @RequestParam roomId: String,
        @RequestParam(required = false) round: Int?
    ): List<BalanceGameResultGetResponse> {
        return balanceGameService.getBalanceGameResults(roomId, round)
    }
}
