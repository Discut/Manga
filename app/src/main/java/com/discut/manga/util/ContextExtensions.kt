package com.discut.manga.util

import android.content.Context
import android.widget.Toast

fun Context.getString(id: Int) = resources.getString(id)

fun Context.toast(
    msg: String?,
    duration: Int = Toast.LENGTH_SHORT
) = Toast.makeText(this, msg.orEmpty(), duration).show()