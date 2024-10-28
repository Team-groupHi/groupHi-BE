package com.groupHi.groupHi.domain.game.balanceGame.service

import com.groupHi.groupHi.domain.game.balanceGame.dto.request.BalanceGameResultCreateRequest
import com.groupHi.groupHi.domain.game.balanceGame.dto.response.BalanceGameResultGetResponse
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class BalanceGameCacheService(private val redisTemplate: RedisTemplate<String, Any>) {

    fun createBalanceGameResult(request: BalanceGameResultCreateRequest) {
        val key = "balance-game:${request.roomId}:${request.turn}"
        redisTemplate.opsForHash<String, String>().put(key, "question", request.question)
        redisTemplate.opsForHash<String, List<String>>().put(key, "a", request.a)
        redisTemplate.opsForHash<String, List<String>>().put(key, "b", request.b)
    }

    fun getBalanceGameResult(roomId: String, turn: Int): BalanceGameResultGetResponse {
        val key = "balance-game:$roomId:$turn"
        val balanceGameResult = redisTemplate.opsForHash<String, Any>().entries(key)
        return BalanceGameResultGetResponse(
            turn = turn,
            question = balanceGameResult["question"] as String,
            a = balanceGameResult["a"] as List<String>,
            b = balanceGameResult["b"] as List<String>
        )
    }

    fun getBalanceGameResults(roomId: String): List<BalanceGameResultGetResponse> {
        return redisTemplate.keys("balance-game:$roomId:*")
            .map { redisTemplate.opsForHash<String, Any>().entries(it) }
            .map {
                BalanceGameResultGetResponse(
                    turn = it["turn"] as Int,
                    question = it["question"] as String,
                    a = it["a"] as List<String>,
                    b = it["b"] as List<String>
                )
            }
    }
}
