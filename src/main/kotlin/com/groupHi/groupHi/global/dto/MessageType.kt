package com.groupHi.groupHi.global.dto

enum class MessageType {
    ERROR,

    // Room
    ENTER,
    EXIT,
    CHAT,
    READY,
    UNREADY,
    CHANGE_GAME,
    CHANGE_PLAYER_NAME,

    // BalanceGame
    BG_START,
    BG_SELECT,
    BG_UNSELECT,
    BG_NEXT,
    BG_ALL_RESULTS,
    BG_END,
}
