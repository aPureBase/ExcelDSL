package com.apurebase.excel

public val MutableList<ExcelRowDSL>.rowIndex: Int get() = sumOf(ExcelRowDSL::span) + 1
public val MutableList<ExcelCell>.cellIndex: Int get() = sumOf { cell ->
    when (cell) {
        is ExcelRegionDSL -> cell.rows.maxOf { it.cells.cellIndex }
        is ExcelCellDSL -> cell.span
    }
}
