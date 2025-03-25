package com.groupHi.groupHi.domain.game.qnaGame.controller

import com.groupHi.groupHi.domain.game.qnaGame.dto.response.QnaGameResultGetResponse
import com.groupHi.groupHi.domain.game.qnaGame.service.QnaGameService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "QnaGame")
@RestController
class QnaGameApiController(private val qnaGameService: QnaGameService) {

    @Operation(summary = "QnA 게임 결과 조회", description = "round가 주어지지 않으면 모든 라운드의 결과를 반환합니다.")
    @GetMapping("/qna-game/results")
    fun getQnaGameResults(
        @RequestParam roomId: String,
        @RequestParam(required = false) round: Int?
    ): List<QnaGameResultGetResponse> {
        return qnaGameService.getResults(roomId, round)
    }
}
