package furhatos.app.memorybot.flow.main.tests

import furhatos.app.memorybot.ackAndGoto
import furhatos.app.memorybot.ackAndGotoNextTest
import furhatos.app.memorybot.botAsk
import furhatos.app.memorybot.flow.main.BasicBehaviour
import furhatos.app.memorybot.flow.main.CommandBehavior
import furhatos.app.memorybot.log
import furhatos.app.memorybot.phrases.*
import furhatos.flow.kotlin.*


val SematicTest : State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botAsk(SEMATIC_TEST_1)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        ackAndGoto(SematicTestQuestion1)
    }
}

val SematicTestQuestion1 : State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botAsk(SEMATIC_TEST_2a)
    }
    onReentry {
        furhat.botAsk(SEMATIC_TEST_2b) // TODO : Check with björn if we want theese kind of variants
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        ackAndGoto(SematicTestQuestion2)
    }
}

val SematicTestQuestion2 : State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botAsk(SEMATIC_TEST_3)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        ackAndGoto(SematicTestQuestion3)
    }
}

val SematicTestQuestion3 : State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botAsk(SEMATIC_TEST_4)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        ackAndGotoNextTest(WordRecall2)
    }
}
