package com.discut.manga.service.source

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import com.discut.manga.data.source.Extension

class ExtensionInstallerActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(Intent.ACTION_VIEW)
            .apply {
                setDataAndType(intent.data, intent.type)
                putExtra(Intent.EXTRA_RETURN_RESULT, true)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }
        try {
            startActivityForResult(intent, INSTALL_REQUEST_CODE)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != INSTALL_REQUEST_CODE) {
            return
        }
        val pkg = intent.extras!!.getString(ExtensionsInstaller.EXTRA_EXTENSION_PKG)
        if (pkg.isNullOrBlank()) {
            return
        }
        val extension = SourceManager.instance.allExtensionsFlow.value
            .filterIsInstance<Extension.RemoteExtension>()
            .find { it.pkg == pkg } ?: return
        when (resultCode) {
            RESULT_OK -> extension.onInstalled()
            RESULT_CANCELED -> extension.onCanceled()
            else -> {}
        }
        finish()
    }

    companion object {
        const val INSTALL_REQUEST_CODE = 0x100
    }
}