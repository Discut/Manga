package com.discut.manga.preference

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import manga.source.preference.SourcePreferenceType
import me.zhanghai.compose.preference.textFieldPreference

fun LazyListScope.sourcePreferences(
    types: List<SourcePreferenceType>
) {
    types.forEach { pref ->
        when (pref) {
            is SourcePreferenceType.TextFiled -> {
                textFieldPreference(
                    key = pref.key,
                    defaultValue = pref.defaultValue,
                    title = {
                        Text(text = pref.title)
                    },
                    textToValue = {
                        it
                    },
                    summary = if (pref.summary != null) {
                        {
                            Text(text = pref.summary!!)
                        }
                    } else null,
                )
            }
        }
    }
}