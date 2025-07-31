import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.events.Event
import org.openrndr.extra.color.spaces.ColorOKHSLa
import org.openrndr.extra.color.tools.shiftHue
import org.openrndr.shape.IntRectangle
import org.openrndr.shape.Rectangle
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class TrackingService {

    val presence = Event<Unit>()
    val absence = Event<Unit>()


    var rectangles = listOf<Rectangle>()
        set(value) {
            if (field.isEmpty() && value.isNotEmpty()) {
                presence.trigger(Unit)
            } else if (field.isNotEmpty() && value.isEmpty()) {
                absence.trigger(Unit)
            }
            field = value
        }


    val serverUrl = "http://localhost:5000"

    val httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build()

    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun getRectangles(): TrackingResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val request = HttpRequest.newBuilder()
                    .uri(URI.create("$serverUrl/rectangles"))
                    .timeout(Duration.ofSeconds(3))
                    .GET()
                    .build()

                val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

                if (response.statusCode() == 200) {
                    json.decodeFromString<TrackingResponse>(response.body())
                } else {
                    println("HTTP Error: ${response.statusCode()}")
                    null
                }
            } catch (e: Throwable) {
                println("Error fetching rectangles: ${e.printStackTrace()}")
                null
            }
        }
    }

    suspend fun startTracking(updateIntervalMs: Long = 50) {
        while (true) {
            val response = getRectangles()
            response?.let {
                rectangles = it.rectangles.map {
                    IntRectangle(it.x, it.y, it.w, it.h).rectangle
                }
            }
            delay(updateIntervalMs)
        }
    }

    fun debugView(drawer: Drawer) {
        drawer.rectangles {
            for (i in 0 until rectangles.size) {
                this.stroke = ColorRGBa.RED.shiftHue<ColorOKHSLa>(i * 60.0)
                this.fill = null
                this.rectangle(rectangles[i])
            }
        }
    }

}