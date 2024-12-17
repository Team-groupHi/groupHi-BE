package com.groupHi.groupHi.domain.room.service

import com.groupHi.groupHi.global.exception.error.MessageError
import com.groupHi.groupHi.global.exception.exception.MessageException
import org.springframework.stereotype.Service

@Service
class RoomMessageService(private val roomCacheService: RoomCacheService) {

    fun enterRoom(roomId: String, name: String) {
        //TODO: 닉네임 중복 체크
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

    fun changeGame(roomId: String, name: String, gameId: String) {
        //TODO: 게임 정보 리턴
        roomCacheService.changeGame(roomId, name, gameId)
    }

    fun changePlayerName(roomId: String, name: String, newName: String) {
        if (roomCacheService.isRoomPlaying(roomId)) {
            throw MessageException(MessageError.NAME_CHANGE_NOT_ALLOWED)
        }
        roomCacheService.changePlayerName(roomId, name, newName)
    }
}
