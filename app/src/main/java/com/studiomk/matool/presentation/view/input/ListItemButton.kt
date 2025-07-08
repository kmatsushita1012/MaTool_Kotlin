package com.studiomk.matool.presentation.view.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.cupertino.CupertinoIcon

@Composable
fun ListItemButton(
    onClick: ()-> Unit,
    icon: ImageVector? = null,
    text: String? = null,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    tint: Color = MaterialTheme.colorScheme.primary
){
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        headlineContent = {
            text?.let {
                Text(
                    text,
                    color = tint
                )
            }
        },
        leadingContent = {
            icon?.let {
                CupertinoIcon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint
                )
            }
        }
    )
}