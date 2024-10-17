package com.groupHi.groupHi.domain.game.service

import com.groupHi.groupHi.domain.game.dto.response.GameGetResponse
import com.groupHi.groupHi.storage.game.repository.GameRepository
import org.springframework.stereotype.Service

@Service
class GameService(private val gameRepository: GameRepository) {

    fun getGames(): List<GameGetResponse> = gameRepository.findAll().map { GameGetResponse.from(it) }
}
