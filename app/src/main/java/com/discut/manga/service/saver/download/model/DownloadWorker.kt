package com.discut.manga.service.saver.download.model

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.asFlow
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.discut.manga.service.saver.download.DownloadProvider
import com.discut.manga.service.saver.download.instance
import com.discut.manga.ui.util.NetworkState
import com.discut.manga.ui.util.activeNetworkState
import com.discut.manga.ui.util.networkStateFlow
import com.discut.manga.util.get
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import manga.core.preference.DownloadPreference
import manga.core.preference.PreferenceManager

class DownloadWorker(
    context: Context, workerParams: WorkerParameters,
) :
    CoroutineWorker(context, workerParams) {

    private val downloadPreference = PreferenceManager.get<DownloadPreference>()
    private val downloadProvider = DownloadProvider.instance
    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = NotificationCompat.Builder(
            applicationContext,
            createNotificationChannel(DownloadScope.CHANNEL_NAME, "Download")
        ).apply {
            setContentTitle("Download")
            setSmallIcon(android.R.drawable.stat_sys_download)
        }.build()
        return ForegroundInfo(
            DownloadScope.CHANNEL_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
        )
    }

    /**
     * 创建通知通道
     * @param channelId
     * @param channelName
     * @return
     */
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = applicationContext.getSystemService(NotificationManager::class.java)
        service.createNotificationChannel(chan)
        return channelId
    }

    override suspend fun doWork(): Result {
        val scopeKey = inputData.getString(SCOPE_KEY) ?: return Result.failure()
        var checkNetworkState = checkNetworkState(
            applicationContext.activeNetworkState(),
            downloadPreference.isWifiOnly(),
            scopeKey
        )
        var active = checkNetworkState && downloadProvider.launchDownloadScope(scopeKey)
        if (active.not()) {
            return Result.failure()
        }

        try {
            setForeground(getForegroundInfo())
            delay(500)
        } catch (e: IllegalStateException) {
            Log.e("DownloadScope", e.message.toString())
            return Result.failure()
        }
        coroutineScope {
            combineTransform(
                applicationContext.networkStateFlow(),
                downloadPreference.getIsWifiOnlyAsFlow(),
                transform = { a, b -> emit(checkNetworkState(a, b, scopeKey)) },
            )
                .onEach { checkNetworkState = it }
                .launchIn(this)
        }
        // Keep the worker running when needed
        while (active) {
            active = !isStopped /*&& downloadManager.isRunning*/ && checkNetworkState
        }
        return Result.success()
    }

    companion object {

        const val TAG = "DownloadWorker"
        const val SCOPE_KEY = "scopeKey"

        private fun getWorkerTag(scopeTag: String): String = "$TAG-$scopeTag"
        fun bootScope(context: Context, downloadScope: DownloadScope) {
            val data = Data.Builder().putString(SCOPE_KEY, downloadScope.scopeTag).build()
            val workerTag = getWorkerTag(downloadScope.scopeTag)
            val oneTimeWorkRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
                .setInputData(data)
                .addTag(workerTag)
                .build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    workerTag,
                    ExistingWorkPolicy.REPLACE,
                    oneTimeWorkRequest
                )
        }

        fun stopScope(context: Context, downloadScope: DownloadScope) {
            val workerTag = getWorkerTag(downloadScope.scopeTag)
            downloadScope.pauseAll()
            WorkManager.getInstance(context)
                .cancelUniqueWork(workerTag)
        }

        fun isScopeRunning(context: Context, downloadScope: DownloadScope): Boolean {
            val workerTag = getWorkerTag(downloadScope.scopeTag)
            return WorkManager.getInstance(context)
                .getWorkInfosForUniqueWork(workerTag)
                .get()
                .let { list -> list.count { it.state == WorkInfo.State.RUNNING } == 1 }
        }

        fun isScopeRunningFlow(context: Context, downloadScope: DownloadScope): Flow<Boolean> {
            val workerTag = getWorkerTag(downloadScope.scopeTag)
            return WorkManager.getInstance(context)
                .getWorkInfosForUniqueWorkLiveData(workerTag)
                .asFlow()
                .map { list -> list.count { it.state == WorkInfo.State.RUNNING } == 1 }
        }
    }

    private fun checkNetworkState(
        state: NetworkState,
        requireWifi: Boolean,
        scopeKey: String
    ): Boolean {
        return if (state.isOnline) {
            val noWifi = requireWifi && !state.isWifi
            if (noWifi) {
                downloadProvider.sendEvent {
                    DownloadEvent.DownloadScopeStop(scopeKey)
                }
            }
            !noWifi
        } else {
            downloadProvider.sendEvent {
                DownloadEvent.DownloadScopeStop(scopeKey)
            }
            false
        }
    }
}