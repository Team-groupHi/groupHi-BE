package com.groupHi.groupHi.global.exception.error

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val code: String,
    val message: String,
    val statusCode: HttpStatus = HttpStatus.BAD_REQUEST
) {

    // Common
    INTERNAL_SERVER_ERROR("E000", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),

    // Room
    GAME_NOT_FOUND("R001", "Game not found.", HttpStatus.NOT_FOUND),
    ROOM_NOT_FOUND("R002", "Room not found.", HttpStatus.NOT_FOUND),
    ROOM_FULL("R003", "The room is full."),
    INVALID_NAME("R004", "Invalid name."),
    ONLY_HOST_CAN_CHANGE_GAME("R005", "Only the host can change the game."),
    NAME_CHANGE_NOT_ALLOWED("R006", "Name changes are only allowed in the unready state."),
    ALREADY_PLAYING("R007", "The game is already in progress."),
    NOT_ENOUGH_PLAYERS("R008", "At least two players are required."),

    // BalanceGame
    ONLY_HOST_CAN_START("B001", "Only the host can start the game."),
    NOT_ALL_PLAYERS_READY("B002", "All players must be ready."),
    INVALID_ROUND_COUNT("B003", "Round count must be between 10 and 20."),
    ONLY_HOST_CAN_NEXT("B004", "Only the host can proceed to the next round."),
    ONLY_HOST_CAN_END("B005", "Only the host can end the game.")
}

