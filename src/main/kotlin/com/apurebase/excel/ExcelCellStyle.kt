package com.apurebase.excel

import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.VerticalAlignment

internal data class ExcelCellStyle(
    val fillColor: IndexedColors? = null,
    val horizontalAlignment: HorizontalAlignment? = null,
    val verticalAlignment: VerticalAlignment? = null,
    val borderSettings: ExcelBorderRegion? = null,
    val font: ExcelFont = ExcelFont(),
    val wrapText: Boolean = false
)
