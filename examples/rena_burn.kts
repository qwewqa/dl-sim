stage {
    Adventurers["Rena"] {
        acl {
            +s1 { default }
            +s2 { +"s1" }
            +s3 { +"fs" }
            +fs { +"x5" }
        }
    }

    enemy {
        def = 10.0
        afflictions.burn.resist = 0.percent
    }

    endIn(180.0)
}