package apps.esampaio.com.voltadoalmoco.utils.extensions

import androidx.work.WorkInfo
import androidx.work.WorkManager

fun WorkManager.checkIfRunning(tag: String) : Boolean{
    val workInfosByTag = this.getWorkInfosByTag(tag)
    val workInfos = workInfosByTag.get();
    var running = false

    if (workInfos == null || workInfos.size === 0) return false

    for (workStatus in workInfos) {
        running =  workStatus.state == WorkInfo.State.RUNNING || workStatus.state == WorkInfo.State.ENQUEUED
        if(running){
            break;
        }
    }

    return running
}