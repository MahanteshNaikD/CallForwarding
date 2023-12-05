package com.example.callforward.Database.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.callforward.Database.NumberEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface NumberDao {

    @Query("SELECT * FROM number_table")
    fun getAllData(): Flow<List<NumberEntity?>?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert( vararg numberEntity: NumberEntity)

    @Query("SELECT * FROM number_table ORDER BY id DESC LIMIT 1")
    fun getLatestNumber(): NumberEntity;

    @Update
     fun update(numberEntity: NumberEntity)

    @Delete
    suspend fun delete(numberEntity: NumberEntity)

    @Query("SELECT * FROM number_table WHERE id = :id")
    fun getNumberDataById(id:Int):NumberEntity

}