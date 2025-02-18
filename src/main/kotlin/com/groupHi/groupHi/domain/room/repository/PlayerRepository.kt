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

        val avatar = redisTemplate.opsForSet().pop("$roomId:avatarPool") as String
        redisTemplate.opsForHash<String, String>().put("$roomId:avatarRegistry", player.name, avatar)

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

    fun updateReady(roomId: String, name: String, isReady: Boolean) {
        redisTemplate.opsForHash<String, Boolean>().put("$roomId:players", name, isReady)
    }

    fun updateName(roomId: String, name: String, newName: String) {
        val avatar = redisTemplate.opsForHash<String, String>().get("$roomId:avatarRegistry", name)
        redisTemplate.opsForHash<String, String>().delete("$roomId:avatarRegistry", name)
        redisTemplate.opsForHash<String, String>().put("$roomId:avatarRegistry", newName, avatar!!)
        redisTemplate.opsForHash<String, Boolean>().delete("$roomId:players", name)
        redisTemplate.opsForHash<String, Boolean>().put("$roomId:players", newName, false)
    }

    fun resetReady(roomId: String) {
        val hostName = redisTemplate.opsForHash<String, String>().get(roomId, "hostName")
        redisTemplate.opsForHash<String, Boolean>().entries("$roomId:players")
            .filter { (name, _) -> name != hostName }
            .forEach { (name, _) ->
                redisTemplate.opsForHash<String, Boolean>().put("$roomId:players", name, false)
            }
    }
}
