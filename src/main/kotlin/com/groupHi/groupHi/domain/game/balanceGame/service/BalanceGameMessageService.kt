package com.groupHi.groupHi.domain.game.balanceGame.service

import com.groupHi.groupHi.domain.game.balanceGame.BalanceGameSelection
import com.groupHi.groupHi.domain.game.balanceGame.BalanceGameTheme
import com.groupHi.groupHi.domain.game.balanceGame.dto.response.BalanceGameResultGetResponse
import com.groupHi.groupHi.domain.game.balanceGame.dto.response.BalanceGameRoundResponse
import com.groupHi.groupHi.domain.game.balanceGame.dto.response.BalanceGameSelectionsResponse
import com.groupHi.groupHi.domain.game.balanceGame.repository.BalanceGameRepository
import com.groupHi.groupHi.domain.room.repository.RoomRepository
import com.groupHi.groupHi.domain.room.repository.RoomStatus
import com.groupHi.groupHi.global.exception.error.MessageError
import com.groupHi.groupHi.global.exception.exception.MessageException
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BalanceGameMessageService(
    private val roomRepository: RoomRepository,
    private val balanceGameRepository: BalanceGameRepository
) {

    fun start(roomId: String, name: String, theme: BalanceGameTheme, totalRounds: Int): BalanceGameRoundResponse {
        val room = roomRepository.getRoom(roomId)
        if (room.hostName != name) {
            throw MessageException(MessageError.ONLY_HOST_CAN_START)
        }
        if (room.players.any { !it.isReady }) {
            throw MessageException(MessageError.NOT_ALL_PLAYERS_READY)
        }
        if (totalRounds < 1 || totalRounds > 20) {
            throw MessageException(MessageError.INVALID_ROUND_COUNT)
        }
        roomRepository.updateRoomStatus(roomId, RoomStatus.PLAYING)

        balanceGameRepository.init(roomId, theme, totalRounds)
        balanceGameRepository.increaseRound(roomId)

        val content = balanceGameRepository.getContents(roomId).first()
        return BalanceGameRoundResponse(
            totalRounds = totalRounds,
            currentRound = 1,
            startTime = LocalDateTime.now(),
            endTime = LocalDateTime.now().plusSeconds(30),
            q = content.q,
            a = content.a,
            b = content.b
        )
    }

    fun select(roomId: String, name: String, round: Int, selection: BalanceGameSelection) {
        balanceGameRepository.select(roomId, name, round, selection)
    }

    fun unselect(roomId: String, name: String, round: Int) {
        balanceGameRepository.unselect(roomId, name, round)
    }

    fun next(roomId: String, name: String): BalanceGameRoundResponse {
        val room = roomRepository.getRoom(roomId)
        if (room.hostName != name) {
            throw MessageException(MessageError.ONLY_HOST_CAN_NEXT)
        }

        balanceGameRepository.increaseRound(roomId)
        val rounds = balanceGameRepository.getRounds(roomId)
        val content = balanceGameRepository.getContents(roomId)[rounds.currentRound - 1]
        return BalanceGameRoundResponse(
            totalRounds = rounds.totalRounds,
            currentRound = rounds.currentRound,
            startTime = LocalDateTime.now(),
            endTime = LocalDateTime.now().plusSeconds(30),
            q = content.q,
            a = content.a,
            b = content.b
        )
    }

    fun end(roomId: String, name: String) {
        val room = roomRepository.getRoom(roomId)
        if (room.hostName != name) {
            throw MessageException(MessageError.ONLY_HOST_CAN_END)
        }
        roomRepository.resetPlayerReady(roomId)
        roomRepository.updateRoomStatus(roomId, RoomStatus.WAITING)
        balanceGameRepository.clean(roomId)
    }

    fun getBalanceGameResults(roomId: String, round: Int?): List<BalanceGameResultGetResponse> {
        val contents = balanceGameRepository.getContents(roomId)
        val selections = balanceGameRepository.getSelections(roomId)

        return contents
            .filter { round == null || it.round == round }
            .map { content ->
                BalanceGameResultGetResponse(
                    round = content.round,
                    q = content.q,
                    a = content.a,
                    b = content.b,
                    result = BalanceGameSelectionsResponse(
                        a = selections[content.round - 1].a,
                        b = selections[content.round - 1].b,
                        c = selections[content.round - 1].c
                    )
                )
            }
    }
}
