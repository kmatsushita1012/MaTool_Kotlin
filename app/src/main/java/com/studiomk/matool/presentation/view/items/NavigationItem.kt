package com.studiomk.matool.presentation.view.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.studiomk.matool.presentation.view.others.AccentIcon
import io.github.alexzhirkevich.cupertino.*
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.ChevronForward

@Composable
fun NavigationItem(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    status: String? = null,
    onTap: () -> Unit,
) {
    ListItem(
        modifier = modifier.clickable{ onTap() },
        leadingContent = {
            if (icon != null) {
                AccentIcon(
                    icon = icon,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        },
        headlineContent = {
            Text(
                text = text,
            )
        },
        trailingContent = {
            Row {
                if (status != null) {
                    Text(
                        text = status,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                CupertinoIcon(
                    imageVector = CupertinoIcons.Default.ChevronForward,
                    contentDescription = null
                )
            }
        }
    )
}