package com.groupHi.groupHi.domain.game.balanceGame.controller

import com.groupHi.groupHi.domain.game.balanceGame.BalanceGameSelection
import com.groupHi.groupHi.domain.game.balanceGame.dto.request.BalanceGameSelectRequest
import com.groupHi.groupHi.domain.game.balanceGame.dto.request.BalanceGameStartRequest
import com.groupHi.groupHi.domain.game.balanceGame.service.BalanceGameMessageService
import com.groupHi.groupHi.global.dto.MessageType
import com.groupHi.groupHi.global.dto.response.MessageResponse
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Controller

@Controller
class BalanceGameMessageController( //TODO: refactor
    private val messagingTemplate: SimpMessageSendingOperations,
    private val balanceGameMessageService: BalanceGameMessageService
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
                content = balanceGameMessageService.start(roomId!!, name, request.theme, request.totalRounds)
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
        balanceGameMessageService.select(roomId!!, name, request.currentRound, BalanceGameSelection.A)
        messagingTemplate.convertAndSend(
            "/sub/rooms/$roomId",
            MessageResponse(
                type = MessageType.BG_SELECT,
                sender = name,
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
        balanceGameMessageService.select(roomId!!, name, request.currentRound, BalanceGameSelection.B)
        messagingTemplate.convertAndSend(
            "/sub/rooms/$roomId",
            MessageResponse(
                type = MessageType.BG_SELECT,
                sender = name
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
        balanceGameMessageService.unselect(roomId!!, name, request.currentRound)
        messagingTemplate.convertAndSend(
            "/sub/rooms/$roomId",
            MessageResponse(
                type = MessageType.BG_UNSELECT,
                sender = name
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
                content = balanceGameMessageService.next(roomId!!, name)
            )
        )
    }

    @MessageMapping("/games/balance-game/end")
    fun end(headerAccessor: SimpMessageHeaderAccessor) {
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as? String
        val name = headerAccessor.sessionAttributes?.get("name") as? String ?: "Unknown"
        balanceGameMessageService.end(roomId!!, name)
        messagingTemplate.convertAndSend(
            "/sub/rooms/$roomId",
            MessageResponse( MessageType.BG_END)
        )
    }
}
