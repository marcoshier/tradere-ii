import audio.AudioDeviceService
import org.openrndr.Program
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.ffmpeg.fileWithoutExtension
import video.VideoPlayerFFMPEG
import video.loadVideo
import java.io.File

class LightLeaks(val program: Program, val audioDeviceService: AudioDeviceService) {

    var video: VideoPlayerFFMPEG? = null
    var videoFile: File? = null

    fun set(chapter: String) {
        videoFile = if (debug) {
            File("data/video/stendali/stendalistroboloop.mp4")
        } else {
            File("data/video/$chapter/").listFiles()!!.filter { it.isFile }.also { println(it.map { it.fileWithoutExtension }) }.find {
                it.nameWithoutExtension.endsWith("loop")
            }

        }
        play()
    }

    var startTime = program.seconds
    var playing = false
    var closed = true

    private fun play() {

        video?.dispose()
        val audioDevice = audioDeviceService.audioDevice("main")

        video = program.loadVideo(audioDevice, videoFile!!.path)
        startTime = program.seconds
        video?.play()
        closed = false
        playing = true
    }

    fun close() {
        if (!closed) {
            playing = false
            startTime = program.seconds
            video?.pause()
            video?.dispose()
            video = null
            closed = true
        }
    }


    fun draw() {
        video?.draw(program.drawer)

        if (program.seconds - startTime > (video?.duration ?: 10.0) && playing) {
            close()
        }
    }


}