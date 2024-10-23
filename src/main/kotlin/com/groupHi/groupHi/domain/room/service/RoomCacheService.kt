package com.groupHi.groupHi.domain.room.service

import com.groupHi.groupHi.domain.room.RoomStatus
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RoomCacheService(private val redisTemplate: RedisTemplate<String, Any>) {

    fun isRoomExist(id: String): Boolean {
        return redisTemplate.hasKey("room:$id")
    }

    fun createRoom(id: String, gameId: String) {
        redisTemplate.opsForHash<String, RoomStatus>().put("room:$id", "status", RoomStatus.WAITING)
        redisTemplate.opsForHash<String, String>().put("room:$id", "gameId", gameId)

        redisTemplate.expire("room:$id", 1, TimeUnit.HOURS)
        redisTemplate.expire("room:$id:players", 1, TimeUnit.HOURS)
    }

    fun getRoom(id: String): RoomResponse {
        val room = redisTemplate.opsForHash<String, Any>().entries("room:$id")
        val players = redisTemplate.opsForHash<String, Any>().entries("room:$id:players")

        return RoomResponse(
            id = id,
            status = room["status"] as RoomStatus,
            gameId = room["gameId"] as String,
            hostName = room["host"] as String?,
            players = players.map { (name, isReady) ->
                PlayerResponse(
                    name = name,
                    isReady = isReady as Boolean
                )
            }
        )
    }
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
