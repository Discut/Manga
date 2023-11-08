package com.discut.manga.ui.main

import android.os.Bundle
import com.discut.manga.ui.base.BaseActivity
import com.discut.manga.util.setComposeContent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    @Inject
    lateinit var mainScreen: MainScreen
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setComposeContent {
            mainScreen.Content()
        }
    }
}