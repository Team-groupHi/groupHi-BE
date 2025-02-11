package com.groupHi.groupHi.domain.room.repository

import com.groupHi.groupHi.domain.room.entity.Player
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class PlayerRepository(private val redisTemplate: RedisTemplate<String, Any>) {

    fun existsByRoomIdAndName(roomId: String, name: String): Boolean {
        return redisTemplate.opsForHash<String, Boolean>().hasKey("$roomId:players", name)
    }

    fun countByRoomId(roomId: String): Long {
        return redisTemplate.opsForHash<String, Boolean>().size("$roomId:players")
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

    fun save(roomId: String, player: Player): Player {
        redisTemplate.opsForHash<String, Boolean>().put("$roomId:players", player.name, player.isReady)
        redisTemplate.opsForHash<String, String>().put("$roomId:avatarRegistry", player.name, player.avatar)

        if (player.isHost) {
            redisTemplate.opsForHash<String, String>().put(roomId, "hostName", player.name)
            redisTemplate.expire("$roomId:avatarRegistry", 1, TimeUnit.HOURS)
        }

        return player
    }

    fun delete(roomId: String, name: String) {
        val avatar = redisTemplate.opsForHash<String, String>().get("$roomId:avatarRegistry", name)
        redisTemplate.opsForSet().add("$roomId:avatarPool", avatar)
        redisTemplate.opsForHash<String, Boolean>().delete("$roomId:players", name)
    }
}
