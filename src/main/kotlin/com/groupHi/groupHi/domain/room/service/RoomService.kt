package com.groupHi.groupHi.domain.room.service

import com.groupHi.groupHi.domain.game.dto.response.GameGetResponse
import com.groupHi.groupHi.domain.game.repository.GameRepository
import com.groupHi.groupHi.domain.room.dto.request.RoomCreateRequest
import com.groupHi.groupHi.domain.room.dto.response.RoomGetResponse
import com.groupHi.groupHi.domain.room.entity.Room
import com.groupHi.groupHi.domain.room.entity.RoomStatus
import com.groupHi.groupHi.domain.room.repository.RoomRepository
import com.groupHi.groupHi.global.exception.error.ApiError
import com.groupHi.groupHi.global.exception.exception.ApiException
import org.springframework.stereotype.Service

@Service
class RoomService(
    private val roomRepository: RoomRepository,
    private val gameRepository: GameRepository
) {

    fun createRoom(request: RoomCreateRequest): String {
        val game = gameRepository.findById(request.gameId)
            .orElseThrow { ApiException(ApiError.GAME_NOT_FOUND) }

        val room = roomRepository.saveRoom(
            Room(
                id = generateUniqueRoomId(),
                status = RoomStatus.WAITING,
                gameId = game.id
            )
        )

        return room.id
    }

    fun getRoom(roomId: String): RoomGetResponse {
        if (!roomRepository.isRoomExist(roomId)) {
            throw ApiException(ApiError.ROOM_NOT_FOUND)
        }

        val room = roomRepository.getRoom(roomId)
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

    fun validateName(roomId: String, name: String): Boolean {
        return !roomRepository.isNameExist(roomId, name)
    }

    private fun generateUniqueRoomId(): String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        while (true) {
            val roomId = (1..8)
                .map { charset.random() }
                .joinToString("")
            if (!roomRepository.isRoomExist(roomId)) {
                return roomId
            }
        }
    }
}
