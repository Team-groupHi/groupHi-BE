package com.groupHi.groupHi.domain.room.controller

import com.groupHi.groupHi.domain.room.dto.request.*
import com.groupHi.groupHi.domain.room.dto.response.RoomGetResponse
import com.groupHi.groupHi.domain.room.dto.response.RoomResultCreateResponse
import com.groupHi.groupHi.domain.room.dto.response.RoomResultGetResponse
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

    @PostMapping("/rooms/{roomId}/results")
    fun createRoomResult(
        @PathVariable roomId: String,
        @RequestBody request: RoomResultCreateRequest
    ): RoomResultCreateResponse {
        return roomService.createRoomResult(roomId, request)
    }

    @GetMapping("/rooms/{roomId}/results")
    fun getRoomResults(@PathVariable roomId: String): List<RoomResultGetResponse> {
        return roomService.getRoomResults(roomId)
    }
}
