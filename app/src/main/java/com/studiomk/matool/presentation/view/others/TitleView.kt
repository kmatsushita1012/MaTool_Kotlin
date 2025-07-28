package com.studiomk.matool.presentation.view.others

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.studiomk.matool.presentation.view.input.DismissButton

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun TitleView(
    text: String,
    image: String? = null,
    isDismissEnabled: Boolean = true,
    onDismiss: () -> Unit
) {
    // デフォルト比率
    val defaultAspectRatio = 297f / 397f

    // 画像のアスペクト比を取得（リソースから取得できなければデフォルト）
    val aspectRatio = remember(image) {
        // 画像サイズ取得はリソースからは難しいためデフォルト比率を使う
        defaultAspectRatio
    }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val widthPx = with(LocalConfiguration.current) { screenWidth }
    val heightPx = widthPx * aspectRatio

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(heightPx)
    ) {
        //TODO
//        Image(
//            painter = painterResource(imageName),
//            contentDescription = null,
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .fillMaxSize()
//        )
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 8.dp, end = 8.dp),
                verticalAlignment = Alignment.Top
            ) {
                DismissButton(
                    isEnabled = isDismissEnabled,
                    onClick = onDismiss
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        }
    }
}

