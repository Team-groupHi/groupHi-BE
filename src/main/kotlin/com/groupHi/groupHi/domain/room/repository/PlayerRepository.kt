package com.groupHi.groupHi.domain.room.repository

import com.groupHi.groupHi.domain.room.entity.Player
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class PlayerRepository(private val redisTemplate: RedisTemplate<String, Player>) {

    fun existsByRoomIdAndName(roomId: String, name: String): Boolean {
        return redisTemplate.opsForHash<String, Boolean>().hasKey("$roomId:players", name)
    }

    fun findAllByRoomId(roomId: String): List<Player> {
        val hostName = redisTemplate.opsForHash<String, String>().get(roomId, "hostName")
        val players = redisTemplate.opsForHash<String, Boolean>().entries("$roomId:players")
        val avatarRegistry = redisTemplate.opsForHash<String, String>().entries("$roomId:avatarRegistry")
        return players.map { (name, isReady) ->
            Player(
                name = name,
                avatar = avatarRegistry[name]!!,
                isHost = name == hostName,
                isReady = isReady,
            )
        }
    }
}
