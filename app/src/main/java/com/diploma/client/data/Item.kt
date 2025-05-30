package com.diploma.client.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.UUID

@Entity(tableName = "items")
data class Item(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val barcode: String,
    val quantity: Int,
    val price: Double,
    val shopId: UUID? = null
)
