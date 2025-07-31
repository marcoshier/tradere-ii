import audio.AudioDeviceService
import org.openrndr.Program
import org.openrndr.events.Event
import video.VideoPlayerFFMPEG
import video.loadVideo
import java.io.File

class VideoService(val program: Program, val audioDeviceService: AudioDeviceService) {

    val ended = Event<Unit>()

    var video: VideoPlayerFFMPEG? = null
    var videoFile: File? = null


    fun set(chapter: String, subchapter: Int) {
        videoFile = if (debug) {
            File("data/video/stendali/stendalidocumentario0$subchapter.mp4")
        } else {
            File("data/video/$chapter/${chapter}0$subchapter.mp4")
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
            ended.trigger(Unit)
        }
    }


}