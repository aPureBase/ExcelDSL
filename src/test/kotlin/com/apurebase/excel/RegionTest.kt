package com.apurebase.excel

import org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER

fun main() {


    excel {

        sheet {
            row {
                span = 2
                cellRegion(4) {
                    row {
                        span = 2
                        cell("abc")
                    }
                }
                cellRegion(3) {
                    row {
                        span = 3
                        cell("next")
                    }
                }
            }
        }

    }

}
