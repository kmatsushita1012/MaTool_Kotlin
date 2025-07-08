package com.studiomk.matool.presentation.view.items

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.cupertino.CupertinoIcon
import io.github.alexzhirkevich.cupertino.CupertinoIconButton
import io.github.alexzhirkevich.cupertino.ExperimentalCupertinoApi
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.SquareAndPencil

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun EditableListItemView(
    text: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f)
        )
        CupertinoIconButton(
            onClick = onEdit,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            CupertinoIcon(
                imageVector = CupertinoIcons.Default.SquareAndPencil,
                contentDescription = "編集",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        CupertinoIconButton(
            onClick = onDelete,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            CupertinoIcon(
                imageVector = Icons.Default.Delete,
                contentDescription = "削除",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}