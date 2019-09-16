stage {
    adventurer {
        name = "Rena"
        element = FLAME
        str = 471
        ex = coabilities["Str"]
        weapon = weapons["Heaven's Acuity"]
        dragon = dragons["Sakuya"]
        wp = wyrmprints["RR"] + wyrmprints["EE"]

        var stance = 1
        s1(3303) {
            enemy.afflictions.burn(
                chance = 120.percent,
                damage = damageFormula(97.percent, skill = true),
                duration = 12.0
            )
            val killer = if (stance == 3 && enemy.afflictions.burning) 1.8 else 1.0
            sdamage(72.percent * killer)
            sdamage(72.percent * killer)
            sdamage(72.percent * killer)
            sdamage(72.percent * killer)
            sdamage(665.percent * killer)
            if (stance >= 2) buffs["crit rate"](10.percent).selfBuff(15.0)
            stance = when(stance) {
                1 -> 2
                2 -> 3
                else -> 1
            }
            wait(2.45)
        }

        s2(6582) {
            wait(0.15)
            buffs["crit damage"](50.percent).selfBuff(20.0)
            sp.charge(100.percent, "s1")
            wait(0.90)
        }

        acl {
            +s1 { +"idle" || +"ui" || cancel }
            +s2 { +"s1" }
            +s3 { +"fs" }
            +fs { +"x5" }
        }
    }

    enemy {
        def = 10.0
        element = WIND
        afflictions.burnRes = 0.percent
    }

    endIn(180.0)
}