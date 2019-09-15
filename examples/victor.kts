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
            sdamage(190.percent)
            sdamage(190.percent)
            sdamage(190.percent)
            sdamage(190.percent)
            sdamage(190.percent)
            chance(80.percent) {
                debuffs["bleed"](damageFormula(146.percent, skill = true)).apply(30.0)
            }
            wait(2.35)
        }

        s2(7500) {
            sdamage(957.percent)
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

    endIn(180.0)
}