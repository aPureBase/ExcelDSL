# ExcelDSL
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
