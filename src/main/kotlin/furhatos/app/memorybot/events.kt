package furhatos.app.memorybot

import furhatos.app.memorybot.flow.useractivity.DEFAULT_POLL_INACTIVITY_MAX
import furhatos.event.Event

// Expressiveness state transition events
class FurhatExpressing : Event("FurhatExpressing")
class FurhatListening : Event("FurhatListening")
class FurhatSpeaking : Event("FurhatSpeaking")
class FurhatIdle : Event("FurhatIdle")
class AcknowledgmentGesture : Event("AcknowledgmentGesture")


// Farmisystem events

class FarmiIpSuccess(val ip : String) : Event("FarmiIpSuccess")
class FarmiIpFail(val ip : String) : Event("FarmiIpFail")
class RequestFarmiIpResponse(val ip : String) : Event("RequestFarmiIpResponse")
class UserActivityPollDoneEvent : Event("UserActivityDoneEvent")
class UserActivityPollStartEvent(val max_inactivity_ms: Double = DEFAULT_POLL_INACTIVITY_MAX) : Event("UserActivityPollStartEvent")
class UserActivityPollEndEvent : Event("UserActivityPollEndEvent")

