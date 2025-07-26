package com.studiomk.matool.presentation.view.items

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import io.github.alexzhirkevich.cupertino.*
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import com.studiomk.ktca.core.util.Binding
import io.github.alexzhirkevich.cupertino.icons.outlined.ChevronDown
import io.github.alexzhirkevich.cupertino.icons.outlined.ChevronForward
import io.github.alexzhirkevich.cupertino.icons.outlined.ChevronUp

@Composable
fun ToggleSelectedItem(
    title: String,
    isExpanded: Binding<Boolean>
) {
    Box(
        modifier = Modifier
            .clickable {
                isExpanded.value = !isExpanded.value
            }
            .animateContentSize()
            .clip(shape = RoundedCornerShape(8.dp))
    ){
        ListItem(
            headlineContent = {
                Text(
                    text = title
                )
            },
            trailingContent = {
                CupertinoIcon(
                    imageVector = if (isExpanded.value) CupertinoIcons.Default.ChevronUp else CupertinoIcons.Default.ChevronDown,
                    tint = Color.Gray,
                    modifier = Modifier.padding(start = 8.dp),
                    contentDescription = null
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ToggleOptionItem(
    title: String,
    onTap: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable { onTap() }
            .animateContentSize()
            .clip(shape = RoundedCornerShape(8.dp))
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = title,
                    modifier = Modifier
                )

            },
            trailingContent = {
                CupertinoIcon(
                    imageVector = CupertinoIcons.Default.ChevronForward,
                    tint = Color.Gray,
                    modifier = Modifier.padding(start = 8.dp),
                    contentDescription = null
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )
    }
}