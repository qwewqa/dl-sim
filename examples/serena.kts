stage {
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
        element = WIND
    }

    endIn(180.0)
}