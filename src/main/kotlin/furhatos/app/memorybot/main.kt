package furhatos.app.memorybot

import furhatos.app.memorybot.flow.main.*
import furhatos.skills.Skill
import furhatos.flow.kotlin.*
import furhatos.skills.HostedGUI

fun initGUI() = MemoryBotGui
val MemoryBotGui = HostedGUI("MemoryBotGui", path="assets/webTemplates/BASIC/dist", port = 1234)

class MemorybotSkill : Skill() {
    override fun start() {
        initGUI()

        Flow(name = "MainFlow").run(WaitingForFarmiConnection)
    }
}

fun main(args: Array<String>) {
    Skill.main(args)
}
