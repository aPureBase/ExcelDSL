package com.apurebase.excel

public val MutableList<ExcelRowDSL>.rowIndex: Int get() = sumBy(ExcelRowDSL::span) + 1
