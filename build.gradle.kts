import de.marcphilipp.gradle.nexus.NexusPublishPlugin
import java.time.Duration

val libVersion: String by project
val poi_ooxmlVersion: String by project
val ooxml_schemasVersion: String by project
val junit_version: String by project
val sonatypeUsername: String? = project.findProperty("sonatypeUsername") as String? ?: System.getenv("sonatypeUsername")
val sonatypePassword: String? = project.findProperty("sonatypePassword") as String? ?: System.getenv("sonatypePassword")
val isReleaseVersion = !version.toString().endsWith("SNAPSHOT")

plugins {
    base
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.dokka") version "1.5.0"
    id("io.codearte.nexus-staging") version "0.21.2"
    id("de.marcphilipp.nexus-publish") version "0.4.0"
    id("com.github.ben-manes.versions") version "0.38.0"
    signing
    jacoco
}

group = "com.apurebase"
version = libVersion

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    api("org.apache.poi:poi-ooxml:${poi_ooxmlVersion}")
    implementation("org.apache.poi:ooxml-schemas:$ooxml_schemasVersion")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit_version")
}

nexusStaging {
    packageGroup = "com.apurebase"
    username = sonatypeUsername
    password = sonatypePassword
    numberOfRetries = 360 // 1 hour if 10 seconds delay
    delayBetweenRetriesInMillis = 10000 // 10 seconds
}

nexusPublishing {
    repositories {
        sonatype()
    }
    clientTimeout.set(Duration.parse("PT10M")) // 10 minutes
}

val sourcesJar by tasks.creating(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}
val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    classifier = "javadoc"
    from(tasks.dokkaHtml)
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = project.name
            from(components["java"])
            artifact(sourcesJar)
            artifact(dokkaJar)
            pom {
                name.set("ExcelDSL")
                description.set("A easy to use Kotlin DSL to build Excel documents")
                organization {
                    name.set("aPureBase")
                    url.set("https://apurebase.com/")
                }
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("jeggy")
                        name.set("Jógvan Olsen")
                        email.set("jol@apurebase.com")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/aPureBase/ExcelDSL.git")
                    developerConnection.set("scm:git:https://github.com/aPureBase/ExcelDSL.git")
                    url.set("https://github.com/aPureBase/ExcelDSL/")
                    tag.set("HEAD")
                }
            }
        }
    }
}

kotlin {
    apply<NexusPublishPlugin>()
    explicitApi()

    tasks {
        test {
            useJUnitPlatform()
            doFirst {
                jvmArgs = listOf(
                    "-javaagent:${classpath.find { it.name.contains("jmockit") }!!.absolutePath}"
                )
            }
        }
        dokkaHtml {
            outputDirectory.set(buildDir.resolve("javadoc"))
            dokkaSourceSets {
                configureEach {
                    jdkVersion.set(8)
                    reportUndocumented.set(true)
                    platform.set(org.jetbrains.dokka.Platform.jvm)
                }
            }
        }
        wrapper {
            distributionType = Wrapper.DistributionType.ALL
        }
        closeRepository {
            mustRunAfter(subprojects.map { it.tasks.getByName("publishToSonatype") }.toTypedArray())
        }
        closeAndReleaseRepository {
            mustRunAfter(subprojects.map { it.tasks.getByName("publishToSonatype") }.toTypedArray())
        }
    }
}
signing {
    isRequired = isReleaseVersion
    useInMemoryPgpKeys(
        System.getenv("ORG_GRADLE_PROJECT_signingKey"),
        System.getenv("ORG_GRADLE_PROJECT_signingPassword")
    )
    sign(publishing.publications["maven"])
}
