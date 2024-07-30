/*
 * Copyright 2019-2024 JetBrains s.r.o. and contributors.
 * Use of this source code is governed by the Apache 2.0 License that can be found in the LICENSE.txt file.
 */

package fuzz

import com.code_intelligence.jazzer.api.FuzzedDataProvider
import com.code_intelligence.jazzer.junit.FuzzTest
import kotlinx.datetime.*
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

private const val billiard = 1_000_000_000L

class InstantTest {

    class instantArithmetic {
        @FuzzTest(maxDuration = "2h")
        fun instantArithmetic(data: FuzzedDataProvider) = with(data) {
            val instant = Instant.fromEpochMilliseconds(consumeInstant().toEpochMilliseconds())
            val diffMillis = consumeLong(-1_000_000_000, 1_000_000_000)
            val diff = diffMillis.milliseconds

            val nextInstant =
                (instant.toEpochMilliseconds() + diffMillis).let { Instant.fromEpochMilliseconds(it) }

            assertEquals(diff, nextInstant - instant)
            assertEquals(nextInstant, instant + diff)
            assertEquals(instant, nextInstant - diff)

//            println("this: $instant, next: $nextInstant, diff: ${diff.toIsoString()}")
        }
    }

    class instantArithmeticCombined {
        @FuzzTest(maxDuration = "2h")
        fun instantArithmetic(data: FuzzedDataProvider) = with(data) {
            val instant = Instant.fromEpochMilliseconds(consumeInstant().toEpochMilliseconds())
            val diffSeconds = consumeLong(-billiard, billiard)
            val diffNanos = consumeLong(-billiard, billiard)

            val diff = diffSeconds.seconds + diffNanos.nanoseconds


            val nextInstant = Instant.fromEpochSeconds(
                instant.epochSeconds + diffSeconds,
                instant.nanosecondsOfSecond + diffNanos
            )
//                (instant.toEpochMilliseconds() ).let { Instant.fromEpochMilliseconds(it) }

            assertEquals(diff, nextInstant - instant)
            assertEquals(nextInstant, instant + diff)
            assertEquals(instant, nextInstant - diff)

//            println("this: $instant, next: $nextInstant, diff: ${diff.toIsoString()}")
        }
    }

    class instantArithmeticNano {
        @FuzzTest(maxDuration = "2h")
        fun instantArithmetic(data: FuzzedDataProvider) = with(data) {
            val instant = consumeInstant()
            val diffMillis = consumeLong(1000, 1_000_000_000)
            val diff = diffMillis.milliseconds

            val nextInstant =
                (instant.toEpochMilliseconds() + diffMillis).let { Instant.fromEpochMilliseconds(it) }

            assertEquals(diff, nextInstant - instant)
            assertEquals(nextInstant, instant + diff)
            assertEquals(instant, nextInstant - diff)

//            println("this: $instant, next: $nextInstant, diff: ${diff.toIsoString()}")
        }
    }

    class diffInvariants {
        @FuzzTest(maxDuration = "2h")
        fun diffInvariant(data: FuzzedDataProvider) = with(data) {
            val millis1 = consumeLong(-2_000_000_000_000L, 2_000_000_000_000L)
            val millis2 = consumeLong(-2_000_000_000_000L, 2_000_000_000_000L)
            val instant1 = Instant.fromEpochMilliseconds(millis1)
            val instant2 = Instant.fromEpochMilliseconds(millis2)

            val diff = instant1.periodUntil(instant2, TimeZone.currentSystemDefault())
            val instant3 = instant1.plus(diff, TimeZone.currentSystemDefault())

            assertEquals(instant2, instant3)
        }
    }

    class diffInvariantsSameAsDate {
        @FuzzTest(maxDuration = "2h")
        fun diffInvariantSameAsDate(data: FuzzedDataProvider) = with(data) {
            val millis1 = consumeLong(-2_000_000_000_000L, 2_000_000_000_000L)
            val millis2 = consumeLong(-2_000_000_000_000L, 2_000_000_000_000L)
//            with(consumeTimeZone()) TZ@{
            with(TimeZone.UTC) TZ@{
                val date1 = Instant.fromEpochMilliseconds(millis1).toLocalDateTime().date
                val date2 = Instant.fromEpochMilliseconds(millis2).toLocalDateTime().date
                val instant1 = date1.atStartOfDayIn(this@TZ)
                val instant2 = date2.atStartOfDayIn(this@TZ)

                val diff1 = instant1.periodUntil(instant2, this@TZ)
                val diff2 = date1.periodUntil(date2)

                assertEquals(diff1, diff2)
            }
        }
    }
}
