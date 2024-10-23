package com.groupHi.groupHi.domain.room.controller

import com.groupHi.groupHi.domain.room.dto.request.RoomCreateRequest
import com.groupHi.groupHi.domain.room.dto.response.RoomGetResponse
import com.groupHi.groupHi.domain.room.service.RoomService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class RoomController(private val roomService: RoomService) {

    @PostMapping("/rooms")
    fun createRoom(@RequestBody request: RoomCreateRequest): String {
        return roomService.createRoom(request)
    }

    @GetMapping("/rooms/{roomId}")
    fun getRoom(@PathVariable roomId: String): RoomGetResponse {
        return roomService.getRoom(roomId)
    }
}
