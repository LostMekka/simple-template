# SimpleTemplate

[ ![Download](https://api.bintray.com/packages/lostmekka/simple-template/simple-template/images/download.svg) ](https://bintray.com/lostmekka/simple-template/simple-template/_latestVersion)

SimpleTemplate is a simple, experimental multi platform Kotlin DSL for string templating. You probably don't need it. It is mainly my excuse to figure out how kotlin multi platform libraries are published...

### Quick start
Gradle:
```kotlin
repositories {
    maven("https://dl.bintray.com/lostmekka/simple-template")
}
dependencies {
    implementation("de.lostmekka:simple-template-jvm:0.1.0")
}
```

Maven:
```xml
<repositories>
    <repository>
      <id>lostmekka-simple-template</id>
      <url>https://dl.bintray.com/lostmekka/simple-template</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>de.lostmekka</groupId>
        <artifactId>simple-template-jvm</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

Usage:
```kotlin
data class Cat(val name: String, val color: String, val memeCount: Int)

fun TemplateBuilder.renderCat(cat: Cat) {
    +cat.name
    +"color: ${cat.color}"
    +"confirmed memes: ${cat.memeCount}"
}

fun TemplateBuilder.renderLeaderboard(topCats: List<Cat>) {
    +"Look at how popular these adorable, hairy meat bags are:"
    renderEach(topCats) { cat ->
        dynamicIndent { line, _ -> if (line == 0) "${index + 1}. " else "   " }
        renderCat(cat)
        if (!isLast) +"---------------------------"
    }
}

fun main() {
    val mostPopularCats = listOf(
        Cat("grumpy", "fur-colored", 293856205),
        Cat("Hamilton", "fur-colored", 115373986),
        Cat("Nala", "fur-colored", 9486208)
    )
    val newcomerHighlight = Cat("Boring Cat", "fur-colored", 0)

    val newsletterText = render {
        +"The worlds most useful newsletter is back!"
        +""
        +"-- LEADERBOARD --"
        render("#  ") {
            renderLeaderboard(mostPopularCats)
        }
        +""
        +"And here is the newcomer cat of the week:"
        render("> ") {
            renderCat(newcomerHighlight)
        }
    }
    println(newsletterText)
}
```
```
The worlds most useful newsletter is back!

-- LEADERBOARD --
#  Look at how popular these adorable, hairy meat bags are:
#  1. grumpy
#     color: fur-colored
#     confirmed memes: 293856205
#     ---------------------------
#  2. Hamilton
#     color: fur-colored
#     confirmed memes: 115373986
#     ---------------------------
#  3. Nala
#     color: fur-colored
#     confirmed memes: 9486208

And here is the newcomer cat of the week:
> Boring Cat
> color: fur-colored
> confirmed memes: 0
```

### Comparison to vanilla string interpolation
The Kotlin string interpolation is quite good, but it gets a bit weird when you try to render indented blocks:
```kotlin
fun vanilla(cat: Cat) =
    """
    ${cat.name}
    color: ${cat.color}
    confirmed memes: ${cat.memeCount}
    """.trimIndent()

fun vanillaIndented(cat: Cat) =
    """
    |bla bla
    ${vanilla(cat).prependIndent("|    ")}
    |bla bla
    """.trimMargin()

fun TemplateBuilder.withBuilder(cat: Cat) {
    +cat.name
    +"color: ${cat.color}"
    +"confirmed memes: ${cat.memeCount}"
}

fun TemplateBuilder.withBuilderIndented(cat: Cat) {
    +"bla bla"
    render("    ") { withBuilder(cat) }
    +"bla bla"
}

fun main() {
    println(vanillaIndented(newcomerHighlight))
    println(render { withBuilderIndented(newcomerHighlight) })
}
```
Both methods render the same output, but notice the margin character `|` inside `prependIndent`. With the vanilla string templating, you need to make sure every line has the margin character at the right position, otherwise the output will look horrible.

Of course, the template builder version is a bit more verbose, and the use of the unary plus operator is a bit ugly. (at least in my opinion)
