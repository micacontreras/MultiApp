package com.example.serviceexam.maps

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay

class ProgressWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    companion object {
        const val Progress = "Progress"
        private const val delayDuration = 1L
    }

    override suspend fun doWork(): Result {
        val firstUpdate = workDataOf(Progress to 0)
        val lastUpdate = workDataOf(Progress to 100)
        setProgress(firstUpdate)
        delay(delayDuration)
        setProgress(lastUpdate)
        return Result.success()
    }
}

//Codigo que se deberia usar donde se vaya a invocar el worker

//Mostrar progreso del worker?
/*WorkManager.getInstance(applicationContext)
    .getWorkInfoByIdLiveData(periodicSyncDataWork)
    .observe(observer, Observer { workInfo: WorkInfo? ->
        if (workInfo != null) {
            val progress = workInfo.progress
            val value = progress.getInt(ProgressWorker.Progress, 0)
            // Do something with progress information
        }
    })*/