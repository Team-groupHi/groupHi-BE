package com.groupHi.groupHi.global.exception.error

enum class MessageError(
    val code: String,
    val message: String
) {

    // Common
    INTERNAL_SERVER_ERROR("E000", "Internal Server Error"),

    // Room
    GAME_NOT_FOUND("R001", "Sorry, the game you are trying to play is unavailable."),
    ROOM_NOT_FOUND("R002", "We couldn’t find the room you’re looking for."),
    ONLY_HOST_CAN_CHANGE_GAME("R003", "Only the host can change the game."),
    NAME_CHANGE_NOT_ALLOWED("R004", "Name changes are only allowed in the ready state."),

    // BalanceGame
    ONLY_HOST_CAN_START("B001", "Only the host can start the game."),
    NOT_ALL_PLAYERS_READY("B002", "All players must be ready."),
    INVALID_ROUND_COUNT("B003", "The number of rounds must be between 1 and 20."),
    ONLY_HOST_CAN_NEXT("B004", "Only the host can proceed to the next round."),
    ONLY_HOST_CAN_END("B005", "Only the host can end the game.")
}
