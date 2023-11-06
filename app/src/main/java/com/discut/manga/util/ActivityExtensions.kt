package com.discut.manga.util

import android.app.Activity
import android.content.Intent

fun <T : Activity> Activity.startToActivity(cls: Class<T>) {
    startActivity(Intent(this, cls))
}