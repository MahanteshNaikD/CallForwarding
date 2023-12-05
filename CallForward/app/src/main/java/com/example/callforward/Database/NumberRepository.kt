package com.example.callforward.Database

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.callforward.Database.Dao.NumberDao
import kotlinx.coroutines.flow.Flow

class NumberRepository(private val numberDao: NumberDao) {

    val readNumberData :NumberEntity = numberDao.getLatestNumber()

    val getAllNumber : Flow<List<NumberEntity?>?> = numberDao.getAllData()
    suspend fun addNumber(numberEntity: NumberEntity){
        numberDao.insert(numberEntity)
    }


    suspend fun update(numberEntity: NumberEntity){
        numberDao.update(numberEntity)
    }

    suspend fun delete(numberEntity: NumberEntity){
        numberDao.delete(numberEntity)
    }


    fun getNumberDataById(id:Int) :NumberEntity {
       return numberDao.getNumberDataById(id)
    }


//     fun getAllNumber():List<NumberEntity>{
//        numberDao.getAllData()
//    }


}