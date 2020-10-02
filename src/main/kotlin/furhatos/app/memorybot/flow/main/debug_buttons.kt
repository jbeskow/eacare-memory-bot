package furhatos.app.memorybot.flow.main

import furhatos.app.memorybot.*
import furhatos.app.memorybot.flow.main.tests.*
import furhatos.app.memorybot.utterances.acknowledgement
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.partialState

val DebugButtons = partialState {
    include(ControllerButtons)
    include(FlowControllButtons)
    include(GestureButtons)
}

val ControllerButtons = partialState {
    onButton("Intro") {
        furhat.stopSpeaking()
        goto(Intro)
    }
    onButton("1 LineDrawing") {
        furhat.stopSpeaking()
        goto(LineDrawing)
    }
    onButton("2 CubeTest") {
        furhat.stopSpeaking()
        goto(CubeDrawing)
    }
    onButton("3 ClockTest") {
        furhat.stopSpeaking()
        goto(ClockTest)
    }
    onButton("4 AnimalRecognition") {
        furhat.stopSpeaking()
        goto(AnimalRecognition)
    }
    onButton("5 WordRecall") {
        furhat.stopSpeaking()
        goto(WordRecall)
    }
    onButton("6 NumberRecall") {
        furhat.stopSpeaking()
        goto(NumberRecall)
    }
    onButton("7 LetterTest") {
        furhat.stopSpeaking()
        goto(LetterTest)
    }
    onButton("8 CountingTest") {
        furhat.stopSpeaking()
        goto(CountingTest)
    }
    onButton("9 SentenceTest") {
        furhat.stopSpeaking()
        goto(NumberRecall)
    }
    onButton("10 WordsOnF") {
        furhat.stopSpeaking()
        goto(WordsOnF)
    }
    onButton("11 SematicTest") {
        furhat.stopSpeaking()
        goto(SematicTest)
    }
    onButton("12 WordRecall2") {
        furhat.stopSpeaking()
        goto(WordRecall2)
    }
    onButton("13 TimeSpaceTest") {
        furhat.stopSpeaking()
        goto(TimeSpaceTest)
    }
    onButton("14 CookieTest") {
        furhat.stopSpeaking()
        goto(CookieTest)
    }
    onButton("15 ReadingTest") {
        furhat.stopSpeaking()
        goto(ReadingTest)
    }
    onButton("Outro") {
        furhat.stopSpeaking()
        goto(Outro)
    }
    onButton("Goodbye") {
        furhat.stopSpeaking()
        goto(Goodbye)
    }
    onButton("say acknowledgement") {
        furhat.botSay(acknowledgement)
        goto(ReadingTest)
    }
}

val FlowControllButtons = partialState {
    onButton("Pausa") {
        if (thisState.name != "pausedState") {
            furhat.stopSpeaking()
            goto(Paused(thisState))
        }
    }
    onButton("Starta om") {
        furhat.stopSpeaking()
        goto(Intro)
    }
}

val GestureButtons = partialState {
    onButton("Expression: FurhatExpressing") {
        send(FurhatExpressing())
    }
    onButton("Expression: FurhatListening") {
        send(FurhatListening())
    }
    onButton("Expression: FurhatSpeaking") {
        send(FurhatSpeaking())
    }
    onButton("Expression: FurhatIdle") {
        send(FurhatIdle())
    }
}