package com.apurebase.excel

import org.apache.poi.ss.usermodel.ComparisonOperator
import org.apache.poi.ss.usermodel.IndexedColors

public data class ExcelConditionalFormatDSL(
    var operator: ConditionalOperator,
    /**
     * Formula for example an operator of [ConditionalOperator.GREATER_THAN] or [ConditionalOperator.EQUAL].
     * Could be: "1.0" to make this format apply when the value is greater than 1.
     */
    var formula: String,
    var fillColor: IndexedColors? = null,
)

public enum class ConditionalOperator(public val byte: Byte) {
    NO_COMPARISON(ComparisonOperator.NO_COMPARISON),
    BETWEEN(ComparisonOperator.BETWEEN),
    NOT_BETWEEN(ComparisonOperator.NOT_BETWEEN),
    EQUAL(ComparisonOperator.EQUAL),
    NOT_EQUAL(ComparisonOperator.NOT_EQUAL),
    GREATER_THAN(ComparisonOperator.GT),
    LESS_THAN(ComparisonOperator.LT),
    GREATER_THAN_OR_EQUAL_TO(ComparisonOperator.GE),
    LESS_THAN_OR_EQUAL_TO(ComparisonOperator.LE),
}
