package furhatos.app.memorybot.flow.main

import furhatos.app.memorybot.botSay
import furhatos.app.memorybot.services.farmiService
import furhatos.app.memorybot.log
import furhatos.app.memorybot.utterances.outro
import furhatos.flow.kotlin.*

val Outro : State = state {
    onEntry {
        log.stateEnter(thisState)
        call { farmiService.hideScreen() }
        furhat.botSay(outro)
        delay(500)
        goto(Goodbye)
    }
    include(CommandBehavior)
    include(UserHandling)
    include(DebugButtons)
}
