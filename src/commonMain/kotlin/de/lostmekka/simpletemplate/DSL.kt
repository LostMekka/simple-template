package de.lostmekka.simpletemplate

@DslMarker
annotation class TemplateDsl

@TemplateDsl
sealed class TemplateBuilder {
    private val lines = mutableListOf<String>()
    private var staticIndent: String? = null
    private var indentProvider: ((Int, String) -> String)? = null
    private var transformer: ((Int, String) -> String)? = null

    @TemplateDsl
    fun staticIndent(indent: String) {
        staticIndent = indent
    }

    @TemplateDsl
    fun dynamicIndent(indentProvider: (index: Int, line: String) -> String) {
        this.indentProvider = { i, it -> indentProvider(i, it) + it }
    }

    @TemplateDsl
    fun transformLines(transformer: (index: Int, line: String) -> String) {
        this.transformer = transformer
    }

    @TemplateDsl
    operator fun String.unaryPlus() {
        lines += this.lines()
    }

    @TemplateDsl
    fun render(config: SimpleTemplateBuilder.() -> Unit) {
        addLines(config)
    }

    @TemplateDsl
    fun render(staticIndent: String, config: SimpleTemplateBuilder.() -> Unit) {
        addLines {
            staticIndent(staticIndent)
            render(config)
        }
    }

    @TemplateDsl
    fun <T> renderEach(subject: Collection<T>, config: IteratingTemplateBuilder.(T) -> Unit) {
        subject.forEachIndexed { i, it ->
            lines += IteratingTemplateBuilder(i, subject.size).apply { config(it) }.buildLines()
        }
    }

    @TemplateDsl
    fun <T> renderEach(subject: Collection<T>, staticIndent: String, config: IteratingTemplateBuilder.(T) -> Unit) {
        addLines {
            staticIndent(staticIndent)
            renderEach(subject, config)
        }
    }

    private inline fun addLines(crossinline config: SimpleTemplateBuilder.() -> Unit) {
        lines += SimpleTemplateBuilder().apply(config).buildLines()
    }

    internal fun buildLines() = lines.mapIndexed { i, original ->
        original
            .let { transformer?.invoke(i, it) ?: it }
            .let { indentProvider?.invoke(i, it) ?: it }
            .let { staticIndent?.plus(it) ?: it }
    }

    internal fun build() = buildLines().joinToString("\n")
}

@TemplateDsl
class SimpleTemplateBuilder : TemplateBuilder()

@TemplateDsl
class IteratingTemplateBuilder(
    @property:TemplateDsl
    val index: Int,
    @property:TemplateDsl
    val total: Int
) : TemplateBuilder() {
    @TemplateDsl
    val isFirst = index == 0

    @TemplateDsl
    val isLast = index == total - 1
}

@TemplateDsl
fun render(config: SimpleTemplateBuilder.() -> Unit) =
    SimpleTemplateBuilder().apply(config).build()
