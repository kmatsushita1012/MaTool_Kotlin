package com.studiomk.matool.presentation.view.items

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                isExpanded.value = !isExpanded.value
            }
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f)
        )
        CupertinoIcon(
            imageVector = if (isExpanded.value) CupertinoIcons.Default.ChevronUp else CupertinoIcons.Default.ChevronDown,
            tint = Color.Gray,
            modifier = Modifier.padding(start = 8.dp),
            contentDescription = null
        )
    }
}

@Composable
fun ToggleOptionItem(
    title: String,
    onTap: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTap() }
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f)
        )
        CupertinoIcon(
            imageVector = CupertinoIcons.Default.ChevronForward,
            tint = Color.Gray,
            modifier = Modifier.padding(start = 8.dp),
            contentDescription = null
        )
    }
}