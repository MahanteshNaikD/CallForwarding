package com.example.callforward

import android.Manifest
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.callforward.Database.AppApplication
import com.example.callforward.Database.NumberEntity
import com.example.callforward.Database.viewModel.NumberViewModel
import com.example.callforward.Database.viewModel.NumberViewModelFactory
import java.util.Calendar
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private var read_phone_number = 102;

    private var REQUIRED_PERMISSIONS = arrayOf(
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.READ_PHONE_NUMBERS,
        android.Manifest.permission.CALL_PHONE,
        android.Manifest.permission.PROCESS_OUTGOING_CALLS,
        android.Manifest.permission.FOREGROUND_SERVICE
    )


    companion object {
        var selcet_sim_id = ""
        var timeMinute = ""
        var timeHour = ""
    }

    lateinit var mPeriodicWorkRequest: PeriodicWorkRequest;

    lateinit var mOneTimeWorkRequest: OneTimeWorkRequest;

    lateinit var context: Context;

    lateinit var activity: Activity;

    lateinit var editTextNumber: EditText;

    lateinit var button: Button

    lateinit var buttonSaveNumber: Button;

    lateinit var selectSimButton: Button;

    lateinit var addTimerButton: Button;

    lateinit var workCacelButton: Button;

    lateinit var deactivateCallForwaridng: Button

    lateinit var phoneStateService: PhoneStateService;

    lateinit var addDateButton: Button

    lateinit var addWorkerButton: Button


    var mHour: Int = 0

    var mMinute: Int = 0

    private lateinit var numberShownAdapter: NumberShownAdapter

    private var mRecycleView: RecyclerView? = null


    val viewModel: NumberViewModel by viewModels {
        NumberViewModelFactory((application as AppApplication).repository)
    }


    @RequiresApi(Build.VERSION_CODES.O)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermissions()

        phoneStateService = PhoneStateService();

        numberShownAdapter = NumberShownAdapter(viewModel, this);


        context = this
        activity = this


        button = findViewById(R.id.buttonClick)

        selectSimButton = findViewById(R.id.multisim_button);

        editTextNumber = findViewById(R.id.edit_text);

        buttonSaveNumber = findViewById(R.id.buttonSaveNumber);

        addTimerButton = findViewById(R.id.buttonClickforTime)

        workCacelButton = findViewById(R.id.workCancelButton);


        deactivateCallForwaridng = findViewById(R.id.deactivate_call_forwading);

        addDateButton = findViewById(R.id.buttonClickforDateSelect)

        addWorkerButton = findViewById(R.id.buttonClickforWorker)

        mRecycleView = findViewById(R.id.recylecViewForNumber);


        val telephoneyManger = getSystemService(TelephonyManager::class.java);


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_NUMBERS
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            askForPermission()
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            println("id isss" + telephoneyManger.subscriptionId)
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            println("number"+subscriptionManager.getPhoneNumber(telephoneyManger.subscriptionId))
//        }

//        val forwardingNumber = "7576934345"

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            simNumber1.text = "Your Mobile Number"+subscriptionManager.getPhoneNumber(telephoneyManger.subscriptionId)
////            simNumber1.text = subscriptionManager.getPhoneNumber(telephoneyManger.subscriptionId)
//        }
        //simNumber2.text = editTextNumber.text


//        simNumber2.text = forwardingNumber

        button.setOnClickListener {
            val callIntent = Intent("callforwardingstatus.TOGGLE_CALL_FORWARDING");
            callIntent.setClass(applicationContext, CallForwardingReceiver::class.java);
            callIntent.putExtra("cfi", phoneStateService.currentState);
            println("button clicked")
            callIntent.putExtra("number", editTextNumber.text.toString())
            context.sendBroadcast(callIntent);

            editTextNumber.text = null
        }

        selectSimButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                showSimSelectionPopup(context)
            };
        }


        mRecycleView.let { mRecycleView ->
            mRecycleView?.setHasFixedSize(true)
            mRecycleView?.layoutManager = LinearLayoutManager(applicationContext)
            mRecycleView?.setAdapter(numberShownAdapter)
        }


        viewModel.getAllNumberData.observe(this) { values->
//            numberShownAdapter.numberList = values as List<NumberEntity>?
            updateList(values as List<NumberEntity>)
        }


//        numberShownAdapter.numberList = viewModel.getAllNumberData

        buttonSaveNumber.setOnClickListener {

            println("sslectred" + selcet_sim_id)

            if (editTextNumber.text.isNotEmpty() && editTextNumber.text.length ==10 && selcet_sim_id != ""){
                viewModel.addNumber(
                    NumberEntity(
                        number = editTextNumber.text.toString(),
                        selectedSimId = selcet_sim_id
                    )
                )


                Toast.makeText(applicationContext, "Number Saved", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(applicationContext, "Number Should be Correct and Select Sim", Toast.LENGTH_SHORT).show()
            }

            editTextNumber.text = null
        }

//        viewModel.workProgress.observe(this){workInfo->
//            numberShownAdapter.workInfo = workInfo
//            numberShownAdapter.notifyDataSetChanged()
//        }
//
//        viewModel.startWork(context)


        addTimerButton.setOnClickListener {
            val calender = Calendar.getInstance()
            mHour = calender.get(Calendar.HOUR_OF_DAY)
            mMinute = calender.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(
                this,
                { view, hourOfDay, minute ->
                    run {
                        timeMinute = "$minute"
                        timeMinute = "$hourOfDay"
//                        timeString = "$hourOfDay:$minute"
                        println("$hourOfDay:$minute")
                    }
                },
                mHour,
                mMinute,
                false
            )
            timePickerDialog.show()
        }


//        addDateButton.setOnClickListener {
//            val calender = Calendar.getInstance()
//            mYear = calender.get(Calendar.YEAR)
//            mMonth = calender.get(Calendar.MONTH)
//            mDay = calender.get(Calendar.DAY_OF_MONTH)
//            val datePickerDialog = DatePickerDialog(this,
//                { view, year, monthOfYear, dayOfMonth ->
//                    run {
//                        dateString = year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString()
//                        println(dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
//                    }
//                },
//                mYear,
//                mMonth,
//                mDay
//            )
//            datePickerDialog.show()
//        }

        addWorkerButton.setOnClickListener {


            val calendar = Calendar.getInstance()

            // Set the desired time
            calendar.set(Calendar.HOUR_OF_DAY, timeHour.toInt())
            calendar.set(Calendar.MINUTE, timeMinute.toInt())
            calendar.set(Calendar.SECOND, 0)



            val constraints = Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.CONNECTED) // Requires an active network connection
                .setRequiresCharging(false) // Doesn't require the device to be charging
                .setRequiresBatteryNotLow(false) // Doesn't require the battery to be not low
                .setRequiresStorageNotLow(false) // Doesn't require storage to be not low
                .setRequiresDeviceIdle(false)
                .build();
//

            val inputData = Data.Builder()
                .putString("number", NumberShownAdapter.numberObject?.number)
                .putString("simId", NumberShownAdapter.numberObject?.selectedSimId)
                .build()
//
//          mPeriodicWorkRequest =
//                PeriodicWorkRequest.Builder(MyWorker::class.java, 15, TimeUnit.MINUTES)
//                    .addTag("periodicWorkRequest")
//                    .setConstraints(constraints)
//                    .setInputData(inputData)
//                    .build();

            var timeStamp = calendar.timeInMillis.minus(System.currentTimeMillis())

            println("timestamp" + timeStamp)

            mOneTimeWorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
                .setConstraints(constraints)
                .addTag("oneTimeRequestWorker")
                .setInputData(inputData)
                .setInitialDelay(timeStamp!!, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueue(mOneTimeWorkRequest);
            Toast.makeText(
                applicationContext,
                "Added Timer for Change Call Forward",
                Toast.LENGTH_LONG
            ).show()
        }

        workCacelButton.setOnClickListener {
            WorkManager.getInstance(applicationContext).cancelAllWorkByTag("oneTimeRequestWorker")
            Toast.makeText(applicationContext, "All Work Canceled", Toast.LENGTH_LONG).show()

            val workTag = "oneTimeRequestWorker"

            val workInfosByTagLiveData =
                WorkManager.getInstance(context).getWorkInfosByTagLiveData(workTag)

            workInfosByTagLiveData.observe(this, Observer { workInfos ->
                for (workInfo in workInfos) {
                    if (workInfo != null) {
                        when (workInfo.state) {
                            WorkInfo.State.ENQUEUED -> {
                                println("work ENQUEUED")
                            }

                            WorkInfo.State.RUNNING -> {
                                println("work runing")
                            }

                            WorkInfo.State.SUCCEEDED -> {
                                println("wprk success")
                            }

                            WorkInfo.State.FAILED -> {
                                println("work cancel")
                            }

                            WorkInfo.State.CANCELLED -> {
                                println("work cancel")
                            }

                            else -> {
                                println("work error")
                            }
                        }
                    }
                }
            })
        }



        deactivateCallForwaridng.setOnClickListener {
            val deactivationCode = Uri.fromParts("tel", "##21#", "#")

            val intent = Intent(Intent.ACTION_CALL, deactivationCode)
            context.startActivity(intent)
        }


    }


    override fun onDestroy() {
        super.onDestroy()
        viewModel.workProgress.removeObservers(this)
    }


    override fun onResume() {
        super.onResume()
        numberShownAdapter.notifyDataSetChanged()
    }

    private fun updateList(values: List<NumberEntity>) {
        numberShownAdapter.numberList = values
        numberShownAdapter.notifyDataSetChanged()
    }


    @RequiresApi(Build.VERSION_CODES.P)
    public fun showSimSelectionPopup(context: Context) {
        val subscriptionManager = getSystemService(SubscriptionManager::class.java)

        if (subscriptionManager != null) {

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED
            ) {

            }
            val subscriptionList: List<SubscriptionInfo> =
                subscriptionManager.activeSubscriptionInfoList

            println("subscriptionList" + subscriptionList)
            if (subscriptionList != null && !subscriptionList.isEmpty()) {
                val dialog = SimSelectionDialog(context, subscriptionList)
                dialog.show()
            }
        }
    }

    class SimSelectionDialog(
        context: Context?,
        private val subscriptionList: List<SubscriptionInfo>
    ) :
        androidx.appcompat.app.AlertDialog(context!!) {
        private var listView: ListView? = null
        private var adapter: ArrayAdapter<String>? = null

        init {
            init()
        }

        private fun init() {
            val context = context
            listView = ListView(context)
            adapter = ArrayAdapter(context, android.R.layout.simple_list_item_single_choice)
            for (subscriptionInfo in subscriptionList) {
                val subscriptionId = subscriptionInfo.subscriptionId
                val displayName = subscriptionInfo.displayName.toString()
                val simInfo = "SIM $subscriptionId: \t $displayName"
                adapter!!.add(simInfo)
            }
            listView!!.adapter = adapter
            listView!!.choiceMode = AbsListView.CHOICE_MODE_SINGLE
            setView(listView)
            setButton(BUTTON_POSITIVE, "OK") { dialog: DialogInterface, which: Int ->
                val selectedItemPosition = listView!!.checkedItemPosition
                if (selectedItemPosition != ListView.INVALID_POSITION) {
                    val selectedSubscription =
                        subscriptionList[selectedItemPosition]
                    val selectedSimId = selectedSubscription.subscriptionId


                    selcet_sim_id = selectedSimId.toString()

                    println("selectedSimId" + selcet_sim_id)


                    val simData = Intent("ADDING_SIM_ID");
                    simData.setClass(getContext(), CallForwardingReceiver::class.java);
                    simData.putExtra("SIM_ID", selectedSimId.toString());
                    context.sendBroadcast(simData);



                    Toast.makeText(context, "Selected SIM ID: $selectedSimId", Toast.LENGTH_SHORT)
                        .show()
//                    updateMultiSimTxt()
                }
                dialog.dismiss()
            }
            setButton(
                BUTTON_NEGATIVE, "Cancel"
            ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == read_phone_number && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //askForPermission();
            } else {
                println("permission denied" + requestCode)
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun askForPermission() {


        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.READ_PHONE_NUMBERS
            ),
            read_phone_number
        )

    }


    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
                println("persmission" + it.key + "   " + it.value)

            }
            if (!permissionGranted) {
                Toast.makeText(
                    baseContext,
                    "Permission request denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


}