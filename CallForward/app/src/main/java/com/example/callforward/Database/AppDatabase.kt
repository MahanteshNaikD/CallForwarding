package com.example.callforward.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.callforward.Database.Dao.NumberDao


@Database(entities = [NumberEntity::class], version = 1 , exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun numberDao(): NumberDao

    companion object {
        @Volatile
        private var INSTNACE :AppDatabase? = null

        fun getDataBase(context: Context):AppDatabase {



            val tempInstace = INSTNACE

            if(tempInstace!=null){
                return tempInstace
            }

            return INSTNACE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "NumbersDataBase"
                )
                    .allowMainThreadQueries()
                    .build()
                INSTNACE = instance
                return instance
            }


//            val tempInstace = INSTNACE
//
//            if(tempInstace!=null){
//                return tempInstace
//            }
//
//
//            synchronized(this){
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    "NumbersDataBase"
//                )
//                    .allowMainThreadQueries()
//                    .build()
//
//                INSTNACE = instance
//
//                return instance
//            }
        }
    }
}

