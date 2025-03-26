package com.groupHi.groupHi.global.annotation

import com.groupHi.groupHi.global.exception.error.ErrorCode
import com.groupHi.groupHi.global.exception.exception.MessageException
import org.springframework.core.MethodParameter
import org.springframework.messaging.Message
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class CurrentPlayerArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(CurrentPlayer::class.java) &&
                parameter.parameterType == PlayerSession::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val headerAccessor = StompHeaderAccessor.wrap(webRequest.getNativeRequest(Message::class.java)!!)
        val sessionAttributes = headerAccessor.sessionAttributes ?: throw MessageException(ErrorCode.INVALID_SESSION)
        val roomId = sessionAttributes["roomId"] as? String ?: throw MessageException(ErrorCode.INVALID_SESSION)
        val name = sessionAttributes["name"] as? String ?: throw MessageException(ErrorCode.INVALID_SESSION)
        return PlayerSession(roomId, name)
    }
}
