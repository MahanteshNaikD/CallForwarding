package com.example.callforward

import android.app.TimePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.callforward.Database.NumberEntity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.callforward.Database.viewModel.NumberViewModel
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NumberShownAdapter(private val viewModel: NumberViewModel, val context: Context) :
    RecyclerView.Adapter<NumberShownAdapter.ViewHolder>() {


    var numberList: List<NumberEntity> = emptyList()

    var mainActivity: MainActivity? = null

    var mHour: Int = 0
    var mMinute: Int = 0

    var workInfo: WorkInfo? = null

    lateinit var mOneTimeWorkRequest: OneTimeWorkRequest;


    companion object {
        var numberObject: NumberEntity? = null
        var timeHourText = ""
        var timeMinuteText = ""
    }


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val numberTextView = ItemView.findViewById<TextView>(R.id.number_shown_text_view)
        val saveButton = ItemView.findViewById<Button>(R.id.number_change_button)
        val timeButton = ItemView.findViewById<Button>(R.id.time_shown_text_view)
        val statusText = ItemView.findViewById<TextView>(R.id.status_shown_text_view)
        val buttonDelete = ItemView.findViewById<Button>(R.id.delete_number_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mainActivity = MainActivity()
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycleviewfornumber, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return numberList?.size!!
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val numberModel = numberList?.get(position)

        holder.numberTextView.text = numberModel?.number

        if (numberModel?.time != null) {

            holder.timeButton.text = numberModel?.time
        } else {
            holder.timeButton.text = "00:00"
            holder.timeButton.setBackgroundColor(context.resources.getColor(R.color.gray))
        }

        if (numberModel?.status != null) {
            var timeandmin = numberModel?.time?.split(":")
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, timeandmin?.get(0)?.toInt() ?: 0)
            calendar.set(Calendar.MINUTE, timeandmin?.get(1)?.toInt() ?: 0)
            calendar.set(Calendar.SECOND, 0)

            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                holder.statusText.text = numberModel?.status
                holder.timeButton.setBackgroundColor(context.resources.getColor(R.color.red))
            } else {
                holder.statusText.text = numberModel?.status
            }
        } else {
            holder.statusText.text = "INACTIVE"
        }



        holder.saveButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, timeHourText.toInt())
            calendar.set(Calendar.MINUTE, timeMinuteText.toInt())
            calendar.set(Calendar.SECOND, 0)

            val constraints = Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.CONNECTED) // Requires an active network connection
                .setRequiresCharging(false) // Doesn't require the device to be charging
                .setRequiresBatteryNotLow(false) // Doesn't require the battery to be not low
                .setRequiresStorageNotLow(false) // Doesn't require storage to be not low
                .setRequiresDeviceIdle(false)
                .build();

            val inputData = Data.Builder()
                .putString("number", numberModel?.number)
                .putString("simId", numberModel?.selectedSimId)
                .putString("id", numberModel?.id.toString())
                .build()

            var timeStamp = calendar.timeInMillis.minus(System.currentTimeMillis())

            mOneTimeWorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
                .setConstraints(constraints)
                .addTag("oneTimeRequestWorker")
                .setInputData(inputData)
                .setInitialDelay(timeStamp!!, TimeUnit.MILLISECONDS)
                .build()


            if (numberModel != null) {

                if (calendar.timeInMillis > System.currentTimeMillis()) {
                    numberModel.status = "ACTIVE"
                    viewModel.update(numberModel)
                    holder.timeButton.setBackgroundColor(context.resources.getColor(R.color.green))
                    WorkManager.getInstance(context).enqueue(mOneTimeWorkRequest);
                    Toast.makeText(
                        context,
                        "Added Timer for Change Call Forward",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "Time Is Not Valid",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }




        holder.timeButton.setOnClickListener {
            if (numberModel != null) {
                val calender = Calendar.getInstance()
                mHour = calender.get(Calendar.HOUR_OF_DAY)
                mMinute = calender.get(Calendar.MINUTE)
                val timePickerDialog = TimePickerDialog(
                    context,
                    { view, hourOfDay, minute ->
                        run {
                            timeHourText = "$hourOfDay"
                            timeMinuteText = "$minute"
                            numberModel.time = "$hourOfDay:$minute"
                            viewModel.update(numberModel)
                        }
                    },
                    mHour,
                    mMinute,
                    false
                )
                timePickerDialog.show()

            }
        }




        holder.buttonDelete.setOnClickListener {
            if (numberModel?.status.equals("ACTIVE")) {

                val id =
                    WorkManager.getInstance(context).getWorkInfosByTag("oneTimeRequestWorker").get()

                WorkManager.getInstance(context).cancelWorkById(id[position].id)
                Toast.makeText(context, "Work Canceled", Toast.LENGTH_SHORT).show()

                if (numberModel != null) {
                    viewModel.delete(numberModel)
                }
            } else {
                if (numberModel != null) {
                    viewModel.delete(numberModel)
                    Toast.makeText(context, "Number Deleted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}