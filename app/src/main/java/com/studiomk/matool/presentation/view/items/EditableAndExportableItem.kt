package com.studiomk.matool.presentation.view.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.cupertino.*
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.SquareAndArrowUp
import io.github.alexzhirkevich.cupertino.icons.outlined.SquareAndPencil

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun EditableAndExportableItem(
    text: String,
    onEdit: () -> Unit,
    onExport: () -> Unit
) =
    ListItem(
        modifier = Modifier.clickable{ onEdit() },
        headlineContent = {},
        leadingContent = {
            Text(
                text = text,
            )
        },
        trailingContent = {
            Row {
                CupertinoIconButton(
                    onClick = onEdit,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    CupertinoIcon(
                        imageVector = CupertinoIcons.Default.SquareAndPencil,
                        contentDescription = null
                    )
                }
                CupertinoIconButton(
                    onClick = onExport,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    CupertinoIcon(
                        imageVector = CupertinoIcons.Default.SquareAndArrowUp,
                        contentDescription = null
                    )
                }
            }
        }
    )
