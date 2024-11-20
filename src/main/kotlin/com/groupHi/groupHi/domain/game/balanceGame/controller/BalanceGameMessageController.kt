package com.groupHi.groupHi.domain.game.balanceGame.controller

import com.groupHi.groupHi.domain.game.balanceGame.BalanceGameSelection
import com.groupHi.groupHi.domain.game.balanceGame.BalanceGameTheme
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
        @Payload theme: BalanceGameTheme,
        @Payload totalRounds: Int
    ) {
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as? String
        val name = headerAccessor.sessionAttributes?.get("name") as? String ?: "Unknown"
        messagingTemplate.convertAndSend(
            "/sub/rooms/$roomId",
            MessageResponse(
                type = MessageType.BG_START,
                sender = "System",
                content = balanceGameService.start(roomId!!, name, theme, totalRounds)
            )
        )
    }

    @MessageMapping("/games/balance-game/select-a")
    fun selectA(
        headerAccessor: SimpMessageHeaderAccessor,
        @Payload currentRound: Int
    ) {
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as? String
        val name = headerAccessor.sessionAttributes?.get("name") as? String ?: "Unknown"
        balanceGameService.select(roomId!!, name, currentRound, BalanceGameSelection.A)
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
        @Payload currentRound: Int
    ) {
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as? String
        val name = headerAccessor.sessionAttributes?.get("name") as? String ?: "Unknown"
        balanceGameService.select(roomId!!, name, currentRound, BalanceGameSelection.B)
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
        @Payload currentRound: Int
    ) {
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as? String
        val name = headerAccessor.sessionAttributes?.get("name") as? String ?: "Unknown"
        balanceGameService.unselect(roomId!!, name, currentRound)
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
        balanceGameService.next(roomId!!, name)
        messagingTemplate.convertAndSend(
            "/sub/rooms/$roomId",
            MessageResponse(
                type = MessageType.BG_NEXT,
                sender = "System",
                content = "Move to the next round."
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
