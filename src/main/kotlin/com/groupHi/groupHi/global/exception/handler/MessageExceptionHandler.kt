package com.groupHi.groupHi.global.exception.handler

import com.groupHi.groupHi.global.dto.MessageType
import com.groupHi.groupHi.global.dto.response.MessageErrorResponse
import com.groupHi.groupHi.global.dto.response.MessageResponse
import com.groupHi.groupHi.global.exception.error.MessageError
import com.groupHi.groupHi.global.exception.exception.MessageException
import org.springframework.messaging.handler.annotation.MessageExceptionHandler
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.web.bind.annotation.ControllerAdvice

@ControllerAdvice
class MessageExceptionHandler(private val messagingTemplate: SimpMessageSendingOperations) {

    @MessageExceptionHandler
    fun handleMessageException(e: MessageException, headerAccessor: SimpMessageHeaderAccessor) {
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as? String //TODO: refactor
        return messagingTemplate.convertAndSend(
            "/sub/rooms/$roomId",
            MessageResponse(
                type = MessageType.ERROR,
                sender = "System",
                content = e.response
            )
        )
    }

    @MessageExceptionHandler
    fun handleException(e: Exception, headerAccessor: SimpMessageHeaderAccessor) {
        print("🚨 $e")
        e.printStackTrace()
        val roomId = headerAccessor.sessionAttributes?.get("roomId") as? String
        val e = MessageError.INTERNAL_SERVER_ERROR
        return messagingTemplate.convertAndSend(
            "/sub/rooms/$roomId",
            MessageResponse(
                type = MessageType.ERROR,
                sender = "System",
                content = MessageErrorResponse.from(e)
            )
        )
    }
}
