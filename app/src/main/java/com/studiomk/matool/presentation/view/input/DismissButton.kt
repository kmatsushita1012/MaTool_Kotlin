package com.studiomk.matool.presentation.view.input

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.cupertino.CupertinoIcon
import io.github.alexzhirkevich.cupertino.CupertinoIconButton
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.ChevronBackward
import io.github.alexzhirkevich.cupertino.theme.CupertinoColors
import io.github.alexzhirkevich.cupertino.theme.DarkGray
import io.github.alexzhirkevich.cupertino.theme.LightGray

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun DismissButton(
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    CupertinoIconButton(
        onClick = onClick,
        enabled = isEnabled,
        modifier = Modifier
            .clip(CircleShape),
        border = BorderStroke(
            width = 1.dp,
            color = if (isEnabled) CupertinoColors.DarkGray else CupertinoColors.LightGray
        )
    ) {
        CupertinoIcon(
            imageVector = CupertinoIcons.Default.ChevronBackward,
            contentDescription = null,
            tint = CupertinoColors.DarkGray
        )
    }
}