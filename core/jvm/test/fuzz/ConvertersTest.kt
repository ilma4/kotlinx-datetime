/*
 * Copyright 2019-2024 JetBrains s.r.o. and contributors.
 * Use of this source code is governed by the Apache 2.0 License that can be found in the LICENSE.txt file.
 */

@file:Suppress("ClassName")

package fuzz

import com.code_intelligence.jazzer.api.FuzzedDataProvider
import com.code_intelligence.jazzer.junit.FuzzTest
import kotlinx.datetime.*
import java.time.Period
import kotlin.test.assertEquals

class ConvertersTest {
    class instant {
        @FuzzTest
        fun instant(data: FuzzedDataProvider) {
            fun test(seconds: Long, nanosecond: Int) {
                val ktInstant = Instant.fromEpochSeconds(seconds, nanosecond.toLong())
                val jtInstant = java.time.Instant.ofEpochSecond(seconds, nanosecond.toLong())

                assertEquals(ktInstant, jtInstant.toKotlinInstant())
                assertEquals(jtInstant, ktInstant.toJavaInstant())

                assertEquals(ktInstant, jtInstant.toString().toInstant())
                assertEquals(jtInstant, ktInstant.toString().let(java.time.Instant::parse))
            }

            val seconds = data.consumeLong(-1_000_000_000_000, 1_000_000_000_000)
            val nanos = data.consumeInt()
            test(seconds, nanos)
        }
    }

    class localDateTime {
        @FuzzTest
        fun localDateTime(data: FuzzedDataProvider) {
            fun test(ktDateTime: LocalDateTime) {
                val jtDateTime = with(ktDateTime) {
                    java.time.LocalDateTime.of(
                        year,
                        month,
                        dayOfMonth,
                        hour,
                        minute,
                        second,
                        nanosecond
                    )
                }

                assertEquals(ktDateTime, jtDateTime.toKotlinLocalDateTime())
                assertEquals(jtDateTime, ktDateTime.toJavaLocalDateTime())

                assertEquals(ktDateTime, jtDateTime.toString().toLocalDateTime())
                assertEquals(jtDateTime, ktDateTime.toString().let(java.time.LocalDateTime::parse))
            }

            test(data.consumeDateTime())
        }
    }

    class localTime {
        @FuzzTest
        fun localTime(data: FuzzedDataProvider) {
            fun test(ktTime: LocalTime) {
                val jtTime =
                    with(ktTime) { java.time.LocalTime.of(hour, minute, second, nanosecond) }

                assertEquals(ktTime, jtTime.toKotlinLocalTime())
                assertEquals(jtTime, ktTime.toJavaLocalTime())

                assertEquals(ktTime, jtTime.toString().toLocalTime())
                assertEquals(jtTime, ktTime.toString().let(java.time.LocalTime::parse))
            }

            test(data.consumeTime())
        }
    }

    class localDate {
        @FuzzTest
        fun localDate(data: FuzzedDataProvider) {
            fun test(ktDate: LocalDate) {
                val jtDate = with(ktDate) { java.time.LocalDate.of(year, month, dayOfMonth) }

                assertEquals(ktDate, jtDate.toKotlinLocalDate())
                assertEquals(jtDate, ktDate.toJavaLocalDate())

                assertEquals(ktDate, jtDate.toString().toLocalDate())
                assertEquals(jtDate, ktDate.toString().let(java.time.LocalDate::parse))
            }

            test(data.consumeDate())
        }
    }

    class datePeriod {
        @FuzzTest
        fun datePeriod(data: FuzzedDataProvider) {

            fun assertJtPeriodNormalizedEquals(a: Period, b: Period) {
                assertEquals(a.days, b.days)
                assertEquals(a.months + a.years * 12, b.months + b.years * 12)
            }

            fun test(years: Int, months: Int, days: Int) {
                val ktPeriod = DatePeriod(years, months, days)
                val jtPeriod = Period.of(years, months, days)

                assertEquals(ktPeriod, jtPeriod.toKotlinDatePeriod())
                assertJtPeriodNormalizedEquals(jtPeriod, ktPeriod.toJavaPeriod())

                assertEquals(ktPeriod, jtPeriod.toString().let(DatePeriod::parse))
                assertJtPeriodNormalizedEquals(jtPeriod, ktPeriod.toString().let(Period::parse))
            }

            test(
                data.consumeInt(-1000, 1000),
                data.consumeInt(-1000, 1000),
                data.consumeInt(-1000, 1000)
            )
        }
    }

    class localDateTimeSetTime {
        @FuzzTest
        fun test(data: FuzzedDataProvider) = with(data) {
            val ktDt = consumeDateTime()
            val jvDt = ktDt.copyj()

            val ktTime = consumeTime()
            val jvTime = ktTime.copyj()

            assertEquals(
                ktDt.date.atTime(ktTime),
                jvDt.toLocalDate().atTime(jvTime).toKotlinLocalDateTime()
            )

            assertEquals(
                ktDt.date.atTime(ktTime).copyj(),
                jvDt.toLocalDate().atTime(jvTime)
            )
        }
    }
}

