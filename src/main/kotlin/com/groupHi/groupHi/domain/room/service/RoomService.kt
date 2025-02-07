package com.groupHi.groupHi.domain.room.service

import com.groupHi.groupHi.domain.game.dto.response.GameGetResponse
import com.groupHi.groupHi.domain.game.repository.GameRepository
import com.groupHi.groupHi.domain.room.dto.request.RoomCreateRequest
import com.groupHi.groupHi.domain.room.dto.response.PlayerResponse
import com.groupHi.groupHi.domain.room.dto.response.RoomResponse
import com.groupHi.groupHi.domain.room.entity.Room
import com.groupHi.groupHi.domain.room.entity.RoomStatus
import com.groupHi.groupHi.domain.room.repository.PlayerRepository
import com.groupHi.groupHi.domain.room.repository.RoomRepository
import com.groupHi.groupHi.global.exception.error.ApiError
import com.groupHi.groupHi.global.exception.exception.ApiException
import org.springframework.stereotype.Service

@Service
class RoomService(
    private val roomRepository: RoomRepository,
    private val playerRepository: PlayerRepository,
    private val gameRepository: GameRepository
) {

    fun createRoom(request: RoomCreateRequest): String {
        val game = gameRepository.findById(request.gameId)
            .orElseThrow { ApiException(ApiError.GAME_NOT_FOUND) }

        val room = roomRepository.save(
            Room(
                id = generateUniqueRoomId(),
                status = RoomStatus.WAITING,
                gameId = game.id
            )
        )

        return room.id
    }

    fun getRoom(roomId: String): RoomResponse {
        val room = roomRepository.findById(roomId)
            ?: throw ApiException(ApiError.ROOM_NOT_FOUND)
        val game = gameRepository.findById(room.gameId)
            .orElseThrow { ApiException(ApiError.GAME_NOT_FOUND) }
        val players = playerRepository.findAllByRoomId(roomId)

        return RoomResponse(
            id = room.id,
            status = room.status,
            hostName = room.hostName,
            game = GameGetResponse.from(game),
            players = players.stream()
                .map { PlayerResponse.from(it) }
                .toList()
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
            if (!roomRepository.existsById(roomId)) {
                return roomId
            }
        }
    }
}
