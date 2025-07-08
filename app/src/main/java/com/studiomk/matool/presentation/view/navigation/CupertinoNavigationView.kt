package com.studiomk.matool.presentation.view.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CupertinoNavigationView(
    modifier: Modifier = Modifier,
    toolBar: (@Composable (Modifier) -> Unit)? = null,
    isFullScreen: Boolean = true, // ← 追加
    content: @Composable () -> Unit
) {
    val topInset = if (isFullScreen) {
        WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    } else {
        0.dp
    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        toolBar?.let {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = topInset)
            ) {
                toolBar(Modifier.fillMaxWidth())
                Divider(
                    color = Color.DarkGray,
                    thickness = 0.3.dp
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = if (toolBar == null) topInset else 0.dp)
        ) {
            content()
        }
    }
}
