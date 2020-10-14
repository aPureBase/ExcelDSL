package com.apurebase.excel

public data class ExcelFont(
    var fontName: String? = "Arial",
    var heightInPoints: Short = 10,
    var bold: Boolean = false,
    var italic: Boolean = false,
    var strikeout: Boolean = false
)
