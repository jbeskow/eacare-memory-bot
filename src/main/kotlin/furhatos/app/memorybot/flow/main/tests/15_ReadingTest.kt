package furhatos.app.memorybot.flow.main.tests

import furhatos.app.memorybot.*
import furhatos.app.memorybot.flow.main.*
import furhatos.app.memorybot.nlu.intents.No
import furhatos.app.memorybot.nlu.intents.Yes
import furhatos.app.memorybot.phrases.READING_TEST_1a
import furhatos.app.memorybot.phrases.READING_TEST_1b
import furhatos.app.memorybot.phrases.READING_TEST_2a
import furhatos.app.memorybot.phrases.READING_TEST_2b
import furhatos.app.memorybot.phrases.READING_TEST_3a
import furhatos.app.memorybot.phrases.READING_TEST_3b
import furhatos.app.memorybot.phrases.READING_TEST_4
import furhatos.app.memorybot.phrases.READING_TEST_5
import furhatos.app.memorybot.phrases.READING_TEST_6
import furhatos.app.memorybot.phrases.READING_TEST_7
import furhatos.app.memorybot.phrases.READING_TEST_8
import furhatos.app.memorybot.phrases.READING_TEST_9
import furhatos.app.memorybot.phrases.READING_TEST_10
import furhatos.app.memorybot.phrases.READING_TEST_11
import furhatos.app.memorybot.services.farmiService
import furhatos.app.memorybot.utterances.waitingUntilReady
import furhatos.flow.kotlin.*

const val READING_TEST_LISTEN_TIME = (60 * 1000)

val ReadingTest : State = state {
    onEntry {
        log.stateEnter(thisState)
        call { farmiService.gotoPage(thisState) }
        furhat.botSay(READING_TEST_1a)
        delay(750)
        furhat.botSay(READING_TEST_2a)
        startListenTimer(this, listenTimeMs = READING_TEST_LISTEN_TIME)
        while (listenTimerTimeLeft(this) > 0) {
            furhat.listen(READING_TEST_LISTEN_TIME)
        }
        reentry()
    }
    onReentry {
        while (listenTimerTimeLeft(this) > 0) {
            furhat.listen(READING_TEST_LISTEN_TIME)
        }
        val readingTestState = this
        furhat.botAskYN(READING_TEST_3a, timeout=55000) {
            onResponse<Yes> {
                log.userSaid(it)
                furhat.botSay(waitingUntilReady)
                startListenTimer(readingTestState)
                readingTestState.reentry()
            }
            onResponse<No> {
                log.userSaid(it)
                furhat.botSay(READING_TEST_4)
                goto(ReadingTestQuestion1)
            }
        }
    }
    onEvent("Repeat") {
        furhat.botSay(READING_TEST_1b)
        delay(1000)
        furhat.botSay(READING_TEST_2b)
        reentry()
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        val readingTestState = this
        furhat.botAskYN(READING_TEST_3b, timeout=55000) {
            onResponse<Yes> {
                log.userSaid(it)
                call { farmiService.hideScreen() }
                goto(ReadingTestQuestion1)
            }
            onResponse<No> {
                log.userSaid(it)
                startListenTimer(readingTestState)
                furhat.botSay(waitingUntilReady)
                readingTestState.reentry()
            }
        }
    }
    onNoResponse {
        reentry()
    }
}

val ReadingTestQuestion1 : State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botAsk(READING_TEST_5, timeout=55000, endSil = 6000)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        ackAndGoto(ReadingTestQuestion2)
    }
}

val ReadingTestQuestion2 : State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botSay(READING_TEST_6)
        call { farmiService.showScreen() }
        furhat.botAsk(READING_TEST_7, timeout=55000, endSil = 6000)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        ackAndGoto(ReadingTestQuestion3)
    }
}

val ReadingTestQuestion3 : State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botAsk(READING_TEST_8, timeout=55000, endSil = 6000)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        ackAndGoto(ReadingTestQuestion4)
    }
}

val ReadingTestQuestion4 : State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botAsk(READING_TEST_9, timeout=55000, endSil = 6000)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        ackAndGoto(ReadingTestQuestion5)
    }
}

val ReadingTestQuestion5 : State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botAsk(READING_TEST_10, timeout=55000, endSil = 6000)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        ackAndGoto(ReadingTestQuestion6)
    }
}

val ReadingTestQuestion6 : State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botAsk(READING_TEST_11, timeout=55000, endSil = 6000)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        ackAndGoto(Outro)
    }
}

