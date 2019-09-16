stage {
    adventurer {
        name = "Philia"
        element = WIND
        str = 469
        weapon = weapons["Stellar Pegasus"]
        wp = wyrmprints["RR"] + wyrmprints["FoG"]
        dragon = dragons["Vayu"]
        ex = coabilities["Bow"]

        a1 = abilities["str"](10.percent, Conditions["hp100"])

        s1(2395) {
            sdamage(262.percent)
            sdamage(262.percent)
            sdamage(262.percent)
            wait(2.05)
        }

        s2(5051) {
            sdamage(667.percent)
            enemy.afflictions.paralysis(
                chance = 90.percent,
                damage = damageFormula(60.percent, skill = true),
                duration = 12.0
            )
            wait(1.0)
        }

        acl {
            +s1 { +"fs" }
            +s2 { +"fs" }
            +s3 { +"fs" }
            +fs { +"x4" }
        }
    }

    enemy {
        def = 10.0
        element = WATER
        afflictions.paralysisRes = 0.percent
    }

    endIn(180.0)
}