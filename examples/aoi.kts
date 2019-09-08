stage {
    adventurer {
        name = "Aoi"
        str = 1881
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

        listen("dmg") {
            if (totalDamage >= 500_000) {
                end()
            }
        }
    }

    onEnd {
        println("${"%.3f".format(enemy.totalDamage / timeline.time)} dps")
    }
}