/*
 * Copyright 2019-2024 JetBrains s.r.o. and contributors.
 * Use of this source code is governed by the Apache 2.0 License that can be found in the LICENSE.txt file.
 */

package fuzz

import com.code_intelligence.jazzer.api.FuzzedDataProvider
import com.code_intelligence.jazzer.junit.FuzzTest

//import com.code_intelligence.jazzer.api.


class FormattersTest {
//    @FuzzTest(maxDuration = "2h")
//    fun instant(data: FuzzedDataProvider) = with(data) {
//        val format = consumeFormat()
//        val s = consumeAsciiString(200)
//    }

    private inline fun <reified T : Throwable> ignore(block: () -> Any?) {
        try {
            block()
        } catch (e: Throwable) {
            if (!T::class.isInstance(e)) throw e
        }
    }

    @FuzzTest(maxDuration = "2h")
    fun localDateTime(data: FuzzedDataProvider): Unit = with(data) {
        val format = consumeFormat()
        val s = consumeAsciiString(200)
        format.parseOrNull(s)
    }
}