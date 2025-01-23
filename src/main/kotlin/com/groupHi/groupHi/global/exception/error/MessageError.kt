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
    INVALID_NAME("R003", "Please use a different name."),
    ROOM_FULL("R004", "The room is full."),
<<<<<<< Updated upstream
    ONLY_HOST_CAN_CHANGE_GAME("R005", "Only the host can change the game."),
    NAME_CHANGE_NOT_ALLOWED("R006", "Name changes are only allowed in the ready state."),
    ALREADY_PLAYING("R007", "The game is already in progress."),
=======
    DUPLICATE_NAME("R005", "The name is already in use."),
    ONLY_HOST_CAN_CHANGE_GAME("R006", "Only the host can change the game."),
    NAME_CHANGE_NOT_ALLOWED("R007", "Name changes are only allowed in the ready state."),
>>>>>>> Stashed changes

    // BalanceGame
    ONLY_HOST_CAN_START("B001", "Only the host can start the game."),
    NOT_ALL_PLAYERS_READY("B002", "All players must be ready."),
    INVALID_ROUND_COUNT("B003", "The number of rounds must be between 1 and 20."),
    ONLY_HOST_CAN_NEXT("B004", "Only the host can proceed to the next round."),
    ONLY_HOST_CAN_END("B005", "Only the host can end the game.")
}
