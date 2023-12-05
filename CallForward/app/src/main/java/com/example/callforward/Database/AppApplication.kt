package com.example.callforward.Database

import android.app.Application

class AppApplication:Application() {
    val database : AppDatabase by lazy { AppDatabase.getDataBase(this) }
    val repository by lazy { NumberRepository(database.numberDao()) }
}