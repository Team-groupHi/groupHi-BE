package com.groupHi.groupHi.domain.room.controller

import com.groupHi.groupHi.domain.room.dto.request.RoomCreateRequest
import com.groupHi.groupHi.domain.room.dto.request.RoomPlayerNameValidateRequest
import com.groupHi.groupHi.domain.room.dto.response.RoomResponse
import com.groupHi.groupHi.domain.room.service.RoomService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(name = "Room")
@RestController
@RequestMapping("/api/v1")
class RoomApiController(private val roomService: RoomService) {

    @Operation(summary = "방 생성")
    @PostMapping("/rooms")
    fun createRoom(@RequestBody request: RoomCreateRequest): String {
        return roomService.createRoom(request)
    }

    @Operation(summary = "방 상세 조회")
    @GetMapping("/rooms/{roomId}")
    fun getRoom(@PathVariable roomId: String): RoomResponse {
        return roomService.getRoom(roomId)
    }

    @Operation(summary = "닉네임 유효성 체크")
    @PostMapping("/rooms/{roomId}/players/is-valid-name")
    fun isValidPlayerName(@PathVariable roomId: String, @RequestBody request: RoomPlayerNameValidateRequest): Boolean {
        return roomService.isValidPlayerName(roomId, request.name)
    }
}
