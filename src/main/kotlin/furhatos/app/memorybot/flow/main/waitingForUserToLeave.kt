package furhatos.app.memorybot.flow.main

import furhatos.app.memorybot.log
import furhatos.flow.kotlin.*

val waitingForUserToLeave : State = state {
    onEntry {
        log.stateEnter(thisState)
    }
    onTime(repeat = 1000) {
        if (users.current == null || !users.current.isVisible) {
            goto(Idle)
        }
    }
    onUserEnter {}
    onUserLeave {}
    include(BasicBehaviour)
    include(CommandBehavior)
}
