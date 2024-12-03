package com.groupHi.groupHi.domain.game.balanceGame.service

import com.groupHi.groupHi.domain.game.balanceGame.BalanceGameSelection
import com.groupHi.groupHi.domain.game.balanceGame.BalanceGameTheme
import com.groupHi.groupHi.domain.game.balanceGame.dto.response.BalanceGameResultGetResponse
import com.groupHi.groupHi.domain.game.balanceGame.dto.response.BalanceGameRoundResponse
import com.groupHi.groupHi.domain.game.balanceGame.dto.response.BalanceGameSelectionsResponse
import com.groupHi.groupHi.domain.room.service.RoomCacheService
import com.groupHi.groupHi.domain.room.service.RoomStatus
import com.groupHi.groupHi.global.exception.error.MessageError
import com.groupHi.groupHi.global.exception.exception.MessageException
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BalanceGameService(
    private val roomCacheService: RoomCacheService,
    private val balanceGameCacheService: BalanceGameCacheService
) {

    fun start(roomId: String, name: String, theme: BalanceGameTheme, totalRounds: Int): BalanceGameRoundResponse {
        val room = roomCacheService.getRoom(roomId)
        if (room.hostName != name) {
            throw MessageException(MessageError.ONLY_HOST_CAN_START)
        }
        if (room.players.any { !it.isReady }) {
            throw MessageException(MessageError.NOT_ALL_PLAYERS_READY)
        }
        if (totalRounds < 1 || totalRounds > 20) {
            throw MessageException(MessageError.INVALID_ROUND_COUNT)
        }
        roomCacheService.updateRoomStatus(roomId, RoomStatus.PLAYING)

        balanceGameCacheService.init(roomId, theme, totalRounds)
        balanceGameCacheService.increaseRound(roomId)

        val content = balanceGameCacheService.getContents(roomId).first()
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
        balanceGameCacheService.select(roomId, name, round, selection)
    }

    fun unselect(roomId: String, name: String, round: Int) {
        balanceGameCacheService.unselect(roomId, name, round)
    }

    fun next(roomId: String, name: String): BalanceGameRoundResponse {
        val room = roomCacheService.getRoom(roomId)
        if (room.hostName != name) {
            throw MessageException(MessageError.ONLY_HOST_CAN_NEXT)
        }

        balanceGameCacheService.increaseRound(roomId)
        val rounds = balanceGameCacheService.getRounds(roomId)
        val content = balanceGameCacheService.getContents(roomId)[rounds.currentRound - 1]
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
        val room = roomCacheService.getRoom(roomId)
        if (room.hostName != name) {
            throw MessageException(MessageError.ONLY_HOST_CAN_END)
        }
        roomCacheService.resetPlayerReady(roomId)
        roomCacheService.updateRoomStatus(roomId, RoomStatus.WAITING)
        balanceGameCacheService.clean(roomId)
    }

    fun getBalanceGameResults(roomId: String, round: Int?): List<BalanceGameResultGetResponse> {
        val contents = balanceGameCacheService.getContents(roomId)
        val selections = balanceGameCacheService.getSelections(roomId)

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
