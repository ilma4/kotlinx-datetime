/*
 * Copyright 2019-2024 JetBrains s.r.o. and contributors.
 * Use of this source code is governed by the Apache 2.0 License that can be found in the LICENSE.txt file.
 */

package fuzz

import com.code_intelligence.jazzer.api.FuzzedDataProvider
import com.code_intelligence.jazzer.junit.FuzzTest
import fuzz.utils.compareTest
import kotlinx.datetime.*
import java.sql.Time
import java.time.ZoneId

class ParseFromString {

    class localDate {

        @FuzzTest
        fun localDate(data: FuzzedDataProvider) {
            val s = data.consumeAsciiString(10)
            compareTest(
                createKotlin = { LocalDate.parse(s) },
                createJava = { java.time.LocalDate.parse(s) },
                kotlinToJava = { it.toJavaLocalDate() },
                javaToKotlin = { it.toKotlinLocalDate() })
        }

        @FuzzTest
        fun localDateDict(data: FuzzedDataProvider) {
            val len = data.consumeInt(4, 20)
            val chars = (0..9).toList().map { it.toString() } + "-"
            val s = List(len) { data.pickValue(chars) }.joinToString()

            compareTest(
                createKotlin = { LocalDate.parse(s) },
                createJava = { java.time.LocalDate.parse(s) },
                kotlinToJava = { it.toJavaLocalDate() },
                javaToKotlin = { it.toKotlinLocalDate() })
        }

    }

    class localDateTime {
        @FuzzTest(maxDuration = "2h")
        fun localDate(data: FuzzedDataProvider) {
            val s = data.consumeString(20)
            compareTest(
                createKotlin = { LocalDateTime.parse(s) },
                createJava = { java.time.LocalDateTime.parse(s) },
                kotlinToJava = { it.toJavaLocalDateTime() },
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
                kotlinToJava = { it.toJavaInstant() },
                javaToKotlin = { it.toKotlinInstant() }
            )
        }
    }

    class timeZone {
        @FuzzTest
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
