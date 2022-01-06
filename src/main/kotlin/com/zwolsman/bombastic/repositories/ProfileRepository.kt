package com.zwolsman.bombastic.repositories

import com.zwolsman.bombastic.db.ProfileModel
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProfileRepository : ReactiveCrudRepository<ProfileModel, Long>
