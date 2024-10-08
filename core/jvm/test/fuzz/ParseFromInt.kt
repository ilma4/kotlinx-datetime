/*
 * Copyright 2019-2024 JetBrains s.r.o. and contributors.
 * Use of this source code is governed by the Apache 2.0 License that can be found in the LICENSE.txt file.
 */

package fuzz

import com.code_intelligence.jazzer.api.FuzzedDataProvider
import com.code_intelligence.jazzer.junit.FuzzTest
import fuzz.utils.compareTest
import kotlinx.datetime.*

class ParseFromInt {
    @FuzzTest(maxDuration = "2h")
    fun instant(data: FuzzedDataProvider) {
        val milliseconds = data.consumeLong()
        compareTest(
            createKotlin = { Instant.fromEpochMilliseconds(milliseconds) },
            createJava = { java.time.Instant.ofEpochMilli(milliseconds) },
            javaToKotlin = { it.toKotlinInstant() },
            kotlinToJava = { it.toJavaInstant() },
            disableOkPrintln = true
        )
    }

    @FuzzTest(maxDuration = "2h")
    fun instantFromSeconds(data: FuzzedDataProvider) {
        val seconds = data.consumeLong(Long.MIN_VALUE / 1000L, Long.MAX_VALUE / 1000L)
        val nanos = data.consumeLong()
        compareTest(
            createKotlin = { Instant.fromEpochSeconds(seconds, nanos) },
            createJava = { java.time.Instant.ofEpochSecond(seconds, nanos) },
            javaToKotlin = { it.toKotlinInstant() },
            kotlinToJava = { it.toJavaInstant() },
            disableOkPrintln = true
        )
    }

    @FuzzTest(maxDuration = "2h")
    fun localDate(data: FuzzedDataProvider) {
        val epochDay = data.consumeInt()
        compareTest(
            createKotlin = { LocalDate.fromEpochDays(epochDay) },
            createJava = { java.time.LocalDate.ofEpochDay(epochDay.toLong()) },
            javaToKotlin = { it.toKotlinLocalDate() },
            kotlinToJava = { it.toJavaLocalDate() },
            disableOkPrintln = true
        )
    }
}
