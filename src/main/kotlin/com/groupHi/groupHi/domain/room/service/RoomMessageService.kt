package com.groupHi.groupHi.domain.room.service

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

    fun changeName(roomId: String, name: String, newName: String) {
        roomCacheService.changeName(roomId, name, newName)
    }

    fun changeGame(roomId: String, name: String, gameId: String) {
        roomCacheService.changeGame(roomId, name, gameId)
    }
}
