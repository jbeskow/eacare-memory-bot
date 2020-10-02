package furhatos.app.memorybot.flow.main.tests

import furhatos.app.memorybot.*
import furhatos.app.memorybot.flow.main.BasicBehaviour
import furhatos.app.memorybot.flow.main.CommandBehavior
import furhatos.app.memorybot.phrases.COOKIE_TEST_1
import furhatos.app.memorybot.phrases.COOKIE_TEST_2
import furhatos.app.memorybot.phrases.COOKIE_TEST_3
import furhatos.app.memorybot.services.farmiService
import furhatos.flow.kotlin.*

val CookieTest : State = state {
    onEntry {
        log.stateEnter(thisState)
        call { farmiService.gotoPage(thisState) }
        furhat.botSay(COOKIE_TEST_1)
        furhat.botAskOpen(COOKIE_TEST_2)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        ackAndGoto(CookieTestAddition)
    }
}

val CookieTestAddition : State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botAskOpen(COOKIE_TEST_3)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        ackAndGotoNextTest(ReadingTest)
    }
}
