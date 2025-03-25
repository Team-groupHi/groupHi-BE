package com.groupHi.groupHi.domain.game.qnaGame.repository

import com.groupHi.groupHi.domain.game.balanceGame.BalanceGameTheme
import com.groupHi.groupHi.domain.game.balanceGame.repository.BalanceGameContentRepository
import com.groupHi.groupHi.domain.game.balanceGame.repository.RoundsResponse
import com.groupHi.groupHi.domain.room.repository.PlayerRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class QnaGameRepository(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val playerRepository: PlayerRepository,
    private val balanceGameContentRepository: BalanceGameContentRepository
) {

    fun init(roomId: String, theme: BalanceGameTheme, totalRounds: Int) {
        // 라운드 세팅
        redisTemplate.opsForValue().set("qna:$roomId:rounds", "0/$totalRounds")
        // 컨텐츠 세팅
        val contents = balanceGameContentRepository.findByTheme(BalanceGameTheme.CLASSIC).shuffled().take(totalRounds)
        redisTemplate.opsForList().rightPushAll("qna:$roomId:contents", contents)
        // 답변 세팅
        val playerNames = playerRepository.getPlayerNamesByRoomId(roomId)
        playerNames.forEach { name ->
            (1..totalRounds).forEach { round ->
                redisTemplate.opsForHash<String, String>().put("qna:$roomId:answers", "$name:$round", "")
            }
        }
        // 좋아요 세팅
        playerNames.forEach { name ->
            (1..totalRounds).forEach { round ->
                redisTemplate.opsForHash<String, Long>().put("qna:$roomId:likes", "$name:$round", 0)
            }
        }
        // 만료 세팅
        redisTemplate.expire("qna:$roomId:rounds", 1, java.util.concurrent.TimeUnit.HOURS)
        redisTemplate.expire("qna:$roomId:contents", 1, java.util.concurrent.TimeUnit.HOURS)
        redisTemplate.expire("qna:$roomId:answers", 1, java.util.concurrent.TimeUnit.HOURS)
    }

    fun submit(roomId: String, name: String, round: Int, answer: String) {
        redisTemplate.opsForHash<String, String>().put("qna:$roomId:answers", "$name:$round", answer)
    }

    fun like(roomId: String, name: String, round: Int) {
        redisTemplate.opsForHash<String, Long>().increment("qna:$roomId:likes", "$name:$round", 1)
    }

    fun unlike(roomId: String, name: String, round: Int) {
        redisTemplate.opsForHash<String, Long>().increment("qna:$roomId:likes", "$name:$round", -1)
    }

    fun clean(roomId: String) {
        redisTemplate.delete("qna:$roomId:rounds")
        redisTemplate.delete("qna:$roomId:contents")
        redisTemplate.delete("qna:$roomId:answers")
        redisTemplate.delete("qna:$roomId:likes")
    }

    //TODO: refactor) round handling 로직 중복 제거 (getRounds, increaseRound, isFinished)
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

    fun getQuestions(roomId: String): List<String> {
        return redisTemplate.opsForList().range("qna:$roomId:contents", 0, -1)
            ?.filterIsInstance<String>() ?: emptyList()
    }

    fun getAnswersByRound(roomId: String, round: Int): List<AnswerResponse> {
        val playerNames = playerRepository.getPlayerNamesByRoomId(roomId)
        return playerNames.map { name ->
            val answer = redisTemplate.opsForHash<String, String>().get("qna:$roomId:answers", "$name:$round") ?: ""
            val likes = redisTemplate.opsForHash<String, Long>().get("qna:$roomId:likes", "$name:$round") ?: 0
            AnswerResponse(name, answer, likes)
        }
    }
}

data class AnswerResponse(
    val name: String,
    val answer: String,
    val likes: Long
)
