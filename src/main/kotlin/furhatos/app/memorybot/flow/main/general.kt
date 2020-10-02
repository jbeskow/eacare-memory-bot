package furhatos.app.memorybot.flow.main

import furhatos.app.memorybot.*
import furhatos.app.memorybot.flow.expressive.ExpressIdleState
import furhatos.app.memorybot.flow.main.tests.*
import furhatos.app.memorybot.flow.useractivity.UserActivityPollIdle
import furhatos.app.memorybot.services.farmiService
import furhatos.app.memorybot.services.FARMI_IP
import furhatos.event.senses.SenseSkillGUIConnected
import furhatos.flow.kotlin.*
import furhatos.flow.kotlin.voice.AcapelaVoice
import furhatos.skills.HostedGUI
import furhatos.util.*

val testsMap = mapOf(
        1 to LineDrawing,
        2 to CubeDrawing,
        3 to ClockTest,
        4 to AnimalRecognition,
        5 to WordRecall,
        6 to NumberRecall,
        7 to LetterTest,
        8 to CountingTest,
        9 to SentenceTest,
        10 to WordsOnF,
        11 to SematicTest,
        12 to WordRecall2,
        13 to TimeSpaceTest,
        14 to CookieTest,
        15 to ReadingTest
)

val WaitingForFarmiConnection : State = state {
    onEntry {
        if (farmiService.testConnection()) {
            goto(Idle)
        }
    }
    onEvent("FarmiIpTest") {
        println(it)
        val url = it.get("url") as String
        if (farmiService.testConnection(url)) {
            send(FarmiIpSuccess(url))
        } else {
            send(FarmiIpFail(url))
        }
    }
    onEvent("FarmiIpSave") {
        val ip = it.get("ip") as String
        FARMI_IP = ip
        if (farmiService.testConnection()) {
            goto(Idle)
        } else {
            send(FarmiIpFail(FARMI_IP))
        }
    }

    onEvent("RequestFarmiIp") {
        send(RequestFarmiIpResponse(FARMI_IP))
    }
}

val Idle: State = state {

    init(setup())

    onEntry {
        log.stateEnter(thisState)
        call { farmiService.stopRecording() }
        call { farmiService.hideScreen() }
        send(FurhatIdle())
        if (users.count > 0) {
            furhat.attend(users.random)
            goto(Intro)
        } else {
            furhat.attendNobody()
        }
    }
    onUserEnter {
        furhat.attend(it)
        goto(Intro)
    }
    include(CommandBehavior)
    include(DebugButtons)
}

val RobotVoice = AcapelaVoice(name = "Samuel22k_HQ", gender = Gender.MALE, language=Language.SWEDISH_FI, rate = 0.8 )

private fun setup(): FlowControlRunner.() -> Unit {
    return {
        parallel(abortOnExit=false) { goto(ExpressIdleState) }
        parallel(abortOnExit=false) { goto(UserActivityPollIdle) }
        furhat.voice = RobotVoice
        furhat.setInputLanguage(Language.SWEDISH)
        furhat.setModel("bertil")
        furhat.setTexture("defaultRemix")
    }
}
