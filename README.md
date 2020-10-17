# ExcelDSL
[![Bintray](https://api.bintray.com/packages/apurebase/apurebase/ExcelDSL/images/download.svg)](https://bintray.com/apurebase/apurebase/ExcelDSL)

A easy to use Kotlin DSL to build Excel documents

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
    jcenter()
}
dependencies {
  implementation("com.apurebase:ExcelDSL:$version")
}
```
