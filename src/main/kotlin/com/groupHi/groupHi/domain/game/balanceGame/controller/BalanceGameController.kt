package com.groupHi.groupHi.domain.game.balanceGame.controller

import com.groupHi.groupHi.domain.game.balanceGame.dto.request.BalanceGameResultCreateRequest
import com.groupHi.groupHi.domain.game.balanceGame.dto.response.BalanceGameResultGetResponse
import com.groupHi.groupHi.domain.game.balanceGame.service.BalanceGameService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(name = "BalanceGame")
@RestController
@RequestMapping("/api/v1/games")
class BalanceGameController(private val balanceGameService: BalanceGameService) {

    @Operation(summary = "밸런스 게임 부분 결과 생성")
    @PostMapping("/balance-game/results")
    fun createBalanceGameResult(@RequestBody request: BalanceGameResultCreateRequest): BalanceGameResultGetResponse {
        return balanceGameService.createBalanceGameResult(request)
    }

    @Operation(summary = "밸런스 게임 모든 결과 조회")
    @GetMapping("/balance-game/results")
    fun getBalanceGameResults(@RequestParam roomId: String): List<BalanceGameResultGetResponse> {
        return balanceGameService.getBalanceGameResults(roomId)
    }
}
