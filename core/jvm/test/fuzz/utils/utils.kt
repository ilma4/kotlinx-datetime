/*
 * Copyright 2019-2024 JetBrains s.r.o. and contributors.
 * Use of this source code is governed by the Apache 2.0 License that can be found in the LICENSE.txt file.
 */

package fuzz.utils

import kotlin.test.assertEquals

inline fun <K_TYPE, J_TYPE> compareTest(
    createKotlin: () -> K_TYPE,
    createJava: () -> J_TYPE,
    kotlinToJava: (K_TYPE) -> J_TYPE,
    javaToKotlin: (J_TYPE) -> K_TYPE,
    disableOkPrintln: Boolean = true
) {
    val kotlinRes = runCatching { createKotlin() }
    val javaRes = runCatching { createJava() }

    assertEquals(kotlinRes.isSuccess, javaRes.isSuccess)
    return if (kotlinRes.isFailure || javaRes.isFailure) {
        Unit
    } else {
        val kotlinVal = kotlinRes.getOrThrow()
        val javaVal = javaRes.getOrThrow()

        val javaFromKotlin = kotlinToJava(kotlinVal)
        val kotlinFromJava = javaToKotlin(javaVal)

        assertEquals(kotlinVal, kotlinFromJava)
        assertEquals(javaVal, javaFromKotlin)
        if (!disableOkPrintln) {
            println("all ok")
            System.out.flush()
        }
        Unit
    }
}


inline fun <T> tryOrNull(block: () -> T): T? = try {
    block()
} catch (t: Throwable) {
    null
}