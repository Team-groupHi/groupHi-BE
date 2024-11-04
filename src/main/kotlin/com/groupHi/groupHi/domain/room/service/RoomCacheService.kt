package com.groupHi.groupHi.domain.room.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

//TODO: 키값 상수화
//TODO: 책임 명확하도록 리팩터링 필요
@Service
class RoomCacheService(private val redisTemplate: RedisTemplate<String, Any>) {

    fun isRoomExist(id: String): Boolean {
        return redisTemplate.hasKey(id)
    }

    fun createRoom(id: String, gameId: String) {
        redisTemplate.opsForHash<String, RoomStatus>().put(id, "status", RoomStatus.WAITING)
        redisTemplate.opsForHash<String, String>().put(id, "gameId", gameId)

        redisTemplate.expire(id, 1, TimeUnit.HOURS)
        redisTemplate.expire("$id:players", 1, TimeUnit.HOURS)
    }

    fun getRoom(id: String): RoomResponse {
        val room = redisTemplate.opsForHash<String, Any>().entries(id)
        val players = redisTemplate.opsForHash<String, Any>().entries("$id:players")

        return RoomResponse(
            id = id,
            status = room["status"] as RoomStatus,
            gameId = room["gameId"] as String,
            hostName = room["hostName"] as String?,
            players = players.map { (name, isReady) ->
                PlayerResponse(
                    name = name,
                    isReady = isReady as Boolean
                )
            }
        )
    }
}

enum class RoomStatus {
    WAITING,
    PLAYING
}

data class RoomResponse(
    val id: String,
    val status: RoomStatus,
    val gameId: String,
    val hostName: String?,
    val players: List<PlayerResponse>
)

data class PlayerResponse(
    val name: String,
    val isReady: Boolean
)
