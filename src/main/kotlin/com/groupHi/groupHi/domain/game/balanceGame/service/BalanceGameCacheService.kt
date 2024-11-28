package com.groupHi.groupHi.domain.game.balanceGame.service

import com.groupHi.groupHi.domain.game.balanceGame.BalanceGameSelection
import com.groupHi.groupHi.domain.game.balanceGame.BalanceGameTheme
import com.groupHi.groupHi.domain.game.balanceGame.repository.BalanceGameContentRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class BalanceGameCacheService(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val balanceGameContentRepository: BalanceGameContentRepository
) { //TODO: 키값 상수화, 서비스 로직과 책임 명확히 나누어 가지도록 리팩터링하기

    fun init(roomId: String, theme: BalanceGameTheme, totalRounds: Int) {
        redisTemplate.opsForValue().set("bg:$roomId:rounds", "0/$totalRounds")
        val contents = balanceGameContentRepository.findByTheme(theme).shuffled().take(totalRounds)
        val players = redisTemplate.opsForHash<String, Boolean>().entries("$roomId:players").keys
        contents.forEachIndexed { idx, content ->
            redisTemplate.opsForHash<String, String>().put("bg:$roomId:contents", "q:${idx + 1}", content.q)
            redisTemplate.opsForHash<String, String>().put("bg:$roomId:contents", "a:${idx + 1}", content.a)
            redisTemplate.opsForHash<String, String>().put("bg:$roomId:contents", "b:${idx + 1}", content.b)
        }
        players.forEach { name ->
            (1..totalRounds).forEach { round ->
                redisTemplate.opsForHash<String, BalanceGameSelection>()
                    .put("bg:$roomId:selections", "$name:$round", BalanceGameSelection.C)
            }
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
            val q = entries.find { it.key.startsWith("q:") }?.value ?: ""
            val a = entries.find { it.key.startsWith("a:") }?.value ?: ""
            val b = entries.find { it.key.startsWith("b:") }?.value ?: ""
            ContentResponse(
                round = round.toInt(),
                q = q,
                a = a,
                b = b
            )
        }
    }

    fun select(roomId: String, name: String, round: Int, selection: BalanceGameSelection) {
        redisTemplate.opsForSet().add("bg:$roomId:$round:result:$selection", name)
    }

    fun unselect(roomId: String, name: String, round: Int) {
        redisTemplate.opsForSet().remove("bg:$roomId:$round:result:${BalanceGameSelection.A}", name)
        redisTemplate.opsForSet().remove("bg:$roomId:$round:result:${BalanceGameSelection.B}", name)
    }

    fun clean(roomId: String) {
        redisTemplate.delete("bg:$roomId:rounds")
        redisTemplate.delete("bg:$roomId:contents")
        redisTemplate.delete("bg:$roomId:selections")
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
