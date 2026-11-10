package com.smarthotel.app.data

data class Room(
    val id: String,
    val name: String,
    val category: String,
    val imageAssetName: String,
    val price: Int,
    val guests: Int,
    val beds: Int,
    val baths: Int,
    val rating: Double,
    val tag: String,
    val colorA: String,
    val colorB: String
)
