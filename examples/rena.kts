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
            enemy.afflictions.burn(120.percent, damageFormula(97.percent, skill = true), 12.0)
            when(stance) {
                1 -> {
                    sdamage(72.percent)
                    sdamage(72.percent)
                    sdamage(72.percent)
                    sdamage(72.percent)
                    sdamage(665.percent)
                    stance = 2
                }
                2 -> {
                    sdamage(72.percent)
                    sdamage(72.percent)
                    sdamage(72.percent)
                    sdamage(72.percent)
                    sdamage(665.percent)
                    buffs["crit rate"](10.percent).selfBuff(15.0)
                    stance = 3
                }
                3 -> {
                    val killer = if (enemy.afflictions.burning) 1.8 else 1.0
                    sdamage(72.percent * killer)
                    sdamage(72.percent * killer)
                    sdamage(72.percent * killer)
                    sdamage(72.percent * killer)
                    sdamage(665.percent * killer)
                    buffs["crit rate"](10.percent).selfBuff(15.0)
                    stance = 1
                }
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