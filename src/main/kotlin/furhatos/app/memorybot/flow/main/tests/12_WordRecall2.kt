package furhatos.app.memorybot.flow.main.tests

import furhatos.app.memorybot.*
import furhatos.app.memorybot.flow.main.*
import furhatos.app.memorybot.phrases.*
import furhatos.app.memorybot.utterances.acknowledgement
import furhatos.flow.kotlin.*

private val Words: List<WordRecallWord> = listOf(
        WordRecallWord(word = "munspel", tip = "ett instrument.", alternatives = "Munspel, Gitarr, Piano"),
        WordRecallWord(word = "stol", tip = "en möbel.", alternatives = "Bord, Stol, Soffa"),
        WordRecallWord(word = "plånbok", tip = "något man brukar ha med sig när man går hemifrån.", alternatives = "Nycklar, Mobiltelefon, Plånbok"),
        WordRecallWord(word = "tång", tip = "en växt som finns i vatten.", alternatives = "Alg, näckros, tång"),
        WordRecallWord(word = "sax", tip = "ett redskap.", alternatives = "Sax, kratta, spett")
)

private val Numerals = mapOf(
    0 to FIRST,
    1 to SECOND,
    2 to THIRD,
    3 to FOURTH,
    4 to LAST
)

private fun checkWords (answer : String, wordsLeft : MutableList<WordRecallWord>) : Boolean {
    wordsLeft.forEach { checkWord(answer, it)}
    return wordsLeft.all { it.userDidRecall }
}
private fun checkWord(answer: String, word : WordRecallWord) : Boolean {
    return if (answer.contains(word.word, ignoreCase = true)) {
        word.userDidRecall = true
        true
    } else {
        word.userDidRecall = false
        false
    }
}

val WordRecall2 : State = state {
    onEntry {
        log.stateEnter(thisState)
        users.current.tests.wordsRecall.clear()
        users.current.tests.wordsRecall.addAll( Words )
        furhat.botSay(WORD_RECALL2_1)
        furhat.setSpeechRecPhrases(Words.map { it.word })
        furhat.botAsk(WORD_RECALL2_2, timeout = 8000, endSil = 4000)
    }
    include(CommandBehavior)
    include(BasicBehaviour)
    onResponse {
        log.userSaid(it)
        if (checkWords(it.text, users.current.tests.wordsRecall)) {
            ackAndGotoNextTest(TimeSpaceTestSpace)
        } else {
            val missedWords =  users.current.tests.wordsRecall.filter { !it.userDidRecall }
            furhat.botSay(wordRecall2MissingWordsPhrase(missedWords.count()))
            furhat.botSay(WORD_RECALL2_3)
            for ((index, word) in missedWords.withIndex()) {
                val numeral : String = (if (missedWords.count() == index + 1) {
                    LAST
                } else {
                    Numerals[index]
                }) as String
                call(wordRecallGiveTips(numeral, word))
            }
            gotoNextTest(TimeSpaceTestSpace)
        }
    }
}

fun wordRecallGiveTips(numeral : String, word : WordRecallWord) : State  = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.setSpeechRecPhrases(Words.map { it.word })
        furhat.botAsk(wordRecallTipPhrase(word, numeral), timeout = 8000)
    }
    include(CommandBehavior)
    include(UserHandling)
    include(RepeatBehaviour)
    include(NoResponseBehaviour)
    onResponse {
        if (checkWord(it.text, word)) {
            furhat.botSay(acknowledgement)
            terminate()
        } else {
            call(WordRecallGiveSynonyms(word))
            terminate()
        }
    }
}

fun WordRecallGiveSynonyms(word : WordRecallWord) : State = state {
    onEntry {
        log.stateEnter(thisState)
        furhat.botSay(WORD_RECALL2_GIVE_ALTERNATIVES_1)
        furhat.setSpeechRecPhrases(Words.map { it.word })
        furhat.botAsk(word.alternatives, timeout = 8000)
    }
    include(CommandBehavior)
    include(UserHandling)
    include(RepeatBehaviour)
    include(NoResponseBehaviour)
    onResponse {
        checkWord(it.text, word)
        furhat.botSay(acknowledgement)
        terminate()
    }
}

