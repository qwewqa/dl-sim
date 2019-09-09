stage {
    adventurer {
        name = "Aoi"
        element = FLAME
        str = 494
        ex = coability("str", 10.percent)
        weapon = HeavensAcuity

        s1(2630) {
            damage(878.percent)
            wait(1.85)
        }

        s2(5280) {
            damage(790.percent)
            wait(1.85)
        }

        acl {
            +s1 { +"x5" }
            +s2 { +"x5" }
            +s3 { +"x5" }
        }
    }

    enemy {
        def = 10.0
        element = WIND
    }

    endIn(180.0)

    onEnd {
        println("${"%.3f".format(enemy.totalDamage / timeline.time)} dps")
    }
}