package com.diploma.client.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface DaoItem {
    @Insert
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("SELECT * FROM items")
    fun getAll(): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE barcode = :barcode")
    fun findByBarcode(barcode: String): Item?

    @Query("SELECT * FROM items WHERE id = :uuid")
    fun findByUuid(uuid: UUID): Item?
}