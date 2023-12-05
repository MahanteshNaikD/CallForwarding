package com.example.callforward.Database.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.callforward.Database.NumberEntity
import com.example.callforward.Database.NumberRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Objects

class NumberViewModel(private val repository: NumberRepository) :ViewModel() {



    val allData:NumberEntity = repository.readNumberData

    val getAllNumberData : LiveData<List<NumberEntity?>?> = repository.getAllNumber.asLiveData()


    fun addNumber(numberEntity: NumberEntity){
        viewModelScope.launch(Dispatchers.IO) { repository.addNumber(numberEntity)  }
    }

    fun getAllData(){
        viewModelScope.launch { repository.getAllNumber }
    }


    fun update(numberEntity: NumberEntity){
        viewModelScope.launch { repository.update(numberEntity) }
    }

    fun delete(numberEntity: NumberEntity){
        viewModelScope.launch { repository.delete(numberEntity) }
    }


    fun getNumberDataById(id:Int){
        viewModelScope.launch { repository.getNumberDataById(id) }
    }

    private var _workProgress = MutableLiveData<WorkInfo>()
    val workProgress: LiveData<WorkInfo> get() = _workProgress

    fun startWork(context: Context){
        WorkManager.getInstance(context).getWorkInfosByTagLiveData("oneTimeRequestWorker").observeForever { workInfo->

//            _workProgress = workInfo
           workInfo.let {
//               val progress = workInfo[1].progress.getInt("progress",0)
               if (workInfo[0]!=null){
                   _workProgress.postValue(workInfo[0])
               }
           }
        }
    }



}


class NumberViewModelFactory(private val repository: NumberRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if(modelClass.isAssignableFrom(NumberViewModel::class.java)) {
            return NumberViewModel(repository) as T
        }
       throw IllegalArgumentException("unkown viewmodel class")
    }
}