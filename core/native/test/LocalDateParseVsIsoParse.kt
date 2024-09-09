/*
 * Copyright 2019-2024 JetBrains s.r.o. and contributors.
 * Use of this source code is governed by the Apache 2.0 License that can be found in the LICENSE.txt file.
 */

package kotlinx.datetime.test

import kotlin.test.Test

class LocalDateParseVsIsoParse {
    @Test
    fun yeah(){
        val s = "+" + "0".repeat(7)  + "2222-07-22"
        println(kotlinx.datetime.LocalDate.parse(s))
        println(kotlinx.datetime.LocalDate.Formats.ISO.parse(s))
    }
}