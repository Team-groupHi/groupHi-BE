package com.groupHi.groupHi.domain.game.balanceGame.controller

import com.groupHi.groupHi.domain.game.balanceGame.BalanceGameSelection
import com.groupHi.groupHi.domain.game.balanceGame.dto.request.BalanceGameSelectRequest
import com.groupHi.groupHi.domain.game.balanceGame.dto.request.BalanceGameStartRequest
import com.groupHi.groupHi.domain.game.balanceGame.service.BalanceGameService
import com.groupHi.groupHi.global.annotation.CurrentPlayer
import com.groupHi.groupHi.global.annotation.HostOnly
import com.groupHi.groupHi.global.annotation.PlayerSession
import com.groupHi.groupHi.global.dto.MessageType
import com.groupHi.groupHi.global.dto.response.MessageResponse
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Controller

@Controller
class BalanceGameMessageController(
    private val messagingTemplate: SimpMessageSendingOperations,
    private val balanceGameService: BalanceGameService
) {

    @MessageMapping("/games/balance-game/start")
    @HostOnly
    fun start(
        @Payload request: BalanceGameStartRequest,
        @CurrentPlayer player: PlayerSession
    ) {
        val roomId = player.roomId
        messagingTemplate.convertAndSend(
            "/topic/rooms/$roomId",
            MessageResponse(
                type = MessageType.BG_START,
                content = balanceGameService.start(roomId, request.theme, request.totalRounds)
            )
        )
    }

    @MessageMapping("/games/balance-game/select-a")
    fun selectA(
        @Payload request: BalanceGameSelectRequest,
        @CurrentPlayer player: PlayerSession
    ) {
        val roomId = player.roomId
        val name = player.name
        balanceGameService.select(roomId, name, request.currentRound, BalanceGameSelection.A)
        messagingTemplate.convertAndSend(
            "/topic/rooms/$roomId",
            MessageResponse(
                type = MessageType.BG_SELECT,
                sender = name,
            )
        )
    }

    @MessageMapping("/games/balance-game/select-b")
    fun selectB(
        @Payload request: BalanceGameSelectRequest,
        @CurrentPlayer player: PlayerSession
    ) {
        val roomId = player.roomId
        val name = player.name
        balanceGameService.select(roomId, name, request.currentRound, BalanceGameSelection.B)
        messagingTemplate.convertAndSend(
            "/topic/rooms/$roomId",
            MessageResponse(
                type = MessageType.BG_SELECT,
                sender = name
            )
        )
    }

    @MessageMapping("/games/balance-game/unselect")
    fun unselect(
        @Payload request: BalanceGameSelectRequest,
        @CurrentPlayer player: PlayerSession
    ) {
        val roomId = player.roomId
        val name = player.name
        balanceGameService.unselect(roomId, name, request.currentRound)
        messagingTemplate.convertAndSend(
            "/topic/rooms/$roomId",
            MessageResponse(
                type = MessageType.BG_UNSELECT,
                sender = name
            )
        )
    }

    @MessageMapping("/games/balance-game/next")
    @HostOnly
    fun next(@CurrentPlayer player: PlayerSession) {
        val roomId = player.roomId

        if (balanceGameService.isFinished(roomId)) {
            messagingTemplate.convertAndSend(
                "/topic/rooms/$roomId",
                MessageResponse(MessageType.BG_ALL_RESULTS)
            )
            return
        }

        messagingTemplate.convertAndSend(
            "/topic/rooms/$roomId",
            MessageResponse(
                type = MessageType.BG_NEXT,
                content = balanceGameService.next(roomId)
            )
        )
    }

    @MessageMapping("/games/balance-game/end")
    @HostOnly
    fun end(@CurrentPlayer player: PlayerSession) {
        val roomId = player.roomId
        balanceGameService.end(roomId)
        messagingTemplate.convertAndSend(
            "/topic/rooms/$roomId",
            MessageResponse(MessageType.BG_END)
        )
    }
}
