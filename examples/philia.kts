stage {
    adventurer {
        name = "Philia"
        element = WIND
        str = 469
        weapon = Weapons["Stellar Pegasus"]
        wp = Wyrmprints["RR", "FoG"]
        dragon = Dragons["Vayu"]
        ex = Coabilities["Bow"]

        a1 = Abilities["str"](10.percent, Conditions["hp100"])

        s1(2395) {
            val s1bolt = skillAtk(262.percent, "s1")
            +s1bolt
            +s1bolt
            +s1bolt
            wait(2.05)
        }

        s2(5051) {
            +skillAtk(667.percent, "s2")
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