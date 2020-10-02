package furhatos.app.memorybot.flow.main

import furhatos.app.memorybot.FurhatExpressing
import furhatos.app.memorybot.botSay
import furhatos.app.memorybot.flow.main.tests.LineDrawing
import furhatos.app.memorybot.services.farmiService
import furhatos.app.memorybot.log
import furhatos.flow.kotlin.*

val Intro : State = state {

    onEntry {
        log.stateEnter(thisState)
        call { farmiService.startRecording() }
        send(FurhatExpressing())
        furhat.botSay("Hej och välkommen!")
        furhat.botSay("Nu ska vi börja testerna med paddan. Du har en penna som du kan rita med direkt på skärmen. Jag kommer läsa instruktioner före varje uppgift.")
        furhat.botSay("Så, nu börjar testet.")
        goto(LineDrawing)
    }
    include(CommandBehavior)
    include(UserHandling)
    include(DebugButtons)
}
