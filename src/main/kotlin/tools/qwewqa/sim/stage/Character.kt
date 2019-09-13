package tools.qwewqa.sim.stage

import tools.qwewqa.sim.buffs.BuffBehavior

interface Character {
    val stage: Stage
    val stats: StatMap
    val buffStacks: MutableMap<BuffBehavior, BuffBehavior.Stack>
}