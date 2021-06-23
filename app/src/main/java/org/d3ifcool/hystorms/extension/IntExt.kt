package org.d3ifcool.hystorms.extension

object IntExt {
    fun Int.combine(y: Int): Int {
        var xScale = 1
        while (xScale <= y) {
            xScale *= 10
        }
        return this * xScale + y
    }
}