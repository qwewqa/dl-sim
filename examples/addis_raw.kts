stage {
    adventurer {
        name = "Addis"
        element = Wind
        str = 509
        ex = Coabilities["Str"]
        weapon = Weapons["Wind 5t3 Blade"]
        dragon = Dragons["Vayu"]
        wyrmprints = Wyrmprints["RR", "BN"]

        a1 = Abilities["Punisher"](8.percent, Conditions["Bleeding"])
        a3 = Abilities["Broken Punisher"](20.percent)

        val s2buff = Buffs["Dignified Soul"]

        s1(2537) {
            s2buff.pause()
            val s1hit = skillAtk(216.percent, "s1", "hit")
            +s1hit
            +s1hit
            +s1hit
            +s1hit
            if (s2buff.on) {
                Debuffs.bleed(skillAtk(132.percent, "s1", "bleed").snapshot()).apply(duration = 30.0, chance = 80.percent)
            } else {
                poison(skillAtk(53.percent, "s1", "poison").snapshot(), duration = 15.0, chance = 100.percent)
            }
            wait(2.5)
            s2buff.start()
        }

        s2(4877) {
            wait(0.15)
            s2buff(25.percent).selfBuff(10.0)
            wait(0.9)
        }

        acl {
            +s2 { sp.remaining("s1") <= 260 && +"x5" && !Debuffs["bleed"].capped }
            +s1 { !sp.ready("s2") && !Debuffs["bleed"].capped }
            +s3 { !s2buff.on }
            +fs { s2buff.on && +"x4" && sp.remaining("s1") <= 200 }
            +fsf { +"x5" }
        }
    }

    endIn(180.0)
}