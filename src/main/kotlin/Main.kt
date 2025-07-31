import audio.AudioDeviceDescription
import audio.AudioDeviceService
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.launch
import org.openrndr.shape.IntRectangle

var debug = false

fun main() {
    application {
        configure {
            width = 1280
            height = 720
        }

        program {

            val cs = ChapterService()
            val ads = AudioDeviceService(mapOf(
                    "main" to AudioDeviceDescription("main", 0.0)
                )
            )
            val ts = TrackingService()
            val vs = VideoService(this, ads)
            val ll = LightLeaks(this, ads)


            var mode = "lights"

/*            if (debug) {
                keyboard.character.listen {
                    when(it.character) {
                        'a' -> ts.presence.trigger(Unit)
                        's' -> ts.absence.trigger(Unit)
                    }
                }
            } else {

            }*/

            launch {
                ts.startTracking()
            }

            ts.presence.listen {
                ll.close()
                cs.step()
                vs.set(cs.chapters[cs.currentChapter], cs.currentSubchapter)
                mode = "video"
            }

            ts.absence.listen {
                vs.close()
                ll.set(cs.chapters[cs.currentChapter])
                mode = "lights"
            }

            extend {

                if (mode == "video") {
                    vs.draw()
                } else {
                    ll.draw()
                }


                cs.debugView(drawer)
                ts.debugView(drawer)

            }
        }
    }
}