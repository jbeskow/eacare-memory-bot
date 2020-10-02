package furhatos.app.memorybot.flow.main.tests

import furhatos.app.memorybot.*
import furhatos.app.memorybot.flow.main.BasicBehaviour
import furhatos.app.memorybot.flow.main.CommandBehavior
import furhatos.app.memorybot.flow.main.DoYouUnderstandState
import furhatos.app.memorybot.phrases.*
import furhatos.app.memorybot.utterances.thenLetsStart
import furhatos.flow.kotlin.*

val LetterTest: State = state {
    onEntry {
        log.stateEnter(thisState)
        withReentryVariation(listOf(LETTER_TEST_1a, LETTER_TEST_1b, LETTER_TEST_1c)) { furhat.botSay(it) }
        if (call(DoYouUnderstandState) as Boolean) {
            furhat.botSay(thenLetsStart)
            goto(LetterTestStart)
        } else { reentry() }
    }
    include(CommandBehavior)
    include(BasicBehaviour)
}

val LetterTestStart: State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botSay(LETTER_TEST_2, rate = 0.7)
        ackAndGotoNextTest(CountingTest)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
}
