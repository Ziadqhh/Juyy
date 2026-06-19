package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MarbleDao {
    @Query("SELECT * FROM marble_items ORDER BY timestamp DESC")
    fun getAllItemsDesc(): Flow<List<MarbleItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: MarbleItem)

    @Update
    suspend fun updateItem(item: MarbleItem)

    @Delete
    suspend fun deleteItem(item: MarbleItem)

    @Query("DELETE FROM marble_items")
    suspend fun clearAllItems()
}
