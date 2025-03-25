package com.groupHi.groupHi.domain.room.repository

import com.groupHi.groupHi.domain.room.entity.Room
import com.groupHi.groupHi.domain.room.entity.RoomStatus
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RoomRepository(private val redisTemplate: RedisTemplate<String, Any>) {
    //TODO: 키값 상수화

    fun existsById(id: String): Boolean {
        return redisTemplate.hasKey(id)
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
            gameId = room["gameId"] as String,
            hostName = room["hostName"] as String?
        )
    }

    fun updateRoomStatus(id: String, status: RoomStatus) {
        redisTemplate.opsForHash<String, RoomStatus>().put(id, "status", status)
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

    fun updateHostName(id: String, name: String) {
        redisTemplate.opsForHash<String, String>().put(id, "hostName", name)
    }

    fun isHost(id: String, name: String): Boolean {
        return redisTemplate.opsForHash<String, String>().get(id, "hostName") == name
    }
}
