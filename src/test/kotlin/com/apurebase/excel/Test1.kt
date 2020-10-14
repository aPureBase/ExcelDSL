package com.apurebase.excel

fun main() {
    excel {
        sheet {
            row {
                cell("Hello")
                cell("World!")
            }
            row(2)
            row {
                emptyCell(3)
                cell("Here!")
            }
        }
    }
}
