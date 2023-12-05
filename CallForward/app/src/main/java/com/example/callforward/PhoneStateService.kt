package com.example.callforward

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyCallback.CallForwardingIndicatorListener
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import java.util.concurrent.Executor
import java.util.concurrent.Executors

public class PhoneStateService : Service() {

    private val CHANNEL_ID = "CallForwardingServiceID"
    var currentState = false
    var context: Context? = null
    val TAG = "Service"
    private val executor: Executor = Executors.newSingleThreadExecutor()


//    val responseCallback: TelephonyManager.UssdResponseCallback = object : TelephonyManager.UssdResponseCallback() {

    @RequiresApi(Build.VERSION_CODES.S)


//    val callForwardingIndicatorListener : CallForwardingIndicatorListener = object : CallForwardingIndicatorListener


    private val phoneStateListener: PhoneStateListener =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            @RequiresApi(Build.VERSION_CODES.Q)
            object : PhoneStateListener(executor) {
                override fun onCallForwardingIndicatorChanged(cfi: Boolean) {
                    println(TAG + "onCallForwardingIndicatorChanged  CFI  Old=$cfi")
                    // Get the current state of unconditional call forwarding
                    currentState = cfi

                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
                        if (ActivityCompat.checkSelfPermission(
                                context!!,
                                Manifest.permission.READ_PHONE_STATE
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            super.onCallForwardingIndicatorChanged(cfi)
                        }
                    }
                }

            }
        } else {
            TODO("VERSION.SDK_INT < Q")
        }


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate() {
        super.onCreate()


        // Create the notification channel
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Call Forwarding Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val manager = getSystemService(
            NotificationManager::class.java
        )
        manager.createNotificationChannel(serviceChannel)

        // Create a notification for the foreground service

        // Create a notification for the foreground service
        val notification: Notification =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(resources.getString(R.string.call_forwarding_service))
                .setContentText(resources.getString(R.string.service_running))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()

        // Start the service as a foreground service

        // Start the service as a foreground service
        startForeground(1, notification)
        context = this

        println("calling in phone state")
        // Register MyPhoneStateListener as a phone state listener
        val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            registerCallForwardingIndicatorListener(telephonyManager)


        } else {
            telephonyManager.listen(
                phoneStateListener,
                PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR
            )

        }
    }


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onDestroy() {
        super.onDestroy()
        // Unregister MyPhoneStateListener as a phone state listener
        val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) telephonyManager.listen(
            phoneStateListener,
            PhoneStateListener.LISTEN_NONE
        )

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun registerCallForwardingIndicatorListener(telephonyManager: TelephonyManager) {
        // New API (API level 31 and above)
        val callback = TelephonyCallback()
        val callForwardingListener = CallForwardingIndicatorListener { cfi: Boolean ->
            println(TAG + "onCallForwardingIndicatorChanged  CFI New=$cfi")
            // Get the current state of unconditional call forwarding
            //Toast.makeText(context, "New", Toast.LENGTH_SHORT).show();
            this.currentState = cfi
//            // Create an Intent with the android.appwidget.action.APPWIDGET_UPDATE action
//            val intent = Intent(context, ForwardingStatusWidget::class.java)
//            intent.action = "callforwardingstatus.APPWIDGET_UPDATE_CFI"
//
//            // Add the app widget IDs as an extra
//            val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(
//                ComponentName(
//                    application,
//                    ForwardingStatusWidget::class.java
//                )
//            )
//            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
//
//            // Add the CFI value as an extra
//            intent.putExtra("cfi", this.currentState)
//
//            // Send the broadcast
//            sendBroadcast(intent)
        }
        // Register the TelephonyCallback
        //telephonyManager.registerTelephonyCallback(executor,callForwardingListener);
    }


}