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
        headerAccessor.sessionAttributes?.set("roomId", request.roomId)
        headerAccessor.sessionAttributes?.set("name", request.name)
        roomMessageService.enterRoom(request.roomId, request.name)
        messagingTemplate.convertAndSend(
            "/sub/rooms/${request.roomId}",
            MessageResponse(
                type = MessageType.ENTER,
                sender = "System",
                content = "${request.name} has entered the room."
            )
        )
    }

    @MessageMapping("/rooms/exit")
    fun exitRoom(headerAccessor: SimpMessageHeaderAccessor) {
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as? String
        val name = headerAccessor.sessionAttributes?.get("name") as? String ?: "Unknown"
        roomMessageService.exitRoom(roomId!!, name) //TODO: 방장 나가면 방 폭바
        messagingTemplate.convertAndSend(
            "/sub/rooms/$roomId",
            MessageResponse(
                type = MessageType.EXIT,
                sender = "System",
                content = "$name has left the room."
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
                sender = "System",
                content = "$name is ready."
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
                sender = "System",
                content = "$name is unready."
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
        roomMessageService.changeGame(roomId!!, name, request.gameId) //TODO: 방장 권한
        messagingTemplate.convertAndSend(
            "/sub/rooms/$roomId",
            MessageResponse(
                type = MessageType.CHANGE_GAME,
                sender = "System",
                content = "$name has changed the game."
            )
        )
    }

    @MessageMapping("/rooms/change-player-name")
    fun changePlayerName(
        @Payload request: RoomPlayerNameChangeRequest,
        headerAccessor: SimpMessageHeaderAccessor
    ) {
        headerAccessor.sessionAttributes?.set("name", request.name)
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as? String
        val name = headerAccessor.sessionAttributes?.get("name") as? String ?: "Unknown"
        roomMessageService.changePlayerName(roomId!!, name, request.name)
        messagingTemplate.convertAndSend(
            "/sub/rooms/$roomId",
            MessageResponse(
                type = MessageType.CHANGE_PLAYER_NAME,
                sender = "System",
                content = request.name
            )
        )
    }

    @EventListener
    fun disconnectRoom(event: SessionDisconnectEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(event.message)
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as? String
        val name = headerAccessor.sessionAttributes?.get("name") as? String ?: "Unknown"
        roomMessageService.exitRoom(roomId!!, name)
        messagingTemplate.convertAndSend(
            "/sub/rooms/$roomId",
            MessageResponse(
                type = MessageType.EXIT,
                sender = "System",
                content = "$name has left the room."
            )
        )
    }
}
