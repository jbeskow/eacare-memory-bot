package furhatos.app.memorybot

import furhatos.app.memorybot.flow.main.WaitForUserToBeReadyState
import furhatos.app.memorybot.phrases.USER_DEFINETLY_DIDNT_UNDERSTAND
import furhatos.event.monitors.MonitorSpeechStart
import furhatos.app.memorybot.utterances.acknowledgement
import furhatos.app.memorybot.utterances.nextTest
import furhatos.event.EventSystem.send
import furhatos.flow.kotlin.*
import furhatos.nlu.Response
import furhatos.util.CommonUtils

const val DEFAULT_TIMEOUT : Int = 5000
const val DEFAULT_ENDSIL : Int = 800
const val DEFAULT_MAX_SPEECH : Int = 15000

const val ASK_OPEN_TIMEOUT_MS = (8 * 1000)
const val ASK_OPEN_ENDSIL_MS = (5 * 1000)
const val ASK_OPEN_MAX_SPEECH_MS = (55 * 1000)

const val DEFAULT_SPEECH_RATE = 1.0

fun Furhat.botSay(text: String, rate : Double = DEFAULT_SPEECH_RATE) {
    if (voice.rate != rate) { voice.rate = rate; }
    send(FurhatSpeaking())
    say(text)
    send(FurhatIdle())
}
fun Furhat.botSay(text: Utterance, rate : Double = DEFAULT_SPEECH_RATE) {
    send(FurhatIdle())
    if (voice.rate != rate) { println("RATE IS ${voice.rate}. CHANGING TO: $rate"); voice.rate = rate; }
    say(text)
}

fun Furhat.botAsk(
        text: String,
        timeout : Int = DEFAULT_TIMEOUT,
        endSil : Int = DEFAULT_ENDSIL,
        maxSpeech : Int = DEFAULT_MAX_SPEECH,
        rate: Double = DEFAULT_SPEECH_RATE) {
    send(FurhatSpeaking())
    botSay(text, rate = rate)
    send(FurhatListening())
    listen(timeout = timeout, endSil = endSil, maxSpeech = maxSpeech)
}

fun Furhat.botAsk(
        utterance: Utterance,
        timeout : Int = DEFAULT_TIMEOUT,
        endSil : Int = DEFAULT_ENDSIL,
        maxSpeech : Int = DEFAULT_MAX_SPEECH,
        rate: Double = DEFAULT_SPEECH_RATE) {
    send(FurhatIdle())
    botSay(utterance, rate = rate)
    send(FurhatListening())
    listen(timeout = timeout, endSil = endSil, maxSpeech = maxSpeech)
}

fun Furhat.botAskYN(
        text: String,
        timeout : Int = DEFAULT_TIMEOUT,
        endSil : Int = DEFAULT_ENDSIL,
        maxSpeech : Int = DEFAULT_MAX_SPEECH,
        rate: Double = DEFAULT_SPEECH_RATE,
        stateDefinition: (furhatos.flow.kotlin.StateBuilder.() -> kotlin.Unit)?) {
    voice.rate = rate;
    send(FurhatSpeaking())
    askYN(text, stateDefinition = stateDefinition)
    send(FurhatListening())
    listen(timeout = timeout, endSil = endSil, maxSpeech = maxSpeech)
}

fun Furhat.botAskYN(
        utterance: Utterance,
        timeout : Int = DEFAULT_TIMEOUT,
        endSil : Int = DEFAULT_ENDSIL,
        maxSpeech : Int = DEFAULT_MAX_SPEECH,
        rate: Double = DEFAULT_SPEECH_RATE,
        stateDefinition: (furhatos.flow.kotlin.StateBuilder.() -> kotlin.Unit)?) {
    voice.rate = rate;
    send(FurhatIdle())
    askYN(utterance, stateDefinition = stateDefinition)
    send(FurhatListening())
    listen(timeout = timeout, endSil = endSil, maxSpeech = maxSpeech)
}

fun Furhat.botAskOpen(
        text : String,
        timeout : Int = ASK_OPEN_TIMEOUT_MS,
        endSil : Int = ASK_OPEN_ENDSIL_MS,
        maxSpeech : Int = ASK_OPEN_MAX_SPEECH_MS,
        rate: Double = DEFAULT_SPEECH_RATE) {
    botSay(text, rate = rate)
    send(FurhatListening())
    listen(
        timeout = timeout,
        endSil = endSil,
        maxSpeech = maxSpeech
    )
}

fun FlowControlRunner.ackAndGoto(nextState : State) {
    furhat.botSay(acknowledgement)
    goto(nextState)
}

fun FlowControlRunner.ackAndGotoNextTest(nextState : State) {
    furhat.botSay(acknowledgement + nextTest)
    goto(nextState)
}
fun FlowControlRunner.gotoNextTest(nextState : State) {
    furhat.botSay(nextTest)
    goto(nextState)
}

fun FlowControlRunner.withReentryVariation(listOfText : List<String>, fallback: String = USER_DEFINETLY_DIDNT_UNDERSTAND, handler: (text : String) -> Unit) {
    if (reentryCount < listOfText.count()) {
        handler(listOfText[reentryCount])
    } else {
        handler(fallback)
        call(WaitForUserToBeReadyState)
    }
}

class EACareLogger {
    val logger = CommonUtils.getLogger(MemorybotSkill::class.java)!!
    fun stateEnter(state : State) {
        logger.debug("Entered state: ${state.name}")
    }
    fun userSaid(res : Response<*>) {
        logger.debug("User said: ${res.text}")
    }
    fun furhatSaid(event : MonitorSpeechStart) {
        logger.debug("Furhat said: ${event.text}")
    }
}

val log = EACareLogger()
