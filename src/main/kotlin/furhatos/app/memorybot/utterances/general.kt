package furhatos.app.memorybot.utterances

import furhatos.app.memorybot.botSay
import furhatos.app.memorybot.flow.main.tests
import furhatos.app.memorybot.phrases.*
import furhatos.flow.kotlin.*
import furhatos.gestures.Gestures

fun getWaitUtterance (context : FlowControlRunner) {
    when (context.users.current.tests.waitCount) {
        0 -> context.furhat.botSay(TELL_WHEN_READY_4)
        1 -> context.furhat.botSay(WAIT_7)
        else -> waitingUntilReady
    }
    context.users.current.tests.waitCount++
    context.furhat.listen(timeout=40000)
}

val letsStart : Utterance = utterance {
    random {
        + LETS_START_1
        + LETS_START_2
        + LETS_START_3
        + LETS_START_4
        + THEN_LETS_START_1
    }
}

val thenLetsStart : Utterance = utterance {
    random {
        + THEN_LETS_START_1
        + THEN_LETS_START_2
        + THEN_LETS_START_3
    }
}

val areYouReady : Utterance = utterance {
    random {
        + READY_QUESTION_1
        + READY_QUESTION_2
        + READY_QUESTION_3
        + READY_QUESTION_4
    }
    + Gestures.Smile
}

val doYouUnderstand : Utterance = utterance {
    random {
        + DO_YOU_UNDERSTAND_1
        + DO_YOU_UNDERSTAND_2
        + DO_YOU_UNDERSTAND_3
    }
    + Gestures.Smile
}

val waitingUntilReady : Utterance = utterance {
    random {
        + Gestures.Smile
        + Gestures.Nod(strength = 0.5)
    }
    random {
        + WAIT_1
        + WAIT_2
        + WAIT_3
        + WAIT_4
        + WAIT_5
        + WAIT_6
    }
    random {
        + TELL_WHEN_READY_1
        + TELL_WHEN_READY_2
        + TELL_WHEN_READY_3
    }
}

val canYouRepeat : Utterance = utterance {
    random {
        + CAN_YOU_REPEAT_1
        + CAN_YOU_REPEAT_2
        + CAN_YOU_REPEAT_3
    }
}

val retry : Utterance = utterance {
    random {
        + RETRY_1
        + RETRY_2
        + RETRY_3
    }
}

val nextTest : Utterance = utterance {
    random {
        + NEXT_TEST_1
        + NEXT_TEST_2
        + NEXT_TEST_3
        + NEXT_TEST_4
        + NEXT_TEST_5
        + NEXT_TEST_6
        + NEXT_TEST_7
        + NEXT_TEST_8
        + NEXT_TEST_9
        + NEXT_TEST_10
        + NEXT_TEST_11
        + NEXT_TEST_12
        + NEXT_TEST_13
    }
}

val thereYouAre : Utterance = utterance {
    + Gestures.Smile(duration = 3.0)
    + "Där är du. Vi fortsätter."
}

val whereAreYou : Utterance = utterance {
    + Gestures.Thoughtful(duration = 3.0)
    + "Var tog du vägen? Jag ser dig inte längre."
}
val outro : Utterance = utterance {
    + Gestures.Smile(duration = 3.0)
    + "Det var det sista testet, så nu är vi färdiga. Tack för att du deltog!"
}
