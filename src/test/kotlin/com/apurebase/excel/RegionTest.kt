package com.apurebase.excel

import org.junit.jupiter.api.Test

class RegionTest {

    @Test
    fun abc() {
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
}
