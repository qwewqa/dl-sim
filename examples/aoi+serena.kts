stage {
    adventurer {
        name = "Aoi"
        element = FLAME
        str = 494
        ex = coabilities["Str"]
        weapon = weapons["Heaven's Acuity"]
        dragon = dragons["Sakuya"]
        wp = wyrmprints["CE"] + wyrmprints["RR"]

        s1(2630) {
            sdamage(878.percent)
            wait(1.85)
        }

        s2(5280) {
            sdamage(790.percent)
            wait(1.85)
        }

        acl {
            +s1 { +"x5" }
            +s2 { +"x5" }
            +s3 { +"x5" }
            +fsf { +"x5" }
        }
    }

    enemy {
        def = 10.0
        element = WIND
    }

    // We can easily just add a second adventurer block
    adventurer {
        name = "Serena"
        element = FLAME
        str = 443
        ex = coabilities["Sword"]
        weapon = weapons["Levatein"]
        dragon = dragons["Arctos"]
        wp = wyrmprints["RR"] + wyrmprints["CE"]

        a1 = abilities["barrage obliteration"](6.percent)
        a3 = abilities["barrage devastation"](3.percent)

        s1(2500) {
            buffs["crit rate"](10.percent).selfBuff(5.0)
            sdamage(350.percent)
            sdamage(350.percent)
            wait(1.55)
        }

        s2(4593) {
            sdamage(169.percent)
            sdamage(169.percent)
            sdamage(169.percent)
            sdamage(169.percent)
            wait(2.2)
        }

        acl {
            +s1
            +s2 { +"fs" }
            +s3 { +"fs" }
            +fs { +"x3" }
        }
    }

    endIn(180.0)
}