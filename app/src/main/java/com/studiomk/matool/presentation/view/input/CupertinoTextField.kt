package com.studiomk.matool.presentation.view.input

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester


@Composable
fun CupertinoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: (@Composable () -> Unit)? = null,
) {
    val focusRequester = remember { FocusRequester() }
    val focusState = remember { mutableStateOf(false) }

    var internalText by rememberSaveable { mutableStateOf(value) }
    LaunchedEffect(value, focusState.value) {
        if(internalText != value && !focusState.value){
            internalText = value
        }
    }

    Box(
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                BasicTextField(
                    value = internalText,
                    onValueChange = {
                        internalText = it
                        onValueChange(it)
                    },
                    enabled = enabled,
                    textStyle = textStyle.copy(
                        color = if (enabled) Color.Black else Color.Gray,
                        textAlign = TextAlign.Start
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged {
                            focusState.value = it.isFocused
                        }
                        .focusRequester(focusRequester),
                    visualTransformation = visualTransformation,
                    decorationBox = { innerTextField ->
                        if (internalText.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = Color.Gray,
                                style = textStyle
                            )
                        }
                        innerTextField()
                    }
                )
            }
            if (trailing != null) {
                Box(
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    trailing()
                }
            }
        }
    }
}


@Composable
fun CupertinoBorderedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) = CupertinoTextField(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier
        .border(1.dp, Color.Black, RoundedCornerShape(8.dp)),
    placeholder = placeholder,
    enabled = enabled,
    textStyle = textStyle,
    visualTransformation = visualTransformation
)
