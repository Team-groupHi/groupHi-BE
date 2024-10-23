package com.groupHi.groupHi.domain.game.controller

import com.groupHi.groupHi.domain.game.dto.response.GameGetResponse
import com.groupHi.groupHi.domain.game.service.GameService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class GameController(private val gameService: GameService) {

    @GetMapping("/games")
    fun getGames(): List<GameGetResponse> = gameService.getGames()
}
