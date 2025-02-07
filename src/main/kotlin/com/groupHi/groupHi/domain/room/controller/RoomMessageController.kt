package com.groupHi.groupHi.domain.room.controller

import com.groupHi.groupHi.domain.room.dto.request.RoomChatRequest
import com.groupHi.groupHi.domain.room.dto.request.RoomEnterRequest
import com.groupHi.groupHi.domain.room.dto.request.RoomGameChangeRequest
import com.groupHi.groupHi.domain.room.dto.request.RoomPlayerNameChangeRequest
import com.groupHi.groupHi.domain.room.service.RoomMessageService
import com.groupHi.groupHi.global.dto.MessageType
import com.groupHi.groupHi.global.dto.response.MessageResponse
import org.springframework.context.event.EventListener
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Controller
import org.springframework.web.socket.messaging.SessionDisconnectEvent

@Controller
class RoomMessageController( //TODO: refactor
    private val messagingTemplate: SimpMessageSendingOperations,
    private val roomMessageService: RoomMessageService
) {

    @MessageMapping("/rooms/enter")
    fun enterRoom(
        @Payload request: RoomEnterRequest,
        headerAccessor: SimpMessageHeaderAccessor
    ) {
        val avatar = roomMessageService.enterRoom(request.roomId, request.name)
        headerAccessor.sessionAttributes?.set("roomId", request.roomId)
        headerAccessor.sessionAttributes?.set("name", request.name)
        headerAccessor.sessionAttributes?.set("avatar", avatar)
        messagingTemplate.convertAndSend(
            "/sub/rooms/${request.roomId}",
            MessageResponse(
                type = MessageType.ENTER,
                sender = request.name,
                content = avatar
            )
        )
    }

    @MessageMapping("/rooms/exit")
    fun exitRoom(headerAccessor: SimpMessageHeaderAccessor) {
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as? String
        val name = headerAccessor.sessionAttributes?.get("name") as? String ?: "Unknown"
        val avatar = headerAccessor.sessionAttributes?.get("avatar") as String
        roomMessageService.exitRoom(roomId!!, name, avatar)
        messagingTemplate.convertAndSend(
            "/sub/rooms/$roomId",
            MessageResponse(
                type = MessageType.EXIT,
                sender = name
            )
        )
    }

    @MessageMapping("/rooms/chat")
    fun chat(
        @Payload request: RoomChatRequest,
        headerAccessor: SimpMessageHeaderAccessor
    ) {
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as? String
        val name = headerAccessor.sessionAttributes?.get("name") as? String ?: "Unknown"
        messagingTemplate.convertAndSend(
            "/sub/rooms/$roomId",
            MessageResponse(
                type = MessageType.CHAT,
                sender = name,
                content = request.message
            )
        )
    }

    @MessageMapping("/rooms/ready")
    fun ready(headerAccessor: SimpMessageHeaderAccessor) {
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as? String
        val name = headerAccessor.sessionAttributes?.get("name") as? String ?: "Unknown"
        roomMessageService.ready(roomId!!, name)
        messagingTemplate.convertAndSend(
            "/sub/rooms/$roomId",
            MessageResponse(
                type = MessageType.READY,
                sender = name
            )
        )
    }

    @MessageMapping("/rooms/unready")
    fun unready(headerAccessor: SimpMessageHeaderAccessor) {
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as? String
        val name = headerAccessor.sessionAttributes?.get("name") as? String ?: "Unknown"
        roomMessageService.unready(roomId!!, name)
        messagingTemplate.convertAndSend(
            "/sub/rooms/$roomId",
            MessageResponse(
                type = MessageType.UNREADY,
                sender = name
            )
        )
    }

    @MessageMapping("/rooms/change-game")
    fun changeGame(
        @Payload request: RoomGameChangeRequest,
        headerAccessor: SimpMessageHeaderAccessor
    ) {
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as? String
        val name = headerAccessor.sessionAttributes?.get("name") as? String ?: "Unknown"
        messagingTemplate.convertAndSend(
            "/sub/rooms/$roomId",
            MessageResponse(
                type = MessageType.CHANGE_GAME,
                content = roomMessageService.changeGame(roomId!!, name, request.gameId)
            )
        )
    }

    @MessageMapping("/rooms/change-player-name")
    fun changePlayerName(
        @Payload request: RoomPlayerNameChangeRequest,
        headerAccessor: SimpMessageHeaderAccessor
    ) {
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as? String
        val name = headerAccessor.sessionAttributes?.get("name") as? String ?: "Unknown"
        val avatar = headerAccessor.sessionAttributes?.get("avatar") as String
        roomMessageService.changePlayerName(roomId!!, name, request.name, avatar)
        headerAccessor.sessionAttributes?.set("name", request.name)
        messagingTemplate.convertAndSend(
            "/sub/rooms/$roomId",
            MessageResponse(
                type = MessageType.CHANGE_PLAYER_NAME,
                sender = name,
                content = request.name
            )
        )
    }

    @EventListener
    fun disconnectRoom(event: SessionDisconnectEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(event.message)
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as? String
        val name = headerAccessor.sessionAttributes?.get("name") as? String ?: "Unknown"
        val avatar = headerAccessor.sessionAttributes?.get("avatar") as String
        roomMessageService.exitRoom(roomId!!, name, avatar)
        messagingTemplate.convertAndSend(
            "/sub/rooms/$roomId",
            MessageResponse(
                type = MessageType.EXIT,
                sender = name
            )
        )
    }
}
