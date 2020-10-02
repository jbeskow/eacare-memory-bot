package furhatos.app.memorybot.flow.useractivity

import furhatos.app.memorybot.UserActivityPollDoneEvent
import furhatos.app.memorybot.UserActivityPollEndEvent
import furhatos.app.memorybot.UserActivityPollStartEvent
import furhatos.app.memorybot.log
import furhatos.app.memorybot.services.farmiService
import furhatos.flow.kotlin.*

const val DEFAULT_POLL_INACTIVITY_MAX = 8.0

fun UserActivityPoll(max_inactivity_ms : Double = DEFAULT_POLL_INACTIVITY_MAX) : State = state {
    onEntry {
        log.stateEnter(thisState)
    }
    onTime(repeat = 1000) {
        val activity = farmiService.getUserActivity()
        if (activity.statusCode == 200 && activity.text != "null") {
            val latestUserActivity = activity.text.toDouble()
            if (latestUserActivity > max_inactivity_ms && latestUserActivity < max_inactivity_ms + 2) {
                send(UserActivityPollDoneEvent())
                goto(UserActivityPollIdle)
            }
        }
    }
    onEvent<UserActivityPollEndEvent> {
        goto(UserActivityPollIdle)
    }
}

val UserActivityPollIdle : State = state {
    onEvent<UserActivityPollStartEvent> {
        goto(UserActivityPoll(it.max_inactivity_ms))
    }
}
