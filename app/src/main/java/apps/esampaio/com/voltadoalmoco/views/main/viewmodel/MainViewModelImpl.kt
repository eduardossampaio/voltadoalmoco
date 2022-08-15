package apps.esampaio.com.voltadoalmoco.views.main.viewmodel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import apps.esampaio.com.voltadoalmoco.services.workers.CountdownWorker
import apps.esampaio.com.voltadoalmoco.utils.extensions.checkIfRunning
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import java.util.*

class MainViewModelImpl(private val context: Context) : MainViewModel {
    override var onTimeUpdate: Subject<Int>? = PublishSubject.create()

    private val isWorkerRunning: Boolean
        get() {
            return WorkManager.getInstance(this.context).checkIfRunning(CountdownWorker.WORKER_TAG)
        }

    private var remainingMinutes: Int = 0
        set(value) {
            field = value
            this.onTimeUpdate?.onNext(value)
        }

    init {
        val intentFilter = IntentFilter("WORKER_RESULT")
        context.registerReceiver(RemainingMinutesBroadcastReceiver(),intentFilter);

    }
    override fun update() {
    }

    override fun start(initDate: Date) {
        if(isWorkerRunning){
            return
        }
        startWorker(initDate);
    }

    override fun stop() {
        if(!isWorkerRunning){
            return
        }
        stopWorker();
    }

    private fun notifyRemainingMinutes(minutes:Int){
      this.remainingMinutes = minutes;
    }

    private fun startWorker(initDate:Date) {
        val inputData = Data.Builder().putLong(CountdownWorker.INIT_TIME_PARAMETER,initDate.time).build()
        val uploadWorkRequest = OneTimeWorkRequestBuilder<CountdownWorker>()
            .addTag(CountdownWorker.WORKER_TAG)
            .setInputData(inputData)
            .build()

        WorkManager
            .getInstance(context)
            .enqueue(uploadWorkRequest)
    }
    private fun stopWorker(){
        WorkManager.getInstance(context).cancelAllWorkByTag(CountdownWorker.WORKER_TAG)
    }


    inner class RemainingMinutesBroadcastReceiver() : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val remainingMinutes = intent?.extras?.getInt("REMAINING_MINUTES")
            if(remainingMinutes != null){
                Log.d("RemainingMinutesBR", "faltam: $remainingMinutes")
                notifyRemainingMinutes(remainingMinutes)
            }
        }
    }
}