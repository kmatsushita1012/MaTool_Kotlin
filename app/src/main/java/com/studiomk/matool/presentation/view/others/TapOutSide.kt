package com.studiomk.matool.presentation.view.others


import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun TapOutsideOverlay(
    onOutsideTap: () -> Unit,
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable () -> Unit
) {
    val enabledModifier = if (enable) {
        modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    onOutsideTap()
                }
            )
        }
    } else {
        modifier
    }

    Box(
        modifier = enabledModifier
            .fillMaxSize()

    ) {
        Box(
            modifier = Modifier
                .align(contentAlignment)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { /* consume only */ })
                }
        ) {
            content()
        }
    }
}
