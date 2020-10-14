package com.apurebase.excel

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


public fun excel(
    path: String = "./${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))}.xlsx",
    block: ExcelDSL.() -> Unit
): File {
    val document = ExcelDSL().apply(block).build()

    return File(path).also {
        it.outputStream().use(document::write)
    }
}
