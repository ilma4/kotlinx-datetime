/*
 * Copyright 2019-2024 JetBrains s.r.o. and contributors.
 * Use of this source code is governed by the Apache 2.0 License that can be found in the LICENSE.txt file.
 */

package fuzz

import com.code_intelligence.jazzer.api.FuzzedDataProvider
import com.code_intelligence.jazzer.junit.FuzzTest
import fuzz.utils.compareTest
import kotlinx.datetime.*
import java.time.ZoneId
import kotlin.time.Duration
import kotlin.time.toKotlinDuration

class ParseFromStringCompare {
    @FuzzTest(maxDuration = "2h")
    fun duration(data: FuzzedDataProvider) = with(data) {
        val s = consumeString(100).uppercase()
        compareTest(
            createKotlin = { Duration.parse(s) },
            createJava = { java.time.Duration.parse(s) },
            kotlinToJava = { it.copyj() },
            javaToKotlin = { it.toKotlinDuration() },
        )
    }

    @FuzzTest(maxDuration = "2h")
    fun datePeriod(data: FuzzedDataProvider) = with(data) {
        val s = consumeString(100)
        compareTest(
            createKotlin = { DatePeriod.parse(s) },
            createJava = { java.time.Period.parse(s) },
            kotlinToJava = { it.toJavaPeriod() },
            javaToKotlin = { it.toKotlinDatePeriod() },
        )
    }

    @FuzzTest(maxDuration = "2h")
    fun localDate(data: FuzzedDataProvider) {
        val s = data.consumeAsciiString(100)
        compareTest(
            createKotlin = { LocalDate.parse(s) },
            createJava = { java.time.LocalDate.parse(s) },
            kotlinToJava = { it.copyj() },
            javaToKotlin = { it.toKotlinLocalDate() },
        )
    }

    @FuzzTest(maxDuration = "2h")
    fun localDateIsoParse(data: FuzzedDataProvider) {
        val s = data.consumeAsciiString(100)
        compareTest(
            createKotlin = { LocalDate.Formats.ISO.parse(s) },
            createJava = { java.time.LocalDate.parse(s) },
            kotlinToJava = { it.copyj() },
            javaToKotlin = { it.toKotlinLocalDate() },
        )
    }

    @FuzzTest(maxDuration = "2h")
    fun localDateDict(data: FuzzedDataProvider) {
        val len = data.consumeInt(1, 20)
        val chars = (0..9).toList().map { it.toString() } + "-"
        val s = List(len) { data.pickValue(chars) }.joinToString(separator = "")

        compareTest(
            createKotlin = { LocalDate.parse(s) },
            createJava = { java.time.LocalDate.parse(s) },
            kotlinToJava = { it.copyj() },
            javaToKotlin = { it.toKotlinLocalDate() },
//                disableOkPrintln = false
        )
    }

    @FuzzTest(maxDuration = "2h")
    fun localDateTime(data: FuzzedDataProvider) {
        val s = data.consumeString(20)
        compareTest(
            createKotlin = { LocalDateTime.parse(s) },
            createJava = { java.time.LocalDateTime.parse(s) },
            kotlinToJava = { it.copyj() },
            javaToKotlin = { it.toKotlinLocalDateTime() }
        )
    }

    @FuzzTest(maxDuration = "10m")
    fun instant(data: FuzzedDataProvider) {
        val s = data.consumeString(100)
        compareTest(
            createKotlin = { Instant.parse(s) },
            createJava = { java.time.Instant.parse(s) },
            kotlinToJava = { it.copyj() },
            javaToKotlin = { it.toKotlinInstant() }
        )
    }

    @FuzzTest(maxDuration = "2h")
    fun timeZone(data: FuzzedDataProvider) = with(data) {
        val s = consumeString(100)
        compareTest(
            createKotlin = { TimeZone.of(s) },
            createJava = { ZoneId.of(s) },
            javaToKotlin = { it.toKotlinTimeZone() },
            kotlinToJava = { it.zoneId }
        )
    }

    @FuzzTest(maxDuration = "2h")
    fun utcOffset(data: FuzzedDataProvider) = with(data){
        val s = consumeString(100).uppercase()
        compareTest(
            createKotlin = { UtcOffset.parse(s) },
            createJava = { java.time.ZoneOffset.of(s) },
            javaToKotlin = { it.toKotlinUtcOffset() },
            kotlinToJava = { it.toJavaZoneOffset() }
        )
    }

    @FuzzTest(maxDuration = "2h")
    fun dayOfWeek(data: FuzzedDataProvider) = with(data) {
        val s = consumeString(100)
        compareTest(
            createKotlin = { DayOfWeek.valueOf(s) },
            createJava = { java.time.DayOfWeek.valueOf(s) },
            kotlinToJava = {java.time.DayOfWeek.of(it.value)},
            javaToKotlin = {DayOfWeek.of(it.value)}
        )
    }


    private val ZoneId.isFixedOffset: Boolean
        get() = try {
            // On older Android versions, this can throw even though it shouldn't
            rules.isFixedOffset
        } catch (e: ArrayIndexOutOfBoundsException) {
            false // Happens for America/Costa_Rica, Africa/Cairo, Egypt
        }
}
