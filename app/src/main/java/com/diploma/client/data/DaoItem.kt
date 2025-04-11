package com.diploma.client.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoItem {
    @Insert
    suspend fun insertItem(item: Item)

    @Query("SELECT * FROM items")
    fun getItems(): Flow<List<Item>>
}