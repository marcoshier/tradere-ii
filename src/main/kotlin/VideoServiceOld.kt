import audio.AudioDeviceService
import org.openrndr.Program
import org.openrndr.events.Event
import org.openrndr.extra.imageFit.imageFit
import video.VideoPlayerFFMPEG
import video.loadVideo
import java.io.File

class VideoServiceOld(val program: Program, val audioDeviceService: AudioDeviceService) {

    val ended = Event<Unit>()

    var video: VideoPlayerFFMPEG? = null
    var videoFile: File? = null


    fun setVideo(chapter: String, subchapter: Int) {
        videoFile = if (debug) {
            File("data/video/stendali/stendalidocumentario${subchapter}.mp4")
        } else {
            File("data/video/$chapter/${chapter}${subchapter}.mp4")
        }
    }

    fun setLights(chapter: String) {
        videoFile = if (debug) {
            File("data/video/stendali/stendalistroboloop.mp4")
        } else {
            File("data/video/$chapter/").listFiles()!!.filter { it.isFile }.find {
                it.nameWithoutExtension.endsWith("loop")
            }
        }
    }

    var startTime = program.seconds
    var playing = false
    var closed = true

    fun play() {
        finished = false
        println("playing")
        video?.dispose()
        val audioDevice = audioDeviceService.audioDevice("main")

        video = program.loadVideo(audioDevice, videoFile!!.path)
        startTime = program.seconds
        video?.play()
        closed = false
        playing = true
    }

    fun pause() {
        println("pausing")
        playing = false
        lastTimestamp = video?.position ?: 0.0
        video?.pause()
    }

    fun resume() {
        println("resuming $lastTimestamp")
        play()
        video?.pause()
        video?.seek(lastTimestamp)
        Thread.sleep(1000)
        video?.resume()
    }

    fun close() {
        println("closing")
        if (!closed) {
            playing = false
            startTime = program.seconds
            video?.pause()
            video?.dispose()
            video = null
            closed = true
        }
    }

    var lastTimestamp = 0.0
    var finished = false

    fun draw() {
        video?.draw(program.drawer, true)

        video?.colorBuffer?.let {
            program.drawer.imageFit(it, program.drawer.bounds)
        }

        if (program.seconds - startTime > (video?.duration ?: 10.0) && playing) {
            finished = true
            close()
            lastTimestamp = 0.0
            ended.trigger(Unit)
        }
    }


}