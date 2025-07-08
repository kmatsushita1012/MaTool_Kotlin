package com.studiomk.matool.presentation.view.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.unit.*

@Composable
fun CupertinoToolBar(
    modifier: Modifier = Modifier,
    leading: @Composable () -> Unit = {},
    center: @Composable () -> Unit = {},
    trailing: @Composable () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 8.dp),
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(horizontal = 8.dp)
        ) {
            Box(modifier = Modifier.align(Alignment.CenterStart)) {
                leading()
            }
            Box(modifier = Modifier.align(Alignment.Center)) {
                center()
            }
            Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                trailing()
            }
        }

    }
}
