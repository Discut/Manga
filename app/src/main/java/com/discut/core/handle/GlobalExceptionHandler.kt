package com.discut.core.handle

import android.content.Context
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import manga.core.network.interceptor.NetworkException
import java.net.SocketException

class GlobalExceptionHandler(
    private val defaultHandler: Thread.UncaughtExceptionHandler,
    private val context: Context
) : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        try {
            when (e) {
                is SocketException, is NetworkException -> {
                    Log.d("GlobalExceptionHandler", "SocketException")
                    //Snackbar.make(context = context,view = context, "网络异常", Snackbar.LENGTH_SHORT).show()
                    e.printStackTrace()
                }

                else -> {
                    Log.d("GlobalExceptionHandler", "Other Exception")
                    throw e
                }
            }
        } catch (e: Exception) {
            defaultHandler.uncaughtException(t, e)
        }
    }

    companion object {
        fun init(context: Context) {
            Thread.setDefaultUncaughtExceptionHandler(
                GlobalExceptionHandler(
                    Thread.getDefaultUncaughtExceptionHandler() as Thread.UncaughtExceptionHandler,
                    context
                )
            )
        }
    }
}