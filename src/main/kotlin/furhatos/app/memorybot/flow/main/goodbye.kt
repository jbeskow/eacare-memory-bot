package furhatos.app.memorybot.flow.main

import furhatos.app.memorybot.botSay
import furhatos.app.memorybot.log
import furhatos.flow.kotlin.*

val Goodbye : State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botSay("Hejdå!")
        goto(waitingForUserToLeave)
    }
    include(CommandBehavior)
}
