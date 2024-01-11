package manga.core.preference

import android.content.Context
import android.content.SharedPreferences
import discut.manga.common.res.R
import kotlinx.coroutines.flow.Flow
import manga.core.disk.DiskUtils
import manga.core.utils.getStringFromLocal
import java.io.File
import java.util.Locale


class DownloadPreference constructor(appPreference: SharedPreferences, flow: Flow<String?>) :
    AppPreference(appPreference, flow) {
    companion object {
        const val DOWNLOAD_DIRECTORY = "storage/emulated/0/Download/Manga"
    }

    fun getDownloadDirectory(context: Context): String {
        if (isExists("download_directory").not()) {
            val baseLocationFolder =
                "${
                    context.getStringFromLocal(
                        R.string.app_name,
                        Locale.ENGLISH
                    )
                }${File.separator}download"
            val first =
                DiskUtils.getExternalStorages(context)
                    .map { File(it.absolutePath, baseLocationFolder) }
                    .first()
            edit {
                putString("download_directory", first.absolutePath)
            }
        }
        return getValue(
            "download_directory",
            context.externalCacheDir?.absolutePath ?: DOWNLOAD_DIRECTORY
        )
    }

    fun setDownloadDirectory(path: String) {
        edit {
            putString("download_directory", path)
        }
    }

    fun isWifiOnly(): Boolean =
        getValue("enable_wifi_only", false)

    fun getIsWifiOnlyAsFlow(): Flow<Boolean> =
        getValueAsFlow("enable_wifi_only", false)


    fun setWifiOnly(enable: Boolean) {
        edit {
            putBoolean("enable_wifi_only", enable)
        }
    }
}