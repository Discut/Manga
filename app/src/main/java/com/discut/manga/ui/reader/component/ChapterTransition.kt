package com.discut.manga.ui.reader.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discut.manga.theme.padding
import com.discut.manga.ui.reader.viewer.domain.ReaderPage
import com.discut.manga.ui.util.isNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChapterTransition(
    chapterTransition: ReaderPage.ChapterTransition
) {
    ProvideTextStyle(
        MaterialTheme.typography.bodyMedium.copy(
            fontSize = 24.sp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(372.dp)
                .padding(horizontal = MaterialTheme.padding.Normal),
            verticalArrangement = Arrangement.Center
        ) {
            if (chapterTransition.prevChapter.isNull()) {
                Text(text = "No chapter can be read")
            } else {
                Text(text = "Prev ${chapterTransition.prevChapter!!.dbChapter.name}")
            }

            Spacer(modifier = Modifier.height(MaterialTheme.padding.Normal))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.padding.Normal))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                if (chapterTransition.currChapter.isNull()) {
                    Text(text = "No chapter can be read")
                } else {
                    Text(text = "Next ${chapterTransition.currChapter!!.dbChapter.name}.")
                }
            }

        }
    }

}