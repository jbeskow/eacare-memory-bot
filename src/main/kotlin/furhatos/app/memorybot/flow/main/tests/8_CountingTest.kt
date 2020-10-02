package furhatos.app.memorybot.flow.main.tests

import furhatos.app.memorybot.botSay
import furhatos.app.memorybot.flow.main.*
import furhatos.app.memorybot.log
import furhatos.app.memorybot.phrases.*
import furhatos.app.memorybot.utterances.nextTest
import furhatos.app.memorybot.utterances.thenLetsStart
import furhatos.app.memorybot.withReentryVariation
import furhatos.flow.kotlin.*
import furhatos.nlu.Intent
import furhatos.nlu.Response
import furhatos.nlu.common.Number
import furhatos.util.Language

class CountingIntent : Intent() {
    val number : Number? = null
    override fun getExamples(lang: Language): List<String> = listOf("@number")
}

const val COUNTING_TEST_MAX_LISTEN_MS = (45 * 1000)
const val COUNTING_TEST_MIN_LISTEN_MS = (15 * 1000)

val CountingTest : State = state {
    onEntry {
        log.stateEnter(thisState)
        withReentryVariation(listOf(COUNTING_TEST_1a, COUNTING_TEST_1b, COUNTING_TEST_1c)) { furhat.botSay(it) }
        if (call(DoYouUnderstandState) as Boolean) {
            furhat.botSay(thenLetsStart)
            goto(CountingTestStart)
        }
        else { reentry() }
    }
    include(CommandBehavior)
    include(BasicBehaviour)
}
val CountingTestStart : State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botSay(COUNTING_TEST_2)
        startListenTimer(this, listenTimeMs = COUNTING_TEST_MAX_LISTEN_MS)
        furhat.listen(listenTimerTimeLeft(this))
    }
    onResponse<CountingIntent> {
        log.userSaid(it)
        gotoNextOrContinueListen(it)
    }
    onResponse {
        log.userSaid(it)
        gotoNextOrContinueListen()
    }
    onNoResponse {
        furhat.botSay(COUNTING_TEST_3)
        furhat.botSay(nextTest)
        goto(SentenceTest)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
}

private fun TriggerRunner<*>.gotoNextOrContinueListen(number: Response<out CountingIntent>? = null) {
    if (listenTimerTimeLeft(this) < COUNTING_TEST_MAX_LISTEN_MS - COUNTING_TEST_MIN_LISTEN_MS) {
        if (number?.intent?.number?.value in 50..65) {
            furhat.botSay(COUNTING_TEST_3)
            furhat.botSay(nextTest)
            goto(SentenceTest)
        } else {
            furhat.listen(listenTimerTimeLeft(this))
        }
    } else {
        furhat.listen(listenTimerTimeLeft(this))
    }
}
