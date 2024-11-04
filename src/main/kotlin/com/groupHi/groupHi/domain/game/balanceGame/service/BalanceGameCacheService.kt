package com.groupHi.groupHi.domain.game.balanceGame.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class BalanceGameCacheService(private val redisTemplate: RedisTemplate<String, Any>) { //TODO: 키값 상수화

    fun getRounds(roomId: String): RoundsResponse {
        val rounds = redisTemplate.opsForValue().get("bg:$roomId:rounds") as? String ?: "0/0"
        val (currentRound, totalRounds) = rounds.split("/").map { it.toInt() }

        return RoundsResponse(
            currentRound = currentRound,
            totalRounds = totalRounds
        )
    }

    fun getContents(roomId: String): List<ContentResponse> {
        val contents = redisTemplate.opsForHash<String, String>().entries("bg:$roomId:contents")
        val groupedContents = contents.entries.groupBy { entry ->
            entry.key.split(":")[1]
        }

        return groupedContents.map { (round, entries) ->
            val q = entries.find { it.key.startsWith("q:") }?.value ?: ""
            val a = entries.find { it.key.startsWith("a:") }?.value ?: ""
            val b = entries.find { it.key.startsWith("b:") }?.value ?: ""

            ContentResponse(
                q = q,
                a = a,
                b = b
            )
        }
    }

    fun getSelections(roomId: String, round: Int): SelectionsResponse {
        val a = redisTemplate.opsForSet().members("bg:$roomId:$round:result:a")?.map { it.toString() } ?: emptyList()
        val b = redisTemplate.opsForSet().members("bg:$roomId:$round:result:b")?.map { it.toString() } ?: emptyList()

        return SelectionsResponse(
            a = a,
            b = b
        )
    }
}

data class RoundsResponse(
    val totalRounds: Int,
    val currentRound: Int
)

data class ContentResponse(
    val q: String,
    val a: String,
    val b: String
)

data class SelectionsResponse(
    val a: List<String>,
    val b: List<String>
)
