package com.groupHi.groupHi.storage.game.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "games")
data class Game(
    @Id val id: String? = null,
    var nameKr: String,
    var nameEn: String,
    var descriptionKr: String,
    var descriptionEn: String,
    var thumbnailUrl: String? = null,
)
