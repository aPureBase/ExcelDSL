package com.apurebase.excel

import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.IndexedColors

public data class ExcelBorderRegion(
    var borderTop: BorderStyle? = null,
    var borderTopColor: IndexedColors? = null,
    var borderRight: BorderStyle? = null,
    var borderRightColor: IndexedColors? = null,
    var borderBottom: BorderStyle? = null,
    var borderBottomColor: IndexedColors? = null,
    var borderLeft: BorderStyle? = null,
    var borderLeftColor: IndexedColors? = null
)
