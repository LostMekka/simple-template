plugins {
    kotlin("multiplatform") version "1.3.72"
    id("org.jetbrains.dokka") version "0.10.1"
    id("com.jfrog.bintray") version "1.8.5"
    `maven-publish`
    `java-library`
}

group = "de.lostmekka"
version = "0.1.0"

repositories {
    mavenCentral()
    jcenter()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    js {
        browser {}
        nodejs {}
    }
    // For ARM, should be changed to iosArm32 or iosArm64
    // For Linux, should be changed to e.g. linuxX64
    // For MacOS, should be changed to e.g. macosX64
    // For Windows, should be changed to e.g. mingwX64
    mingwX64("mingw")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val mingwMain by getting {
        }
        val mingwTest by getting {
        }
    }
}

val publishingGroup = "de.lostmekka"
val publishingArtifact = "simple-template"
val publishingVersion = version.toString()
val publishingGithubUrl = "https://github.com/LostMekka/simple-template"
val publishingGithubIssuesUrl = "https://github.com/LostMekka/simple-template/issues"

val publishingBintrayRepository = "simple-template"
val publishingBintrayOrganization = "lostmekka"
val bintrayUsername: String? by project
val bintrayApiKey: String? by project

bintray {
    user = System.getenv("BINTRAY_USERNAME") ?: bintrayUsername
    key = System.getenv("BINTRAY_API_KEY") ?: bintrayApiKey
    publish = false

    val pubNames = project.publishing.publications
        .withType<MavenPublication>()
        .map { it.name }
        .filter { it != "kotlinMultiplatform" }
        .toTypedArray()
    setPublications(*pubNames)

    pkg = PackageConfig().apply {
        repo = publishingBintrayRepository
        name = publishingArtifact
        userOrg = publishingBintrayOrganization
        setLicenses("Apache-2.0")
        vcsUrl = publishingGithubUrl
        issueTrackerUrl = publishingGithubIssuesUrl
        version = VersionConfig().apply {
            name = publishingVersion
        }
    }
}

tasks.bintrayUpload {
    dependsOn(
        tasks.build,
        tasks.withType<GenerateMavenPom>()
    )
}
val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    archiveClassifier.set("javadoc")
    from(tasks.dokka)
}

publishing {
    publications.withType<MavenPublication> {
        groupId = publishingGroup
        artifactId = when {
            name.contains("metadata") -> publishingArtifact
            else -> "$publishingArtifact-$name"
        }

        artifact(dokkaJar)

        pom {
            name.set("simple-template")
            description.set("A simple Kotlin string templating DSL")
            url.set("https://github.com/LostMekka/simple-template")

            scm {
                connection.set("scm:git:https://github.com/LostMekka/simple-template/")
                developerConnection.set("scm:git:https://github.com/LostMekka/")
                url.set("https://github.com/LostMekka/simple-template/")
            }

            licenses {
                license {
                    name.set("Apache-2.0")
                    url.set("https://opensource.org/licenses/Apache-2.0")
                }
            }
        }
    }
}
