package com.groupHi.groupHi.global.exception.error

enum class MessageError(
    val code: String,
    val message: String
) {

    // Common
    INTERNAL_SERVER_ERROR("E0000", "Internal Server Error"),
}
