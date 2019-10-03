stage{
    Adventurers["addis"] {
        wyrmprints = Wyrmprints["RR", "BN"]
        weapon = Weapons["wind 5t3 blade"]
        dragon = Dragons["vayu"]
        acl {
            +s2 { "s1".remaining < 260 && +"x5" && !Debuffs["bleed"].capped }
            +s1 { !"s2".ready && !Debuffs["bleed"].capped }
            +s3 { !s1Transform }
            +fs { s1Transform && +"x4" && "s1".remaining <= 200 }
            +fsf { +"x5" }
        }
    }
    Adventurers["noelle"] {
        rotation {
            loop = "c5fs c5 s1"
        }
    }
    enemy {
        def = 10.0
        hp = 1_000_000
        element = Water
    }
    endIn(180.0)
}