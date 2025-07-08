package com.studiomk.matool.presentation.view.others

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText


@Composable
fun OutlinedText(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    stroke: Stroke = Stroke(),
    strokeColor: Color = Color.Transparent,
) {
    var textLayoutResult: TextLayoutResult? by remember {
        mutableStateOf(null)
    }
    Text(
        text = text,
        style = textStyle,
        onTextLayout = {
            textLayoutResult = it
        },
        modifier = modifier
            .drawBehind {
                textLayoutResult?.let {
                    drawText(
                        textLayoutResult = it,
                        drawStyle = stroke,
                        color = strokeColor,
                    )
                }
            }
    )
}