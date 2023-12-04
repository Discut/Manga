package com.discut.manga.components.manga

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.discut.manga.components.domain.toMangaCoverInfo
import com.discut.manga.theme.MangaTheme
import com.discut.manga.theme.padding
import com.discut.manga.ui.manga.details.MangaDetails

@SuppressLint("InflateParams")
@Composable
fun MangaInfoBox(
    modifier: Modifier = Modifier,
    info: MangaDetails
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(vertical = MaterialTheme.padding.Normal),
    ) {

        MangaCover.BOOK(
            modifier = Modifier,
            info = info.toMangaCoverInfo()
        )

        MangaInfo(
            modifier = Modifier
                .weight(2.75f)
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
            title = info.title,
            author = info.author,
            artist = info.artist,
            source = info.source
        )
    }
}

@Composable
@Preview
fun MangaInfoBoxPreview() {
    MangaTheme {
        MangaInfoBox(
            info = MangaDetails(
                coverUrl = "https://pro-api.mgsearcher.com/_next/image?url=https%3A%2F%2Fcover1.baozimh.org%2Fcover%2Ftx%2Fzuijiangneijuanjitong%2F27_17_20_325503b799a59e545f85633395ed7f13_1651051230260.webp&w=640&q=50",
                title = "内卷系统",
                author = "作者",
                artist = "艺术家",
                source = "源",
                url = "url",
                description = "描述",
            )
        )
    }
}