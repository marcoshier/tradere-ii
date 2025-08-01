import org.openrndr.application
import org.openrndr.ffmpeg.loadVideo
import video.loadVideo

fun main() {
    application {
        configure {
            width = 1280
            height = 720
        }

        program {

            val video = loadVideo("data/video/stendalidocumentario/stendalidocumentario0.mp4")
            video.play()

            val video2 = loadVideo("data/video/stendalidocumentario/stendalidocumentario1.mp4")
            video2.play()

            keyboard.character.listen {
                when(it.character) {
                    'a' -> video.pause()
                    's' -> video.resume()
                    'd' -> video2.pause()
                    'f' -> video2.resume()
                }
            }

            extend {
                video.draw(drawer)
                video2.draw(drawer)


            }
        }
    }
}