package com.groupHi.groupHi.domain.room.controller

import com.groupHi.groupHi.domain.room.dto.request.RoomChatRequest
import com.groupHi.groupHi.domain.room.dto.request.RoomEnterRequest
import com.groupHi.groupHi.domain.room.dto.request.RoomGameChangeRequest
import com.groupHi.groupHi.domain.room.dto.request.RoomPlayerNameChangeRequest
import com.groupHi.groupHi.domain.room.service.RoomService
import com.groupHi.groupHi.global.annotation.CurrentPlayer
import com.groupHi.groupHi.global.annotation.HostOnly
import com.groupHi.groupHi.global.annotation.PlayerSession
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
class RoomMessageController(
    private val messagingTemplate: SimpMessageSendingOperations,
    private val roomService: RoomService
) {

    @MessageMapping("/rooms/enter")
    fun enterRoom(
        @Payload request: RoomEnterRequest,
        headerAccessor: SimpMessageHeaderAccessor
    ) {
        val avatar = roomService.enterRoom(request.roomId, request.name)
        headerAccessor.sessionAttributes?.set("roomId", request.roomId)
        headerAccessor.sessionAttributes?.set("name", request.name)
        messagingTemplate.convertAndSend(
            "/topic/rooms/${request.roomId}",
            MessageResponse(
                type = MessageType.ENTER,
                sender = request.name,
                content = avatar
            )
        )
    }

    @MessageMapping("/rooms/exit")
    fun exitRoom(@CurrentPlayer player: PlayerSession) {
        val roomId = player.roomId
        val name = player.name
        roomService.exitRoom(roomId, name)
        messagingTemplate.convertAndSend(
            "/topic/rooms/$roomId",
            MessageResponse(
                type = MessageType.EXIT,
                sender = name
            )
        )
    }

    @MessageMapping("/rooms/chat")
    fun chat(
        @Payload request: RoomChatRequest,
        @CurrentPlayer player: PlayerSession,
    ) {
        val roomId = player.roomId
        val name = player.name
        messagingTemplate.convertAndSend(
            "/topic/rooms/$roomId",
            MessageResponse(
                type = MessageType.CHAT,
                sender = name,
                content = request.message
            )
        )
    }

    @MessageMapping("/rooms/ready")
    fun ready(@CurrentPlayer player: PlayerSession) {
        val roomId = player.roomId
        val name = player.name
        roomService.ready(roomId, name)
        messagingTemplate.convertAndSend(
            "/topic/rooms/$roomId",
            MessageResponse(
                type = MessageType.READY,
                sender = name
            )
        )
    }

    @MessageMapping("/rooms/unready")
    fun unready(@CurrentPlayer player: PlayerSession) {
        val roomId = player.roomId
        val name = player.name
        roomService.unready(roomId, name)
        messagingTemplate.convertAndSend(
            "/topic/rooms/$roomId",
            MessageResponse(
                type = MessageType.UNREADY,
                sender = name
            )
        )
    }

    @MessageMapping("/rooms/change-game")
    @HostOnly
    fun changeGame(
        @Payload request: RoomGameChangeRequest,
        @CurrentPlayer player: PlayerSession
    ) {
        val roomId = player.roomId
        messagingTemplate.convertAndSend(
            "/topic/rooms/$roomId",
            MessageResponse(
                type = MessageType.CHANGE_GAME,
                content = roomService.changeGame(roomId, request.gameId)
            )
        )
    }

    @MessageMapping("/rooms/change-player-name")
    fun changePlayerName(
        @Payload request: RoomPlayerNameChangeRequest,
        @CurrentPlayer player: PlayerSession,
        headerAccessor: SimpMessageHeaderAccessor
    ) {
        val roomId = player.roomId
        val name = player.name
        roomService.changePlayerName(roomId, name, request.name)
        headerAccessor.sessionAttributes?.set("name", request.name)
        messagingTemplate.convertAndSend(
            "/topic/rooms/$roomId",
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
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as String
        val name = headerAccessor.sessionAttributes?.get("name") as String
        println("disconnectRoom: $roomId, $name")
        roomService.exitRoom(roomId, name)
        messagingTemplate.convertAndSend(
            "/topic/rooms/$roomId",
            MessageResponse(
                type = MessageType.EXIT,
                sender = name
            )
        )
    }
}
