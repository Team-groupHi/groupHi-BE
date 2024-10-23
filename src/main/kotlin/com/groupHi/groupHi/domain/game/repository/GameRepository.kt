package com.groupHi.groupHi.domain.game.repository

import com.groupHi.groupHi.domain.game.entity.Game
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface GameRepository : MongoRepository<Game, String>
