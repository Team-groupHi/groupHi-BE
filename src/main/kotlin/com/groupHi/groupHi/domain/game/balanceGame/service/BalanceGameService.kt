package com.groupHi.groupHi.domain.game.balanceGame.service

import com.groupHi.groupHi.domain.game.balanceGame.BalanceGameSelection
import com.groupHi.groupHi.domain.game.balanceGame.BalanceGameTheme
import com.groupHi.groupHi.domain.game.balanceGame.dto.response.BalanceGameResultGetResponse
import com.groupHi.groupHi.domain.game.balanceGame.dto.response.BalanceGameRoundResponse
import com.groupHi.groupHi.domain.game.balanceGame.dto.response.BalanceGameSelectionsResponse
import com.groupHi.groupHi.domain.game.balanceGame.repository.BalanceGameRepository
import com.groupHi.groupHi.domain.room.entity.RoomStatus
import com.groupHi.groupHi.domain.room.repository.PlayerRepository
import com.groupHi.groupHi.domain.room.repository.RoomRepository
import com.groupHi.groupHi.domain.room.service.RoomService
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class BalanceGameService(
    private val roomService: RoomService,
    private val roomRepository: RoomRepository,
    private val playerRepository: PlayerRepository,
    private val balanceGameRepository: BalanceGameRepository
) {

    fun start(roomId: String, theme: BalanceGameTheme, totalRounds: Int): BalanceGameRoundResponse {
        roomService.validateStartable(roomId, totalRounds)
        roomRepository.updateRoomStatus(roomId, RoomStatus.PLAYING)
        balanceGameRepository.init(roomId, theme, totalRounds)
        balanceGameRepository.increaseRound(roomId)

        val content = balanceGameRepository.getContents(roomId).first()
        return BalanceGameRoundResponse(
            totalRounds = totalRounds,
            currentRound = 1,
            startTime = Instant.now(),
            endTime = Instant.now().plusSeconds(10), //TODO: 시간 상수화 or 설정값으로 변경
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

    fun isFinished(roomId: String): Boolean {
        val rounds = balanceGameRepository.getRounds(roomId)
        return rounds.currentRound >= rounds.totalRounds
    }

    fun next(roomId: String): BalanceGameRoundResponse {
        balanceGameRepository.increaseRound(roomId)

        val rounds = balanceGameRepository.getRounds(roomId)
        val content = balanceGameRepository.getContents(roomId)[rounds.currentRound - 1]
        return BalanceGameRoundResponse(
            totalRounds = rounds.totalRounds,
            currentRound = rounds.currentRound,
            startTime = Instant.now(),
            endTime = Instant.now().plusSeconds(10), //TODO: 시간 상수화 or 설정값으로 변경
            q = content.q,
            a = content.a,
            b = content.b
        )
    }

    fun end(roomId: String) {
        playerRepository.resetReady(roomId)
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
