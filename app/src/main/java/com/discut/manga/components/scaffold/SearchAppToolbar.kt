package com.discut.manga.components.scaffold

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults.ContainerBox
import androidx.compose.material3.OutlinedTextFieldDefaults.colors
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.contentPaddingWithoutLabel
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.discut.manga.theme.alpha

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAppToolbar(
    isMainAppbar: Boolean = true,
    defaultSearchKey: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    onChangeSearchKey: (String) -> Unit = {},
    titleContent: (@Composable () -> Unit) = { },

    actions: @Composable RowScope.() -> Unit = {},
    onBack: () -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var isSearchAction by remember {
        mutableStateOf(false)
    }
    var searchKey by remember {
        mutableStateOf(defaultSearchKey ?: "")
    }
    val changeInputFieldText: (text: String) -> Unit = {
        searchKey = it
        onChangeSearchKey(it)
    }
    val innerTitleContent: @Composable () -> Unit = {

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        val colors = colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            disabledBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            errorBorderColor = Color.Transparent
        )
        BasicTextField(
            value = searchKey,
            onValueChange = changeInputFieldText,
            textStyle = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .clearFocusOnSoftKeyboardHide(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
            visualTransformation = visualTransformation,
            interactionSource = interactionSource,
            singleLine = true,
            decorationBox = { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = searchKey,
                    innerTextField = innerTextField,
                    enabled = true,
                    singleLine = true,
                    visualTransformation = visualTransformation,
                    interactionSource = interactionSource,
                    isError = false,
                    label = null,
                    placeholder = {
                        Text(
                            modifier = Modifier.alpha(MaterialTheme.alpha.Normal),
                            text = "Search...",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Normal,
                            ),
                        )
                    },
                    leadingIcon = null,
                    trailingIcon = {
                        if (isSearchAction && searchKey.isNotBlank()) {
                            IconButton(onClick = {
                                changeInputFieldText("")
                            }) {
                                Icon(
                                    imageVector = Icons.Outlined.Clear,
                                    contentDescription = "Clear search"
                                )
                            }
                        }
                    },
                    prefix = null,
                    suffix = null,
                    supportingText = null,
                    shape = TextFieldDefaults.shape,
                    colors = colors,
                    contentPadding =
                    contentPaddingWithoutLabel(),
                    container = {
                        ContainerBox(
                            enabled = true,
                            isError = false,
                            interactionSource = interactionSource,
                            colors = colors,
                            shape = TextFieldDefaults.shape
                        )
                    },
                )
            },
        )
    }
    TopAppBar(
        title = {
            if (!isSearchAction) {
                titleContent()
                return@TopAppBar
            }
            innerTitleContent()
        },
        actions = {
            if (!isSearchAction) {
                IconButton(onClick = { isSearchAction = true }) {
                    Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search")
                }
            }
            actions()
        },
        navigationIcon = {
            if (isSearchAction) {
                IconButton(onClick = {
                    isSearchAction = false
                    changeInputFieldText("")
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Close search"
                    )
                }
            } else if (!isMainAppbar) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        windowInsets = windowInsets
    )
}

/**
 * For TextField, this modifier will clear focus when soft
 * keyboard is hidden.
 *
 * from tachiyomi
 */
@OptIn(ExperimentalLayoutApi::class)
private fun Modifier.clearFocusOnSoftKeyboardHide(
    onFocusCleared: (() -> Unit)? = null,
): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }
    var keyboardShowedSinceFocused by remember { mutableStateOf(false) }
    if (isFocused) {
        val imeVisible = WindowInsets.isImeVisible
        val focusManager = LocalFocusManager.current
        LaunchedEffect(imeVisible) {
            if (imeVisible) {
                keyboardShowedSinceFocused = true
            } else if (keyboardShowedSinceFocused) {
                focusManager.clearFocus()
                onFocusCleared?.invoke()
            }
        }
    }

    Modifier.onFocusChanged {
        if (isFocused != it.isFocused) {
            if (isFocused) {
                keyboardShowedSinceFocused = false
            }
            isFocused = it.isFocused
        }
    }
}