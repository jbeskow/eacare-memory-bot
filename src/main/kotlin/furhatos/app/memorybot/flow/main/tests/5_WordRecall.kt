package furhatos.app.memorybot.flow.main.tests

import furhatos.app.memorybot.*
import furhatos.app.memorybot.flow.main.BasicBehaviour
import furhatos.app.memorybot.flow.main.CommandBehavior
import furhatos.app.memorybot.flow.main.DoYouUnderstandState
import furhatos.app.memorybot.nlu.intents.No
import furhatos.app.memorybot.nlu.intents.Yes
import furhatos.app.memorybot.phrases.*
import furhatos.app.memorybot.services.farmiService
import furhatos.app.memorybot.utterances.doYouUnderstand
import furhatos.app.memorybot.utterances.letsStart
import furhatos.app.memorybot.utterances.thenLetsStart
import furhatos.flow.kotlin.*

val WordRecallWords : List<String> = listOf("Stol", "Plånbok", "Tång", "Munspel", "Sax")

private fun checkAnswer(answer : String) : Boolean {
    return WordRecallWords.all {
        answer.contains(it, ignoreCase = true)
    }
}

val WordRecallRepeat : State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botSay(WORD_RECALL_3)
        furhat.setSpeechRecPhrases(WordRecallWords)
        furhat.botAskOpen(WORD_RECALL_2)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        ackAndGotoNextTest(NumberRecall)
    }
}

val WordRecall : State = state {
    onEntry {
        log.stateEnter(thisState)
        call { farmiService.hideScreen() } as Boolean
        withReentryVariation(listOf(WORD_RECALL_1a, WORD_RECALL_1b, WORD_RECALL_1c)) {
            furhat.botSay(it)
        }
        if (call(DoYouUnderstandState) as Boolean) {
            furhat.botSay(thenLetsStart)
            goto(WordRecallStart)
        } else { reentry() }
    }
    include(CommandBehavior)
    include(BasicBehaviour)
}

val WordRecallStart : State = state {
    onEntry {
        furhat.setSpeechRecPhrases(WordRecallWords)
        furhat.botAskOpen(WORD_RECALL_2)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        if (checkAnswer(it.text)) {
            ackAndGotoNextTest(NumberRecall)
        } else {
            ackAndGoto(WordRecallRepeat)
        }
    }
}
