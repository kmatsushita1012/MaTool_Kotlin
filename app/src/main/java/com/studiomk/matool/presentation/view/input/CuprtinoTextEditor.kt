package com.studiomk.matool.presentation.view.input

import android.util.Log
import androidx.compose.runtime.remember
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun CupertinoTextEditor(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    minLines: Int = 4,
) {
    val focusRequester = remember { FocusRequester() }
    val focusState = remember { mutableStateOf(false) }

    var internalText by rememberSaveable { mutableStateOf(value) }
    LaunchedEffect(value, focusState.value) {
        if(internalText != value && !focusState.value){
            internalText = value
        }
    }

    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .background(Color.White, shape)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        BasicTextField(
            value = internalText,
            onValueChange = {
                internalText = it
                onValueChange(it)
            },
            enabled = enabled,
            textStyle = textStyle.copy(
                color = if (enabled) Color.Black else Color.Gray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    focusState.value = it.isFocused
                }
                .focusRequester(focusRequester),
            cursorBrush = SolidColor(Color.Black),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .defaultMinSize(minHeight = (minLines * 20).dp) // 行数相当の高さ確保
                ) {
                    if (internalText.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = textStyle,
                            color = Color.Gray
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}
