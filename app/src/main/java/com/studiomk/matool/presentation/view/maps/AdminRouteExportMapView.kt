package com.studiomk.matool.presentation.view.maps

import android.graphics.Canvas as AndroidCanvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.studiomk.matool.core.binding.Binding
import com.studiomk.matool.domain.entities.routes.Point
import com.studiomk.matool.domain.entities.routes.Segment
import com.studiomk.matool.domain.entities.shared.Coordinate
import com.studiomk.matool.presentation.utils.SimpleRegion
import com.studiomk.matool.domain.entities.shared.text
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import androidx.core.graphics.createBitmap
import com.studiomk.matool.presentation.utils.makeRegion

@Composable
fun AdminRouteExportMapView(
    points: List<Point>,
    segments: List<Segment>,
    region: Binding<SimpleRegion?>,
    modifier: Modifier = Modifier,
    onChangeWholeSnapshot: (ImageBitmap)->Unit,
    onChangePartialSnapshot: (ImageBitmap)->Unit
) {
    // スナップショット用の状態
     var cameraPositionState = rememberSyncedCameraState(region)
     LaunchedEffect(Unit) {
        val whole = createRouteExportSnapshotBitmap(
            points = points,
            segments = segments,
            region = makeRegion(points.map { it.coordinate }, ratio = 1.4),
            widthPx = 594,
            heightPx = 420
        )
         onChangeWholeSnapshot(whole)
    }

    LaunchedEffect(region.get()) {
        region.get().let {
            val partial = createRouteExportSnapshotBitmap(
                points = points,
                segments = segments,
                region = it,
                widthPx = 594,
                heightPx = 420
            )
            onChangePartialSnapshot(partial)
        }
    }
    

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
fun createRouteExportSnapshotBitmap(
    points: List<Point>,
    segments: List<Segment>,
    region: SimpleRegion?,
    widthPx: Int = 594,
    heightPx: Int = 420
): ImageBitmap {
    val bounds = region?.toLatLngBounds() ?: calculateBounds(points.map { it.coordinate })
    val minLat = bounds.southwest.latitude
    val maxLat = bounds.northeast.latitude
    val minLng = bounds.southwest.longitude
    val maxLng = bounds.northeast.longitude

    fun geoToCanvas(lat: Double, lng: Double): Pair<Float, Float> {
        val x = ((lng - minLng) / (maxLng - minLng)) * widthPx
        val y = ((maxLat - lat) / (maxLat - minLat)) * heightPx
        return x.toFloat() to y.toFloat()
    }

    val bitmap = createBitmap(widthPx, heightPx)
    val canvas = AndroidCanvas(bitmap)
    canvas.drawColor(AndroidColor.WHITE)

    // ポリライン（白縁→青線）
    val paintWhite = Paint().apply {
        color = AndroidColor.WHITE
        strokeWidth = 8f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    val paintBlue = Paint().apply {
        color = AndroidColor.rgb(52, 152, 219)
        strokeWidth = 6f
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
    val pinRadius = 6f
    val fontSize = 24f
    val padding = 4f
    val margin = 12f
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
        strokeWidth = 1.5f
        isAntiAlias = true
    }
    val paintLine = Paint().apply {
        color = AndroidColor.RED
        strokeWidth = 2f
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
        val textBounds = android.graphics.Rect()
        paintText.getTextBounds(caption, 0, caption.length, textBounds)
        val textWidth = textBounds.width().toFloat()
        val textHeight = textBounds.height().toFloat()
        val halfWidth = textWidth / 2 + padding
        val halfHeight = textHeight / 2 + padding

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
                val lines = caption.split("\n")
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

    return bitmap.asImageBitmap()
}

// 地理座標リストからLatLngBoundsを計算
fun calculateBounds(coords: List<Coordinate>): LatLngBounds {
    val builder = LatLngBounds.builder()
    coords.forEach { builder.include(LatLng(it.latitude, it.longitude)) }
    return builder.build()
}

