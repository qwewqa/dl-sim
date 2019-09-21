stage {
    adventurer {
        name = "Aoi"
        element = FLAME
        str = 494
        ex = Coabilities["Str"]
        weapon = Weapons["Heaven's Acuity"]
        dragon = Dragons["Sakuya"]
        wp = Wyrmprints["RR", "CE"]

        s1(2630) {
            +skillAtk(878.percent, "s1")
            wait(1.85)
        }

        s2(5280) {
            +skillAtk(790.percent, "s2")
            wait(1.85)
        }

        acl {
            +s1 { +"x5" }
            +s2 { +"x5" }
            +s3 { +"x5" }
            +fsf { +"x5" }
        }
    }

    // We can easily just add a second adventurer block
    adventurer {
        name = "Serena"
        element = FLAME
        str = 443
        ex = Coabilities["Sword"]
        weapon = Weapons["Levatein"]
        dragon = Dragons["Arctos"]
        wp = Wyrmprints["RR"] + Wyrmprints["CE"]

        a1 = Abilities["barrage obliteration"](6.percent)
        a3 = Abilities["barrage devastation"](3.percent)

        s1(2500) {
            Buffs["crit rate"](10.percent).selfBuff(5.0)
            val s1hit = skillAtk(350.percent, "s1")
            +s1hit
            +s1hit
            wait(1.55)
        }

        s2(4593) {
            val s2hit = skillAtk(169.percent, "s2")
            +s2hit
            +s2hit
            +s2hit
            +s2hit
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
        hp = 1_000_000 // if an hp is specified stage will end at 0 hp
        element = WIND
    }
}