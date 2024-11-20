package com.groupHi.groupHi.domain.game.balanceGame.service

import com.groupHi.groupHi.domain.game.balanceGame.BalanceGameSelection
import com.groupHi.groupHi.domain.game.balanceGame.BalanceGameTheme
import com.groupHi.groupHi.domain.game.balanceGame.dto.response.BalanceGameResultGetResponse
import com.groupHi.groupHi.domain.game.balanceGame.dto.response.BalanceGameSelectionsResponse
import com.groupHi.groupHi.domain.game.balanceGame.dto.response.BalanceGameRoundResponse
import com.groupHi.groupHi.domain.room.service.RoomCacheService
import com.groupHi.groupHi.domain.room.service.RoomStatus
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
            throw IllegalArgumentException("Only the host can start the game.")
        }
        if (room.players.any { !it.isReady }) {
            throw IllegalArgumentException("All players must be ready.")
        }
        if (totalRounds < 1) {
            throw IllegalArgumentException("The number of rounds must be greater than 0.")
        }
        if (totalRounds > 20) {
            throw IllegalArgumentException("The number of rounds must be less than 20.")
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
            throw IllegalArgumentException("Only the host can start the game.")
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

    fun end(roomId: String, name: String): List<BalanceGameResultGetResponse> {
        val room = roomCacheService.getRoom(roomId)
        if (room.hostName != name) {
            throw IllegalArgumentException("Only the host can start the game.")
        }
        roomCacheService.resetPlayerReady(roomId)
        roomCacheService.updateRoomStatus(roomId, RoomStatus.WAITING)
        balanceGameCacheService.clean(roomId)
        return getBalanceGameResults(roomId, null)
    }

    fun getBalanceGameResults(roomId: String, round: Int?): List<BalanceGameResultGetResponse> {
        val contents = balanceGameCacheService.getContents(roomId)
        val players = roomCacheService.getPlayers(roomId)

        return contents
            .filter { round == null || it.round == round }
            .map { content ->
                val selection = balanceGameCacheService.getSelections(roomId, content.round)
                BalanceGameResultGetResponse(
                    round = content.round,
                    q = content.q,
                    a = content.a,
                    b = content.b,
                    result = BalanceGameSelectionsResponse(
                        a = selection.a,
                        b = selection.b,
                        c = players
                            .filter { player -> player.name !in selection.a && player.name !in selection.b }
                            .map { player -> player.name }
                    )
                )
            }
    }
}
