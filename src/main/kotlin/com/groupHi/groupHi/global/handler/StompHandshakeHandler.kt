package com.groupHi.groupHi.global.handler

import org.springframework.web.socket.server.support.DefaultHandshakeHandler
import java.security.Principal

class StompPrincipal(val id: String) : Principal {
    override fun getName(): String {
        return id
    }
}

class StompHandshakeHandler : DefaultHandshakeHandler() {
    override fun determineUser(
        request: org.springframework.http.server.ServerHttpRequest,
        wsHandler: org.springframework.web.socket.WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): java.security.Principal {
        val userId = java.util.UUID.randomUUID().toString()
        return StompPrincipal(userId)
    }
}
