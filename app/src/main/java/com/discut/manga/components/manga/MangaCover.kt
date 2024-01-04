package com.discut.manga.components.manga

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.TextView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.discut.manga.R
import com.discut.manga.components.domain.MangaCoverInfo

enum class MangaCover(private val ratio: Float) {
    SQUARE(1f), BOOK(2f / 3f);

    @Composable
    operator fun invoke(
        modifier: Modifier = Modifier,
        info: MangaCoverInfo,
        shape: Shape = MaterialTheme.shapes.extraSmall,
        onClick: (() -> Unit)? = null,
    ) {
        invoke(
            modifier = modifier,
            model = info.coverUrl,
            title = info.title,
            shape = shape,
            onClick = onClick,
            contentDescription = info.contentDescription
        )
    }

    @SuppressLint("InflateParams")
    @Composable
    operator fun invoke(
        modifier: Modifier = Modifier,
        model: Any?,
        title: String,
        contentDescription: String = "",
        shape: Shape = MaterialTheme.shapes.extraSmall,
        onClick: (() -> Unit)? = null,
    ) {
        val value = MaterialTheme.typography.titleLarge.fontSize.value
        val chars = getChars(title)
        var enablePlaceholderCover by remember {
            mutableStateOf(true)
        }
        var enableImageCover by remember {
            mutableStateOf(true)
        }
        val rememberCoroutineScope = rememberCoroutineScope()

        LaunchedEffect(key1 = model){
            model?.apply {

            }
        }
        Surface(
            modifier = modifier
                .aspectRatio(ratio)
                .clip(shape)
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            role = Role.Button,
                            onClick = onClick,
                        )
                    } else {
                        Modifier
                    }
                ),
        ) {
            if (enableImageCover) {
                AsyncImage(
                    model = model,
                    contentDescription = contentDescription,
                    contentScale = ContentScale.Crop,
                    onError = {
                        enableImageCover = false
                    },
                    onSuccess = {
                        /*rememberCoroutineScope.launch {
                            delay(1000)
                            enablePlaceholderCover = false
                        }*/
                        enablePlaceholderCover = false
                    }
                )
            }

            AnimatedVisibility(
                visible = enablePlaceholderCover,
                content = {
                    AndroidView(modifier = Modifier
                        .fillMaxSize(),
                        factory = {
                            val view =
                                LayoutInflater.from(it).inflate(R.layout.cover_layout, null)
                            val char1View = view.findViewById<TextView>(R.id.cover_text_1)
                            val char2View = view.findViewById<TextView>(R.id.cover_text_2)
                            char1View.textSize = value * 8
                            char2View.textSize = value * 13
                            char1View.text = chars.first
                            char2View.text = chars.second
                            view.setBackgroundColor(placeholderCoverBackgroundColors.random())
                            view
                        })
                },
                exit = fadeOut()
            )
        }
    }
}

private fun getChars(str: String): Pair<String, String> {
    try {
        var chars = str.uppercase()
        if (str.isBlank()) {
            return "N" to "O"
        }
        if (str.length == 1) {
            return str to ""
        }
        val char1 = chars[(chars.indices).random()].toString()
        chars = chars.replace(char1, "", true)
        val char2 = chars[(chars.indices).random()].toString()
        return char1 to char2
    } catch (e: Exception) {
        return "N" to "O"
    }
}

private val placeholderCoverBackgroundColors = listOf(
    Color.parseColor("#196A71"),
    Color.parseColor("#f9d3e3"),
    Color.parseColor("#6a8d52"),
    Color.parseColor("#f0908d"),
    Color.parseColor("#d9883d"),
)