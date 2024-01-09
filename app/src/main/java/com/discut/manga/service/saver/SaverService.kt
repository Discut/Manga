package com.discut.manga.service.saver

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.discut.manga.service.saver.download.DownloadProvider
import com.discut.manga.service.saver.export.ExportManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SaverService : Service() {

    @Inject
    lateinit var downloadProvider: DownloadProvider

    @Inject
    lateinit var exportManager: ExportManager
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SaverService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }
    }

}