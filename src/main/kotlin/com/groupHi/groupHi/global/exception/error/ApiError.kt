package com.groupHi.groupHi.global.exception.error

import org.springframework.http.HttpStatus

enum class ApiError(
    val statusCode: HttpStatus,
    val code: String,
    val message: String
) {

    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E000", "Internal Server Error"),

    // Room
    GAME_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "Sorry, the game you are trying to play is unavailable."),
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "R002", "We couldn’t find the room you’re looking for."),
}
