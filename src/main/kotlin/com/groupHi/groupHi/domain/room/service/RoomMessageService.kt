package com.groupHi.groupHi.domain.room.service

import com.groupHi.groupHi.domain.game.dto.response.GameGetResponse
import com.groupHi.groupHi.domain.game.repository.GameRepository
import com.groupHi.groupHi.domain.room.repository.RoomRepository
import com.groupHi.groupHi.global.exception.error.MessageError
import com.groupHi.groupHi.global.exception.exception.MessageException
import org.springframework.stereotype.Service

@Service
class RoomMessageService(
    private val roomRepository: RoomRepository,
    private val gameRepository: GameRepository
) {

    fun enterRoom(roomId: String, name: String): String {
        validateName(roomId, name)
        return roomRepository.enterRoom(roomId, name)
    }

    fun exitRoom(roomId: String, name: String, avatar: String) {
        roomRepository.exitRoom(roomId, name, avatar)
    }

    fun ready(roomId: String, name: String) {
        roomRepository.ready(roomId, name)
    }

    fun unready(roomId: String, name: String) {
        roomRepository.unready(roomId, name)
    }

    fun changeGame(roomId: String, name: String, gameId: String): GameGetResponse {
        val game = gameRepository.findById(gameId)
            .orElseThrow { MessageException(MessageError.GAME_NOT_FOUND) }
        roomRepository.changeGame(roomId, name, game.id)
        return GameGetResponse.from(game)
    }

    fun changePlayerName(roomId: String, name: String, newName: String) {
        if (roomRepository.isRoomPlaying(roomId)) {
            throw MessageException(MessageError.NAME_CHANGE_NOT_ALLOWED)
        }
        roomRepository.changePlayerName(roomId, name, newName)
    }

    private fun validateName(roomId: String, name: String) {
        if (name == "System" || roomRepository.isNameExist(roomId, name)) {
            throw MessageException(MessageError.INVALID_NAME)
        }
    }
}
