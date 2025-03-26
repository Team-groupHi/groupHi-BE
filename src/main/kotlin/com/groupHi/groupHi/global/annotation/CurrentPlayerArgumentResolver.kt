package com.groupHi.groupHi.global.annotation

import com.groupHi.groupHi.global.exception.error.ErrorCode
import com.groupHi.groupHi.global.exception.exception.MessageException
import org.springframework.core.MethodParameter
import org.springframework.messaging.Message
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component

@Component
class CurrentPlayerArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(CurrentPlayer::class.java) &&
                parameter.parameterType == PlayerSession::class.java
    }

    override fun resolveArgument(parameter: MethodParameter, message: Message<*>): Any? {
        val headerAccessor = StompHeaderAccessor.wrap(message)
        val sessionAttributes = headerAccessor.sessionAttributes ?: throw MessageException(ErrorCode.INVALID_SESSION)
        val roomId = sessionAttributes["roomId"] as? String ?: throw MessageException(ErrorCode.INVALID_SESSION)
        val name = sessionAttributes["name"] as? String ?: throw MessageException(ErrorCode.INVALID_SESSION)
        return PlayerSession(roomId, name)
    }
}
