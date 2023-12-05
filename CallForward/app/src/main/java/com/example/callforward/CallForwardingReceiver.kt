package com.example.callforward

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.telephony.TelephonyManager.UssdResponseCallback
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

class CallForwardingReceiver : BroadcastReceiver() {


    var sharedPreferences: SharedPreferences? = null


    companion object {
        var simNumberId = "";
    }


    val TAG: String = "CallForwardingReceiver"

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onReceive(context: Context, intent: Intent) {
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        println("TOGGLE_CALL_FORWARDING" + intent.extras)





        if ("ADDING_SIM_ID".equals(intent.action)) {
            println("sim id")
            CallForwardingReceiver.simNumberId = intent.getStringExtra("SIM_ID").toString();

        }

        if ("callforwardingstatus.TOGGLE_CALL_FORWARDING_FROM_WORKMANAGER".equals(intent.action)) {



//            val numId:Int =  Integer.parseInt(simIdNumber)


//            if( numId!==null){
//                setCallForwading(
//                    context,
//                    PhoneStateService().currentState,
//                    intent.getStringExtra("number").toString(),
//                    numId
//                )
//            }else{
//                setCallForwading(
//                    context,
//                    PhoneStateService().currentState,
//                    intent.getStringExtra("number").toString(),
//                   1
//                )
//            }


            println("from work manager" + intent.getStringExtra("number"))
            println("from work manager" + intent.getStringExtra("simId"))

            val simIdNumber = intent.getStringExtra("simId")

            if (intent.extras != null) {
                setCallForwading(
                    context,
                    PhoneStateService().currentState,
                    intent.getStringExtra("number").toString(),
                    simIdNumber?.toInt()
                )
            }
        }

        if ("callforwardingstatus.TOGGLE_CALL_FORWARDING".equals(intent.action)) {
            setCallForwading(
                context,
                PhoneStateService().currentState,
                intent.getStringExtra("number").toString(),
                simNumberId.toInt()
            )
            println("TOGGLE_CALL_FORWARDING")
        }


    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun setCallForwading(context: Context, currentState: Boolean, s: String, simId: Int?) {
        println("call foraed broad cast" + currentState.toString())
        println("call foraed broad cast" + s)

        val manager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager


        val telephoneyManger = context.getSystemService(TelephonyManager::class.java);
        val subscriptionManager = context.getSystemService(SubscriptionManager::class.java)
//        val subscriptionManager = SubscriptionManager.from(context)
        println("id isss" + telephoneyManger.subscriptionId)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_NUMBERS
            ) == PackageManager.PERMISSION_GRANTED
        ) {

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            var defaultSubId = -1

            if (simId != null) {
                if (simId <= 0) {
                    defaultSubId = SubscriptionManager.getDefaultSubscriptionId();
                } else {
                    if (simId != null) {
                        defaultSubId = simId.toInt()
                    };
                }
            }


            val activeSubscriptions = subscriptionManager.activeSubscriptionInfoList

            println("activeSubscriptions" + activeSubscriptions)
            if (activeSubscriptions != null && !activeSubscriptions.isEmpty()) {
                // Choose the first active subscription
                val subscriptionInfo = activeSubscriptions[0]
                // defaultSubId = subscriptionInfo.subscriptionId
            }

            println(TAG + defaultSubId.toString())

            val h = Handler();


            val responseCallback: UssdResponseCallback = object : UssdResponseCallback() {
                override fun onReceiveUssdResponse(
                    telephonyManager: TelephonyManager,
                    request: String,
                    response: CharSequence
                ) {
                    super.onReceiveUssdResponse(telephonyManager, request, response)
                    Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show()
                    println("onReceiveUssdResponse" + response.toString())


                }

                override fun onReceiveUssdResponseFailed(
                    telephonyManager: TelephonyManager,
                    request: String,
                    failureCode: Int
                ) {
                    super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode)
                    Toast.makeText(
                        context,
                        "CallForwading number chnage failed",
                        Toast.LENGTH_SHORT
                    ).show()
                    println("onReceiveUssdResponseFailed" + failureCode.toString())


                    when (failureCode) {
                        TelephonyManager.USSD_RETURN_FAILURE -> println("USSD_RETURN_FAILURE")

                        else -> println("USSD_RETURN_FAILURE")

                    }

                }
            }


            var manager1: TelephonyManager


            if (!currentState) {
                val ussdRequest: String = "*21*$s#";



                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CALL_PHONE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {

                    // Set the subscription ID for call forwarding
                    manager1 = manager.createForSubscriptionId(defaultSubId)

                    manager1.sendUssdRequest(ussdRequest, responseCallback, h);
                }
                println("ussed resuest" + ussdRequest);


            } else {
                println("fghyuiop")
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CALL_PHONE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {

                    // Set the subscription ID for call forwarding
                    manager1 = manager.createForSubscriptionId(defaultSubId)

                    manager1.sendUssdRequest("#21#", responseCallback, h)
                }
                // Set the subscription ID for call forwarding


            }


        } else {
            var telemnager = context.getSystemService(TelephonyManager::class.java);

            var id = telemnager.subscriptionId;

            subscriptionManager.activeSubscriptionInfoList


            val resposne: UssdResponseCallback = object : UssdResponseCallback() {
                override fun onReceiveUssdResponse(
                    telephonyManager: TelephonyManager?,
                    request: String?,
                    response: CharSequence?
                ) {
                    super.onReceiveUssdResponse(telephonyManager, request, response)
                    Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show()
                    println("onReceiveUssdResponse" + response.toString())
                }


                override fun onReceiveUssdResponseFailed(
                    telephonyManager: TelephonyManager?,
                    request: String?,
                    failureCode: Int
                ) {
                    super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode)

                    Toast.makeText(
                        context,
                        "CallForwading number chnage failed",
                        Toast.LENGTH_SHORT
                    ).show()
                    println("onReceiveUssdResponseFailed" + failureCode.toString())


                    when (failureCode) {
                        TelephonyManager.USSD_RETURN_FAILURE -> println("USSD_RETURN_FAILURE")

                        else -> println("USSD_RETURN_FAILURE")

                    }
                }
            }

            val hh = Handler()


            var manager1: TelephonyManager


            if (!currentState) {
                val ussdRequest: String = "*21*$s#";



                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CALL_PHONE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {

                    // Set the subscription ID for call forwarding
                    manager1 = manager.createForSubscriptionId(id)

                    manager1.sendUssdRequest(ussdRequest, resposne, hh);
                }
                println("ussed resuest" + ussdRequest);


            } else {

                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CALL_PHONE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {

                    // Set the subscription ID for call forwarding
                    manager1 = manager.createForSubscriptionId(id)

                    manager1.sendUssdRequest("#21#", resposne, hh)
                }
                // Set the subscription ID for call forwarding


            }


        }


    }


}