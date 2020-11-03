package com.apurebase.excel

fun main() {
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
}
