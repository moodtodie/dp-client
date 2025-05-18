package com.diploma.client.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val username: String,
    val role: String,
    val shopId: UUID? = null
)
