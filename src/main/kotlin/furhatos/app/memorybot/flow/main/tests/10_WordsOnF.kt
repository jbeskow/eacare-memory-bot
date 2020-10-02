package furhatos.app.memorybot.flow.main.tests

import furhatos.app.memorybot.*
import furhatos.app.memorybot.flow.main.*
import furhatos.app.memorybot.phrases.*
import furhatos.app.memorybot.utterances.acknowledgement
import furhatos.app.memorybot.utterances.thenLetsStart
import furhatos.flow.kotlin.*

const val WORDS_ON_F_LISTEN_TIME = (60 * 1000)

val WordsOnF : State = state {
    onEntry {
        log.stateEnter(thisState)
        withReentryVariation(listOf(WORDS_ON_F_1a, WORDS_ON_F_1b, WORDS_ON_F_1c)) { furhat.botSay(it) }
        if (call(DoYouUnderstandState) as Boolean) {
            furhat.botSay(thenLetsStart)
            goto(WordsOnFStart)
        } else { reentry() }
    }
    include(CommandBehavior)
    include(BasicBehaviour)
}

val WordsOnFStart : State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botSay(WORDS_ON_F_2)
        startListenTimer(this, listenTimeMs = WORDS_ON_F_LISTEN_TIME)
        while (listenTimerTimeLeft(this) > 0) {
            val timeLeft = listenTimerTimeLeft(this)
            call(createLongListen(timeLeft))
        }

        furhat.botSay(acknowledgement)
        furhat.botSay(WORDS_ON_F_3)
        gotoNextTest(SematicTest)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
}
