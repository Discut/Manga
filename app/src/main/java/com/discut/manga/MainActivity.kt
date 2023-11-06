package com.discut.manga

import android.os.Bundle
import android.widget.Toast
import com.discut.manga.ui.base.BaseActivity
import com.discut.manga.ui.security.SecurityPreference
import com.discut.manga.util.get
import com.discut.manga.util.setComposeContent
import dagger.hilt.android.AndroidEntryPoint
import manga.core.preference.PreferenceManager

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var securityPreference: SecurityPreference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        securityPreference = PreferenceManager.get()
        Toast.makeText(this, securityPreference.useAuthenticator().toString(), Toast.LENGTH_LONG)
            .show()

        /*setComposeContent {

        }*/
    }
}