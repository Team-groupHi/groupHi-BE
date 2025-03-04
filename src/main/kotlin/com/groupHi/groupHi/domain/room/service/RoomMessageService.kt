package com.groupHi.groupHi.domain.room.service

import com.groupHi.groupHi.domain.game.dto.response.GameGetResponse
import com.groupHi.groupHi.domain.game.repository.GameRepository
import com.groupHi.groupHi.domain.room.entity.Player
import com.groupHi.groupHi.domain.room.entity.RoomStatus
import com.groupHi.groupHi.domain.room.repository.PlayerRepository
import com.groupHi.groupHi.domain.room.repository.RoomRepository
import com.groupHi.groupHi.global.exception.error.MessageError
import com.groupHi.groupHi.global.exception.exception.MessageException
import org.springframework.stereotype.Service

@Service
class RoomMessageService(
    private val roomRepository: RoomRepository,
    private val playerRepository: PlayerRepository,
    private val gameRepository: GameRepository
) {

    fun enterRoom(roomId: String, name: String): String {
        val room = roomRepository.findById(roomId)
            ?: throw MessageException(MessageError.ROOM_NOT_FOUND)

        if (room.status == RoomStatus.PLAYING) {
            throw MessageException(MessageError.ALREADY_PLAYING)
        }
        if (name == "System" || playerRepository.existsByRoomIdAndName(room.id, name)) {
            throw MessageException(MessageError.INVALID_NAME)
        }
        if (playerRepository.countByRoomId(room.id) >= 8) {
            throw MessageException(MessageError.ROOM_FULL)
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

    fun changeGame(roomId: String, name: String, gameId: String): GameGetResponse {
        val room = roomRepository.findById(roomId)
            ?: throw MessageException(MessageError.ROOM_NOT_FOUND)

        if (!room.hostName.equals(name)) {
            throw MessageException(MessageError.ONLY_HOST_CAN_CHANGE_GAME)
        }
        if (room.status == RoomStatus.PLAYING) {
            throw MessageException(MessageError.ALREADY_PLAYING)
        }

        val game = gameRepository.findById(gameId)
            .orElseThrow { MessageException(MessageError.GAME_NOT_FOUND) }

        roomRepository.updateGame(roomId, gameId)

        return GameGetResponse.from(game)
    }

    fun changePlayerName(roomId: String, name: String, newName: String) {
        val room = roomRepository.findById(roomId)
            ?: throw MessageException(MessageError.ROOM_NOT_FOUND)

        if (room.status == RoomStatus.PLAYING) {
            throw MessageException(MessageError.NAME_CHANGE_NOT_ALLOWED)
        }
        if (playerRepository.existsByRoomIdAndName(room.id, newName)) {
            throw MessageException(MessageError.INVALID_NAME)
        }

        playerRepository.updateName(roomId, name, newName)

        if (room.hostName == name) {
            roomRepository.updateHostName(roomId, newName)
            playerRepository.updateReady(roomId, newName, true)
        }
    }
}
