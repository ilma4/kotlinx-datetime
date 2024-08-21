/*
 * Copyright 2019-2024 JetBrains s.r.o. and contributors.
 * Use of this source code is governed by the Apache 2.0 License that can be found in the LICENSE.txt file.
 */

package fuzz

import kotlinx.datetime.*
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.time.*

//import org.junit.jupiter.api.Test

class Reproduce {
    @Test
    fun datePeriod() {
        val s = "PT1M"
        kotlin.time.Duration.parseIsoString(s)
        val a = DateTimePeriod.parse(s)
        val b = java.time.Duration.parse(s)

    }

    @Test
    fun lol() {
        val a = kotlin.time.Duration.parse("PT+-2H")
        println(a)
    }

    @Test
    fun lal() {
//        val s = "PT+-2H"
        val s = "P0D" //T2H"
        val kt = assertDoesNotThrow { kotlin.time.Duration.parseIsoString(s) }
//        val ktx = assertFails { kotlinx.datetime.DateTimePeriod.parse(s) }
//        val jv = assertFails { java.time.Duration.parse(s) }
        java.time.Period.parse(s)
    }

    @Test
    fun until() {
        val first = kotlinx.datetime.LocalDate(year = 0, monthNumber = 4, dayOfMonth = 1)
        val second = kotlinx.datetime.LocalDate(year = 0, monthNumber = 5, dayOfMonth = 31)

        val ktPeriod = first.periodUntil(second)
        val jvPeriod = first.toJavaLocalDate().until(second.toJavaLocalDate())

        println(ktPeriod)
        println(jvPeriod)
    }

    @Test
    fun until2() {
        val firstDate = kotlinx.datetime.LocalDate(year = 0, monthNumber = 5, dayOfMonth = 31)
        val secondDate = kotlinx.datetime.LocalDate(year = 0, monthNumber = 4, dayOfMonth = 1)

        val ktPeriod = firstDate.periodUntil(secondDate)
        val jvPeriod = firstDate.toJavaLocalDate().until(secondDate.toJavaLocalDate())

        println(ktPeriod)
        println(jvPeriod)
    }

    @Test
    fun until3() {
        val firstDate = kotlinx.datetime.LocalDate(year = 0, monthNumber = 5, dayOfMonth = 31)
        val secondDate = kotlinx.datetime.LocalDate(year = 0, monthNumber = 4, dayOfMonth = 1)

        val ktPeriod = firstDate.atStartOfDayIn(TimeZone.UTC)
            .periodUntil(secondDate.atStartOfDayIn(TimeZone.UTC), TimeZone.UTC)
        val jvPeriod = firstDate.toJavaLocalDate().until(secondDate.toJavaLocalDate())

        println(ktPeriod)
        println(jvPeriod)
    }

    @Test
    fun exampleFromWiki() {
        val secondDate = kotlinx.datetime.LocalDate(year = 2001, monthNumber = 3, dayOfMonth = 14)
        val firstDate = kotlinx.datetime.LocalDate(year = 2003, monthNumber = 12, dayOfMonth = 25)

        val ktPeriod = firstDate.atStartOfDayIn(TimeZone.UTC)
            .periodUntil(secondDate.atStartOfDayIn(TimeZone.UTC), TimeZone.UTC)
        val jvPeriod = firstDate.toJavaLocalDate().until(secondDate.toJavaLocalDate())

        println(ktPeriod)
        println(jvPeriod)
    }

    @Test
    fun pm() {
        val firstDate = kotlinx.datetime.LocalDate(year = 0, monthNumber = 5, dayOfMonth = 31)
        val period = kotlinx.datetime.DatePeriod(months = -1, days = -29)
        println(firstDate + period)
        println(firstDate + period - period)
    }

    @Test
    fun durationInfiniteOfDays() {
        val jv = java.time.Duration.ofDays(1_000_000_000_000L)
        val kt = jv.toKotlinDuration()
        assertEquals(Duration.INFINITE, kt)
        assertNotEquals(kt.toJavaDuration(), jv)

        println("jv: $jv")
        println("kt: $kt")
        println("kt.toJavaDuration(): ${kt.toJavaDuration()}")
    }

    @Test
    fun ofSecondsMinToInf(){
        var fromExcl = 0L
        var toIncl = Long.MAX_VALUE
        while(toIncl - fromExcl > 1L){
            val m = (toIncl + fromExcl) / 2L
            val toKotlinDuration = java.time.Duration.ofSeconds(m).toKotlinDuration()
            if (toKotlinDuration == Duration.INFINITE){
                toIncl = m
            } else {
                fromExcl = m
            }
        }
        println(fromExcl)
    }

    @Test
    fun aaa(){
        val a = java.time.Duration.ofSeconds(Long.MAX_VALUE / 2)
        val b = (Long.MAX_VALUE / 2).toDuration(DurationUnit.SECONDS)

        println(a)
        println(b.toString())
        println(b.toJavaDuration())
    }

    @Test
    fun bbb(){
        val maxSecond = 31556889864403199L
        java.time.Instant.ofEpochSecond(maxSecond)
        Instant.fromEpochSeconds(maxSecond)
    }

    @Test
    fun jvOfSecondsMinToInf(){
        var fromExcl = 0L
        var toIncl = Long.MAX_VALUE
        while(toIncl - fromExcl > 1L){
            val m = (toIncl + fromExcl) / 2L
            val jvDuration = java.time.Duration.ofSeconds(m)
            if (jvDuration.seconds == Long.MIN_VALUE){
                toIncl = m
            } else {
                fromExcl = m
            }
        }
        println(fromExcl)
    }

    @Test
    fun durationInfiniteOfSeconds() {
        val jv = java.time.Duration.ofDays(1_000_000_000_000L)
        val kt = jv.toKotlinDuration()
        assertEquals(Duration.INFINITE, kt)
        assertNotEquals(kt.toJavaDuration(), jv)

        println("jv: $jv")
        println("kt: $kt")
        println("kt.toJavaDuration(): ${kt.toJavaDuration()}")
    }
}
