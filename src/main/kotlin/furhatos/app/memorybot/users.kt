package furhatos.app.memorybot.flow.main
import furhatos.records.User


data class WordRecallWord(val word : String, val tip : String, val alternatives : String, var userDidRecall : Boolean = false)

class TestsData (
    var waitCount : Int = 0,
    var listenStartAt : Long = 0,
    var listenTimeMs : Int = 0,
    var wordsRecall : MutableList<WordRecallWord> = mutableListOf()
)

val User.tests : TestsData
    get() = data.getOrPut(TestsData::class.qualifiedName, TestsData())