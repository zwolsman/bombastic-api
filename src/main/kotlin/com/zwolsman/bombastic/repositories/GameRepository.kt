package com.zwolsman.bombastic.repositories

import com.zwolsman.bombastic.db.GameModel
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface GameRepository : ReactiveCrudRepository<GameModel, String>
