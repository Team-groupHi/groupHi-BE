package com.groupHi.groupHi.storage.game.repository

import com.groupHi.groupHi.storage.game.entity.Game
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface GameRepository : MongoRepository<Game, String>
