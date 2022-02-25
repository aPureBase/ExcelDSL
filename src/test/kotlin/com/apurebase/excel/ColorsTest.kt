package com.apurebase.excel

import org.apache.poi.ss.usermodel.IndexedColors

fun main() {
    val block: ExcelCellDSL.() -> Unit = {
        addConditionalFormatting(
            operator = ConditionalOperator.GREATER_THAN,
            formula = "0",
            color = IndexedColors.LIGHT_GREEN,
        )
        addConditionalFormatting(
            operator = ConditionalOperator.LESS_THAN,
            formula = "0",
            color = IndexedColors.RED,
        )
        addConditionalFormatting(
            operator = ConditionalOperator.EQUAL,
            formula = "0",
            color = IndexedColors.GREY_50_PERCENT,
        )
    }
    excel {
        sheet {

            row {
                cell(-1, block)
                cell(0, block)
                cell(1, block)
                emptyCell(3)
            }
            row {
                span = 3
                cell(5, block)
                cellRegion(3) {
                    row {
                        cell("Hello")
                        cell("Hello2")
                        cell("Hello3")
                    }
                    row {
                        cell("Hello")
                        cell("Hello2")
                        cell("Hello3")
                    }
                    row {
                        cell("Hello")
                        cell("Hello2")
                        cell("Hello3")
                    }
                }
                cell(0, block)
                cell(-5, block)
            }
            row {
                cell(10, block)
                cell(0, block)
                cell(-10, block)
            }
        }
    }
}
