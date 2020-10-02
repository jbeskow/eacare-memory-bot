package furhatos.app.memorybot.flow.main.tests

import furhatos.app.memorybot.UserActivityPollDoneEvent
import furhatos.app.memorybot.UserActivityPollStartEvent
import furhatos.app.memorybot.botSay
import furhatos.app.memorybot.flow.main.BasicBehaviour
import furhatos.app.memorybot.flow.main.CommandBehavior
import furhatos.app.memorybot.log
import furhatos.app.memorybot.phrases.CLOCK_TEST_1
import furhatos.app.memorybot.phrases.CLOCK_TEST_2
import furhatos.app.memorybot.services.farmiService
import furhatos.app.memorybot.utterances.acknowledgement
import furhatos.app.memorybot.utterances.nextTest
import furhatos.flow.kotlin.*

val ClockTest : State = state {
    onEntry {
        log.stateEnter(thisState)
        val res = call { farmiService.gotoPage(thisState) } as Boolean
        if (res) {
            send(UserActivityPollStartEvent(10.0))
            furhat.botSay(CLOCK_TEST_1)
            furhat.listen()
        } else {
            reentry()
        }
    }
    onEvent<UserActivityPollDoneEvent> {
        furhat.botSay(acknowledgement + nextTest)
        furhat.botSay(CLOCK_TEST_2)
        goto(AnimalRecognition)
    }
    include(CommandBehavior)
    onResponse { furhat.listen() }
    onNoResponse { furhat.listen() }
    include(BasicBehaviour)
}
