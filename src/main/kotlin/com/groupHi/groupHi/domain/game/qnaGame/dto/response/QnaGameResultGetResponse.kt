package com.groupHi.groupHi.domain.game.qnaGame.dto.response

data class QnaGameResultGetResponse(
    val round: Int,
    val question: String,
    val result: List<QnaGameAnswerResponse>
)

data class QnaGameAnswerResponse(
    val name: String,
    val answer: String,
    val likes: Long
)
