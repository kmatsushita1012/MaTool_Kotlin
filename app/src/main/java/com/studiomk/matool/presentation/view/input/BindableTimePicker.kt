package com.studiomk.matool.presentation.view.input

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.studiomk.ktca.core.util.Binding
import com.studiomk.matool.domain.entities.shared.SimpleTime
import com.studiomk.matool.domain.entities.shared.text
import com.seo4d696b75.compose.material3.picker.NumberPicker
import kotlinx.collections.immutable.toImmutableList

@Composable
fun BindableTimePicker(
    label: String = "時刻を選択",
    selection: Binding<SimpleTime>
) {
    var showDialog by remember { mutableStateOf(false) }

    val timeText = selection.value.text

    val hourRange = (0..23).toImmutableList()
    val minuteRange = (0..55 step 5).toImmutableList()

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = { showDialog = false },
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("キャンセル")
                }
            },
            title = {
                Text(label)
            },
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(48.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NumberPicker(
                        value = selection.value.hour,
                        range = hourRange,
                        onValueChange = {
                            selection.set(SimpleTime(it, selection.value.minute))
                        }
                    )
                    NumberPicker(
                        value = selection.value.minute,
                        range = minuteRange,
                        onValueChange = {
                            selection.set(SimpleTime(selection.value.hour, it))
                        }
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = timeText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .clickable { showDialog = true }
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
