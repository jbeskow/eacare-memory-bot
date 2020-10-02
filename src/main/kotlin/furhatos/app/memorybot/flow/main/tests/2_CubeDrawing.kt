package furhatos.app.memorybot.flow.main.tests

import furhatos.app.memorybot.*
import furhatos.app.memorybot.flow.main.BasicBehaviour
import furhatos.app.memorybot.flow.main.CommandBehavior
import furhatos.app.memorybot.log
import furhatos.app.memorybot.phrases.CUBE_DRAWING_1
import furhatos.app.memorybot.services.farmiService
import furhatos.flow.kotlin.*

val CubeDrawing : State = state {
    onEntry {
        log.stateEnter(thisState)
        val res = call { farmiService.gotoPage(thisState) } as Boolean
        if (res) {
            send(UserActivityPollStartEvent(8.0))
            furhat.botSay(CUBE_DRAWING_1)
            furhat.listen()
        } else {
            reentry()
        }
    }
    onEvent<UserActivityPollDoneEvent> {
        ackAndGotoNextTest(ClockTest)
    }
    include(CommandBehavior)
    onResponse { furhat.listen() }
    onNoResponse { furhat.listen() }
    include(BasicBehaviour)
}
