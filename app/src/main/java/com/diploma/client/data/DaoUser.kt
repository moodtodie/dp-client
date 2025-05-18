package com.diploma.client.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface DaoUser {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM users")
    fun getAll(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE username = :username")
    fun findByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE id = :uuid")
    fun findByUUID(uuid: UUID): User?
}