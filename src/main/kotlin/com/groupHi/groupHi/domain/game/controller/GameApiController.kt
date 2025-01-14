package com.groupHi.groupHi.domain.game.controller

import com.groupHi.groupHi.domain.game.dto.response.GameGetResponse
import com.groupHi.groupHi.domain.game.service.GameService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Game")
@RestController
@RequestMapping("/api/v1")
class GameApiController(private val gameService: GameService) {

    @Operation(summary = "게임 목록 조회")
    @GetMapping("/games")
    fun getGames(): List<GameGetResponse> = gameService.getGames()
}
