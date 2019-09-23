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
    serena()

    serena {
        name = "Serena 2"
    }

    enemy {
        def = 10.0
        hp = 1_000_000 // if an hp is specified stage will end at 0 hp
        element = WIND
    }
}