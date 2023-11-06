package com.discut.manga.ui.security

import android.os.Bundle
import androidx.biometric.BiometricPrompt
import androidx.biometric.auth.AuthPromptCallback
import androidx.biometric.auth.startClass2BiometricOrCredentialAuthentication
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.discut.manga.ui.base.BaseActivity
import com.discut.manga.util.get
import com.discut.manga.util.setComposeContent
import manga.core.preference.PreferenceManager


class UnlockActivity : BaseActivity() {

    val securityPreference = PreferenceManager.get<SecurityPreference>()

    companion object {
        var isAuthorized = false
    }

    override fun isAutoRegisterSecurityActivity(): Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setComposeContent {
            UnlockScreen()
        }

        startClass2BiometricOrCredentialAuthentication(
            title = "解锁App",
            subtitle = "请使用您的指纹解锁",
            confirmationRequired = true,
            executor = ContextCompat.getMainExecutor(this),
            callback = object : AuthPromptCallback() {
                override fun onAuthenticationError(
                    activity: FragmentActivity?,
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    finishAffinity()
                }

                override fun onAuthenticationSucceeded(
                    activity: FragmentActivity?,
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    isAuthorized = true
                    finish()
                }
            },
        )
    }
}

@Composable
fun UnlockScreen() {
    Column(
        Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "",
            modifier = Modifier
                .size(48.dp)
                .align(alignment = Alignment.CenterHorizontally)
        )
        Text(
            text = "App已锁定",
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        )
    }

}

@Composable
@Preview
fun Preview() {
    MaterialTheme {
        UnlockScreen()
    }
}