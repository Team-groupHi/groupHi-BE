package com.groupHi.groupHi.domain.room.service

import com.groupHi.groupHi.domain.game.dto.response.GameGetResponse
import com.groupHi.groupHi.domain.game.repository.GameRepository
import com.groupHi.groupHi.domain.room.dto.request.RoomCreateRequest
import com.groupHi.groupHi.domain.room.dto.response.RoomGetResponse
import com.groupHi.groupHi.global.exception.error.ApiError
import com.groupHi.groupHi.global.exception.error.MessageError
import com.groupHi.groupHi.global.exception.exception.ApiException
import com.groupHi.groupHi.global.exception.exception.MessageException
import org.springframework.stereotype.Service

@Service
class RoomService(
    private val roomCacheService: RoomCacheService,
    private val gameRepository: GameRepository
) {

    fun createRoom(request: RoomCreateRequest): String {
        val game = gameRepository.findById(request.gameId)
            .orElseThrow { ApiException(ApiError.GAME_NOT_FOUND) }
        val roomId = generateRoomId()
        roomCacheService.createRoom(roomId, game.id)
        return roomId
    }

    fun getRoom(roomId: String): RoomGetResponse {
        if (!roomCacheService.isRoomExist(roomId)) {
            throw ApiException(ApiError.ROOM_NOT_FOUND)
        }

        val room = roomCacheService.getRoom(roomId)
        val game = gameRepository.findById(room.gameId)
            .orElseThrow { ApiException(ApiError.GAME_NOT_FOUND) }

        return RoomGetResponse(
            id = room.id,
            status = room.status,
            game = GameGetResponse.from(game),
            hostName = room.hostName,
            players = room.players
        )
    }

    private fun generateRoomId(): String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        while (true) {
            val roomId = (1..8)
                .map { charset.random() }
                .joinToString("")
            if (!roomCacheService.isRoomExist(roomId)) {
                return roomId
            }
        }
    }
}
