package furhatos.app.memorybot.flow.main.tests

import furhatos.app.memorybot.*
import furhatos.app.memorybot.flow.main.*
import furhatos.app.memorybot.phrases.LINE_DRAWING_1
import furhatos.app.memorybot.services.farmiService
import furhatos.flow.kotlin.*

val LineDrawing : State = state {
    onEntry {
        log.stateEnter(thisState)
        val res = call { farmiService.gotoPage(thisState) } as Boolean
        if (res) {
            send(UserActivityPollStartEvent(6.0))
            furhat.botSay(LINE_DRAWING_1)
            furhat.listen()
        } else {
            reentry()
        }
    }
    onEvent<UserActivityPollDoneEvent> {
        ackAndGotoNextTest(CubeDrawing)
    }
    include(CommandBehavior)
    onResponse { furhat.listen() }
    onNoResponse { furhat.listen() }
    include(BasicBehaviour)
}
