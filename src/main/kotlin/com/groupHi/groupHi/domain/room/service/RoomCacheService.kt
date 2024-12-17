package com.groupHi.groupHi.domain.room.service

import com.groupHi.groupHi.global.exception.error.MessageError
import com.groupHi.groupHi.global.exception.exception.MessageException
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RoomCacheService(private val redisTemplate: RedisTemplate<String, Any>) {
    //TODO: 키값 상수화, 서비스 로직과 책임 명확히 나누어 가지도록 리팩터링하기

    fun isRoomExist(id: String): Boolean {
        return redisTemplate.hasKey(id)
    }

    fun isRoomPlaying(id: String): Boolean {
        return redisTemplate.opsForHash<String, RoomStatus>().get(id, "status") == RoomStatus.PLAYING
    }

    fun isNameExist(id: String, name: String): Boolean {
        return redisTemplate.opsForHash<String, Boolean>().hasKey("$id:players", name)
    }

    fun createRoom(id: String, gameId: String) {
        redisTemplate.opsForHash<String, RoomStatus>().put(id, "status", RoomStatus.WAITING)
        redisTemplate.opsForHash<String, String>().put(id, "gameId", gameId)
        redisTemplate.opsForSet().add("$id:avatars", "blue", "green", "mint", "orange", "pink", "purple", "red", "yellow")
        redisTemplate.expire(id, 1, TimeUnit.HOURS)
        redisTemplate.expire("$id:avatars", 1, TimeUnit.HOURS)
    }

    fun getRoom(id: String): RoomResponse {
        val room = redisTemplate.opsForHash<String, Any>().entries(id)
        val players = getPlayers(id)
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


    fun getPlayers(id: String): List<PlayerResponse> {
        return redisTemplate.opsForHash<String, Boolean>().entries("$id:players")
            .map { (name, isReady) ->
                PlayerResponse(
                    name = name,
                    isReady = isReady
                )
            }
    }

    fun enterRoom(id: String, name: String): String {
        val isHost = redisTemplate.opsForHash<String, String>().get(id, "hostName") == null
        if (isHost) {
            redisTemplate.opsForHash<String, String>().put(id, "hostName", name)
            redisTemplate.expire(id, 1, TimeUnit.HOURS)
        }
        if(redisTemplate.opsForHash<String, Boolean>().size("$id:players") >= 8) {
            throw MessageException(MessageError.ROOM_FULL)
        }
        redisTemplate.opsForHash<String, Boolean>().put("$id:players", name, isHost)
        return redisTemplate.opsForSet().pop("$id:avatars") as String
    }

    fun exitRoom(id: String, name: String, avatar: String?) {
        if (!isHost(id, name)) {
            redisTemplate.delete(id)
            redisTemplate.delete("$id:players")
            redisTemplate.delete("$id:avatars")
        }
        redisTemplate.opsForHash<String, Boolean>().delete("$id:players", name)
        if (avatar != null) {
            redisTemplate.opsForSet().add("$id:avatars", avatar)
        }
    }

    fun ready(id: String, name: String) {
        redisTemplate.opsForHash<String, Boolean>().put("$id:players", name, true)
    }

    fun unready(id: String, name: String) {
        redisTemplate.opsForHash<String, Boolean>().put("$id:players", name, false)
    }

    fun changeGame(id: String, name: String, gameId: String) {
        if (!isHost(id, name)) {
            throw MessageException(MessageError.ONLY_HOST_CAN_CHANGE_GAME)
        }
        redisTemplate.opsForHash<String, String>().put(id, "gameId", gameId)
    }

    fun changePlayerName(id: String, name: String, newName: String) {
        redisTemplate.opsForHash<String, Boolean>().delete("$id:players", name)
        redisTemplate.opsForHash<String, Boolean>().put("$id:players", newName, false)
    }

    fun isHost(id: String, name: String): Boolean {
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
