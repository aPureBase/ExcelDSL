package com.apurebase.excel

import org.junit.jupiter.api.Test

class Test1 {
    @Test
    fun abc() {
        excel {
            (1..10).map {
                println("Generating sheet $it")
                sheet {
                    row {
                        (1..20).map {
                            cell("Cell $it")
                        }
                    }
                    (1..10_000).map {
                        row {
                            (1..20).map {
                                cell("Value $it")
                            }
                        }
                    }
                }
            }
        }
        excel {
            sheet {
                row {
                    cell("column 1")
                    cellRegion(3) {
                        row {
                            cell("column 2-4")
                        }
                    }
                    cell("Column 5")
                }
                row {
                    cell("Value 1")
                    cell("Value 2-4")
                    cell("Value 2-4")
                    cell("Value 2-4")
                    cell("Value 5")
                }
            }
        }
    }
}
