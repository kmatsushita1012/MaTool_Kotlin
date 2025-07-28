package com.studiomk.matool.presentation.view.input

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import io.github.alexzhirkevich.cupertino.CupertinoButton
import io.github.alexzhirkevich.cupertino.CupertinoButtonDefaults.filledButtonColors
import io.github.alexzhirkevich.cupertino.theme.CupertinoTheme
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.cupertino.CupertinoButtonColors;
import io.github.alexzhirkevich.cupertino.CupertinoButtonDefaults.plainButtonColors
import io.github.alexzhirkevich.cupertino.CupertinoButtonSize;
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi;

@Composable
@ExperimentalCupertinoApi
fun CupertinoSecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: CupertinoButtonSize = CupertinoButtonSize.Regular,
    colors: CupertinoButtonColors = plainButtonColors(),
    border: BorderStroke? = BorderStroke(
        width = 2.dp,
        color = MaterialTheme.colorScheme.primary
    ),
    shape: Shape = size.shape(CupertinoTheme.shapes),
    contentPadding: PaddingValues = size.contentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) = CupertinoButton(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    size = size,
    colors = colors,
    border = border,
    shape = shape,
    contentPadding = contentPadding,
    interactionSource = interactionSource,
    content = content
)