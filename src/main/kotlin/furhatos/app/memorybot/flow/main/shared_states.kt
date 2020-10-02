package furhatos.app.memorybot.flow.main

import furhatos.app.memorybot.*
import furhatos.app.memorybot.flow.main.tests.*
import furhatos.app.memorybot.nlu.intents.*
import furhatos.app.memorybot.phrases.*
import furhatos.app.memorybot.utterances.*
import furhatos.flow.kotlin.*

val BasicBehaviour = partialState {
    include(UserHandling)
    include(RepeatBehaviour)
    include(NoResponseBehaviour)
    include(DebugButtons)
}

val UserHandling = partialState {
    onUserLeave(instant = true) {
        if (users.count > 0) {
            if (it == users.current) {
                furhat.attend(users.other)
            } else {
                furhat.glance(it)
            }
        } else {
            goto(UserMaybeGone(thisState))
        }
    }
    onUserEnter(instant = true) {
        if (users.current.isVisible) {
            furhat.glance(it)
        } else {
            furhat.attend(it)
        }
    }
}

val RepeatBehaviour = partialState {
    onResponse<Repeat> {
        log.userSaid(it)
        send("Repeat")
    }
    onEvent("Repeat") {
        furhat.botSay("Jo, jag sa.")
        reentry()
    }
}
val NoResponseBehaviour = partialState {
    var noResponseCount = 0;
    onNoResponse {
        noResponseCount++
        when  {
            noResponseCount in 1..3 -> furhat.botAsk(canYouRepeat, timeout=7000)
            noResponseCount < 6 -> furhat.listen(7000)
            else -> goto(Goodbye)
        }
    }
}

fun createLongListen(listenTimeMs: Int = 55000) : State {
    return state {
        onEntry {
            log.stateEnter(thisState)
            furhat.listen(listenTimeMs, endSil = 10000)
        }
        onNoResponse { terminate() }
        onResponse { terminate() }
    }
}

val DoYouUnderstandState = state() {
    onEntry {
        log.stateEnter(thisState)
        furhat.botAsk(doYouUnderstand)
    }
    onResponse<Yes> {
        log.userSaid(it)
        terminate(true)
    }
    onResponse<Wait> {
        log.userSaid(it)
        val ready = call(WaitForUserToBeReadyState) as Boolean
        if (ready) {
            terminate(true)
        } else {
            terminate(false)
        }
    }
    onResponse<No> {
        log.userSaid(it)
        terminate(false)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
}

val WaitForUserToBeReadyState = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.say("Säg till när du vill fortsätta.")
        furhat.listen()
    }
    onResponse<StartContinue> {
        log.userSaid(it)
        terminate(true)
    }
    onResponse<Wait> {
        log.userSaid(it)
        getWaitUtterance(this)
    }
    onNoResponse { furhat.listen() }
    onResponse {
        furhat.botAskYN(areYouReady) {
            onResponse<Yes> {
                log.userSaid(it)
                terminate(true)
            }
            onResponse<No> {
                log.userSaid(it)
                getWaitUtterance(this)
                reentry()
            }
        }
    }
    onUserLeave { furhat.listen() }
    include(CommandBehavior)
    include(BasicBehaviour)
}

val CommandBehavior = partialState {
    onResponse<DoctorGotoNumber> {
        handlePauseResponse(it.intent.x?.value)
    }
    onResponse<DoctorGotoOrdinal> {
        handlePauseResponse(it.intent.x?.value)
    }
    onResponse<DoctorPause> {
        goto(Paused(thisState))
    }
    onResponse<DoctorRestart> {
        furhat.botSay(COMMAND_RESTART)
        goto(Intro)
    }
}

fun TriggerRunner<*>.handlePauseResponse(testNumber : Int?) {
    if (testNumber is Int && testsMap.containsKey(testNumber)) {
        furhat.say("Javisst, vi går till test nummer ${testNumber}.")
        goto(testsMap[testNumber]!!)
    } else {
        furhat.say("Jag hittar inte det testet..")
        reentry()
    }
}
