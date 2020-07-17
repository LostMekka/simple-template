package de.lostmekka.simpletemplate

import kotlin.test.Test
import kotlin.test.assertEquals

class StaticIndent {
    @Test
    fun individualLines() {
        assertEquals(
            render {
                +"header"
                render(" - ") {
                    +"bullet 1"
                    +"bullet 2"
                    +"bullet 3"
                }
            },
            """
            header
             - bullet 1
             - bullet 2
             - bullet 3
            """.trimIndent()
        )
    }

    @Test
    fun multiLineString() {
        assertEquals(
            render {
                +"header"
                render(" - ") {
                    +"""
                    bullet 1
                    bullet 2
                    bullet 3
                    """.trimIndent()
                }
            },
            """
            header
             - bullet 1
             - bullet 2
             - bullet 3
            """.trimIndent()
        )
    }
}
