package com.groupHi.groupHi.domain.game.qnaGame.controller

import com.groupHi.groupHi.domain.game.balanceGame.BalanceGameTheme
import com.groupHi.groupHi.domain.game.qnaGame.dto.request.QnaGameStartRequest
import com.groupHi.groupHi.domain.game.qnaGame.dto.request.QnaGameSubmitRequest
import com.groupHi.groupHi.domain.game.qnaGame.service.QnaGameService
import com.groupHi.groupHi.global.dto.MessageType
import com.groupHi.groupHi.global.dto.response.MessageResponse
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Controller

@Controller
class QnaGameMessageController(
    private val messagingTemplate: SimpMessageSendingOperations,
    private val qnaGameService: QnaGameService
) {

    @MessageMapping("/games/qna-game/start")
    fun start(
        headerAccessor: SimpMessageHeaderAccessor,
        @Payload request: QnaGameStartRequest
    ) {
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as String
        val name = headerAccessor.sessionAttributes?.get("name") as String
        messagingTemplate.convertAndSend(
            "/topic/rooms/$roomId",
            MessageResponse(
                type = MessageType.QNA_START,
                content = qnaGameService.start(roomId, name, BalanceGameTheme.CLASSIC, request.totalRounds)
            )
        )
    }

    @MessageMapping("/games/qna-game/submit")
    fun submit(
        headerAccessor: SimpMessageHeaderAccessor,
        @Payload request: QnaGameSubmitRequest
    ) {
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as String
        val name = headerAccessor.sessionAttributes?.get("name") as String
        qnaGameService.submit(roomId, name, request.round, request.answer)
        messagingTemplate.convertAndSend(
            "/topic/rooms/$roomId",
            MessageResponse(
                type = MessageType.QNA_SUBMIT,
                sender = name
            )
        )
    }

    @MessageMapping("/games/qna-game/like")
    fun like(
        headerAccessor: SimpMessageHeaderAccessor,
        @Payload request: QnaGameSubmitRequest
    ) {
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as String
        val name = headerAccessor.sessionAttributes?.get("name") as String
        qnaGameService.like(roomId, name, request.round)
        messagingTemplate.convertAndSend(
            "/topic/rooms/$roomId",
            MessageResponse(
                type = MessageType.QNA_LIKE,
                sender = name
            )
        )
    }

    @MessageMapping("/games/qna-game/unlike")
    fun unlike(
        headerAccessor: SimpMessageHeaderAccessor,
        @Payload request: QnaGameSubmitRequest
    ) {
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as String
        val name = headerAccessor.sessionAttributes?.get("name") as String
        qnaGameService.unlike(roomId, name, request.round)
        messagingTemplate.convertAndSend(
            "/topic/rooms/$roomId",
            MessageResponse(
                type = MessageType.QNA_UNLIKE,
                sender = name
            )
        )
    }

    @MessageMapping("/games/qna-game/next")
    fun next(headerAccessor: SimpMessageHeaderAccessor) {
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as String
        val name = headerAccessor.sessionAttributes?.get("name") as String

        if (qnaGameService.isFinished(roomId)) {
            messagingTemplate.convertAndSend(
                "/topic/rooms/$roomId",
                MessageResponse(MessageType.QNA_ALL_RESULTS)
            )
            return
        }

        messagingTemplate.convertAndSend(
            "/topic/rooms/$roomId",
            MessageResponse(
                type = MessageType.QNA_NEXT,
                content = qnaGameService.next(roomId, name)
            )
        )
    }

    @MessageMapping("/games/qna-game/end")
    fun end(headerAccessor: SimpMessageHeaderAccessor) {
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as String
        val name = headerAccessor.sessionAttributes?.get("name") as String
        qnaGameService.end(roomId, name)
        messagingTemplate.convertAndSend(
            "/topic/rooms/$roomId",
            MessageResponse(MessageType.QNA_END)
        )
    }
}
