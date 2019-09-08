stage {
    adventurer {
        name = "Aoi"
        str = 1881
        weapon = HeavensAcuity

        s1(2630) {
            damage(878.percent)
            wait(1.85)
        }

        s2(5280) {
            damage(790.percent)
            wait(1.85)
        }

        s3 = skill("s3", 8030) {
            damage(354.percent)
            damage(354.percent)
            damage(354.percent)
            wait(2.65)
        }.bound()

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
                println("${"%.3f".format(totalDamage / timeline.time)} dps")
                end()
            }
        }
    }
}