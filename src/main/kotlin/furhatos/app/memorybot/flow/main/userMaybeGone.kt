package furhatos.app.memorybot.flow.main

import furhatos.app.memorybot.*
import furhatos.app.memorybot.utterances.thereYouAre
import furhatos.app.memorybot.utterances.whereAreYou
import furhatos.flow.kotlin.*

fun UserMaybeGone(continueState : State ) : State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botSay(whereAreYou)
    }
    onTime(45000) {
        goto(Goodbye)
    }
    onUserEnter {
        furhat.attend(it)
        furhat.botSay(thereYouAre)
        goto(continueState)
    }
}