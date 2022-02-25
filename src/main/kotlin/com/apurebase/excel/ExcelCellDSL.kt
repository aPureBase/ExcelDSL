package com.apurebase.excel

import com.apurebase.excel.BorderSide.*
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.usermodel.BorderStyle.THIN
import org.apache.poi.ss.usermodel.IndexedColors.BLACK
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.*

@ExcelDSLMarker
public open class ExcelCellDSL(
    private val parent: ExcelRowDSL,
    public val currentColumnIndex: Int,
): ExcelCell {

    public var value: Any? = null
    public var span: Int = 1
    public var fillColor: IndexedColors? = null
    public var wrapText: Boolean


    public var verticalAlignment: VerticalAlignment? = null
    public var horizontalAlignment: HorizontalAlignment? = null

    public var borderSettings: ExcelBorderRegion? = null


    public var font: ExcelFont

    public fun border(style: BorderStyle? = THIN, sides: BorderSide = ALL, color: IndexedColors = BLACK) {
        if (style == null) {
            borderSettings = null
            return
        }
        else if (borderSettings == null) borderSettings = ExcelBorderRegion()
        // TOP
        if (sides in listOf(TOP, TOP_BOTTOM, ALL)) {
            borderSettings!!.borderTop = style
            borderSettings!!.borderTopColor = color
        }

        // RIGHT
        if (sides in listOf(RIGHT, LEFT_RIGHT, ALL)) {
            borderSettings!!.borderRight = style
            borderSettings!!.borderRightColor = color
        }

        // BOTTOM
        if (sides in listOf(BOTTOM, TOP_BOTTOM, ALL)) {
            borderSettings!!.borderBottom = style
            borderSettings!!.borderBottomColor = color
        }

        // LEFT
        if (sides in listOf(LEFT, LEFT_RIGHT, ALL)) {
            borderSettings!!.borderLeft = style
            borderSettings!!.borderLeftColor = color
        }
    }

    public fun font(block: ExcelFont.() -> Unit) {
        font = ExcelFont().apply(block)
    }

    private val conditionalFormatting = mutableListOf<ExcelConditionalFormatDSL>()

    public fun addConditionalFormatting(operator: ConditionalOperator = ConditionalOperator.EQUAL, formula: String, color: IndexedColors? = null) {
        conditionalFormatting.add(ExcelConditionalFormatDSL(operator, formula, color))
    }

    init {
        font = parent.font ?: ExcelFont()
        wrapText = parent.wrapText ?: false
        fillColor = parent.fillColor
        if (parent.borderStyle != null) border(
            style = parent.borderStyle!!.style,
            sides = parent.borderStyle!!.side,
            color = parent.borderStyle!!.color
        )
    }

    internal companion object {
        private val fontSet = mutableMapOf<Pair<XSSFWorkbook, ExcelFont>, XSSFFont>()
        private val styleSet = mutableMapOf<Pair<XSSFWorkbook, ExcelCellStyle>, XSSFCellStyle>()
        private var dataFormat: XSSFDataFormat? = null
    }

    internal fun ExcelFont.getCachedFont(workbook: XSSFWorkbook) = fontSet.getOrPut(workbook to this) {
        workbook.createFont().apply {
            this@getCachedFont.fontName?.let { this@apply.fontName = it }
            this@apply.fontHeightInPoints = this@getCachedFont.heightInPoints
            this@apply.bold = this@getCachedFont.bold
            this@apply.italic = this@getCachedFont.italic
            this@apply.strikeout = this@getCachedFont.strikeout
            this@getCachedFont.color?.let { this@apply.color = it.getIndex() }
        }
    }

    private fun ExcelCellStyle.getCachedStyle(workbook: XSSFWorkbook) = styleSet.getOrPut(workbook to this) {
        workbook.createCellStyle().apply {
            fillColor?.let {
                this@apply.fillForegroundColor = it.getIndex()
                this@apply.fillPattern = FillPatternType.SOLID_FOREGROUND
            }
            horizontalAlignment?.let { this@apply.alignment = it }
            verticalAlignment?.let { this@apply.verticalAlignment = it }

            borderSettings?.let { bs ->
                bs.borderTop?.let {
                    this@apply.borderTop = it
                    this@apply.topBorderColor = bs.borderTopColor?.index ?: throw TODO("Show always exist!")
                }
                bs.borderRight?.let {
                    this@apply.borderRight = it
                    this@apply.rightBorderColor = bs.borderRightColor?.index ?: throw TODO("Show always exist!")
                }
                bs.borderBottom?.let {
                    this@apply.borderBottom = it
                    this@apply.bottomBorderColor = bs.borderBottomColor?.index ?: throw TODO("Show always exist!")
                }
                bs.borderLeft?.let {
                    this@apply.borderLeft = it
                    this@apply.leftBorderColor = bs.borderLeftColor?.index ?: throw TODO("Show always exist!")
                }
            }

            setFont(this@getCachedStyle.font.getCachedFont(workbook))
            this@getCachedStyle.font.numberFormat?.let { nf ->
                if (ExcelCellDSL.dataFormat == null) ExcelCellDSL.dataFormat = workbook.createDataFormat()
                this@apply.dataFormat = ExcelCellDSL.dataFormat!!.getFormat(nf)
            }
            this@apply.wrapText = wrapText
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    internal open fun buildAndApply(workbook: XSSFWorkbook, sheet: XSSFSheet, cell: XSSFCell) {
        cell.cellStyle = ExcelCellStyle(
            fillColor = fillColor,
            horizontalAlignment = horizontalAlignment,
            verticalAlignment = verticalAlignment,
            borderSettings = borderSettings,
            font = font,
            wrapText = wrapText
        ).getCachedStyle(workbook)


        conditionalFormatting.forEach { cf ->
            sheet.sheetConditionalFormatting.addConditionalFormatting(
                arrayOf(CellRangeAddress(parent.currentRow - 1, parent.currentRow - 1, currentColumnIndex, currentColumnIndex)),
                sheet.sheetConditionalFormatting.createConditionalFormattingRule(cf.operator.byte, cf.formula).apply {
                    cf.fillColor?.let { color ->
                        createPatternFormatting().apply {
                            fillBackgroundColor = color.index
                        }
                    }
                },
            )
        }


        when (value) {
            null, "" -> return
            is String -> cell.setCellValue(value as String)
            is Number -> {
                cell.cellType = CellType.NUMERIC
                cell.setCellValue((value as Number).toDouble())
            }
            is ExcelCellFormula -> cell.cellFormula = (value as ExcelCellFormula).formula
            is XSSFRichTextString -> cell.setCellValue(value as XSSFRichTextString)
            else -> throw TODO("Type of value '$value' is not supported")
        }
    }
}
