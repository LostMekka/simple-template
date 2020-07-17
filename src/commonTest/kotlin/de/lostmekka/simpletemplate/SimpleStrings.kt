package de.lostmekka.simpletemplate

import kotlin.test.Test
import kotlin.test.assertEquals

class SimpleStrings {
    @Test
    fun simpleString() {
        assertEquals(
            render { +"myString" },
            "myString"
        )
    }

    @Test
    fun simpleMultiLineString() {
        assertEquals(
            render {
                +"""
                line 1
                line 2
                line 3
                """.trimIndent()
            },
            """
            line 1
            line 2
            line 3
            """.trimIndent()
        )
    }
}
