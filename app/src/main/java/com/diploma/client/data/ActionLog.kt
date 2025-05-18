package com.diploma.client.data

import java.sql.Timestamp
import java.util.UUID

data class ActionLog(
    val logUUID: UUID?,
    val createdAt: Timestamp,
    val createdBy: String,
    val entity: String,
    val action: String,
    val details: String,
)
