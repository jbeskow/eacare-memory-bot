package furhatos.app.memorybot.flow.main

import furhatos.app.memorybot.botSay
import furhatos.app.memorybot.nlu.intents.DoctorContinue
import furhatos.app.memorybot.phrases.COMMAND_CONTINUE
import furhatos.app.memorybot.phrases.COMMAND_PAUSE
import furhatos.flow.kotlin.*

fun Paused(continueState : State) : State = state {
    onEntry {
        furhat.botSay(COMMAND_PAUSE)
        furhat.listen()
    }
    onButton("Resume") {
        furhat.botSay(COMMAND_CONTINUE)
        goto(continueState)
    }
    onResponse<DoctorContinue> {
        furhat.botSay(COMMAND_CONTINUE)
        goto(continueState)
    }
    include(CommandBehavior)
    onResponse { furhat.listen() }
    onNoResponse { furhat.listen() }
    onUserLeave { furhat.listen() }
    onUserEnter { furhat.attend(it); furhat.listen() }
}
