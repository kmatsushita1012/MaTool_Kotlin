package com.studiomk.matool.presentation.view.input

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.cupertino.*
import com.studiomk.matool.core.binding.Binding
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.ChevronDown
import io.github.alexzhirkevich.cupertino.icons.outlined.ChevronUp

@Composable
fun <T : Any> MenuSelector(
    header: String,
    items: List<T>?,
    selection: Binding<T?>,
    textForItem: (T?) -> String,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    isNullable: Boolean = false
) {
    val hasError = errorMessage != null
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        // ヘッダー
        Text(
            text = header,
            style = MaterialTheme.typography.bodyMedium,
        )
        // メニュー選択部分
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 0.dp)
                    .clickable { expanded = true }
                    .then(
                        Modifier
                            .border(
                                width = 1.5.dp,
                                color = if (hasError) Color.Red else Color(0xFF2196F3),
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = textForItem(selection.value),
                    color = if (selection.value == null) Color.Gray else Color.Unspecified,
                    modifier = Modifier.weight(1f)
                        .padding(vertical = 8.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                CupertinoIcon(
                    imageVector = if(expanded)  CupertinoIcons.Default.ChevronUp else CupertinoIcons.Default.ChevronDown,
                    tint = Color.Gray,
                    contentDescription = null
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                if (isNullable) {
                    DropdownMenuItem(
                        text = { Text("未設定", color = Color.Gray) },
                        onClick = {
                            selection.value = null
                            expanded = false
                        }
                    )
                }
                items?.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(textForItem(item)) },
                        onClick = {
                            selection.value = item
                            expanded = false
                        }
                    )
                }
            }
        }

        // エラーメッセージ
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Red,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}