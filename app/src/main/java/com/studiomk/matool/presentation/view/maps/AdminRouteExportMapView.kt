package com.studiomk.matool.presentation.view.maps

import android.content.Context
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.util.Log
import android.view.View
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.studiomk.ktca.core.util.Binding
import com.studiomk.matool.domain.entities.routes.Point
import com.studiomk.matool.domain.entities.routes.Segment
import com.studiomk.matool.domain.entities.shared.Coordinate
import com.studiomk.matool.presentation.utils.CoordinateRegion
import com.studiomk.matool.domain.entities.shared.text
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import androidx.core.graphics.createBitmap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.MapStyleOptions
import com.studiomk.matool.presentation.utils.makeRegion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resumeWithException
import com.studiomk.matool.R
import com.studiomk.matool.domain.entities.shared.latLng
import kotlin.math.*

@Composable
fun AdminRouteExportMapView(
    points: List<Point>,
    segments: List<Segment>,
    region: Binding<CoordinateRegion?>,
    modifier: Modifier = Modifier,
    onChangeWholeSnapshot: (ImageBitmap)->Unit,
    onChangePartialSnapshot: (ImageBitmap)->Unit
) {
    var cameraPositionState = rememberSyncedCameraState(region)
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val region = makeRegion(
            segments.flatMap { it.coordinates },

            aspectRatio = 1.414 ,
            paddingRatio = 1.4
        )
        region?.let {
            val whole = createRouteExportSnapshotWithOverlay(
                context = context,
                points = points,
                segments = segments,
                region = it,
            )
            onChangeWholeSnapshot(whole)
        }
    }

//    LaunchedEffect(region.get()) {
//        region.get()?.let {
//            val partial = createRouteExportSnapshotWithOverlay(
//                context = context,
//                points = points,
//                segments = segments,
//                region = it,
//                widthPx = 594,
//                heightPx = 420
//            )
//            onChangePartialSnapshot(partial)
//        }
//    }
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
    ) {
        // マーカー
        points.forEach { point ->
            Marker(
                state = MarkerState(
                    position = LatLng(point.coordinate.latitude, point.coordinate.longitude)
                ),
                title = point.title ?: "",
                snippet = point.description
            )
        }
        // ポリライン
        segments.forEach { segment ->
            Polyline(
                points = segment.coordinates.map { LatLng(it.latitude, it.longitude) },
                color = androidx.compose.ui.graphics.Color.Blue,
                width = 8f
            )
        }
    }
    
}

/**
 * PDFや画像出力用のスナップショットをBitmapで生成する関数
 * 必要なサイズ・region・地理情報を渡すと、iOSのtakeSnapshot相当の画像を返します
 */
suspend fun createRouteExportSnapshotWithOverlay(
    context: Context,
    points: List<Point>,
    segments: List<Segment>,
    region: CoordinateRegion,
    widthPx: Int = 594,
    heightPx: Int = 420
): ImageBitmap = withContext(Dispatchers.Main) {
    suspendCancellableCoroutine { cont ->
        val mapView = MapView(
            context,
            GoogleMapOptions().liteMode(true)
        ).apply {
            onCreate(null)
            onResume()
            measure(
                View.MeasureSpec.makeMeasureSpec(widthPx,  View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(heightPx, View.MeasureSpec.EXACTLY)
            )
            layout(0, 0, widthPx, heightPx)
        }

        mapView.getMapAsync { gMap ->
            val success = gMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(context, R.raw.exportable_map_style)
            )
            if (!success) {
                Log.e("MapView", "Style parsing failed.")
            }
            gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(region.toLatLngBounds(),0))
            gMap.setOnMapLoadedCallback {

                gMap.snapshot { bitmap ->
                    if (cont.isCancelled) {
                        mapView.onPause()
                        mapView.onDestroy()
                        return@snapshot
                    }

                    if (bitmap == null) {
                        if (!cont.isCompleted) {
                            cont.resumeWithException(IllegalStateException("snapshot returned null"))
                        }
                        mapView.onPause()
                        mapView.onDestroy()
                        return@snapshot
                    }

                    val overlayBitmap = createBitmap(bitmap.width, bitmap.height)
                    val canvas = AndroidCanvas(overlayBitmap)
                    val dest = android.graphics.Rect(0, 0, bitmap.width, bitmap.height)
                    canvas.drawBitmap(bitmap, null, dest, null)

                    drawOverlayOnCanvas(
                        context,
                        canvas,
                        points,
                        segments,
                        region,
                        bitmap.width,
                        bitmap.height
                    )

                    if (!cont.isCompleted) {
                        cont.resume(overlayBitmap.asImageBitmap()) {}
                    }
                    // MapViewはキャンセルや後処理時に呼ぶのが安全
                    mapView.onPause()
                    mapView.onDestroy()
                }
            }
        }

        cont.invokeOnCancellation {
            mapView.onPause()
            mapView.onDestroy()
        }
    }

}

fun drawOverlayOnCanvas(
    context: Context,
    canvas: AndroidCanvas,
    points: List<Point>,
    segments: List<Segment>,
    region: CoordinateRegion,
    widthPx: Int,
    heightPx: Int
) {
    val bounds = region.toLatLngBounds()
    val minLat = bounds.southwest.latitude
    val maxLat = bounds.northeast.latitude
    val minLng = bounds.southwest.longitude
    val maxLng = bounds.northeast.longitude

    fun geoToCanvas(lat: Double, lng: Double): Pair<Float, Float> {
        val x = ((lng - minLng) / region.longitudeDelta ) * widthPx
        val y = ((maxLat - lat) / region.latitudeDelta) * heightPx
        return x.toFloat() to y.toFloat()
    }

    // ポリライン（白縁→青線）
    val paintWhite = Paint().apply {
        color = AndroidColor.WHITE
        strokeWidth = 4f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    val paintBlue = Paint().apply {
        color = AndroidColor.rgb(0, 0, 255)
        strokeWidth = 3f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    segments.forEach { segment ->
        if (segment.coordinates.size > 1) {
            val path = android.graphics.Path()
            segment.coordinates.firstOrNull()?.let {
                val (x, y) = geoToCanvas(it.latitude, it.longitude)
                path.moveTo(x, y)
            }
            segment.coordinates.drop(1).forEach {
                val (x, y) = geoToCanvas(it.latitude, it.longitude)
                path.lineTo(x, y)
            }
            canvas.drawPath(path, paintWhite)
            canvas.drawPath(path, paintBlue)
        }
    }

    // ピンとキャプション
    val pinRadius = 5f
    val fontSize = 8f
    val padding = 2f
    val margin =  5f
    val paintRed = Paint().apply {
        color = AndroidColor.RED
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    val paintText = Paint().apply {
        color = AndroidColor.BLACK
        textSize = fontSize
        isFakeBoldText = true
        isAntiAlias = true
    }
    val paintRect = Paint().apply {
        color = AndroidColor.WHITE
        alpha = (0.7f * 255).toInt()
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    val paintRectStroke = Paint().apply {
        color = AndroidColor.RED
        style = Paint.Style.STROKE
        strokeWidth =  0.5f
        isAntiAlias = true
    }
    val paintLine = Paint().apply {
        color = AndroidColor.RED
        strokeWidth =  1.0f
        isAntiAlias = true
    }

    val drawnRects = mutableListOf<android.graphics.RectF>()

    points.forEachIndexed { index, point ->
        val (x, y) = geoToCanvas(point.coordinate.latitude, point.coordinate.longitude)
        // ピン
        canvas.drawCircle(x, y, pinRadius, paintRed)

        // キャプション
        var caption = "${index + 1}"
        point.title?.let { caption += ":$it" }
        point.time?.text?.let { caption += "\n$it" }

        // テキストサイズ計算
        val lines = caption.split("\n")
        val textBounds = android.graphics.Rect()
        val length = lines.maxOf { it.length }
        paintText.getTextBounds(caption, 0, length, textBounds)
        val textWidth = textBounds.width().toFloat()
        val textHeight = textBounds.height().toFloat()
        val halfWidth = textWidth / 2 + padding
        val halfHeight = textHeight * lines.size  / 2 + padding

        // 衝突回避しながら描画位置決定
        val directions = listOf(
            Pair(+1f, -1f), Pair(+1f, +1f), Pair(-1f, +1f), Pair(-1f, -1f)
        )
        for ((dx, dy) in directions) {
            val centerX = x + dx * (margin + halfWidth)
            val centerY = y + dy * (margin + halfHeight)
            val rect = android.graphics.RectF(
                centerX - halfWidth,
                centerY - halfHeight,
                centerX + halfWidth,
                centerY + halfHeight
            )
            if (drawnRects.none { it.intersect(rect) }) {
                // 吹き出し線
                canvas.drawLine(
                    x, y,
                    x + dx * margin, y + dy * margin,
                    paintLine
                )
                // 背景
                canvas.drawRect(rect, paintRect)
                // 枠
                canvas.drawRect(rect, paintRectStroke)
                // テキスト
                lines.forEachIndexed { i, line ->
                    canvas.drawText(
                        line,
                        rect.left + padding,
                        rect.top + padding + fontSize * (i + 1),
                        paintText
                    )
                }
                drawnRects.add(rect)
                break
            }
        }
    }
}

// 地理座標リストからLatLngBoundsを計算
fun calculateBounds(coords: List<Coordinate>): LatLngBounds {
    val builder = LatLngBounds.builder()
    coords.forEach { builder.include(LatLng(it.latitude, it.longitude)) }
    return builder.build()
}

/**
 * Swift の MKCoordinateSpan と同じ (latitudeDelta, longitudeDelta) から
 * Google Maps ズームレベルを推定する。
 *
 * @param centerLat        画面中心の緯度（度）
 * @param latitudeDelta    画面に収めたい緯度方向の角度差（MKCoordinateSpan.latitudeDelta 相当）
 * @param longitudeDelta   画面に収めたい経度方向の角度差（MKCoordinateSpan.longitudeDelta 相当）
 * @param mapWidthPx       生成ビットマップの幅（ピクセル）
 * @param mapHeightPx      生成ビットマップの高さ（ピクセル）
 * @return                 Google Maps のズームレベル (float)。必要に応じて floor() / ceil() して整数に。
 */

fun spanToZoom(
    centerLat: Double,
    latitudeDelta: Double,
    longitudeDelta: Double,
    mapWidthPx: Int,
    mapHeightPx: Int
): Float {
    // 地球の半周の長さ（地球は半径6378137mの球体）
    val EQUATOR_LENGTH = 40075016.686 // 地球の赤道周長(m)

    // 緯度をラジアンに変換
    val latRad = Math.toRadians(centerLat)

    // メルカトル投影のY方向の長さ（経度1度あたりの距離は赤道付近で最長）
    // ピクセル当たりの距離を計算
    val metersPerPixelX = (EQUATOR_LENGTH * cos(latRad)) / (256 * 2.0.pow(0.0)) // zoom 0 でのピクセル距離

    // 経度方向のズーム計算
    val zoomLon = ln((mapWidthPx * EQUATOR_LENGTH * cos(latRad)) / (longitudeDelta * 256 * EQUATOR_LENGTH)) / ln(2.0)

    // これだと変なので簡略化、経度方向のズーム計算は
    val zoomLonCorrected = ln((mapWidthPx * 360.0) / (longitudeDelta * 256.0)) / ln(2.0)

    // 緯度方向はメルカトルのY座標の差から計算
    fun latToMercator(lat: Double): Double {
        val sinLat = sin(Math.toRadians(lat))
        return 0.5 * ln((1 + sinLat) / (1 - sinLat))
    }

    val mercatorTop = latToMercator(centerLat + latitudeDelta / 2)
    val mercatorBottom = latToMercator(centerLat - latitudeDelta / 2)
    val mercatorDelta = abs(mercatorTop - mercatorBottom)

    val zoomLat = ln(mapHeightPx / (mercatorDelta * 256)) / ln(2.0)

    // 最終的に縦横ともに収まるように小さい方を返す
    return min(zoomLonCorrected, zoomLat).toFloat()
}
