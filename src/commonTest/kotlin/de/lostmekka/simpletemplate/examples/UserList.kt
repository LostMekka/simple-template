package de.lostmekka.simpletemplate.examples

import de.lostmekka.simpletemplate.render
import kotlin.test.Test
import kotlin.test.assertEquals

class UserList {
    private data class User(val name: String, val age: Int, val accountBalance: Int)

    @Test
    fun test() {
        val users = listOf(
            User("Buzz", 50, 42),
            User("Lynn", 21, 666),
            User("Fritz", 48, 100),
            User("Justin", 26, -80)
        )
        assertEquals(
            render {
                +"balance of all users:"
                renderEach(users, "  ") {
                    +"""
                    - ${it.name} (age ${it.age})
                      balance: ${it.accountBalance}
                    """.trimIndent()
                }
            },
            """
            balance of all users:
              - Buzz (age 50)
                balance: 42
              - Lynn (age 21)
                balance: 666
              - Fritz (age 48)
                balance: 100
              - Justin (age 26)
                balance: -80
            """.trimIndent()
        )
    }
}
