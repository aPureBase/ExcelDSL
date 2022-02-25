package com.apurebase.excel

import org.apache.poi.ss.usermodel.IndexedColors

public data class ExcelFont(
    var fontName: String? = "Arial",
    var heightInPoints: Short = 10,
    var bold: Boolean = false,
    var italic: Boolean = false,
    var strikeout: Boolean = false,
    var color: IndexedColors? = null,
    /**
     * @see <a href="https://support.microsoft.com/en-us/office/review-guidelines-for-customizing-a-number-format-c0a1d1fa-d3f4-4018-96b7-9c9354dd99f5">Review guidelines for customizing a number format</a>
     */
    var numberFormat: String? = null
)
