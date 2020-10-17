package com.apurebase.excel

import com.apurebase.excel.BorderSide
import com.apurebase.excel.BorderSide.*
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.usermodel.BorderStyle.THIN
import org.apache.poi.ss.usermodel.IndexedColors.BLACK
import org.apache.poi.xssf.usermodel.*

@ExcelDSLMarker
public open class ExcelCellDSL(private val parent: ExcelRowDSL) {

    public var value: Any? = null
    public var span: Int = 1
    public var fillColor: IndexedColors? = null
    public var wrapText: Boolean


    /**
     * @see <a href="https://support.microsoft.com/en-us/office/review-guidelines-for-customizing-a-number-format-c0a1d1fa-d3f4-4018-96b7-9c9354dd99f5">Review guidelines for customizing a number format</a>
     */
    public var numberFormat: String? = null

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

    private companion object {
        private val fontSet = mutableMapOf<ExcelFont, XSSFFont>()
        private var dataFormat: XSSFDataFormat? = null
    }

    internal fun ExcelFont.getCachedFont(workbook: XSSFWorkbook) = fontSet.getOrPut(this) {
        workbook.createFont().apply {
            this@getCachedFont.fontName?.let { this@apply.fontName = it }
            this@apply.fontHeightInPoints = this@getCachedFont.heightInPoints
            this@apply.bold = this@getCachedFont.bold
            this@apply.italic = this@getCachedFont.italic
            this@apply.strikeout = this@getCachedFont.strikeout
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    internal open fun buildAndApply(workbook: XSSFWorkbook, cell: XSSFCell) {
        val style = workbook.createCellStyle()

        fillColor?.let {
            style.fillForegroundColor = it.getIndex()
            style.fillPattern = FillPatternType.SOLID_FOREGROUND
        }
        horizontalAlignment?.let { style.alignment = it }
        verticalAlignment?.let { style.verticalAlignment = it }

        borderSettings?.let { bs ->
            bs.borderTop?.let {
                style.borderTop = it
                style.topBorderColor = bs.borderTopColor?.index ?: throw TODO("Show always exist!")
            }
            bs.borderRight?.let {
                style.borderRight = it
                style.rightBorderColor = bs.borderRightColor?.index ?: throw TODO("Show always exist!")
            }
            bs.borderBottom?.let {
                style.borderBottom = it
                style.bottomBorderColor = bs.borderBottomColor?.index ?: throw TODO("Show always exist!")
            }
            bs.borderLeft?.let {
                style.borderLeft = it
                style.leftBorderColor = bs.borderLeftColor?.index ?: throw TODO("Show always exist!")
            }
        }

        font.let { f ->
            style.setFont(f.getCachedFont(workbook))
        }

        numberFormat?.let { nf ->
            if (dataFormat == null) dataFormat = workbook.createDataFormat()
            style.dataFormat = dataFormat!!.getFormat(nf)
        }

        style.wrapText = wrapText

        cell.cellStyle = style

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
