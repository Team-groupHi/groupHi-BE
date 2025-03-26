package com.groupHi.groupHi.domain.game.qnaGame.service

import com.groupHi.groupHi.domain.game.balanceGame.BalanceGameTheme
import com.groupHi.groupHi.domain.game.qnaGame.dto.response.QnaGameAnswerResponse
import com.groupHi.groupHi.domain.game.qnaGame.dto.response.QnaGameResultGetResponse
import com.groupHi.groupHi.domain.game.qnaGame.dto.response.QnaGameRoundResponse
import com.groupHi.groupHi.domain.game.qnaGame.repository.QnaGameRepository
import com.groupHi.groupHi.domain.room.entity.RoomStatus
import com.groupHi.groupHi.domain.room.repository.PlayerRepository
import com.groupHi.groupHi.domain.room.repository.RoomRepository
import com.groupHi.groupHi.domain.room.service.RoomService
import org.springframework.stereotype.Service

@Service
class QnaGameService(
    private val roomService: RoomService,
    private val roomRepository: RoomRepository,
    private val playerRepository: PlayerRepository,
    private val qnaGameRepository: QnaGameRepository
) {

    fun start(roomId: String, theme: BalanceGameTheme, totalRounds: Int): QnaGameRoundResponse {
        roomService.validateStartable(roomId, totalRounds)
        roomRepository.updateRoomStatus(roomId, RoomStatus.PLAYING)
        qnaGameRepository.init(roomId, theme, totalRounds)
        qnaGameRepository.increaseRound(roomId)

        val question = qnaGameRepository.getQuestions(roomId).first()
        return QnaGameRoundResponse(
            totalRounds = totalRounds,
            currentRound = 1,
            question = question
        )
    }

    fun submit(roomId: String, name: String, round: Int, answer: String) {
        qnaGameRepository.submit(roomId, name, round, answer)
    }

    fun like(roomId: String, round: Int, receiver: String) {
        qnaGameRepository.like(roomId, round, receiver)
    }

    fun unlike(roomId: String, round: Int, receiver: String) {
        qnaGameRepository.unlike(roomId, round, receiver)
    }

    fun next(roomId: String): QnaGameRoundResponse {
        qnaGameRepository.increaseRound(roomId)

        val rounds = qnaGameRepository.getRounds(roomId)
        val questions = qnaGameRepository.getQuestions(roomId)[rounds.currentRound - 1]
        return QnaGameRoundResponse(
            totalRounds = rounds.totalRounds,
            currentRound = rounds.currentRound,
            question = questions
        )
    }

    fun isFinished(roomId: String): Boolean {
        val rounds = qnaGameRepository.getRounds(roomId)
        return rounds.currentRound >= rounds.totalRounds
    }

    fun end(roomId: String) {
        playerRepository.resetReady(roomId)
        roomRepository.updateRoomStatus(roomId, RoomStatus.WAITING)
        qnaGameRepository.clean(roomId)
    }

    fun getResults(roomId: String, round: Int?): List<QnaGameResultGetResponse> {
        val questions = qnaGameRepository.getQuestions(roomId)
        return if (round == null) {
            val answers = qnaGameRepository.getAnswersByRound(roomId, 1)
            questions.mapIndexed { idx, question ->
                QnaGameResultGetResponse(
                    round = idx + 1,
                    question = question,
                    result = answers
                        .sortedByDescending { it.likes }
                        .map {
                            QnaGameAnswerResponse(
                                name = it.name,
                                answer = it.answer,
                                likes = it.likes
                            )
                        }
                )
            }
        } else {
            listOf(
                QnaGameResultGetResponse(
                    round = round,
                    question = questions[round - 1],
                    result = qnaGameRepository.getAnswersByRound(roomId, round)
                        .sortedByDescending { it.likes }
                        .map {
                            QnaGameAnswerResponse(
                                name = it.name,
                                answer = it.answer,
                                likes = it.likes
                            )
                        }
                )
            )
        }
    }
}
