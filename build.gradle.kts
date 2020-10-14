plugins {
    kotlin("jvm") version "1.4.10"
}

group = "com.apurebase"
version = "1.0.0"

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))

    api("org.apache.poi:poi-ooxml:4.1.2")
    implementation("org.apache.poi:ooxml-schemas:1.4")
}

kotlin {
    explicitApi()
}
