/*
 * Copyright 2019-2024 JetBrains s.r.o. and contributors.
 * Use of this source code is governed by the Apache 2.0 License that can be found in the LICENSE.txt file.
 */

package kotlinx.datetime.test

import kotlinx.datetime.*
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.*

//import org.junit.jupiter.api.Test

private inline fun assertThrows(block: () -> Unit): Unit = try {
    block()
    throw AssertionError("Expected an exception to be thrown")
} catch (_: Throwable) {
}

private inline fun assertDoesNotThrow(block: () -> Unit): Unit = try {
    block()
} catch (_: Throwable) {
    throw AssertionError("Expected no exception to be thrown")
}

class Reproduce {
    @Test
    fun lol() {
        val a = kotlin.time.Duration.parse("PT+-2H")
        println(a)
    }

    @Test
    fun lal() {
        val s = "P0D" //T2H"
        val kt = assertDoesNotThrow { kotlin.time.Duration.parseIsoString(s) }
    }

    @Test
    fun until() {
        val first = kotlinx.datetime.LocalDate(year = 0, monthNumber = 4, dayOfMonth = 1)
        val second = kotlinx.datetime.LocalDate(year = 0, monthNumber = 5, dayOfMonth = 31)

        val ktPeriod = first.periodUntil(second)

        println(ktPeriod)
    }

    @Test
    fun until2() {
        val firstDate = kotlinx.datetime.LocalDate(year = 0, monthNumber = 5, dayOfMonth = 31)
        val secondDate = kotlinx.datetime.LocalDate(year = 0, monthNumber = 4, dayOfMonth = 1)

        val ktPeriod = firstDate.periodUntil(secondDate)

        println(ktPeriod)
    }

    @Test
    fun until3() {
        val firstDate = kotlinx.datetime.LocalDate(year = 0, monthNumber = 5, dayOfMonth = 31)
        val secondDate = kotlinx.datetime.LocalDate(year = 0, monthNumber = 4, dayOfMonth = 1)

        val ktPeriod = firstDate.atStartOfDayIn(TimeZone.UTC)
            .periodUntil(secondDate.atStartOfDayIn(TimeZone.UTC), TimeZone.UTC)

        println(ktPeriod)
    }

    @Test
    fun exampleFromWiki() {
        val secondDate = kotlinx.datetime.LocalDate(year = 2001, monthNumber = 3, dayOfMonth = 14)
        val firstDate = kotlinx.datetime.LocalDate(year = 2003, monthNumber = 12, dayOfMonth = 25)

        val ktPeriod = firstDate.atStartOfDayIn(TimeZone.UTC)
            .periodUntil(secondDate.atStartOfDayIn(TimeZone.UTC), TimeZone.UTC)

        println(ktPeriod)
    }

    @Test
    fun pm() {
        val firstDate = kotlinx.datetime.LocalDate(year = 0, monthNumber = 5, dayOfMonth = 31)
        val period = kotlinx.datetime.DatePeriod(months = -1, days = -29)
        println(firstDate + period)
        println(firstDate + period - period)
    }

    @Test
    fun aaa() {
        val b = (Long.MAX_VALUE / 2).toDuration(DurationUnit.SECONDS)

        println(b.toString())
    }

    @Test
    fun bbb() {
        val maxSecond = 31556889864403199L
        Instant.fromEpochSeconds(maxSecond)
    }

    @Test
    fun kotlinDurationFromString() {
        // with `repeat(16)` parse works as expected
        val s = "PT" + "0".repeat(17) + "M"
        val kotlinDuration = kotlin.time.Duration.parseIsoString(s)

        assertTrue(kotlinDuration.isInfinite())
    }

    //    @Test
    fun datePeriodParse() {
        val s = "p"
        assertDoesNotThrow { kotlinx.datetime.DateTimePeriod.parse(s) }
        assertDoesNotThrow { kotlinx.datetime.DatePeriod.parse(s) }

        assertThrows { kotlin.time.Duration.parseIsoString(s) }
    }

    @Test
    fun instantFromString() {
        val s = "-211242-10-21t20:20:44+10"
        assertDoesNotThrow { kotlinx.datetime.Instant.parse(s) }
    }

    @Test
    fun datePeriodToJavaPeriod() {
        val kotlinDatePeriod = kotlinx.datetime.DatePeriod(months = 88)
        println(kotlinDatePeriod) // P7Y4M
    }

    @Test
    fun utcOffsetParse() {
        val s = "+1"
        assertThrows { kotlinx.datetime.UtcOffset.Companion.parse(s) }
    }

    @Test
    fun localDateParseVsIsoParse() {
//        "+0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002222-07-22"
        val s = "+" + "0".repeat(7) + "2222-07-22"
        assertThrows { kotlinx.datetime.LocalDate.parse(s) }
        assertDoesNotThrow { kotlinx.datetime.LocalDate.Formats.ISO.parse(s) }
    }

    @Test
    fun localDateTimeParseVsIsoParse() {
//        "+0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002222-07-22"
        val s = "+" + "0".repeat(7) + "2020-08-30T18:43"
        assertThrows { kotlinx.datetime.LocalDateTime.parse(s) }
        assertDoesNotThrow { kotlinx.datetime.LocalDateTime.Formats.ISO.parse(s) }
    }
}
