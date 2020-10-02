package furhatos.app.memorybot.flow.main.tests

import furhatos.app.memorybot.*
import furhatos.app.memorybot.flow.main.BasicBehaviour
import furhatos.app.memorybot.flow.main.CommandBehavior
import furhatos.app.memorybot.phrases.*
import furhatos.app.memorybot.utterances.acknowledgement
import furhatos.app.memorybot.utterances.retry
import furhatos.flow.kotlin.*

private val NumberRecallNumbers: List<List<String>> = listOf(
        listOf("2", "två"),
        listOf("1", "ett"),
        listOf("8", "åtta"),
        listOf("5", "fem"),
        listOf("4", "fyra")
)

private val NumberRecallRegex = NumberRecallNumbers
        .joinToString(separator = "(.*?)", postfix = "(.*?)", prefix = "(.*?)") {
            it.joinToString(separator = "|", prefix = "(", postfix = ")")
        }
        .toRegex()

private fun checkAnswer(answer: String): Boolean {
    return NumberRecallRegex.matches(answer.toLowerCase())
}

val NumberRecall: State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botSay(NUMBER_RECALL_1)
        furhat.botAskOpen(NUMBER_RECALL_2)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        if (checkAnswer(it.text)) {
            ackAndGoto(NumberRecallBackwards)
        } else {
            furhat.botSay(acknowledgement)
            furhat.botSay(retry)
            goto(NumberRecallRepeat)
        }
    }
}

val NumberRecallRepeat: State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botSay(NUMBER_RECALL_3)
        furhat.botAskOpen(NUMBER_RECALL_2)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        ackAndGoto(NumberRecallBackwards)
    }
}

val NumberRecallBackwards: State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botSay(NUMBER_RECALL_4)
        furhat.botAskOpen(NUMBER_RECALL_5)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        ackAndGotoNextTest(LetterTest)
    }
}
