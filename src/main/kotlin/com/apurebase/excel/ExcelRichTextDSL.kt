package com.apurebase.excel

import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFWorkbook


public class ExcelRichTextDSL(parent: ExcelRowDSL): ExcelCellDSL(parent) {

    private val texts = mutableListOf<ExcelRichTextIndexedDSL>()

    public fun add(str: String, font: ExcelFont? = null) {
        texts.add(ExcelRichTextIndexedDSL(str, font))
    }

    override fun buildAndApply(workbook: XSSFWorkbook, cell: XSSFCell) {
        val richText = workbook.creationHelper.createRichTextString(texts.joinToString(separator = "") { it.text })

        var pointer = 0
        texts.forEach { config ->
            val end = pointer + config.text.length
            config.font?.let { richText.applyFont(pointer, end, it.getCachedFont(workbook))}
            pointer = end
        }

        value = richText
        super.buildAndApply(workbook, cell)
    }

}
