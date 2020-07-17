package de.lostmekka.simpletemplate.examples

import de.lostmekka.simpletemplate.IteratingTemplateBuilder
import de.lostmekka.simpletemplate.TemplateBuilder
import de.lostmekka.simpletemplate.render
import kotlin.test.Test
import kotlin.test.assertEquals

class DirectoryStructure {
    private data class Dir(val name: String, val children: List<Dir> = emptyList())

    private operator fun String.invoke(vararg children: Dir) =
        Dir(this, children.toList())

    private fun TemplateBuilder.renderDir(subDir: Dir) {
        +subDir.name
        renderEach(subDir.children) { renderSubDir(it) }
    }

    private fun IteratingTemplateBuilder.renderSubDir(subDir: Dir) {
        when {
            isLast -> dynamicIndent { i, _ -> if (i == 0) "└── " else "    " }
            else -> dynamicIndent { i, _ -> if (i == 0) "├── " else "|   " }
        }
        renderDir(subDir)
    }

    @Test
    fun test() {
        val rootDir = "root"(
            "bin"(
                "stuff"(
                    "a"(),
                    "b"()
                ),
                "foo"(),
                "bar"()
            ),
            "srv"(),
            "home"(
                "userA"(),
                "userB"()
            )
        )
        assertEquals(
            render { renderDir(rootDir) },
            """
            root
            ├── bin
            |   ├── stuff
            |   |   ├── a
            |   |   └── b
            |   ├── foo
            |   └── bar
            ├── srv
            └── home
                ├── userA
                └── userB
            """.trimIndent()
        )
    }
}
