package com.apurebase.excel

import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.ss.util.RegionUtil
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

@ExcelDSLMarker
public class ExcelRowDSL(public val currentRow: Int) {

    internal val cells = mutableListOf<ExcelCell>()

    public var span: Int = 1
    public var heightInPoints: Float = 15f

    // Default settings that will be used in columns if not overridden.
    public var fillColor: IndexedColors? = null
    public var borderStyle: ExcelCellBorder? = null
    public var font: ExcelFont? = null
    public var wrapText: Boolean? = null

    public inner class ExcelCellBorder(
        public val style: BorderStyle = BorderStyle.THIN,
        public val side: BorderSide = BorderSide.ALL,
        public val color: IndexedColors = IndexedColors.BLACK
    )


    public fun richCell(block: ExcelRichTextDSL.() -> Unit = {}) {
        ExcelRichTextDSL(this, cells.cellIndex)
            .apply(block)
            .let(cells::add)
    }

    public fun cell(value: String = "", block: ExcelCellDSL.() -> Unit = {}) {
        ExcelCellDSL(this, cells.cellIndex).apply {
            this.value = value
            block(this)
        }.let(cells::add)
    }

    public fun cell(value: Number, block: ExcelCellDSL.() -> Unit = {}) {
        ExcelCellDSL(this, cells.cellIndex).apply {
            this.value = value
            block(this)
        }.let(cells::add)
    }

    public fun cellFormula(formula: String, block: ExcelCellDSL.() -> Unit = {}) {
        ExcelCellDSL(this, cells.cellIndex).apply {
            this.value = ExcelCellFormula(formula)
            block(this)
        }.let(cells::add)
    }

    public fun cellRegion(colspan: Int, block: ExcelRegionDSL.() -> Unit) {
        ExcelRegionDSL(rowSpan = span, colSpan = colspan).apply {
            block()
        }.let(cells::add)
    }

    public fun emptyCell(count: Int = 1) {
        if (count < 1) return
        repeat(count) { cell("") }
    }

    internal fun buildAndApply(workbook: XSSFWorkbook, sheet: XSSFSheet, row: XSSFRow, startColIndex: Int = 0): List<CellRangeAddress> {
        val ranges = mutableListOf<CellRangeAddress>()
        var currentColIndex = startColIndex

        row.heightInPoints = heightInPoints

        cells.forEach { cell ->
            when (cell) {
                is ExcelCellDSL -> {
                    row.createCell(currentColIndex).let { cell.buildAndApply(workbook, sheet, it) }

                    if (span > 1 || cell.span > 1) {
                        val newRange = CellRangeAddress(
                            row.rowNum,
                            row.rowNum + span - 1,
                            currentColIndex,
                            currentColIndex + cell.span - 1
                        )
                        cell.borderSettings?.let { bs ->
                            bs.borderTop?.let { RegionUtil.setBorderTop(it, newRange, sheet) }
                            bs.borderTopColor?.let { RegionUtil.setTopBorderColor(it.index.toInt(), newRange, sheet) }
                            bs.borderRight?.let { RegionUtil.setBorderRight(it, newRange, sheet) }
                            bs.borderRightColor?.let { RegionUtil.setRightBorderColor(it.index.toInt(), newRange, sheet) }
                            bs.borderBottom?.let { RegionUtil.setBorderBottom(it, newRange, sheet) }
                            bs.borderBottomColor?.let { RegionUtil.setBottomBorderColor(it.index.toInt(), newRange, sheet) }
                            bs.borderLeft?.let { RegionUtil.setBorderLeft(it, newRange, sheet) }
                            bs.borderLeftColor?.let { RegionUtil.setLeftBorderColor(it.index.toInt(), newRange, sheet) }
                        }
                        ranges.add(newRange)
                    }
                    currentColIndex += cell.span
                }
                is ExcelRegionDSL -> {
                    val innerRegions = cell.buildAndApply(workbook, sheet, row.rowNum, currentColIndex)
                    ranges.addAll(innerRegions)
                    currentColIndex += cell.colSpan
                }
            }
        }


        return ranges
    }
}
