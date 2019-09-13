stage {
    adventurer {
        name = "Serena"
        element = FLAME
        str = 443
        weapon = weapons["Levatein"]
        dragon = dragons["Arctos"]
        wp = wyrmprints["RR"] + wyrmprints["CE"]

        a1 = Abilities["barrage obliteration"](6.percent)
        a3 = Abilities["barrage devastation"](3.percent)

        s1(2500) {
            buffs["crit rate"](10.percent).selfBuff(5.0)
            damage(350.percent)
            damage(350.percent)
            wait(1.55)
        }

        s2(4593) {
            damage(169.percent)
            damage(169.percent)
            damage(169.percent)
            damage(169.percent)
            wait(2.2)
        }

        acl {
            +s1
            +s2 { +"fs" }
            +s3 { +"fs" }
            +fs { +"x3" }
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