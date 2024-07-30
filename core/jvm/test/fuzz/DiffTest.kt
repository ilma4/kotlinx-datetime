/*
 * Copyright 2019-2024 JetBrains s.r.o. and contributors.
 * Use of this source code is governed by the Apache 2.0 License that can be found in the LICENSE.txt file.
 */

package fuzz

import com.code_intelligence.jazzer.api.FuzzedDataProvider
import com.code_intelligence.jazzer.junit.FuzzTest
import fuzz.utils.compareTest
import kotlinx.datetime.*
import org.junit.jupiter.api.Test
import java.time.Period
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.time.toJavaDuration
import kotlin.time.toKotlinDuration

class DiffTest {
    class datePeriodDiff {
        @FuzzTest(maxDuration = "2h")
        fun diff(data: FuzzedDataProvider) {
            val mod = 0 //5000
            val a = data.consumeDate(-mod, mod)
            val b = data.consumeDate(-mod, mod)

            val kotlinRes = runCatching<DatePeriod> { a.periodUntil(b) }
            val javaRes = runCatching<Period> { a.toJavaLocalDate().until(b.toJavaLocalDate()) }

            assertEquals(kotlinRes.isSuccess, javaRes.isSuccess)

            if (kotlinRes.isFailure || javaRes.isFailure) return

            val kotlinVal = kotlinRes.getOrThrow()
            val javaVal = javaRes.getOrThrow()

            val javaFromKotlin = kotlinVal.toJavaPeriod()
            val kotlinFromJava = javaVal.toKotlinDatePeriod()

            assertEquals(kotlinVal, kotlinFromJava)
            assertEquals(javaVal, javaFromKotlin)
        }
    }

    @Test
    fun reproduce() {
        val first = "0000-05-31"
        val second = "0000-04-01"

        val a = LocalDate.parse(first)
        val b = LocalDate.parse(second)

        val kotlinRes = runCatching<DatePeriod> { a.periodUntil(b) }
        val javaRes = runCatching<Period> { a.copyj().until(b.copyj()) }

        assertEquals(kotlinRes.isSuccess, javaRes.isSuccess)
        if (kotlinRes.isFailure || javaRes.isFailure) return

        val kotlinVal = kotlinRes.getOrThrow()
        val javaVal = javaRes.getOrThrow()

        val javaFromKotlin = kotlinVal.toJavaPeriod()
        val kotlinFromJava = javaVal.toKotlinDatePeriod()

        assertEquals(kotlinVal, kotlinFromJava)
        assertEquals(javaVal, javaFromKotlin)

    }

    class instantDiff {
        @FuzzTest(maxDuration = "2h")
        fun diff(data: FuzzedDataProvider) = with(data) {
            val kfirst = consumeInstant()
            val ksecond = consumeInstant()

            val jfirst = kfirst.toJavaInstant()
            val jsecond = ksecond.toJavaInstant()

            compareTest(
                createKotlin = { kfirst.until(ksecond, DateTimeUnit.SECOND) },
                createJava = {
                    jfirst.until(jsecond, ChronoUnit.SECONDS)
                },
                kotlinToJava = { it },
                javaToKotlin = { it }
            )
        }
    }

    class instantTZDiff {
        @FuzzTest(maxDuration = "2h")
        fun diff(data: FuzzedDataProvider) = with(data) {
            val kfirst = consumeInstant()
            val ksecond = consumeInstant()

            val ktz = consumeTimeZone()
            val jtz = ktz.toJavaZoneId()

            val jfirst = kfirst.copyj().atZone(jtz)
            val jsecond = ksecond.copyj().atZone(jtz)


            compareTest(
                createKotlin = { kfirst.until(ksecond, DateTimeUnit.SECOND, ktz) },
                createJava = {
                    jfirst.until(jsecond, ChronoUnit.SECONDS)
                },
                kotlinToJava = { it },
                javaToKotlin = { it }
            )
        }
    }

    class InstantToDateTimeAndBack {
        @FuzzTest(maxDuration = "2h")
        fun test(data: FuzzedDataProvider) = with(data) {
            val d = consumeDateTime()
            val tz = consumeTimeZone()
            compareTest(
                createKotlin = { d },
                createJava = { d.toInstant(tz).toLocalDateTime(tz) },
                javaToKotlin = { it },
                kotlinToJava = { it }
            )
        }
    }

    class CompareDurations {
        @FuzzTest(maxDuration = "2h")
        fun test(data: FuzzedDataProvider) = with(data) {
            val first = consumeInstant(nanoFrom = 0, nanoTo = 0)
            val second = consumeInstant(nanoFrom = 0, nanoTo = 0)

            compareTest(
                createKotlin = { second - first },
                createJava = { java.time.Duration.between(first.copyj(), second.copyj()) },
                kotlinToJava = { it.toJavaDuration() },
                javaToKotlin = { it.toKotlinDuration() }
            )
        }
    }
}
