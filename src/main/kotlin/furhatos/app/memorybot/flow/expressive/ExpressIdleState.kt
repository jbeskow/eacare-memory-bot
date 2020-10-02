package furhatos.app.memorybot.flow.expressive

import furhatos.app.memorybot.gestures.*
import furhatos.app.memorybot.log
import furhatos.flow.kotlin.*
import furhatos.gestures.Gestures
import furhatos.records.Location

val ExpressIdleState : State = state {
    onEntry {
        log.stateEnter(thisState)
    }
    onTime(repeat = 3000) {
        val randomXCoordinate : Double = ((40..80).random() * listOf(-1, 1).random()).toDouble() / 100
        val randomDuration : Int = (5..20).random() * 100
        println("glancing at $randomXCoordinate for $randomDuration MS")
        furhat.glance(Location(randomXCoordinate, 0.0, 3.0), duration = randomDuration)
    }
    include(MonitorSpeechLogger)
    include(ManualExpressEventListeners)
}

val ExpressSpeakingState : State = state {
    onEntry {
        log.stateEnter(thisState)
    }
    onTime(repeat = 4000) {
        val rollTo = (5..12).random() * listOf(1, -1).random()
        furhat.gesture(NeckRollTo(rollTo), async = true)
    }
    include(MonitorSpeechLogger)
    include(ManualExpressEventListeners)
    onExit {
        furhat.gesture(NeckRollReset())
    }
}

val ExpressListeningState : State = state {
    onEntry {
        log.stateEnter(thisState)
    }
    onTime(repeat = 5000) {
        val randomUtterance = random (
            utterance { + eyeSquint(0.3) },
            utterance {
                + Gestures.Smile(duration = 3.0)
                + nodForwardGesture(duration = 2.0)
            },
            utterance {
                + Gestures.BrowRaise(duration = 3.0)
                + nodBackwardGesture(duration = 2.0)
            }
        )
        furhat.say(randomUtterance)
    }
    onExit {
        furhat.gesture(NeckRollReset())
    }
    include(MonitorSpeechLogger)
    include(ManualExpressEventListeners)
}
