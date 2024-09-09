/*
 * Copyright 2019-2024 JetBrains s.r.o. and contributors.
 * Use of this source code is governed by the Apache 2.0 License that can be found in the LICENSE.txt file.
 */

package fuzz

import com.code_intelligence.jazzer.api.FuzzedDataProvider
import com.code_intelligence.jazzer.junit.FuzzTest
import fuzz.utils.compareTest
import kotlinx.datetime.*
import org.junit.jupiter.api.BeforeAll
import java.time.Period
import java.time.temporal.ChronoUnit
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.deleteRecursively
import kotlin.test.assertEquals
import kotlin.time.toKotlinDuration

class DiffTest {
    companion object {
        @OptIn(ExperimentalPathApi::class)
        @JvmStatic
        @BeforeAll
        fun cleanCorpus() {
            Path(".cifuzz-corpus").deleteRecursively()
        }
    }

    @FuzzTest(/*maxDuration = "2h"*/)
    fun diffDatePeriod(data: FuzzedDataProvider) {
        val mod = 0 //5000
        val a = data.consumeDate(-mod, mod)
        val b = data.consumeDate(-mod, mod)

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

    @FuzzTest(maxDuration = "2h")
    fun diffDatePeriodNonNegative(data: FuzzedDataProvider) {
        val aa = data.consumeDate()
        val bb = data.consumeDate()

        val a = if (aa < bb) aa else bb
        val b = if (aa < bb) bb else aa

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


    @FuzzTest(maxDuration = "1m")
    fun diffInstant(data: FuzzedDataProvider) = with(data) {
        val kfirst = consumeInstant()
        val ksecond = consumeInstant()

        val jfirst = kfirst.copyj()
        val jsecond = ksecond.copyj()

        compareTest(
            firstBlock = { kfirst.until(ksecond, DateTimeUnit.SECOND) },
            secondBlock = { jfirst.until(jsecond, ChronoUnit.SECONDS) },
        )
    }

    @FuzzTest(maxDuration = "2h")
    fun diffInstantWithZoneId(data: FuzzedDataProvider) = with(data) {
        val kfirst = consumeInstant()
        val ksecond = consumeInstant()

        val ktz = consumeTimeZone()
        val jtz = ktz.toJavaZoneId()

        val jfirst = kfirst.copyj().atZone(jtz)
        val jsecond = ksecond.copyj().atZone(jtz)


        compareTest(
            firstBlock = { kfirst.until(ksecond, DateTimeUnit.SECOND, ktz) },
            secondBlock = { jfirst.until(jsecond, ChronoUnit.SECONDS) },
        )
    }

    @FuzzTest(maxDuration = "2h")
    fun diffDateTimeTimeZone(data: FuzzedDataProvider) = with(data) {
        val d = consumeDateTime()
        val tz = consumeTimeZone()
        compareTest(
            firstBlock = { d },
            secondBlock = { d.toInstant(tz).toLocalDateTime(tz) },
        )
    }

    @FuzzTest(maxDuration = "2h")
    fun diffInstantNoNanos(data: FuzzedDataProvider) = with(data) {
        val first = consumeInstant(nanoFrom = 0, nanoTo = 0)
        val second = consumeInstant(nanoFrom = 0, nanoTo = 0)

        compareTest(
            createKotlin = { second - first },
            createJava = { java.time.Duration.between(first.copyj(), second.copyj()) },
            kotlinToJava = { it.copyj() },
            javaToKotlin = { it.toKotlinDuration() }
        )
    }

}
