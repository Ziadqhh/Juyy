package com.example

import android.app.Application
import com.example.data.AppDatabase
import com.example.data.MarbleRepository

class MarbleApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { MarbleRepository(database.marbleDao()) }
}
