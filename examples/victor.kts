stage {
    adventurer {
        name = "Victor"
        element = WIND
        str = 494
        ex = coabilities["Str"]
        weapon = weapons["Anemone"]
        dragon = dragons["Vayu"]
        wp = wyrmprints["RR"] + wyrmprints["BN"]

        a1 = abilities["str"](13.percent, conditions["hp70"])

        s1(2838) {
            val s1Hit = skillAtk(190.percent, "s1", "hit")
            +s1Hit
            +s1Hit
            +s1Hit
            +s1Hit
            +s1Hit
            chance(80.percent) {
                debuffs["bleed"](skillAtk(146.percent, "s1", "bleed").hit()).apply(30.0)
            }
            wait(2.35)
        }

        s2(7500) {
            +skillAtk(957.percent, "s2")
            wait(2.7)
        }

        acl {
            +s1 { +"idle" || cancel }
            +s2 { seq == 5 }
            +s3 { seq == 5 }
        }
    }

    enemy {
        def = 10.0
        element = WATER
    }

    endIn(181.317)
}