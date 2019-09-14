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

    endIn(180.0)
}