package com.kpri.binasejahtera.ui.components

import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable
import com.kpri.binasejahtera.R
import com.kpri.binasejahtera.ui.theme.InfoBlue
import com.kpri.binasejahtera.ui.theme.InfoGreen
import com.kpri.binasejahtera.ui.theme.PrimaryBlack
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

@Composable
fun KpriOsmMap(
    latitude: Double,
    longitude: Double,
    radiusMeter: Double,
    userLatitude: Double? = null,
    userLongitude: Double? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val primaryBlackInt = PrimaryBlack.toArgb()

    // titik user (kaya google)
    val userDotDrawable = remember {
        val radius = 20f
        val bitmap = createBitmap((radius * 2).toInt(), (radius * 2).toInt())
        val canvas = Canvas(bitmap)
        val paint = Paint().apply { isAntiAlias = true }

        paint.color = android.graphics.Color.WHITE
        canvas.drawCircle(radius, radius, radius, paint)

        paint.color = InfoBlue.toArgb()
        canvas.drawCircle(radius, radius, radius - 4f, paint)

        bitmap.toDrawable(context.resources)
    }

    // inisialisasi mapview
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(18.0)
        }
    }

    // pake AndroidView buat render MapView di dalam Compose
    AndroidView(
        factory = { mapView },
        modifier = modifier.fillMaxSize(),
        update = { map ->
            val officePoint = GeoPoint(latitude, longitude)

            map.controller.setCenter(officePoint)

            map.overlays.clear()

            // lingkaran safezone
            val circle = Polygon().apply {
                points = Polygon.pointsAsCircle(officePoint, radiusMeter)
                fillPaint.color = InfoGreen.copy(alpha = 0.25f).toArgb()
                outlinePaint.color = InfoGreen.toArgb()
                outlinePaint.strokeWidth = 3f
            }
            map.overlays.add(circle)

            // pin kantor
            val marker = Marker(map).apply {
                position = officePoint
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = "Lokasi Kantor"
                val iconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_map)
                iconDrawable?.setTint(primaryBlackInt)
                icon = iconDrawable
            }
            map.overlays.add(marker)

            if (userLatitude != null && userLongitude != null && userLatitude != 0.0) {
                val userPoint = GeoPoint(userLatitude, userLongitude)

                val userHalo = Polygon().apply {
                    points = Polygon.pointsAsCircle(userPoint, 15.0)
                    fillPaint.color = InfoBlue.copy(alpha = 0.25f).toArgb()
                    outlinePaint.color = InfoBlue.toArgb()
                }
                map.overlays.add(userHalo)

                val userMarker = Marker(map).apply {
                    position = userPoint
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    title = "Lokasi Anda"
                    icon = userDotDrawable
                }
                map.overlays.add(userMarker)
            }

            // refresh map biar gambarnya muncul
            map.invalidate()
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            mapView.onDetach()
        }
    }
}