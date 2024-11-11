package com.groupHi.groupHi.domain.game.balanceGame.service

import com.groupHi.groupHi.domain.game.balanceGame.dto.response.BalanceGameResultGetResponse
import com.groupHi.groupHi.domain.game.balanceGame.dto.response.BalanceGameSelectionsResponse
import com.groupHi.groupHi.domain.room.service.RoomCacheService
import org.springframework.stereotype.Service

@Service
class BalanceGameService(
    private val balanceGameCacheService: BalanceGameCacheService,
    private val roomCacheService: RoomCacheService
) {

    fun getBalanceGameResults(roomId: String, round: Int?): List<BalanceGameResultGetResponse> {
        val contents = balanceGameCacheService.getContents(roomId)
        val players = roomCacheService.getPlayers(roomId)

        return (contents.indices)
            .filter { round == null || it + 1 == round }
            .map { idx ->
                val content = contents[idx]
                val selection = balanceGameCacheService.getSelections(roomId, idx + 1)
                BalanceGameResultGetResponse(
                    round = idx + 1,
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
