package furhatos.app.memorybot.gestures

import furhatos.gestures.BasicParams
import furhatos.gestures.Gesture
import furhatos.gestures.defineGesture

// Looks
fun lookTopRightGesture(strength: Double = 1.0, duration: Double = 1.0) : Gesture = defineGesture("LookRightUp", strength, duration) {
    frame(0.5, 1.5) {
        BasicParams.GAZE_PAN to -20.0
        BasicParams.GAZE_TILT to -20.0
        BasicParams.NECK_PAN to -20.0
        BasicParams.NECK_ROLL to -10.0
        BasicParams.NECK_TILT to -5
    }
    reset(2.0)
}

fun lookBottomLeftGesture(strength: Double = 1.0, duration: Double = 1.0) : Gesture = defineGesture("LookRightUp", strength, duration) {
    frame(0.5, 1.5) {
        BasicParams.GAZE_PAN to 20.0
        BasicParams.GAZE_TILT to 20.0
        BasicParams.NECK_PAN to 20.0
        BasicParams.NECK_ROLL to 10.0
        BasicParams.NECK_TILT to 5
    }
    reset(2.0)
}

fun lookBottomRightGesture(strength: Double = 1.0, duration: Double = 1.0) : Gesture = defineGesture("LookRightUp", strength, duration) {
    frame(0.5, 1.5) {
        BasicParams.GAZE_PAN to 20.0
        BasicParams.GAZE_TILT to -20.0
        BasicParams.NECK_PAN to -20.0
        BasicParams.NECK_ROLL to 10.0
        BasicParams.NECK_TILT to 5
    }
    reset(2.0)
}

fun lookTopLeftGesture(strength: Double = 1.0, duration: Double = 1.0) : Gesture = defineGesture("LookLeftUp", strength, duration) {
    frame(0.5, 1.5) {
        BasicParams.GAZE_PAN to -20.0
        BasicParams.GAZE_TILT to 20.0
        BasicParams.NECK_PAN to 20.0
        BasicParams.NECK_ROLL to -10.0
        BasicParams.NECK_TILT to -5
    }
    reset(2.0)
}

// Tilts
fun NeckRollTo(value : Int, strength: Double = 1.0, duration: Double = 1.0) : Gesture = defineGesture("NeckRoll", strength, duration) {
    frame(0.5, 2.5) {
        BasicParams.NECK_ROLL to value
    }
    reset(3.5)
}
fun NeckRollReset() : Gesture = defineGesture("NeckRollReset") {
    frame(0.5, 2.5) {
        BasicParams.NECK_ROLL to 0
    }
}


// Brow down
fun eyeSquint(strength: Double = 1.0, duration: Double = 1.0) : Gesture = defineGesture("HeadTiltRight", strength, duration) {
    frame(0.5, 1.5) {
        BasicParams.EYE_SQUINT_LEFT to 10
        BasicParams.EYE_SQUINT_RIGHT to 10
    }
    reset(2.0)
}

// Nods
fun nodForwardGesture(strength: Double = 1.0, duration: Double = 1.0) : Gesture = defineGesture("NodForward", strength, duration) {
    frame(0.0, 0.5) {
        BasicParams.NECK_TILT to 8
    }
    reset(1.0)
}

fun nodBackwardGesture(strength: Double = 1.0, duration: Double = 1.0) : Gesture = defineGesture("NodForward", strength, duration) {
    frame(0.0, 0.5) {
        BasicParams.NECK_TILT to -8
    }
    reset(1.0)
}


