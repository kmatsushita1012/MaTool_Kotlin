package com.studiomk.matool.presentation.view.items

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.cupertino.*
import io.github.alexzhirkevich.cupertino.icons.*
import io.github.alexzhirkevich.cupertino.icons.outlined.InfoCircle

@Composable
fun BulletItem(
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CupertinoIcon(
            imageVector = CupertinoIcons.Default.InfoCircle,
            contentDescription = null,
            modifier = Modifier.padding(start = 8.dp)
        )
        Text(
            text = text,
            modifier = Modifier.padding(start = 8.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}