package com.kpri.binasejahtera.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.kpri.binasejahtera.R
import com.kpri.binasejahtera.ui.theme.InfoGreen
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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

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
                icon = ContextCompat.getDrawable(context, R.drawable.ic_map)
            }
            map.overlays.add(marker)

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