package audio

import io.github.oshai.kotlinlogging.KotlinLogging
import org.lwjgl.openal.AL10.*
import org.lwjgl.openal.ALC10.*

private val logger = KotlinLogging.logger { }

fun checkALError(taskName: String = "", exception: Boolean = true) {

    val error = alGetError()
    val context = alcGetCurrentContext()

    //AL11.alGetBoolean(AL_DOPPLER_FACTOR)
    val errorName = when (error) {
        AL_INVALID_NAME -> "AL_INVALID_NAME"
        AL_INVALID_ENUM -> "AL_INVALID_ENUM"
        AL_INVALID_VALUE -> "AL_INVALID_VALUE"
        AL_INVALID_OPERATION -> "AL_INVALID_OPERATION"
        AL_OUT_OF_MEMORY -> "AL_OUT_OF_MEMORY"
        else -> "unknown error ${String.format("%x", error)}"
    }
    if (error != 0) {
        logger.error { "OpenAL error: <context: $context> [$taskName] $errorName" }
    }
    if (exception) {
        require(error == AL_NO_ERROR) {
            "OpenAL error: <context: $context> [$taskName] $errorName"
        }
    }
}

fun checkALCError(device: Long, taskName: String = "") {

    val error = alcGetError(device)
    require(error == ALC_NO_ERROR) {
        val errorName = when (error) {
            ALC_INVALID_DEVICE -> "ALC_INVALID_DEVICE"
            ALC_INVALID_CONTEXT -> "ALC_INVALID_CONTEXT"
            ALC_INVALID_ENUM -> "ALC_INVALID_ENUM"
            ALC_INVALID_VALUE -> "ALC_INVALID_VALUE"
            ALC_OUT_OF_MEMORY -> "ALC_OUT_OF_MEMORY"

            else -> "unknown error ${String.format("%x", error)}"
        }
        logger.error { "OpenALC error: [$taskName] $errorName" }
        "OpenALC error: [$taskName] $errorName"
    }
}