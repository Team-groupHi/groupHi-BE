package com.groupHi.groupHi.domain.room.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RoomCacheService(private val redisTemplate: RedisTemplate<String, Any>) { //TODO: 키값 상수화

    fun isRoomExist(id: String): Boolean {
        return redisTemplate.hasKey(id)
    }

    fun createRoom(id: String, gameId: String) {
        redisTemplate.opsForHash<String, RoomStatus>().put(id, "status", RoomStatus.WAITING)
        redisTemplate.opsForHash<String, String>().put(id, "gameId", gameId)
        redisTemplate.expire(id, 1, TimeUnit.HOURS)
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

    fun enterRoom(id: String, name: String) {
        val isHost = redisTemplate.opsForHash<String, String>().get(id, "hostName") == null
        if (isHost) {
            redisTemplate.opsForHash<String, String>().put(id, "hostName", name)
            redisTemplate.expire(id, 1, TimeUnit.HOURS)
        }
        redisTemplate.opsForHash<String, Boolean>().put("$id:players", name, false)
    }

    fun exitRoom(id: String, name: String) {
        if (!isHost(id, name)) {
            redisTemplate.delete(id)
            redisTemplate.delete("$id:players")
        }
        redisTemplate.opsForHash<String, Boolean>().delete("$id:players", name)
    }

    fun ready(id: String, name: String) {
        redisTemplate.opsForHash<String, Boolean>().put("$id:players", name, true)
    }

    fun unready(id: String, name: String) {
        redisTemplate.opsForHash<String, Boolean>().put("$id:players", name, false)
    }

    fun changeGame(id: String, name: String, gameId: String) {
        if (!isHost(id, name)) {
            throw IllegalArgumentException("Only host can change game")
        }
        redisTemplate.opsForHash<String, String>().put(id, "gameId", gameId)
    }

    fun changePlayerName(id: String, name: String, newName: String) {
        redisTemplate.opsForHash<String, Boolean>().delete("$id:players", name)
        redisTemplate.opsForHash<String, Boolean>().put("$id:players", newName, false)
    }

    private fun isHost(id: String, name: String): Boolean {
        return redisTemplate.opsForHash<String, String>().get(id, "hostName") == name
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
