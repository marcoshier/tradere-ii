import org.openrndr.Program
import org.openrndr.ffmpeg.fileWithoutExtension
import org.openrndr.ffmpeg.loadVideo
import java.io.File

class VideoService(val program: Program, val chapterService: ChapterService) {

    val videos1 = File("data/video/stendalidocumentario").listFiles()!!.filter {
        !it.fileWithoutExtension.endsWith("loop")
    }.map { program.loadVideo(it.path) }
    val loop1 = program.loadVideo("data/video/stendalidocumentario/stendalistroboloop.mp4")

    val videos2 = File("data/video/poveragentedocumentario").listFiles()!!.filter {
        !it.fileWithoutExtension.endsWith("loop")
    }.map { program.loadVideo(it.path) }
    val loop2 = program.loadVideo("data/video/poveragentedocumentario/poveragenteloop.mp4")

    val videos3 = File("data/video/pianetaacciaiodocumentario").listFiles()!!.filter {
        !it.fileWithoutExtension.endsWith("loop")
    }.map { program.loadVideo(it.path) }
    val loop3 = program.loadVideo("data/video/pianetaacciaiodocumentario/pianetaacciaioloop.mp4")


    val videos = listOf(
        videos1, videos2, videos3
    )

    val loops = listOf(
        loop1, loop2, loop3
    )

    init {
        for (video in videos.flatten()) {
            video.play()
        }

        for (loop in loops) {
            loop.play()
        }
    }

}