package com.groupHi.groupHi.domain.room.repository

import com.groupHi.groupHi.domain.room.entity.Room
import com.groupHi.groupHi.domain.room.entity.RoomStatus
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RoomRepository(private val redisTemplate: RedisTemplate<String, Any>) {
    //TODO: 키값 상수화, 서비스 로직과 책임 명확히 나누어 가지도록 리팩터링하기

    fun existsById(id: String): Boolean {
        return redisTemplate.hasKey(id)
    }

    fun isRoomPlaying(id: String): Boolean {
        return redisTemplate.opsForHash<String, RoomStatus>().get(id, "status") == RoomStatus.PLAYING
    }

    fun save(room: Room): Room {
        redisTemplate.opsForHash<String, RoomStatus>().put(room.id, "status", room.status)
        redisTemplate.opsForHash<String, String>().put(room.id, "gameId", room.gameId)
        redisTemplate.expire(room.id, 1, TimeUnit.HOURS)

        redisTemplate.opsForSet()
            .add("${room.id}:avatarPool", "blue", "green", "mint", "orange", "pink", "purple", "red", "yellow")
        redisTemplate.expire("${room.id}:avatarPool", 1, TimeUnit.HOURS)

        return room
    }

    fun findById(id: String): Room? {
        if (!existsById(id)) {
            return null
        }

        val room = redisTemplate.opsForHash<String, Any>().entries(id)
        return Room(
            id = id,
            status = room["status"] as RoomStatus,
            gameId = room["gameId"] as String
        )
    }

    fun updateRoomStatus(id: String, status: RoomStatus) {
        redisTemplate.opsForHash<String, RoomStatus>().put(id, "status", status)
    }

    // 방장을 제외한 모든 플레이어 준비상태 false로
    fun resetPlayerReady(id: String) {
        redisTemplate.opsForHash<String, Boolean>().entries("$id:players")
            .filter { (name, _) -> !isHost(id, name) }
            .forEach { (name, _) ->
                redisTemplate.opsForHash<String, Boolean>().put("$id:players", name, false)
            }
    }

    fun takeAvatar(id: String): String {
        return redisTemplate.opsForSet().pop("$id:avatarPool") as String
    }

    fun delete(id: String) {
        redisTemplate.delete(id)
        redisTemplate.delete("$id:players")
        redisTemplate.delete("$id:avatarPool")
        redisTemplate.delete("$id:avatarRegistry")
    }

    fun updateGame(id: String, gameId: String) {
        redisTemplate.opsForHash<String, String>().put(id, "gameId", gameId)
    }

    fun changePlayerName(id: String, name: String, newName: String, avatar: String) {
        //TODO: 순서 보장하도록 아키텍처 구성 (ex. 닉네임 수정중인데, 게임 시작하면?)
        if (isHost(id, name)) {
            redisTemplate.opsForHash<String, String>().put(id, "hostName", newName)
        }
        redisTemplate.opsForHash<String, Boolean>().delete("$id:players", name)
        redisTemplate.opsForHash<String, String>().delete("$id:avatarRegistry", name)
        redisTemplate.opsForHash<String, Boolean>().put("$id:players", newName, false)
        redisTemplate.opsForHash<String, String>().put("$id:avatarRegistry", newName, avatar)
    }

    fun isHost(id: String, name: String): Boolean {
        return redisTemplate.opsForHash<String, String>().get(id, "hostName") == name
    }
}
