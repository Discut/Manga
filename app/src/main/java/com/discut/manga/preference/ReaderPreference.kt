package com.discut.manga.preference

import android.content.SharedPreferences
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.discut.manga.ui.reader.navigation.LAndRNavigation
import com.discut.manga.ui.reader.navigation.LShapeNavigation
import com.discut.manga.ui.reader.navigation.NoneNavigation
import discut.manga.common.res.R
import kotlinx.coroutines.flow.Flow
import manga.core.preference.AppPreference

class ReaderPreference(appPreference: SharedPreferences, flow: Flow<String?>) :
    AppPreference(appPreference, flow) {

    companion object {
        const val READER_MODE = "reader_mode"
        const val READER_BACKGROUND_COLOR = "reader_background_color"
        const val READER_SCREEN_ON = "reader_screen_on"
    }

    var readerMode
        get() =
            ReaderMode.entries.find { it.value == getValue(READER_MODE, 0) }
                ?: ReaderMode.WEBTOON
        set(value) {
            edit { putInt(READER_MODE, value.value) }
        }

    var screenOn
        get() = getValue(READER_SCREEN_ON, true)
        set(value) {
            edit { putBoolean(READER_SCREEN_ON, value) }
        }

    var backgroundColor
        get() = ReaderBackgroundColor.entries.find {
            it.value == getValue(
                READER_BACKGROUND_COLOR,
                0
            )
        }
            ?: ReaderBackgroundColor.BLACK
        set(value) {
            edit { putInt(READER_BACKGROUND_COLOR, value.value) }
        }
}

enum class ReaderMode(
    val value: Int,
    @StringRes val stringRes: Int,
    @DrawableRes val iconRes: Int,
) {
    WEBTOON(
        0,
        R.string.reader_mode_webtoon,
        com.discut.manga.R.drawable.ic_reader_mode_webtoon_24
    ),
    LEFT_TO_RIGHT(
        1,
        R.string.reader_mode_left_to_right,
        com.discut.manga.R.drawable.ic_reader_mode_ltr_24
    )
}

enum class ReaderBackgroundColor(
    val value: Int,
    @StringRes val stringRes: Int,
) {
    BLACK(
        0,
        R.string.reader_background_color_black,
    ),
    WHITE(
        1,
        R.string.reader_background_color_white,
    ),
    GRAY(
        2,
        R.string.reader_background_color_gray,
    )
}

val clickNavigationList = listOf(
    LAndRNavigation(),
    LShapeNavigation(),
    NoneNavigation(),
)