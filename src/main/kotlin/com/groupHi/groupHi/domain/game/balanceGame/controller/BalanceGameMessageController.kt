package com.groupHi.groupHi.domain.game.balanceGame.controller

import com.groupHi.groupHi.domain.game.balanceGame.BalanceGameSelection
import com.groupHi.groupHi.domain.game.balanceGame.dto.request.BalanceGameSelectRequest
import com.groupHi.groupHi.domain.game.balanceGame.dto.request.BalanceGameStartRequest
import com.groupHi.groupHi.domain.game.balanceGame.service.BalanceGameService
import com.groupHi.groupHi.global.dto.MessageType
import com.groupHi.groupHi.global.dto.response.MessageResponse
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Controller

@Controller
class BalanceGameMessageController( //TODO: refactor, timer
    private val messagingTemplate: SimpMessageSendingOperations,
    private val balanceGameService: BalanceGameService
) {

    @MessageMapping("/games/balance-game/start")
    fun start(
        headerAccessor: SimpMessageHeaderAccessor,
        @Payload request: BalanceGameStartRequest
    ) {
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as? String
        val name = headerAccessor.sessionAttributes?.get("name") as? String ?: "Unknown"
        messagingTemplate.convertAndSend(
            "/sub/rooms/$roomId",
            MessageResponse(
                type = MessageType.BG_START,
                sender = "System",
                content = balanceGameService.start(roomId!!, name, request.theme, request.totalRounds)
            )
        )
    }

    @MessageMapping("/games/balance-game/select-a")
    fun selectA(
        headerAccessor: SimpMessageHeaderAccessor,
        @Payload request: BalanceGameSelectRequest
    ) {
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as? String
        val name = headerAccessor.sessionAttributes?.get("name") as? String ?: "Unknown"
        balanceGameService.select(roomId!!, name, request.currentRound, BalanceGameSelection.A)
        messagingTemplate.convertAndSend(
            "/sub/rooms/$roomId",
            MessageResponse(
                type = MessageType.BG_SELECT,
                sender = "System",
                content = "$name has selected."
            )
        )
    }

    @MessageMapping("/games/balance-game/select-b")
    fun selectB(
        headerAccessor: SimpMessageHeaderAccessor,
        @Payload request: BalanceGameSelectRequest
    ) {
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as? String
        val name = headerAccessor.sessionAttributes?.get("name") as? String ?: "Unknown"
        balanceGameService.select(roomId!!, name, request.currentRound, BalanceGameSelection.B)
        messagingTemplate.convertAndSend(
            "/sub/rooms/$roomId",
            MessageResponse(
                type = MessageType.BG_SELECT,
                sender = "System",
                content = "$name has selected."
            )
        )
    }

    @MessageMapping("/games/balance-game/unselect")
    fun unselect(
        headerAccessor: SimpMessageHeaderAccessor,
        @Payload request: BalanceGameSelectRequest
    ) {
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as? String
        val name = headerAccessor.sessionAttributes?.get("name") as? String ?: "Unknown"
        balanceGameService.unselect(roomId!!, name, request.currentRound)
        messagingTemplate.convertAndSend(
            "/sub/rooms/$roomId",
            MessageResponse(
                type = MessageType.BG_UNSELECT,
                sender = "System",
                content = "$name has unselected."
            )
        )
    }

    @MessageMapping("/games/balance-game/next")
    fun next(headerAccessor: SimpMessageHeaderAccessor) {
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as? String
        val name = headerAccessor.sessionAttributes?.get("name") as? String ?: "Unknown"
        messagingTemplate.convertAndSend(
            "/sub/rooms/$roomId",
            MessageResponse(
                type = MessageType.BG_NEXT,
                sender = "System",
                content = balanceGameService.next(roomId!!, name)
            )
        )
    }

    @MessageMapping("/games/balance-game/end")
    fun end(headerAccessor: SimpMessageHeaderAccessor) {
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as? String
        val name = headerAccessor.sessionAttributes?.get("name") as? String ?: "Unknown"
        messagingTemplate.convertAndSend(
            "/sub/rooms/$roomId",
            MessageResponse(
                type = MessageType.BG_END,
                sender = "System",
                content = balanceGameService.end(roomId!!, name)
            )
        )
    }
}
