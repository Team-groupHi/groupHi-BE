package com.groupHi.groupHi.domain.room.controller

import com.groupHi.groupHi.domain.room.dto.request.RoomCreateRequest
import com.groupHi.groupHi.domain.room.dto.response.RoomGetResponse
import com.groupHi.groupHi.domain.room.service.RoomService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(name = "Room")
@RestController
@RequestMapping("/api/v1")
class RoomController(private val roomService: RoomService) {

    @Operation(summary = "방 생성")
    @PostMapping("/rooms")
    fun createRoom(@RequestBody request: RoomCreateRequest): String {
        return roomService.createRoom(request)
    }

    @Operation(summary = "방 상세 조회")
    @GetMapping("/rooms/{roomId}")
    fun getRoom(@PathVariable roomId: String): RoomGetResponse {
        return roomService.getRoom(roomId)
    }
}
