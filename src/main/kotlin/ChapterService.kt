import io.github.oshai.kotlinlogging.KotlinLogging
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer

private val logger = KotlinLogging.logger {  }

class ChapterService() {

    val chapters = listOf(
        "stendalidocumentario",
        "poveragentedocumentario",
        "pianetaacciaiodocumentario",
    )

    var currentChapter = 0



    val subchapters = listOf(
        listOf(0, 1, 2),
        listOf(0, 1),
        listOf(0, 1, 2)
    )

    var currentSubchapter = 0


    fun step() {

        val nextSubchapter = currentSubchapter + 1

        if (nextSubchapter > subchapters[currentChapter].last()) {
            val nextChapter = (currentChapter + 1).mod(chapters.size)
            logger.info { "Stepping to next chapter $nextChapter" }
            currentChapter = nextChapter
            currentSubchapter = 0
            return
        }

        currentSubchapter = nextSubchapter
    }



    fun debugView(drawer: Drawer) {
        drawer.stroke = null
        drawer.fill = ColorRGBa.RED

        for (i in 0 until chapters.size) {
            drawer.fill = if (i == currentChapter) ColorRGBa.YELLOW else ColorRGBa.RED
            drawer.rectangle(i * 40.0, 20.0, 30.0, 30.0)
        }

        for (i in 0 until subchapters.size) {
            for (j in 0 until subchapters[i].size) {
                drawer.fill = if (j == currentSubchapter && i == currentChapter) ColorRGBa.YELLOW else ColorRGBa.RED
                drawer.rectangle(j * 40.0, i * 35.0 + 60.0, 30.0, 30.0)
            }
        }
    }

}