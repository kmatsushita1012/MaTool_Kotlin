package com.studiomk.matool.presentation.view.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import com.studiomk.ktca.core.util.Binding
import io.github.alexzhirkevich.cupertino.CupertinoHorizontalDivider

@Composable
fun <T> PickerMenu(
    selection: Binding<T>,
    items: List<T>,
    itemLabel: (T) -> String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = itemLabel(selection.value),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.widthIn(min = 100.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            items.forEachIndexed { index, item ->
                if (index > 0) {
                    CupertinoHorizontalDivider()
                }
                DropdownMenuItem(
                    text = { Text(itemLabel(item)) },
                    onClick = {
                        selection.set(item)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}
