package furhatos.app.memorybot.utterances

import furhatos.app.memorybot.AcknowledgmentGesture
import furhatos.app.memorybot.phrases.*
import furhatos.flow.kotlin.behavior
import furhatos.flow.kotlin.utterance

val acknowledgement = utterance {
    random {
        block { + "$GOOD_JOB_1, $THANK_YOU_1." }
        block { + "$THANK_YOU_1, $GOOD_JOB_1." }
        block { + "$GOOD_JOB_2, $THANK_YOU_3." }
        block { + "$GOOD_JOB_2, $THANK_YOU_3." }
        block { + behavior { send(AcknowledgmentGesture()) }; + "$THANK_YOU_1." }
        block { + behavior { send(AcknowledgmentGesture()) }; + "$THANK_YOU_2." }
        block { + behavior { send(AcknowledgmentGesture()) }; + "$THANK_YOU_3." }
        block { + behavior { send(AcknowledgmentGesture()) }; + "$GOOD_JOB_1." }
        block { + behavior { send(AcknowledgmentGesture()) }; + "$GOOD_JOB_2." }
        block { + "$OK, $THANK_YOU_1." }
        block { + "$OK, $GOOD_JOB_2." }
    }
}