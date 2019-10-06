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

        s1TransformBuff = Buffs.dignifiedSoul

        s1(2537) {
            Buffs.dignifiedSoul.pause()
            doSkill(216.percent, "s1", "hit")
            doSkill(216.percent, "s1", "hit")
            doSkill(216.percent, "s1", "hit")
            doSkill(216.percent, "s1", "hit")
            if (s1Transform) {
                Debuffs.bleed(snapshotSkill(132.percent, "s1", "bleed")).apply(duration = 30.0, chance = 80.percent)
            } else {
                poison(snapshotSkill(53.percent, "s1", "poison"), duration = 15.0, chance = 100.percent)
            }
            wait(2.5)
            Buffs.dignifiedSoul.start()
        }

        s2(4877, false) {
            wait(0.15)
            Buffs.dignifiedSoul(25.percent).selfBuff(10.0)
            wait(0.9)
        }

        acl {
            +s2 { sp.remaining("s1") <= 260 && +"x5" && !Debuffs.bleed.capped }
            +s1 { !sp.ready("s2") && !Debuffs.bleed.capped }
            +s3 { !s1Transform }
            +fs { s1Transform && +"x4" && sp.remaining("s1") <= 200 }
            +fsf { +"x5" }
        }
    }

    endIn(180.0)
}