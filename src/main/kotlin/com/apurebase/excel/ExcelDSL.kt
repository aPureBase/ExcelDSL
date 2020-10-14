package com.apurebase.excel

import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator
import org.apache.poi.xssf.usermodel.XSSFWorkbook


@ExcelDSLMarker
public class ExcelDSL {
    private val sheets = mutableListOf<ExcelSheetDSL>()
    private var sheetsOrder: List<String>? = null


    /**
     * Document owner
     */
    public var author: String = "Apache POI"
    public var activeSheetIndex: Int = 0 // TODO: Add support for this!

    public fun sheet(block: ExcelSheetDSL.() -> Unit) {
        ExcelSheetDSL().apply(block).let(sheets::add)
    }

    public fun sheetsOrder(vararg sheetNames: String) {
        sheetsOrder = sheetNames.toList()
    }

    internal fun build(): XSSFWorkbook {
        val wb = XSSFWorkbook()

        wb.properties.apply {
            coreProperties.apply {
                creator = author
            }
        }

        sheets.forEach {
            it.buildAndApply(wb)
        }


        sheetsOrder?.mapIndexed { index, sheetName ->
            wb.setSheetOrder(sheetName, index)
        }

        // Make sure all formulas are calculated
        XSSFFormulaEvaluator.evaluateAllFormulaCells(wb)

        return wb
    }
}
