package apps.esampaio.com.voltadoalmoco

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.WorkManager

class CancelCountdownBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val WORKER_TAG = "apps.esampaio.com.voltadoalmoco.WORKERS.COUNTDOWN_WORKER"
        WorkManager.getInstance(context).cancelAllWorkByTag(WORKER_TAG)
    }
}