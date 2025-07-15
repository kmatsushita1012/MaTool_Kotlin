package com.studiomk.matool.presentation.store_view.admin.districts.route.export

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
import com.studiomk.ktca.core.store.StoreOf
import io.github.alexzhirkevich.cupertino.icons.CupertinoIcons
import io.github.alexzhirkevich.cupertino.icons.outlined.ChevronBackward
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.ui.layout.onGloballyPositioned
import com.studiomk.matool.presentation.utils.makeRegion
import com.studiomk.matool.presentation.view.maps.rememberSyncedCameraState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalCupertinoApi::class)
@Composable
fun AdminRouteExportStoreView(store: StoreOf<AdminRouteExport.State, AdminRouteExport.Action>) {
    val state by store.state.collectAsState()

    val context = LocalContext.current
    val scope   = rememberCoroutineScope()
    val savePdf = rememberSavePdfLauncher()

    var size by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    var cameraPositionState = rememberSyncedCameraState(
        Binding(
            getter = { state.region },
            setter = { region -> store.send(AdminRouteExport.Action.RegionChanged(region)) }
        )
    )

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
                        CupertinoIconButton(
                            onClick = {
                                size?.let{ (screenW, screenH) ->
                                    Log.d("MapView", "screenSize ${screenW}x${screenH}")
                                    state.region?.let {
                                        scope.launch {
                                            val  snapshot = createRouteExportSnapshotWithOverlay(
                                                context = context,
                                                points = state.points,
                                                segments = state.segments,
                                                region = it,
                                                cameraPosition = cameraPositionState.position,
                                                widthPx = screenW,
                                                heightPx = screenH
                                            )
                                            savePdf(snapshot, "行動図（一部）")
                                        }
                                    }
                                }

                            }
                        ) {
                            CupertinoIcon(
                                Icons.Default.Camera,
                                contentDescription = null
                            )
                        }

                        CupertinoIconButton(
                            onClick = {
                                val region = makeRegion(
                                    state.segments.flatMap { it.coordinates },
                                    aspectRatio = 1.414 ,
                                    paddingRatio = 1.3
                                )
                                region?.let {
                                    Log.d("MapView", "region ${it}")
                                    scope.launch {
                                        Log.d("MapView", "launch")
                                        val snapshot = createRouteExportSnapshotWithOverlay(
                                            context = context,
                                            points = state.points,
                                            segments = state.segments,
                                            region = it,
                                        )
                                        Log.d("MapView", "snapshot")
                                        savePdf(snapshot, "行動図（全体）")
                                    }
                                }
                            }
                        ) {
                            CupertinoIcon(
                                Icons.Default.Route,
                                contentDescription = null
                            )
                        }


                    }
                }
            )
        }
    ) {
        // 背景のMap
        Box(modifier = Modifier
            .fillMaxSize()
        ) {
            AdminRouteExportMapView(
                points = state.points,
                segments = state.segments,
                cameraPositionState = cameraPositionState,
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        val wPx = layoutCoordinates.size.width
                        val hPx = layoutCoordinates.size.height
                        size = wPx to hPx
                    }
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

@Composable
fun rememberSavePdfLauncher(
    scope: CoroutineScope = rememberCoroutineScope()
): (ImageBitmap, String) -> Unit {
    val context = LocalContext.current
    // ❶ 保存待ちのスナップショットを保持
    var pendingSnapshot by remember { mutableStateOf<ImageBitmap?>(null) }

    // ❷ CreateDocument のランチャ
    val launcher = rememberLauncherForActivityResult(
        CreateDocument("application/pdf")
    ) { uri: Uri? ->
        val snapshot = pendingSnapshot   // コールバック内で参照
        if (uri != null && snapshot != null) {
            scope.launch {
                val ok = try {
                    context.contentResolver.openOutputStream(uri)?.use { out ->
                        val bitmap   = snapshot.asAndroidBitmap()
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
                Toast.makeText(
                    context,
                    if (ok) "PDF を保存しました" else "保存に失敗しました",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        pendingSnapshot = null           // 後片付け
    }

    // ❸ 呼び出し側に返すラムダ
    return remember {
        { snapshot: ImageBitmap, fileName: String ->
            pendingSnapshot = snapshot
            launcher.launch("$fileName.pdf")
        }
    }
}
