import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.imageFit.imageFit
import org.openrndr.extra.noise.uniform
import org.openrndr.extra.shapes.primitives.grid
import org.openrndr.ffmpeg.loadVideo
import video.loadVideo
import java.io.File

fun main() {
    application {
        configure {
            width = 1280
            height = 720
        }

        program {

            val videos1 = File("data/video/pianetaacciaiodocumentario").listFiles()!!.filter { it.isFile }
            val videos2 = File("data/video/poveragentedocumentario").listFiles()!!.filter { it.isFile }
            val videos3 = File("data/video/stendalidocumentario").listFiles()!!.filter { it.isFile }

            val videos = (videos1 + videos2 + videos3).map {
                loadVideo(it.path)
            }

            for (video in videos) {
                video.play()
            }

            var paused = false
            var pausedIdx = 0

            keyboard.character.listen {
                when(it.character) {
                    'a' -> {
                        pausedIdx = Int.uniform(0, videos.size)
                        videos[pausedIdx].pause()
                        paused = true
                    }
                    's' -> {
                        videos[pausedIdx].resume()
                        paused = false
                    }
                }
            }

            val grid = drawer.bounds.grid(5, 5).flatten()


            extend {
                for ((i, video) in videos.withIndex()) {
                    val rect = grid[i]
                    video.draw(drawer, true)
                    video.colorBuffer?.let {
                        drawer.imageFit(it, rect)
                    }

                    if (i == pausedIdx && paused) {
                        drawer.stroke = null
                        drawer.fill = ColorRGBa.RED.opacify(0.5)
                        drawer.rectangle(rect)
                    }
                }

            }
        }
    }
}