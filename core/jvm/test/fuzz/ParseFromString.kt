/*
 * Copyright 2019-2024 JetBrains s.r.o. and contributors.
 * Use of this source code is governed by the Apache 2.0 License that can be found in the LICENSE.txt file.
 */

package fuzz

import com.code_intelligence.jazzer.api.FuzzedDataProvider
import com.code_intelligence.jazzer.junit.FuzzTest
import fuzz.utils.compareTest
import fuzz.utils.tryOrNull
import kotlinx.datetime.*
import java.time.ZoneId
import kotlin.time.Duration
import kotlin.time.toKotlinDuration

class ParseFromString {
    class dateTimePeriod {
        @FuzzTest(maxDuration = "2h")
        fun duration(data: FuzzedDataProvider) = with(data) {
            val s = consumeString(20).uppercase()
            compareTest(
                createKotlin = { Duration.parse(s) },
                createJava = { java.time.Duration.parse(s) },
                kotlinToJava = { it.copyj() },
                javaToKotlin = { it.toKotlinDuration() },
            )
        }
    }

    class duration {
        @FuzzTest(maxDuration = "2h")
        fun duration(data: FuzzedDataProvider) = with(data) {
            val s = consumeString(20).uppercase()
            compareTest(
                createKotlin = { Duration.parseIsoString(s) },
                createJava = { java.time.Duration.parse(s) },
                kotlinToJava = { it.copyj() },
                javaToKotlin = { it.toKotlinDuration() },
            )
        }
    }

    class durationJavaToKotlin {
        @FuzzTest(maxDuration = "2h")
        fun duration(data: FuzzedDataProvider) = with(data) {
            val s = consumeString(20)
            val jv = tryOrNull { java.time.Duration.parse(s) } ?: return
            compareTest(
                createKotlin = { jv.toKotlinDuration() },
                createJava = { jv },
                kotlinToJava = { it.copyj() },
                javaToKotlin = { it.toKotlinDuration() },
            )
        }
    }

    class localDate {
        @FuzzTest(maxDuration = "2h")
        fun localDate(data: FuzzedDataProvider) {
            val s = data.consumeAsciiString(20)
            compareTest(
                createKotlin = { LocalDate.parse(s) },
                createJava = { java.time.LocalDate.parse(s) },
                kotlinToJava = { it.copyj() },
                javaToKotlin = { it.toKotlinLocalDate() },
            )
        }
    }

    class localDateDict {
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
    }

    class localDateTime {
        @FuzzTest(maxDuration = "2h")
        fun localDate(data: FuzzedDataProvider) {
            val s = data.consumeString(20)
            compareTest(
                createKotlin = { LocalDateTime.parse(s) },
                createJava = { java.time.LocalDateTime.parse(s) },
                kotlinToJava = { it.copyj() },
                javaToKotlin = { it.toKotlinLocalDateTime() }
            )
        }
    }

    class instant {
        @FuzzTest(maxDuration = "2h")
        fun instant(data: FuzzedDataProvider) {
            val s = data.consumeString(200)
            compareTest(
                createKotlin = { Instant.parse(s) },
                createJava = { java.time.Instant.parse(s) },
                kotlinToJava = { it.copyj() },
                javaToKotlin = { it.toKotlinInstant() }
            )
        }
    }

    class timeZone {
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
    }
}
