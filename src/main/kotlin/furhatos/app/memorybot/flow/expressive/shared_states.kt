package furhatos.app.memorybot.flow.expressive

import furhatos.app.memorybot.*
import furhatos.app.memorybot.gestures.nodBackwardGesture
import furhatos.app.memorybot.gestures.nodForwardGesture
import furhatos.event.monitors.MonitorListenStart
import furhatos.event.monitors.MonitorSpeechEnd
import furhatos.event.monitors.MonitorSpeechStart
import furhatos.event.senses.SenseSpeechEnd
import furhatos.event.senses.SenseSpeechStart
import furhatos.flow.kotlin.*
import furhatos.gestures.Gesture
import furhatos.gestures.Gestures


val ManualExpressEventListeners = partialState {
    onEvent<FurhatExpressing>(instant  = false) {
        if (furhat.isListening) {
            goto(ExpressListeningState)
        } else {
            goto(ExpressSpeakingState)
        }
    }
    onEvent<FurhatListening>(instant  = false) {
        goto(ExpressListeningState)
    }
    onEvent<FurhatSpeaking>(instant  = false) {
        goto(ExpressSpeakingState)
    }
    onEvent<FurhatIdle>(instant  = false) {
        goto(ExpressIdleState)
    }
    onEvent<AcknowledgmentGesture>(instant = true) {
        val gesture : Gesture? = random (
                nodForwardGesture(1.0),
                nodBackwardGesture(1.0),
                Gestures.Smile,
                null
        )
        if (gesture != null) {
            furhat.gesture(gesture, async = true, priority = 10)
        }
    }
}

val MonitorSpeechLogger = partialState {
    onEvent<MonitorSpeechStart> {
        log.furhatSaid(it)
        propagate()
    }
}
