package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "marble_items")
data class MarbleItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    val length: Double,
    val width: Double,
    val count: Int,
    val timestamp: Long = System.currentTimeMillis()
) {
    val pieceArea: Double
        get() = length * width

    val totalArea: Double
        get() = length * width * count
}
