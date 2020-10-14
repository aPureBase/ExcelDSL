package com.apurebase.excel

import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

@ExcelDSLMarker
public class ExcelRegionDSL(public val rowSpan: Int, public val colSpan: Int) {
    private val rows = mutableListOf<ExcelRowDSL>()

    public fun row(block: ExcelRowDSL.() -> Unit) {
        ExcelRowDSL(rows.rowIndex).apply(block).let(rows::add)
    }

    public fun emptyRow(count: Int = 1) {
        repeat(count) { rows.add(ExcelRowDSL(rows.rowIndex)) }
    }


    internal fun buildAndApply(workbook: XSSFWorkbook, sheet: XSSFSheet, startRowIndex: Int, startColIndex: Int): List<CellRangeAddress> {
        val actualRowSpan = rows.sumBy(ExcelRowDSL::span)
        require(actualRowSpan <= rowSpan) {
            // TODO: Provide some more information about where in the document this error happened, as this is all DSL we need better error reporting!
            "Number of rows within region '$actualRowSpan' when '$rowSpan' rows are required!"
        }
        val ranges = mutableListOf<CellRangeAddress>()

        var currentRowIndex = startRowIndex
        rows.map {
            sheet.getRow(currentRowIndex).run {
                val nestedRegions = it.buildAndApply(workbook, sheet, this, startColIndex)
                ranges.addAll(nestedRegions)
            }
            currentRowIndex += it.span
        }

        return ranges
    }

}
