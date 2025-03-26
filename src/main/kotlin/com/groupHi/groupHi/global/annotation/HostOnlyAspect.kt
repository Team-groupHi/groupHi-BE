package com.groupHi.groupHi.global.annotation

import com.groupHi.groupHi.domain.room.repository.RoomRepository
import com.groupHi.groupHi.global.exception.error.ErrorCode
import com.groupHi.groupHi.global.exception.exception.MessageException
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class HostOnlyAspect(private val roomRepository: RoomRepository) {
    @Around("@annotation(HostOnly) && args(.., playerSession)")
    fun checkHost(joinPoint: ProceedingJoinPoint, playerSession: PlayerSession): Any? {
        val isHost = roomRepository.isHost(playerSession.roomId, playerSession.name)
        if (!isHost) {
            throw MessageException(ErrorCode.HOST_ONLY)
        }
        return joinPoint.proceed()
    }
}
