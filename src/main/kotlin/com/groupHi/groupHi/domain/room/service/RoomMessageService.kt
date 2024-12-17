package com.groupHi.groupHi.domain.room.service

import com.groupHi.groupHi.domain.game.dto.response.GameGetResponse
import com.groupHi.groupHi.domain.game.repository.GameRepository
import com.groupHi.groupHi.global.exception.error.MessageError
import com.groupHi.groupHi.global.exception.exception.MessageException
import org.springframework.stereotype.Service

@Service
class RoomMessageService(
    private val roomCacheService: RoomCacheService,
    private val gameRepository: GameRepository
) {

    fun enterRoom(roomId: String, name: String) {
        validateName(roomId, name)
        roomCacheService.enterRoom(roomId, name)
        //TODO: 프로필 컬러 지정 및 리턴
    }

    fun exitRoom(roomId: String, name: String) {
        //TODO: 프로필 컬러 해제
        roomCacheService.exitRoom(roomId, name)
    }

    fun ready(roomId: String, name: String) {
        roomCacheService.ready(roomId, name)
    }

    fun unready(roomId: String, name: String) {
        roomCacheService.unready(roomId, name)
    }

    fun changeGame(roomId: String, name: String, gameId: String): GameGetResponse {
        val game = gameRepository.findById(gameId)
            .orElseThrow { MessageException(MessageError.GAME_NOT_FOUND) }
        roomCacheService.changeGame(roomId, name, game.id)
        return GameGetResponse.from(game)
    }

    fun changePlayerName(roomId: String, name: String, newName: String) {
        if (roomCacheService.isRoomPlaying(roomId)) {
            throw MessageException(MessageError.NAME_CHANGE_NOT_ALLOWED)
        }
        roomCacheService.changePlayerName(roomId, name, newName)
    }

    private fun validateName(roomId: String, name: String) {
        if (name == "System" || roomCacheService.isNameExist(roomId, name)) {
            throw MessageException(MessageError.INVALID_NAME)
        }
    }
}
