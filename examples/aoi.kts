stage {
    adventurer {
        name = "Aoi"
        element = Flame
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

    endIn(180.0)
}