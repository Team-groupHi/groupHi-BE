package com.groupHi.groupHi.domain.game.balanceGame.service

import com.groupHi.groupHi.domain.game.balanceGame.BalanceGameTheme
import com.groupHi.groupHi.domain.game.balanceGame.repository.BalanceGameContentRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class BalanceGameCacheService(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val balanceGameContentRepository: BalanceGameContentRepository
) { //TODO: 키값 상수화

    fun init(roomId: String, theme: BalanceGameTheme, totalRounds: Int) {
        redisTemplate.opsForValue().set("bg:$roomId:rounds", "0/$totalRounds")
        val contents = balanceGameContentRepository.findByTheme(theme).shuffled().take(totalRounds)
        contents.forEachIndexed { idx, content ->
            redisTemplate.opsForHash<String, String>().put("bg:$roomId:contents", "q:${idx + 1}", content.q)
            redisTemplate.opsForHash<String, String>().put("bg:$roomId:contents", "a:${idx + 1}", content.a)
            redisTemplate.opsForHash<String, String>().put("bg:$roomId:contents", "b:${idx + 1}", content.b)
        }
    }

    fun getRounds(roomId: String): RoundsResponse {
        val rounds = redisTemplate.opsForValue().get("bg:$roomId:rounds") as? String ?: "0/0"
        val (currentRound, totalRounds) = rounds.split("/").map { it.toInt() }
        return RoundsResponse(
            currentRound = currentRound,
            totalRounds = totalRounds
        )
    }

    fun increaseRound(roomId: String) {
        val rounds = getRounds(roomId)
        if (rounds.currentRound < rounds.totalRounds) {
            redisTemplate.opsForValue().set("bg:$roomId:rounds", "${rounds.currentRound + 1}/${rounds.totalRounds}")
        }
    }

    fun getContents(roomId: String): List<ContentResponse> {
        val contents = redisTemplate.opsForHash<String, String>().entries("bg:$roomId:contents")
        val groupedContents = contents.entries.groupBy { entry ->
            entry.key.split(":")[1]
        }
        return groupedContents.toSortedMap().map { (round, entries) ->
            val q = entries.find { it.key.endsWith(":q") }?.value ?: ""
            val a = entries.find { it.key.endsWith(":a") }?.value ?: ""
            val b = entries.find { it.key.endsWith(":b") }?.value ?: ""
            ContentResponse(
                round = round.toInt(),
                q = q,
                a = a,
                b = b
            )
        }
    }

    fun select(roomId: String, name: String, round: Int, selection: String) {
        redisTemplate.opsForSet().add("bg:$roomId:$round:result:$selection", name)
    }

    fun getSelections(roomId: String, round: Int): SelectionsResponse { //TODO: 자료구조 리팩터링
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
    val round: Int,
    val q: String,
    val a: String,
    val b: String
)

data class SelectionsResponse(
    val a: List<String>,
    val b: List<String>
)
