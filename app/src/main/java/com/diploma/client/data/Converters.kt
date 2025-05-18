package com.diploma.client.data

import androidx.room.TypeConverter
import java.math.BigDecimal
import java.util.UUID

class Converters {
    @TypeConverter
    fun fromUUID(uuid: UUID?): String? = uuid?.toString()

    @TypeConverter
    fun toUUID(uuid: String?): UUID? = uuid?.let { UUID.fromString(it) }
}