package furhatos.app.memorybot.flow.main.tests

import furhatos.app.memorybot.ackAndGoto
import furhatos.app.memorybot.ackAndGotoNextTest
import furhatos.app.memorybot.botAsk
import furhatos.app.memorybot.flow.main.BasicBehaviour
import furhatos.app.memorybot.flow.main.CommandBehavior
import furhatos.app.memorybot.log
import furhatos.app.memorybot.phrases.TIME_SPACE_TEST_1
import furhatos.app.memorybot.phrases.TIME_SPACE_TEST_2
import furhatos.app.memorybot.phrases.TIME_SPACE_TEST_3
import furhatos.app.memorybot.phrases.TIME_SPACE_TEST_4
import furhatos.flow.kotlin.*

val TimeSpaceTest : State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botAsk(TIME_SPACE_TEST_1, timeout = 8000, endSil = 4000)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        ackAndGoto(TimeSpaceTestYear)
    }
}

val TimeSpaceTestYear : State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botAsk(TIME_SPACE_TEST_2, timeout = 8000, endSil = 4000)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        ackAndGoto(TimeSpaceTestWeekday)
    }
}

val TimeSpaceTestWeekday : State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botAsk(TIME_SPACE_TEST_3, timeout = 8000, endSil = 4000)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        ackAndGoto(TimeSpaceTestSpace)
    }
}

val TimeSpaceTestSpace : State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botAsk(TIME_SPACE_TEST_4, timeout = 8000, endSil = 4000)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        ackAndGotoNextTest(CookieTest)
    }
}
