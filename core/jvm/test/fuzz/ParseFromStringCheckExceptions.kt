/*
 * Copyright 2019-2024 JetBrains s.r.o. and contributors.
 * Use of this source code is governed by the Apache 2.0 License that can be found in the LICENSE.txt file.
 */

package fuzz

import com.code_intelligence.jazzer.api.FuzzedDataProvider
import com.code_intelligence.jazzer.junit.FuzzTest
import kotlinx.datetime.DateTimeFormatException
import kotlinx.datetime.Instant
import kotlinx.datetime.format.DayOfWeekNames

@Suppress("UnusedEquals")
class ParseFromStringCheckExceptions {

    private inline fun isFine(block: () -> Unit) = try {
        block()
    } catch (_: DateTimeFormatException) {
    }

    @FuzzTest(maxDuration = "2h")
    fun onlyEquals(data: FuzzedDataProvider): Unit = with(data) {
        val a = data.consumeInstant()
        val b = data.consumeInstant()
        isFine { a == b }
    }


    @FuzzTest(maxDuration = "2h")
    fun localDateIso(data: FuzzedDataProvider): Unit = with(data) {
        val s = consumeString(100)
        isFine { kotlinx.datetime.LocalDate.Formats.ISO.parse(s) }
    }

    @FuzzTest(maxDuration = "2h")
    fun localDateTime(data: FuzzedDataProvider): Unit = with(data) {
        val s = consumeString(1000)
        isFine { kotlinx.datetime.LocalDateTime.parse(s) }
    }

    @FuzzTest(maxDuration = "2h")
    fun localDateMyFormat(data: FuzzedDataProvider): Unit = with(data) {
        val format = kotlinx.datetime.LocalDate.Format {
            dayOfMonth()
            dayOfWeek(DayOfWeekNames.ENGLISH_FULL)
            monthNumber()
        }
        val s = consumeString(1000)
        isFine { format.parse(s) }
    }

    @FuzzTest(maxDuration = "2h")
    fun localDateIsoBasic(data: FuzzedDataProvider): Unit = with(data) {
        val s = consumeString(1000)
        val format = kotlinx.datetime.LocalDate.Formats.ISO_BASIC
        isFine { format.parse(s) }
    }

    @FuzzTest(maxDuration = "2h")
    fun instant(data: FuzzedDataProvider): Unit = with(data) {
        isFine { Instant.parse(consumeString(100)) }
    }
}