package furhatos.app.memorybot.flow.main.tests

import furhatos.app.memorybot.*
import furhatos.app.memorybot.flow.main.BasicBehaviour
import furhatos.app.memorybot.flow.main.CommandBehavior
import furhatos.app.memorybot.phrases.SENTENCE_TEST_1
import furhatos.app.memorybot.phrases.SENTENCE_TEST_2
import furhatos.app.memorybot.phrases.SENTENCE_TEST_3
import furhatos.app.memorybot.phrases.SENTENCE_TEST_4
import furhatos.flow.kotlin.*

val SentenceTest : State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botSay(SENTENCE_TEST_1)
        delay(1000)
        furhat.botAsk(SENTENCE_TEST_2, timeout = ASK_OPEN_TIMEOUT_MS, endSil = 5000)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        ackAndGoto(SentenceTest2)
    }
}

val SentenceTest2 : State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botSay(SENTENCE_TEST_3)
        delay(1000)
        furhat.botAsk(SENTENCE_TEST_4, timeout = ASK_OPEN_TIMEOUT_MS, endSil = 3000)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        ackAndGotoNextTest(WordsOnF)
    }
}
