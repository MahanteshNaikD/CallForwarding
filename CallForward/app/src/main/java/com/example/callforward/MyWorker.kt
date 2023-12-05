package com.example.callforward

import android.annotation.SuppressLint
import android.app.Application
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.net.http.HttpException
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.ext.SdkExtensions
import android.telephony.TelephonyManager
import android.util.Log
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.Worker
import com.example.callforward.Database.AppApplication
import com.example.callforward.Database.AppDatabase
import com.example.callforward.Database.NumberEntity
import com.example.callforward.Database.viewModel.NumberViewModel
import com.example.callforward.Database.viewModel.NumberViewModelFactory

class MyWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    private val CHANNEL_ID = "CallForwardingServiceID"



    override fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(1,createNotogification())
    }

    private fun createNotogification(): Notification {

        val notification: Notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setContentTitle("Call Forward Service")
                .setContentText("Service Running")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()

        return notification

    }


    override fun doWork(): Result {

        val database = AppDatabase.getDataBase(applicationContext).numberDao()




      return  try {

            println("peridoic worker is onging")

          val inputData = inputData;

          val number = inputData.getString("number");
          val simId = inputData.getString("simId");
          val id = inputData.getString("id")

          println("number"+number+"simId"+simId);

          if (id != null) {
             val numberData  =  database.getNumberDataById(id.toInt())
              numberData.status = "INACTIVE"
              database.run { update(numberData) }
          }



            runForTask(number.toString(),simId.toString());

            Result.success()

        } catch (e: Exception) {
            //Log.e("TAG", "Error in doWork", e.cause)
            println("Error in doWork" + e.toString())
            Result.retry()
            return Result.failure()
        }

    }

    private fun runForTask(number:String,simId:String) {
        val handler = Handler(Looper.getMainLooper())

        handler.post {

            val callIntent = Intent("callforwardingstatus.TOGGLE_CALL_FORWARDING_FROM_WORKMANAGER");
            callIntent.setClass(applicationContext, CallForwardingReceiver::class.java);
//            callIntent.putExtra("cfi", phoneStateService.currentState);
            callIntent.putExtra("number", number)
            callIntent.putExtra("simId",simId)
            applicationContext.sendBroadcast(callIntent);
        }

    }


}