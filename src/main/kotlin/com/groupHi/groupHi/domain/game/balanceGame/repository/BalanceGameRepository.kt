package com.groupHi.groupHi.domain.game.balanceGame.repository

import com.groupHi.groupHi.domain.game.balanceGame.BalanceGameSelection
import com.groupHi.groupHi.domain.game.balanceGame.BalanceGameTheme
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class BalanceGameRepository( //TODO: DataHandler?
    private val redisTemplate: RedisTemplate<String, Any>,
    private val balanceGameContentRepository: BalanceGameContentRepository
) { //TODO: 키값 상수화, 서비스 로직과 책임 명확히 나누어 가지도록 리팩터링하기

    fun init(roomId: String, theme: BalanceGameTheme, totalRounds: Int) {
        // 라운드 세팅
        redisTemplate.opsForValue().set("bg:$roomId:rounds", "0/$totalRounds")
        // 컨텐츠 세팅
        val contents = if (theme == BalanceGameTheme.ALL || theme == BalanceGameTheme.GENERAL) { //TODO: GENERAL 조건문 삭제
            balanceGameContentRepository.findAll().shuffled().take(totalRounds)
        } else {
            balanceGameContentRepository.findByTheme(theme).shuffled().take(totalRounds)
        }
        contents.forEachIndexed { idx, content ->
            redisTemplate.opsForHash<String, String>().put("bg:$roomId:contents", "q:${idx + 1}", content.q)
            redisTemplate.opsForHash<String, String>().put("bg:$roomId:contents", "a:${idx + 1}", content.a)
            redisTemplate.opsForHash<String, String>().put("bg:$roomId:contents", "b:${idx + 1}", content.b)
        }
        // 선택 세팅
        val players = redisTemplate.opsForHash<String, Boolean>().entries("$roomId:players").keys
        players.forEach { name ->
            (1..totalRounds).forEach { round ->
                redisTemplate.opsForHash<String, BalanceGameSelection>()
                    .put("bg:$roomId:selections", "$name:$round", BalanceGameSelection.C)
            }
        }
        // 만료 세팅
        redisTemplate.expire("bg:$roomId:rounds", 1, TimeUnit.HOURS)
        redisTemplate.expire("bg:$roomId:contents", 1, TimeUnit.HOURS)
        redisTemplate.expire("bg:$roomId:selections", 1, TimeUnit.HOURS)
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
            entry.key.split(":")[1].toInt()
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
        redisTemplate.opsForHash<String, BalanceGameSelection>()
            .put("bg:$roomId:selections", "$name:$round", selection)
    }

    fun unselect(roomId: String, name: String, round: Int) {
        redisTemplate.opsForHash<String, BalanceGameSelection>()
            .put("bg:$roomId:selections", "$name:$round", BalanceGameSelection.C)
    }

    fun clean(roomId: String) {
        redisTemplate.delete("bg:$roomId:rounds")
        redisTemplate.delete("bg:$roomId:contents")
        redisTemplate.delete("bg:$roomId:selections")
    }

    fun getSelections(roomId: String): List<SelectionsResponse> {
        val selections = redisTemplate.opsForHash<String, BalanceGameSelection>().entries("bg:$roomId:selections")
        val groupedSelections = selections.entries.groupBy { entry ->
            entry.key.split(":")[1].toInt()
        }
        return groupedSelections.toSortedMap().map { (round, entries) ->
            val a = entries.filter { it.value == BalanceGameSelection.A }.map { it.key.split(":")[0] }
            val b = entries.filter { it.value == BalanceGameSelection.B }.map { it.key.split(":")[0] }
            val c = entries.filter { it.value == BalanceGameSelection.C }.map { it.key.split(":")[0] }
            SelectionsResponse(
                a = a,
                b = b,
                c = c
            )
        }
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
    val b: List<String>,
    val c: List<String>
)
