package com.discut.manga.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.discut.manga.ui.security.SecurityActivityDelegate
import com.discut.manga.ui.security.SecurityActivityDelegateImpl

abstract class BaseActivity : AppCompatActivity(),
    SecurityActivityDelegate by SecurityActivityDelegateImpl() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isAutoRegisterSecurityActivity()) {
            registerSecurityActivity(this)
        }
    }

    /**
     * Judge whether to automatically register the security activity
     */
    protected open fun isAutoRegisterSecurityActivity(): Boolean = true
}