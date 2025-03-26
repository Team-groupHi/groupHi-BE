package com.groupHi.groupHi.global.config

import com.groupHi.groupHi.global.annotation.CurrentPlayerArgumentResolver
import com.groupHi.groupHi.global.handler.StompHandshakeHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
    @Value("\${allowed-origins}") private val allowedOrigins: String,
    private val currentPlayerArgumentResolver: CurrentPlayerArgumentResolver
) :
    WebSocketMessageBrokerConfigurer {

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws")
            .setAllowedOrigins(allowedOrigins)
            .setHandshakeHandler(StompHandshakeHandler())
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/sub", "/topic", "/queue") //TODO: sub 삭제
            .setHeartbeatValue(longArrayOf(10000, 10000))
            .setTaskScheduler(taskScheduler())
        registry.setApplicationDestinationPrefixes("/pub", "/app") //TODO: pub 삭제
        registry.setUserDestinationPrefix("/user")
    }

    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
        argumentResolvers.add(currentPlayerArgumentResolver)
    }

    @Bean
    fun taskScheduler(): ThreadPoolTaskScheduler {
        val scheduler = ThreadPoolTaskScheduler()
        scheduler.poolSize = 1
        scheduler.setThreadNamePrefix("WebSocket-Heartbeat-")
        scheduler.initialize()
        return scheduler
    }
}
