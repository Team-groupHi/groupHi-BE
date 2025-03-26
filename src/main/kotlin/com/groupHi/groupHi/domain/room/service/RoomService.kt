package com.groupHi.groupHi.domain.room.service

import com.groupHi.groupHi.domain.game.dto.response.GameGetResponse
import com.groupHi.groupHi.domain.game.repository.GameRepository
import com.groupHi.groupHi.domain.room.dto.request.RoomCreateRequest
import com.groupHi.groupHi.domain.room.dto.response.PlayerResponse
import com.groupHi.groupHi.domain.room.dto.response.RoomResponse
import com.groupHi.groupHi.domain.room.entity.Player
import com.groupHi.groupHi.domain.room.entity.Room
import com.groupHi.groupHi.domain.room.entity.RoomStatus
import com.groupHi.groupHi.domain.room.repository.PlayerRepository
import com.groupHi.groupHi.domain.room.repository.RoomRepository
import com.groupHi.groupHi.global.exception.error.ErrorCode
import com.groupHi.groupHi.global.exception.exception.ApiException
import com.groupHi.groupHi.global.exception.exception.MessageException
import org.springframework.stereotype.Service

@Service
class RoomService(
    private val roomRepository: RoomRepository,
    private val playerRepository: PlayerRepository,
    private val gameRepository: GameRepository
) {

    fun createRoom(request: RoomCreateRequest): String {
        val game = gameRepository.findById(request.gameId)
            .orElseThrow { ApiException(ErrorCode.GAME_NOT_FOUND) }

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
            ?: throw ApiException(ErrorCode.ROOM_NOT_FOUND)
        val game = gameRepository.findById(room.gameId)
            .orElseThrow { ApiException(ErrorCode.GAME_NOT_FOUND) }
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

    fun enterRoom(roomId: String, name: String): String {
        val room = roomRepository.findById(roomId)
            ?: throw MessageException(ErrorCode.ROOM_NOT_FOUND)

        if (room.status == RoomStatus.PLAYING) {
            throw MessageException(ErrorCode.ALREADY_PLAYING)
        }
        if (name == "System" || playerRepository.existsByRoomIdAndName(room.id, name)) {
            throw MessageException(ErrorCode.INVALID_NAME)
        }
        if (playerRepository.countByRoomId(room.id) >= 8) {
            throw MessageException(ErrorCode.ROOM_FULL)
        }

        val player = playerRepository.save(
            room.id,
            Player(
                name = name,
                isHost = room.hostName == null,
                isReady = room.hostName == null
            )
        )

        return player.avatar!!
    }

    fun exitRoom(roomId: String, name: String) {
        val room = roomRepository.findById(roomId)
        room?.let {
            if (it.hostName == name) {
                roomRepository.delete(it.id)
            } else {
                playerRepository.delete(it.id, name)
            }
        }
    }

    fun ready(roomId: String, name: String) {
        playerRepository.updateReady(roomId, name, true)
    }

    fun unready(roomId: String, name: String) {
        playerRepository.updateReady(roomId, name, false)
    }

    fun changeGame(roomId: String, gameId: String): GameGetResponse {
        val room = roomRepository.findById(roomId)
            ?: throw MessageException(ErrorCode.ROOM_NOT_FOUND)

        if (room.status == RoomStatus.PLAYING) {
            throw MessageException(ErrorCode.ALREADY_PLAYING)
        }

        val game = gameRepository.findById(gameId)
            .orElseThrow { MessageException(ErrorCode.GAME_NOT_FOUND) }

        roomRepository.updateGame(roomId, gameId)

        return GameGetResponse.from(game)
    }

    fun isValidPlayerName(roomId: String, name: String): Boolean {
        return name != "System" && !playerRepository.existsByRoomIdAndName(roomId, name)
    }

    fun changePlayerName(roomId: String, name: String, newName: String) {
        val room = roomRepository.findById(roomId)
            ?: throw MessageException(ErrorCode.ROOM_NOT_FOUND)

        if (room.status == RoomStatus.PLAYING) {
            throw MessageException(ErrorCode.NAME_CHANGE_NOT_ALLOWED)
        }
        if (playerRepository.findByRoomIdAndName(roomId, name)?.isReady == true) {
            throw MessageException(ErrorCode.NAME_CHANGE_NOT_ALLOWED)
        }
        if (playerRepository.existsByRoomIdAndName(room.id, newName)) {
            throw MessageException(ErrorCode.INVALID_NAME)
        }

        playerRepository.updateName(roomId, name, newName)

        if (room.hostName == name) {
            roomRepository.updateHostName(roomId, newName)
            playerRepository.updateReady(roomId, newName, true)
        }
    }

    fun validateStartable(roomId: String, totalRounds: Int) {
        val room = getRoom(roomId)

        if (room.players.size < 2) {
            throw MessageException(ErrorCode.NOT_ENOUGH_PLAYERS)
        }
        if (room.players.any { !it.isReady }) {
            throw MessageException(ErrorCode.NOT_ALL_PLAYERS_READY)
        }
        if (totalRounds < 1 || totalRounds > 20) {
            throw MessageException(ErrorCode.INVALID_ROUND_COUNT)
        }
        if (room.status == RoomStatus.PLAYING) {
            throw MessageException(ErrorCode.ALREADY_PLAYING)
        }
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
