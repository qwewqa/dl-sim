stage {
    adventurer {
        name = "Victor"
        element = WIND
        str = 494
        ex = Coabilities["Str"]
        weapon = Weapons["Anemone"]
        dragon = Dragons["Vayu"]
        wp = Wyrmprints["RR", "BN"]

        a1 = Abilities["str"](13.percent, Conditions["hp70"])

        s1(2838) {
            val s1Hit = skillAtk(190.percent, "s1", "hit")
            +s1Hit
            +s1Hit
            +s1Hit
            +s1Hit
            +s1Hit
            chance(80.percent) {
                Debuffs["bleed"](skillAtk(146.percent, "s1", "bleed").hit()).apply(30.0)
            }
            wait(2.35)
        }

        s2(7500) {
            +skillAtk(957.percent, "s2")
            wait(2.7)
        }

        acl {
            +s1 { +"idle" || cancel }
            +s2 { +"x5" }
            +s3 { +"x5" }
            +fsf { +"x5" }
        }
    }

    enemy {
        def = 10.0
        element = WATER
    }

    endIn(180.0)
}