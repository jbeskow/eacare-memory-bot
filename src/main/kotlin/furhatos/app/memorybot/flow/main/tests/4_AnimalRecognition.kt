package furhatos.app.memorybot.flow.main.tests

import furhatos.app.memorybot.*
import furhatos.app.memorybot.flow.main.*
import furhatos.app.memorybot.phrases.ANIMAL_RECOGNITION_1
import furhatos.app.memorybot.phrases.ANIMAL_RECOGNITION_2
import furhatos.app.memorybot.phrases.ANIMAL_RECOGNITION_3
import furhatos.app.memorybot.phrases.ANIMAL_RECOGNITION_4
import furhatos.app.memorybot.services.farmiService
import furhatos.flow.kotlin.*

val AnimalRecognition : State = state {
    onEntry {
        log.stateEnter(thisState)
        val res = call { farmiService.gotoPage(thisState) } as Boolean
        if (res) {
            furhat.botSay(ANIMAL_RECOGNITION_1)
            goto(FirstAnimalQuestion)
        } else {
            reentry()
        }
    }
    include(CommandBehavior)
    include(BasicBehaviour)
}

val FirstAnimalQuestion : State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botAsk(ANIMAL_RECOGNITION_2, timeout=7000)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        ackAndGoto(SecondAnimalQuestion)
    }
}
val SecondAnimalQuestion : State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botAsk(ANIMAL_RECOGNITION_3, timeout=7000)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        ackAndGoto(ThirdAnimalQuestion)
    }
}

val ThirdAnimalQuestion : State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botAsk(ANIMAL_RECOGNITION_4, timeout=7000)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        ackAndGotoNextTest(WordRecall)
    }
}
