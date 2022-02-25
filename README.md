# ExcelDSL
[![Maven Central](https://img.shields.io/maven-central/v/com.apurebase/ExcelDSL.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.apurebase%22%20AND%20a:%22ExcelDSL%22)

An easy-to-use Kotlin DSL to build Excel documents

```kotlin
val file: File = excel {
  sheet {
    row {
      cell("Hello")
      cell("World!")
    }
    row(2)
    row {
      emptyCell(3)
      cell("Here!")
    }
  }
}
```


# Installation

Installation via Kotlin Gradle Script

```kotlin
repositories {
    mavenCentral()
}
dependencies {
  implementation("com.apurebase:ExcelDSL:$version")
}
```
