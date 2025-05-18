package com.diploma.client.service.model

data class ItemRequest(
    val name: String? = null,
    val barcode: String,
    val shopId: String
)
