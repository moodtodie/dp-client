package com.diploma.client.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Item::class],
    version = 1
)
abstract class MainDb : RoomDatabase() {
    abstract val daoItem: DaoItem
}