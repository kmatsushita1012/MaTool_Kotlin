package com.studiomk.matool.presentation.store_view.admin.districts.route.export

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

import android.view.View
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.studiomk.matool.R
import com.studiomk.matool.domain.entities.routes.Point
import com.studiomk.matool.domain.entities.routes.Segment
import com.studiomk.matool.domain.entities.shared.text
import com.studiomk.matool.presentation.utils.CoordinateRegion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.collections.forEach
import kotlin.coroutines.resumeWithException


/**
 * PDFや画像出力用のスナップショットをBitmapで生成する関数
 * 必要なサイズ・region・地理情報を渡すと、iOSのtakeSnapshot相当の画像を返します
 */
suspend fun createRouteExportSnapshotWithOverlay(
    context: Context,
    points: List<Point>,
    segments: List<Segment>,
    region: CoordinateRegion,
    cameraPosition: CameraPosition? = null,
    widthPx: Int = 594 * 2,
    heightPx: Int = 420 * 2,
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
        val scale = scaleToA4Height(
            if(widthPx > heightPx) widthPx else heightPx
        )

        mapView.getMapAsync { gMap ->
            val success = gMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(context, R.raw.exportable_map_style)
            )
            if (!success) {
                Log.e("MapView", "Style parsing failed.")
            }
            val cameraUpdate =
                if(cameraPosition!=null)
                    CameraUpdateFactory.newCameraPosition(cameraPosition)
                else
                    CameraUpdateFactory.newLatLngBounds(region.toLatLngBounds(), 0)
            gMap.moveCamera(cameraUpdate)
            gMap.drawRoute(segments, scale)
            gMap.drawPoints(context, points, scale)
            gMap.setOnMapLoadedCallback {
                gMap.snapshot { bitmap ->
                    if (cont.isCancelled) {
                        mapView.onPause(); mapView.onDestroy(); return@snapshot
                    }
                    if (bitmap == null) {
                        if (!cont.isCompleted) cont.resumeWithException(
                            IllegalStateException("snapshot returned null")
                        )
                        mapView.onPause(); mapView.onDestroy(); return@snapshot
                    }

                    // ← ここでもう完成画像が取れている
                    if (!cont.isCompleted) {
                        cont.resume(bitmap.asImageBitmap()) {}
                    }

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

fun scaleToA4Height(
    value: Int,
    a4LongSidePx: Int = 594 * 2
): Float {
    val scale = value / a4LongSidePx.toFloat()
    return scale
}

// 1) ポリライン（白→青）
fun GoogleMap.drawRoute(
    segments: List<Segment>,
    scale: Float
    ) {
    val outlineWidth = 8f
    val lineWidth = 6f
    segments.forEach { seg ->
        val pts = seg.coordinates.map { LatLng(it.latitude, it.longitude) }
        addPolyline(PolylineOptions().addAll(pts)
            .color(Color.WHITE).width(outlineWidth * scale).zIndex(1f))
        addPolyline(PolylineOptions().addAll(pts)
            .color(Color.BLUE ).width(lineWidth * scale).zIndex(2f))
    }
}

// 3) マーカー追加
fun GoogleMap.drawPoints(
    context: Context,
    points: List<Point>,
    scale: Float,
) {
    val placedRects = mutableListOf<RectF>()
    val redCircleBitmap = createRedCircleBitmap(context)
    val descriptor = BitmapDescriptorFactory.fromBitmap(redCircleBitmap)

    val margin = 10f * scale
    val padding = 4f * scale
    val borderWidth = 2f * scale
    val lineWidth = 4f * scale
    val fontPx = 16f * scale

    val directions = listOf(
        Pair(1, -1),
        Pair(1, 1),
        Pair(-1, 1),
        Pair(-1, -1),
    )

    val paintBackground = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        alpha = (0.7f * 255).toInt()
        style = Paint.Style.FILL
    }
    val paintBorder = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = borderWidth
    }
    val paintLine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = lineWidth
    }

    points.forEachIndexed { idx, p ->
        val latitude = p.coordinate.latitude
        val longitude = p.coordinate.longitude

        val caption = buildString {
            append(idx + 1)
            p.title?.let { append(":$it") }
            p.time?.text?.let { append("\n$it") }
        }

        val paintTxt = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = fontPx
            isFakeBoldText = true
        }
        val lines = caption.split("\n")
        val textW = lines.maxOf { paintTxt.measureText(it) }
        val fm = paintTxt.fontMetrics
        val lineH = fm.descent - fm.ascent
        val textH = lineH * lines.size

        val (rectWidth, rectHeight) = Pair(textW + padding * 2, textH + padding * 2)
        val (halfWidth, halfHeight) = Pair(rectWidth/2, rectHeight/2)
        val (bmpWidth, bmpHeight) = Pair(rectWidth + margin, rectHeight + margin)
        val bmp = createBitmap(bmpWidth.toInt(), bmpHeight.toInt())
        val canvas = Canvas(bmp)
        val point = projection.toScreenLocation(LatLng(latitude, longitude))
        val (x, y) = Pair(point.x.toFloat(), point.y.toFloat())
        for (direction in directions) {
            val (dx, dy) = direction
            val (shiftX, shiftY) = Pair(x + dx * margin, y + dy * margin)
            val (centerX, centerY) = Pair(shiftX + dx * halfWidth, shiftY  + dy * halfHeight)
            val bounds = RectF(
                centerX - halfWidth,
                centerY - halfHeight,
                centerX + halfWidth,
                centerY + halfHeight
            )

            if (placedRects.any {
                it.intersects(bounds)
            }){
                continue
            }
            placedRects.add(bounds)

            val backgroundRect = RectF(
                margin * (1 + dx) / 2,
                margin * (1 + dy) / 2,
                rectWidth + margin * (1 + dx) / 2,
                rectHeight + margin * (1 + dy) / 2,
            )
            val strokeRect = RectF(
                margin * (1 + dx) / 2 + borderWidth/2,
                margin * (1 + dy) / 2  + borderWidth/2,
                rectWidth + margin * (1 + dx) / 2  - borderWidth/2,
                rectHeight + margin * (1 + dy) / 2 - borderWidth/2,
            )
            canvas.drawRoundRect(backgroundRect, 0f, 0f, paintBackground)
            canvas.drawRoundRect(strokeRect, 0f, 0f, paintBorder)
            canvas.drawLine(
                if(dx > 0) 0f else bmpWidth,
                if(dy > 0) 0f else bmpHeight,
                if(dx > 0) margin else rectWidth,
                if(dy > 0) margin else rectHeight,
                paintLine
            )
            // ───── 5) 文字描画 ─────
            lines.forEachIndexed { i, line ->
                val x = padding +  if(dx > 0) margin else 0f
                val y = padding +  (if(dy > 0) margin else 0f) - fm.ascent + lineH * i
                canvas.drawText(line, x, y, paintTxt)
            }

            addMarker(
                MarkerOptions()
                    .position(LatLng(p.coordinate.latitude, p.coordinate.longitude))
                    .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                    .anchor((1 - dx).toFloat()/2, (1 - dy).toFloat()/2)
                    .zIndex(3f)
            )

            // 小さい赤丸ピン (基点)
            addMarker(
                MarkerOptions()
                    .position(LatLng(p.coordinate.latitude, p.coordinate.longitude))
                    .icon(descriptor)
                    .anchor(0.5f, 0.5f)  // 中央に配置
            )
            break
        }
    }
}

fun createRedCircleBitmap(context: Context, diameter: Int = 12): Bitmap {
    val bitmap = createBitmap(diameter, diameter)
    val canvas = Canvas(bitmap)
    val paint = Paint().apply {
        color = Color.RED
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    val radius = diameter / 2f
    canvas.drawCircle(radius, radius, radius, paint)
    return bitmap
}

fun RectF.intersects(other: RectF): Boolean =
    !(other.left >= this.right   ||  // 完全に右
            other.right <= this.left   ||  // 完全に左
            other.top >= this.bottom   ||  // 完全に下
            other.bottom <= this.top)      // 完全に上