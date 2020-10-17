import java.util.*

plugins {
    kotlin("jvm") version "1.4.10"
    id("com.jfrog.bintray") version "1.8.4"
    `maven-publish`
}

group = "com.apurebase"
version = "1.0.2"

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))

    api("org.apache.poi:poi-ooxml:4.1.2")
    implementation("org.apache.poi:ooxml-schemas:1.4")
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
    from("LICENCE") {
        into("META-INF")
    }
}



val githubUrl = "https://github.com/aPureBase/ExcelDSL"

publishing {
    publications {
        create<MavenPublication>("excel-dsl") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])
            artifact(sourcesJar)

            pom {
                packaging = "jar"
                name.set(rootProject.name)
                url.set(githubUrl)
                scm { url.set(githubUrl) }
                issueManagement { url.set("$githubUrl/issues") }
                licenses {
                    license {
                        name.set("MIT")
                        url.set("$githubUrl/blob/master/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("jeggy")
                        name.set("Jógvan Olsen")
                    }
                }
            }
        }
    }
}
kotlin {
    explicitApi()

    bintray {
        user = System.getenv("BINTRAY_USER")
        key = System.getenv("BINTRAY_KEY")

        publish = true
        setPublications("excel-dsl")
        pkg.apply {
            repo = "apurebase"
            name = project.name
            setLicenses("MIT")
            setLabels("kotlin", "excel", "apache", "poi", "dsl")
            vcsUrl = githubUrl
            websiteUrl = githubUrl
            issueTrackerUrl = "$githubUrl/issues"
            version.apply {
                name = "${project.version}"
                released = "${Date()}"
            }
        }
    }
}
