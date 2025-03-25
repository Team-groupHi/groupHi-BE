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
import com.groupHi.groupHi.global.exception.error.ErrorCode
import com.groupHi.groupHi.global.exception.exception.MessageException
import org.springframework.stereotype.Service

@Service
class QnaGameService(
    private val roomService: RoomService,
    private val roomRepository: RoomRepository,
    private val playerRepository: PlayerRepository,
    private val qnaGameRepository: QnaGameRepository
) {

    fun start(roomId: String, name: String, theme: BalanceGameTheme, totalRounds: Int): QnaGameRoundResponse {
        roomService.validateStartable(roomId, name, totalRounds)
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

    fun like(roomId: String, name: String, round: Int) {
        qnaGameRepository.like(roomId, name, round)
    }

    fun unlike(roomId: String, name: String, round: Int) {
        qnaGameRepository.unlike(roomId, name, round)
    }

    fun next(roomId: String, name: String): QnaGameRoundResponse {
        val room = roomService.getRoom(roomId)
        if (room.hostName != name) {
            throw MessageException(ErrorCode.ONLY_HOST_CAN_NEXT)
        }

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

    fun end(roomId: String, name: String) {
        val room = roomService.getRoom(roomId)
        if (room.hostName != name) {
            throw MessageException(ErrorCode.ONLY_HOST_CAN_END)
        }

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
                    result = answers.map { answer ->
                        QnaGameAnswerResponse(
                            name = answer.name,
                            answer = answer.answer,
                            likes = answer.likes
                        )
                    }
                )
            }
        } else {
            listOf(
                QnaGameResultGetResponse(
                    round = round,
                    question = questions[round - 1],
                    result = qnaGameRepository.getAnswersByRound(roomId, round).map { answer ->
                        QnaGameAnswerResponse(
                            name = answer.name,
                            answer = answer.answer,
                            likes = answer.likes
                        )
                    }
                )
            )
        }
    }
}
