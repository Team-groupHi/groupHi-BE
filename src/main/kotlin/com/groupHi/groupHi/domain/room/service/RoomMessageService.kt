package com.groupHi.groupHi.domain.room.service

import com.groupHi.groupHi.global.exception.error.MessageError
import com.groupHi.groupHi.global.exception.exception.MessageException
import org.springframework.stereotype.Service

@Service
class RoomMessageService(private val roomCacheService: RoomCacheService) {

    fun enterRoom(roomId: String, name: String) {
        roomCacheService.enterRoom(roomId, name)
    }

    fun exitRoom(roomId: String, name: String) {
        roomCacheService.exitRoom(roomId, name)
    }

    fun ready(roomId: String, name: String) {
        roomCacheService.ready(roomId, name)
    }

    fun unready(roomId: String, name: String) {
        roomCacheService.unready(roomId, name)
    }

    fun changeGame(roomId: String, name: String, gameId: String) {
        roomCacheService.changeGame(roomId, name, gameId)
    }

    fun changePlayerName(roomId: String, name: String, newName: String) {
        if (roomCacheService.isRoomPlaying(roomId)) {
            throw MessageException(MessageError.NAME_CHANGE_NOT_ALLOWED)
        }
        roomCacheService.changePlayerName(roomId, name, newName)
    }
}
