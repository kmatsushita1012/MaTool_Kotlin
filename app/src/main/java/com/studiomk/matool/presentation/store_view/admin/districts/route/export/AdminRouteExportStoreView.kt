package com.studiomk.matool.presentation.store_view.admin.districts.route.export

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.studiomk.matool.core.binding.Binding
import com.studiomk.matool.presentation.store_view.shared.notice_alert.NoticeAlertDialog
import com.studiomk.matool.presentation.view.maps.AdminRouteExportMapView
import com.studiomk.matool.presentation.view.navigation.CupertinoNavigationView
import com.studiomk.matool.presentation.view.navigation.CupertinoToolBar
import com.studiomk.matool.presentation.view.navigation.CupertinoToolbarLeadingButton
import io.github.alexzhirkevich.cupertino.*
import java.io.File
import java.io.FileOutputStream
import com.studiomk.ktca.core.store.StoreOf
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.ChevronBackward

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun AdminRouteExportStoreView(store: StoreOf<AdminRouteExport.State, AdminRouteExport.Action>) {
    val state by store.state.collectAsState()

    var partialSnapshot by remember { mutableStateOf<ImageBitmap?>(null) }
    var wholeSnapshot by remember { mutableStateOf<ImageBitmap?>(null) }
    val context = LocalContext.current

    CupertinoNavigationView(
        toolBar = {
            CupertinoToolBar(
                leading = {
                    CupertinoToolbarLeadingButton(
                        onClick =  { store.send(AdminRouteExport.Action.DismissTapped) },
                        icon = CupertinoIcons.Default.ChevronBackward,
                        text = "戻る"
                    )
                },
                center = { Text(state.title, modifier = Modifier.padding(horizontal = 8.dp)) },
                trailing = {
                    Row {
                        partialSnapshot?.let { snapshot ->
                            createPDFWithImage(context, snapshot, state.partialPath)?.let { pdfUri ->
                                CupertinoIconButton(
                                    onClick = {
                                        shareFile(context, pdfUri, "行動図")
                                    }
                                ) {
                                    CupertinoIcon(
                                        Icons.Default.Camera,
                                        contentDescription = null
                                    )
                                }

                            }
                        }
                        wholeSnapshot?.let { snapshot ->
                            createPDFWithImage(context, snapshot, state.wholePath)?.let { pdfUri ->
                                CupertinoIconButton(
                                    onClick = {
                                        shareFile(context, pdfUri, "行動図（全体）")
                                    }
                                ) {
                                    CupertinoIcon(
                                        Icons.Default.Route,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
    ) {
        // 背景のMap
        Box(modifier = Modifier.fillMaxSize()) {
            AdminRouteExportMapView(
                points = state.points,
                segments = state.segments,
                region = Binding(
                    getter = { state.region },
                    setter = { region -> store.send(AdminRouteExport.Action.RegionChanged(region)) }
                ),
                onChangeWholeSnapshot = { wholeSnapshot = it },
                onChangePartialSnapshot = { partialSnapshot = it }
            )
        }
    }
    NoticeAlertDialog(
        store = store.optionalScope(
            statePath = AdminRouteExport.alertKey,
            actionPath = AdminRouteExport.alertCase
        )
    )
}

// PDF生成（ImageBitmap→PDFファイルのパスを返す）
fun createPDFWithImage(context: android.content.Context, image: ImageBitmap, path: String): java.io.File? {
    return try {
        val file = File(context.cacheDir, path)
        val bitmap = image.asAndroidBitmap()
        val stream = FileOutputStream(file)
        val document = android.graphics.pdf.PdfDocument()
        val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = document.startPage(pageInfo)
        page.canvas.drawBitmap(bitmap, 0f, 0f, null)
        document.finishPage(page)
        document.writeTo(stream)
        document.close()
        stream.close()
        file
    } catch (e: Exception) {
        null
    }
}

// シェア処理（Compose用のシェアAPIやIntentを利用）
fun shareFile(context: android.content.Context, file: File, title: String) {
    val uri = androidx.core.content.FileProvider.getUriForFile(
        context,
        context.packageName + ".fileprovider",
        file
    )
    val intent = android.content.Intent().apply {
        action = android.content.Intent.ACTION_SEND
        type = "application/pdf"
        putExtra(android.content.Intent.EXTRA_STREAM, uri)
        putExtra(android.content.Intent.EXTRA_TITLE, title)
        addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(
        android.content.Intent.createChooser(intent, title)
            .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}
