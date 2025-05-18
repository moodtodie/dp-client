package com.diploma.client.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Item::class, User::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class MainDb : RoomDatabase() {
    abstract val daoItem: DaoItem
    abstract val daoUser: DaoUser
}