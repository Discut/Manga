package com.discut.manga.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.discut.manga.components.domain.MangaCoverInfo
import com.discut.manga.components.manga.MangaInfoBox

@Composable
fun HistoryScreen() {
    Row(
        modifier = Modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically  // 垂直居中
    ) {
        /*MangaInfoBox(
            info = MangaCoverInfo(
                coverUrl = "https://pro-api.mgsearcher.com/_next/image?url=https%3A%2F%2Fcover1.baozimh.org%2Fcover%2Ftx%2Fzuijiangneijuanjitong%2F27_17_20_325503b799a59e545f85633395ed7f13_1651051230260.webp&w=640&q=50",
                title = "内卷系统"
            )
        )*/
    }
}