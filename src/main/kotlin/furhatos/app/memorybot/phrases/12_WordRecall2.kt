package furhatos.app.memorybot.phrases

import furhatos.app.memorybot.flow.main.WordRecallWord

const val WORD_RECALL2_1 = "Jag läste tidigare upp några ord för dig, som jag bad dig komma ihåg."
const val WORD_RECALL2_2 = "Säg så många av dem, som du kan komma ihåg."
const val WORD_RECALL2_3 = "Jag kan hjälpa dig med dem."
const val WORD_RECALL2_GIVE_ALTERNATIVES_1 = "Om jag ger tre olika alternativ varav ett är det rätta. Se om du känner igen det."

const val FIRST = "första"
const val SECOND = "andra"
const val THIRD = "tredje"
const val FOURTH = "fjärde"
const val LAST = "sista"

fun wordRecall2MissingWordsPhrase(wordsCount : Number) : String {
    return "Det verkar som att du missade ${wordsCount} av orden."
}
fun wordRecallTipPhrase(word : WordRecallWord, numeral : String) : String {
    return "Det ${numeral} ordet var ${word.tip}."
}
