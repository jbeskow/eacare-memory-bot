package furhatos.app.memorybot.flow.main

import furhatos.flow.kotlin.FlowControlRunner
import furhatos.flow.kotlin.users

fun startListenTimer(context : FlowControlRunner, listenTimeMs : Int = 55000) : Unit {
    context.users.current.tests.listenStartAt = System.currentTimeMillis()
    context.users.current.tests.listenTimeMs = listenTimeMs
}

fun listenTimerTimeLeft(context : FlowControlRunner) : Int {
    val timeLeft = (context.users.current.tests.listenTimeMs
        - System.currentTimeMillis()
        + context.users.current.tests.listenStartAt
    ).toInt()
    return if (timeLeft > 0) {
        timeLeft
    } else {
        0
    }
}