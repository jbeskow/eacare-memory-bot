package furhatos.app.memorybot.nlu.intents

import furhatos.app.memorybot.nlu.intents.examples.*
import furhatos.nlu.EnumEntity
import furhatos.nlu.Intent
import furhatos.nlu.common.Number
import furhatos.nlu.common.Ordinal
import furhatos.util.Language

class Yes : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return YesExamples
    }
    override fun getSpeechRecPhrases(lang: Language): List<String> {
        return YesSpeechRecPhrases
    }
}

class No : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return NoExamples
    }
}

class Repeat : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return RepeatExamples
    }
}

class StartContinue : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return StartContinueExamples
    }
}


class Wait : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return WaitExamples
    }
}


class DoctorContinue : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return DoctorContinueExamples
    }
}
class DoctorPause : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return DoctorPauseExamples
    }
}
class DoctorRestart : Intent() {
    override fun getExamples(lang: Language): List<String> {
        return DoctorRestartExamples
    }
}

class DoctorGotoNumber : Intent() {
    var x : Number? = null
    override fun getExamples(lang: Language): List<String> {
        return DoctorGoToNumberExamples
    }
}
class DoctorGotoOrdinal : Intent() {
    var x : Ordinal? = null
    override fun getExamples(lang: Language): List<String> {
        return DoctorGoToOrdinalExamples
    }
}