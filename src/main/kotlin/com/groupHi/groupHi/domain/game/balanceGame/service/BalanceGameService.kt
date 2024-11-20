package com.groupHi.groupHi.domain.game.balanceGame.service

import com.groupHi.groupHi.domain.game.balanceGame.BalanceGameTheme
import com.groupHi.groupHi.domain.game.balanceGame.dto.response.BalanceGameResultGetResponse
import com.groupHi.groupHi.domain.game.balanceGame.dto.response.BalanceGameSelectionsResponse
import com.groupHi.groupHi.domain.game.balanceGame.dto.response.RoundResponse
import com.groupHi.groupHi.domain.room.service.RoomCacheService
import com.groupHi.groupHi.domain.room.service.RoomStatus
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BalanceGameService(
    private val roomCacheService: RoomCacheService,
    private val balanceGameCacheService: BalanceGameCacheService
) {

    fun start(roomId: String, name: String, theme: BalanceGameTheme, totalRounds: Int): RoundResponse {
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
        return RoundResponse(
            totalRounds = totalRounds,
            currentRound = 1,
            startTime = LocalDateTime.now(),
            endTime = LocalDateTime.now().plusSeconds(30),
            q = content.q,
            a = content.a,
            b = content.b
        )
    }

    fun select(roomId: String, name: String, round: Int, selection: String) {
    }

    fun unselect(roomId: String, name: String) {
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

    fun next(roomId: String, name: String) {
        // round 세팅
        // selection 세팅
        // timer 및 qna 전송
    }

    fun end(roomId: String, name: String) {
        // 결과 전송 및 데이터 정리
    }
}
