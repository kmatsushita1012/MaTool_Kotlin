package com.studiomk.matool.presentation.store_view.admin.districts.route.export

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import com.studiomk.ktca.core.util.Binding
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import kotlinx.coroutines.launch

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun AdminRouteExportStoreView(store: StoreOf<AdminRouteExport.State, AdminRouteExport.Action>) {
    val state by store.state.collectAsState()

    var partialSnapshot by remember { mutableStateOf<ImageBitmap?>(null) }
    var wholeSnapshot by remember { mutableStateOf<ImageBitmap?>(null) }

    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    // SAF: CreateDocument で PDF を作成
    val partialSnapshotLauncher = rememberLauncherForActivityResult(
        contract = CreateDocument("application/pdf")
    ) { uri: Uri? ->
        uri?.let {
            partialSnapshot?.let { snapshot ->
                scope.launch {
                    val ok = saveImageAsPdf(context, snapshot, it)
                    val msg = if (ok) "PDF を保存しました" else "保存に失敗しました"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val wholeSnapshotLauncher = rememberLauncherForActivityResult(
        contract = CreateDocument("application/pdf")
    ) { uri: Uri? ->
        uri?.let {
            wholeSnapshot?.let { snapshot ->
                scope.launch {
                    val ok = saveImageAsPdf(context, snapshot, it)
                    val msg = if (ok) "PDF を保存しました" else "保存に失敗しました"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


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
                            CupertinoIconButton(
                                onClick = {
                                    partialSnapshotLauncher.launch("行動図（一部）")
                                }
                            ) {
                                CupertinoIcon(
                                    Icons.Default.Camera,
                                    contentDescription = null
                                )
                            }
                        }
                        wholeSnapshot?.let { snapshot ->
                            CupertinoIconButton(
                                onClick = {
                                    wholeSnapshotLauncher.launch("行動図（全体）")
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
    BackHandler(enabled = true) {
        store.send(AdminRouteExport.Action.DismissTapped)
    }
}

// PDF生成（ImageBitmap→PDFファイルのパスを返す）
suspend fun saveImageAsPdf(
    context: Context,
    image: ImageBitmap,
    uri:   Uri
): Boolean = withContext(Dispatchers.IO) {
    return@withContext try {
        context.contentResolver.openOutputStream(uri)?.use { out ->
            val bitmap   = image.asAndroidBitmap()
            val pdfDoc   = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(
                bitmap.width,
                bitmap.height,
                1
            ).create()

            pdfDoc.startPage(pageInfo).apply {
                canvas.drawBitmap(bitmap, 0f, 0f, null)
                pdfDoc.finishPage(this)
            }
            pdfDoc.writeTo(out)
            pdfDoc.close()
        }
        true
    } catch (e: Exception) {
        Log.e("PdfSave", "error", e)
        false
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
    Log.d("Export", "shareFilie3 ${intent.toString()}")
    context.startActivity(
        android.content.Intent.createChooser(intent, title)
            .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}
//fun createPDFWithImage(context: android.content.Context, image: ImageBitmap, path: String, isCache: Boolean): java.io.File? {
//    Log.d("AdminRouteExport","createPDFWithImage1 ${isCache}")
//    return try {
//        val file = if (!isCache){ File(context.getExternalFilesDir(null), path) } else { File(context.cacheDir, path) }
//        Log.d("AdminRouteExport","createPDFWithImage2 ${file}")
//        val bitmap = image.asAndroidBitmap()
//        val stream = FileOutputStream(file)
//        val document = android.graphics.pdf.PdfDocument()
//        val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
//        val page = document.startPage(pageInfo)
//        page.canvas.drawBitmap(bitmap, 0f, 0f, null)
//        document.finishPage(page)
//        document.writeTo(stream)
//        document.close()
//        stream.close()
//        file
//    } catch (e: Exception) {
//        Log.d("Export", "createPDFWithImage ${e.toString()}")
//        null
//    }
//}