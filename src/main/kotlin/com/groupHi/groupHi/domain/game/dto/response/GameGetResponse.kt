package com.groupHi.groupHi.domain.game.dto.response

import com.groupHi.groupHi.domain.game.entity.Game

data class GameGetResponse(
    val id: String,
    val nameKr: String,
    val nameEn: String,
    val descriptionKr: String,
    val descriptionEn: String,
    val thumbnailUrl: String?
) {

    companion object {
        fun from(game: Game): GameGetResponse {
            return GameGetResponse(
                id = game.id!!,
                nameKr = game.nameKr,
                nameEn = game.nameEn,
                descriptionKr = game.descriptionKr,
                descriptionEn = game.descriptionEn,
                thumbnailUrl = game.thumbnailUrl
            )
        }
    }
}
