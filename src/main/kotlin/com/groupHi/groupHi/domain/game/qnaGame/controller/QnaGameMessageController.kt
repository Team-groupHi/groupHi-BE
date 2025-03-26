package com.groupHi.groupHi.domain.game.qnaGame.controller

import com.groupHi.groupHi.domain.game.balanceGame.BalanceGameTheme
import com.groupHi.groupHi.domain.game.qnaGame.dto.request.QnaGameStartRequest
import com.groupHi.groupHi.domain.game.qnaGame.dto.request.QnaGameSubmitRequest
import com.groupHi.groupHi.domain.game.qnaGame.service.QnaGameService
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
class QnaGameMessageController(
    private val messagingTemplate: SimpMessageSendingOperations,
    private val qnaGameService: QnaGameService
) {


    @MessageMapping("/games/qna-game/start")
    @HostOnly
    fun start(
        @Payload request: QnaGameStartRequest,
        @CurrentPlayer player: PlayerSession
    ) {
        val roomId = player.roomId
        messagingTemplate.convertAndSend(
            "/topic/rooms/$roomId",
            MessageResponse(
                type = MessageType.QNA_START,
                content = qnaGameService.start(roomId, BalanceGameTheme.CLASSIC, request.totalRounds)
            )
        )
    }

    @MessageMapping("/games/qna-game/submit")
    fun submit(
        @Payload request: QnaGameSubmitRequest,
        @CurrentPlayer player: PlayerSession
    ) {
        val roomId = player.roomId
        val name = player.name
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
        @Payload request: QnaGameSubmitRequest,
        @CurrentPlayer player: PlayerSession
    ) {
        val roomId = player.roomId
        val name = player.name
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
        @Payload request: QnaGameSubmitRequest,
        @CurrentPlayer player: PlayerSession
    ) {
        val roomId = player.roomId
        val name = player.name
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
    @HostOnly
    fun next(@CurrentPlayer player: PlayerSession) {
        val roomId = player.roomId

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
                content = qnaGameService.next(roomId)
            )
        )
    }

    @MessageMapping("/games/qna-game/end")
    @HostOnly
    fun end(@CurrentPlayer player: PlayerSession) {
        val roomId = player.roomId
        qnaGameService.end(roomId)
        messagingTemplate.convertAndSend(
            "/topic/rooms/$roomId",
            MessageResponse(MessageType.QNA_END)
        )
    }
}
