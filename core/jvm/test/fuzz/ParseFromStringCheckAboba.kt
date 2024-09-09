/*
 * Copyright 2019-2024 JetBrains s.r.o. and contributors.
 * Use of this source code is governed by the Apache 2.0 License that can be found in the LICENSE.txt file.
 */

package fuzz

import com.code_intelligence.jazzer.api.FuzzedDataProvider
import com.code_intelligence.jazzer.junit.FuzzTest
import kotlinx.datetime.DateTimeFormatException
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DayOfWeekNames
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.assertDoesNotThrow

@Suppress("UnusedEquals")
class ParseFromStringCheckAboba {

    private inline fun isFine(block: () -> Unit) = try {
        block()
    } catch (_: DateTimeFormatException) {
    }

    @FuzzTest
    fun onlyEquals(data: FuzzedDataProvider): Unit = with(data) {
        val a = data.consumeInstant()
        val b = data.consumeInstant()
        isFine { a == b }
    }


    @FuzzTest
    fun localDateIso(data: FuzzedDataProvider): Unit = with(data) {
        val s = consumeString(100)
        isFine { kotlinx.datetime.LocalDate.Formats.ISO.parse(s) }
    }

    @FuzzTest
    fun localDateTime(data: FuzzedDataProvider): Unit = with(data) {
        val s = consumeString(1000)
        isFine { kotlinx.datetime.LocalDateTime.parse(s) }
    }

    @FuzzTest
    fun localDateMyFormat(data: FuzzedDataProvider): Unit = with(data) {
        val format = kotlinx.datetime.LocalDate.Format {
            dayOfMonth()
            dayOfWeek(DayOfWeekNames.ENGLISH_FULL)
            monthNumber()
        }
        val s = consumeString(1000)
        isFine { format.parse(s) }
    }

    @FuzzTest
    fun localDateIsoBasic(data: FuzzedDataProvider): Unit = with(data) {
        val s = consumeString(1000)
        val format = kotlinx.datetime.LocalDate.Formats.ISO_BASIC
        isFine { format.parse(s) }
    }

    @FuzzTest(maxDuration = "10m")
    fun instant(data: FuzzedDataProvider): Unit = with(data) {
        isFine { Instant.parse(consumeString(100)) }
    }
}