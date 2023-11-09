package com.discut.manga.ui.reader

import android.os.Bundle
import androidx.activity.viewModels
import com.discut.manga.R
import com.discut.manga.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReaderActivity : BaseActivity(){

    private val vm :ReaderActivityViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reader_layout)
    }
}