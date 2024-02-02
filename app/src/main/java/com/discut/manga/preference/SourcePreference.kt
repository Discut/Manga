package com.discut.manga.preference

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import manga.source.ConfigurationSource
import manga.source.preference.SourcePreferenceType
import me.zhanghai.compose.preference.listPreference
import me.zhanghai.compose.preference.switchPreference
import me.zhanghai.compose.preference.textFieldPreference

fun LazyListScope.sourcePreferences(
    source: ConfigurationSource
) {
    val types = source.getSourcePreferences()
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
                    summary = if (pref.summaryBuilder != null) {
                        {
                            val preferenceValue by
                            source.getPreferenceStateFlow(
                                key = pref.key,
                                default = pref.defaultValue
                            )
                                .collectAsStateWithLifecycle()
                            val summary by remember {
                                derivedStateOf {
                                    pref.summaryBuilder!!.invoke(mapOf(pref.key to preferenceValue))
                                }
                            }
                            Text(text = summary)
                        }
                    } else null,
                )
            }

            is SourcePreferenceType.ListSelect -> {
                listPreference(
                    key = pref.key,
                    defaultValue = pref.defaultValue,
                    title = {
                        Text(text = pref.title)
                    },
                    values = pref.values,
                    summary = if (pref.summaryBuilder != null) {
                        {
                            val preferenceValue by
                            source.getPreferenceStateFlow(
                                key = pref.key,
                                default = pref.defaultValue
                            )
                                .collectAsStateWithLifecycle()
                            val summary by remember {
                                derivedStateOf {
                                    pref.summaryBuilder!!.invoke(mapOf(pref.key to preferenceValue))
                                }
                            }
                            Text(text = summary)
                        }
                    } else null,
                )
            }

            is SourcePreferenceType.Switch -> {
                switchPreference(
                    key = pref.key,
                    defaultValue = pref.defaultValue,
                    title = {
                        Text(text = pref.title)
                    },
                    summary = if (pref.summaryBuilder != null) {
                        {
                            val preferenceValue by
                            source.getPreferenceStateFlow(
                                key = pref.key,
                                default = pref.defaultValue
                            )
                                .collectAsStateWithLifecycle()
                            val summary by remember {
                                derivedStateOf {
                                    pref.summaryBuilder!!.invoke(mapOf(pref.key to preferenceValue))
                                }
                            }
                            Text(text = summary)
                        }
                    } else null,
                )
            }
        }
    }
}

@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun <T : Any> ConfigurationSource.getPreferenceStateFlow(
    key: String,
    default: T
): StateFlow<T> {
    val state by remember {
        mutableStateOf(MutableStateFlow(getPreferences().get(key, default)))
    }
    val scope = rememberCoroutineScope()
    val listener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, innerKey ->
            if (key == innerKey) {
                state.update {
                    sharedPreferences.get(key, default)
                }
            }
        }
    LaunchedEffect(Unit) {
        getPreferences()
            .registerOnSharedPreferenceChangeListener(listener)
    }
    DisposableEffect(Unit) {
        onDispose {
            getPreferences()
                .unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
    return state.stateIn(scope, SharingStarted.Eagerly, default)
}

@Suppress("UNCHECKED_CAST")
private fun <T> SharedPreferences.get(key: String, default: T): T {
    return when (default) {
        is Boolean -> getBoolean(key, default) as T
        is Int -> getInt(key, default) as T
        is Long -> getLong(key, default) as T
        is Float -> getFloat(key, default) as T
        is String -> getString(key, default)!! as T
        is Set<*> -> getStringSet(key, default as Set<String>)!! as T
        else -> throw IllegalArgumentException("Unsupported type for default $default")
    }
}