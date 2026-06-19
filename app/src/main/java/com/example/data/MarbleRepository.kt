package com.example.data

import kotlinx.coroutines.flow.Flow

class MarbleRepository(private val marbleDao: MarbleDao) {
    val allItems: Flow<List<MarbleItem>> = marbleDao.getAllItemsDesc()

    suspend fun insertItem(item: MarbleItem) {
        marbleDao.insertItem(item)
    }

    suspend fun updateItem(item: MarbleItem) {
        marbleDao.updateItem(item)
    }

    suspend fun deleteItem(item: MarbleItem) {
        marbleDao.deleteItem(item)
    }

    suspend fun clearAllItems() {
        marbleDao.clearAllItems()
    }
}
