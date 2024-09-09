/*
 * Copyright 2019-2024 JetBrains s.r.o. and contributors.
 * Use of this source code is governed by the Apache 2.0 License that can be found in the LICENSE.txt file.
 */

package fuzz

import com.code_intelligence.jazzer.api.FuzzedDataProvider
import com.code_intelligence.jazzer.junit.FuzzTest
import fuzz.utils.compareTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

class IsoFormats {

    @FuzzTest
    fun localDate(data: FuzzedDataProvider): Unit = with(data) {
        val s = data.consumeString(1000)
        compareTest(
            { LocalDate.parse(s) },
            { LocalDate.Formats.ISO.parse(s) }
        )
    }

    @FuzzTest
    fun localDateTime(data: FuzzedDataProvider): Unit = with(data) {
        val s = data.consumeString(1000)
        compareTest(
            { LocalDateTime.parse(s) },
            { LocalDateTime.Formats.ISO.parse(s) }
        )
    }
}