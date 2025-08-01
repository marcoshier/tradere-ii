import org.lwjgl.openal.ALC11.ALC_ALL_DEVICES_SPECIFIER
import org.lwjgl.openal.ALUtil
import org.lwjgl.system.MemoryUtil
import org.openrndr.application
import org.openrndr.draw.ColorBuffer
import org.openrndr.extra.imageFit.imageFit
import org.openrndr.launch
import kotlin.math.abs

var debug = false

fun main() {
    application {
        configure {
            width = 1920
            height = 1080
            hideCursor = true
            hideWindowDecorations = true
            windowAlwaysOnTop = true
        }

        program {

            println(
                ALUtil.getStringList(MemoryUtil.NULL, ALC_ALL_DEVICES_SPECIFIER)!!
            )

            val cs = ChapterService()
            val vs = VideoService(this, cs)
            val ts = TrackingService()

            var mode = "video"

            fun presence() {
                mode = "video"
                vs.loops[cs.currentChapter].pause()

                if (vs.videos[cs.currentChapter][cs.currentSubchapter].position != 0.0) {
                    vs.videos[cs.currentChapter][cs.currentSubchapter].resume()
                } else {
                    vs.videos[cs.currentChapter][cs.currentSubchapter].seek(0.0)
                    cs.step()
                }
            }

            fun absence() {
                mode = "lights"
                vs.videos[cs.currentChapter][cs.currentSubchapter].pause()

                Thread.sleep(1000)
                vs.loops[cs.currentChapter].seek(0.0)
            }

            launch {
                ts.startTracking()
            }

            ts.presence.listen {
                presence()
            }

            ts.absence.listen {
                absence()
            }

            keyboard.character.listen {
                when(it.character) {
                    'a' -> presence()
                    's' -> absence()
                }
            }


            extend {

                if (mode == "video") {
                    val video = vs.videos[cs.currentChapter][cs.currentSubchapter]
                    video.draw(drawer, true)

                    video.colorBuffer?.let {
                        drawer.imageFit(it, drawer.bounds)
                    }

                } else {
                    val video = vs.loops[cs.currentChapter]
                    video.draw(drawer, true)

                    video.colorBuffer?.let {
                        drawer.imageFit(it, drawer.bounds)
                    }
                }


                for ((i, videoGroup) in vs.videos.withIndex()) {
                    for ((j, video) in videoGroup.withIndex()) {
                        if (i == cs.currentChapter && j == cs.currentSubchapter && video.position >= video.duration - 3.0) {
                            video.seek(0.0)
                            cs.step()
                        }
                    }
                }


               // cs.debugView(drawer)
            }
        }
    }
}