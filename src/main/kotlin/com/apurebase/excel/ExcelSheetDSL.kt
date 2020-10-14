package com.apurebase.excel

import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

@ExcelDSLMarker
public class ExcelSheetDSL {

    public var sheetName: String? = null

    private val rows = mutableListOf<ExcelRowDSL>()
    private val columnConfigs = mutableListOf<ExcelColumnConfig>()
    private var pivot: ExcelPivotDSL? = null

    public fun row(rowCount: Int = 1, cellCount: Int = 0, block: ExcelRowDSL.() -> Unit = {}) {
        repeat(rowCount) {
            ExcelRowDSL(rows.rowIndex).apply {
                emptyCell(cellCount)
                block()
            }.let(rows::add)
        }
    }

    public fun pivot(areaReference: String, block: ExcelPivotDSL.() -> Unit) {
        pivot = ExcelPivotDSL(areaReference).apply(block)
    }

    public fun columnWidth(columnIndexes: List<Int>, widthSize: Int = 2048) {
        columnIndexes.map { columnIndex ->
            columnWidth(columnIndex, widthSize)
        }
    }

    public fun columnWidth(columnIndex: Int, widthSize: Int = 2048) {
        columnConfigs.add(ExcelColumnConfig(index = columnIndex, width = widthSize))
    }

    internal fun buildAndApply(workbook: XSSFWorkbook): XSSFSheet {
        val sheet = if (sheetName == null) workbook.createSheet() else workbook.createSheet(sheetName)

        pivot?.buildAndApply(sheet)

        var currentIndex = 0
        val ranges = rows.flatMap { row ->
            sheet.createRow(currentIndex).let {
                if (row.span > 1) repeat(row.span - 1) { sheet.createRow(currentIndex + it + 1) }
                currentIndex += row.span
                row.buildAndApply(workbook, sheet, it)
            }
        }

        // Set custom column widths
        columnConfigs.map { config -> sheet.setColumnWidth(config.index, config.width) }

        // Add all ranges
        ranges.map { sheet.addMergedRegion(it) }

        pivot?.buildAndApply(sheet)

        return sheet
    }
}
