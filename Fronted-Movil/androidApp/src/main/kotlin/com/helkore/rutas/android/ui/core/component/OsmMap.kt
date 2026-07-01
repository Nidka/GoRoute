package com.helkore.rutas.android.ui.core.component

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.TileSourcePolicy
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

// ── Tile source oscuro (CartoDB Dark Matter — gratuito) ────────────────────
val DarkMatterTileSource = object : OnlineTileSourceBase(
    "CartoDB.DarkMatter",
    1, 19, 256, ".png",
    arrayOf(
        "https://a.basemaps.cartocdn.com/dark_all/",
        "https://b.basemaps.cartocdn.com/dark_all/",
        "https://c.basemaps.cartocdn.com/dark_all/"
    ),
    "© OpenStreetMap contributors © CARTO",
    TileSourcePolicy(
        2,
        TileSourcePolicy.FLAG_NO_BULK or TileSourcePolicy.FLAG_USER_AGENT_MEANINGFUL
    )
) {
    override fun getTileURLString(pMapTileIndex: Long): String {
        return baseUrl +
            MapTileIndex.getZoom(pMapTileIndex) + "/" +
            MapTileIndex.getX(pMapTileIndex)    + "/" +
            MapTileIndex.getY(pMapTileIndex)    + mImageFilenameEnding
    }
}

// ── Datos de ruta/marker ──────────────────────────────────────────────────
data class MapRoute(
    val points: List<GeoPoint>,
    val color: Color,
    val width: Float = 8f,
    val alpha: Float = 1f
)

data class MapMarker(
    val position:  GeoPoint,
    val title:     String = "",
    val snippet:   String = "",
    val color:     Color  = Color(0xFF7C3AED),
    val isTerminal: Boolean = false   // terminales → círculo grande con borde blanco
)

// ── Crear bitmap de círculo coloreado para el marker ─────────────────────
private fun makeCircleIcon(color: Int, radiusPx: Int, isTerminal: Boolean): Bitmap {
    val size = radiusPx * 2 + 8
    val bmp  = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    val cx = size / 2f
    val cy = size / 2f

    if (isTerminal) {
        // Borde blanco exterior
        canvas.drawCircle(cx, cy, radiusPx.toFloat(), Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = android.graphics.Color.WHITE
        })
        // Círculo de color interior
        canvas.drawCircle(cx, cy, (radiusPx - 3).toFloat(), Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
        })
        // Punto blanco al centro
        canvas.drawCircle(cx, cy, (radiusPx - 8).toFloat().coerceAtLeast(2f),
            Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = android.graphics.Color.WHITE })
    } else {
        // Círculo simple
        canvas.drawCircle(cx, cy, radiusPx.toFloat(), Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
        })
        // Borde blanco fino
        canvas.drawCircle(cx, cy, radiusPx.toFloat(), Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = android.graphics.Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
        })
    }
    return bmp
}

// ── Composable OsmMap ─────────────────────────────────────────────────────
@Composable
fun OsmMap(
    modifier:   Modifier    = Modifier,
    center:     GeoPoint    = GeoPoint(-8.1091, -79.0215),
    zoom:       Double      = 14.0,
    routes:     List<MapRoute>   = emptyList(),
    markers:    List<MapMarker>  = emptyList(),
    onMapReady: ((MapView) -> Unit)? = null
) {
    val context = LocalContext.current

    // Configurar osmdroid user agent (obligatorio)
    Configuration.getInstance().apply {
        userAgentValue = context.packageName
        load(context, context.getSharedPreferences("osm_pref", 0))
    }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(DarkMatterTileSource)
            setMultiTouchControls(true)
            isTilesScaledToDpi = true
            minZoomLevel = 5.0
            maxZoomLevel = 20.0
            controller.setZoom(zoom)
            controller.setCenter(center)
            overlayManager.tilesOverlay.loadingBackgroundColor = android.graphics.Color.TRANSPARENT
            overlayManager.tilesOverlay.loadingLineColor       = android.graphics.Color.TRANSPARENT
        }
    }

    // Notificar que el mapa está listo la primera vez
    val mapReadyNotified = remember { booleanArrayOf(false) }

    // Actualizar overlays cuando cambian rutas/marcadores
    AndroidView(
        factory  = { mapView },
        modifier = modifier,
        update   = { mv ->
            mv.overlays.clear()

            // ── Polylines ───────────────────────────────────────────────
            routes.forEach { route ->
                if (route.points.size >= 2) {
                    val polyline = Polyline(mv).apply {
                        setPoints(route.points)
                        outlinePaint.apply {
                            color       = route.color.copy(alpha = route.alpha).toArgb()
                            strokeWidth = route.width
                            isAntiAlias = true
                            strokeCap   = Paint.Cap.ROUND
                            strokeJoin  = Paint.Join.ROUND
                        }
                    }
                    mv.overlays.add(polyline)
                }
            }

            // ── Markers ─────────────────────────────────────────────────
            markers.forEach { markerData ->
                val radiusPx = if (markerData.isTerminal) 14 else 8
                val bmp      = makeCircleIcon(markerData.color.toArgb(), radiusPx, markerData.isTerminal)
                val icon     = BitmapDrawable(context.resources, bmp)

                val marker = Marker(mv).apply {
                    position  = markerData.position
                    title     = markerData.title
                    snippet   = markerData.snippet
                    this.icon = icon
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                }
                mv.overlays.add(marker)
            }

            mv.invalidate()

            if (!mapReadyNotified[0]) {
                mapReadyNotified[0] = true
                onMapReady?.invoke(mv)
            }
        }
    )

    DisposableEffect(Unit) {
        onDispose { mapView.onDetach() }
    }
}

// ── Utilidad para parsear colorHex con o sin '#' ──────────────────────────
fun parseColorHex(hex: String, fallback: Color = Color(0xFF7C3AED)): Color =
    runCatching {
        val clean = if (hex.startsWith("#")) hex else "#$hex"
        Color(android.graphics.Color.parseColor(clean))
    }.getOrDefault(fallback)
